package com.jaspersoft.jasperserver.jaxrs.client.apiadapters.resources;

import com.jaspersoft.jasperserver.dto.resources.ClientFile;
import com.jaspersoft.jasperserver.dto.resources.ClientReference;
import com.jaspersoft.jasperserver.dto.resources.ClientSemanticLayerDataSource;
import com.jaspersoft.jasperserver.dto.resources.domain.ClientDomain;
import com.jaspersoft.jasperserver.dto.resources.domain.Schema;
import com.jaspersoft.jasperserver.jaxrs.client.RestClientTestUtil;
import com.jaspersoft.jasperserver.jaxrs.client.core.operationresult.OperationResult;
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
public class ResourcesDomainServiceTest extends RestClientTestUtil {

    private String testDomainUri = "/public/Samples/Domains/supermartDomain";

    @BeforeClass
    public void before() {
        initClient();
        initSession();
    }

    @Test
    public void should_upload_domain() throws FileNotFoundException {
        OperationResult<ClientDomain> operationResult = session
                .resourcesService()
                .resource("/public/Samples/Domains/supermartDomain")
                .get(ClientDomain.class);
        final ClientDomain domain = operationResult.getEntity();

        final Schema schema = domain.getSchema();

        OperationResult<ClientDomain> resDomain =
                session.resourcesService()
                        .domainResource()
                        .withDataSource(new ClientReference().setUri(domain.getDataSource().getUri()))
                        .withSchema(schema)
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
    public void should_upload_domain_as_descriptor() throws FileNotFoundException {
        OperationResult<ClientDomain> operationResult = session
                .resourcesService()
                .resource("/public/Samples/Domains/supermartDomain")
                .get(ClientDomain.class);
        final ClientDomain domain = operationResult.getEntity();

        ClientDomain resDomain =
                session.resourcesService()
                        .domainResource(domain)
                        .inFolder("/public")
                        .create()
                .getEntity();

        assertNotNull(resDomain);
        assertEquals(resDomain.getLabel(), "testDomain");
        assertEquals(resDomain.getDescription(), "testDescription");

    }

    @Deprecated
    @Test(enabled = false)
    public void should_upload_semanticLayerDataSource_with_schema() throws FileNotFoundException {

        OperationResult<ClientSemanticLayerDataSource> operationResult = session
                .resourcesService()
                .resource("/public/Samples/Domains/supermartDomain")
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