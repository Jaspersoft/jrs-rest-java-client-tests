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

import static org.junit.Assert.assertNull;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class ThumbnailsServiceTest extends RestClientTestUtil {
    private final String EXIST_THUMBNAIL_REPORT_URI_1 = "/public/Samples/Reports/08g.UnitSalesDetailReport";
    private final String EXIST_THUMBNAIL_REPORT_URI_2 = "/public/Samples/Reports/11g.SalesByMonthReport";
    private final String NOT_EXIST_THUMBNAIL_REPORT_URI_1 = "/public/Samples/Reports/04._Product_Results_by_Store_Type_Report";
    private final String NOT_EXIST_THUMBNAIL_REPORT_URI_2 = "/public/Samples/Reports/02._Sales_Mix_by_Demographic_Report";

    @BeforeClass
    public void before() {
        initClient();
        initSession();
    }

    @Test
    public void should_return_list_of_thumbnails_with_default_request_method_without_default_picture() {
        // When
        List<ResourceThumbnail> entity = session.thumbnailsService()
                .thumbnails()
                .reports(EXIST_THUMBNAIL_REPORT_URI_1, EXIST_THUMBNAIL_REPORT_URI_2)
                .get()
                .getEntity()
                .getThumbnails();
        // Then
        assertNotNull(entity);
        assertTrue(entity.size() == 2);
        assertFalse(entity.get(0).getThumbnailData().isEmpty());
        assertFalse(entity.get(1).getThumbnailData().isEmpty());
    }
    @Test
    public void should_return_list_of_thumbnails_with_default_request_method_with_default_picture() {
        // When
        List<ResourceThumbnail> entity = session.thumbnailsService()
                .thumbnails()
                .reports(EXIST_THUMBNAIL_REPORT_URI_1, EXIST_THUMBNAIL_REPORT_URI_2)
                .defaultAllowed(true)
                .get()
                .getEntity()
                .getThumbnails();
        // Then
        assertNotNull(entity);
        assertTrue(entity.size() == 2);
        assertFalse(entity.get(0).getThumbnailData().isEmpty());
        assertFalse(entity.get(1).getThumbnailData().isEmpty());
    }

    @Test
    public void should_return_list_of_thumbnails_with_get_request_method_and_default_picture() {
        // When
        List<ResourceThumbnail> entity = session.thumbnailsService()
                .thumbnails()
                .reports(EXIST_THUMBNAIL_REPORT_URI_1, EXIST_THUMBNAIL_REPORT_URI_2)
                .defaultAllowed(true)
                .requestMethod(RequestMethod.GET)
                .get()
                .getEntity()
                .getThumbnails();
        // Then
        assertNotNull(entity);
        assertTrue(entity.size() == 2);
        assertFalse(entity.get(0).getThumbnailData().isEmpty());
        assertFalse(entity.get(1).getThumbnailData().isEmpty());
    }

    @Test
    public void should_return_list_of_thumbnails_with_get_request_method_and_without_default_picture() {
        // When
        List<ResourceThumbnail> entity = session.thumbnailsService()
                .thumbnails()
                .reports(EXIST_THUMBNAIL_REPORT_URI_1, EXIST_THUMBNAIL_REPORT_URI_2)
                .requestMethod(RequestMethod.GET)
                .get()
                .getEntity()
                .getThumbnails();
        // Then
        assertNotNull(entity);
        assertTrue(entity.size() == 2);
        assertFalse(entity.get(0).getThumbnailData().isEmpty());
        assertFalse(entity.get(1).getThumbnailData().isEmpty());
    }

    @Test
    public void should_not_return_list_of_thumbnails_without_default_picture() {
        // When
        List<ResourceThumbnail> entity = session.thumbnailsService()
                .thumbnails()
                .reports(NOT_EXIST_THUMBNAIL_REPORT_URI_1,
                        NOT_EXIST_THUMBNAIL_REPORT_URI_2)
                .get()
                .getEntity()
                .getThumbnails();
        // Then
        assertNotNull(entity);
        assertTrue(entity.get(0).getThumbnailData().isEmpty());
        assertTrue(entity.get(1).getThumbnailData().isEmpty());
    }

    @Test
    public void should_return_list_of_default_pictures_with_default_request_method() {
        // When
        List<ResourceThumbnail> entity = session.thumbnailsService()
                .thumbnails()
                .reports(NOT_EXIST_THUMBNAIL_REPORT_URI_1, NOT_EXIST_THUMBNAIL_REPORT_URI_2)
                .defaultAllowed(true)
                .get()
                .getEntity()
                .getThumbnails();
        // Then
        assertNotNull(entity);
        assertTrue(entity.size() == 2);
        assertFalse(entity.get(0).getThumbnailData().isEmpty());
        assertFalse(entity.get(1).getThumbnailData().isEmpty());
    }

    @Test
    public void should_return_list_of_thumbnails_with_get_request_method() {
        // When
        List<ResourceThumbnail> entity = session.thumbnailsService()
                .thumbnails()
                .reports(NOT_EXIST_THUMBNAIL_REPORT_URI_1, NOT_EXIST_THUMBNAIL_REPORT_URI_2)
                .defaultAllowed(true)
                .requestMethod(RequestMethod.GET)
                .get()
                .getEntity()
                .getThumbnails();
        // Then
        assertNotNull(entity);
        assertTrue(entity.size() == 2);
        assertFalse(entity.get(0).getThumbnailData().isEmpty());
        assertFalse(entity.get(1).getThumbnailData().isEmpty());
    }

    @Test
    public void should_not_return_list_of_thumbnails_with_get_request_method() {
        // When
        List<ResourceThumbnail> entity = session.thumbnailsService()
                .thumbnails()
                .reports(NOT_EXIST_THUMBNAIL_REPORT_URI_1, NOT_EXIST_THUMBNAIL_REPORT_URI_2)
                .requestMethod(RequestMethod.GET)
                .get()
                .getEntity()
                .getThumbnails();
        // Then
        assertNotNull(entity);
        assertTrue(entity.get(0).getThumbnailData().isEmpty());
        assertTrue(entity.get(1).getThumbnailData().isEmpty());
    }

    @Test
    public void should_return_single_thumbnail_as_stream() throws IOException {
        // When
        InputStream entity = session.thumbnailsService()
                .thumbnail()
                .report(EXIST_THUMBNAIL_REPORT_URI_1)
                .get()
                .getEntity();
        // Then
        assertNotNull(entity);
    }


    @Test
    public void should_return_single_thumbnail_as_stream_with_default_allowed() throws IOException {
        // When
        InputStream entity = session.thumbnailsService()
                .thumbnail()
                .report(EXIST_THUMBNAIL_REPORT_URI_1)
                .defaultAllowed(true)
                .get()
                .getEntity();
        // Then
        assertNotNull(entity);
    }

    @Test
    public void should_not_return_single_thumbnail_as_stream_without_default_image() throws IOException {
        // When
        InputStream entity = session.thumbnailsService()
                .thumbnail()
                .report(NOT_EXIST_THUMBNAIL_REPORT_URI_1)
                .get()
                .getEntity();
        // Then
        assertNull(entity);
    }

    @Test
    public void should_return_default_picture_as_stream() throws IOException {
        // When
        InputStream entity = session.thumbnailsService()
                .thumbnail()
                .report(NOT_EXIST_THUMBNAIL_REPORT_URI_1)
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