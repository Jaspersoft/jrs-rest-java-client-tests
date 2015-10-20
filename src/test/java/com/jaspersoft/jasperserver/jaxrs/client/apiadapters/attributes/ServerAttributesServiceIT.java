package com.jaspersoft.jasperserver.jaxrs.client.apiadapters.attributes;

import com.jaspersoft.jasperserver.dto.authority.ClientUserAttribute;
import com.jaspersoft.jasperserver.dto.authority.hypermedia.HypermediaAttribute;
import com.jaspersoft.jasperserver.dto.authority.hypermedia.HypermediaAttributesListWrapper;
import com.jaspersoft.jasperserver.jaxrs.client.RestClientTestUtil;
import com.jaspersoft.jasperserver.jaxrs.client.core.operationresult.NullEntityOperationResult;
import com.jaspersoft.jasperserver.jaxrs.client.core.operationresult.OperationResult;
import java.util.List;
import javax.ws.rs.core.Response;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static java.util.Arrays.asList;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertNull;
import static org.testng.AssertJUnit.assertTrue;

public class ServerAttributesServiceIT extends RestClientTestUtil {

    private HypermediaAttributesListWrapper serverAttributes;
    HypermediaAttribute serverAttribute;
    private String attrName;
    private String attrValue;

    @BeforeClass
    public void before() {
        initClient();
        initSession();
        serverAttribute = new HypermediaAttribute();
        serverAttributes = new HypermediaAttributesListWrapper();
        serverAttributes.setProfileAttributes(asList(
                new HypermediaAttribute(new ClientUserAttribute().setName("test_server_attribute_1").setValue("test_value_1")),
                new HypermediaAttribute(new ClientUserAttribute().setName("test_server_attribute_2").setValue("test_value_2"))));

        attrName = "test_server_attribute";
        attrValue = "test_value";
    }

    @Test
    public void should_create_single_attribute() {

        serverAttribute.setName(attrName);
        serverAttribute.setValue(attrValue);

        OperationResult<HypermediaAttribute> operationResult = session
                .attributesService()
                .attribute(serverAttribute.getName())
                .createOrUpdate(serverAttribute);

        HypermediaAttribute entity = operationResult.getEntity();

        assertNotNull(entity);
        assertEquals(operationResult.getResponse().getStatus(), Response.Status.CREATED.getStatusCode());
    }

    @Test(dependsOnMethods = "should_create_single_attribute")
    public void should_return_attribute() {
        HypermediaAttribute entity = session
                .attributesService()
                .attribute(attrName)
                .get()
                .getEntity();

        assertEquals(entity.getValue(), attrValue);
        assertNull(entity.getEmbedded());
    }

    @Test(dependsOnMethods = "should_return_attribute")
    public void should_return_attribute_with_permissions() {
        HypermediaAttribute entity = session
                .attributesService()
                .attribute(attrName)
                .setIncludePermissions(true)
                .get()
                .getEntity();

        assertEquals(entity.getValue(), attrValue);
        assertNotNull(entity.getEmbedded());
    }

    @Test(dependsOnMethods = "should_return_attribute_with_permissions")
    public void should_delete_attribute() {
        OperationResult<HypermediaAttribute> entity = session
                .attributesService()
                .attribute(attrName)
                .delete();

        assertNotNull(entity);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), entity.getResponse().getStatus());
    }


    @Test(dependsOnMethods = "should_delete_attribute")
    public void should_create_attributes() {

        OperationResult<HypermediaAttributesListWrapper> attributes = session
                .attributesService()
                .attributes(asList(serverAttributes.getProfileAttributes().get(0).getName(),
                        serverAttributes.getProfileAttributes().get(1).getName()))
                .createOrUpdate(serverAttributes);

        assertNotNull(attributes);
        assertEquals(Response.Status.OK.getStatusCode(), attributes.getResponse().getStatus());

    }

    @Test(dependsOnMethods = "should_create_attributes")
    public void should_update_attributes() {
        HypermediaAttributesListWrapper newServerAttributes = new HypermediaAttributesListWrapper(serverAttributes);
        newServerAttributes.getProfileAttributes().get(0).setValue("new_value");
        newServerAttributes.getProfileAttributes().get(1).setValue("new_value");
        newServerAttributes.getProfileAttributes().add((HypermediaAttribute) new HypermediaAttribute().setName("extra_attr_1").setValue("some_value_1"));
        newServerAttributes.getProfileAttributes().add((HypermediaAttribute) new HypermediaAttribute().setName("extra_attr_2").setValue("some_value_2"));
        newServerAttributes.getProfileAttributes().add((HypermediaAttribute) new HypermediaAttribute().setName("extra_attr_3").setValue("some_value_3"));
        OperationResult<HypermediaAttributesListWrapper> attributes = session
                .attributesService()
                .attributes(asList(serverAttributes.getProfileAttributes().get(0).getName(),
                        serverAttributes.getProfileAttributes().get(1).getName()))
                .createOrUpdate(newServerAttributes);

        assertNotNull(attributes);
        assertEquals(Response.Status.OK.getStatusCode(), attributes.getResponse().getStatus());
        assertTrue(attributes.getEntity().getProfileAttributes().size() == 2);

    }

    @Test(dependsOnMethods = "should_update_attributes")
    public void should_return_server_attributes() {
        List<HypermediaAttribute> attributes = session
                .attributesService()
                .allAttributes()
                .get()
                .getEntity()
                .getProfileAttributes();

        assertTrue(attributes.size() >= 2);
        assertTrue(attributes.get(0).getEmbedded() == null);
    }

    @Test(dependsOnMethods = "should_return_server_attributes")
    public void should_return_server_attributes_with_permissions() {
        List<HypermediaAttribute> attributes = session
                .attributesService()
                .allAttributes()
                .setIncludePermissions(true)
                .get()
                .getEntity()
                .getProfileAttributes();

        assertTrue(attributes.size() >= 2);
        assertTrue(attributes.get(0).getEmbedded() != null);
    }

    @Test(dependsOnMethods = "should_return_server_attributes_with_permissions")
    public void should_return_specified_server_attributes() {
        List<HypermediaAttribute> attributes = session
                .attributesService()
                .attributes(asList(serverAttributes.getProfileAttributes().get(0).getName(),
                        serverAttributes.getProfileAttributes().get(1).getName()))
                .get()
                .getEntity()
                .getProfileAttributes();

        assertTrue(attributes.size() >= 2);

    }

    @Test(dependsOnMethods = "should_return_specified_server_attributes")
    public void should_delete_specified_server_attributes() {
        OperationResult<HypermediaAttributesListWrapper> entity = session
                .attributesService()
                .attributes(serverAttributes.getProfileAttributes().get(0).getName())
                .delete();

        assertTrue(instanceOf(NullEntityOperationResult.class).matches(entity));
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), entity.getResponse().getStatus());
    }

    @Test(dependsOnMethods = "should_delete_specified_server_attributes")
    public void should_delete_server_attributes() {
        HypermediaAttributesListWrapper entity = session
                .attributesService()
                .allAttributes()
                .delete()
                .getEntity();

        assertNull(entity);
    }


    @AfterClass
    public void after() {
        session.logout();
        session = null;
        attrName = null;
        attrValue = null;
    }
}