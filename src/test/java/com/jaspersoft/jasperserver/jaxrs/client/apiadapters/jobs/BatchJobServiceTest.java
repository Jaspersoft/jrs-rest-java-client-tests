package com.jaspersoft.jasperserver.jaxrs.client.apiadapters.jobs;

import com.jaspersoft.jasperserver.dto.common.OutputFormat;
import com.jaspersoft.jasperserver.dto.job.ClientIntervalUnitType;
import com.jaspersoft.jasperserver.dto.job.ClientJobRepositoryDestination;
import com.jaspersoft.jasperserver.dto.job.ClientJobSimpleTrigger;
import com.jaspersoft.jasperserver.dto.job.ClientJobSource;
import com.jaspersoft.jasperserver.dto.job.ClientReportJob;
import com.jaspersoft.jasperserver.dto.job.wrappers.ClientJobSummariesListWrapper;
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

/**
 * @author Tetiana Iefimenko
 */
public class BatchJobServiceTest extends RestClientTestUtil{
    private String reportUri;
    private Long jobId;

    @BeforeClass
    public void before() {
        initClient();
        initSession();
        reportUri = "/organizations/organization_1/adhoc/topics/Cascading_multi_select_topic";
    }
    @Test
    public void should_search_jobs() {
        // Given
        List<Long> jobIds = new ArrayList<Long>();
        for (int i = 0; i < 10; i++) {
            OperationResult<ClientReportJob> result = session
                    .jobsService()
                    .job(prepareJob("reportOutput" + i))
                    .create();
            if (result.getEntity() != null) {
                jobIds.add(result.getEntity().getId());
            }
        }

        // When
        OperationResult<ClientJobSummariesListWrapper> result = session
                .jobsService()
                .jobs()
                .searchJobs();


        // Then
//        assertNotNull(job.getSource().getParameters());
//        assertEquals(new String[]{"USA"}, job.getSource().getParameters().get("Country_multi_select"));
//        assertEquals(new String[]{"Chin-Lovell Engineering Associates"}, job.getSource().getParameters().get("Cascading_name_single_select"));
//        assertEquals(new String[]{"DF", "Jalisco", "Mexico"}, job.getSource().getParameters().get("Cascading_state_multi_select"));
    }


    private ClientReportJob prepareJob(String outputName) {
        ClientReportJob job = new ClientReportJob();
        job.setLabel("New Job for ISS Report");
        job.setDescription("New Job for the report template: " + reportUri);
        job.setBaseOutputFilename(outputName);

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
