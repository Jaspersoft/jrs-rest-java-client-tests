    package com.jaspersoft.jasperserver.jaxrs.client.apiadapters.reporting;

import com.jaspersoft.jasperserver.dto.reports.ReportParameter;
import com.jaspersoft.jasperserver.dto.reports.ReportParameters;
import com.jaspersoft.jasperserver.jaxrs.client.RestClientTestUtil;
import com.jaspersoft.jasperserver.jaxrs.client.core.operationresult.OperationResult;
import com.jaspersoft.jasperserver.jaxrs.client.dto.reports.ExportDescriptor;
import com.jaspersoft.jasperserver.jaxrs.client.dto.reports.ReportExecutionDescriptor;
import com.jaspersoft.jasperserver.jaxrs.client.dto.reports.ReportExecutionRequest;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static java.util.Arrays.asList;
import static org.testng.AssertJUnit.assertNotNull;


/**
 * @author Alex Krasnyanskiy
 * @author Tetiana Iefimenko
 */
public class ReportingServiceTest extends RestClientTestUtil {


    private final String reportUnitUri = "/organizations/organization_1/adhoc/topics/Cascading_multi_select_topic";

    @BeforeClass
    public void before() {
        initClient();
        initSession();
    }

    @Test
    public void should_return_proper_entity_for_pdf_report_output_format() {

        // When
        OperationResult<InputStream> result = session
                .reportingService()
                .report(reportUnitUri)
                .prepareForRun(ReportOutputFormat.PDF, 1)
                .parameter("Cascading_state_multi_select", "CA")
                .parameter("Cascading_state_multi_select", "OR", "WA")
                .parameter("Cascading_name_single_select", "Adams-Steen Transportation Holdings")
                .parameter("Country_multi_select", "USA")
                .run();

        InputStream entity = result.getEntity();
        // Then
        assertNotNull(entity);
    }

    @Test
    public void should_return_proper_entity_for_LA_timezone() {

        // When
        OperationResult<InputStream> result = session
                .reportingService()
                .report("/public/Samples/Reports/12g.PromotionDetailsReport")
                .prepareForRun(ReportOutputFormat.PDF, 1)
                .forTimeZone(TimeZone.getTimeZone("America/Los_Angeles"))
                .run();

        InputStream entity = result.getEntity();
        // Then
        assertNotNull(entity);
    }

    @Test
    public void should_return_proper_entity_for_csv_report_output_format() {

        // When
        OperationResult<InputStream> result = session
                .reportingService()
                .report(reportUnitUri)
                .prepareForRun("csv", 1)
                .parameter("Cascading_state_multi_select", "CA")
                .parameter("Cascading_state_multi_select", "OR", "WA")
                .parameter("Cascading_name_single_select", "Adams-Steen Transportation Holdings")
                .parameter("Country_multi_select", "USA")
                .run();

        InputStream entity = result.getEntity();
        // Then
        assertNotNull(entity);

    }

    @Test
    public void should_return_proper_entity() {

        // When
        OperationResult<InputStream> result = client
                .authenticate("superuser", "superuser")
                .reportingService()
                .report(reportUnitUri)
                .prepareForRun(ReportOutputFormat.PDF, 1)
                .parameter("Cascading_state_multi_select", "CA")
                .parameter("Cascading_state_multi_select", "OR", "WA")
                .run();
        InputStream entity = result.getEntity();
        // Then
        assertNotNull(entity);
    }

    @Test
    public void should_return_proper_entity_if_passed_number_of_pages_zero() {

        // When
        OperationResult<InputStream> result = session
                .reportingService()
                .report(reportUnitUri)
                .prepareForRun(ReportOutputFormat.PDF, 0)
                .parameter("Cascading_state_multi_select", "CA")
                .parameter("Cascading_state_multi_select", "OR", "WA")
                .parameter("Cascading_name_single_select", "Adams-Steen Transportation Holdings")
                .parameter("Country_multi_select", "USA")
                .run();

        InputStream entity = result.getEntity();
        // Then
        assertNotNull(entity);

    }
    @Test
    public void should_return_proper_entity_if_pass_string_output_format() {

        // When
        OperationResult<InputStream> result = session
                .reportingService()
                .report(reportUnitUri)
                .prepareForRun("PDF", 1)
                .parameter("Cascading_state_multi_select", "CA")
                .parameter("Cascading_name_single_select", "Adams-Steen Transportation Holdings")
                .parameter("Country_multi_select", "USA")
                .run();

        InputStream entity = result.getEntity();
        // Then
        assertNotNull(entity);

    }

