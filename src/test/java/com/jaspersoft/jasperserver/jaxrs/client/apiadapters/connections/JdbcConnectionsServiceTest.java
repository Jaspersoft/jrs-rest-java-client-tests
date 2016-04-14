package com.jaspersoft.jasperserver.jaxrs.client.apiadapters.connections;

import com.jaspersoft.jasperserver.dto.resources.ClientJdbcDataSource;
import com.jaspersoft.jasperserver.dto.resources.domain.ResourceGroupElement;
import com.jaspersoft.jasperserver.jaxrs.client.RestClientTestUtil;
import com.jaspersoft.jasperserver.jaxrs.client.core.enums.ConnectionMediaType;
import com.jaspersoft.jasperserver.jaxrs.client.core.operationresult.OperationResult;
import java.util.Map;
import javax.ws.rs.core.Response;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

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
public class JdbcConnectionsServiceTest extends RestClientTestUtil {

    private String uuId;
    private ClientJdbcDataSource connection;

    @BeforeClass
    public void before() {
        initClient();
        initSession();
        connection = new ClientJdbcDataSource()
                .setDriverClass("org.postgresql.Driver")
                .setUsername("postgres")
                .setPassword("superuser")
                .setConnectionUrl("jdbc:postgresql://localhost:5433/foodmart");
    }

    @AfterClass
    public void after() {
        session.logout();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void should_create_connection() {

        OperationResult<ClientJdbcDataSource> operationResult = session
                .connectionsService()
                .connection(ClientJdbcDataSource.class, ConnectionMediaType.JDBC_DATA_SOURCE_TYPE)
                .create(connection);

        assertNotNull(operationResult);
        assertEquals(Response.Status.CREATED.getStatusCode(), operationResult.getResponse().getStatus());

        extractUuid(operationResult.getResponse().getHeaderString("Location"));
    }

    @SuppressWarnings("unchecked")
    @Test(dependsOnMethods = "should_create_connection")
    public void should_update_connection() {
        OperationResult<ClientJdbcDataSource> operationResult = session
                .connectionsService()
                .connection(ClientJdbcDataSource.class, ConnectionMediaType.JDBC_DATA_SOURCE_TYPE, uuId)
                .update(connection.setDescription("Test connection"));

        assertNotNull(operationResult);
        assertEquals(Response.Status.OK.getStatusCode(), operationResult.getResponse().getStatus());
    }

    @SuppressWarnings("unchecked")
    @Test(dependsOnMethods = "should_update_connection")
    public void should_get_connection() {
        OperationResult<? extends ClientJdbcDataSource> operationResult = session
                .connectionsService()
                .connection(ClientJdbcDataSource.class, ConnectionMediaType.JDBC_DATA_SOURCE_TYPE, uuId)
                .get();

        assertNotNull(operationResult);
        assertEquals(Response.Status.OK.getStatusCode(), operationResult.getResponse().getStatus());
    }

    @Test(dependsOnMethods = "should_get_connection")
    public void should_get_metadata() {
        OperationResult<ResourceGroupElement> operationResult = session
                .connectionsService()
                .connection(uuId, ResourceGroupElement.class,
                        ConnectionMediaType.JDBC_DATA_SOURCE_METADATA_TYPE)
                .metadata();

        assertNotNull(operationResult);
        assertEquals(Response.Status.OK.getStatusCode(), operationResult.getResponse().getStatus());
    }

    @Test(dependsOnMethods = "should_get_metadata")
    public void should_delete_connection() {
        OperationResult operationResult = session
                .connectionsService()
                .connection(uuId)
                .delete();

        assertNotNull(operationResult);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), operationResult.getResponse().getStatus());
    }

    @Test(dependsOnMethods = "should_delete_connection")
    public void should_create_connection_and_get_metadata() {
        OperationResult<ResourceGroupElement> operationResult = session
                .connectionsService()
                .connection(ClientJdbcDataSource.class,
                        ConnectionMediaType.JDBC_DATA_SOURCE_TYPE,
                        ResourceGroupElement.class,
                        ConnectionMediaType.JDBC_DATA_SOURCE_METADATA_TYPE)
                .createAndGetMetadata(connection);

        assertNotNull(operationResult);
        extractUuid(operationResult.getResponse().getHeaderString("Location"));
        assertEquals(Response.Status.OK.getStatusCode(), operationResult.getResponse().getStatus());
    }

    @SuppressWarnings("unchecked")
    @Test(dependsOnMethods = "should_create_connection_and_get_metadata")
    public void should_execute_query() {
        OperationResult<Map> operationResult = session
                .connectionsService()
                .connection(uuId)
                .query("select * from account", Map.class)
                .execute();

        Map result = operationResult.getEntity();

        assertNotNull(operationResult);
        assertNotNull(result);
        assertEquals(Response.Status.OK.getStatusCode(), operationResult.getResponse().getStatus());
    }

    @SuppressWarnings("unchecked")
    @Test(dependsOnMethods = "should_execute_query")
    public void should_return_query_metadata() {
        OperationResult<Map> operationResult = session
                .connectionsService()
                .connection(uuId)
                .query("select * from account", Map.class)
                .resultSetMetadata();
        Map result = operationResult.getEntity();

        assertNotNull(operationResult);
        assertNotNull(result);
        assertEquals(Response.Status.OK.getStatusCode(), operationResult.getResponse().getStatus());
    }

    private void extractUuid(String locationHeader) {
        if (locationHeader.endsWith("metadata")) {
            locationHeader = locationHeader.substring(0, locationHeader.lastIndexOf("/"));
        }

        uuId = locationHeader.substring(locationHeader.lastIndexOf("/") + 1);
    }
}
