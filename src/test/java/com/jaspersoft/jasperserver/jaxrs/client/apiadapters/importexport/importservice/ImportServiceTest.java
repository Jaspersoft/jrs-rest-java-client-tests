package com.jaspersoft.jasperserver.jaxrs.client.apiadapters.importexport.importservice;

import com.jaspersoft.jasperserver.dto.importexport.ImportTask;
import com.jaspersoft.jasperserver.dto.importexport.State;
import com.jaspersoft.jasperserver.jaxrs.client.RestClientTestUtil;
import com.jaspersoft.jasperserver.jaxrs.client.core.operationresult.OperationResult;
import java.io.File;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;

/**
 * <p/>
 * <p/>
 *
 * @author tetiana.iefimenko
 * @version $Id$
 * @see
 */
public class ImportServiceTest extends RestClientTestUtil {

    private static final String INPROGRESS_STATUS = "inprogress";

    @BeforeMethod
    public void before() {
        initClient();
        initSession();

    }

    @Test
    public void should_import_resource_to_server() throws InterruptedException {
        OperationResult<State> operationResult = session
                .importService()
                .newTask()
                .parameter(ImportParameter.INCLUDE_ACCESS_EVENTS, true)
                .create(new File("d:\\workspaceIdea\\exportedDomains\\supermart.zip"));

        State stateDto = operationResult.getEntity();

        while (stateDto.getPhase().equals(INPROGRESS_STATUS)) {
            stateDto = session
                    .importService()
                    .task(stateDto.getId())
                    .state().getEntity();
            Thread.sleep(100);
        }
        assertEquals("finished", stateDto.getPhase());
    }


    @Test
    public void should_get_import_task() throws InterruptedException {
        ImportService importService = session
                .importService();
        OperationResult<State> operationResult = importService
                .newTask()
                .parameter(ImportParameter.INCLUDE_ACCESS_EVENTS, true)
                .parameter(ImportParameter.UPDATE, true)
                .create(new File("d:\\workspaceIdea\\exportedDomains\\supermart.zip"));
        State state = operationResult.getEntity();

        ImportTask task = importService.task(state.getId()).getTask().getEntity();

        assertNotNull(task);
    }


    @Test(enabled = false)
    public void should_restart_import_task() throws InterruptedException {
        ImportService importService = session
                .importService();
        OperationResult<State> operationResult = importService
                .newTask()
                .parameter(ImportParameter.INCLUDE_ACCESS_EVENTS, true)
                .parameter(ImportParameter.UPDATE, true)
                .create(new File("d:\\workspaceIdea\\exportedDomains\\supermart.zip"));
        State state = operationResult.getEntity();

        ImportTask task = importService.task(state.getId()).getTask().getEntity();

        ImportTask updatedTask = importService.
                task(state.getId()).
                restartTask(task.setBrokenDependencies(BrokenDependenciesParameter.INCLUDE.getValueName())).getEntity();

        assertNotNull(updatedTask);
        assertEquals("include", updatedTask.getBrokenDependencies());
    }

    @AfterMethod
    public void after() {
        session.logout();
    }

}
