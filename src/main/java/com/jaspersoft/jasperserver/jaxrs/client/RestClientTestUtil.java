package com.jaspersoft.jasperserver.jaxrs.client;

import com.jaspersoft.jasperserver.jaxrs.client.core.JasperserverRestClient;
import com.jaspersoft.jasperserver.jaxrs.client.core.RestClientConfiguration;
import com.jaspersoft.jasperserver.jaxrs.client.core.Session;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;
import java.util.Properties;
import java.util.TimeZone;

/**
 * @author Tetiana Iefimenko
 */
public abstract class RestClientTestUtil {
    protected RestClientConfiguration configuration;
    protected JasperserverRestClient client;
    protected Session session;
    protected Properties properties;
    protected final String PROPERTY_FILE_NAME = "test_config.properties";

    protected void initClient() {
        loadTestProperties(PROPERTY_FILE_NAME);
        configuration = RestClientConfiguration.loadConfiguration(properties);
        client = new JasperserverRestClient(configuration);
    }

    protected void initSession() {
        initSession(properties.getProperty("username"), properties.getProperty("password"));
    }

    protected void initSession(String username, String password){
        session = client.authenticate(username, password);

    }

    protected void initSession(String username, String password, Locale locale, TimeZone timeZone){
        session = client.authenticate(username, password, locale, timeZone);

    }

    private void loadTestProperties(String path) {
        properties = new Properties();
        InputStream is;
        try {
            is = RestClientConfiguration.class.getClassLoader().getResourceAsStream(path);
            if (is == null) {
                throw new IOException("Property file is not found");
            }
            properties.load(is);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    protected static void streamToFile(InputStream entity, String filename) {
        OutputStream output = null;
        try {
            output = new FileOutputStream(filename);
            int i = 0;
            while (i != -1) {
                i = entity.read();
                output.write(i);
                output.flush();

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            try {
                entity.close();
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
