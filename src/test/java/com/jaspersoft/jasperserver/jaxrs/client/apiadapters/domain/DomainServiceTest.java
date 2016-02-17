package com.jaspersoft.jasperserver.jaxrs.client.apiadapters.domain;

import com.jaspersoft.jasperserver.dto.resources.ClientFolder;
import com.jaspersoft.jasperserver.dto.resources.ClientResourceListWrapper;
import com.jaspersoft.jasperserver.dto.resources.ClientResourceLookup;
import com.jaspersoft.jasperserver.dto.resources.ResourceMediaType;
import com.jaspersoft.jasperserver.dto.resources.domain.ClientDomain;
import com.jaspersoft.jasperserver.jaxrs.client.RestClientTestUtil;
import com.jaspersoft.jasperserver.jaxrs.client.apiadapters.importexport.importservice.ImportParameter;
import com.jaspersoft.jasperserver.jaxrs.client.apiadapters.resources.ResourceSearchParameter;
import com.jaspersoft.jasperserver.jaxrs.client.core.exceptions.JSClientWebException;
import com.jaspersoft.jasperserver.jaxrs.client.core.operationresult.OperationResult;
import com.jaspersoft.jasperserver.jaxrs.client.dto.importexport.StateDto;
import java.io.File;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

/**
 * @author Tetiana Iefimenko
 */
public class DomainServiceTest extends RestClientTestUtil {

    private static final String RESOURCES_LOCAL_FOLDER = "D:\\workspaceIdea\\jrs-rest-java-client-tests\\src\\main\\resources\\imports\\domains";
    private static final String EXPORT_SERVER_URI = "/temp/exportResources";
    private static final String DESTINATION_COPY_URI = "/temp/DomainsRestCopies";
    private static final String DESTINATION_COPY_LABEL = "DomainsRestCopies";

    private static final String INPROGRESS_STATUS = "inprogress";
    private static final String NEW_LINE_CHARS = "\n\n";
    public static final Logger LOGGER = Logger.getLogger("consoleLogger");

    @BeforeGroups(groups = {"domains"})
    public void before() {
        initClient();
        initSession();
        session.getStorage().getConfiguration().setHandleErrors(false);

        createTestResource();

        /*
        * Upload source data to server
        * */
        try {
            loadTestResources(RESOURCES_LOCAL_FOLDER);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test(groups = {"domains"})
    public void should_get_copy_and_compare_domains() throws URISyntaxException, InterruptedException {

        /*
        * Get all domains as resources from server
        * */

        ClientResourceListWrapper list = session
                .resourcesService()
                .resources()
                .parameter(ResourceSearchParameter.FOLDER_URI, EXPORT_SERVER_URI)
                .parameter(ResourceSearchParameter.TYPE, ResourceMediaType.SEMANTIC_LAYER_DATA_SOURCE_CLIENT_TYPE)
                .search()
                .getEntity();

        Map<String, String> resultMap = new HashMap<String, String>(list.getResourceLookups().size());
        for (ClientResourceLookup resourceLookup : list.getResourceLookups()) {

            /*
            * Each resource get as domain, clone, post to server, get posted clone and compare with uploaded domain
            * */
            String result = executeTest(resourceLookup);
            if (result != null) {
                resultMap.put(resourceLookup.getUri(), result);
                if (resultMap.size() != 0) {
                    if (LOGGER.getLevel().equals(Level.DEBUG)) {
                        LOGGER.debug(NEW_LINE_CHARS + resourceLookup.getUri() + " : " + result);
                    } else {
                        LOGGER.info(NEW_LINE_CHARS + resourceLookup.getUri());
                    }
                }
            }
        }

        assertTrue(resultMap.size() == 0);
    }

    private String executeTest(ClientResourceLookup clientResourceLookup) throws URISyntaxException, InterruptedException {

            /*
            * Get domain form server
            * */
        ClientDomain domain;
        OperationResult<ClientDomain> operationResult = session
                .domainService()
                .domain(clientResourceLookup.getUri())
                .get();
        try {
            domain = operationResult.getEntity();
        } catch (JSClientWebException e) {
            return operationResult.getSerializedContent();
        }
        domain.setSecurityFile(null);
        domain.setBundles(null);

        /*
        *
        * Clone domain*/
        ClientDomain clonedDomain = new ClientDomain(domain);

        /*
        * Post domain to server
        * */

        String newUri = clientResourceLookup.getUri().replace(EXPORT_SERVER_URI, DESTINATION_COPY_URI);
        try {
            operationResult = session
                    .domainService()
                    .domain(newUri)
                    .update(clonedDomain);
            operationResult.getEntity();
        } catch (Exception e) {
            return operationResult.getSerializedContent();
        }
        /*
        * Get cloned domain from server
        * */

        ClientDomain retrievedDomain;

        operationResult = session
                .domainService()
                .domain(newUri)
                .get();
        try {
            retrievedDomain = operationResult.getEntity();
        } catch (JSClientWebException e) {
            return operationResult.getSerializedContent();
        }
        domain.setCreationDate(null);
        domain.setUpdateDate(null);
        domain.setUri(null);

        retrievedDomain.setCreationDate(null);
        retrievedDomain.setUpdateDate(null);
        retrievedDomain.setUri(null);

        return (domain.equals(retrievedDomain)) ? null : "Domains are not equal";
    }

    private void loadTestResources(String folderName) throws URISyntaxException, InterruptedException {

        File folder = new File(folderName);
        File[] listOfResources = folder.listFiles();
        if (listOfResources.length > 0) {
            for (File listOfResource : listOfResources) {
                OperationResult<StateDto> operationResult = session
                        .importService()
                        .newTask()
                        .parameter(ImportParameter.INCLUDE_ACCESS_EVENTS, true)
                        .create(listOfResource);

                StateDto stateDto = operationResult.getEntity();

                while (stateDto.getPhase().equals(INPROGRESS_STATUS)) {
                    stateDto = session
                            .importService()
                            .task(stateDto.getId())
                            .state().getEntity();
                    Thread.sleep(100);
                }
            }
        }
    }

    private void createTestResource() {

        ClientFolder folder = new ClientFolder();
        folder
                .setUri(DESTINATION_COPY_URI)
                .setLabel(DESTINATION_COPY_LABEL)
                .setDescription("Test folder")
                .setVersion(0);

        session
                .resourcesService()
                .resource(folder.getUri())
                .createOrUpdate(folder);

    }

    @AfterGroups(groups = {"domains"})
    public void after() {
        session
                .resourcesService()
                .resources()
                .parameter(ResourceSearchParameter.RESOURCE_URI, DESTINATION_COPY_URI)
                .parameter(ResourceSearchParameter.RESOURCE_URI, EXPORT_SERVER_URI)
                .delete();
        session.logout();
    }
}
