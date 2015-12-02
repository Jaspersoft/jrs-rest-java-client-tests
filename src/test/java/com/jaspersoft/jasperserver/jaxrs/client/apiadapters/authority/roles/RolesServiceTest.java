package com.jaspersoft.jasperserver.jaxrs.client.apiadapters.authority.roles;

import com.jaspersoft.jasperserver.dto.authority.ClientRole;
import com.jaspersoft.jasperserver.dto.authority.RolesListWrapper;
import com.jaspersoft.jasperserver.jaxrs.client.RestClientTestUtil;
import com.jaspersoft.jasperserver.jaxrs.client.core.operationresult.OperationResult;
import javax.ws.rs.core.Response;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * @author Tetiana Iefimenko
 */
public class RolesServiceTest extends RestClientTestUtil {
    private final int MIN_EXPECTED_COUNT_OF_ROLES = 2;
    private final String TEST_USER_NAME = "superuser";
    private final String TEST_ROLE = "ROLE_ADMINISTRATOR";
    private final String TEST_ROLE_TO_CREATE = "ROLE_TEST";
    private final String TEST_ROLE_TO_UPDATE = "ROLE_NEW_NAME";

    @BeforeClass
    public void before() {
        initClient();
        initSession();
    }

    @Test
    public void should_return_all_roles_for_user() {
        // When
        OperationResult<RolesListWrapper> operationResult =
                session
                        .rolesService()
                        .allRoles()
                        .param(RolesParameter.USER, TEST_USER_NAME)
                        .get();

        RolesListWrapper rolesListWrapper = operationResult.getEntity();
        // Then
        assertEquals(Response.Status.OK.getStatusCode(), operationResult.getResponse().getStatus());
        assertNotNull(operationResult);
        assertNotNull(rolesListWrapper);
        assertTrue(rolesListWrapper.getRoleList().size() >= MIN_EXPECTED_COUNT_OF_ROLES);
    }

    @Test
    public void should_return_role_descriptor() {
        // When
        OperationResult<ClientRole> operationResult =
                session
                        .rolesService()
                        .roleName(TEST_ROLE)
                        .get();

        ClientRole role = operationResult.getEntity();
        // Then
        assertEquals(Response.Status.OK.getStatusCode(), operationResult.getResponse().getStatus());
        assertNotNull(operationResult);
        assertNotNull(role);
        assertNotNull(role.getName());
        assertNotNull(role.isExternallyDefined());
    }


    @Test
    public void should_return_roles_for_root_organization() {
        // When
        OperationResult<RolesListWrapper> operationResult =
                session
                        .rolesService()
                        .allRoles()
                        .get();

        RolesListWrapper role = operationResult.getEntity();
        // Then
        assertEquals(Response.Status.OK.getStatusCode(), operationResult.getResponse().getStatus());
        assertNotNull(operationResult);
        assertNotNull(role);
    }


    @Test
    public void should_create_role() {
        //Given
        ClientRole role = new ClientRole()
                .setName(TEST_ROLE_TO_CREATE);

        // When
        OperationResult<ClientRole> operationResult =
                session
                        .rolesService()
                        .roleName(role.getName())
                        .createOrUpdate(role);
        // Then
        assertEquals(Response.Status.CREATED.getStatusCode(), operationResult.getResponse().getStatus());
        assertNotNull(operationResult);
        assertNotNull(role);
    }

    @Test(dependsOnMethods = "should_create_role")
    public void should_update_role() {
        // Given
        ClientRole newRole = new ClientRole()
                .setName(TEST_ROLE_TO_UPDATE);

        // When
        OperationResult<ClientRole> operationResult =
                session
                        .rolesService()
                        .roleName(TEST_ROLE_TO_CREATE)
                        .createOrUpdate(newRole);
        // Then
        assertEquals(Response.Status.OK.getStatusCode(), operationResult.getResponse().getStatus());
        assertNotNull(operationResult);
        assertNotNull(newRole);
    }

    @Test(dependsOnMethods = "should_update_role")
    public void should_delete_role() {
        // When
        OperationResult<ClientRole> operationResult =
                session
                        .rolesService()
                        .roleName(TEST_ROLE_TO_UPDATE)
                        .delete();
        // Then
        assertNotNull(operationResult);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), operationResult.getResponse().getStatus());
    }



    @AfterClass
    public void after() {
        session.logout();
    }

}
