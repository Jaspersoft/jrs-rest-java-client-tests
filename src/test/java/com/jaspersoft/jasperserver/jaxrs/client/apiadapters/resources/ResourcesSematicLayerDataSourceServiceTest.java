package com.jaspersoft.jasperserver.jaxrs.client.apiadapters.resources;

import com.jaspersoft.jasperserver.dto.resources.ClientFile;
import com.jaspersoft.jasperserver.dto.resources.ClientReference;
import com.jaspersoft.jasperserver.dto.resources.ClientSemanticLayerDataSource;
import com.jaspersoft.jasperserver.jaxrs.client.RestClientTestUtil;
import com.jaspersoft.jasperserver.jaxrs.client.core.operationresult.OperationResult;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * @author Alexander Krasnyanskiy
 * @author tetiana Iefimenko
 */
public class ResourcesSematicLayerDataSourceServiceTest extends RestClientTestUtil {

    private String testDomainUri = "/public/Samples/Domains/supermartDomain";

    @BeforeClass
    public void before() {
        initClient();
        initSession();
    }

    @Test
    public void should_upload_semanticLayerDataSource_with_schema_stream() throws FileNotFoundException {
        OperationResult<ClientSemanticLayerDataSource> operationResult = session
                .resourcesService()
                .resource(testDomainUri)
                .get(ClientSemanticLayerDataSource.class);
        final ClientSemanticLayerDataSource domain = operationResult.getEntity();

        InputStream schemaXmlResult = session
                .resourcesService()
                .resource(domain.getSchema().getUri())
                .downloadBinary()
                .getEntity();

        OperationResult<ClientSemanticLayerDataSource> resDomain =
                session.resourcesService()
                        .semanticLayerDataSourceResource()
                        .withDataSource(new ClientReference().setUri(domain.getDataSource().getUri()))
                        .withSchema(schemaXmlResult, "schema.xml", "schema")
                        .withLabel("testDomain")
                        .withDescription("testDescription")
                        .inFolder("/public")
                        .create();

        OperationResult<ClientSemanticLayerDataSource> operationResult1 = session
                .resourcesService()
                .resource("/public/testDomain")
                .get(ClientSemanticLayerDataSource.class);
        final ClientSemanticLayerDataSource domainNew = operationResult1.getEntity();

        assertNotNull(domainNew);
        assertEquals(domainNew.getLabel(), "testDomain");
        assertEquals(domainNew.getDescription(), "testDescription");

    }
    @Test
    public void should_upload_semanticLayerDataSource_with_resourceDescriptor() throws FileNotFoundException {
        OperationResult<ClientSemanticLayerDataSource> operationResult = session
                .resourcesService()
                .resource(testDomainUri)
                .get(ClientSemanticLayerDataSource.class);
        final ClientSemanticLayerDataSource domain = operationResult.getEntity();

        final String uri = operationResult.getEntity().getSchema().getUri();
        OperationResult<ClientSemanticLayerDataSource> resDomain =
                session.resourcesService()
                        .semanticLayerDataSourceResource()
                        .withDataSource(new ClientReference().setUri(domain.getDataSource().getUri()))
                        .withSchema(new ClientReference().setUri("/public/schema.xml"))
                        .withLabel("testDomain")
                        .withDescription("testDescription")
                        .inFolder("/public")
                        .create();

        OperationResult<ClientSemanticLayerDataSource> operationResult1 = session
                .resourcesService()
                .resource("/public/testDomain")
                .get(ClientSemanticLayerDataSource.class);
        final ClientSemanticLayerDataSource domainNew = operationResult1.getEntity();

        assertNotNull(domainNew);
        assertEquals(domainNew.getLabel(), "testDomain");
        assertEquals(domainNew.getDescription(), "testDescription");
    }

