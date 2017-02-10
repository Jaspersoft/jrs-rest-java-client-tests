package com.jaspersoft.jasperserver.jaxrs.client.apiadapters.resources;

import com.jaspersoft.jasperserver.dto.resources.ClientFile;
import com.jaspersoft.jasperserver.dto.resources.ClientFolder;
import com.jaspersoft.jasperserver.dto.resources.ClientJndiJdbcDataSource;
import com.jaspersoft.jasperserver.dto.resources.ClientReference;
import com.jaspersoft.jasperserver.dto.resources.ClientReferenceableFile;
import com.jaspersoft.jasperserver.dto.resources.ClientReportUnit;
import com.jaspersoft.jasperserver.dto.resources.ClientResource;
import com.jaspersoft.jasperserver.dto.resources.ClientResourceListWrapper;
import com.jaspersoft.jasperserver.dto.resources.domain.ClientDomain;
import com.jaspersoft.jasperserver.jaxrs.client.RestClientTestUtil;
import com.jaspersoft.jasperserver.jaxrs.client.core.operationresult.OperationResult;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.ws.rs.core.Response;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * @author Alexander Krasnyanskiy
 * @author tetiana Iefimenko
 */
public class ResourcesServiceTest extends RestClientTestUtil {

    private ClientDomain testDomain;

    @BeforeClass
    public void before() {
        initClient();
        initSession();
    }


    @Test
    public void should_return_resources() {

        // When

        OperationResult<ClientResourceListWrapper> result = session
                .resourcesService()
                .resources()
                .search();
        ClientResourceListWrapper resourceListWrapper = result.getEntity();

        // Then

        assertNotNull(resourceListWrapper);
        assertTrue(resourceListWrapper.getResourceLookups().size() > 0);
    }

    @Test
    public void should_return_domain_as_resource() {

        // When

        OperationResult<ClientDomain> result = session
                .resourcesService()
                .resource("/organizations/organization_1/AdditionalResourcesForTesting/Domains/Domain_for_special_characters_testing")
                .get(ClientDomain.class);



        // Then

        assertNotNull(result.getEntity());
        testDomain = result.getEntity();
    }

    @Test(dependsOnMethods ="should_return_domain_as_resource")
    public void should_create_domain_as_resource() {

        // When

        OperationResult<ClientResource> result = session
                .resourcesService()
                .resource("/public")
                .createNew(testDomain);

        // Then

        assertNotNull(result.getEntity());
    }


    @Test
    public void should_return_resources_with_parameters() {

        // When

        OperationResult<ClientResourceListWrapper> result = session
                .resourcesService()
                .resources()
                .parameter(ResourceSearchParameter.FOLDER_URI, "/public/Samples")
                .parameter(ResourceSearchParameter.LIMIT, "5")
                .search();
        ClientResourceListWrapper resourceListWrapper = result.getEntity();

        // Then

        assertNotNull(resourceListWrapper);
        assertTrue(resourceListWrapper.getResourceLookups().size() > 0);
        assertTrue(resourceListWrapper.getResourceLookups().size() <= 5);
    }

    @Test
    public void should_upload_resource_as_binaryData() throws IOException {

        byte[] encoded = Files.readAllBytes(Paths.get("D:\\workspaceIdea\\jrs-rest-java-client-tests\\image1.jpeg"));
        String content = new String(encoded, StandardCharsets.UTF_8);

        ClientFile file = new ClientFile();
        file.
                setUri("/public").
                setType(ClientFile.FileType.img).
                setLabel("testFile").
                setDescription("testDescription").
                setContent(content);

        OperationResult<ClientResource> result = session
                .resourcesService()
                .resource("/public")
                .createNew(file);

        assertNotNull(result);
    }

    @Test
    public void should_upload_resource_as_multipart() throws FileNotFoundException {

        OperationResult<ClientFile> result = session
                .resourcesService()
                .resource("/public")
                .uploadFile(new File("D:\\workspaceIdea\\jrs-rest-java-client-tests\\image1.jpeg"), ClientFile.FileType.img, "fileName", "fileDescription");

        assertNotNull(result);
    }

    @Test
    public void should_delete_folder() throws InterruptedException {

        // When
        Response resp = session.resourcesService()
                .resource("/reports/testFolder")
                .delete()
                .getResponse();

        // Then
        ByteArrayInputStream is = (ByteArrayInputStream) resp.getEntity();

        Assert.assertEquals(resp.getStatus(), 204);
        Assert.assertNotNull(is);
    }

