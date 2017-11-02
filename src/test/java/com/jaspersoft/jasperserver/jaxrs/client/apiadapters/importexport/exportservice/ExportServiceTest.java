package com.jaspersoft.jasperserver.jaxrs.client.apiadapters.importexport.exportservice;

import com.jaspersoft.jasperserver.dto.importexport.ExportTask;
import com.jaspersoft.jasperserver.dto.importexport.State;
import com.jaspersoft.jasperserver.jaxrs.client.RestClientTestUtil;
import com.jaspersoft.jasperserver.jaxrs.client.core.operationresult.OperationResult;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.junit.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertTrue;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * <p/>
 * <p/>
 *
 * @author tetiana.iefimenko
 * @version $Id$
 * @see
 */
public class ExportServiceTest extends RestClientTestUtil {

    private static final String INPROGRESS_STATUS = "inprogress";
    public static final String FINISHED_STATUS = "finished";
    private final String TEST_URI = "/public/Samples/Reports/AllAccounts";
    private static String taskId;

    @BeforeClass
    public void before() {
        initClient();
        initSession();
    }

    @Deprecated
    @Test(enabled = false)
    public void should_export_resource_for_user_role() throws InterruptedException {
        OperationResult<State> stateOperationResult = session
                .exportService()
                .newTask()
                .uri(TEST_URI)
                .user("superuser")
                .role("ROLE_USER")
                .create();

        State stateDto = stateOperationResult.getEntity();

        OperationResult<State> operationResult = session
                .exportService()
                .task(stateDto.getId())
                .state();
        stateDto = operationResult.getEntity();

        OperationResult<InputStream> streamOperationResult = session
                .exportService()
                .task(stateDto.getId())
                .fetch(10000);

        assertNotNull(streamOperationResult);
        try {
            Files.copy(streamOperationResult.getEntity(), Paths.get("export.zip"), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void should_start_export() throws InterruptedException {
        ExportTask exportTask = new ExportTask().setUris(asList(TEST_URI)).setUsers(asList("superuser")).setRoles(asList("ROLE_USER"));
        OperationResult<State> stateOperationResult = session
                .exportService()
                .newTask(exportTask)
                .create();

        State stateDto = stateOperationResult.getEntity();
        assertEquals(stateDto.getPhase(), INPROGRESS_STATUS);
        taskId = stateDto.getId();
    }


    @Test(enabled = false)//method not allowed
    public void should_get_export_task() throws InterruptedException {
        OperationResult<ExportTask> stateOperationResult = session
                .exportService()
                .task(taskId)
                .getMetadata();

        ExportTask task = stateOperationResult.getEntity();
        assertNotNull(task);
        assertEquals(task.getUris().get(0), TEST_URI);
    }


    @Test(dependsOnMethods = "should_start_export")
    public void should_get_export_state() throws InterruptedException {
        OperationResult<State> stateOperationResult = session
                .exportService()
                .task(taskId)
                .state();

        State task = stateOperationResult.getEntity();
        assertNotNull(task);
        assertTrue(task.getPhase().equals(INPROGRESS_STATUS) || task.getPhase().equals(FINISHED_STATUS));
    }

    @Test(dependsOnMethods = "should_get_export_state")
    public void should_fetch_file() throws InterruptedException {
        String state = INPROGRESS_STATUS;
        while (!FINISHED_STATUS .equals(state)) {
            state = session.exportService().task(taskId).state().getEntity().getPhase();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {

            }
        }

        OperationResult<InputStream> operationResult = session
                .exportService()
                .task(taskId)
                .fetchToFile("export.zip");

        InputStream inputStream = operationResult.getEntity();
        assertNotNull(inputStream);
    }

    @Test
    public void should_delete_export_task() throws InterruptedException {
        ExportTask exportTask = new ExportTask().setUris(asList(TEST_URI)).setUsers(asList("superuser")).setRoles(asList("ROLE_USER"));
        OperationResult<State> stateOperationResult = session
                .exportService()
                .newTask(exportTask)
                .create();

        State stateDto = stateOperationResult.getEntity();
        assertEquals(stateDto.getPhase(), INPROGRESS_STATUS);
        taskId = stateDto.getId();
        OperationResult OperationResult = session
                .exportService()
                .task(taskId)
                .cancel();

        assertEquals(OperationResult.getResponse().getStatus(), 204);
    }

    @Deprecated
    @Test(enabled = false)
    public void should_export_resource_without_users_roles() throws InterruptedException {
        OperationResult<State> stateOperationResult = session
                .exportService()
                .newTask()
                .uri(TEST_URI)
                .create();

        State stateDto = stateOperationResult.getEntity();

        OperationResult<State> operationResult = session
                .exportService()
                .task(stateDto.getId())
                .state();
        stateDto = operationResult.getEntity();

        OperationResult<InputStream> streamOperationResult = session
                .exportService()
                .task(stateDto.getId())
                .fetch(10000);

        assertNotNull(streamOperationResult);
        try {
            Files.copy(streamOperationResult.getEntity(), Paths.get("export.zip"), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Deprecated
    @Test(enabled = false)
    public void should_export_resource_for_all_users_roles() throws InterruptedException {
        OperationResult<State> stateOperationResult = session
                .exportService()
                .newTask()
                .uri(TEST_URI)
                .allRoles()
                .allUsers()
                .create();

        State stateDto = stateOperationResult.getEntity();

        OperationResult<State> operationResult = session
                .exportService()
                .task(stateDto.getId())
                .state();
        stateDto = operationResult.getEntity();

        OperationResult<InputStream> streamOperationResult = session
                .exportService()
                .task(stateDto.getId())
                .fetch(10000);

        assertNotNull(streamOperationResult);
        try {
            Files.copy(streamOperationResult.getEntity(), Paths.get("export.zip"), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Deprecated
    @Test(enabled = false)
    public void should_export_resource_for_all_users_roles_scheduled_job() throws InterruptedException {
        OperationResult<State> stateOperationResult = session
                .exportService()
                .newTask()
                .uri(TEST_URI)
                .scheduledJob("/temp")
                .allRoles()
                .allUsers()
                .create();

        State stateDto = stateOperationResult.getEntity();

        OperationResult<State> operationResult = session
                .exportService()
                .task(stateDto.getId())
                .state();
        stateDto = operationResult.getEntity();

        OperationResult<InputStream> streamOperationResult = session
                .exportService()
                .task(stateDto.getId())
                .fetch(10000);

        assertNotNull(streamOperationResult);
        try {
            Files.copy(streamOperationResult.getEntity(), Paths.get("export.zip"), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @AfterClass
    public void after() {
        session.logout();
        taskId = null;
    }

}
