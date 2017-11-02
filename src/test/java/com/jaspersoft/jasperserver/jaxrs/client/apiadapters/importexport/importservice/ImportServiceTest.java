package com.jaspersoft.jasperserver.jaxrs.client.apiadapters.importexport.importservice;

import com.jaspersoft.jasperserver.dto.importexport.ImportTask;
import com.jaspersoft.jasperserver.dto.importexport.State;
import com.jaspersoft.jasperserver.jaxrs.client.RestClientTestUtil;
import com.jaspersoft.jasperserver.jaxrs.client.core.operationresult.OperationResult;
import java.io.File;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
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
    public static final String PATH_TO_FILE = "d:\\workspaceIdea\\exportedDomains\\supermart.zip";
    private String taskId;

    @BeforeClass
    public void before() {
        initClient();
        initSession();

    }

    @Deprecated
    @Test(enabled = false)
    public void should_import_resource_to_server() throws InterruptedException {
        OperationResult<State> operationResult = session
                .importService()
                .newTask()
                .parameter(ImportParameter.INCLUDE_ACCESS_EVENTS, true)
                .create(new File(PATH_TO_FILE));

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
    public void should_create_import_task() throws InterruptedException {
        OperationResult<State> operationResult = session
                .importService()
                .newImport(PATH_TO_FILE)
                .parameter(ImportParameter.UPDATE, true)
                .create();

        State stateDto = operationResult.getEntity();
        taskId = stateDto.getId();

        assertEquals("inprogress", stateDto.getPhase());
    }

    @Test(dependsOnMethods = "should_create_import_task")
    public void should_get_import_task() throws InterruptedException {
        OperationResult<ImportTask> operationResult = session
                .importService()
                .task(taskId)
                .getTask();

        ImportTask importTask = operationResult.getEntity();

        assertNotNull(importTask);
    }

    @Test(dependsOnMethods = "should_create_import_task")
    public void should_get_state_import_task() throws InterruptedException {
        OperationResult<State> operationResult = session
                .importService()
                .task(taskId)
                .state();

        State state = operationResult.getEntity();

        assertNotNull(state);
    }

    @Test(dependsOnMethods = "should_get_state_import_task", enabled = false)
    public void should_restart_import_task() throws InterruptedException {

        State stateDto = session
                .importService()
                .task(taskId)
                .state().getEntity();

        while (stateDto.getPhase().equals(INPROGRESS_STATUS)) {
            stateDto = session
                    .importService()
                    .task(stateDto.getId())
                    .state().getEntity();
            Thread.sleep(100);
        }

        ImportTask newImportTask = new ImportTask().setBrokenDependencies("include");
        OperationResult<ImportTask> operationResult = session
                .importService()
                .task(taskId)
                .restartTask(newImportTask);

        while (stateDto.getPhase().equals(INPROGRESS_STATUS)) {
            stateDto = session
                    .importService()
                    .task(stateDto.getId())
                    .state().getEntity();
            Thread.sleep(100);
        }

        ImportTask importTask = operationResult.getEntity();

        assertNotNull(importTask);
    }

    @Test
    public void should_delete_import_task() throws InterruptedException {

        State state = session
                .importService()
                .newImport("d:\\workspaceIdea\\exportedDomains\\supermart.zip")
                .parameter(ImportParameter.UPDATE, true)
                .create().getEntity();
        String taskId = state.getId();

        OperationResult operationResult = session
                .importService()
                .task(taskId)
                .cancelTask();

        assertEquals(operationResult.getResponse().getStatus(), 204);
    }

    @Test
    public void should_create_import_task_multipart() throws InterruptedException {

        State state = session
                .importService()
                .newMultiPartImport(new File("d:\\workspaceIdea\\exportedDomains\\supermart.zip"))
                .parameter(ImportParameter.UPDATE, true)
                .create().getEntity();

        assertNotNull(state);
    }


    @Test(enabled = false)
    public void should_get_import_task_deprecated() throws InterruptedException {
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
    public void should_restart_import_task_deprecated() throws InterruptedException {
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

    @AfterClass
    public void after() {
        session.logout();
    }

}
