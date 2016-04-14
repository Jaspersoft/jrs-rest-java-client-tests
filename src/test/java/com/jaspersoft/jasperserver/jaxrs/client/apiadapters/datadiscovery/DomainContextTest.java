package com.jaspersoft.jasperserver.jaxrs.client.apiadapters.datadiscovery;

import com.jaspersoft.jasperserver.dto.resources.domain.ClientDomain;
import com.jaspersoft.jasperserver.dto.resources.domain.DataIslandsContainer;
import com.jaspersoft.jasperserver.jaxrs.client.RestClientTestUtil;
import com.jaspersoft.jasperserver.jaxrs.client.core.operationresult.OperationResult;
import javax.ws.rs.core.Response;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * <p/>
 * <p/>
 *
 * @author tetiana.iefimenko
 * @version $Id$
 * @see
 */
public class DomainContextTest extends RestClientTestUtil {
    private String uuId;
    private ClientDomain domainContext;

    @BeforeClass
    public void before() {
        initClient();
        initSession();
        domainContext = new ClientDomain().setUri("/organizations/organization_1/Domains/Simple_Domain");
    }

    @AfterClass
    public void after() {
        session.logout();
    }

    @Test
    public void should_create_context() {

        OperationResult<ClientDomain> operationResult = session
                .dataDiscoveryService()
                .domainContext()
                .create(domainContext);

        assertNotNull(operationResult);
        assertEquals(Response.Status.CREATED.getStatusCode(), operationResult.getResponse().getStatus());

        extractUuid(operationResult.getResponse().getHeaderString("Location"));
        assertNotNull(uuId);

    }

    @Test(dependsOnMethods = "should_create_context")
    public void should_get_metadata() {
        OperationResult<DataIslandsContainer> operationResult = session
                .dataDiscoveryService()
                .domainContext()
                .fetchMetadataById(uuId);
        assertNotNull(operationResult);
        assertEquals(Response.Status.OK.getStatusCode(), operationResult.getResponse().getStatus());
        assertNotNull(operationResult.getEntity());
    }

    @Test
    public void should_create_context_and_get_metadata() {
        OperationResult<DataIslandsContainer> operationResult = session
                .dataDiscoveryService()
                .domainContext()
                .fetchMetadataByContext(domainContext);

        assertNotNull(operationResult);
        assertEquals(Response.Status.OK.getStatusCode(), operationResult.getResponse().getStatus());
        assertNotNull(operationResult.getEntity());
    }

    private void extractUuid(String locationHeader) {
        if (locationHeader.endsWith("metadata")) {
            locationHeader = locationHeader.substring(0, locationHeader.lastIndexOf("/"));
        }

        uuId = locationHeader.substring(locationHeader.lastIndexOf("/") + 1);
    }

}
