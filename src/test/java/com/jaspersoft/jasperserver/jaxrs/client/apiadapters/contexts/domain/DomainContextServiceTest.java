package com.jaspersoft.jasperserver.jaxrs.client.apiadapters.contexts.domain;

import com.jaspersoft.jasperserver.dto.adhoc.query.ClientMultiLevelQuery;
import com.jaspersoft.jasperserver.dto.adhoc.query.field.ClientQueryGroup;
import com.jaspersoft.jasperserver.dto.adhoc.query.group.ClientQueryGroupBy;
import com.jaspersoft.jasperserver.dto.executions.ClientMultiLevelQueryResultData;
import com.jaspersoft.jasperserver.dto.resources.ClientFile;
import com.jaspersoft.jasperserver.dto.resources.ClientReference;
import com.jaspersoft.jasperserver.dto.resources.ClientResourceLookup;
import com.jaspersoft.jasperserver.dto.resources.ClientSemanticLayerDataSource;
import com.jaspersoft.jasperserver.dto.resources.domain.ClientDomain;
import com.jaspersoft.jasperserver.dto.resources.domain.PresentationElement;
import com.jaspersoft.jasperserver.dto.resources.domain.PresentationGroupElement;
import com.jaspersoft.jasperserver.dto.resources.domain.PresentationSingleElement;
import com.jaspersoft.jasperserver.dto.resources.domain.ResourceElement;
import com.jaspersoft.jasperserver.dto.resources.domain.ResourceGroupElement;
import com.jaspersoft.jasperserver.dto.resources.domain.ResourceSingleElement;
import com.jaspersoft.jasperserver.dto.resources.domain.Schema;
import com.jaspersoft.jasperserver.dto.resources.domain.SchemaElement;
import com.jaspersoft.jasperserver.jaxrs.client.RestClientTestUtil;
import com.jaspersoft.jasperserver.jaxrs.client.apiadapters.context.domain.DomainContextOperationResult;
import com.jaspersoft.jasperserver.jaxrs.client.core.operationresult.OperationResult;
import java.util.Arrays;
import javax.ws.rs.core.Response;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static java.util.Arrays.asList;
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
public class DomainContextServiceTest extends RestClientTestUtil {
    private String uuId;
    private ClientResourceLookup context;
    private ClientDomain clientDomain;
    private ClientSemanticLayerDataSource clientSemanticLayerDataSource;

    @BeforeClass
    public void before() {
        initClient();
        initSession();
        context = new ClientResourceLookup().setUri("/public/Samples/Data_Sources/FoodmartDataSource");

        ClientReference dataSource = new ClientReference().setUri("/public/Samples/Data_Sources/FoodmartDataSource");

        clientDomain = new ClientDomain().setDataSource(dataSource);
        ResourceSingleElement resourceSingleElement = new ResourceSingleElement().
                setName("account_type").
                setType("java.lang.String");
        ResourceGroupElement account = new ResourceGroupElement().
                setName("account").
                setElements(Arrays.<SchemaElement>asList(resourceSingleElement));
        ResourceGroupElement publicElem = new ResourceGroupElement().
                setName("public").
                setElements(Arrays.<SchemaElement>asList(account));
        ResourceGroupElement foodmartDataSourceJNDI = new ResourceGroupElement().
                setName("FoodmartDataSourceJNDI").
                setElements(Arrays.<SchemaElement>asList(publicElem));
        PresentationSingleElement presentationSingleElement = new PresentationSingleElement().
                setName("account_type").
                setLabel("account_type").
                setType("java.lang.String").
                setHierarchicalName("public.account.account_type").
                setResourcePath("FoodmartDataSourceJNDI.public.account.account_type");
        PresentationGroupElement presentationGroupElement = new PresentationGroupElement().setName("account").setElements(Arrays.<PresentationElement>asList(presentationSingleElement));
        Schema schema = new Schema().setResources(Arrays.<ResourceElement>asList(foodmartDataSourceJNDI)).setPresentation(asList(presentationGroupElement));
        clientDomain.setSchema(schema);

        clientSemanticLayerDataSource = new ClientSemanticLayerDataSource();
        clientSemanticLayerDataSource.setDataSource(dataSource);
        ClientFile schema1 = new ClientFile();
        schema1.setType(ClientFile.FileType.xml);
        schema1.setContent("PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz4NCjxzY2hlbWEgeG1sbnM9Imh0dHA6Ly93d3cuamFzcGVyc29mdC5jb20vMjAwNy9TTC9YTUxTY2hlbWEiIHZlcnNpb249IjEuMyI+DQogIDxkYXRhSXNsYW5kcz4NCiAgICA8aXRlbUdyb3VwIGlkPSJhY2NvdW50IiByZXNvdXJjZUlkPSJhY2NvdW50IiAvPg0KICA8L2RhdGFJc2xhbmRzPg0KICA8ZGF0YVNvdXJjZXM+DQogICAgPGpkYmNEYXRhU291cmNlIGlkPSJGb29kbWFydERhdGFTb3VyY2VKTkRJIj4NCiAgICAgIDxzY2hlbWFNYXA+DQogICAgICAgIDxlbnRyeSBrZXk9InB1YmxpYyI+DQogICAgICAgICAgPHN0cmluZz5wdWJsaWM8L3N0cmluZz4NCiAgICAgICAgPC9lbnRyeT4NCiAgICAgICAgPGVudHJ5IGtleT0iZGVmYXVsdFNjaGVtYSI+DQogICAgICAgICAgPHN0cmluZyAvPg0KICAgICAgICA8L2VudHJ5Pg0KICAgICAgPC9zY2hlbWFNYXA+DQogICAgPC9qZGJjRGF0YVNvdXJjZT4NCiAgPC9kYXRhU291cmNlcz4NCiAgPGl0ZW1zPg0KICAgIDxpdGVtIGlkPSJhY2NvdW50X3R5cGUiIGxhYmVsPSJhY2NvdW50X3R5cGUiIHJlc291cmNlSWQ9ImFjY291bnQuYWNjb3VudF90eXBlIiAvPg0KICA8L2l0ZW1zPg0KICA8cmVzb3VyY2VzPg0KICAgIDxqZGJjVGFibGUgaWQ9ImFjY291bnQiIGRhdGFzb3VyY2VJZD0iRm9vZG1hcnREYXRhU291cmNlSk5ESSIgdGFibGVOYW1lPSJwdWJsaWMuYWNjb3VudCI+DQogICAgICA8ZmllbGRMaXN0Pg0KICAgICAgICA8ZmllbGQgaWQ9ImFjY291bnRfdHlwZSIgdHlwZT0iamF2YS5sYW5nLlN0cmluZyIgLz4NCiAgICAgIDwvZmllbGRMaXN0Pg0KICAgIDwvamRiY1RhYmxlPg0KICA8L3Jlc291cmNlcz4NCjwvc2NoZW1hPg0KDQo=");
        clientSemanticLayerDataSource.setSchema(schema1);
    }