    @Test
    public void should_return_proper_entity_if_passed_wrong_number_of_pages() {

        // When
        OperationResult<InputStream> result = session
                .reportingService()
                .report(reportUnitUri)
                .prepareForRun("PDF", 0, -1, 1)
                .parameter("Cascading_state_multi_select", "CA")
                .parameter("Cascading_name_single_select", "Adams-Steen Transportation Holdings")
                .parameter("Country_multi_select", "USA")
                .run();

        InputStream entity = result.getEntity();
        // Then
        assertNotNull(entity);

    }

    @Test
    public void should_return_proper_entity_if_passed_all_wrong_number_of_pages() {

        // When
        OperationResult<InputStream> result = session
                .reportingService()
                .report(reportUnitUri)
                .prepareForRun("PDF", 0, -1)
                .parameter("Cascading_state_multi_select", "CA")
                .parameter("Cascading_name_single_select", "Adams-Steen Transportation Holdings")
                .parameter("Country_multi_select", "USA")
                .run();

        InputStream entity = result.getEntity();
        // Then
        assertNotNull(entity);

    }

    @Test
    public void should_return_proper_entity_without_numbers_of_pages_for_pdf() {

        // When
        OperationResult<InputStream> result = session
                .reportingService()
                .report(reportUnitUri)
                .prepareForRun(ReportOutputFormat.PDF)
                .parameter("Cascading_state_multi_select", "CA")
                .parameter("Cascading_name_single_select", "Adams-Steen Transportation Holdings")
                .parameter("Country_multi_select", "USA")
                .run();

        InputStream entity = result.getEntity();
        // Then
        assertNotNull(entity);
    }

    @Test
    public void should_return_proper_entity_with_page_range_for_csv() {

        // When
        OperationResult<InputStream> result = session
                .reportingService()
                .report(reportUnitUri)
                .prepareForRun(ReportOutputFormat.CSV)
                .parameter("Cascading_state_multi_select", "CA")
                .parameter("Cascading_name_single_select", "Adams-Steen Transportation Holdings")
                .parameter("Country_multi_select", "USA")
                .run();

        InputStream entity = result.getEntity();
        // Then
        assertNotNull(entity);

    }

    @Test
    public void should_return_proper_entity_with_page_range() {

        // When
        OperationResult<InputStream> result = session
                .reportingService()
                .report(reportUnitUri)
                .prepareForRun(ReportOutputFormat.PDF)
                .parameter("Cascading_state_multi_select", "CA")
                .parameter("Cascading_name_single_select", "Adams-Steen Transportation Holdings")
                .parameter("Country_multi_select", "USA")
                .run();

        InputStream entity = result.getEntity();
        // Then
        assertNotNull(entity);

    }

    @Test
    public void should_return_proper_entity_in_async_mode_for_pdf() {

        // When
        ReportExecutionRequest request = new ReportExecutionRequest();
        request.setReportUnitUri(reportUnitUri);
        request
                .setAsync(true)
                .setOutputFormat(ReportOutputFormat.HTML);

        OperationResult<ReportExecutionDescriptor> operationResult = session
                .reportingService()
                .newReportExecutionRequest(request);

        ReportExecutionDescriptor reportExecutionDescriptor = operationResult.getEntity();
        // Then
        assertNotNull(reportExecutionDescriptor);
    }

