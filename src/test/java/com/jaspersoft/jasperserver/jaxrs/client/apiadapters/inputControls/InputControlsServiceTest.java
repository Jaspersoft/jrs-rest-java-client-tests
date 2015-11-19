package com.jaspersoft.jasperserver.jaxrs.client.apiadapters.inputControls;

import com.jaspersoft.jasperserver.dto.reports.inputcontrols.ReportInputControl;
import com.jaspersoft.jasperserver.dto.reports.inputcontrols.ReportInputControlsListWrapper;
import com.jaspersoft.jasperserver.jaxrs.client.RestClientTestUtil;
import com.jaspersoft.jasperserver.jaxrs.client.core.operationresult.OperationResult;
import com.jaspersoft.jasperserver.jaxrs.client.dto.reports.inputcontrols.InputControlStateListWrapper;
import java.util.List;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;
import static org.testng.AssertJUnit.assertEquals;
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
        // When
        OperationResult<ReportInputControlsListWrapper> operationResult = session
                .inputControlsService()
                .inputControls()
                .container("/organizations/organization_1/adhoc/topics/Cascading_multi_select_topic")
                .get();
        // Then
        assertNotNull(operationResult);
        assertNotNull(operationResult.getEntity());
        assertTrue(operationResult.getEntity().getInputParameters().size() > 0);

    }

    @Test(enabled = false)
    public void should_reorder_input_controls_structure_for_report() {
        // When
        OperationResult<ReportInputControlsListWrapper> operationResult = session
                .inputControlsService()
                .inputControls()
                .container("/organizations/organization_1/adhoc/topics/Cascading_multi_select_topic")
                .get();

        ReportInputControlsListWrapper result = operationResult.getEntity();

        List<ReportInputControl> inputParameters = result.getInputParameters();
        ReportInputControl ic0 = inputParameters.get(0);
        ReportInputControl ic1 = inputParameters.get(1);
        inputParameters.set(0, ic1);
        inputParameters.set(1,ic0);
        OperationResult<ReportInputControlsListWrapper> reorderedOperationResult = session
                .inputControlsService()
                .inputControls()
                .container("/organizations/organization_1/adhoc/topics/Cascading_multi_select_topic")
                .reorder(inputParameters);

        // Then
        assertNotNull(operationResult);
        assertNotNull(result);
        assertEquals(inputParameters.get(0), reorderedOperationResult.getEntity().getInputParameters().get(0));

    }

    @Test(dependsOnMethods = "should_return_input_controls_structure_for_report")
    public void should_return_input_controls_structure_for_report_without_state() {
        // When
        OperationResult<ReportInputControlsListWrapper> operationResult = session
                .inputControlsService()
                .inputControls()
                .container("/organizations/organization_1/adhoc/topics/Cascading_multi_select_topic")
                .excludeState(true)
                .get();
        // Then
        assertNotNull(operationResult);
        assertNotNull(operationResult.getEntity());
        assertTrue(operationResult.getEntity().getInputParameters().size() > 0);
        assertNull(operationResult.getEntity().getInputParameters().get(0).getState());
    }

    @Test(dependsOnMethods = "should_return_input_controls_structure_for_report_without_state")
    public void should_return_input_controls_values_for_report() {
        // When
        OperationResult<InputControlStateListWrapper> operationResult = session
                .inputControlsService()
                .inputControls()
                .container("/organizations/organization_1/adhoc/topics/Cascading_multi_select_topic")
                .values()
                .get();
        // Then
        assertNotNull(operationResult);
        assertNotNull(operationResult.getEntity());
        assertTrue(operationResult.getEntity().getInputControlStateList().size() > 0);

    }

    @Test//(dependsOnMethods = "should_return_input_controls_values_for_report")
    public void should_update_input_controls_values_for_report() {
        // When
        OperationResult<InputControlStateListWrapper> operationResult = session
                .inputControlsService()
                .inputControls()
                .container("/organizations/organization_1/adhoc/topics/Cascading_multi_select_topic")
                .values()
                .parameter("Country_multi_select", "Mexico")
                .parameter("Cascading_state_multi_select", "Guerrero", "Sinaloa")
                .run();
        // Then
        assertNotNull(operationResult);
        assertNotNull(operationResult.getEntity());
        assertTrue(operationResult.getEntity().getInputControlStateList().size() == 2);

    }

    @Test//(dependsOnMethods = "should_update_input_controls_values_for_report")
    public void should_update_input_controls_values_for_report_and_return_full_structure() {
        // When
        OperationResult<InputControlStateListWrapper> operationResult = session
                .inputControlsService()
                .inputControls()
                .container("/organizations/organization_1/adhoc/topics/Cascading_multi_select_topic")
                .values()
                .parameter("Country_multi_select", "USA")
                .parameter("Cascading_state_multi_select", "CA", "OR", "WA")
                .includeFullStructure(true)
                .run();
        // Then
        assertNotNull(operationResult);
        assertNotNull(operationResult.getEntity());
        assertTrue(operationResult.getEntity().getInputControlStateList().size() > 2);

    }

    @Test(dependsOnMethods = "should_update_input_controls_values_for_report_and_return_full_structure")
    public void should_return_input_controls_values_for_report_with_fresh_data() {
        // When
        OperationResult<InputControlStateListWrapper> operationResult = session
                .inputControlsService()
                .inputControls()
                .container("/organizations/organization_1/adhoc/topics/Cascading_multi_select_topic")
                .values()
                .useCashedData(false)
                .get();
        // Then
        assertNotNull(operationResult);
        assertNotNull(operationResult.getEntity());
        assertTrue(operationResult.getEntity().getInputControlStateList().size() > 0);

    }

    @AfterClass
    public void after() {
        session.logout();
    }
}
