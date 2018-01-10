package com.jaspersoft.jasperserver.jaxrs.client.apiadapters.reporting;

import com.jaspersoft.jasperserver.dto.executions.ExecutionStatus;
import com.jaspersoft.jasperserver.dto.reports.ReportParameter;
import com.jaspersoft.jasperserver.dto.reports.ReportParameters;
import com.jaspersoft.jasperserver.jaxrs.client.RestClientTestUtil;
import com.jaspersoft.jasperserver.jaxrs.client.apiadapters.reporting.util.PageRange;
import com.jaspersoft.jasperserver.jaxrs.client.apiadapters.reporting.util.ReportSearchParameter;
import com.jaspersoft.jasperserver.jaxrs.client.core.operationresult.OperationResult;
import com.jaspersoft.jasperserver.jaxrs.client.dto.reports.ExportDescriptor;
import com.jaspersoft.jasperserver.jaxrs.client.dto.reports.ExportExecution;
import com.jaspersoft.jasperserver.jaxrs.client.dto.reports.ExportExecutionDescriptor;
import com.jaspersoft.jasperserver.jaxrs.client.dto.reports.ExportExecutionOptions;
import com.jaspersoft.jasperserver.jaxrs.client.dto.reports.OutputResourceDescriptor;
import com.jaspersoft.jasperserver.jaxrs.client.dto.reports.ReportExecution;
import com.jaspersoft.jasperserver.jaxrs.client.dto.reports.ReportExecutionDescriptor;
import com.jaspersoft.jasperserver.jaxrs.client.dto.reports.ReportExecutionRequest;
import com.jaspersoft.jasperserver.jaxrs.client.dto.reports.ReportExecutionStatusEntity;
import com.jaspersoft.jasperserver.jaxrs.client.dto.reports.ReportExecutionStatusObject;
import com.jaspersoft.jasperserver.jaxrs.client.dto.reports.ReportExecutionsSetWrapper;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertTrue;
import static org.testng.AssertJUnit.assertEquals;
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
    public void should_run_report_pdf_sync_with_reportExecutionRequest() {
        final ReportParameter reportParameter1 = new ReportParameter().setName("Cascading_state_multi_select").setValues(asList("CA"));
        final ReportParameter reportParameter2 = new ReportParameter().setName("Cascading_state_multi_select").setValues(asList("OR", "WA"));
        final ReportParameter reportParameter3 = new ReportParameter().setName("Cascading_name_single_select").setValues(asList("Adams-Steen Transportation Holdings"));
        final ReportParameter reportParameter4 = new ReportParameter().setName("Country_multi_select").setValues(asList("USA"));
        final ReportParameters reportParameters = new ReportParameters();
        reportParameters.setReportParameters(asList(reportParameter1, reportParameter2, reportParameter3, reportParameter4));

        final ReportExecutionRequest reportExecutionRequest = new ReportExecutionRequest()
                .setReportUnitUri(reportUnitUri)
                .setPages("1")
                .setParameters(reportParameters)
                .setOutputFormat("pdf")
                .setAsync(Boolean.TRUE);
        // When
        OperationResult<ReportExecution> result = session
                .reportingService()
                .reportExecution(reportExecutionRequest)
                .run();

        ReportExecution entity = result.getEntity();
        // Then
        assertNotNull(entity);
        assertEquals(entity.getStatus(), ExecutionStatus.ready.name());
    }

    @Test
    public void should_run_report_pdf_sync() {

        // When
        OperationResult<ReportExecution> result = session
                .reportingService()
                .report(reportUnitUri)
                .reportExecutions()
                .outputFormat(com.jaspersoft.jasperserver.jaxrs.client.apiadapters.reporting.util.ReportOutputFormat.PDF)
                .pages(1)
                .reportParameter(new ReportParameter().setName("Cascading_state_multi_select").setValues(asList("CA")))
                .reportParameter(new ReportParameter().setName("Cascading_state_multi_select").setValues(asList("OR", "WA")))
                .reportParameter(new ReportParameter().setName("Cascading_name_single_select").setValues(asList("Adams-Steen Transportation Holdings")))
                .reportParameter("Country_multi_select", "USA")
                .async(Boolean.FALSE)
                .run();

        ReportExecution entity = result.getEntity();
        // Then
        assertNotNull(entity);
        assertEquals(entity.getStatus(), ExecutionStatus.ready.name());
    }

    @Test
    public void should_run_report_pdf_sync_with_timeZone() {

        // When
        OperationResult<ReportExecution> result = session
                .reportingService()
                .report(reportUnitUri)
                .reportExecutions()
                .outputFormat(com.jaspersoft.jasperserver.jaxrs.client.apiadapters.reporting.util.ReportOutputFormat.PDF)
                .pages(1)
                .reportParameter(new ReportParameter().setName("Cascading_state_multi_select").setValues(asList("CA")))
                .reportParameter(new ReportParameter().setName("Cascading_state_multi_select").setValues(asList("OR", "WA")))
                .reportParameter(new ReportParameter().setName("Cascading_name_single_select").setValues(asList("Adams-Steen Transportation Holdings")))
                .reportParameter("Country_multi_select", "USA")
                .async(Boolean.FALSE)
                .timeZone(TimeZone.getTimeZone("America/Los_Angeles"))
                .run();

        ReportExecution entity = result.getEntity();
        // Then
        assertNotNull(entity);
        assertEquals(entity.getStatus(), ExecutionStatus.ready.name());
    }


    @Test
    public void should_get_report_execution_runtime_information() {//204

        // When
        session
                .reportingService()
                .report(reportUnitUri)
                .reportExecutions()
                .outputFormat(com.jaspersoft.jasperserver.jaxrs.client.apiadapters.reporting.util.ReportOutputFormat.PDF)
                .pages(1)
                .reportParameter(new ReportParameter().setName("Cascading_state_multi_select").setValues(asList("CA")))
                .reportParameter(new ReportParameter().setName("Cascading_state_multi_select").setValues(asList("OR", "WA")))
                .reportParameter(new ReportParameter().setName("Cascading_name_single_select").setValues(asList("Adams-Steen Transportation Holdings")))
                .reportParameter("Country_multi_select", "USA")
                .async(Boolean.FALSE)
                .run();

        final OperationResult<ReportExecutionsSetWrapper> operationResult = session
                .reportingService()
                .reportExecutions()
                .queryParameter(ReportSearchParameter.REPORT_URI, reportUnitUri)
                .search();
        final ReportExecutionsSetWrapper entity = operationResult.getEntity();

        // Then
        assertNotNull(entity);
        assertTrue(entity.getReportExecutionStatuses().size() == 1);
    }

    @Test
    public void should_get_report_execution_details() {

        // When
        final OperationResult<ReportExecution> executionOperationR4esult = session
                .reportingService()
                .report(reportUnitUri)
                .reportExecutions()
                .outputFormat(com.jaspersoft.jasperserver.jaxrs.client.apiadapters.reporting.util.ReportOutputFormat.PDF)
                .pages(1)
                .reportParameter(new ReportParameter().setName("Cascading_state_multi_select").setValues(asList("CA")))
                .reportParameter(new ReportParameter().setName("Cascading_state_multi_select").setValues(asList("OR", "WA")))
                .reportParameter(new ReportParameter().setName("Cascading_name_single_select").setValues(asList("Adams-Steen Transportation Holdings")))
                .reportParameter("Country_multi_select", "USA")
                .run();
        String executionId = executionOperationR4esult.getEntity().getRequestId();

        final OperationResult<ReportExecution> operationResult = session
                .reportingService()
                .reportExecution(executionId)
                .details();
        final ReportExecution entity = operationResult.getEntity();

        // Then
        assertNotNull(entity);
    }

    @Test
    public void should_get_report_execution_status() {

        // When
        final OperationResult<ReportExecution> executionOperationR4esult = session
                .reportingService()
                .report(reportUnitUri)
                .reportExecutions()
                .outputFormat(com.jaspersoft.jasperserver.jaxrs.client.apiadapters.reporting.util.ReportOutputFormat.PDF)
                .pages(1)
                .reportParameter(new ReportParameter().setName("Cascading_state_multi_select").setValues(asList("CA")))
                .reportParameter(new ReportParameter().setName("Cascading_state_multi_select").setValues(asList("OR", "WA")))
                .reportParameter(new ReportParameter().setName("Cascading_name_single_select").setValues(asList("Adams-Steen Transportation Holdings")))
                .reportParameter("Country_multi_select", "USA")
                .async(Boolean.FALSE)
                .run();
        String executionId = executionOperationR4esult.getEntity().getRequestId();

        final OperationResult<ReportExecutionStatusObject> operationResult = session
                .reportingService()
                .reportExecution(executionId)
                .status();
        final ReportExecutionStatusObject entity = operationResult.getEntity();

        // Then
        assertNotNull(entity);
        assertEquals(entity.getValue(), ExecutionStatus.ready);
    }

    @Test
    public void should_cancel_report_execution() {

        // When
        final OperationResult<ReportExecution> executionOperationR4esult = session
                .reportingService()
                .report(reportUnitUri)
                .reportExecutions()
                .outputFormat(com.jaspersoft.jasperserver.jaxrs.client.apiadapters.reporting.util.ReportOutputFormat.PDF)
                .pages(1)
                .reportParameter(new ReportParameter().setName("Cascading_state_multi_select").setValues(asList("CA")))
                .reportParameter(new ReportParameter().setName("Cascading_state_multi_select").setValues(asList("OR", "WA")))
                .reportParameter(new ReportParameter().setName("Cascading_name_single_select").setValues(asList("Adams-Steen Transportation Holdings")))
                .reportParameter("Country_multi_select", "USA")
                .async(Boolean.TRUE)
                .run();
        String executionId = executionOperationR4esult.getEntity().getRequestId();

        final OperationResult<ReportExecutionStatusEntity> operationResult = session
                .reportingService()
                .reportExecution(executionId)
                .cancel();

        // Then
        assertNotNull(operationResult.getEntity());
        assertEquals(operationResult.getEntity().getValue(), ExecutionStatus.cancelled);
    }

    @Test
    public void should_update_report_parameters() {

        // When
        OperationResult<ReportExecution> result = session
                .reportingService()
                .report(reportUnitUri)
                .reportExecutions()
                .outputFormat(com.jaspersoft.jasperserver.jaxrs.client.apiadapters.reporting.util.ReportOutputFormat.PDF)
                .pages(1)
                .reportParameter(new ReportParameter().setName("Cascading_state_multi_select").setValues(asList("CA")))
                .reportParameter(new ReportParameter().setName("Cascading_state_multi_select").setValues(asList("OR", "WA")))
                .reportParameter(new ReportParameter().setName("Cascading_name_single_select").setValues(asList("Adams-Steen Transportation Holdings")))
                .reportParameter("Country_multi_select", "USA")
                .run();

        assertNotNull(result.getEntity());
        final ArrayList<ReportParameter> reportParameters = new ArrayList<ReportParameter>() {{
            add(new ReportParameter().setName("Cascading_state_multi_select").setValues(asList("BC")));
            add(new ReportParameter().setName("Cascading_name_single_select").setValues(asList("Johnson-Marlowe Telecommunications Group")));
            add(new ReportParameter().setName("Country_multi_select").setValues(asList("Canada")));
        }};

        String executionId = result.getEntity().getRequestId();
        session
                .reportingService()
                .reportExecution(executionId)
                .updateParameters(reportParameters);

//        final ReportExecutionDescriptor details = session.reportingService().reportExecution(executionId).details().getEntity();
        // Then
   // CHECK PARAMS!!!!!
    }


    @Test
    public void should_delete_report_execution() {

        // When
        OperationResult<ReportExecution> runOperationResult = session
                .reportingService()
                .report(reportUnitUri)
                .reportExecutions()
                .outputFormat(com.jaspersoft.jasperserver.jaxrs.client.apiadapters.reporting.util.ReportOutputFormat.PDF)
                .pages(1)
                .async(Boolean.FALSE)
                .run();

        String executionId = runOperationResult.getEntity().getRequestId();
        final OperationResult deleteOperationresult = session
                .reportingService()
                .reportExecution(executionId)
                .delete();
        // Then
        assertEquals(deleteOperationresult.getResponseStatus(), 204);
    }


    @Test
    public void should_run_report_execution_with_options_as_object() {

        // When
        OperationResult<ReportExecution> runOperationResult = session
                .reportingService()
                .report(reportUnitUri)
                .reportExecutions()
                .outputFormat(com.jaspersoft.jasperserver.jaxrs.client.apiadapters.reporting.util.ReportOutputFormat.PDF)
                .pages(1)
                .async(Boolean.FALSE)
                .run();

        String executionId = runOperationResult.getEntity().getRequestId();
        ExportExecutionOptions executionOptions = new ExportExecutionOptions();
        executionOptions.setBaseUrl(System.getProperty("uri"));
        executionOptions.setOutputFormat(ReportOutputFormat.PDF.name());
        executionOptions.setPages("1");
        executionOptions.setIgnorePagination(Boolean.FALSE);
        final OperationResult<ExportExecution> expoirtExecutionOpertaionResult = session
                .reportingService()
                .reportExecution(executionId)
                .export()
                .withOptions(executionOptions)
                .run();
        // Then
        assertNotNull(expoirtExecutionOpertaionResult.getEntity());
        assertEquals(expoirtExecutionOpertaionResult.getEntity().getStatus().name(), ExecutionStatus.execution.name());
    }

    @Test
    public void should_run_report_export_execution() {// ADD BUILDER

        // When
        OperationResult<ReportExecution> runOperationResult = session
                .reportingService()
                .report(reportUnitUri)
                .reportExecutions()
                .outputFormat(com.jaspersoft.jasperserver.jaxrs.client.apiadapters.reporting.util.ReportOutputFormat.PDF)
                .pages(1)
                .run();

        String executionId = runOperationResult.getEntity().getRequestId();

        ExportExecutionOptions executionOptions = new ExportExecutionOptions();
        executionOptions.setBaseUrl(System.getProperty("uri"));
        executionOptions.setOutputFormat(ReportOutputFormat.PDF.name());
        executionOptions.setPages("1");
        executionOptions.setIgnorePagination(Boolean.FALSE);

        final OperationResult<ExportExecution> operationResult = session
                .reportingService()
                .reportExecution(executionId)
                .export()
                .withOptions(executionOptions)
                .run();

        final String exportExecutionId = operationResult.getEntity().getId();

        // Then
//        assertNotNull(statusOperationResult.getEntity());
//        assertEquals(statusOperationResult.getEntity().getValue(), "ready");
    }

    @Test
    public void should_return_export_execution_status() {

        // When
        OperationResult<ReportExecution> runOperationResult = session
                .reportingService()
                .report(reportUnitUri)
                .reportExecutions()
                .outputFormat(com.jaspersoft.jasperserver.jaxrs.client.apiadapters.reporting.util.ReportOutputFormat.PDF)
                .pages(1)
                .async(Boolean.FALSE)
                .run();

        String executionId = runOperationResult.getEntity().getRequestId();

        final OperationResult<ExportExecution> exportExecutionOperationResult = session
                .reportingService()
                .reportExecution(executionId)
                .export()
                .baseUrl(System.getProperty("uri"))
                .ignorePagination(Boolean.FALSE)
                .outputFormat(com.jaspersoft.jasperserver.jaxrs.client.apiadapters.reporting.util.ReportOutputFormat.PDF)
                .pages(new PageRange(1, 3))
                .run();
        final String exportId = exportExecutionOperationResult.getEntity().getId();
        final OperationResult<ReportExecutionStatusEntity> statusOperationResult = session
                .reportingService()
                .reportExecution(executionId)
                .export(exportId)
                .status();
        // Then
        assertNotNull(statusOperationResult.getEntity());
        assertEquals(statusOperationResult.getEntity().getValue(), ExecutionStatus.execution.name());
    }

    @Test
    public void should_return_export_execution_status_with_error_descriptor() {

        // When
        OperationResult<ReportExecution> runOperationResult = session
                .reportingService()
                .report(reportUnitUri)
                .reportExecutions()
                .outputFormat(com.jaspersoft.jasperserver.jaxrs.client.apiadapters.reporting.util.ReportOutputFormat.PDF)
                .pages(1)
                .async(Boolean.FALSE)
                .run();

        String executionId = runOperationResult.getEntity().getRequestId();

        final OperationResult<ExportExecution> exportExecutionOperationResult = session
                .reportingService()
                .reportExecution(executionId)
                .export()
                .baseUrl(System.getProperty("uri"))
                .ignorePagination(Boolean.FALSE)
                .outputFormat(com.jaspersoft.jasperserver.jaxrs.client.apiadapters.reporting.util.ReportOutputFormat.PDF)
                .pages(new PageRange(1, 3))
                .run();
        final String exportId = exportExecutionOperationResult.getEntity().getId();
        final OperationResult<ReportExecutionStatusEntity> statusOperationResult = session
                .reportingService()
                .reportExecution(executionId)
                .export(exportId)
                .withErrordescriptor(Boolean.TRUE)
                .status();
        // Then
        assertNotNull(statusOperationResult.getEntity());
        assertEquals(statusOperationResult.getEntity().getValue(), ExecutionStatus.failed.name());
//        assertNotNull(statusOperationResult.getEntity(), ExecutionStatus.failed.name());
    }

    @Test
    public void should_return_export_output_resource() throws InterruptedException {//NOT FOUND

        // When
        OperationResult<ReportExecution> runOperationResult = session
                .reportingService()
                .report(reportUnitUri)
                .reportExecutions()
                .outputFormat(com.jaspersoft.jasperserver.jaxrs.client.apiadapters.reporting.util.ReportOutputFormat.PDF)
                .pages(1)
                .async(Boolean.FALSE)
                .run();

        String executionId = runOperationResult.getEntity().getRequestId();


        final OperationResult<ExportExecution> executionOperationResult = session
                .reportingService()
                .reportExecution(executionId)
                .export()
                .baseUrl(System.getProperty("uri"))
                .ignorePagination(Boolean.FALSE)
                .outputFormat(com.jaspersoft.jasperserver.jaxrs.client.apiadapters.reporting.util.ReportOutputFormat.PDF)
                .pages(1)
                .run();
        final String exportId = executionOperationResult.getEntity().getId();

        final OperationResult<InputStream> outputResourceOperationResult = session
                .reportingService()
                .reportExecution(executionId)
                .export(exportId)
                .suppressContentDisposition(false)
                .getOutputResource();
        // Then
        assertNotNull(outputResourceOperationResult.getEntity());
        RestClientTestUtil.streamToFile(outputResourceOperationResult.getEntity(), "reportOutput.pdf");
    }

    @Test
    public void should_return_export_output_test_resource() throws InterruptedException {//NOT FOUND

        // When
        OperationResult<ReportExecution> runOperationResult = session
                .reportingService()
                .report(reportUnitUri)
                .reportExecutions()
                .outputFormat(com.jaspersoft.jasperserver.jaxrs.client.apiadapters.reporting.util.ReportOutputFormat.PDF)
                .pages(1)
                .async(Boolean.FALSE)
                .run();

        String executionId = runOperationResult.getEntity().getRequestId();


        final OperationResult<ExportExecution> executionOperationResult = session
                .reportingService()
                .reportExecution(executionId)
                .export()
                .baseUrl(System.getProperty("uri"))
                .ignorePagination(Boolean.FALSE)
                .outputFormat(com.jaspersoft.jasperserver.jaxrs.client.apiadapters.reporting.util.ReportOutputFormat.HTML)
                .pages(1)
                .run();
        final String exportId = executionOperationResult.getEntity().getId();

        final OperationResult<String> outputResourceOperationResult = session
                .reportingService()
                .reportExecution(executionId)
                .export(exportId)
                .suppressContentDisposition(false)
                .getOutputResourceAsText();
        // Then
        assertNotNull(outputResourceOperationResult.getEntity());
        assertTrue(outputResourceOperationResult.getEntity().indexOf("<html>") != -1);
    }

    @Test
    public void should_return_export_output_resource_attachment() {

        // When
        OperationResult<ReportExecution> runOperationResult = session
                .reportingService()
                .report("/public/Samples/Reports/State_Performance")
                .reportExecutions()
                .outputFormat(com.jaspersoft.jasperserver.jaxrs.client.apiadapters.reporting.util.ReportOutputFormat.HTML)
                .pages(1)
                .async(Boolean.FALSE)
                .run();

        final ReportExecution entity = runOperationResult.getEntity();
        String executionId = entity.getRequestId();
        final ExportExecution export = entity.getExports().iterator().next();
        final OutputResourceDescriptor attachment = export.getAttachments().entrySet().iterator().next().getValue();

        final String exportId = export.getId();

        final OperationResult<InputStream> outputResourceOperationResult = session
                .reportingService()
                .reportExecution(executionId)
                .export(exportId)
                .getOutputResourceAttachment(attachment.getFileName(), attachment.getContentType());
        // Then
        assertNotNull(outputResourceOperationResult.getEntity());
    }

    @Test
    public void should_run_report_pdf_async() {

        // When
        OperationResult<ReportExecution> result = session
                .reportingService()
                .report(reportUnitUri)
                .reportExecutions()
                .outputFormat(com.jaspersoft.jasperserver.jaxrs.client.apiadapters.reporting.util.ReportOutputFormat.PDF)
                .pages(1)
                .reportParameter(new ReportParameter().setName("Cascading_state_multi_select").setValues(asList("CA")))
                .reportParameter(new ReportParameter().setName("Cascading_state_multi_select").setValues(asList("OR", "WA")))
                .reportParameter(new ReportParameter().setName("Cascading_name_single_select").setValues(asList("Adams-Steen Transportation Holdings")))
                .reportParameter(new ReportParameter().setName("Country_multi_select").setValues(asList("USA")))
                .run();

        ReportExecution executionDescriptor = result.getEntity();
        // Then
        assertNotNull(executionDescriptor);
        assertNotNull(executionDescriptor.getStatus());
    }

    @Deprecated
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

    @Deprecated
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

        RestClientTestUtil.streamToFile(entity, "report.pdf");
    }

    @Deprecated
    @Test
    public void should_return_proper_entity_for_LA_timezone_de_locale() {

        // When
        OperationResult<InputStream> result = session
                .reportingService()
                .report("/public/Samples/Reports/12g.PromotionDetailsReport")
                .prepareForRun(ReportOutputFormat.PDF, 1)
                .forTimeZone(TimeZone.getTimeZone("America/Los_Angeles"))
                .parameter("userLocale", "de")
                .run();

        InputStream entity = result.getEntity();
        // Then
        assertNotNull(entity);

        RestClientTestUtil.streamToFile(entity, "report.pdf");
    }

    @Deprecated
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

    @Deprecated
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

    @Deprecated
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

    @Deprecated
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

    @Deprecated
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

    @Deprecated
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

    @Deprecated
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

    @Deprecated
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

    @Deprecated
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

    @Deprecated
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

    @Deprecated
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

    @Deprecated
    @Test
    public void should_return_proper_entity_in_async_mode_if_format_is_string_with_executionOptions() {

        // When
        ReportExecutionRequest request = new ReportExecutionRequest();
        request.setReportUnitUri(reportUnitUri);
        request
                .setAsync(true)
                .setOutputFormat("pdf");

        OperationResult<ReportExecutionDescriptor> operationResult = session
                .reportingService()
                .newReportExecutionRequest(request);

        final String requestId = operationResult.getEntity().getRequestId();

        ExportExecutionOptions exportExecutionOptions = new ExportExecutionOptions()
                .setOutputFormat(ReportOutputFormat.HTML.name())
                .setPages("10000-20000");

        final OperationResult<ExportExecutionDescriptor> executionDescriptorOperationResult = session
                .reportingService()
                .reportExecutionRequest(requestId)
                .runExport(exportExecutionOptions);

        final ExportExecutionDescriptor executionDescriptor = executionDescriptorOperationResult.getEntity();

        // Then
        assertNotNull(executionDescriptor);
    }

    @Deprecated
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
        while (!descriptor.getStatus().equals("ready")) {
            executionDetails = reportingService.reportExecutionRequest(reportExecutionDescriptor.getRequestId()).executionDetails();
            descriptor = executionDetails.getEntity();
        }

        final ReportExecutionRequestBuilder reportExecutionRequest = reportingService.reportExecutionRequest(descriptor.getRequestId());
        final List<ExportDescriptor> exports = descriptor.getExports();
        OperationResult<InputStream> reportOutput = null;
        if (exports != null && exports.size() > 0) {
            final String exportId = exports.get(0).getId();
            final ExportExecutionRequestBuilder export = reportExecutionRequest.export(exportId);
            reportOutput = export.outputResource();
        }
        // Then
        assertNotNull(reportOutput);
    }

    @Deprecated
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
        while (!descriptor.getStatus().equals("ready")) {
            executionDetails = reportingService.reportExecutionRequest(reportExecutionDescriptor.getRequestId()).executionDetails();
            descriptor = executionDetails.getEntity();
        }

        final ReportExecutionRequestBuilder reportExecutionRequest = reportingService.reportExecutionRequest(descriptor.getRequestId());
        final List<ExportDescriptor> exports = descriptor.getExports();
        OperationResult<InputStream> reportOutput = null;
        if (exports != null && exports.size() > 0) {
            final String exportId = exports.get(0).getId();
            final ExportExecutionRequestBuilder export = reportExecutionRequest.export(exportId);
            reportOutput = export.outputResource();
        }
        // Then
        assertNotNull(reportOutput);
    }

    @Deprecated
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
        if (exports != null && exports.size() > 0) {
            final String exportId = exports.get(0).getId();
            final ExportExecutionRequestBuilder export = reportExecutionRequest.export(exportId);
            reportOutput = export.outputResource();
        }
        // Then
        assertNotNull(reportOutput);

    }

    @Deprecated
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
        if (exports != null && exports.size() > 0) {
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