package com.jaspersoft.jasperserver.jaxrs.client.apiadapters.attributes;

import com.jaspersoft.jasperserver.dto.authority.hypermedia.HypermediaAttribute;
import com.jaspersoft.jasperserver.dto.authority.hypermedia.HypermediaAttributeEmbeddedContainer;
import com.jaspersoft.jasperserver.dto.authority.hypermedia.HypermediaAttributesListWrapper;
import com.jaspersoft.jasperserver.dto.permissions.RepositoryPermission;
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
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

public class ServerAttributesServiceTest extends RestClientTestUtil {

    private HypermediaAttributesListWrapper serverAttributes;
    HypermediaAttribute serverAttribute;
    private String attrName;
    private String attrValue;

    @BeforeClass
    public void before() {
        initClient();
        initSession();
        //prepare single attribute
        attrName = "test_server_attribute";
        attrValue = "test_value";
        serverAttribute = new HypermediaAttribute();
        serverAttribute.setName(attrName).setValue(attrValue).setDescription("Server attribute description");
        serverAttribute.setPermissionMask(1).setSecure(false).setInherited(false).setHolder("tenant:/");
        HypermediaAttributeEmbeddedContainer container = new HypermediaAttributeEmbeddedContainer();
        container.setRepositoryPermissions(
                asList(new RepositoryPermission()
                        .setMask(32)
                        .setRecipient("role:/ROLE_ADMINISTRATOR")
                        .setUri("attr:/attributes/" + attrName)));
        serverAttribute.setEmbedded(container);
        // prepare list of attributes
        serverAttributes = new HypermediaAttributesListWrapper();
        serverAttributes.setProfileAttributes(asList(
                new HypermediaAttribute(),
                new HypermediaAttribute()));
        // first element of list
        serverAttributes.getProfileAttributes().get(0).setName(attrName + "_1").setValue(attrValue).setDescription("Server attribute description");
        serverAttributes.getProfileAttributes().get(0).setPermissionMask(1).setSecure(false).setInherited(false).setHolder("tenant:/");
        HypermediaAttributeEmbeddedContainer container0 = new HypermediaAttributeEmbeddedContainer();
        container0.setRepositoryPermissions(
                asList(new RepositoryPermission()
                        .setMask(32)
                        .setRecipient("role:/ROLE_ADMINISTRATOR")
                        .setUri("attr:/attributes/" + attrName + "_1")));
        serverAttributes.getProfileAttributes().get(0).setEmbedded(container0);
        // second element of list
        serverAttributes.getProfileAttributes().get(1).setName(attrName + "_2").setValue(attrValue).setDescription("Server attribute description");
        serverAttributes.getProfileAttributes().get(1).setPermissionMask(1).setSecure(false).setInherited(false).setHolder("tenant:/");
        HypermediaAttributeEmbeddedContainer container1 = new HypermediaAttributeEmbeddedContainer();
        container1.setRepositoryPermissions(
                asList(new RepositoryPermission()
                        .setMask(2)
                        .setRecipient("role:/ROLE_ADMINISTRATOR")
                        .setUri("attr:/attributes/" + attrName + "_2")));
        serverAttributes.getProfileAttributes().get(1).setEmbedded(container1);
    }
    /**
     * For JPS v6.2.1 create or update permissions for single attribute doesn't work
     * */
    @Test
    public void should_create_single_attribute_with_permissions() {
        // Given
        serverAttribute.setName(attrName);
        serverAttribute.setValue(attrValue);
        // When
        OperationResult<HypermediaAttribute> operationResult = session
                .attributesService()
                .attribute(serverAttribute.getName())
                .setIncludePermissions(true)
                .createOrUpdate(serverAttribute);

        HypermediaAttribute entity = operationResult.getEntity();
        // Then
        assertNotNull(entity);
        assertEquals(operationResult.getResponse().getStatus(), Response.Status.CREATED.getStatusCode());
        assertEquals(new Integer(1), entity.getEmbedded().getRepositoryPermissions().get(0).getMask());
    }

    @Test(dependsOnMethods = "should_create_single_attribute_with_permissions")
    public void should_create_attributes_with_permissions() {
        // When
        OperationResult<HypermediaAttributesListWrapper> attributes = session
                .attributesService()
                .attributes(asList(serverAttributes.getProfileAttributes().get(0).getName(),
                        serverAttributes.getProfileAttributes().get(1).getName()))
                .setIncludePermissions(true)
                .createOrUpdate(serverAttributes);

        List<HypermediaAttribute> profileAttributes = attributes.getEntity().getProfileAttributes();
        // Then
        assertNotNull(attributes);
        assertNotNull(attributes.getEntity());
        assertEquals(Response.Status.OK.getStatusCode(), attributes.getResponse().getStatus());
        assertEquals(new Integer(32), profileAttributes.get(0).getEmbedded().getRepositoryPermissions().get(0).getMask());
        assertEquals(new Integer(2), profileAttributes.get(1).getEmbedded().getRepositoryPermissions().get(0).getMask());
    }

    @Test(dependsOnMethods = "should_create_attributes_with_permissions")
    public void should_return_attribute() {
        // When
        HypermediaAttribute entity = session
                .attributesService()
                .attribute(attrName)
                .get()
                .getEntity();
        // Then
        assertEquals(entity, serverAttribute);
        assertEquals(entity.getValue(), attrValue);
        assertNull(entity.getEmbedded());
    }

