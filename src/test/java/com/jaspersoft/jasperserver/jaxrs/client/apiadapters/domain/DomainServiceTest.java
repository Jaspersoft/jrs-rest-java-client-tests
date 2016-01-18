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
public class DomainServiceTest extends RestClientTestUtil{

    private ClientSimpleDomain domain;
    private final  String  TEST_DOMAIN_URI = "/public/Simple_Domain";

    @BeforeClass
    public void before() {
        initClient();
        initSession();
    }

    @Test
    public void should_get_domain() {
        domain = session
                .domainService()
                .domain(TEST_DOMAIN_URI)
                .get()
                .getEntity();


        assertNotNull(domain);
    }

    @Test(dependsOnMethods = "should_get_domain")
    public void should_update__domain() {
        this.domain.setDescription("New simple domain");
        ClientSimpleDomain domain = session
                .domainService()
                .domain(TEST_DOMAIN_URI)
                .update(this.domain)
                .getEntity();


        assertNotNull(domain);
    }

    @Test(dependsOnMethods = "should_get_domain")
    public void should_create__domain() {
        ClientSimpleDomain simpleDomain = new ClientSimpleDomain(domain);
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
