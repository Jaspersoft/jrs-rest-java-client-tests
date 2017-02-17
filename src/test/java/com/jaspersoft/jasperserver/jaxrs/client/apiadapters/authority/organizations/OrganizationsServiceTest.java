package com.jaspersoft.jasperserver.jaxrs.client.apiadapters.authority.organizations;

import com.jaspersoft.jasperserver.dto.authority.ClientTenant;
import com.jaspersoft.jasperserver.dto.authority.OrganizationsListWrapper;
import com.jaspersoft.jasperserver.jaxrs.client.RestClientTestUtil;
import com.jaspersoft.jasperserver.jaxrs.client.core.operationresult.OperationResult;
import javax.ws.rs.core.Response;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;


/**
 * @author Tetiana Iefimenko
 */
public class OrganizationsServiceTest extends RestClientTestUtil {

    public static final String TEST_ID = "test_Id";
    public static final String TEST_ALIAS = "test_alias";
    private ClientTenant organization;


    @BeforeClass
    public void before() {
        initClient();
        initSession();
        organization = new ClientTenant();
        organization.setAlias(TEST_ALIAS);
        organization.setId(TEST_ID);
    }

    @Test
    public void should_create_organization_by_object() {

        OperationResult<ClientTenant> operationResult = session
                .organizationsService()
                .organization(organization)
                .create();

        ClientTenant entity = operationResult.getEntity();

        assertEquals(Response.Status.CREATED.getStatusCode(), operationResult.getResponse().getStatus());
        assertNotNull(entity);
    }

    @Test
    public void should_create_organization_by_alias() {

        OperationResult<ClientTenant> operationResult = session
                .organizationsService()
                .organization(new ClientTenant().setAlias(TEST_ALIAS))
                .create();

        ClientTenant entity = operationResult.getEntity();

        assertEquals(Response.Status.CREATED.getStatusCode(), operationResult.getResponse().getStatus());
        assertNotNull(entity);
    }

    @Test(dependsOnMethods = "should_create_organization")
    public void should_return_organization() {

        OperationResult<ClientTenant> operationResult = session
                .organizationsService()
                .organization(organization)
                .get();

        ClientTenant entity = operationResult.getEntity();

        assertEquals(Response.Status.OK.getStatusCode(), operationResult.getResponse().getStatus());
        assertNotNull(entity);
    }

    @Test(dependsOnMethods = "should_return_organization")
    public void should_return_organizations() {

        OperationResult<OrganizationsListWrapper> operationResult = session
                .organizationsService()
                .allOrganizations()
                .get();

        OrganizationsListWrapper entity = operationResult.getEntity();

        assertEquals(Response.Status.OK.getStatusCode(), operationResult.getResponse().getStatus());
        assertNotNull(entity);
        assertEquals(true, entity.getList().size() > 1);
    }


    @Test(dependsOnMethods = "should_return_organizations")
    public void should_update_organization() {
        OperationResult<ClientTenant> operationResult = session
                .organizationsService()
                .organization(organization)
                .createOrUpdate(new ClientTenant(organization).setAlias("new_alias"));

        ClientTenant entity = operationResult.getEntity();

        assertEquals(Response.Status.OK.getStatusCode(), operationResult.getResponse().getStatus());
        assertNotNull(entity);
        assertEquals("new_alias", entity.getAlias());
    }


    @Test(dependsOnMethods = "should_update_organization")
    public void should_delete_organization() {

        OperationResult<ClientTenant> operationResult = session
                .organizationsService()
                .organization(organization)
                .delete();

        ClientTenant entity = operationResult.getEntity();

        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), operationResult.getResponse().getStatus());
        assertNull(entity);
    }

    @AfterClass
    public void after() {
        session.logout();
        session = null;
    }

}
