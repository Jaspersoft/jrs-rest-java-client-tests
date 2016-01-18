package com.jaspersoft.jasperserver.jaxrs.client.apiadapters.domain;

import com.jaspersoft.jasperserver.dto.resources.ClientFile;
import com.jaspersoft.jasperserver.dto.resources.domain.ClientSchema;
import com.jaspersoft.jasperserver.dto.resources.domain.ResourceElement;
import com.jaspersoft.jasperserver.dto.resources.domain.ResourceGroupElement;
import com.jaspersoft.jasperserver.dto.resources.domain.Schema;
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
                .forDomain("/public/schema.xml")
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
                .forDomain("/public/schema.xml")
                .schema()
                .get()
                .getEntity();

        ClientSchema schemaAfterPut = session
                .domainService()
                .forDomain("/public/schema.xml")
                .schema()
                .update(schema)
                .getEntity();

        ClientSchema resultSchema = session
                .domainService()
                .forDomain("/public/schema.xml")
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

        if (!schema1.getPresentation().containsAll(schema2.getPresentation())) {
            return false;
        }

        if (!schema1.getResources().containsAll(schema2.getResources())) {
            return false;
        }

        if (!isEqual(schema1.getResources(), schema2.getResources())) {
            return false;
        }

        return true;
    }

    private boolean isEqual(List<? extends ResourceElement> groupElements1, List<? extends  ResourceElement> groupElements2) {
        if (groupElements1.size() != groupElements2.size()) {
            return false;
        }

        if (!(groupElements1.get(0) instanceof ResourceGroupElement)){
            return groupElements1.containsAll(groupElements2);
    }

        Comparator<ResourceElement> comparator = new Comparator<ResourceElement>() {
            public int compare(ResourceElement elem1, ResourceElement elem2) {
                return elem1.getName().compareTo(elem2.getName());
            }
        };
        Collections.sort(groupElements1, comparator);
        Collections.sort(groupElements2, comparator);
        List<? extends ResourceGroupElement> castedGroupElements1 = (List<? extends ResourceGroupElement>) groupElements1;
        List<? extends ResourceGroupElement> castedGroupElements2 = (List<? extends ResourceGroupElement>) groupElements2;
        boolean result = true;
        for (int i = 0; i < groupElements1.size(); i++) {

             if (!isEqual(castedGroupElements1.get(i).getElements(), castedGroupElements2.get(i).getElements())) {
                 result = false;
             }

        }

        return result;
    }

    @AfterClass
    public void after() {
        session.logout();
    }
}
