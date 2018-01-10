package com.jaspersoft.jasperserver.jaxrs.client.apiadapters.resources;

import com.jaspersoft.jasperserver.dto.resources.ClientSecureMondrianConnection;
import com.jaspersoft.jasperserver.jaxrs.client.RestClientTestUtil;
import com.jaspersoft.jasperserver.jaxrs.client.core.operationresult.OperationResult;
import java.io.File;
import java.io.FileNotFoundException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;

/**
 * @author Alexander Krasnyanskiy
 * @author tetiana Iefimenko
 */
public class ResourcesSecureMondranConnectionServiceTest extends RestClientTestUtil {


    @BeforeClass
    public void before() {
        initClient();
        initSession();
    }

    @Test
    public void should_upload_secureMondrianConnection() throws FileNotFoundException {

        OperationResult<ClientSecureMondrianConnection> repUnut =
                session.resourcesService()
                        .secureMondrianConnection()
                        .withMondrianSchema(new File("report_upload_resources/foodmartMondrioanConnection/foodmartMondrioanConnectionSchema.xml"), "schema", "schema")
                        .withAccessGrantSchema(new File("report_upload_resources/foodmartMondrioanConnection/FoodmartNoTopGrant.xml"), "FoodmartNoTopGrant.xml", "FoodmartNoTopGrant")
                        .withDataSource("/organizations/organization_1/datasources/JServerJNDIDS")
                        .withLabel("testMondrianConnection")
                        .withDescription("testDescription")
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