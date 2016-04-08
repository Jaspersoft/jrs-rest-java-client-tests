package com.jaspersoft.jasperserver.jaxrs.client.apiadapters.connections;

import com.jaspersoft.jasperserver.jaxrs.client.RestClientTestUtil;
import com.jaspersoft.jasperserver.jaxrs.client.core.enums.ConnectionMediaType;
import com.jaspersoft.jasperserver.jaxrs.client.core.operationresult.OperationResult;
import com.jaspersoft.jasperserver.dto.connection.LfsConnection;
import javax.ws.rs.core.Response;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * <p/>
 * <p/>
 *
 * @author tetiana.iefimenko
 * @version $Id$
 * @see
 */
public class LocalFileSystemConnectionsServiceTest extends RestClientTestUtil {

    private String uuId;

    @BeforeClass
    public void before() {
        initClient();
        initSession();
    }

    @AfterClass
    public void after() {
        session.logout();
    }

    @Test
    public void should_create_connection() {
        OperationResult<LfsConnection> operationResult = session
                .connectionsService()
                .connection(LfsConnection.class, ConnectionMediaType.LOCAL_FILE_SYSTEM_TYPE)
                .create(new LfsConnection().setPath("D:\\"));

        assertEquals(Response.Status.CREATED.getStatusCode(), operationResult.getResponse().getStatus());

        String header = operationResult.getResponse().getHeaderString("Location");
        uuId = header.substring(header.lastIndexOf("/") + 1);
    }

    @Test(dependsOnMethods = "should_create_connection")
    public void should_update_connection() {
        OperationResult<LfsConnection> operationResult = session
                .connectionsService()
                .connection(LfsConnection.class, ConnectionMediaType.LOCAL_FILE_SYSTEM_TYPE, uuId)
                .update(new LfsConnection().setPath("C:\\"));

        assertEquals(Response.Status.OK.getStatusCode(), operationResult.getResponse().getStatus());
    }

    @Test(dependsOnMethods = "should_update_connection")
    public void should_get_connection() {
        OperationResult<LfsConnection> operationResult = session
                .connectionsService()
                .connection(LfsConnection.class, ConnectionMediaType.LOCAL_FILE_SYSTEM_TYPE, uuId)
                .get();

        assertEquals(Response.Status.OK.getStatusCode(), operationResult.getResponse().getStatus());
    }

    @Test(dependsOnMethods = "should_get_connection")
    public void should_delete_connection() {
        OperationResult operationResult = session
                .connectionsService()
                .connection(uuId)
                .delete();
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), operationResult.getResponse().getStatus());
    }
}