    @Test(dependsOnMethods = "should_return_attribute")
    public void should_return_attribute_with_permissions() {
        // When
        HypermediaAttribute entity = session
                .attributesService()
                .attribute(attrName)
                .setIncludePermissions(true)
                .get()
                .getEntity();
        // Them
        assertEquals(entity.getValue(), attrValue);
        assertNotNull(entity.getEmbedded());
    }


    @Test(dependsOnMethods = "should_return_attribute_with_permissions")
    public void should_return_server_attributes() {
        // When
        List<HypermediaAttribute> attributes = session
                .attributesService()
                .allAttributes()
                .get()
                .getEntity()
                .getProfileAttributes();
        // Then
        assertTrue(attributes.size() >= 2);
        assertTrue(attributes.get(0).getEmbedded() == null);
    }

    @Test(dependsOnMethods = "should_return_server_attributes")
    public void should_update_attributes() {
        // Given
        HypermediaAttributesListWrapper newServerAttributes = new HypermediaAttributesListWrapper(serverAttributes);
        newServerAttributes.getProfileAttributes().get(0).setValue("new_value");
        newServerAttributes.getProfileAttributes().get(1).setValue("new_value");
        newServerAttributes.getProfileAttributes().add((HypermediaAttribute) new HypermediaAttribute().setName("extra_attr_1").setValue("some_value_1"));
        newServerAttributes.getProfileAttributes().add((HypermediaAttribute) new HypermediaAttribute().setName("extra_attr_2").setValue("some_value_2"));
        newServerAttributes.getProfileAttributes().add((HypermediaAttribute) new HypermediaAttribute().setName("extra_attr_3").setValue("some_value_3"));

        // When
        OperationResult<HypermediaAttributesListWrapper> attributes = session
                .attributesService()
                .attributes(asList(serverAttributes.getProfileAttributes().get(0).getName(),
                        serverAttributes.getProfileAttributes().get(1).getName()))
                .createOrUpdate(newServerAttributes);

        // Then
        assertNotNull(attributes);
        assertEquals(Response.Status.OK.getStatusCode(), attributes.getResponse().getStatus());
        assertTrue(attributes.getEntity().getProfileAttributes().size() == 2);
    }

    @Test(dependsOnMethods = "should_update_attributes")
    public void should_return_server_attributes_with_permissions() {
        // When
        List<HypermediaAttribute> attributes = session
                .attributesService()
                .allAttributes()
                .setIncludePermissions(true)
                .get()
                .getEntity()
                .getProfileAttributes();
        // Then
        assertTrue(attributes.size() >= 2);
        assertTrue(attributes.get(0).getEmbedded() != null);
    }

    @Test(dependsOnMethods = "should_return_server_attributes_with_permissions")
    public void should_delete_attribute() {
        // When
        OperationResult<HypermediaAttribute> entity = session
                .attributesService()
                .attribute(attrName)
                .delete();
        // Then
        assertNotNull(entity);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), entity.getResponse().getStatus());
    }

    @Test(dependsOnMethods = "should_delete_attribute")
    public void should_return_specified_server_attributes() {
        // When
        List<HypermediaAttribute> attributes = session
                .attributesService()
                .attributes(asList(serverAttributes.getProfileAttributes().get(0).getName(),
                        serverAttributes.getProfileAttributes().get(1).getName()))
                .get()
                .getEntity()
                .getProfileAttributes();
        // Then

        assertTrue(attributes.size() == 2);
    }

    @Test(dependsOnMethods = "should_return_specified_server_attributes")
    public void should_delete_specified_server_attributes() {
        // When
        OperationResult<HypermediaAttributesListWrapper> entity = session
                .attributesService()
                .attributes(serverAttributes.getProfileAttributes().get(0).getName())
                .delete();
        // Then
        assertTrue(instanceOf(NullEntityOperationResult.class).matches(entity));
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), entity.getResponse().getStatus());
    }

    @Test(dependsOnMethods = "should_delete_specified_server_attributes")
    public void should_replace_all_attributes() {
        // Given
        serverAttributes.getProfileAttributes().add(serverAttribute);
        // When
        OperationResult<HypermediaAttributesListWrapper> operationResult = session
                .attributesService()
                .allAttributes()
                .createOrUpdate(serverAttributes);
        HypermediaAttributesListWrapper attributes = operationResult.getEntity();

        // Then
        assertNotNull(attributes);
        assertEquals(Response.Status.OK.getStatusCode(), operationResult.getResponse().getStatus());
        assertEquals(serverAttributes, attributes);
    }

    @Test(dependsOnMethods = "should_replace_all_attributes")
    public void should_delete_server_attributes() {
        // When
        OperationResult<HypermediaAttributesListWrapper> operationResult = session
                .attributesService()
                .allAttributes()
                .delete();

        HypermediaAttributesListWrapper entity  = operationResult.getEntity();
        // Then
        assertNull(entity);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), operationResult.getResponse().getStatus());
    }


    @AfterClass
    public void after() {
        session.logout();
        session = null;
        attrName = null;
        attrValue = null;
    }
}