package com.jaspersoft.jasperserver.jaxrs.client.apiadapters.permissions;

import com.jaspersoft.jasperserver.dto.permissions.RepositoryPermission;
import com.jaspersoft.jasperserver.dto.permissions.RepositoryPermissionListWrapper;
import com.jaspersoft.jasperserver.dto.resources.ClientFolder;
import com.jaspersoft.jasperserver.jaxrs.client.RestClientTestUtil;
import com.jaspersoft.jasperserver.jaxrs.client.core.operationresult.OperationResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static java.util.Arrays.asList;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * <p/>
 * <p/>
 *
 * @author tetiana.iefimenko
 * @version $Id$
 * @see
 */
public class PermissionsServiceTest extends RestClientTestUtil {

    public static final String RESOURCE_URI = "/public/testPermission";

    @BeforeClass
    public void before() {
        initClient();
        initSession();

        ClientFolder folder = new ClientFolder();
        folder
                .setUri(RESOURCE_URI)
                .setLabel("testPermission")
                .setDescription("Test folder")
                .setVersion(0);

        session
                .resourcesService()
                .resource(folder.getUri())
                .createOrUpdate(folder);

    }
    @Deprecated
    @Test(enabled = false)
    public void should_get_permissions() {
        OperationResult<RepositoryPermission> operationResult = session
                .permissionsService()
                .resource(RESOURCE_URI)
                .permissionRecipient(PermissionRecipient.ROLE, "ROLE_USER")
                .get();

        RepositoryPermission permission = operationResult.getEntity();
        assertNotNull(permission);

    }

    @Test
    public void should_create_collection_permissions() {
        RepositoryPermissionListWrapper permissions = new RepositoryPermissionListWrapper();
        final RepositoryPermission repositoryPermission = new RepositoryPermission().setUri(RESOURCE_URI).setRecipient("role:/ROLE_USER").setMask(1);
        final RepositoryPermission repositoryPermission1 = new RepositoryPermission().setUri(RESOURCE_URI).setRecipient("role:/ROLE_ADMIN").setMask(1);
        permissions.setPermissions(asList(repositoryPermission, repositoryPermission1));

        OperationResult<RepositoryPermissionListWrapper> operationResult = session
                .permissionsService()
                .permissions(permissions)
                .create();

        assertNotNull(operationResult);
        assertEquals(operationResult.getResponse().getStatus(), 201);

    }

    @Test(dependsOnMethods = "should_create_collection_permissions")
    public void should_get_collection_permissions() {
        OperationResult<RepositoryPermissionListWrapper> operationResult = session
                .permissionsService()
                .forResource(RESOURCE_URI)
                .permissions()
                .get();

        RepositoryPermissionListWrapper permission = operationResult.getEntity();
        assertNotNull(permission);

    }

    @Test(dependsOnMethods = "should_get_collection_permissions")
    public void should_get_collection_permissions_with_params() {
        OperationResult<RepositoryPermissionListWrapper> operationResult = session
                .permissionsService()
                .forResource("/public")
                .permissions()
                .param(PermissionResourceParameter.LIMIT, "10")
                .get();

        RepositoryPermissionListWrapper permission = operationResult.getEntity();
        assertNotNull(permission);

    }

    @Test(dependsOnMethods = "should_get_collection_permissions_with_params")
    public void should_update_collection_permissions() {
        RepositoryPermissionListWrapper permissions = new RepositoryPermissionListWrapper();
        final RepositoryPermission repositoryPermission = new RepositoryPermission().setRecipient("role:/ROLE_USER").setMask(2);
        final RepositoryPermission repositoryPermission1 = new RepositoryPermission().setRecipient("role:/ROLE_ADMIN").setMask(8);
        permissions.setPermissions(asList(repositoryPermission, repositoryPermission1));

        OperationResult operationResult = session
                .permissionsService()
                .forResource(RESOURCE_URI)
                .permissions()
                .createOrUpdate(permissions);

        assertNotNull(operationResult);
        assertEquals(operationResult.getResponse().getStatus(), 200);

    }

    @Test(dependsOnMethods = "should_update_collection_permissions")
    public void should_delete_collection_permissions() {

        OperationResult operationResult = session
                .permissionsService()
                .forResource(RESOURCE_URI)
                .permissions()
                .delete();

        assertNotNull(operationResult);
        assertEquals(operationResult.getResponse().getStatus(), 204);

    }

    @Test
    public void should_create_permission() {
        final RepositoryPermission repositoryPermission = new RepositoryPermission()
                .setUri(RESOURCE_URI)
                .setRecipient("role:/ROLE_USER")
                .setMask(1);

        OperationResult operationResult = session
                .permissionsService()
                .permission(repositoryPermission)
                .create();

        assertNotNull(operationResult);
        assertEquals(operationResult.getResponse().getStatus(), 201);

    }

    @Test(dependsOnMethods = "should_create_permission")
    public void should_get_permission() {
        OperationResult<RepositoryPermission> operationResult = session
                .permissionsService()
                .forResource(RESOURCE_URI)
                .permission()
                .permissionRecipient(PermissionRecipient.ROLE, "ROLE_USER")
                .get();

        RepositoryPermission permission = operationResult.getEntity();
        assertNotNull(permission);

    }

    @Test(dependsOnMethods = "should_get_permission")
    public void should_update_permission() {
        final RepositoryPermission repositoryPermission = new RepositoryPermission().setMask(20);

        OperationResult operationResult = session
                .permissionsService()
                .forResource(RESOURCE_URI)
                .permission()
                .permissionRecipient(PermissionRecipient.ROLE, "ROLE_USER")
                .createOrUpdate(repositoryPermission);

        assertNotNull(operationResult);
        assertEquals(operationResult.getResponse().getStatus(), 200);

    }

    @Test(dependsOnMethods = "should_update_permission")
    public void should_delete_permissions() {

        OperationResult operationResult = session
                .permissionsService()
                .forResource(RESOURCE_URI)
                .permission()
                .permissionRecipient(PermissionRecipient.ROLE, "ROLE_USER")
                .delete();

        assertNotNull(operationResult);
        assertEquals(operationResult.getResponse().getStatus(), 204);

    }

    @Test
    public void should_createOrUpdate_permission() {

        RepositoryPermission permission = new RepositoryPermission()
                .setMask(6);
        OperationResult result = session
                .permissionsService()
                .forResource("/public/testPermission")
                .permission()
                .permissionRecipient(PermissionRecipient.ROLE, "ROLE_USER")
                .createOrUpdate(permission);

        assertNotNull(result);
        assertEquals(result.getResponse().getStatus(), 200);

    }




    @AfterClass
    public void after() {
        session
                .resourcesService()
                .resource("/public/testPermission")
                .delete();

        session.logout();
    }
}
