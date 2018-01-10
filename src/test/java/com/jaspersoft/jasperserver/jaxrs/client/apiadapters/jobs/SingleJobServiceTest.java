package com.jaspersoft.jasperserver.jaxrs.client.apiadapters.jobs;

import com.jaspersoft.jasperserver.dto.common.OutputFormat;
import com.jaspersoft.jasperserver.dto.job.ClientIntervalUnitType;
import com.jaspersoft.jasperserver.dto.job.ClientJobRepositoryDestination;
import com.jaspersoft.jasperserver.dto.job.ClientJobSimpleTrigger;
import com.jaspersoft.jasperserver.dto.job.ClientJobSource;
import com.jaspersoft.jasperserver.dto.job.ClientReportJob;
import com.jaspersoft.jasperserver.dto.job.wrappers.ClientJobIdListWrapper;
import com.jaspersoft.jasperserver.jaxrs.client.RestClientTestUtil;
import com.jaspersoft.jasperserver.jaxrs.client.core.operationresult.OperationResult;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.junit.Assert.assertNull;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * @author Tetiana Iefimenko
 */
public class SingleJobServiceTest extends RestClientTestUtil{
    private String reportUri;
    private Long jobId;

    @BeforeClass
    public void before() {
        initClient();
        initSession();
        reportUri = "/organizations/organization_1/adhoc/topics/Cascading_multi_select_topic";
    }
    @Test
    public void should_create_job() {
        // Given
        ClientReportJob job = prepareJob();

        // When
        OperationResult<ClientReportJob> result = session
                .jobsService()
                .job(job)
                .create();

        job = result.getEntity();
        jobId = job.getId();

        // Then
        assertNotNull(job.getSource().getParameters());
        assertEquals(new String[]{"USA"}, job.getSource().getParameters().get("Country_multi_select"));
        assertEquals(new String[]{"Chin-Lovell Engineering Associates"}, job.getSource().getParameters().get("Cascading_name_single_select"));
        assertEquals(new String[]{"DF", "Jalisco", "Mexico"}, job.getSource().getParameters().get("Cascading_state_multi_select"));
    }

    @Test(dependsOnMethods = "should_create_job")
    public void should_get_job() {
        // Given
        ClientReportJob job = prepareJob();

        // When
        OperationResult<ClientReportJob> result = session
                .jobsService()
                .job(jobId)
                .getJob();

        job = result.getEntity();

        // Then
        assertNotNull(job.getSource().getParameters());
        assertEquals(new String[]{"USA"}, job.getSource().getParameters().get("Country_multi_select"));
        assertEquals(new String[]{"Chin-Lovell Engineering Associates"}, job.getSource().getParameters().get("Cascading_name_single_select"));
        assertEquals(new String[]{"DF", "Jalisco", "Mexico"}, job.getSource().getParameters().get("Cascading_state_multi_select"));
    }

    @Test(dependsOnMethods = "should_get_job")
    public void should_update_job() {
        // Given
        ClientReportJob job = prepareJob();
        final String description = "new job test description";
        final ClientReportJob newJob = job.setDescription(description);

        // When
        OperationResult<ClientReportJob> result = session
                .jobsService()
                .job(jobId)
                .update(newJob);

        job = result.getEntity();

        // Then
        assertNotNull(job);
        assertEquals(job.getDescription(), description);
    }

    @Test(dependsOnMethods = "should_update_job")
    public void should_delete_job() {
        // Given

        // When
        OperationResult result = session
                .jobsService()
                .job(jobId)
                .delete();


        // Then
        assertNull(result.getEntity());
    }

    @Deprecated
    @Test(enabled = false)
    public void should_scheduled_report() {
        // Given
        ClientReportJob job = prepareJob();

        // When
        OperationResult<ClientReportJob> result = session
                .jobsService()
                .scheduleReport(job);

        job = result.getEntity();

        // Then
        assertNotNull(job.getSource().getParameters());
        assertEquals(new String[]{"USA"}, job.getSource().getParameters().get("Country_multi_select"));
        assertEquals(new String[]{"Chin-Lovell Engineering Associates"}, job.getSource().getParameters().get("Cascading_name_single_select"));
        assertEquals(new String[]{"DF", "Jalisco", "Mexico"}, job.getSource().getParameters().get("Cascading_state_multi_select"));
    }
    @Deprecated
    @Test(enabled = false)
    public void should_scheduled_and_delete_reports() {
        // Given
        ClientReportJob job = null;
        List<String> ids = new ArrayList<String>(3);

        for (int i = 0; i < 3; i++) {
            job = prepareJob();
            OperationResult<ClientReportJob> scheduleResult = session
                    .jobsService()
                    .scheduleReport(job);

            if (scheduleResult != null && scheduleResult.getEntity() != null) {
                ids.add(String.valueOf(scheduleResult.getEntity().getId()));
            }
        }

        // When
        OperationResult<ClientJobIdListWrapper> deleteResult = session
                .jobsService()
                .jobs()
                .parameters(JobsParameter.JOB_ID, ids.toArray(new String[ids.size()]))
                .delete();
        // Then
        assertNotNull(deleteResult.getEntity());
        assertEquals(3, ids.size());
        assertEquals(200, deleteResult.getResponse().getStatus());

    }

    private ClientReportJob prepareJob() {
        ClientReportJob job = new ClientReportJob();
        job.setLabel("New Job for ISS Report");
        job.setDescription("New Job for the report template: " + reportUri);

        ClientJobSource jobSource = new ClientJobSource();
        jobSource.setReportUnitURI(reportUri);

        Map<String, String[]> parameterValues = new LinkedHashMap<String, String[]>();
        parameterValues.put("Country_multi_select", new String[]{"USA"});
        parameterValues.put("Cascading_name_single_select", new String[]{"Chin-Lovell Engineering Associates"});
        parameterValues.put("Cascading_state_multi_select", new String[]{"DF", "Jalisco", "Mexico"});

        jobSource.setParameters(parameterValues);
        job.setSource(jobSource);

        Set<OutputFormat> outputFormats = new HashSet<OutputFormat>();
        outputFormats.add(OutputFormat.PDF);
        outputFormats.add(OutputFormat.XLSX);
        job.setOutputFormats(outputFormats);

        ClientJobRepositoryDestination repositoryDestination = new ClientJobRepositoryDestination();
        repositoryDestination.setSaveToRepository(true);

        repositoryDestination.setFolderURI("/organizations/organization_1/adhoc/topics");
        job.setRepositoryDestination(repositoryDestination);
        ClientJobSimpleTrigger trigger = new ClientJobSimpleTrigger();

        trigger.setStartType(ClientJobSimpleTrigger.START_TYPE_SCHEDULE);
        trigger.setStartDate(new GregorianCalendar(2018,1,1,0,0,0).getTime());

        trigger.setOccurrenceCount(1);
        trigger.setRecurrenceInterval(0);
        trigger.setRecurrenceIntervalUnit(ClientIntervalUnitType.DAY);
        job.setTrigger(trigger);
        job.setBaseOutputFilename("Cascading_multi_select_topic" + Math.random());
        return job;
    }

    @AfterClass
    public void after() {
        session.logout();
    }

}
