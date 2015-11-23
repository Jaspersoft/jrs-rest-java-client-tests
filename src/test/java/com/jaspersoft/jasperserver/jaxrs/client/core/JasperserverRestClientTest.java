package com.jaspersoft.jasperserver.jaxrs.client.core;


import com.jaspersoft.jasperserver.jaxrs.client.RestClientTestUtil;
import com.jaspersoft.jasperserver.jaxrs.client.core.enums.AuthenticationType;
import com.jaspersoft.jasperserver.jaxrs.client.core.exceptions.JSClientWebException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;

/**
 * @author Tetiana Iefimenko
 */
public class JasperserverRestClientTest extends RestClientTestUtil {

    @BeforeMethod
    public void before() {
        initClient();
    }

    @Test
    public void should_return_session_via_j_sucurity_check() {
        session = client.authenticate("superuser", "superuser");
        assertNotNull(session);
        assertNotNull(session.getStorage().getSessionId());
    }

    @Test (expectedExceptions = JSClientWebException.class)
    public void should_not_return_session_id_with_wrong_credentials_via_j_sucurity_check() {
        session = client.authenticate("superuser", "superuser1");

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

