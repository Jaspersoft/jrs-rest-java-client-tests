package com.jaspersoft.jasperserver.jaxrs.client.apiadapters.resources;

import com.jaspersoft.jasperserver.dto.common.PatchDescriptor;
import com.jaspersoft.jasperserver.dto.common.PatchItem;
import com.jaspersoft.jasperserver.dto.resources.ClientFile;
import com.jaspersoft.jasperserver.dto.resources.ClientFolder;
import com.jaspersoft.jasperserver.dto.resources.ClientJndiJdbcDataSource;
import com.jaspersoft.jasperserver.dto.resources.ClientReference;
import com.jaspersoft.jasperserver.dto.resources.ClientReferenceableFile;
import com.jaspersoft.jasperserver.dto.resources.ClientReportUnit;
import com.jaspersoft.jasperserver.dto.resources.ClientResource;
import com.jaspersoft.jasperserver.dto.resources.ClientResourceListWrapper;
import com.jaspersoft.jasperserver.dto.resources.ClientSemanticLayerDataSource;
import com.jaspersoft.jasperserver.dto.resources.domain.ClientDomain;
import com.jaspersoft.jasperserver.jaxrs.client.RestClientTestUtil;
import com.jaspersoft.jasperserver.jaxrs.client.apiadapters.resources.util.ResourceSearchParameter;
import com.jaspersoft.jasperserver.jaxrs.client.apiadapters.resources.util.ResourceServiceParameter;
import com.jaspersoft.jasperserver.jaxrs.client.core.operationresult.OperationResult;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import javax.ws.rs.core.Response;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * @author Alexander Krasnyanskiy
 * @author tetiana Iefimenko
 */
public class ResourcesServiceTest extends RestClientTestUtil {

    public static final String NEW_TEST_DESCRIPTION = "new test description";
    private ClientDomain testDomain;
    private String testDomainUri = "/public/Samples/Domains/supermartDomain";
    private String testResourceUri = "";
    private static ClientFile fileAsStrreamUploaded;
    private ClientFile fileAsBase64Uploaded;
    private ClientFile fileAsMultipartUploaded;

    @BeforeClass
    public void before() {
        initClient();
        initSession();
    }

// GET section

    @Test
    public void should_get_domain_descriptor() throws InterruptedException {

        // When
        OperationResult<ClientDomain> result = session
                .resourcesService()
                .resource(testDomainUri)
                .detailsForType(ClientDomain.class);

        Assert.assertNotNull(result);
        Assert.assertNotNull(result.getEntity().getCreationDate());
    }

    @Test
    public void should_return_resources() {

        // When

        OperationResult<ClientResourceListWrapper> result = session
                .resourcesService()
                .resources()
                .parameter(ResourceSearchParameter.LIMIT, "10")
                .search();
        ClientResourceListWrapper resourceListWrapper = result.getEntity();

        // Then

        assertNotNull(resourceListWrapper);
        assertTrue(resourceListWrapper.getResourceLookups().size() > 0);
    }

    @Test
    public void should_return_resource_descriptor() throws InterruptedException {

        // When
        OperationResult<ClientFolder> clientFolderOperationResult = session
                .resourcesService()
                .resource("/")
                .detailsForType(ClientFolder.class);

        Assert.assertTrue(clientFolderOperationResult.getResponse().getStatus() == 200);
        assertNotNull(clientFolderOperationResult.getEntity());
        assertNotNull(clientFolderOperationResult.getEntity().getVersion());
    }

    @Test
    public void should_search_resources_with_parameters() {

        // When

        OperationResult<ClientResourceListWrapper> result = session
                .resourcesService()
                .resources()
                .parameter(ResourceSearchParameter.FOLDER_URI, "/public")
                .parameter(ResourceSearchParameter.LIMIT, "10")
                .search();
        ClientResourceListWrapper resourceListWrapper = result.getEntity();

        // Then

        assertNotNull(resourceListWrapper);
        assertTrue(resourceListWrapper.getResourceLookups().size() > 0);
    }

    @Test
    public void should_return_domain_as_resource_client_domain() {

        // When
        testDomainUri = "/public/Samples/Domains/supermartDomain";
        OperationResult<ClientDomain> result = session
                .resourcesService()
                .resource(testDomainUri)
                .detailsForType(ClientDomain.class);

        // Then
        assertNotNull(result.getEntity());
        testDomain = result.getEntity();
    }

