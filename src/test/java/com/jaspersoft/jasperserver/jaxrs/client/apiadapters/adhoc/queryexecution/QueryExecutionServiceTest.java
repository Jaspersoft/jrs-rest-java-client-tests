package com.jaspersoft.jasperserver.jaxrs.client.apiadapters.adhoc.queryexecution;

import com.jaspersoft.jasperserver.dto.adhoc.datasource.ClientDataSourceField;
import com.jaspersoft.jasperserver.dto.adhoc.query.ClientMultiAxisQuery;
import com.jaspersoft.jasperserver.dto.adhoc.query.ClientMultiLevelQuery;
import com.jaspersoft.jasperserver.dto.adhoc.query.ClientWhere;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.ClientVariable;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.literal.ClientString;
import com.jaspersoft.jasperserver.dto.adhoc.query.el.operator.comparison.ClientEquals;
import com.jaspersoft.jasperserver.dto.adhoc.query.field.ClientQueryAggregatedField;
import com.jaspersoft.jasperserver.dto.adhoc.query.field.ClientQueryField;
import com.jaspersoft.jasperserver.dto.adhoc.query.field.ClientQueryLevel;
import com.jaspersoft.jasperserver.dto.adhoc.query.group.ClientMultiAxisGroupBy;
import com.jaspersoft.jasperserver.dto.adhoc.query.group.axis.ClientGroupAxisEnum;
import com.jaspersoft.jasperserver.dto.adhoc.query.group.axis.ClientLevelAxis;
import com.jaspersoft.jasperserver.dto.adhoc.query.order.ClientPathOrder;
import com.jaspersoft.jasperserver.dto.adhoc.query.select.ClientSelect;
import com.jaspersoft.jasperserver.dto.executions.ClientFlatQueryResultData;
import com.jaspersoft.jasperserver.dto.executions.ClientMultiAxesQueryExecution;
import com.jaspersoft.jasperserver.dto.executions.ClientMultiLevelQueryExecution;
import com.jaspersoft.jasperserver.dto.executions.ClientMultiLevelQueryResultData;
import com.jaspersoft.jasperserver.dto.executions.ClientProvidedQueryExecution;
import com.jaspersoft.jasperserver.dto.executions.ClientQueryParams;
import com.jaspersoft.jasperserver.dto.executions.ClientQueryResultData;
import com.jaspersoft.jasperserver.jaxrs.client.RestClientTestUtil;
import com.jaspersoft.jasperserver.jaxrs.client.core.operationresult.OperationResult;
import java.text.ParseException;
import java.util.LinkedList;
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
public class QueryExecutionServiceTest extends RestClientTestUtil {
    private String uuId;

    @BeforeClass
    public void before() {
        initClient();
        initSession();
    }

    @Test(enabled = true)
    public void should_get_flat_data_result_set() {
        // Given
        LinkedList<ClientQueryField> fields = new LinkedList<ClientQueryField>();
        fields.add(new ClientQueryField().setId("city1").
                setDataSourceField(new ClientDataSourceField().
                        setName("sales_fact_ALL.sales__store.sales__store__region.sales__store__region__sales_city").
                        setType("String")));

        ClientMultiLevelQuery query = (ClientMultiLevelQuery) new ClientMultiLevelQuery().
                setSelect(new ClientSelect().setFields(fields)).setLimit(1000);

        ClientMultiLevelQueryExecution queryExecution = new ClientMultiLevelQueryExecution().
                setDataSourceUri("/public/Samples/Ad_Hoc_Views/04__Product_Results_by_Store_Type").
                setQuery(query).
                setParams(new ClientQueryParams().setOffset(new int[]{0}).setPageSize(new int[]{100}));

        // When
        OperationResult<ClientFlatQueryResultData> execute = session.
                queryExecutionService().
                flatQuery().
                execute(queryExecution);

        // Then
        assertEquals(Response.Status.OK.getStatusCode(), execute.getResponse().getStatus());
        assertNotNull(execute);
        assertNotNull(execute.getEntity());
    }


