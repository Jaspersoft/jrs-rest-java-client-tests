package com.jaspersoft.jasperserver.jaxrs.client.apiadapters.attributes;

import com.jaspersoft.jasperserver.dto.authority.ClientUserAttribute;
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

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertNull;
import static org.testng.AssertJUnit.assertTrue;

@Test
public class OrganizationAttributesServiceTest extends RestClientTestUtil {

    private HypermediaAttribute organizationAttribute;
    private HypermediaAttributesListWrapper organizationAttributes;
    private String organizationName;

    @BeforeClass
    public void before() {
        organizationAttributes = new HypermediaAttributesListWrapper();
        organizationAttributes.setProfileAttributes(asList(
                new HypermediaAttribute(new ClientUserAttribute().setName("test_org_attr_1").setValue("test_value")),
                new HypermediaAttribute(new ClientUserAttribute().setName("test_org_attr_2").setValue("test_value"))));
        organizationAttribute = new HypermediaAttribute();
        organizationAttribute.setName("test_org_attr");
        organizationAttribute.setValue("test_value");
        organizationName = "myOrg1";
        initClient();
        initSession();
    }

    @Test
    public void should_create_organization_attribute() {
        OperationResult<HypermediaAttribute> retrieved = session
                .attributesService()
                .forOrganization(organizationName)
                .attribute(organizationAttribute.getName())
                .createOrUpdate(organizationAttribute);

        assertNotNull(retrieved);
        assertEquals(retrieved.getEntity().getName(), organizationAttribute.getName());

    }

    @Test(dependsOnMethods = "should_create_organization_attribute")
    public void should_return_attribute() {
        HypermediaAttribute entity = session
                .attributesService()
                .forOrganization(organizationName)
                .attribute(organizationAttribute.getName())
                .get()
                .getEntity();

        assertEquals(entity.getValue(), organizationAttribute.getValue());
        assertNull(entity.getEmbedded());
    }

    @Test(dependsOnMethods = "should_return_attribute")
    public void should_return_attribute_with_permissions() {
        HypermediaAttribute entity = session
                .attributesService()
                .forOrganization(organizationName)
                .attribute(organizationAttribute.getName())
                .setIncludePermissions(true)
                .get()
                .getEntity();

        assertEquals(entity.getValue(), organizationAttribute.getValue());
        assertNotNull(entity.getEmbedded());
    }


    @Test(dependsOnMethods = "should_return_attribute_with_permissions")
    public void should_update_attribute_with_permissions() {
        organizationAttribute.setDescription("Organization attribute description");
        organizationAttribute.setPermissionMask(32);
        organizationAttribute.setSecure(false);
        organizationAttribute.setInherited(false);
        organizationAttribute.setHolder("tenant:/" + organizationName);
        HypermediaAttributeEmbeddedContainer container = new HypermediaAttributeEmbeddedContainer();
        container.setRepositoryPermissions(
                asList(new RepositoryPermission().setUri("attr:/organizations/" + organizationName + "/attributes/" + organizationAttribute.getName()).setRecipient("role:/ROLE_ADMINISTRATOR").setMask(32)));

        organizationAttribute.setEmbedded(container);


        HypermediaAttribute entity = session
                .attributesService()
                .forOrganization(organizationName)
                .attribute(organizationAttribute.getName())
                .setIncludePermissions(true)
                .createOrUpdate(organizationAttribute)
                .getEntity();

        assertEquals(entity.getValue(), organizationAttribute.getValue());
        assertNotNull(entity.getEmbedded());
        assertEquals(new Integer(32), entity.getEmbedded().getRepositoryPermissions().get(0).getMask());
    }

    @Test(dependsOnMethods = "should_update_attribute_with_permissions")
    public void should_delete_attribute() {
        OperationResult<HypermediaAttribute> operationResult = session
                .attributesService()
                .forOrganization(organizationName)
                .attribute(organizationAttribute.getName())
                .delete();

        assertNotNull(operationResult);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), operationResult.getResponse().getStatus());
    }

    @Test(dependsOnMethods = "should_delete_attribute")
    public void should_create_attributes() {

        OperationResult<HypermediaAttributesListWrapper> attributes = session
                .attributesService()
                .forOrganization(organizationName)
                .attributes(asList(organizationAttributes.getProfileAttributes().get(0).getName(),
                        organizationAttributes.getProfileAttributes().get(1).getName()))
                .createOrUpdate(organizationAttributes);

        assertNotNull(attributes);

    }

    @Test(dependsOnMethods = "should_create_attributes")
    public void should_return_server_attributes() {
        List<HypermediaAttribute> attributes = session
                .attributesService()
                .forOrganization(organizationName)
                .allAttributes()
                .get()
                .getEntity()
                .getProfileAttributes();

        assertTrue(attributes.size() >= 2);
    }

    @Test(dependsOnMethods = "should_return_server_attributes")
    public void should_return_server_attributes_with_permissions() {
        List<HypermediaAttribute> attributes = session
                .attributesService()
                .forOrganization(organizationName)
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
                .forOrganization(organizationName)
                .attributes(asList(organizationAttributes.getProfileAttributes().get(0).getName(),
                        organizationAttributes.getProfileAttributes().get(1).getName()))
                .get()
                .getEntity()
                .getProfileAttributes();

        assertTrue(attributes.size() == 2);
    }


    @Test(dependsOnMethods = "should_return_specified_server_attributes")
    public void should_return_specified_server_attributes_with_permissions() {
        List<HypermediaAttribute> attributes = session
                .attributesService()
                .forOrganization(organizationName)
                .attributes(asList(organizationAttributes.getProfileAttributes().get(0).getName(),
                        organizationAttributes.getProfileAttributes().get(1).getName()))
                .setIncludePermissions(true)
                .get()
                .getEntity()
                .getProfileAttributes();

        assertTrue(attributes.size() == 2);
        assertTrue(attributes.get(0).getEmbedded() != null);
    }

    @Test(dependsOnMethods = "should_return_specified_server_attributes_with_permissions")
    public void should_delete_specified_server_attributes() {
        OperationResult<HypermediaAttributesListWrapper> operationResult = session
                .attributesService()
                .forOrganization(organizationName)
                .attributes(asList(organizationAttributes.getProfileAttributes().get(0).getName(),
                        organizationAttributes.getProfileAttributes().get(1).getName()))
                .delete();

        assertTrue(instanceOf(NullEntityOperationResult.class).matches(operationResult));
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), operationResult.getResponse().getStatus());
    }


    @AfterClass
    public void after() {
        session.logout();
        session = null;
        organizationName = null;
        organizationAttribute = null;
    }
}