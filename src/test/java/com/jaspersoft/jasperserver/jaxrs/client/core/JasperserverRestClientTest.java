package com.jaspersoft.jasperserver.jaxrs.client.core;


import com.jaspersoft.jasperserver.jaxrs.client.RestClientTestUtil;
import com.jaspersoft.jasperserver.jaxrs.client.core.enums.AuthenticationType;
import com.jaspersoft.jasperserver.jaxrs.client.core.exceptions.JSClientWebException;
import java.util.TimeZone;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * @author Tetiana Iefimenko
 */
public class JasperserverRestClientTest extends RestClientTestUtil {
    private final String USER_NAME = "superuser";
    private final String PASSWORD = "superuser";
    private final String USER_TIME_ZONE = "Canada/Central";

    @BeforeMethod
    public void before() {
        initClient();
    }

    @Test
    public void should_return_session_via_j_sucurity_check() {
        session = client.authenticate(USER_NAME, PASSWORD);
        assertNotNull(session);
        assertNotNull(session.getStorage().getSessionId());
    }

    @Test
    public void should_return_session_via_j_sucurity_check_with_user_time_zone_as_object() {
        session = client.authenticate(USER_NAME, PASSWORD, TimeZone.getTimeZone(USER_TIME_ZONE));
        assertNotNull(session);
        assertNotNull(session.getStorage().getUserTimeZone());
        assertEquals(USER_TIME_ZONE, session.getStorage().getUserTimeZone().getID());
    }

    @Test (expectedExceptions = JSClientWebException.class)
    public void should_not_return_session_id_with_wrong_credentials_via_j_sucurity_check() {
        session = client.authenticate("user", "password");

    }

    @Test
    public void should_return_session_via_basic_login() {
        configuration.setAuthenticationType(AuthenticationType.BASIC);
        session = client.authenticate("jasperadmin", "jasperadmin");
        assertNotNull(session);
    }

    @AfterMethod
    public  void  after() {
        if (session != null)
            session.logout();
    }
}

