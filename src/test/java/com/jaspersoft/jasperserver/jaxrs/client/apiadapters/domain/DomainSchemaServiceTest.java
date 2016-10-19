package com.jaspersoft.jasperserver.jaxrs.client.apiadapters.domain;

import com.jaspersoft.jasperserver.dto.resources.ClientFile;
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
public class DomainSchemaServiceTest extends RestClientTestUtil {


    private final String SCHEMA_URI = "/public/schema.xml";
    private final String SIMPLE_SCHEMA_URI = "/public/Simple_Domain_schema";


    @BeforeClass
    public void before() {
        initClient();
        initSession();
    }

    /*Enable the test if schema was not uploaded yet*/
    @Test(enabled = false)
    public void should_upload_domain_schema_to_server() {
        ClientFile schema = session
                .domainService()
                .forDomain("/public")
                .schema()
                .upload("schema_tmp.xml", "schema_tmp.xml", "Description")
                .getEntity();
        assertNotNull(schema);
    }


    @Test
    public void should_get_domain_schema() {
        session.getStorage().getConfiguration().setAcceptMimeType(MimeType.JSON);
        Schema schema = session
                .domainService()
                .forDomain(SCHEMA_URI)
                .schema()
                .get()
                .getEntity();
        assertNotNull(schema);
    }

    @Test
    public void should_put_domain_schema_and_get() {

        session.getStorage().getConfiguration().setAcceptMimeType(MimeType.JSON);
        Schema schema = session
                .domainService()
                .forDomain(SIMPLE_SCHEMA_URI)
                .schema()
                .get()
                .getEntity();

        Schema schemaAfterPut = session
                .domainService()
                .forDomain(SIMPLE_SCHEMA_URI)
                .schema()
                .update(schema)
                .getEntity();

        Schema resultSchema = session
                .domainService()
                .forDomain(SIMPLE_SCHEMA_URI)
                .schema()
                .get()
                .getEntity();


        assertNotNull(schema);
        assertNotNull(schemaAfterPut);
        assertNotNull(resultSchema);
        assertTrue(schema.equals(resultSchema));
        assertTrue(schema.hashCode() == resultSchema.hashCode());
    }


    @AfterClass
    public void after() {
        session.logout();
    }
}
