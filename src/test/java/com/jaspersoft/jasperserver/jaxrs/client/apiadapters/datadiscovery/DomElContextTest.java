package com.jaspersoft.jasperserver.jaxrs.client.apiadapters.datadiscovery;

import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientExpressionContainer;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientVariable;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.ClientFunction;
import com.jaspersoft.jasperserver.dto.domain.DomElExpressionCollectionContext;
import com.jaspersoft.jasperserver.dto.domain.DomElExpressionContext;
import com.jaspersoft.jasperserver.dto.domain.DomElVariable;
import com.jaspersoft.jasperserver.jaxrs.client.RestClientTestUtil;
import com.jaspersoft.jasperserver.jaxrs.client.core.operationresult.OperationResult;
import javax.ws.rs.core.Response;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static java.util.Arrays.asList;
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
public class DomElContextTest extends RestClientTestUtil {
    private DomElExpressionContext expressionStringContext;
    private DomElExpressionContext expressionObjectContext;
    private DomElExpressionContext expressionConstant;
    private DomElExpressionContext expressionConstant1;

    @BeforeClass
    public void before() {
        initClient();
        initSession();
        expressionObjectContext = new DomElExpressionContext().
                setExpression(new ClientExpressionContainer().setObject(new ClientFunction().
                        setFunctionName("concat").addOperand(new ClientVariable("test")).addOperand(new ClientVariable("test1")).addOperand(new ClientVariable("b")))).
                setVariables(asList(new DomElVariable().setName("test").setType("java.lang.String"),
                        new DomElVariable().setName("test1").setType("java.lang.String"))).
                setResultType("java.lang.String");

        expressionStringContext = new DomElExpressionContext().
                setExpression(new ClientExpressionContainer().setString("concat(test, test1,'b')")).
                setVariables(asList(new DomElVariable().setName("test").setType("java.lang.String"),
                        new DomElVariable().setName("test1").setType("java.lang.String"))).
                setResultType("java.lang.String");

        expressionConstant = new DomElExpressionContext().
                setExpression(new ClientExpressionContainer().setString("constant == '21:12:36'")).
                setVariables(asList(new DomElVariable().setName("constant").setType("java.sql.Time"))).
                setResultType("java.lang.Boolean").
                setSkipTypeAndFunctionsValidation(Boolean.TRUE);

        expressionConstant1 = new DomElExpressionContext().
                setExpression(new ClientExpressionContainer().setString("anotherConstant == 'olala'")).
                setVariables(asList(new DomElVariable().setName("anotherConstant").setType("java.lang.String"))).
                setResultType("java.lang.Boolean").
                setSkipTypeAndFunctionsValidation(Boolean.TRUE);
    }

    @AfterClass
    public void after() {
        session.logout();
    }

    @Test
    public void should_create_context_for_string_expression() {

        OperationResult<DomElExpressionContext> operationResult = session
                .dataDiscoveryService()
                .domElContext()
                .create(expressionStringContext);

        assertNotNull(operationResult);
        assertEquals(Response.Status.CREATED.getStatusCode(), operationResult.getResponse().getStatus());
    }

    @Test
    public void should_create_context_for_object_expression() {

        OperationResult<DomElExpressionContext> operationResult = session
                .dataDiscoveryService()
                .domElContext()
                .create(expressionObjectContext);

        assertNotNull(operationResult);
        assertEquals(Response.Status.CREATED.getStatusCode(), operationResult.getResponse().getStatus());
    }

    @Test
    public void should_create_context_for_collection_expression() {

        OperationResult<DomElExpressionCollectionContext> operationResult = session
                .dataDiscoveryService()
                .domElContext()
                .create(new DomElExpressionCollectionContext().setExpressionContexts(asList(expressionConstant, expressionConstant1)));

        assertNotNull(operationResult);
        assertEquals(Response.Status.CREATED.getStatusCode(), operationResult.getResponse().getStatus());
    }
}
