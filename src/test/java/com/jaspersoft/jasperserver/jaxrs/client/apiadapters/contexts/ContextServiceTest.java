package com.jaspersoft.jasperserver.jaxrs.client.apiadapters.contexts;

import com.jaspersoft.jasperserver.dto.resources.ClientResourceLookup;
import com.jaspersoft.jasperserver.dto.resources.domain.PresentationGroupElement;
import com.jaspersoft.jasperserver.dto.resources.domain.ResourceGroupElement;
import com.jaspersoft.jasperserver.jaxrs.client.RestClientTestUtil;
import com.jaspersoft.jasperserver.jaxrs.client.core.operationresult.OperationResult;
import javax.ws.rs.core.Response;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * <p/>
 * <p/>
 *
 * @author tetiana.iefimenko
 * @version $Id$
 * @see
 */
public class ContextServiceTest extends RestClientTestUtil {
    private String uuId;
    private ClientResourceLookup context;

    @BeforeClass
    public void before() {
        initClient();
        initSession();
        context = new ClientResourceLookup().setUri("/public/Samples/Data_Sources/FoodmartDataSource");
    }

    @AfterClass
    public void after() {
        session.logout();
    }

    @Test
    public void should_create_context() {

        OperationResult<ClientResourceLookup> operationResult = session
                .contextService()
                .context(ClientResourceLookup.class, "application/repository.resourceLookup+json")
                .create(context);

        assertNotNull(operationResult);
        assertEquals(Response.Status.CREATED.getStatusCode(), operationResult.getResponse().getStatus());

        extractUuid(operationResult.getResponse().getHeaderString("Location"));
        assertNotNull(uuId);

    }

    @Test(dependsOnMethods = "should_create_context")
    public void should_get_metadata() {
        OperationResult<PresentationGroupElement> operationResult = session
                .contextService()
                .context(uuId,
                        PresentationGroupElement.class,
                        "application/repository.resourceLookup.metadata+json")
                .metadata();
        assertNotNull(operationResult);
        assertEquals(Response.Status.OK.getStatusCode(), operationResult.getResponse().getStatus());
        assertNotNull(operationResult.getEntity());
    }

    @Test(dependsOnMethods = "should_create_context")
    public void should_get_metadata_with_parameters() {
        OperationResult<PresentationGroupElement> operationResult = session
                .contextService()
                .context(uuId,
                        PresentationGroupElement.class,
                        "application/repository.resourceLookup.metadata+json")
                .addParameter("expand", "public.accounts")
                .metadata();
        assertNotNull(operationResult);
        assertEquals(Response.Status.OK.getStatusCode(), operationResult.getResponse().getStatus());
        assertNotNull(operationResult.getEntity());
    }

    @Test(dependsOnMethods = "should_create_context")
    public void should_get_metadata_with_parameters_() {
        OperationResult<PresentationGroupElement> operationResult = session
                .contextService()
                .context(uuId,
                        PresentationGroupElement.class,
                        "application/repository.resourceLookup.metadata+json")
                .addParameter("include", "public.currency")
                .metadata();
        assertNotNull(operationResult);
        assertEquals(Response.Status.OK.getStatusCode(), operationResult.getResponse().getStatus());
        assertNotNull(operationResult.getEntity());
    }

    @Test(dependsOnMethods = "should_create_context")
    public void should_get_metadata_with_parameters_post() {
        OperationResult<PresentationGroupElement> operationResult = session
                .contextService()
                .context(uuId,
                        PresentationGroupElement.class,
                        "application/contexts.partialMetadataOptions+json")
                .addParameter("expands", "public.account")
                .addParameter("expands", "public.category")
                .partialMetadata();
        assertNotNull(operationResult);
        assertEquals(Response.Status.OK.getStatusCode(), operationResult.getResponse().getStatus());
        assertNotNull(operationResult.getEntity());
    }

    @Test(dependsOnMethods = "should_create_context")
    public void should_get_metadata_with_parameters__post() {
        OperationResult<PresentationGroupElement> operationResult = session
                .contextService()
                .context(uuId,
                        PresentationGroupElement.class,
                        "application/contexts.partialMetadataOptions+json")
                .addParameter("includes", "public.currency")
                .partialMetadata();
        assertNotNull(operationResult);
        assertEquals(Response.Status.OK.getStatusCode(), operationResult.getResponse().getStatus());
        assertNotNull(operationResult.getEntity());
    }

    @Test
    public void should_create_context_get_metadata_with_parameter_include() {

        OperationResult<ResourceGroupElement> operationResult = session
                .contextService()
                .context(ClientResourceLookup.class,
                        "application/repository.resourceLookup+json",
                        ResourceGroupElement.class,
                        "application/repository.resourceLookup.metadata+json")
                .addParameter("include", "public.account")
                .createAndGetMetadata(context);

        assertNotNull(operationResult);
        assertEquals(Response.Status.OK.getStatusCode(), operationResult.getResponse().getStatus());

        extractUuid(operationResult.getResponse().getHeaderString("Location"));
        assertNotNull(uuId);

    }

    @Test
    public void should_create_context_get_metadata_with_parameter_expands() {

        OperationResult<ResourceGroupElement> operationResult = session
                .contextService()
                .context(ClientResourceLookup.class,
                        "application/repository.resourceLookup+json",
                        ResourceGroupElement.class,
                        "application/repository.resourceLookup.metadata+json")
                .addParameter("expand", "public.account")
                .createAndGetMetadata(context);

        assertNotNull(operationResult);
        assertEquals(Response.Status.OK.getStatusCode(), operationResult.getResponse().getStatus());

        extractUuid(operationResult.getResponse().getHeaderString("Location"));
        assertNotNull(uuId);

    }

    private void extractUuid(String locationHeader) {
        if (locationHeader.endsWith("metadata")) {
            locationHeader = locationHeader.substring(0, locationHeader.lastIndexOf("/"));
        }

        uuId = locationHeader.substring(locationHeader.lastIndexOf("/") + 1);
    }

}