    @Test(enabled = true)
    public void should_get_multi_level_data_result_set() throws ParseException {
        // Given
        LinkedList<ClientQueryField> fields = new LinkedList<ClientQueryField>();
        fields.add(new ClientQueryField().setId("city1").
                setDataSourceField(new ClientDataSourceField().
                        setName("sales_fact_ALL.sales__store.sales__store__region.sales__store__region__sales_city").
                        setType("String")));

        ClientEquals clientEquals = new ClientEquals();
        clientEquals.
                getOperands().add(new ClientVariable("city1"));
        clientEquals.
                getOperands().add(new ClientString("San Francisco"));

        ClientWhere where = new ClientWhere().setFilterExpressionObject(clientEquals);

        ClientMultiLevelQuery query = (ClientMultiLevelQuery) new ClientMultiLevelQuery().
                setSelect(new ClientSelect().setFields(fields)).
                setWhere(where).setLimit(1000);

        ClientMultiLevelQueryExecution queryExecution = new ClientMultiLevelQueryExecution().
                setDataSourceUri("/public/Samples/Ad_Hoc_Views/04__Product_Results_by_Store_Type").
                setQuery(query).
                setParams(new ClientQueryParams().setOffset(new int[]{0}).setPageSize(new int[]{100}));

        // When
        OperationResult<ClientMultiLevelQueryResultData> execute = session.
                queryExecutionService().
                multiLevelQuery().
                execute(queryExecution);

        // Then
        assertEquals(Response.Status.OK.getStatusCode(), execute.getResponse().getStatus());
        assertNotNull(execute);
    }

    @Test(enabled = true)
    public void should_get_provided_data_result_set() throws ParseException {

        // Given
        ClientProvidedQueryExecution queryExecution = new ClientProvidedQueryExecution().
                setDataSourceUri("/public/Samples/Ad_Hoc_Views/01__Geographic_Results_by_Segment").
                setParams(new ClientQueryParams().setOffset(new int[]{0}).setPageSize(new int[]{100}));

        // When
        OperationResult<? extends ClientQueryResultData> execute = session.
                queryExecutionService().
                providedQuery().
                execute(queryExecution);

        // Then
        assertEquals(Response.Status.OK.getStatusCode(), execute.getResponse().getStatus());
        extractUuid(execute.getResponse().getHeaderString("Content-Location"));
        assertNotNull(execute);
        ClientQueryResultData entity = execute.getEntity();
        assertNotNull(entity);
    }

    @Test(enabled = true, dependsOnMethods = "should_get_provided_data_result_set")
    public void should_get_provided_data_fragmented_result_set() throws ParseException {

        // When
        OperationResult<? extends ClientQueryResultData> execute = session.
                queryExecutionService().
                providedQuery().
                offset(0).
                pageSize(100).
                retrieveData(uuId);

        // Then
        assertEquals(Response.Status.OK.getStatusCode(), execute.getResponse().getStatus());
        assertNotNull(execute);
        ClientQueryResultData entity = execute.getEntity();
        assertNotNull(entity);
    }