    @AfterClass
    public void after() {
        session.logout();
    }

    @Test
    public void should_create_resource_lookup_context() {

        OperationResult<ClientResourceLookup> operationResult = session
                .domainContextService()
                .context("/public/Samples/Data_Sources/FoodmartDataSource")
                .create();

        assertNotNull(operationResult);
        assertEquals(Response.Status.CREATED.getStatusCode(), operationResult.getResponse().getStatus());

        extractUuid(operationResult.getResponse().getHeaderString("Location"));
        assertNotNull(uuId);

    }

    @Test
    public void should_create_resource_lookup_context_and_get_metadata() {
        OperationResult<PresentationGroupElement> operationResult = session
                .domainContextService()
                .context("/public/Samples/Data_Sources/FoodmartDataSource")
                .create().getMetadata();
        assertNotNull(operationResult);
        assertEquals(Response.Status.OK.getStatusCode(), operationResult.getResponse().getStatus());
        assertNotNull(operationResult.getEntity());
    }

    @Test
    public void should_create_resource_lookup_context_and_get_metadata_with_parameters() {
        OperationResult<PresentationGroupElement> operationResult = session
                .domainContextService()
                .context("/public/Samples/Data_Sources/FoodmartDataSource")
                .create()
                .addParam("expand","public.accounts")
                .getMetadata();
        assertNotNull(operationResult);
        assertEquals(Response.Status.OK.getStatusCode(), operationResult.getResponse().getStatus());
        assertNotNull(operationResult.getEntity());
    }


