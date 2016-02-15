package com.jaspersoft.jasperserver.jaxrs.client.apiadapters.domain;

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
import java.util.ArrayList;
import java.util.List;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;


/**
 * @author Tetiana Iefimenko
 */
public class DomainServiceTest extends RestClientTestUtil {


    public static final String RESOURCES_LOCAL_FOLDER = "D:\\workspaceIdea\\jrs-rest-java-client-tests\\src\\main\\resources\\imports\\domains";
    public static final String EXPORT_SERVER_URI = "/temp/exportResources";
    public static final String DESTINATION_COPY_URI = "/temp/DomainsRestCopies";
    public static final String INPROGRESS_STATUS = "inprogress";

    @BeforeGroups(groups = {"domains"})
    public void before() {
        initClient();
        initSession();

        /*
        * Upload source data to server
        * */
        try {
            loadResources(RESOURCES_LOCAL_FOLDER);
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

        List<Boolean> resultList = new ArrayList<Boolean>(list.getResourceLookups().size());
        for (ClientResourceLookup resourceLookup : list.getResourceLookups()) {

            /*
            * Each resource get as domain, clone, post to server, get posted clone and compare with uploaded domain
            * */
            resultList.add(executeTest(resourceLookup));
        }
        assertTrue(!resultList.contains(Boolean.FALSE));
    }

    private Boolean executeTest(ClientResourceLookup clientResourceLookup) throws URISyntaxException, InterruptedException {

            /*
            * Get domain form server
            * */
        ClientDomain domain = session
                .domainService()
                .domain(clientResourceLookup.getUri())
                .get()
                .getEntity();

        domain.setSecurityFile(null);
        domain.setBundles(null);

        /*
        *
        * Clone domain*/
        ClientDomain clonedDomain = new ClientDomain(domain);

        /*
        * Post domain to server
        * */
        try {
            OperationResult<ClientDomain> operationResult = session
                    .domainService()
                    .domain(DESTINATION_COPY_URI)
                    .create(clonedDomain);
        } catch (JSClientWebException e) {
            return Boolean.FALSE;
        }


        String uri = completeClonedDomainUri(clonedDomain.getLabel(), DESTINATION_COPY_URI);

        /*
        * Get cloned domain from server
        * */

        ClientDomain retrievedDomain = session
                .domainService()
                .domain(uri)
                .get()
                .getEntity();

        domain.setCreationDate(null);
        domain.setUpdateDate(null);
        domain.setUri(null);

        retrievedDomain.setCreationDate(null);
        retrievedDomain.setUpdateDate(null);
        retrievedDomain.setUri(null);

        return domain.equals(retrievedDomain);
    }

    private String completeClonedDomainUri(String label, String destinationFolder) {
        return new StringBuilder(destinationFolder).append("/").append(label.replaceAll(" ", "_")).toString();
    }

    private void loadResources(String folderName) throws URISyntaxException, InterruptedException {

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


    @AfterGroups(groups = {"domains"})
    public void after() {
        session.logout();
    }
}
