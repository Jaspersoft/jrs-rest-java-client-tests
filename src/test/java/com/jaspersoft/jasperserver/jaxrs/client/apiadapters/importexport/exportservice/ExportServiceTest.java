package com.jaspersoft.jasperserver.jaxrs.client.apiadapters.importexport.exportservice;

import com.jaspersoft.jasperserver.dto.importexport.State;
import com.jaspersoft.jasperserver.jaxrs.client.RestClientTestUtil;
import com.jaspersoft.jasperserver.jaxrs.client.core.operationresult.OperationResult;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

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
    private final String TEST_URI = "/public/Samples/Reports/AllAccounts";

    @BeforeMethod
    public void before() {
        initClient();
        initSession();
    }

    @Test
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

    @Test
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

    @Test
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


    @AfterMethod
    public void after() {
        session.logout();
    }

}
