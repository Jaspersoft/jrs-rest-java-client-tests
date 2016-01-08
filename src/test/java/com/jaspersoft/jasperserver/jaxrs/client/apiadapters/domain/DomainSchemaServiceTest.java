package com.jaspersoft.jasperserver.jaxrs.client.apiadapters.domain;

import com.jaspersoft.jasperserver.dto.resources.domain.Schema;
import com.jaspersoft.jasperserver.jaxrs.client.RestClientTestUtil;
import com.jaspersoft.jasperserver.jaxrs.client.core.enums.MimeType;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * @author Tetiana Iefimenko
 */
public class DomainSchemaServiceTest extends RestClientTestUtil{

    @BeforeClass
    public void before() {
        initClient();
        initSession();
    }

    @Test
    public void should_return_domain_schema() {
        session.getStorage().getConfiguration().setAcceptMimeType(MimeType.XML);
        Schema schema = session.domainSchemaService()
                .domainSchema("/public/Simple_Domain_schema.xml")
                .retrieve()
                .getEntity();
        assertNotNull(schema);
        assertTrue(schema.getPresentation().size() > 0);
        assertTrue(schema.getResources().size() > 0);
    }


    @AfterClass
    public  void after() {
     session.logout();
    }
}