    @Test
    public void should_return_resource_details() throws InterruptedException {

        // When
        ClientResource clientResource = session.resourcesService()
                .resource("/organizations/organization_1/Domains/Simple_Domain")
                .details()
                .getEntity();

        Assert.assertNotNull(clientResource);
        Assert.assertNotNull(clientResource.getCreationDate());
    }


    @Test
    public void should_copy_resource() throws InterruptedException {

        // When
        ClientResource clientResource = session.resourcesService()
                .resource("/public")
                .copyFrom("/public/Samples/Ad_Hoc_Views/01__Geographic_Results_by_Segment")
                .getEntity();

        Assert.assertNotNull(clientResource);
        Assert.assertNotNull(clientResource.getCreationDate());
    }

    @Test
    public void should_get_domain_descriptor() throws InterruptedException {

        // When
        OperationResult<ClientDomain> result = session
                .resourcesService()
                .resource("/public/Samples/Domains/supermartDomain")
                .get(ClientDomain.class);

        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getEntity().getCreationDate());
    }


    @Test
    public void should_return_resource() throws InterruptedException {

        // When
        OperationResult<ClientFolder> clientFolderOperationResult = session.resourcesService()
                .resource("/").get(ClientFolder.class);
        Assert.assertTrue(clientFolderOperationResult.getResponse().getStatus() == 200);
        assertNotNull(clientFolderOperationResult.getEntity());
        assertNotNull(clientFolderOperationResult.getEntity().getVersion());
    }


    @Test
    public void should_upload_report_with_jrxml() throws FileNotFoundException {
        ClientReferenceableFile jrxml = new ClientReferenceableFile() {
            @Override
            public String getUri() {
                return null;
            }
        };
        ClientReportUnit repunit = new ClientReportUnit();
        repunit.setJrxml(jrxml);
        repunit.setLabel("05_1.All accounts test report unit");
        repunit.setDataSource(new ClientJndiJdbcDataSource().
                setUri("/public/Samples/Data_Sources/JServerJNDIDS").
                setLabel("JServer JNDI Data Source").
                setJndiName("jdbc/sugarcrm"));
        repunit.setLabel("All accounts test report unit");

        ClientFile clifile = new ClientFile();
        clifile.setType(ClientFile.FileType.jrxml);
        clifile.setLabel("AllAccounts");

        OperationResult<ClientReportUnit> repUnut =
                session.resourcesService()
                        .resource(repunit)
                        .withJrxml(new FileInputStream("report_upload_resources\\AllAccounts.jrxml"), clifile)
                        .createInFolder("/temp");

        assertNotNull(repUnut);
    }



    @Test
    public void should_upload_report_with_jrxml_with_image() throws FileNotFoundException {
        ClientReferenceableFile jrxml = new ClientReferenceableFile() {
            @Override
            public String getUri() {
                return null;
            }
        };
        ClientReportUnit repunit = new ClientReportUnit();
        repunit.setJrxml(jrxml);
        repunit.setLabel("05_1.All accounts test report unit");
        repunit.setDataSource(new ClientJndiJdbcDataSource().
                setUri("/public/Samples/Data_Sources/JServerJNDIDS").
                setLabel("JServer JNDI Data Source").
                setJndiName("jdbc/sugarcrm"));
        repunit.setLabel("All accounts test report unit");


        ClientFile clifile = new ClientFile();
        clifile.setType(ClientFile.FileType.jrxml);
        clifile.setLabel("AllAccounts");

        OperationResult<ClientReportUnit> repUnut =
                session.resourcesService()
                        .resource(repunit)
                        .withJrxml(new FileInputStream("report_upload_resources\\AllAccounts.jrxml"), clifile)
                        .withNewFileReference("Jaspersoft_logo.png", new ClientReference().setUri("/public/Samples/Resources/Images/Jaspersoft_logo.png"))
                        .withNewFileReference("JRLogo", new ClientReference().setUri("/organizations/organization_1/images/JRLogo"))
                        .createInFolder("/temp");
        assertNotNull(repUnut);
    }

    @AfterClass
    public void after() {
        session.logout();
        session = null;
    }

}