package com.jaspersoft.jasperserver.jaxrs.client.apiadapters.resources;

import com.jaspersoft.jasperserver.dto.resources.ClientFolder;
import com.jaspersoft.jasperserver.dto.resources.ClientResource;
import com.jaspersoft.jasperserver.dto.resources.ClientResourceListWrapper;
import com.jaspersoft.jasperserver.jaxrs.client.RestClientTestUtil;
import com.jaspersoft.jasperserver.jaxrs.client.core.operationresult.OperationResult;
import java.io.ByteArrayInputStream;
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
        assertTrue(resourceListWrapper.getResourceLookups().size() <= 5 );
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
                .resource("/organizations/organization_1/Domains/supermartDomain")
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
                .copyFrom("/public/Simple_Domain_files/Simple_Domain_schema")
                .getEntity();

        Assert.assertNotNull(clientResource);
        Assert.assertNotNull(clientResource.getCreationDate());
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
////TODO
//    @Test
//    public void should_create_resource() throws InterruptedException {
//
//        ClientFolder folder = new ClientFolder();
//        folder
//                .setUri("/organizations/organization_1/AdditionalResourcesForTesting/Domains")
//                .setLabel("Test Domain")
//                .setDescription("Test domain description")
//                .setPermissionMask(0)
//                .setCreationDate("2014-01-24 16:27:47")
//                .setUpdateDate("2014-01-24 16:27:47")
//                .setVersion(0);
//
//        OperationResult<ClientResource> result = session
//                .resourcesService()
//                .resource(folder.getUri())
//                .createNew(folder);
//
//    }
//
//    //TODO
//    @Test
//    public void should_create_or_update_resource() throws InterruptedException {
//
//        ClientFolder folder = new ClientFolder();
//        folder
//                .setUri("/reports/testFolder")
//                .setLabel("Test Folder")
//                .setDescription("Test folder description")
//                .setPermissionMask(0)
//                .setCreationDate("2014-01-24 16:27:47")
//                .setUpdateDate("2014-01-24 16:27:47")
//                .setVersion(0);
//
//        OperationResult<ClientResource> result = client
//                .authenticate("jasperadmin", "jasperadmin")
//                .resourcesService()
//                .resource(folder.getUri())
//                .createOrUpdate(folder);
//
//
//    }



    @AfterClass
    public void after() {
        session.logout();
        session = null;
    }


    /*
    *Searching the Repository

        Downloading File Resources

There are two operations on file resources:

    Viewing the file resource details to determine the file format
    Downloading the binary file contents

To view the file resource details, specify the URL of the file in resource() method and use the code form Viewing Resource Details section. To download file binary content, specify the URL of the file in resource() method and use the code below

OperationResult<InputStream> result = client
        .authenticate("jasperadmin", "jasperadmin")
        .resourcesService()
        .resource("/themes/default/buttons.css")
        .downloadBinary();

InputStream inputStream = result.getEntity();

Creating a Resource

The createNew() and createOrUpdate() methods offer alternative ways to create resources. Both take a resource descriptor but each handles the URL differently. With the createNew() method, specify a folder in the URL, and the new resource ID is created automatically from the label attribute in its descriptor. With the createOrUpdate() method, specify a unique new resource ID as part of the URL in resource() method.

ClientFolder folder = new ClientFolder();
folder
        .setUri("/reports/testFolder")
        .setLabel("Test Folder")
        .setDescription("Test folder description")
        .setPermissionMask(0)
        .setCreationDate("2014-01-24 16:27:47")
        .setUpdateDate("2014-01-24 16:27:47")
        .setVersion(0);

OperationResult<ClientResource> result = client
        .authenticate("jasperadmin", "jasperadmin")
        .resourcesService()
        .resource(folder.getUri())
        .createOrUpdate(folder);
//OR
OperationResult<ClientResource> result = client
        .authenticate("jasperadmin", "jasperadmin")
        .resourcesService()
        .resource(folder.getUri())
        .createNew(folder);

        Modifying a Resource

Use the createOrUpdate() method above to overwrite an entire resource. Specify the path of the target resource in the resource() method and specify resource of the same type. Use parameter(ResourceServiceParameter.OVERWRITE, "true") to replace a resource of a different type. The resource descriptor must completely describe the updated resource, not use individual fields. The descriptor must also use only references for nested resources, not other resources expanded inline. You can update the local resources using the hidden folder _file. The patchResource() method updates individual descriptor fields on the target resource. It also accept expressions that modify the descriptor in the Spring Expression Language. This expression language lets you easily modify the structure and values of descriptors.

PatchDescriptor patchDescriptor = new PatchDescriptor();
patchDescriptor.setVersion(0);
patchDescriptor.field("label", "Patch Label");

OperationResult<ClientFolder> result = client
        .authenticate("jasperadmin", "jasperadmin")
        .resourcesService()
        .resource("/reports/testFolder")
        .patchResource(ClientFolder.class, patchDescriptor);

        Copying a Resource

To copy a resource, specify in copyFrom() method its URI and in resource() method URI of destination location.

OperationResult<ClientResource> result = client
        .authenticate("jasperadmin", "jasperadmin")
        .resourcesService()
        .resource("/reports")
        .copyFrom("/datasources/testFolder");



        Moving a Resource

To move a resource, specify in moveFrom() method its URI and in resource() method URI of destination location.

OperationResult<ClientResource> result = client
        .authenticate("jasperadmin", "jasperadmin")
        .resourcesService()
        .resource("/datasources")
        .moveFrom("/reports/testFolder");

        Uploading File Resources

To upload file you must specify the MIME type that corresponds with the desired file type, you can take it from ClientFile.FileType enumeration.

OperationResult<ClientFile> result = client
        .authenticate("jasperadmin", "jasperadmin")
        .resourcesService()
        .resource("/reports/testFolder")
        .uploadFile(imageFile, ClientFile.FileType.img, "fileName", "fileDescription");

        Uploading SemanticLayerDataSource

RestClient also supports a way to create complex resources and their nested resources in a single multipart request. One of such resources is SemanticLayerDataSource.

ClientSemanticLayerDataSource domainEntity = session
        .resourcesService()
            .resource(domain)
                .withBundle(defBundle, newDefaultBundle)
                .withBundle(enUSBundle, newEnUsBundle)
                .withSecurityFile(securityFile, securityFile)
                .withSchema(schemaFile, schema)
            .inFolder("/my/new/folder/")
                .create()
                    .entity();

                    Uploading MondrianConnection

REST Client allows you to create MondrianConnection Resource with mondrian schema XML file. You can specify the folder in which the resource will be placed. Provided API allows to add XML schema as String or InputStream.

ClientMondrianConnection connection = session
    .resourcesService()
        .resource(mondrianConnection)
            .withMondrianSchema(schema, schemaRef)
        .createInFolder("my/olap/folder")
            .entity();

            Uploading SecureMondrianConnection

To upload SecureMondrianConnection Resource with a bunch of support files such as Mondrian schema XML file and AccessGrantSchemas files you can use our new API

ClientSecureMondrianConnection entity = session.resourcesService()
    .resource(secureMondrianConnection)
        .withMondrianSchema(mondrianSchema)
        .withAccessGrantSchemas(Arrays.asList(accessGrantSchema))
    .createInFolder("/my/new/folder/")
        .entity();

        Uploading ReportUnit

To upload ReportUnit resource to the server you can use next API, which allows you to do it in a very simple way. You can add JRXML file and a bunch of various files like images and others as well.

ClientReportUnit entity = session.resourcesService()
    .resource(reportUnit)
        .withJrxml(file, descriptor)
        .withNewFile(imgFile, "myFile", imgDescriptor)
            .createInFolder("/my/new/folder/")
                .entity();

                Deleting Resources

You can delete resources in two ways, one for single resources and one for multiple resources. To delete multiple resources at once, specify multiple URIs with the ResourceSearchParameter.RESOURCE_URI parameter.

//multiple
OperationResult result = client
        .authenticate("jasperadmin", "jasperadmin")
        .resourcesService()
        .resources()
        .parameter(ResourceSearchParameter.RESOURCE_URI, "/some/resource/uri/1")
        .parameter(ResourceSearchParameter.RESOURCE_URI, "/some/resource/uri/2")
        .delete();
//OR
//single
OperationResult result = client
        .authenticate("jasperadmin", "jasperadmin")
        .resourcesService()
        .resource("/reports/testFolder")
        .delete();
    * */

}