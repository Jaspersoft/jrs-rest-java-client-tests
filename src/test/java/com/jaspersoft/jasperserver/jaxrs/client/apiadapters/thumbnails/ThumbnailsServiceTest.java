package com.jaspersoft.jasperserver.jaxrs.client.apiadapters.thumbnails;

import com.jaspersoft.jasperserver.dto.thumbnails.ResourceThumbnail;
import com.jaspersoft.jasperserver.jaxrs.client.RestClientTestUtil;
import com.jaspersoft.jasperserver.jaxrs.client.core.enums.RequestMethod;
import com.jaspersoft.jasperserver.jaxrs.client.core.exceptions.RequestedRepresentationNotAvailableForResourceException;
import com.jaspersoft.jasperserver.jaxrs.client.core.exceptions.ResourceNotFoundException;
import com.jaspersoft.jasperserver.jaxrs.client.core.operationresult.OperationResult;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import javax.ws.rs.core.Response;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static java.util.Arrays.asList;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
* Integration tests for {@link ThumbnailsService}
*/
public class ThumbnailsServiceTest extends RestClientTestUtil {

    @BeforeClass
    public void before() {
        initClient();
        initSession();
    }

    @Test
    /**
     * Batch thumbnails operation
     */
    public void should_return_list_of_thumbnails_with_default_request_method() {
        // When
        List<ResourceThumbnail> entity = session.thumbnailsService()
                .thumbnails()
                .reports("/public/Samples/Reports/08g.UnitSalesDetailReport",
                        "/public/Samples/Reports/11g.SalesByMonthReport")
                .defaultAllowed(true)
                .get()
                .getEntity()
                .getThumbnails();
        // Then
        assertNotNull(entity);
        assertTrue(entity.size() == 2);
    }

    @Test
    /**
     * Batch thumbnails operation
     */
    public void should_return_list_of_thumbnails_with_get_request_method() {
        // When
        List<ResourceThumbnail> entity = session.thumbnailsService()
                .thumbnails()
                .reports(asList("/public/Samples/Reports/08g.UnitSalesDetailReport",
                        "/public/Samples/Reports/11g.SalesByMonthReport"))
                .defaultAllowed(true).requestMethod(RequestMethod.GET)
                .get()
                .getEntity()
                .getThumbnails();
        // Then
        assertNotNull(entity);
        assertTrue(entity.size() == 2);
    }

    @Test
    /**
     * Single thumbnail operation
     */
    public void should_return_single_thumbnail_as_stream() throws IOException {
        // When
        InputStream entity = session.thumbnailsService()
                .thumbnail()
                .report("/public/Samples/Reports/08g.UnitSalesDetailReport")
                .defaultAllowed(true)
                .get()
                .getEntity();
        // Then
        assertNotNull(entity);
    }

    @Test(expectedExceptions = ResourceNotFoundException.class)
    public void should_throw_ResourceNotFoundException() throws IOException {
        // Given
        configuration.setHandleErrors(true);
        // When
        session.thumbnailsService()
                .thumbnail()
                .report("/some")
                .get();
        // Then an exception should be thrown
    }


    @Test
    public void should_not_throw_ResourceNotFoundException_and_return_null_entity() throws IOException {
        // Given
        configuration.setHandleErrors(false);
        // When
        OperationResult<InputStream> result = session
                .thumbnailsService()
                .thumbnail()
                .report("/some")
                .get();
        // Then
        assertNotNull(result);
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), result.getResponseStatus());

    }


    @Test(expectedExceptions = ResourceNotFoundException.class)
    public void should_return_operstionResult_and_throw_ResourceNotFoundException() throws IOException {
        // Given
        configuration.setHandleErrors(false);
        // When
        OperationResult<InputStream> result = session
                .thumbnailsService()
                .thumbnail()
                .report("/some")
                .get();
        result.getEntity();
        // Then an exception should be thrown

    }

    @Test(expectedExceptions = RequestedRepresentationNotAvailableForResourceException.class)
    public void should_throw_RequestedRepresentationNotAvailableForResourceException(){
        // Given
        configuration.setHandleErrors(true);
        // When
        OperationResult<InputStream> result = session
                .thumbnailsService()
                .thumbnail()
                .report("/")
                .get();
        // Then an exception should be thrown
    }


    @Test
    public void should_not_throw_RequestedRepresentationNotAvailableForResourceException_and_return_null_entity() throws IOException {
        //Given
        configuration.setHandleErrors(false);
        // When
        OperationResult<InputStream> result = session
                .thumbnailsService()
                .thumbnail()
                .report("/")
                .get();
        // Then
        assertNotNull(result);
        assertEquals(Response.Status.NOT_ACCEPTABLE.getStatusCode(), result.getResponseStatus());

    }


    @Test(expectedExceptions = RequestedRepresentationNotAvailableForResourceException.class)
    public void should_return_operstionResult_and_throw_RequestedRepresentationNotAvailableForResourceException() throws IOException {
        //Given
        configuration.setHandleErrors(false);
        // When
        OperationResult<InputStream> result = session
                .thumbnailsService()
                .thumbnail()
                .report("/")
                .get();
        result.getEntity();
        // Then an exception should be thrown

    }

    @AfterClass
    public void after() {
        session.logout();
        session = null;
    }
}