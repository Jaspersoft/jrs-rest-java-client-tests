    package com.jaspersoft.jasperserver.jaxrs.client.apiadapters.reporting.reportoptions;

    import com.jaspersoft.jasperserver.dto.reports.ReportParameter;
    import com.jaspersoft.jasperserver.dto.reports.options.ReportOptionsSummary;
    import com.jaspersoft.jasperserver.dto.reports.options.ReportOptionsSummaryList;
    import com.jaspersoft.jasperserver.jaxrs.client.RestClientTestUtil;
    import com.jaspersoft.jasperserver.jaxrs.client.core.operationresult.OperationResult;
    import java.util.LinkedList;
    import java.util.List;
    import javax.ws.rs.core.MultivaluedHashMap;
    import org.testng.annotations.AfterClass;
    import org.testng.annotations.BeforeClass;
    import org.testng.annotations.Test;

    import static java.util.Arrays.asList;
    import static org.testng.Assert.assertEquals;
    import static org.testng.AssertJUnit.assertNotNull;


    /**
     * @author Tetiana Iefimenko
     */
    public class ReportOptionsServiceTest extends RestClientTestUtil {


        public static final String OPTIONS_LABEL = "new_options";
        private final String reportUnitUri = "organizations/organization_1/adhoc/topics/Cascading_multi_select_topic";

        @BeforeClass
        public void before() {
            initClient();
            initSession();
        }

        @Test
        public void should_return_report_options() {

            // When
            OperationResult<ReportOptionsSummaryList> result = session
                    .reportingService()
                    .report(reportUnitUri)
                    .reportOptions()
                    .get();

            final ReportOptionsSummaryList entity = result.getEntity();
            // Then
            assertEquals(result.getResponseStatus(), 200);
        }

        @Test(dependsOnMethods = "should_return_report_options")
        public void should_create_report_options() {
            final MultivaluedHashMap<String, String> map = new MultivaluedHashMap<String, String>();
            map.addAll("Country_multi_select", "Mexico");
            map.addAll("Cascading_state_multi_select", "Guerrero", "Sinaloa");
            map.addAll("Cascading_name_single_select", "Crow-Sims Construction Associates");

            // When
            OperationResult<ReportOptionsSummary> result = session
                    .reportingService()
                    .report(reportUnitUri)
                    .reportOptions(map)
                    .label(OPTIONS_LABEL)
                    .create();

            final ReportOptionsSummary entity = result.getEntity();
            // Then
            assertEquals(result.getResponseStatus(), 200);
            assertNotNull(entity);
            assertEquals(entity.getId(), OPTIONS_LABEL);
            assertEquals(entity.getLabel(), OPTIONS_LABEL);
        }

        @Test(dependsOnMethods = "should_create_report_options")
        public void should_update_report_options() {
            final MultivaluedHashMap<String, String> map = new MultivaluedHashMap<String, String>();
            map.addAll("Country_multi_select", "USA");


            // When
            OperationResult<ReportOptionsSummary> result = session
                    .reportingService()
                    .report(reportUnitUri)
                    .reportOptions(OPTIONS_LABEL)
                    .update(map);

            final ReportOptionsSummary entity = result.getEntity();
            // Then
            assertEquals(result.getResponseStatus(), 200);
        }

        @Test(dependsOnMethods = "should_update_report_options")
        public void should_delete_report_options() {

            // When
            OperationResult result = session
                    .reportingService()
                    .report(reportUnitUri)
                    .reportOptions(OPTIONS_LABEL)
                    .delete();

            // Then
            assertEquals(result.getResponseStatus(), 200);
        }

        @Test(dependsOnMethods = "should_delete_report_options")
        public void should_create_report_options_with_list_report_parameters() {
            final List<ReportParameter> reportParameters = new LinkedList<ReportParameter>();
            reportParameters.add(new ReportParameter().setName("Country_multi_select").setValues(asList("Mexico")));
            reportParameters.add(new ReportParameter().setName("Cascading_state_multi_select").setValues(asList("Guerrero", "Sinaloa")));
            reportParameters.add(new ReportParameter().setName("Cascading_name_single_select").setValues(asList("Crow-Sims Construction Associates")));

            // When
            OperationResult<ReportOptionsSummary> result = session
                    .reportingService()
                    .report(reportUnitUri)
                    .reportOptions(reportParameters)
                    .label(OPTIONS_LABEL)
                    .create();

            final ReportOptionsSummary entity = result.getEntity();
            // Then
            assertEquals(result.getResponseStatus(), 200);
            assertNotNull(entity);
            assertEquals(entity.getId(), OPTIONS_LABEL);
            assertEquals(entity.getLabel(), OPTIONS_LABEL);
        }

        @Test(dependsOnMethods = "should_create_report_options_with_list_report_parameters")
        public void should_update_report_options_with_list_report_parameters() {
            final List<ReportParameter> reportParameters = new LinkedList<ReportParameter>();
            reportParameters.add(new ReportParameter().setName("Country_multi_select").setValues(asList("USA")));


            // When
            OperationResult<ReportOptionsSummary> result = session
                    .reportingService()
                    .report(reportUnitUri)
                    .reportOptions(OPTIONS_LABEL)
                    .update(reportParameters);

            final ReportOptionsSummary entity = result.getEntity();
            // Then
            assertEquals(result.getResponseStatus(), 200);
        }

        @Test(dependsOnMethods = "should_update_report_options_with_list_report_parameters")
        public void should_delete_report_options_with_list_report_parameters() {

            // When
            OperationResult result = session
                    .reportingService()
                    .report(reportUnitUri)
                    .reportOptions(OPTIONS_LABEL)
                    .delete();

            // Then
            assertEquals(result.getResponseStatus(), 200);
        }

        @AfterClass
        public void after() {
            session.logout();
        }


    }