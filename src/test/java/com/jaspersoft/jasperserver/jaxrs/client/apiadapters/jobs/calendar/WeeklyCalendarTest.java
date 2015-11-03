package com.jaspersoft.jasperserver.jaxrs.client.apiadapters.jobs.calendar;

import com.jaspersoft.jasperserver.jaxrs.client.RestClientTestUtil;
import com.jaspersoft.jasperserver.jaxrs.client.core.operationresult.OperationResult;
import com.jaspersoft.jasperserver.jaxrs.client.dto.jobs.ReportJobCalendar;
import com.jaspersoft.jasperserver.jaxrs.client.dto.jobs.calendars.WeeklyCalendar;
import java.util.TimeZone;
import javax.ws.rs.core.Response;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.assertNotNull;

/**
 * @author Tetiana Iefimenko
 */
public class WeeklyCalendarTest extends RestClientTestUtil {

    @BeforeClass
    public void before() {
        initClient();
        initSession();
    }

    @Test
    public void should_create_calendar() {
        // Given
        WeeklyCalendar calendar = new WeeklyCalendar();

        calendar.setDescription("ACalendarForTheTestPurposes");
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+03:00"));
        calendar.setExcludeDaysFlags(new boolean[]{
                true,   // Sunday
                false,  // Monday
                false,  // Tuesday
                false,  // Wednesday
                false,  // Thursday
                true,   // Friday
                true    // Saturday
        });
        // When
        OperationResult<ReportJobCalendar> operationResult = session
                .jobsService()
                .calendar("TestCalendar")
                .createNew(calendar);

        ReportJobCalendar jobCalendar = operationResult.getEntity();

        // Then
        assertEquals(Response.Status.OK.getStatusCode(), operationResult.getResponse().getStatus());
        assertEquals(calendar.getDescription(), jobCalendar.getDescription());
        assertEquals(calendar.getTimeZone(), jobCalendar.getTimeZone());
        assertNotNull(jobCalendar.getExcludeDaysFlags());
        assertTrue(jobCalendar.getExcludeDaysFlags()[0]);
        assertFalse(jobCalendar.getExcludeDaysFlags()[1]);
        assertFalse(jobCalendar.getExcludeDaysFlags()[2]);
        assertFalse(jobCalendar.getExcludeDaysFlags()[3]);
        assertFalse(jobCalendar.getExcludeDaysFlags()[4]);
        assertTrue(jobCalendar.getExcludeDaysFlags()[5]);
        assertTrue(jobCalendar.getExcludeDaysFlags()[6]);
    }

    @Test(dependsOnMethods = "should_create_calendar")
    public void should_delete_calendar() {
        // When
        OperationResult operationResult = session
                .jobsService()
                .calendar("TestCalendar")
                .delete();

        //Then
        assertEquals(operationResult.getResponse().getStatus(), Response.Status.OK.getStatusCode());
    }

    @AfterClass
    public void after() {
        session.logout();
    }
}