    @Test
    public void should_upload_semanticLayerDataSource_with_resourceDescriptor_asBase64Content() throws FileNotFoundException {
        OperationResult<ClientSemanticLayerDataSource> operationResult = session
                .resourcesService()
                .resource(testDomainUri)
                .get(ClientSemanticLayerDataSource.class);
        final ClientSemanticLayerDataSource domain = operationResult.getEntity();
        final ClientFile clientFile = new ClientFile()
                .setType(ClientFile.FileType.xml)
                .setContent(RestClientTestUtil.fileToStringBase64Encoded(new File("D:\\workspaceIdea\\jrs-rest-java-client-tests\\report_upload_resources\\schema.xml")))
                .setLabel("schema.xml");
        final String uri = operationResult.getEntity().getSchema().getUri();
        OperationResult<ClientSemanticLayerDataSource> resDomain =
                session.resourcesService()
                        .semanticLayerDataSourceResource()
                        .withDataSource(new ClientReference().setUri(domain.getDataSource().getUri()))
                        .withSchema(clientFile)
                        .withLabel("testDomain")
                        .withDescription("testDescription")
                        .inFolder("/public")
                        .create();

        OperationResult<ClientSemanticLayerDataSource> operationResult1 = session
                .resourcesService()
                .resource("/public/testDomain")
                .get(ClientSemanticLayerDataSource.class);
        final ClientSemanticLayerDataSource domainNew = operationResult1.getEntity();

        assertNotNull(domainNew);
        assertEquals(domainNew.getLabel(), "testDomain");
        assertEquals(domainNew.getDescription(), "testDescription");
    }

    @Test
    public void should_upload_semanticLayerDataSource_as_resourceDescriptor() throws FileNotFoundException {
        OperationResult<ClientSemanticLayerDataSource> operationResult = session
                .resourcesService()
                .resource(testDomainUri)
                .get(ClientSemanticLayerDataSource.class);

        final ClientSemanticLayerDataSource domain = operationResult.getEntity();

        ClientSemanticLayerDataSource newDomain = new ClientSemanticLayerDataSource()
                .setSchema(new ClientFile()
                        .setUri(domain.getSchema().getUri())
                        .setType(ClientFile.FileType.xml)
                        .setLabel("schema.Xml"))
                .setDataSource(new ClientReference().setUri(domain.getDataSource().getUri()))
                .setLabel("testDomain");

        OperationResult<ClientSemanticLayerDataSource> resDomain =
                session.resourcesService()
                        .semanticLayerDataSourceResource(newDomain)
                        .inFolder("/public")
                        .create();

        OperationResult<ClientSemanticLayerDataSource> operationResult1 = session
                .resourcesService()
                .resource("/public/testDomain")
                .get(ClientSemanticLayerDataSource.class);
        final ClientSemanticLayerDataSource domainNew = operationResult1.getEntity();

        assertNotNull(domainNew);
        assertEquals(domainNew.getLabel(), "testDomain");
        assertEquals(domainNew.getDescription(), "testDescription");
    }

    @Deprecated
    @Test(enabled = false)
    public void should_upload_semanticLayerDataSource_with_schema() throws FileNotFoundException {

        OperationResult<ClientSemanticLayerDataSource> operationResult = session
                .resourcesService()
                .resource(testDomainUri)
                .get(ClientSemanticLayerDataSource.class);
        final ClientSemanticLayerDataSource domain = operationResult.getEntity();

        OperationResult<InputStream> schemaXmlResult = session
                .resourcesService()
                .resource(domain.getSchema().getUri())
                .downloadBinary();

        OperationResult<InputStream> securityFileResult = session
                .resourcesService()
                .resource(domain.getSecurityFile().getUri())
                .downloadBinary();

        InputStream inputStreamSchema = schemaXmlResult.getEntity();
        InputStream inputStreamSecurityFile = securityFileResult.getEntity();

        ClientFile schemaClientFile = new ClientFile();
        schemaClientFile.setType(ClientFile.FileType.xml);
        schemaClientFile.setLabel("domain_schema");
        schemaClientFile.setUri(domain.getSchema().getUri());

        ClientFile securityClientFile = new ClientFile();
        schemaClientFile.setType(ClientFile.FileType.xml);
        schemaClientFile.setLabel("domain_security");
        schemaClientFile.setUri(domain.getSecurityFile().getUri());

        OperationResult<ClientSemanticLayerDataSource> resDomain =
                session.resourcesService()
                        .resource(domain)
                        .withDataSource(new ClientReference().setUri(domain.getDataSource().getUri()))
                        .withUri("/public/temp/new_domain")
                        .withSecurityFile(inputStreamSecurityFile, securityClientFile)
                        .withSchema(inputStreamSchema, schemaClientFile)
                        .createInFolder("/public/temp");

        assertNotNull(resDomain);
    }

    @AfterClass
    public void after() {
        session.logout();
        session = null;
    }

}