    @Test
    public void should_return_domain_as_resource_semanticLayerDatasource() {

        // When
        OperationResult<ClientSemanticLayerDataSource> result = session
                .resourcesService()
                .resource("/public/Samples/Domains/supermartDomain")
                .detailsForType(ClientSemanticLayerDataSource.class);

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
    public void should_return_file_resource_domain_details() throws InterruptedException {

        // When
        ClientResource clientResource = session.resourcesService()
                .resource("/organizations/organization_1/Domains/Simple_Domain")
                .details()
                .getEntity();

        Assert.assertNotNull(clientResource);
        Assert.assertNotNull(clientResource.getCreationDate());
    }

    @Test
    public void should_return_resource_folder_details() throws InterruptedException {

        // When
        ClientResource clientResource = session.resourcesService()
                .resource("/public")
                .details()
                .getEntity();

        Assert.assertNotNull(clientResource);
        Assert.assertNotNull(clientResource.getCreationDate());
    }


    @Test
    public void should_download_binary_content() throws InterruptedException {

        // When
        OperationResult<ClientSemanticLayerDataSource> operationResult = session
                .resourcesService()
                .resource("/public/Samples/Domains/supermartDomain")
                .detailsForType(ClientSemanticLayerDataSource.class);
        final ClientSemanticLayerDataSource domain = operationResult.getEntity();

        InputStream schemaXmlResult = session
                .resourcesService()
                .resource(domain.getSchema().getUri())
                .downloadBinary()
                .getEntity();

        Assert.assertNotNull(schemaXmlResult);
    }


    @Test(dependsOnMethods = "should_create_image_as_file_resource_with_stream")
    public void should_return_resource_file_details() throws InterruptedException {
        // When
        ClientResource clientResource = session.resourcesService()
                .resource("/public/testImage.gpg")
                .details()
                .getEntity();

        Assert.assertNotNull(clientResource);
        Assert.assertNotNull(clientResource.getCreationDate());

    }

    // CREATE section

    @Test
    public void should_create_folder_as_resource_with_descriptor_post() {
        ClientFolder folder = new ClientFolder();
        folder
                .setLabel("testFolder")
                .setDescription("Test folder");

        final OperationResult<ClientResource> operationResult = session
                .resourcesService()
                .resource(folder)
                .inFolder("/public")
                .create();

        final ClientResource entity = operationResult.getEntity();
        assertNotNull(entity);
    }
    @Test
    public void should_create_folder_as_resource_with_descriptor_put() {
        ClientFolder folder = new ClientFolder();
        folder
                .setUri("/public/testFolder1")
                .setLabel("testFolder1")
                .setDescription("Test folder")
                .setVersion(-1);

        final OperationResult<ClientResource> operationResult = session
                .resourcesService()
                .resource(folder.getUri())
                .createOrUpdate(folder);

        final ClientResource entity = operationResult.getEntity();
        assertNotNull(entity);
    }

    @Test(dependsOnMethods = "should_return_domain_as_resource_client_domain")
    public void should_create_domain_as_file_resource_with_descriptor() {

        // When
        testDomain.setSecurityFile(null);
        testDomain.setBundles(null);
        OperationResult<ClientResource> result = session
                .resourcesService()
                .resource(testDomain)
                .parameter(com.jaspersoft.jasperserver.jaxrs.client.apiadapters.resources.util.ResourceServiceParameter.CREATE_FOLDERS, true)
                .inFolder("/public")
                .create();

        // Then
        assertNotNull(result.getEntity());
        testResourceUri = result.getEntity().getUri();
    }

    @Test
    public void should_create_image_as_file_resource_with_stream() throws FileNotFoundException {

        // When

        OperationResult<ClientFile> result = session
                .resourcesService()
                .fileResource(new FileInputStream("D:\\workspaceIdea\\jrs-rest-java-client-tests\\image.jpeg"),
                        new ClientFile()
                                .setLabel("testImage.jpg")
                                .setDescription("test description")
                                .setType(ClientFile.FileType.img))
                .asInputStream()
                .toFolder("/public")
                .upload();

        // Then

        assertNotNull(result.getEntity());
        fileAsStrreamUploaded = result.getEntity();
    }

    @Test
    public void should_create_image_as_file_resource_with_base64Content() throws FileNotFoundException {

        // When


        final ClientFile resourceDescriptor = new ClientFile().setLabel("testImage1.jpg")
                .setDescription("test description")
                .setType(ClientFile.FileType.img);

        OperationResult<ClientFile> result = session
                .resourcesService()
                .fileResource(new FileInputStream("D:\\workspaceIdea\\jrs-rest-java-client-tests\\image.jpeg"),
                        resourceDescriptor)
                .asBase64EncodedContent()
                .toFolder("/public")
                .upload();

        // Then
        assertNotNull(result.getEntity());
        fileAsBase64Uploaded = result.getEntity();
    }

    @Test
    public void should_create_image_as_file_resource_with_multipart() throws FileNotFoundException {

        // When
        final ClientFile resourceDescriptor = new ClientFile()
                .setLabel("testImage2.jpg")
                .setDescription("test description")
                .setType(ClientFile.FileType.img);
        OperationResult<ClientFile> result = session
                .resourcesService()
                .fileResource(new FileInputStream("D:\\workspaceIdea\\jrs-rest-java-client-tests\\image.jpeg"),
                        resourceDescriptor)
                .asMultipartForm()
                .toFolder("/public")
                .upload();

        // Then
        assertNotNull(result.getEntity());
        fileAsMultipartUploaded = result.getEntity();
    }

    @Test
    public void should_create_image_as_file_resource_with_multipart_predefined_name() {

        // When

        OperationResult<ClientFile> result = null;
        try {
            result = session
                    .resourcesService()
                    .fileResource(new FileInputStream("D:\\workspaceIdea\\jrs-rest-java-client-tests\\image.jpeg"),
                            new ClientFile()
                                    .setLabel("testImage2.jpg")
                                    .setDescription("test description")
                                    .setType(ClientFile.FileType.img))
                    .asMultipartForm()
                    .toFolder("/public")
                    .withName("testImageName")
                    .upload();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Then
        assertNotNull(result.getEntity());
        assertEquals(result.getEntity().getUri(), "/public/testImageName");
    }


    // UPDATE section

    @Test(dependsOnMethods = "should_create_domain_as_file_resource_with_descriptor")
    public void should_update_domain_as_file_resource_with_descriptor() {
        // When

        OperationResult<ClientResource> result = session
                .resourcesService()
                .resource(testResourceUri)
                .createOrUpdate(testDomain.setLabel("New test label").setDescription("new test description"));

        // Then

        assertNotNull(result.getEntity());
        assertEquals(result.getEntity().getLabel(), "New test label");
    }

    @Test(dependsOnMethods = "should_create_folder_as_resource_with_descriptor")
    public void should_copy_resource_to_folder() throws InterruptedException {

        // When
        OperationResult<ClientResource> clientResource = session.resourcesService()
                .resource(testDomainUri)
                .toFolder("/public/testFolder")
                .parameter(ResourceServiceParameter.CREATE_FOLDERS, true)
                .copy();

        Assert.assertNotNull(clientResource);
    }

    @Test(dependsOnMethods = "should_create_image_as_file_resource_with_stream")
    public void should_move_resource_to_folder() throws InterruptedException {

        // When
        OperationResult<ClientResource> clientResource = session.resourcesService()
                .resource("/public/testImage.jpg")
                .toFolder("/public/testFolder")
                .move();

        Assert.assertNotNull(clientResource);
    }

// UPDATE section

    @Test(dependsOnMethods = "should_create_folder_as_resource_with_descriptor_put")
    public void should_update_folder_with_stream() {
        final ClientFolder folder = session
                .resourcesService()
                .resource("/public/testFolder1")
                .get(ClientFolder.class)
                .getEntity();
        final ArrayList<PatchItem> patchItems = new ArrayList<PatchItem>() {{
            add(new PatchItem().setField("label").setValue(folder.getLabel() + "_new"));
        }};
        OperationResult<ClientFolder> patchOperationResult = session
                .resourcesService()
                .resource(folder.getUri())
                .patchResource(ClientFolder.class, new PatchDescriptor().setVersion(folder.getVersion()).setItems(patchItems));

        assertNotNull(patchOperationResult.getEntity());
        assertEquals(patchOperationResult.getEntity().getLabel(), folder.getLabel()+"_new");
    }

    @Test(dependsOnMethods = "should_create_image_as_file_resource_with_stream")
    public void should_update_image_as_file_resource_with_stream() {
        fileAsStrreamUploaded.setDescription(NEW_TEST_DESCRIPTION);
        // When

        OperationResult<ClientFile> result = null;
        try {
            result = session
                    .resourcesService()
                    .fileResource(fileAsStrreamUploaded.getUri())
                    .asInputStream()
                    .createOrUpdate(new FileInputStream("D:\\workspaceIdea\\jrs-rest-java-client-tests\\image.jpeg"),
                            fileAsStrreamUploaded);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Then
        assertNotNull(result.getEntity());
        assertEquals(result.getEntity().getDescription(), NEW_TEST_DESCRIPTION);
    }

    @Test(dependsOnMethods = "should_create_image_as_file_resource_with_base64Content")
    public void should_update_image_as_file_resource_with_base64Content() {
        fileAsBase64Uploaded.setDescription(NEW_TEST_DESCRIPTION);
        // When

        OperationResult<ClientFile> result = null;
        try {
            result = session
                    .resourcesService()
                    .fileResource(fileAsBase64Uploaded.getUri())
                    .asBase64EncodedContent()
                    .createOrUpdate(new FileInputStream("D:\\workspaceIdea\\jrs-rest-java-client-tests\\image1.jpeg"),
                            fileAsBase64Uploaded);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Then
        assertNotNull(result.getEntity());
    }

    @Test(dependsOnMethods = "should_create_image_as_file_resource_with_multipart")
    public void should_update_image_as_file_resource_with_multipart() {
fileAsMultipartUploaded.setDescription(NEW_TEST_DESCRIPTION);
        // When
        OperationResult<ClientFile> result = null;
        try {
            result = session
                    .resourcesService()
                    .fileResource(fileAsMultipartUploaded.getUri())
                    .asMultipartForm()
                    .createOrUpdate(new FileInputStream("D:\\workspaceIdea\\jrs-rest-java-client-tests\\image.jpeg"),
                            fileAsMultipartUploaded);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Then
        assertNotNull(result.getEntity());
    }

    // DELETE section

    @Test(dependsOnMethods = "should_move_resource_to_folder")
    public void should_delete_resources() throws InterruptedException {

        // When
        Response resp = session.resourcesService()
                .resources()
                .parameter(ResourceSearchParameter.RESOURCE_URI, "/public/testFolder/supermartDomain")
                .parameter(ResourceSearchParameter.RESOURCE_URI, "/public/testFolder/testImage.jpg")
                .delete()
                .getResponse();

        // Then
        ByteArrayInputStream is = (ByteArrayInputStream) resp.getEntity();

        assertEquals(resp.getStatus(), 204);
        Assert.assertNotNull(is);
    }

    @Test(dependsOnMethods = "should_delete_resources")
    public void should_delete_resource_folder() throws InterruptedException {

        // When
        Response resp = session.resourcesService()
                .resource("/public/testFolder")
                .delete()
                .getResponse();

        // Then
        ByteArrayInputStream is = (ByteArrayInputStream) resp.getEntity();

        assertEquals(resp.getStatus(), 204);
        Assert.assertNotNull(is);
    }

    @Deprecated
    @Test
    public void should_create_resource() {
        ClientFolder folder = new ClientFolder();
        folder
                .setUri("/public/testFolder")
                .setLabel("testFolder")
                .setDescription("Test folder")
                .setVersion(0);

        final OperationResult<ClientResource> operationResult = session
                .resourcesService()
                .resource(folder)
                .create();

        final ClientResource entity = operationResult.getEntity();
        assertNotNull(entity);
    }

    @Deprecated
    @Test
    public void should_create_resource_as_binaryData() throws IOException {

        byte[] encoded = Files.readAllBytes(Paths.get("D:\\workspaceIdea\\jrs-rest-java-client-tests\\image1.jpeg"));
        String content = new String(encoded, StandardCharsets.UTF_8);

        ClientFile file = new ClientFile();
        file.
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

    @Deprecated
    @Test
    public void should_create_domain_as_resource() {

        // When

        OperationResult<ClientResource> result = session
                .resourcesService()
                .resource("/public")
                .createNew(testDomain);

        // Then

        assertNotNull(result.getEntity());
    }

    @Deprecated
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

    @Deprecated
    @Test
    public void should_upload_resource_as_multipart() throws FileNotFoundException {

        OperationResult<ClientFile> result = session
                .resourcesService()
                .resource("/public")
                .uploadFile(new File("D:\\workspaceIdea\\jrs-rest-java-client-tests\\image1.jpeg"), ClientFile.FileType.img, "fileName", "fileDescription");

        assertNotNull(result);
    }

    @Deprecated
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