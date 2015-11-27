package com.jaspersoft.jasperserver.jaxrs.client.apiadapters.serverInfo;

import com.jaspersoft.jasperserver.dto.serverinfo.ServerInfo;
import com.jaspersoft.jasperserver.jaxrs.client.RestClientTestUtil;
import com.jaspersoft.jasperserver.jaxrs.client.core.AnonymousSession;
import com.jaspersoft.jasperserver.jaxrs.client.core.operationresult.OperationResult;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertNull;

/**
 * @author Tetiana Iefimenko
 */
public class ServerInfoServiceTest extends RestClientTestUtil {


    private AnonymousSession session;

    @Override
    public void initSession() {
        session = client.getAnonymousSession();
    }

    @BeforeClass
    public void before() {
        initClient();
        initSession();
    }

    @Test
    public void should_return_server_info() {
        // When
        OperationResult<ServerInfo> result = session
                .serverInfoService()
                .details();


        ServerInfo serverInfo = result.getEntity();

        assertNotNull(serverInfo);
        assertTrue(serverInfo.getEdition().name().equals("PRO"));
    }

    @Test
    public void should_return_server_edition() {
        // When
        OperationResult<String> result = session
                .serverInfoService()
                .edition();

        String edition = result.getEntity();

        assertNotNull(edition);
        assertTrue(edition.equals("PRO"));
    }


    @Test
    public void should_return_server_version() {
        // When
        OperationResult<String> result = session
                .serverInfoService()
                .version();

        String version = result.getEntity();

        assertNotNull(version);
        assertTrue(version.equals("6.1"));
    }


    @Test
    public void should_return_server_build() {
        // When
        OperationResult<String> result = session
                .serverInfoService()
                .build();

        String build = result.getEntity();

        assertNotNull(build);
        assertTrue(build.equals("20150527_1447"));
    }

    @Test
    public void should_return_server_license_type() {
        // When
        OperationResult<String> result = session
                .serverInfoService()
                .licenseType();

        String licenseType = result.getEntity();

        assertNotNull(licenseType);
        assertTrue(licenseType.equals("Commercial"));
    }
     @Test
    public void should_return_server_features() {
         // When
        OperationResult<String> result =session
                .serverInfoService()
                .features();
        String features = result.getEntity();

        assertNotNull(features);
        assertTrue(features.equals("Fusion AHD EXP DB ANA AUD MT "));
    }
     @Test
    public void should_return_server_expiration() {
         // When
        OperationResult<String> result =session
                .serverInfoService()
                .expiration();
        String expiration = result.getEntity();

        assertNull(expiration);
    }

    @Test
    public void should_return_server_editionName() {
        // When
        OperationResult<String> result =session
                .serverInfoService()
                .editionName();
        String editionName = result.getEntity();

        assertNotNull(editionName);
        assertTrue(editionName.equals("Enterprise"));
    }
    @Test
    public void should_return_server_dateTimeFormatPattern() {
        // When
        OperationResult<String> result =session
                .serverInfoService()
                .dateTimeFormatPattern();
        String dateTimeFormatPattern = result.getEntity();

        assertNotNull(dateTimeFormatPattern);
        assertTrue(dateTimeFormatPattern.equals("yyyy-MM-dd'T'HH:mm:ss"));
    }
    @Test
    public void should_return_server_dateFormatPattern() {
        // When
        OperationResult<String> result =session
                .serverInfoService()
                .dateFormatPattern();
        String dateFormatPattern = result.getEntity();

        assertNotNull(dateFormatPattern);
        assertTrue(dateFormatPattern.equals("yyyy-MM-dd"));
    }

}