    @Test
    public void should_return_proper_entity_in_async_mode_if_format_is_string() {

        // When
        ReportExecutionRequest request = new ReportExecutionRequest();
        request.setReportUnitUri(reportUnitUri);
        request
                .setAsync(true)
                .setOutputFormat("html");

        OperationResult<ReportExecutionDescriptor> operationResult = session
                .reportingService()
                .newReportExecutionRequest(request);

        ReportExecutionDescriptor reportExecutionDescriptor = operationResult.getEntity();
        // Then
        assertNotNull(reportExecutionDescriptor);
    }

    @Test
    public void should_export_report_to_xls_in_async_mode() {
        // Given
        ReportingService reportingService = session.reportingService();

        ReportExecutionRequest request = new ReportExecutionRequest();
        ReportParameters reportParameters = new ReportParameters(new LinkedList<ReportParameter>());
        reportParameters.getReportParameters().add(new ReportParameter().setName("Cascading_state_multi_select").setValues(asList("CA")));
        reportParameters.getReportParameters().add(new ReportParameter().setName("Cascading_name_single_select").setValues(asList("Adams-Steen Transportation Holdings\"")));
        reportParameters.getReportParameters().add(new ReportParameter().setName("Country_multi_select").setValues(asList("USA")));
        request
                .setOutputFormat(ReportOutputFormat.XLS)
                .setParameters(reportParameters)
                .setPages("1")
                .setReportUnitUri(reportUnitUri)
                .setAsync(true);

        // When
        OperationResult<ReportExecutionDescriptor> operationResult = reportingService.newReportExecutionRequest(request);

        ReportExecutionDescriptor reportExecutionDescriptor = operationResult.getEntity();

        OperationResult<ReportExecutionDescriptor> executionDetails = reportingService.reportExecutionRequest(reportExecutionDescriptor.getRequestId()).executionDetails();

        ReportExecutionDescriptor descriptor = executionDetails.getEntity();
        while (!descriptor.getStatus().equals("ready"))
        {
            executionDetails = reportingService.reportExecutionRequest(reportExecutionDescriptor.getRequestId()).executionDetails();
            descriptor = executionDetails.getEntity();
        }

        final ReportExecutionRequestBuilder reportExecutionRequest = reportingService.reportExecutionRequest(descriptor.getRequestId());
        final List<ExportDescriptor> exports = descriptor.getExports();
        OperationResult<InputStream> reportOutput = null;
        if (exports != null && exports.size() > 0)
        {
            final String exportId = exports.get(0).getId();
            final ExportExecutionRequestBuilder export = reportExecutionRequest.export(exportId);
            reportOutput = export.outputResource();
        }
        // Then
        assertNotNull(reportOutput);
    }

    @Test
    public void should_export_report_to_pdf_in_LA_timezone_in_async_mode() {
        // Given
        ReportingService reportingService = session.reportingService();

        ReportExecutionRequest request = new ReportExecutionRequest();
        request
                .setOutputFormat(ReportOutputFormat.PDF)
                .setPages("1")
                .setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"))
                .setReportUnitUri("/public/Samples/Reports/12g.PromotionDetailsReport")
                .setAsync(true);

        // When
        OperationResult<ReportExecutionDescriptor> operationResult = reportingService.newReportExecutionRequest(request);

        ReportExecutionDescriptor reportExecutionDescriptor = operationResult.getEntity();

        OperationResult<ReportExecutionDescriptor> executionDetails = reportingService.reportExecutionRequest(reportExecutionDescriptor.getRequestId()).executionDetails();

        ReportExecutionDescriptor descriptor = executionDetails.getEntity();
        while (!descriptor.getStatus().equals("ready"))
        {
            executionDetails = reportingService.reportExecutionRequest(reportExecutionDescriptor.getRequestId()).executionDetails();
            descriptor = executionDetails.getEntity();
        }

        final ReportExecutionRequestBuilder reportExecutionRequest = reportingService.reportExecutionRequest(descriptor.getRequestId());
        final List<ExportDescriptor> exports = descriptor.getExports();
        OperationResult<InputStream> reportOutput = null;
        if (exports != null && exports.size() > 0)
        {
            final String exportId = exports.get(0).getId();
            final ExportExecutionRequestBuilder export = reportExecutionRequest.export(exportId);
            reportOutput = export.outputResource();
        }
        // Then
        assertNotNull(reportOutput);
    }


