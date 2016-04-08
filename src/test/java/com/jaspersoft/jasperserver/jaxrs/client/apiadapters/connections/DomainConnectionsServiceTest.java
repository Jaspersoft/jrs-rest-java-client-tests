package com.jaspersoft.jasperserver.jaxrs.client.apiadapters.connections;

import com.jaspersoft.jasperserver.dto.resources.domain.ClientDomain;
import com.jaspersoft.jasperserver.jaxrs.client.RestClientTestUtil;
import com.jaspersoft.jasperserver.jaxrs.client.core.enums.ConnectionMediaType;
import com.jaspersoft.jasperserver.jaxrs.client.core.operationresult.OperationResult;
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
public class DomainConnectionsServiceTest extends RestClientTestUtil {

    private String uuId;
    private ClientDomain connection;

    @BeforeClass
    public void before() {
        initClient();
        initSession();
        connection = new ClientDomain().setUri("/organizations/organization_1/Domains/Simple_Domain");
    }

    @AfterClass
    public void after() {
        session.logout();
    }

    @Test
    public void should_create_connection() {

        OperationResult<ClientDomain> operationResult = session
                .connectionsService()
                .connection(ClientDomain.class, ConnectionMediaType.DOMAIN_DATA_SOURCE_TYPE)
                .create(connection);

        assertEquals(Response.Status.CREATED.getStatusCode(), operationResult.getResponse().getStatus());

        String header = operationResult.getResponse().getHeaderString("Location");
        uuId = header.substring(header.lastIndexOf("/") + 1);
    }

    @Test(dependsOnMethods = "should_create_connection")
    public void should_update_connection() {
        OperationResult<ClientDomain> operationResult = session
                .connectionsService()
                .connection(ClientDomain.class, ConnectionMediaType.DOMAIN_DATA_SOURCE_TYPE, uuId)
                .update(connection.setDescription("Test connection"));

        assertEquals(Response.Status.OK.getStatusCode(), operationResult.getResponse().getStatus());
    }

    @Test(dependsOnMethods = "should_update_connection")
    public void should_get_connection() {
        OperationResult<ClientDomain> operationResult = session
                .connectionsService()
                .connection(ClientDomain.class, ConnectionMediaType.DOMAIN_DATA_SOURCE_TYPE, uuId)
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
