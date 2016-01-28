package com.jaspersoft.jasperserver.jaxrs.client.apiadapters.diagnostic;

import com.jaspersoft.jasperserver.dto.common.PatchDescriptor;
import com.jaspersoft.jasperserver.dto.common.PatchItem;
import com.jaspersoft.jasperserver.dto.logcapture.CollectorSettings;
import com.jaspersoft.jasperserver.dto.logcapture.CollectorSettingsList;
import com.jaspersoft.jasperserver.dto.logcapture.LogFilterParameters;
import com.jaspersoft.jasperserver.dto.logcapture.ResourceAndSnapshotFilter;
import com.jaspersoft.jasperserver.jaxrs.client.RestClientTestUtil;
import com.jaspersoft.jasperserver.jaxrs.client.core.operationresult.OperationResult;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.core.Response;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * @author Tetiana Iefimenko
 */
public class DiagnosticServiceTest extends RestClientTestUtil {

    private CollectorSettings collector1;
    private CollectorSettings collector2;

    @BeforeClass
    public void before() {
        initClient();
        initSession();
        collector1 = new CollectorSettings();
        collector1.setName("collector_1");
        collector1.setVerbosity("HIGH");
        LogFilterParameters logFilterParameters1 = new LogFilterParameters();
        ResourceAndSnapshotFilter snapshotFilter1 = new ResourceAndSnapshotFilter();
        snapshotFilter1.setResourceUri("/public/Samples/Reports/PromotionDetailsReport");
        snapshotFilter1.setIncludeDataSnapshots(true);
        logFilterParameters1.setResourceAndSnapshotFilter(snapshotFilter1);
        collector1.setLogFilterParameters(logFilterParameters1);

        collector2 = new CollectorSettings();
        collector2.setName("collector_2");
        collector2.setVerbosity("MEDIUM");
        LogFilterParameters logFilterParameters2 = new LogFilterParameters();
        ResourceAndSnapshotFilter snapshotFilter2 = new ResourceAndSnapshotFilter();
        snapshotFilter2.setResourceUri("/public/Samples/Reports/12g.PromotionDetailsReport");
        snapshotFilter2.setIncludeDataSnapshots(true);
        logFilterParameters2.setResourceAndSnapshotFilter(snapshotFilter2);
        collector2.setLogFilterParameters(logFilterParameters2);
    }

    @Test
    public void should_create_log_collector() {
        // Given
        DiagnosticService service = session
                .diagnosticService();
        // When
        OperationResult<CollectorSettings> operationResult1 = service
                .forCollector(collector1)
                .create();

        OperationResult<CollectorSettings> operationResult2 = service
                .forCollector(collector2)
                .create();

        collector1 = operationResult1
                .getEntity();

        collector2 = operationResult2
                .getEntity();
        // Then
        assertNotNull(collector1);
        assertNotNull(collector2);
        assertEquals(Response.Status.OK.getStatusCode(), operationResult1.getResponse().getStatus());
        assertEquals(Response.Status.OK.getStatusCode(), operationResult2.getResponse().getStatus());

    }

    @Test(dependsOnMethods = "should_create_log_collector")
    public void should_return_collectors() {
        // When
        OperationResult<CollectorSettingsList> operationResult = session
                .diagnosticService()
                .allCollectors()
                .collectorsSettings();

        CollectorSettingsList result = operationResult
                .getEntity();
        // Then
        assertNotNull(result);
        assertEquals(Response.Status.OK.getStatusCode(), operationResult.getResponse().getStatus());
        assertTrue(result.getCollectorSettingsList().size() >= 2);
    }


    @Test(dependsOnMethods = "should_return_collectors")
    public void should_return_collector_metadata_by_id() {
        // When
        OperationResult<CollectorSettings> operationResult = session
                .diagnosticService()
                .forCollector(collector1.getId())
                .collectorSettings();

        CollectorSettings result = operationResult
                .getEntity();
        // Then
        assertNotNull(result);
        assertEquals(Response.Status.OK.getStatusCode(), operationResult.getResponse().getStatus());

    }

    @Test(dependsOnMethods = "should_return_collector_metadata_by_id")
    public void should_update_collector_metadata() {
        // Given
        PatchDescriptor patchDescriptor = new PatchDescriptor();
        List<PatchItem> items = new ArrayList<PatchItem>();
        items.add(new PatchItem().setField("verbosity").setValue("MEDIUM"));
        patchDescriptor.setItems(items);
        // When
        OperationResult<CollectorSettings> operationResult = session
                .diagnosticService()
                .forCollector(collector1)
                .updateCollectorSettings(patchDescriptor);

        CollectorSettings result = operationResult
                .getEntity();
        // Then
        assertNotNull(result);
        assertEquals(Response.Status.OK.getStatusCode(), operationResult.getResponse().getStatus());
        assertEquals("MEDIUM", result.getVerbosity());

    }


