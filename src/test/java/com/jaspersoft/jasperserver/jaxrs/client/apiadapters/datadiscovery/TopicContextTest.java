package com.jaspersoft.jasperserver.jaxrs.client.apiadapters.datadiscovery;

import com.jaspersoft.jasperserver.dto.resources.ClientReportUnit;
import com.jaspersoft.jasperserver.dto.resources.domain.ResourceGroupElement;
import com.jaspersoft.jasperserver.jaxrs.client.RestClientTestUtil;
import com.jaspersoft.jasperserver.jaxrs.client.core.operationresult.OperationResult;
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
public class TopicContextTest extends RestClientTestUtil {
    private String uuId;
    private ClientReportUnit reportUnit;

    @BeforeClass
    public void before() {
        initClient();
        initSession();
        reportUnit = new ClientReportUnit().setUri("/public/Samples/Reports/14.PerformanceSummary");
    }

    @AfterClass
    public void after() {
        session.logout();
    }

    @Test
    public void should_create_context() {

        OperationResult<ClientReportUnit> operationResult = session
                .dataDiscoveryService()
                .topicContext()
                .create(reportUnit);

        assertNotNull(operationResult);
        assertEquals(Response.Status.CREATED.getStatusCode(), operationResult.getResponse().getStatus());

        extractUuid(operationResult.getResponse().getHeaderString("Location"));
        assertNotNull(uuId);

    }


    @Test(dependsOnMethods = "should_create_context")
    public void should_get_metadata() {
        OperationResult<ResourceGroupElement> operationResult = session
                .dataDiscoveryService()
                .topicContext()
                .fetchMetadataById(uuId);
        assertNotNull(operationResult);
        assertEquals(Response.Status.OK.getStatusCode(), operationResult.getResponse().getStatus());
        assertNotNull(operationResult.getEntity());
    }

    @Test(dependsOnMethods = "should_get_metadata")
    public void should_create_context_and_get_metadata() {
        OperationResult<ResourceGroupElement> operationResult = session
                .dataDiscoveryService()
                .topicContext()
                .fetchMetadataByContext(reportUnit);

        assertNotNull(operationResult);
        assertEquals(Response.Status.OK.getStatusCode(), operationResult.getResponse().getStatus());
        assertNotNull(operationResult.getEntity());
    }


    private void extractUuid(String locationHeader) {
        if (locationHeader.endsWith("metadata")) {
            locationHeader = locationHeader.substring(0, locationHeader.lastIndexOf("/"));
        }

        uuId = locationHeader.substring(locationHeader.lastIndexOf("/") + 1);
    }

}
