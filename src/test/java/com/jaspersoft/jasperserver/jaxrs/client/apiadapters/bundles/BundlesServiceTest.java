package com.jaspersoft.jasperserver.jaxrs.client.apiadapters.bundles;

import com.jaspersoft.jasperserver.jaxrs.client.RestClientTestUtil;
import com.jaspersoft.jasperserver.jaxrs.client.core.AnonymousSession;
import com.jaspersoft.jasperserver.jaxrs.client.core.Session;
import com.jaspersoft.jasperserver.jaxrs.client.core.operationresult.OperationResult;
import java.util.Locale;
import java.util.Map;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertEquals;

/**
 * @author Tetiana Iefimenko
 */

public class BundlesServiceTest extends RestClientTestUtil {

    private AnonymousSession session;
    private final String TEST_KEY = "jasperserver_config";
    private final String TEST_LOCALE_DE = "de";
    private final String TEST_LOCALE_EN = "en_US";
    private final String TEST_LOCALE_PT = "pt_BR";
    private final String TEST_BUNDLE_NAME = "jasperserver_messages";

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
    public void should_return_all_bundles_for_default_locale() {

       // When
        final Map<String, Map<String, String>> bundles = session
                .bundlesService()
                .allBundles()
                .getEntity();

        // Then
        assertNotNull(bundles);
        assertFalse(bundles.size() == 0);
        assertTrue(bundles.containsKey(TEST_KEY));
    }

    @Test
    public void should_return_all_bundles_for_specified_string_locale() {

       // When
        final Map<String, Map<String, String>> bundles = session
                .bundlesService()
                .forLocale(TEST_LOCALE_DE)
                .allBundles()
                .getEntity();

        // Then
        assertNotNull(bundles);
        assertFalse(bundles.size() == 0);
        assertTrue(bundles.containsKey(TEST_KEY));
    }

    @Test
    public void should_return_all_bundles_for_specified_locale() {

       // When
        final Map<String, Map<String, String>> bundles = session
                .bundlesService()
                .forLocale(new Locale(TEST_LOCALE_DE))
                .allBundles()
                .getEntity();

        // Then
        assertNotNull(bundles);
        assertFalse(bundles.size() == 0);
        assertTrue(bundles.containsKey(TEST_KEY));
    }

    @Test
    public void should_return__bundle_by_name_for_specified_string_locale() {

        // When
        final Map<String, String> bundle = session
                .bundlesService()
                .forLocale(TEST_LOCALE_DE)
                .bundle(TEST_BUNDLE_NAME)
                .getEntity();

        // Then
        assertNotNull(bundle);
        assertFalse(bundle.size() == 0);
        assertTrue(bundle.containsKey("logCollectors.form.resourceUri.hint"));
    }

    @Test
    public void should_return__bundle_by_name_for_specified_locale() {

        // When
        final Map<String, String> bundle = session
                .bundlesService()
                .forLocale(new Locale(TEST_LOCALE_EN))
                .bundle(TEST_BUNDLE_NAME)
                .getEntity();

        // Then
        assertNotNull(bundle);
        assertFalse(bundle.size() == 0);
        assertTrue(bundle.containsKey("logCollectors.form.resourceUri.hint"));
    }


    @Test
    public void should_return__bundle_by_name_for_specified_pt_BR_locale() {

        // When
        final Map<String, String> bundle = session
                .bundlesService()
                .forLocale(TEST_LOCALE_PT)
                .bundle(TEST_BUNDLE_NAME)
                .getEntity();

        // Then
        assertNotNull(bundle);
        assertFalse(bundle.size() == 0);
        assertTrue(bundle.containsKey("logCollectors.form.resourceUri.hint"));
    }

    @Test
    public void should_return__bundle_by_name_for_authentication_locale() {

        // When
        Session session = client.authenticate("superuser", "superuser", "de", null);
        final Map<String, String> bundle = session
                .bundlesService()
                .bundle(TEST_BUNDLE_NAME)
                .getEntity();

        // Then
        assertNotNull(bundle);
        assertFalse(bundle.size() == 0);
        assertTrue(bundle.containsKey("export.file.name"));
        Assert.assertEquals("Name der Exportdatendatei:", bundle.get("export.file.name"));
    }

    @Test
    public void should_return__bundle_by_name_for_specified_pt_BR_locale_and_ignore_default() {

        // When
        Session session = client.authenticate("superuser", "superuser", "de", null);
        final Map<String, String> bundle = session
                .bundlesService()
                .forLocale(TEST_LOCALE_PT)
                .bundle(TEST_BUNDLE_NAME)
                .getEntity();

        // Then
        assertNotNull(bundle);
        assertFalse(bundle.size() == 0);
        assertTrue(bundle.containsKey("export.file.name"));
        Assert.assertEquals("Exportar nome de arquivo de dados:", bundle.get("export.file.name"));
    }

    @Test
    public void should_return__bundle_by_name_for_specified_as_constant_locale() {

        // When
        final Map<String, String> bundle = session
                .bundlesService()
                .forLocale(Locale.US)
                .bundle(TEST_BUNDLE_NAME)
                .getEntity();

        // Then
        assertNotNull(bundle);
        assertFalse(bundle.size() == 0);
        assertTrue(bundle.containsKey("logCollectors.form.resourceUri.hint"));
    }

    @Test
    public void should_return__bundle_by_name_for_default_locale() {

        // When
        final Map<String, String> bundle = session
                .bundlesService()
                .bundle(TEST_BUNDLE_NAME)
                .getEntity();

        // Then
        assertNotNull(bundle);
        assertFalse(bundle.size() == 0);
        assertTrue(bundle.containsKey("logCollectors.form.resourceUri.hint"));
    }

    /**
     * Platform specific test, fails on JBoss 8
     */
    @Test
    public void test_verifyReturnsResultsForEmptyLocale(){
        // When
        OperationResult<Map<String, Map<String, String>>> result = client
                .getAnonymousSession()
                .bundlesService()
                .forLocale(new Locale(""))
                .allBundles();
        // Then
        assertEquals(result.getResponseStatus(), 200);
        assertNotNull(result.getEntity());
        assertFalse(result.getEntity().isEmpty());
    }

}