    @Test(dependsOnMethods = "should_update_collector_metadata")
    public void should_update_all_collector_metadata() {
        // Given
        PatchDescriptor patchDescriptor = new PatchDescriptor();
        List<PatchItem> items = new ArrayList<PatchItem>();
        items.add(new PatchItem().setField("verbosity").setValue("LOW"));
        patchDescriptor.setItems(items);

        // When
        OperationResult<CollectorSettingsList> operationResult = session
                .diagnosticService()
                .allCollectors()
                .updateCollectorsSettings(patchDescriptor);
        CollectorSettingsList result = operationResult
                .getEntity();
        // Then
        assertNotNull(result);
        assertEquals(Response.Status.OK.getStatusCode(), operationResult.getResponse().getStatus());
        assertEquals("LOW", result.getCollectorSettingsList().get(0).getVerbosity());
        assertEquals("LOW", result.getCollectorSettingsList().get(1).getVerbosity());

    }

    @Test(dependsOnMethods = "should_update_all_collector_metadata")
    public void should_update_collector_by_id() {
        // When
        collector1.setStatus("STOPPED");
        OperationResult<CollectorSettings> operationResult = session
                .diagnosticService()
                .forCollector(collector1)
                .updateCollectorSettings(collector1);

        CollectorSettings result = operationResult
                .getEntity();
        // Then
        assertNotNull(result);
        assertEquals(Response.Status.OK.getStatusCode(), operationResult.getResponse().getStatus());
        assertTrue(collector1.getStatus().equals("STOPPED") || collector1.getStatus().equals("SHUTTING_DOWN"));

    }


    @Test(dependsOnMethods = "should_update_collector_by_id")
    public void should_return_collector_content() throws InterruptedException {
        // When
        String collectorStatus  = "RUNNING";

        while(!collectorStatus.equals("STOPPED")) {
            collectorStatus = session
                    .diagnosticService()
                    .forCollector(collector1)
                    .collectorSettings().getEntity().getStatus();
            Thread.sleep(1000);
        }
        OperationResult<InputStream> operationResult = session
                .diagnosticService()
                .forCollector(collector1)
                .collectorContent();

        InputStream result = operationResult
                .getEntity();
        // Then
        assertNotNull(result);
        assertEquals(Response.Status.OK.getStatusCode(), operationResult.getResponse().getStatus());
    }

    @Test(dependsOnMethods = "should_return_collector_content")
    public void should_return_collectors_content() throws InterruptedException {
        // When
        collector2.setStatus("STOPPED");
        session
                .diagnosticService()
                .forCollector(collector2)
                .updateCollectorSettings(collector2);

        String collectorStatus  = "RUNNING";

        while(!collectorStatus.equals("STOPPED")) {
            collectorStatus = session
                    .diagnosticService()
                    .forCollector(collector2)
                    .collectorSettings().getEntity().getStatus();
            Thread.sleep(1000);
        }
        OperationResult<InputStream> operationResult = session
                .diagnosticService()
                .allCollectors()
                .collectorsContent();

        InputStream result = operationResult
                .getEntity();
        assertNotNull(result);
        assertEquals(Response.Status.OK.getStatusCode(), operationResult.getResponse().getStatus());
    }


    @Test(dependsOnMethods = "should_return_collectors_content")
    public void should_delete_collector_by_id() {
        // When
        OperationResult<CollectorSettings> operationResult = session
                .diagnosticService()
                .forCollector(collector1)
                .delete();

        CollectorSettings result = operationResult
                .getEntity();
        // Then
        assertNull(result);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), operationResult.getResponse().getStatus());

    }

    @Test(dependsOnMethods = "should_delete_collector_by_id")
    public void should_delete_all_collectors() {
        // When
        OperationResult<CollectorSettingsList> operationResult = session
                .diagnosticService()
                .allCollectors()
                .delete();
        CollectorSettingsList result = operationResult
                .getEntity();
        // Then
        assertNull(result);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), operationResult.getResponse().getStatus());

    }

    @AfterClass
    public void after() {
        session.logout();
    }
}
