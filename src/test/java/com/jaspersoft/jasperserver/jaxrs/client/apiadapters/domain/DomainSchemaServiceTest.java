package com.jaspersoft.jasperserver.jaxrs.client.apiadapters.domain;

import com.jaspersoft.jasperserver.dto.resources.ClientFile;
import com.jaspersoft.jasperserver.dto.resources.domain.ClientSchema;
import com.jaspersoft.jasperserver.dto.resources.domain.PresentationGroupElement;
import com.jaspersoft.jasperserver.dto.resources.domain.ResourceGroupElement;
import com.jaspersoft.jasperserver.dto.resources.domain.Schema;
import com.jaspersoft.jasperserver.dto.resources.domain.SchemaElement;
import com.jaspersoft.jasperserver.jaxrs.client.RestClientTestUtil;
import com.jaspersoft.jasperserver.jaxrs.client.core.enums.MimeType;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.junit.Assert.assertTrue;
import static org.testng.Assert.assertNotNull;

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
                .upload("schema.xml")
                .getEntity();
        assertNotNull(schema);
    }


    @Test
    public void should_get_domain_schema() {
        session.getStorage().getConfiguration().setAcceptMimeType(MimeType.JSON);
        ClientSchema schema = session
                .domainService()
                .forDomain(SIMPLE_SCHEMA_URI)
                .schema()
                .get()
                .getEntity();
        assertNotNull(schema);
    }

    @Test
    public void should_put_domain_schema_and_get() {

        session.getStorage().getConfiguration().setAcceptMimeType(MimeType.JSON);
        ClientSchema schema = session
                .domainService()
                .forDomain(SIMPLE_SCHEMA_URI)
                .schema()
                .get()
                .getEntity();

        ClientSchema schemaAfterPut = session
                .domainService()
                .forDomain(SIMPLE_SCHEMA_URI)
                .schema()
                .update(schema)
                .getEntity();

        ClientSchema resultSchema = session
                .domainService()
                .forDomain(SIMPLE_SCHEMA_URI)
                .schema()
                .get()
                .getEntity();
        ResourceGroupElement groupElement0 = resultSchema.getResources().get(0);
        ResourceGroupElement groupElement1 = resultSchema.getResources().get(1);
        resultSchema.getResources().set(0, groupElement1);
        resultSchema.getResources().set(1, groupElement0);

        assertNotNull(schema);
        assertNotNull(schemaAfterPut);
        assertNotNull(resultSchema);
        assertTrue(isClientSchemasEqual(schema, resultSchema));
    }

    private boolean isClientSchemasEqual(ClientSchema clientSchema1, ClientSchema clientSchema2) {
        if (clientSchema1 == clientSchema2) {
            return true;
        }

        if (!clientSchema1.getPermissionMask().equals(clientSchema2.getPermissionMask())) {
            return false;
        }
        if (!clientSchema1.getCreationDate().equals(clientSchema2.getCreationDate())) {
            return false;
        }
        if (!clientSchema1.getLabel().equals(clientSchema2.getLabel())) {
            return false;
        }
        if (!clientSchema1.getDescription().equals(clientSchema2.getDescription())) {
            return false;
        }
        if (!clientSchema1.getUri().equals(clientSchema2.getUri())) {
            return false;
        }
        Schema schema1 = clientSchema1.getSchema();
        Schema schema2 = clientSchema2.getSchema();

        if (schema1.getResources().size() != schema2.getResources().size()) {
            return false;
        }

        if (schema1.getPresentation().size() != schema2.getPresentation().size()) {
            return false;
        }

        if (!equalGroupElements(schema1.getPresentation(), schema2.getPresentation())) {
            return false;
        }

        if (!equalGroupElements(schema1.getResources(), schema2.getResources())) {
            return false;
        }

        return true;
    }

    private boolean equalGroupElements(List<? extends SchemaElement> groupElements1, List<? extends SchemaElement> groupElements2) {
        if (groupElements1.size() != groupElements2.size()) {
            return false;
        }

        if ((!(groupElements1.get(0) instanceof ResourceGroupElement ) & (!(groupElements1.get(0) instanceof PresentationGroupElement)))) {
            return groupElements1.containsAll(groupElements2);
        }

        Comparator<SchemaElement> comparator = new Comparator<SchemaElement>() {
            public int compare(SchemaElement elem1, SchemaElement elem2) {
                return elem1.getName().compareTo(elem2.getName());
            }
        };
        Collections.sort(groupElements1, comparator);
        Collections.sort(groupElements2, comparator);
        boolean result = true;
        if (groupElements1.get(0) instanceof  ResourceGroupElement) {
            List<? extends ResourceGroupElement> castedGroupElements1 = (List<? extends ResourceGroupElement>) groupElements1;
            List<? extends ResourceGroupElement> castedGroupElements2 = (List<? extends ResourceGroupElement>) groupElements2;
            for (int i = 0; i < groupElements1.size(); i++) {
                if (!equalGroupElements(castedGroupElements1.get(i).getElements(), castedGroupElements2.get(i).getElements())) {
                    result = false;
                }
            }
        } else {
            List<? extends PresentationGroupElement> castedGroupElements1 = (List<? extends PresentationGroupElement>) groupElements1;
            List<? extends PresentationGroupElement> castedGroupElements2 = (List<? extends PresentationGroupElement>) groupElements2;
            for (int i = 0; i < groupElements1.size(); i++) {
                if (!equalGroupElements(castedGroupElements1.get(i).getElements(), castedGroupElements2.get(i).getElements())) {
                    result = false;
                }
            }
        }

        return result;
    }


    @AfterClass
    public void after() {
        session.logout();
    }
}
