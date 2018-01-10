package com.jaspersoft.jasperserver.jaxrs.client.apiadapters.resources;

import com.jaspersoft.jasperserver.dto.resources.ClientDomainTopic;
import com.jaspersoft.jasperserver.jaxrs.client.RestClientTestUtil;
import com.jaspersoft.jasperserver.jaxrs.client.core.operationresult.OperationResult;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;

/**
 * @author Alexander Krasnyanskiy
 * @author tetiana Iefimenko
 */
public class ResourcesDomainTopicServiceTest extends RestClientTestUtil {


    @BeforeClass
    public void before() {
        initClient();
        initSession();
    }

    @Test
    public void should_upload_domainTopic() throws FileNotFoundException {

        OperationResult<ClientDomainTopic> repUnut =
                session.resourcesService()
                        .domainTopicResource()
                        .withJrxml(new FileInputStream("report_upload_resources/ShippingReport.jrxml"), "ShippingReport.jrxml", "jrxml file")
                        .withLabel("testDomainTopic")
                        .withDescription("testDescription")
                        .withDataSource("/organizations/organization_1/datasources/JServerJNDIDS")
                        .inFolder("/public")
                        .create();

        assertNotNull(repUnut);
    }

    @AfterClass
    public void after() {
        session.logout();
        session = null;
    }

}