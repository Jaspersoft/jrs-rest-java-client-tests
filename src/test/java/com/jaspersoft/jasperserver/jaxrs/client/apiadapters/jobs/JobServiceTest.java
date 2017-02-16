package com.jaspersoft.jasperserver.jaxrs.client.apiadapters.jobs;

import com.jaspersoft.jasperserver.dto.common.OutputFormat;
import com.jaspersoft.jasperserver.dto.job.ClientIntervalUnitType;
import com.jaspersoft.jasperserver.dto.job.ClientJobRepositoryDestination;
import com.jaspersoft.jasperserver.dto.job.ClientJobSimpleTrigger;
import com.jaspersoft.jasperserver.dto.job.ClientJobSource;
import com.jaspersoft.jasperserver.dto.job.ClientReportJob;
import com.jaspersoft.jasperserver.jaxrs.client.RestClientTestUtil;
import com.jaspersoft.jasperserver.jaxrs.client.core.operationresult.OperationResult;
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
        // Given
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
        trigger.setStartType(ClientJobSimpleTrigger.START_TYPE_NOW);
        trigger.setOccurrenceCount(1);
        trigger.setRecurrenceInterval(0);
        trigger.setRecurrenceIntervalUnit(ClientIntervalUnitType.DAY);
        job.setTrigger(trigger);
        job.setBaseOutputFilename("Cascading_multi_select_topic" + System.currentTimeMillis());

        // When
        OperationResult<ClientReportJob> result = session
                .jobsService()
                .scheduleReport(job);

        job = result.getEntity();

        // Then
        assertNotNull(job.getSource().getParameters());
        assertEquals(parameterValues.get("Country_multi_select"), job.getSource().getParameters().get("Country_multi_select"));
        assertEquals(parameterValues.get("Cascading_name_single_select"), job.getSource().getParameters().get("Cascading_name_single_select"));
        assertEquals(parameterValues.get("Cascading_state_multi_select"), job.getSource().getParameters().get("Cascading_state_multi_select"));


    }

    @AfterClass
    public void after() {
        session.logout();
    }

}
