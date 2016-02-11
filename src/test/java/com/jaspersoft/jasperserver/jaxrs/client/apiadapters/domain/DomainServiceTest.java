package com.jaspersoft.jasperserver.jaxrs.client.apiadapters.domain;

import com.jaspersoft.jasperserver.dto.resources.domain.ClientDomain;
import com.jaspersoft.jasperserver.jaxrs.client.RestClientTestUtil;
import com.jaspersoft.jasperserver.jaxrs.client.core.operationresult.OperationResult;
import org.testng.annotations.AfterGroups;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

/**
 * @author Tetiana Iefimenko
 */
public class DomainServiceTest extends RestClientTestUtil{

    public static final String DESTINATION_URI = "/temp";

    private static final  String SIMPLE_DOMAIN_URI = "/organizations/organization_1/Domains/Simple_Domain";
    private static final  String VIRTUAL_DS_DOMAIN_URI = "/organizations/organization_1/Domains/virtualDSDomain";
    private static final  String SUPERMART_DOMAIN_URI = "/public/Samples/Domains/supermartDomain";
    private static final  String RELATIVE_DOMAIN_URI = "/organizations/organization_1/Domains/Relative_Dates_domain";

    @BeforeGroups(groups = {"domains"})
    public void before() {
        initClient();
        initSession();
    }

    @Test(groups = {"domains"})
    public void should_get_cloned_simple_domain() {
        assertTrue(runTest(SIMPLE_DOMAIN_URI, DESTINATION_URI));
    }

    @Test(groups = {"domains"})
    public void should_get_cloned_supermart_domain() {
        assertTrue(runTest(SUPERMART_DOMAIN_URI, DESTINATION_URI));
    }

    @Test(groups = {"domains"})
    public void should_get_cloned_virtual_ds_domain() {
        assertTrue(runTest(VIRTUAL_DS_DOMAIN_URI, DESTINATION_URI));
    }

    @Test(groups = {"domains"})
    public void should_get_cloned_relative_dates_domain() {
        assertTrue(runTest(RELATIVE_DOMAIN_URI, DESTINATION_URI));
    }

    private Boolean runTest(String domainUri, String destinationUri) {
        ClientDomain domain = session
                .domainService()
                .domain(domainUri)
                .get()
                .getEntity();

        domain.setSecurityFile(null);
        domain.setBundles(null);

        ClientDomain cloneOfDomain = new ClientDomain(domain);

        OperationResult<ClientDomain> operationResult = session
                .domainService()
                .domain(destinationUri)
                .create(cloneOfDomain);


        String uri = completeClonedDomainUri(cloneOfDomain.getLabel(), destinationUri);

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


    @AfterGroups(groups = {"domains"})
    public  void after() {
     session.logout();
    }
}
