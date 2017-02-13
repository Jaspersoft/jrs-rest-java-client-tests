package com.jaspersoft.jasperserver.jaxrs.client.apiadapters.datadiscovery;

import com.jaspersoft.jasperserver.dto.resources.SqlExecutionRequest;
import com.jaspersoft.jasperserver.dto.resources.domain.PresentationGroupElement;
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
public class DerivedTableContextTest extends RestClientTestUtil {
    private SqlExecutionRequest sqlExecutionRequest;


    @BeforeClass
    public void before() {
        initClient();
        initSession();
        sqlExecutionRequest = new SqlExecutionRequest().
                setSql("select * from account").
                setDataSourceUri("/public/Samples/Data_Sources/FoodmartDataSource");

    }

    @AfterClass
    public void after() {
        session.logout();
    }

    @Test
    public void should_create_context_for_string_expression() {

        OperationResult<PresentationGroupElement> operationResult = session
                .dataDiscoveryService()
                .derivedTableContext()
                .execute(sqlExecutionRequest);

        assertNotNull(operationResult);
        assertEquals(Response.Status.OK.getStatusCode(), operationResult.getResponse().getStatus());
        assertNotNull(operationResult.getEntity());
    }


}