    @Test
    public void should_export_report_to_csv_in_async_mode() {
        // Given
        ReportingService reportingService = session.reportingService();

        ReportExecutionRequest request = new ReportExecutionRequest();
        ReportParameters reportParameters = new ReportParameters(new LinkedList<ReportParameter>());
        reportParameters.getReportParameters().add(new ReportParameter().setName("Cascading_state_multi_select").setValues(asList("CA")));
        reportParameters.getReportParameters().add(new ReportParameter().setName("Cascading_name_single_select").setValues(asList("Adams-Steen Transportation Holdings\"")));
        reportParameters.getReportParameters().add(new ReportParameter().setName("Country_multi_select").setValues(asList("USA")));
        request
                .setOutputFormat(ReportOutputFormat.CSV)
                .setParameters(reportParameters)
                .setPages("1")
                .setReportUnitUri(reportUnitUri)
                .setAsync(true);

        // When
        OperationResult<ReportExecutionDescriptor> operationResult = reportingService.newReportExecutionRequest(request);

        ReportExecutionDescriptor reportExecutionDescriptor = operationResult.getEntity();

        OperationResult<ReportExecutionDescriptor> executionDetails = reportingService.reportExecutionRequest(reportExecutionDescriptor.getRequestId()).executionDetails();

        ReportExecutionDescriptor descriptor = executionDetails.getEntity();

         ReportExecutionRequestBuilder reportExecutionRequest = reportingService.reportExecutionRequest(descriptor.getRequestId());
         List<ExportDescriptor> exports = descriptor.getExports();
        OperationResult<InputStream> reportOutput = null;
        if (exports != null && exports.size() > 0)
        {
            final String exportId = exports.get(0).getId();
            final ExportExecutionRequestBuilder export = reportExecutionRequest.export(exportId);
            reportOutput = export.outputResource();
        }
        // Then
        assertNotNull(reportOutput);

    }

    @Test
    public void should_export_report_to_csv_in_async_mode_when_format_as_string() {
        // Given
        ReportingService reportingService = session.reportingService();

        ReportExecutionRequest request = new ReportExecutionRequest();
        ReportParameters reportParameters = new ReportParameters(new LinkedList<ReportParameter>());
        reportParameters.getReportParameters().add(new ReportParameter().setName("Cascading_state_multi_select").setValues(asList("CA")));
        reportParameters.getReportParameters().add(new ReportParameter().setName("Cascading_name_single_select").setValues(asList("Adams-Steen Transportation Holdings\"")));
        reportParameters.getReportParameters().add(new ReportParameter().setName("Country_multi_select").setValues(asList("USA")));
        request
                .setOutputFormat("csv")
                .setParameters(reportParameters)
                .setPages("1")
                .setReportUnitUri(reportUnitUri)
                .setAsync(true);

        // When
        OperationResult<ReportExecutionDescriptor> operationResult = reportingService.newReportExecutionRequest(request);

        ReportExecutionDescriptor reportExecutionDescriptor = operationResult.getEntity();

        OperationResult<ReportExecutionDescriptor> executionDetails = reportingService.reportExecutionRequest(reportExecutionDescriptor.getRequestId()).executionDetails();

        ReportExecutionDescriptor descriptor = executionDetails.getEntity();

         ReportExecutionRequestBuilder reportExecutionRequest = reportingService.reportExecutionRequest(descriptor.getRequestId());
         List<ExportDescriptor> exports = descriptor.getExports();
        OperationResult<InputStream> reportOutput = null;
        if (exports != null && exports.size() > 0)
        {
            final String exportId = exports.get(0).getId();
            final ExportExecutionRequestBuilder export = reportExecutionRequest.export(exportId);
            reportOutput = export.outputResource();
        }
        // Then
        assertNotNull(reportOutput);

    }


    @AfterClass
    public void after() {
        session.logout();
    }


}