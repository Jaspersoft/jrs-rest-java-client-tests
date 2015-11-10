package com.jaspersoft.jasperserver.jaxrs.client.apiadapters.jobs;

import com.jaspersoft.jasperserver.jaxrs.client.RestClientTestUtil;
import com.jaspersoft.jasperserver.jaxrs.client.core.operationresult.OperationResult;
import com.jaspersoft.jasperserver.jaxrs.client.dto.jobs.IntervalUnitType;
import com.jaspersoft.jasperserver.jaxrs.client.dto.jobs.Job;
import com.jaspersoft.jasperserver.jaxrs.client.dto.jobs.JobSource;
import com.jaspersoft.jasperserver.jaxrs.client.dto.jobs.OutputFormat;
import com.jaspersoft.jasperserver.jaxrs.client.dto.jobs.RepositoryDestination;
import com.jaspersoft.jasperserver.jaxrs.client.dto.jobs.SimpleTrigger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * @author Tetiana Iefimenko
 */
public class JobServiceTest extends RestClientTestUtil{
    private String reportUri;
    @BeforeClass
    public void before() {
        initClient();
        initSession();
        reportUri = "/organizations/organization_1/adhoc/topics/Cascading_multi_select_topic";
    }

    @Test
    public void should_scheduled_report() {
        Job job = new Job();
        job.setLabel("New Job for ISS Report");
        job.setDescription("New Job for the report template: " + reportUri);

        JobSource jobSource = new JobSource();
        jobSource.setReportUnitURI(reportUri);

        Map<String, Object> parameterValues = new LinkedHashMap<String, Object>();
        parameterValues.put("Country_multi_select", new String[]{"USA"});
        parameterValues.put("Cascading_name_single_select", new String[]{"Chin-Lovell Engineering Associates"});
        parameterValues.put("Cascading_state_multi_select", new String[]{"DF", "Jalisco", "Mexico"});

        jobSource.setParameters(parameterValues);
        job.setSource(jobSource);

        Set<OutputFormat> outputFormats = new HashSet<OutputFormat>();
        outputFormats.add(OutputFormat.PDF);
        outputFormats.add(OutputFormat.XLSX);
        job.setOutputFormats(outputFormats);

        RepositoryDestination repositoryDestination = new RepositoryDestination();
        repositoryDestination.setSaveToRepository(true);

        repositoryDestination.setFolderURI("/organizations/organization_1/adhoc/topics");
        job.setRepositoryDestination(repositoryDestination);
        SimpleTrigger trigger = new SimpleTrigger();
        trigger.setStartType(SimpleTrigger.START_TYPE_NOW);
        trigger.setOccurrenceCount(1);
        trigger.setRecurrenceInterval(0);
        trigger.setRecurrenceIntervalUnit(IntervalUnitType.DAY);
        job.setTrigger(trigger);
        job.setBaseOutputFilename("Cascading_multi_select_topic" + System.currentTimeMillis());

        OperationResult<Job> result = session
                .jobsService()
                .scheduleReport(job);

        job = result.getEntity();

        assertNotNull(job.getSource().getParameters());
        assertEquals(parameterValues.get("Country_multi_select"), ((ArrayList) job.getSource().getParameters().get("Country_multi_select")).toArray());
        assertEquals(parameterValues.get("Cascading_name_single_select"), ((ArrayList) job.getSource().getParameters().get("Cascading_name_single_select")).toArray());
        assertEquals(parameterValues.get("Cascading_state_multi_select"), ((ArrayList) job.getSource().getParameters().get("Cascading_state_multi_select")).toArray());


    }

    @AfterClass
    public void after() {
        session.logout();
    }

}