    @Test
    public void should_create_resource_lookup_context_and_get_metadata_after_pause() {
        DomainContextOperationResult<ClientResourceLookup> domainContextOperationResult = session
                .domainContextService()
                .context("/public/Samples/Data_Sources/FoodmartDataSource")
                .create();
        try {
            Thread.sleep(2*60*1000+10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        OperationResult<PresentationGroupElement> operationResult = domainContextOperationResult.getMetadata();
        assertNotNull(operationResult);
        assertEquals(Response.Status.OK.getStatusCode(), operationResult.getResponse().getStatus());
        assertNotNull(operationResult.getEntity());
    }

    @Test
    public void should_create_domain_context() {

        OperationResult<ClientDomain> operationResult = session
                .domainContextService()
                .context(clientDomain)
                .create();

        assertNotNull(operationResult);
        assertEquals(Response.Status.CREATED.getStatusCode(), operationResult.getResponse().getStatus());

        extractUuid(operationResult.getResponse().getHeaderString("Location"));
        assertNotNull(uuId);

    }

    @Test
    public void should_create_domain_context_and_get_metadata() {
        OperationResult<PresentationGroupElement> operationResult = session
                .domainContextService()
                .context(clientDomain)
                .create().getMetadata();
        assertNotNull(operationResult);
        assertEquals(Response.Status.OK.getStatusCode(), operationResult.getResponse().getStatus());
        assertNotNull(operationResult.getEntity());
    }

    @Test
    public void should_create_domain_context_and_get_metadata_with_parameters() {
        OperationResult<PresentationGroupElement> operationResult = session
                .domainContextService()
                .context(clientDomain)
                .create()
                .addParam("expand","public.accounts")
                .getMetadata();
        assertNotNull(operationResult);
        assertEquals(Response.Status.OK.getStatusCode(), operationResult.getResponse().getStatus());
        assertNotNull(operationResult.getEntity());
    }


    @Test
    public void should_create_domain_context_and_get_metadata_after_pause() {
        DomainContextOperationResult<ClientDomain> domainContextOperationResult = session
                .domainContextService()
                .context(clientDomain)
                .create();
        try {
            Thread.sleep(2*60*1000+10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        OperationResult<PresentationGroupElement> operationResult = domainContextOperationResult.getMetadata();
        assertNotNull(operationResult);
        assertEquals(Response.Status.OK.getStatusCode(), operationResult.getResponse().getStatus());
        assertNotNull(operationResult.getEntity());
    }


    @Test
    public void should_create_semanticLayerDataSource_context() {

        OperationResult<ClientSemanticLayerDataSource> operationResult = session
                .domainContextService()
                .context(clientSemanticLayerDataSource)
                .create();

        assertNotNull(operationResult);
        assertEquals(Response.Status.CREATED.getStatusCode(), operationResult.getResponse().getStatus());

        extractUuid(operationResult.getResponse().getHeaderString("Location"));
        assertNotNull(uuId);

    }

    @Test
    public void should_create_semanticLayerDataSource_context_and_get_metadata() {
        OperationResult<PresentationGroupElement> operationResult = session
                .domainContextService()
                .context(clientSemanticLayerDataSource)
                .create().getMetadata();
        assertNotNull(operationResult);
        assertEquals(Response.Status.OK.getStatusCode(), operationResult.getResponse().getStatus());
        assertNotNull(operationResult.getEntity());
    }

    @Test
    public void should_create_semanticLayerDataSource_context_and_get_metadata_with_parameters() {
        OperationResult<PresentationGroupElement> operationResult = session
                .domainContextService()
                .context(clientSemanticLayerDataSource)
                .create()
                .addParam("expand","public.accounts")
                .getMetadata();
        assertNotNull(operationResult);
        assertEquals(Response.Status.OK.getStatusCode(), operationResult.getResponse().getStatus());
        assertNotNull(operationResult.getEntity());
    }


    @Test
    public void should_create_semanticLayerDataSource_context_and_get_metadata_after_pause() {
        DomainContextOperationResult<ClientSemanticLayerDataSource> domainContextOperationResult = session
                .domainContextService()
                .context(clientSemanticLayerDataSource)
                .create();
        try {
            Thread.sleep(2*60*1000+10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        OperationResult<PresentationGroupElement> operationResult = domainContextOperationResult.addParam("expand","public.accounts").getMetadata();
        assertNotNull(operationResult);
        assertEquals(Response.Status.OK.getStatusCode(), operationResult.getResponse().getStatus());
        assertNotNull(operationResult.getEntity());
    }

    @Test
    public void should_create_domain_context_and_execute_query() {
        ClientMultiLevelQuery clientMultiLevelQuery = new ClientMultiLevelQuery().setGroupBy(
                new ClientQueryGroupBy().setGroups(
                        asList(new ClientQueryGroup().
                                setId("account_type").
                                setFieldName("account_type"))));
        OperationResult<ClientMultiLevelQueryResultData> operationResult = session
                .domainContextService()
                .context(clientDomain)
                .create().executeQuery(clientMultiLevelQuery);
        assertNotNull(operationResult);
        assertEquals(Response.Status.OK.getStatusCode(), operationResult.getResponse().getStatus());
        assertNotNull(operationResult.getEntity());
    }


    @Test
    public void should_create_semanticLayerDataSource_context_and_execute_query() {
        ClientMultiLevelQuery clientMultiLevelQuery = new ClientMultiLevelQuery().setGroupBy(
                new ClientQueryGroupBy().setGroups(
                        asList(new ClientQueryGroup().
                                setId("account_type").
                                setFieldName("account_type"))));
        OperationResult<ClientMultiLevelQueryResultData> operationResult = session
                .domainContextService()
                .context(clientSemanticLayerDataSource)
                .create().executeQuery(clientMultiLevelQuery);
        assertNotNull(operationResult);
        assertEquals(Response.Status.OK.getStatusCode(), operationResult.getResponse().getStatus());
        assertNotNull(operationResult.getEntity());
    }

    private void extractUuid(String locationHeader) {
        if (locationHeader.endsWith("metadata")) {
            locationHeader = locationHeader.substring(0, locationHeader.lastIndexOf("/"));
        }

        uuId = locationHeader.substring(locationHeader.lastIndexOf("/") + 1);
    }

}
