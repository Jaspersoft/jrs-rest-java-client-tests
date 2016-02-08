package com.jaspersoft.jasperserver.jaxrs.client.apiadapters.domain;

import com.jaspersoft.jasperserver.dto.domain.ClientSimpleDomain;
import com.jaspersoft.jasperserver.jaxrs.client.RestClientTestUtil;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;

/**
 * @author Tetiana Iefimenko
 */
public class DomainServiceTes extends RestClientTestUtil{

    private ClientSimpleDomain simpleDomain = null;
    private ClientSimpleDomain virtualDomain = null;
    private ClientSimpleDomain supermartDomain = null;
    private ClientSimpleDomain relativeDatesDomain = null;
    private final  String SIMPLE_DOMAIN_URI = "/temp/DomainsCopy/Simple_Domain";
    private final  String VIRTUAL_DS_DOMAIN_URI = "/temp/DomainsCopy/virtualDSDomain";
    private final  String SUPERMART_DOMAIN_URI = "/temp/DomainsCopy/supermartDomain";
    private final  String RELATIVE_DOMAIN_URI = "/temp/DomainsCopy/Relative_Dates_domain";

    @BeforeClass
    public void before() {
        initClient();
        initSession();
    }

    @Test
    public void should_get_simple_domain() {
        simpleDomain = session
                .domainService()
                .domain(SIMPLE_DOMAIN_URI)
                .get()
                .getEntity();


        assertNotNull(simpleDomain);
    }

    @Test
    public void should_get_virtual_ds_domain() {
        virtualDomain = session
                .domainService()
                .domain(VIRTUAL_DS_DOMAIN_URI)
                .get()
                .getEntity();


        assertNotNull(virtualDomain);
    }


    @Test
    public void should_get_supermart_domain() {
        supermartDomain = session
                .domainService()
                .domain(SUPERMART_DOMAIN_URI)
                .get()
                .getEntity();


        assertNotNull(supermartDomain);
    }


    @Test
    public void should_get_relative_dates_domain() {
        relativeDatesDomain = session
                .domainService()
                .domain(RELATIVE_DOMAIN_URI)
                .get()
                .getEntity();


        assertNotNull(relativeDatesDomain);
    }

    @Test(dependsOnMethods = "should_get_simple_domain")
    public void should_create_simple_domain() {
        ClientSimpleDomain newSimpleDomain = new ClientSimpleDomain(this.simpleDomain);
        ClientSimpleDomain domain = session
                .domainService()
                .domain("/temp/DomainsRestCopies")
                .create(newSimpleDomain)
                .getEntity();

        assertNotNull(domain);
    }

    @Test(dependsOnMethods = "should_get_virtual_ds_domain")
    public void should_create_virtual_ds__domain() {
        ClientSimpleDomain newVirtualDsDomain = new ClientSimpleDomain(this.virtualDomain);
        ClientSimpleDomain domain = session
                .domainService()
                .domain("/temp/DomainsRestCopies")
                .create(newVirtualDsDomain)
                .getEntity();

        assertNotNull(domain);
    }

    @Test(dependsOnMethods = "should_get_supermart_domain")
    public void should_create_supermart_domain() {
        ClientSimpleDomain newSupermartDomain = new ClientSimpleDomain(this.supermartDomain);
        ClientSimpleDomain domain = session
                .domainService()
                .domain("/temp/DomainsRestCopies")
                .create(newSupermartDomain)
                .getEntity();

        assertNotNull(domain);
    }


    @Test(dependsOnMethods = "should_get_relative_dates_domain")
    public void should_create_relative_dates_domain() {
        ClientSimpleDomain newRelativeDatesDomain = new ClientSimpleDomain(this.relativeDatesDomain);
        ClientSimpleDomain domain = session
                .domainService()
                .domain("/temp/DomainsRestCopies")
                .create(newRelativeDatesDomain)
                .getEntity();

        assertNotNull(domain);
    }

    @Test(dependsOnMethods = "should_get_domain", enabled = false)
    public void should_update_domain() {
        this.simpleDomain.setDescription("New simple domain");
        ClientSimpleDomain domain = session
                .domainService()
                .domain(SIMPLE_DOMAIN_URI)
                .update(this.simpleDomain)
                .getEntity();


        assertNotNull(domain);
    }


    @Test(dependsOnMethods = "should_get_domain", enabled = false)
    public void should_create__domain() {
        ClientSimpleDomain simpleDomain = new ClientSimpleDomain(this.simpleDomain);
        simpleDomain.setUri("/public/new_simple_domain");
        simpleDomain.setLabel("New simple domain");
        ClientSimpleDomain domain = session
                .domainService()
                .domain("/public")
                .create(simpleDomain)
                .getEntity();

        assertNotNull(domain);
    }


    @AfterClass
    public  void after() {
        session.logout();
    }
}
