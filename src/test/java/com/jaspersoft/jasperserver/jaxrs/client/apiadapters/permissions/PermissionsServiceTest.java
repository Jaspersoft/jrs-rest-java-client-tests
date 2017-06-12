package com.jaspersoft.jasperserver.jaxrs.client.apiadapters.permissions;

import com.jaspersoft.jasperserver.dto.permissions.RepositoryPermission;
import com.jaspersoft.jasperserver.jaxrs.client.RestClientTestUtil;
import com.jaspersoft.jasperserver.jaxrs.client.core.operationresult.OperationResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

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

    @BeforeClass
    public void before() {
        initClient();
        initSession();
    }

    @Test
    public void should_get_permissions() {
        OperationResult<RepositoryPermission> operationResult = session
                .permissionsService()
                .resource("/organizations/organization_1/datasources")
                .permissionRecipient(PermissionRecipient.ROLE, "ROLE_USER")
                .get();

        RepositoryPermission permission = operationResult.getEntity();
        assertNotNull(permission);

    }


    @AfterClass
    public void after() {
        session.logout();
    }
}
