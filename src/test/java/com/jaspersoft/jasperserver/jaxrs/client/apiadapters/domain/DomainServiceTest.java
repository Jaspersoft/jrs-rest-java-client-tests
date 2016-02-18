package com.jaspersoft.jasperserver.jaxrs.client.apiadapters.domain;

import com.jaspersoft.jasperserver.dto.resources.ClientFolder;
import com.jaspersoft.jasperserver.dto.resources.ClientResource;
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
    public static final Logger CONSOLE_LOGGER = Logger.getLogger("consoleLogger");
    public static final Logger TEST_LOGGER = Logger.getLogger(DomainServiceTest.class.getName());

    @BeforeGroups(groups = {"domains"})
    public void before() {
        initClient();
        initSession();
        session.getStorage().getConfiguration().setHandleErrors(false);
        TEST_LOGGER.debug("Start to create test folders on server");
        createTestResource();
        TEST_LOGGER.debug("Test folders were created successfully");
        /*
        * Upload source data to server
        * */
        TEST_LOGGER.debug("Start to load test resources to server");
        try {
            loadTestResources(RESOURCES_LOCAL_FOLDER);
            TEST_LOGGER.debug("Test resources was loaded successfully");
        } catch (Exception e) {
            TEST_LOGGER.debug("Test resources were not loaded resources of exception", e);
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
                if (CONSOLE_LOGGER.isDebugEnabled()) {
                    CONSOLE_LOGGER.debug(resourceLookup.getUri() + " domain failed with message " + result);
                } else {
                    CONSOLE_LOGGER.info(resourceLookup.getUri() + " domain failed");
                }
            }
        }
        assertTrue(resultMap.size() == 0);
    }

    private String executeTest(ClientResourceLookup clientResourceLookup) throws URISyntaxException, InterruptedException {
        TEST_LOGGER.debug("Start to test " + clientResourceLookup.getUri());
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
            TEST_LOGGER.debug("GET " + clientResourceLookup.getUri() + " domain from server was done successfully");
        } catch (JSClientWebException e) {
            TEST_LOGGER.info("GET " + clientResourceLookup.getUri()
                    + " from server failed with error code "
                    + operationResult.getResponseStatus());
            if (TEST_LOGGER.isTraceEnabled())
                TEST_LOGGER.trace(operationResult.getSerializedContent());
            TEST_LOGGER.info("COPY of " + clientResourceLookup.getUri() + " skipped");
            TEST_LOGGER.info("GET copy of " + clientResourceLookup.getUri() + " skipped");
            TEST_LOGGER.info("Comparison of original and copied " + clientResourceLookup.getLabel() + " skipped");
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
            TEST_LOGGER.debug("COPY of " + clientResourceLookup.getUri() + " domain to server was done successfully");
        } catch (Exception e) {
            TEST_LOGGER.info("COPY of "
                    + clientResourceLookup.getUri()
                    + " to server failed with error code "
                    + operationResult.getResponseStatus());
            if (TEST_LOGGER.isTraceEnabled())
                TEST_LOGGER.trace(operationResult.getSerializedContent());
            TEST_LOGGER.info("GET copy of " + clientResourceLookup.getUri() + " skipped");
            TEST_LOGGER.info("Comparison of original and copied " + domain.getLabel() + " skipped");
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
            TEST_LOGGER.debug("GET copy of domain from server" + clientResourceLookup.getUri() + "was done successfully");
        } catch (JSClientWebException e) {
            TEST_LOGGER.info("GET copy of " + clientResourceLookup.getUri()
                    + " from server failed with error code "
                    + operationResult.getResponseStatus());
            if (TEST_LOGGER.isTraceEnabled())
                TEST_LOGGER.trace(operationResult.getSerializedContent());
            TEST_LOGGER.info("Comparison of original and copied " + domain.getLabel() + " skipped");
            return operationResult.getSerializedContent();
        }
        domain.setCreationDate(null);
        domain.setUpdateDate(null);
        domain.setUri(null);

        retrievedDomain.setCreationDate(null);
        retrievedDomain.setUpdateDate(null);
        retrievedDomain.setUri(null);

        TEST_LOGGER.debug("Comparison of original and copied domains");
        if (domain.equals(retrievedDomain)) {
            TEST_LOGGER.debug("Comparison of original and copied " + domain.getLabel() + " passed");
            return null;
        } else {
            TEST_LOGGER.info("Comparison of original and copied " + domain.getLabel() + " filed");
            return "Domains are not equal";
        }
    }

    private void loadTestResources(String folderName) throws URISyntaxException, InterruptedException {
        TEST_LOGGER.debug("Start to scan import local folder");
        File folder = new File(folderName);
        File[] listOfResources = folder.listFiles();
        if (listOfResources.length > 0) {
            TEST_LOGGER.debug("For upload were founded " + listOfResources.length + "resources");
        } else {
            TEST_LOGGER.debug("Resources were not founded");
            return;
        }
        if (listOfResources.length > 0) {
            for (File resource : listOfResources) {
                OperationResult<StateDto> operationResult = session
                        .importService()
                        .newTask()
                        .parameter(ImportParameter.INCLUDE_ACCESS_EVENTS, true)
                        .create(resource);

                StateDto stateDto = operationResult.getEntity();

                while (stateDto.getPhase().equals(INPROGRESS_STATUS)) {
                    stateDto = session
                            .importService()
                            .task(stateDto.getId())
                            .state().getEntity();
                    Thread.sleep(100);
                }
                TEST_LOGGER.debug(resource.getName() + " was uploaded successfully");
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

        OperationResult<ClientResource> operationResult = session
                .resourcesService()
                .resource(folder.getUri())
                .createOrUpdate(folder);
        if (operationResult.getResponse().getStatus() == 200) {
            TEST_LOGGER.debug("Test folder " + DESTINATION_COPY_URI + " was created successfully");
        }

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
