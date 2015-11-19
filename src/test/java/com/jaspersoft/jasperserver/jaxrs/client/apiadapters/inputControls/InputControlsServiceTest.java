package com.jaspersoft.jasperserver.jaxrs.client.apiadapters.inputControls;

import com.jaspersoft.jasperserver.dto.reports.inputcontrols.ReportInputControlsListWrapper;
import com.jaspersoft.jasperserver.jaxrs.client.RestClientTestUtil;
import com.jaspersoft.jasperserver.jaxrs.client.core.operationresult.OperationResult;
import com.jaspersoft.jasperserver.jaxrs.client.dto.reports.inputcontrols.InputControlStateListWrapper;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;
import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.assertNull;

/**
 * @author Tetiana Iefimenko
 */
public class InputControlsServiceTest extends RestClientTestUtil {

    @BeforeClass
    public void before() {
        initClient();
        initSession();
    }

    @Test
    public void should_return_input_controls_structure_for_report() {
        OperationResult<ReportInputControlsListWrapper> operationResult = session
                .inputControlsService()
                .inputControls()
                .container("/organizations/organization_1/adhoc/topics/Cascading_multi_select_topic")
                .get();

        assertNotNull(operationResult);
        assertNotNull(operationResult.getEntity());
        assertTrue(operationResult.getEntity().getInputParameters().size() > 0);

    }

    @Test
    public void should_return_input_controls_structure_for_report_without_state() {
        OperationResult<ReportInputControlsListWrapper> operationResult = session
                .inputControlsService()
                .inputControls()
                .container("/organizations/organization_1/adhoc/topics/Cascading_multi_select_topic")
                .excludeState(true)
                .get();

        assertNotNull(operationResult);
        assertNotNull(operationResult.getEntity());
        assertTrue(operationResult.getEntity().getInputParameters().size() > 0);
        assertNull(operationResult.getEntity().getInputParameters().get(0).getState());

    }


    @Test
    public void should_return_input_controls_values_for_report() {
        OperationResult<InputControlStateListWrapper> operationResult = session
                .inputControlsService()
                .inputControls()
                .container("/organizations/organization_1/adhoc/topics/Cascading_multi_select_topic")
                .values()
                .get();

        assertNotNull(operationResult);
        assertNotNull(operationResult.getEntity());
        assertTrue(operationResult.getEntity().getInputControlStateList().size() > 0);

    }


    @Test
    public void should_update_input_controls_values_for_report() {
        OperationResult<InputControlStateListWrapper> operationResult = session
                .inputControlsService()
                .inputControls()
                .container("/organizations/organization_1/adhoc/topics/Cascading_multi_select_topic")
                .values()
                .parameter("Country_multi_select", "Mexico")
                .parameter("Cascading_state_multi_select", "Guerrero", "Sinaloa")
                .run();

        assertNotNull(operationResult);
        assertNotNull(operationResult.getEntity());
        assertTrue(operationResult.getEntity().getInputControlStateList().size() == 2);

    }

    @Test
    public void should_return_input_controls_values_for_report_with_fresh_data() {
        OperationResult<InputControlStateListWrapper> operationResult = session
                .inputControlsService()
                .inputControls()
                .container("/organizations/organization_1/adhoc/topics/Cascading_multi_select_topic")
                .values()
                .useCashedData(false)
                .get();

        assertNotNull(operationResult);
        assertNotNull(operationResult.getEntity());
        assertTrue(operationResult.getEntity().getInputControlStateList().size() > 0);

    }


    @AfterClass
    public void after() {
        session.logout();
    }
}
