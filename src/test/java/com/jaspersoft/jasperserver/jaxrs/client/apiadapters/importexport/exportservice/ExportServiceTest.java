package com.jaspersoft.jasperserver.jaxrs.client.apiadapters.importexport.exportservice;

import com.jaspersoft.jasperserver.dto.importexport.State;
import com.jaspersoft.jasperserver.jaxrs.client.RestClientTestUtil;
import com.jaspersoft.jasperserver.jaxrs.client.core.operationresult.OperationResult;
import java.io.InputStream;
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

    @BeforeMethod
    public void before() {
        initClient();
        initSession();
    }

    @Test
    public void should_export_resource() throws InterruptedException {
        OperationResult<State> stateOperationResult = session
                .exportService()
                .newTask()
                .uri("/temp/supermartDomain")
                .user("superuser")
                .role("ROLE_USER")
                .parameter(ExportParameter.EVERYTHING)
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
                        .fetch();

        assertNotNull(streamOperationResult);
    }

    @AfterMethod
    public void after() {
        session.logout();
    }

}