    @Test(enabled = true, dependsOnMethods = "should_get_provided_data_fragmented_result_set")
    public void should_delete_query_execution() throws ParseException {

        // When
        OperationResult<? extends ClientQueryResultData> execute = session.
                queryExecutionService().
                providedQuery().
                deleteExecution(uuId);

        // Then
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), execute.getResponse().getStatus());
        assertNotNull(execute);
    }

    @Test
    public void should_run_simple_multi_select_query_with_expression() throws ParseException {

        // Given
        // select
        LinkedList<ClientQueryAggregatedField> aggregations = new LinkedList<ClientQueryAggregatedField>();
        aggregations.add(new ClientQueryAggregatedField().
                setId("Sumsales1").
        setAggregateExpression("Sum(sales1)").
                setDataSourceField(new ClientDataSourceField().
                        setName("sales_fact_ALL.sales_fact_ALL__store_sales_2013").
                        setType("java.lang.Double")));
        ClientSelect clientSelect = new ClientSelect().setAggregations(aggregations);

        // query
        ClientMultiAxisQuery query = (ClientMultiAxisQuery) new ClientMultiAxisQuery().
                setSelect(clientSelect).
                setLimit(1000);

        ClientMultiAxesQueryExecution queryExecution = new ClientMultiAxesQueryExecution().
                setDataSourceUri("/public/Samples/Ad_Hoc_Views/04__Product_Results_by_Store_Type").
                setQuery(query).
                setParams(new ClientQueryParams().setOffset(new int[]{0}).setPageSize(new int[]{100}));

        // When
        OperationResult execute = session.
                queryExecutionService().
                multiAxesQuery().
                execute(queryExecution);

        // Then
        assertEquals(Response.Status.OK.getStatusCode(), execute.getResponse().getStatus());
        assertNotNull(execute);
        assertNotNull(execute.getEntity());
    }

    @Test
    public void should_run_simple_multi_select_query_with_function() throws ParseException {

        // Given
        // select
        LinkedList<ClientQueryAggregatedField> aggregations = new LinkedList<ClientQueryAggregatedField>();
        aggregations.add(new ClientQueryAggregatedField().
                setId("Sumsales1").
                setAggregateFunction("Sum").
                setDataSourceField(new ClientDataSourceField().
                        setName("sales_fact_ALL.sales_fact_ALL__store_sales_2013").
                        setType("java.lang.Double")));
        ClientSelect clientSelect = new ClientSelect().setAggregations(aggregations);

        // query
        ClientMultiAxisQuery query = (ClientMultiAxisQuery) new ClientMultiAxisQuery().
                setSelect(clientSelect).
                setLimit(1000);

        ClientMultiAxesQueryExecution queryExecution = new ClientMultiAxesQueryExecution().
                setDataSourceUri("/public/Samples/Ad_Hoc_Views/04__Product_Results_by_Store_Type").
                setQuery(query).
                setParams(new ClientQueryParams().setOffset(new int[]{0}).setPageSize(new int[]{100}));

        // When
        OperationResult execute = session.
                queryExecutionService().
                multiAxesQuery().
                execute(queryExecution);

        // Then
        assertEquals(Response.Status.OK.getStatusCode(), execute.getResponse().getStatus());
        assertNotNull(execute);
        assertNotNull(execute.getEntity());
    }

    @Test
    public void should_run_multi_select_query_with_group_by_columns() throws ParseException {

        // Given
        // select
        LinkedList<ClientQueryAggregatedField> aggregations = new LinkedList<ClientQueryAggregatedField>();
        aggregations.add(new ClientQueryAggregatedField().
                setFieldReference("sales_fact_ALL.sales_fact_ALL__store_sales_2013"));
        ClientQueryLevel shippedDateLevel = new ClientQueryLevel().
                setFieldName("sales_fact_ALL.sales__store.sales__store__region.sales__store__region__sales_city");

        ClientSelect clientSelect = new ClientSelect().setAggregations(aggregations);
        ClientQueryLevel.ClientLevelAggregationsRef aggregationsRef = new ClientQueryLevel.ClientLevelAggregationsRef();
        // group dy
        ClientMultiAxisGroupBy groupBy = new ClientMultiAxisGroupBy();
        groupBy.addAxis(ClientGroupAxisEnum.COLUMNS, new ClientLevelAxis(asList(aggregationsRef), null));
        groupBy.addAxis(ClientGroupAxisEnum.ROWS, new ClientLevelAxis(asList(shippedDateLevel), null));
        // query
        ClientMultiAxisQuery query = (ClientMultiAxisQuery) new ClientMultiAxisQuery().
                setSelect(clientSelect).
                setGroupBy(groupBy).
                setLimit(1000);

        // When
        ClientMultiAxesQueryExecution queryExecution = new ClientMultiAxesQueryExecution().
                setDataSourceUri("/public/Samples/Ad_Hoc_Views/04__Product_Results_by_Store_Type").
                setQuery(query).
                setParams(new ClientQueryParams().setOffset(new int[]{0}).setPageSize(new int[]{100}));
        OperationResult execute = session.
                queryExecutionService().
                multiAxesQuery().
                execute(queryExecution);

        // Then
        assertEquals(Response.Status.OK.getStatusCode(), execute.getResponse().getStatus());
        assertNotNull(execute);
        assertNotNull(execute.getEntity());
    }

    @Test
    public void should_run_multi_select_query_with_group_by_rows() throws ParseException {

        // Given
        // select
        LinkedList<ClientQueryAggregatedField> aggregations = new LinkedList<ClientQueryAggregatedField>();
        aggregations.add(new ClientQueryAggregatedField().setFieldReference("sales_fact_ALL.sales_fact_ALL__store_sales_2013"));
        ClientQueryLevel shippedDateLevel = new ClientQueryLevel().setFieldName("sales_fact_ALL.sales__store.sales__store__region.sales__store__region__sales_city");

        ClientSelect clientSelect = new ClientSelect().setAggregations(aggregations);
        // group by
        ClientQueryLevel.ClientLevelAggregationsRef aggregationsRef = new ClientQueryLevel.ClientLevelAggregationsRef();

        ClientMultiAxisGroupBy groupBy = new ClientMultiAxisGroupBy();
        groupBy.addAxis(ClientGroupAxisEnum.COLUMNS, new ClientLevelAxis(asList(shippedDateLevel), null));
        groupBy.addAxis(ClientGroupAxisEnum.ROWS, new ClientLevelAxis(asList(aggregationsRef), null));
        // query
        ClientMultiAxisQuery query = (ClientMultiAxisQuery) new ClientMultiAxisQuery().
                setSelect(clientSelect).
                setGroupBy(groupBy).
                setLimit(1000);

        // When
        ClientMultiAxesQueryExecution queryExecution = new ClientMultiAxesQueryExecution().
                setDataSourceUri("/public/Samples/Ad_Hoc_Views/04__Product_Results_by_Store_Type").
                setQuery(query).
                setParams(new ClientQueryParams().setOffset(new int[]{0}).setPageSize(new int[]{100}));
        OperationResult execute = session.
                queryExecutionService().
                multiAxesQuery().
                execute(queryExecution);

        // Then
        assertEquals(Response.Status.OK.getStatusCode(), execute.getResponse().getStatus());
        assertNotNull(execute);
        assertNotNull(execute.getEntity());
    }


    @Test(enabled = true)
    public void should_run_multi_select_query_with_group_by_order_by() throws ParseException {

        // Given
        // select
        LinkedList<ClientQueryAggregatedField> aggregations = new LinkedList<ClientQueryAggregatedField>();
        aggregations.add(new ClientQueryAggregatedField().
                setId("Sumsales1").
                setAggregateFunction("Sum").
                setDataSourceField(new ClientDataSourceField().
                        setName("sales_fact_ALL.sales_fact_ALL__store_sales_2013").
                        setType("java.lang.Double")));

        ClientSelect clientSelect = new ClientSelect().setAggregations(aggregations);

        // group by
        ClientQueryLevel.ClientLevelAggregationsRef aggregationsRef = new ClientQueryLevel.ClientLevelAggregationsRef();

        ClientMultiAxisGroupBy groupBy = new ClientMultiAxisGroupBy();
        groupBy.addAxis(ClientGroupAxisEnum.COLUMNS, new ClientLevelAxis(asList(aggregationsRef), null));
        ClientQueryLevel queryLevel = (ClientQueryLevel) new ClientQueryLevel().
                setId("city1").
                setDataSourceField(new ClientDataSourceField().
                        setName("sales_fact_ALL.sales__store.sales__store__region.sales__store__region__sales_city").
                        setType("java.lang.String"));
        groupBy.addAxis(ClientGroupAxisEnum.ROWS, new ClientLevelAxis(asList(queryLevel), null));

        // order by
        ClientPathOrder clientPathOrder = new ClientPathOrder().
                setPath(asList("sales_fact_ALL.sales_fact_ALL__store_sales_2013")).
                setAscending(true);
        ClientMultiAxisQuery query = (ClientMultiAxisQuery) new ClientMultiAxisQuery().
                setSelect(clientSelect).
                setGroupBy(groupBy).
                setOrderBy(asList(clientPathOrder)).
                setLimit(1000);

        // When
        ClientMultiAxesQueryExecution queryExecution = new ClientMultiAxesQueryExecution().
                setDataSourceUri("/public/Samples/Ad_Hoc_Views/04__Product_Results_by_Store_Type").
                setQuery(query).
                setParams(new ClientQueryParams().setOffset(new int[]{0}).setPageSize(new int[]{100}));
        OperationResult execute = session.
                queryExecutionService().
                multiAxesQuery().
                execute(queryExecution);

        // Then
        assertEquals(Response.Status.OK.getStatusCode(), execute.getResponse().getStatus());
        assertNotNull(execute);
        assertNotNull(execute.getEntity());
    }

    private void extractUuid(String locationHeader) {
        if (locationHeader.endsWith("data")) {
            locationHeader = locationHeader.substring(0, locationHeader.lastIndexOf("/"));
        }

        uuId = locationHeader.substring(locationHeader.lastIndexOf("/") + 1);
    }

    @AfterClass
    public void after() {
        session.logout();
        session = null;
    }
}
