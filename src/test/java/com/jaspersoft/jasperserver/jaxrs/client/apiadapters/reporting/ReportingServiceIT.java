package com.jaspersoft.jasperserver.jaxrs.client.apiadapters.reporting;

import com.jaspersoft.jasperserver.jaxrs.client.RestClientTestUtil;
import com.jaspersoft.jasperserver.jaxrs.client.core.operationresult.OperationResult;
import com.jaspersoft.jasperserver.jaxrs.client.dto.reports.ReportExecutionDescriptor;
import com.jaspersoft.jasperserver.jaxrs.client.dto.reports.ReportExecutionRequest;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertNotNull;


/**
 * @author Alex Krasnyanskiy
 * @author Tetiana Iefimenko
 */
public class ReportingServiceIT extends RestClientTestUtil {


    @BeforeClass
    public void before() {
        initClient();
        initSession();
    }

    @Test
    public void should_return_proper_entity_if_pass_pdf_report_output_format() {

        /** When **/
        OperationResult<InputStream> result = session
                .reportingService()
                .report("/organizations/organization_1/adhoc/topics/Cascading_multi_select_topic")
                .prepareForRun(ReportOutputFormat.PDF, 1)
                .parameter("Cascading_state_multi_select", "CA")
                .parameter("Cascading_state_multi_select", "OR", "WA")
                .parameter("Cascading_name_single_select", "Adams-Steen Transportation Holdings")
                .parameter("Country_multi_select", "USA")
                .run();

        InputStream entity = result.getEntity();
        /** Then **/
        assertNotNull(entity);
    }

    @Test
    public void should_return_proper_entity_() {

        /** When **/
        OperationResult<InputStream> result = client
                .authenticate("superuser", "superuser")
                .reportingService()
                .report("/organizations/organization_1/adhoc/topics/Cascading_multi_select_topic")
                .prepareForRun(ReportOutputFormat.PDF, 1)
                .parameter("Cascading_state_multi_select", "CA")
                .parameter("Cascading_state_multi_select", "OR", "WA")
                .run();
        InputStream entity = result.getEntity();
        /** Then **/
        assertNotNull(entity);
    }

    @Test
    public void should_return_proper_entity_if_passed_number_of_pages_zero() {

        /** When **/
        OperationResult<InputStream> result = session
                .reportingService()
                .report("/organizations/organization_1/adhoc/topics/Cascading_multi_select_topic")
                .prepareForRun(ReportOutputFormat.PDF, 0)
                .parameter("Cascading_state_multi_select", "CA")
                .parameter("Cascading_state_multi_select", "OR", "WA")
                .parameter("Cascading_name_single_select", "Adams-Steen Transportation Holdings")
                .parameter("Country_multi_select", "USA")
                .run();

        InputStream entity = result.getEntity();
        /** Then **/
        assertNotNull(entity);

    }
    @Test
    public void should_return_proper_entity_if_pass_string_output_format() {

        /** When **/
        OperationResult<InputStream> result = session
                .reportingService()
                .report("/organizations/organization_1/adhoc/topics/Cascading_multi_select_topic")
                .prepareForRun("PDF", 1)
                .parameter("Cascading_state_multi_select", "CA")
                .parameter("Cascading_name_single_select", "Adams-Steen Transportation Holdings")
                .parameter("Country_multi_select", "USA")
                .run();

        InputStream entity = result.getEntity();
        /** Then **/
        assertNotNull(entity);

    }

    @Test
    public void should_return_proper_entity_if_passed_wrong_number_of_pages() {

        /** When **/
        OperationResult<InputStream> result = session
                .reportingService()
                .report("/organizations/organization_1/adhoc/topics/Cascading_multi_select_topic")
                .prepareForRun("PDF", 0,-1,1)
                .parameter("Cascading_state_multi_select", "CA")
                .parameter("Cascading_name_single_select", "Adams-Steen Transportation Holdings")
                .parameter("Country_multi_select", "USA")
                .run();

        InputStream entity = result.getEntity();
        /** Then **/
        assertNotNull(entity);

    }

    @Test
    public void should_return_proper_entity_if_passed_all_wrong_number_of_pages() {

        /** When **/
        OperationResult<InputStream> result = session
                .reportingService()
                .report("/organizations/organization_1/adhoc/topics/Cascading_multi_select_topic")
                .prepareForRun("PDF", 0,-1)
                .parameter("Cascading_state_multi_select", "CA")
                .parameter("Cascading_name_single_select", "Adams-Steen Transportation Holdings")
                .parameter("Country_multi_select", "USA")
                .run();

        InputStream entity = result.getEntity();
        /** Then **/
        assertNotNull(entity);

    }

    @Test
    public void should_return_proper_entity_without_numbers_of_pages() {

        /** When **/
        OperationResult<InputStream> result = session
                .reportingService()
                .report("/organizations/organization_1/adhoc/topics/Cascading_multi_select_topic")
                .prepareForRun(ReportOutputFormat.PDF)
                .parameter("Cascading_state_multi_select", "CA")
                .parameter("Cascading_name_single_select", "Adams-Steen Transportation Holdings")
                .parameter("Country_multi_select", "USA")
                .run();

        InputStream entity = result.getEntity();
        /** Then **/
        assertNotNull(entity);
    }

    @Test
    public void should_return_proper_entity_with_page_range() {

        /** When **/
        OperationResult<InputStream> result = session
                .reportingService()
                .report("/organizations/organization_1/adhoc/topics/Cascading_multi_select_topic")
                .prepareForRun(ReportOutputFormat.PDF)
                .parameter("Cascading_state_multi_select", "CA")
                .parameter("Cascading_name_single_select", "Adams-Steen Transportation Holdings")
                .parameter("Country_multi_select", "USA")
                .run();

        InputStream entity = result.getEntity();
        /** Then **/
        assertNotNull(entity);

    }

    @Test
    public void should_return_proper_entity_in_async_mode() {

        /** When **/
        ReportExecutionRequest request = new ReportExecutionRequest();
        request.setReportUnitUri("/organizations/organization_1/adhoc/topics/Cascading_multi_select_topic");
        request
                .setAsync(true)
                .setOutputFormat(ReportOutputFormat.HTML);

        OperationResult<ReportExecutionDescriptor> operationResult = session
                .reportingService()
                .newReportExecutionRequest(request);

        ReportExecutionDescriptor reportExecutionDescriptor = operationResult.getEntity();
        /** Then **/
        assertNotNull(reportExecutionDescriptor);
    }

    @Test
    public void should_return_proper_entity_in_async_mode_if_format_is_string() {

        /** When **/
        ReportExecutionRequest request = new ReportExecutionRequest();
        request.setReportUnitUri("/organizations/organization_1/adhoc/topics/Cascading_multi_select_topic");
        request
                .setAsync(true)
                .setOutputFormat("html");

        OperationResult<ReportExecutionDescriptor> operationResult = session
                .reportingService()
                .newReportExecutionRequest(request);

        ReportExecutionDescriptor reportExecutionDescriptor = operationResult.getEntity();
        /** Then **/
        assertNotNull(reportExecutionDescriptor);
    }

    @AfterClass
    public void after() {
        session.logout();
    }

    private void reportToPdf(InputStream entity) {
        OutputStream output = null;
        try {
            output = new FileOutputStream("file.pdf");
            int i = 0;
            while (i != -1) {
                i = entity.read();
                output.write(i);
                output.flush();

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            try {
                entity.close();
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}