package com.jaspersoft.jasperserver.jaxrs.client.apiadapters.resources;

import com.jaspersoft.jasperserver.dto.resources.ClientFile;
import com.jaspersoft.jasperserver.dto.resources.ClientJndiJdbcDataSource;
import com.jaspersoft.jasperserver.dto.resources.ClientReference;
import com.jaspersoft.jasperserver.dto.resources.ClientReferenceableFile;
import com.jaspersoft.jasperserver.dto.resources.ClientReportUnit;
import com.jaspersoft.jasperserver.dto.resources.domain.ClientDomain;
import com.jaspersoft.jasperserver.jaxrs.client.RestClientTestUtil;
import com.jaspersoft.jasperserver.jaxrs.client.core.operationresult.OperationResult;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;

/**
 * @author Alexander Krasnyanskiy
 * @author tetiana Iefimenko
 */
public class ResourcesReportUnitServiceTest extends RestClientTestUtil {

    private ClientDomain testDomain;
    private String testDomainUri = "/public/Samples/Domains/supermartDomain";
    private String testResourceUri = "";

    @BeforeClass
    public void before() {
        initClient();
        initSession();
    }

    @Test
    public void should_upload_reportUnit() throws FileNotFoundException {

        OperationResult<ClientReportUnit> repUnut =
                session.resourcesService()
                        .reportUnitResource()
                        .withJrxml(new FileInputStream("report_upload_resources\\AllAccounts.jrxml"), "AllAccounts.jrxml", "jrxml file")
                        .withLabel("testReport")
                        .withDescription("testDescription")
                        .inFolder("/public")
                        .create();

        assertNotNull(repUnut);
    }

    @Test
    public void should_upload_reportUnit_with_Image() throws FileNotFoundException {

        OperationResult<ClientReportUnit> repUnut =
                session.resourcesService()
                        .reportUnitResource()
                        .withJrxml(new FileInputStream("report_upload_resources\\AllAccounts.jrxml"), "AllAccounts.jrxml", "jrxml file")
                        .withFile(new ClientReference().setUri("/public/Samples/Resources/Images/Jaspersoft_logo.png"), "Jaspersoft_logo.png")
                        .withFile(new ClientReference().setUri("/organizations/organization_1/images/JRLogo"), "JRLogo")
                        .withLabel("testReport")
                        .withDescription("testDescription")
                        .inFolder("/public")
                        .create();

        assertNotNull(repUnut);
    }

    @Test
    public void should_upload_reportUnit_with_Image_as_resource_descriptor() throws FileNotFoundException {
        final ClientReportUnit clientReportUnit = new ClientReportUnit()
                .setLabel("testReport")
                .setDescription("testDescription");
        clientReportUnit.setFiles(new HashMap<String, ClientReferenceableFile>());
        clientReportUnit.getFiles().put("JRLogo", new ClientFile()
                .setUri("/organizations/organization_1/images/JRLogo")
                .setLabel("JRLogo")
                .setType(ClientFile.FileType.img));
        clientReportUnit.getFiles().put("Jaspersoft_logo.png", new ClientFile()
                .setUri("/public/Samples/Resources/Images/Jaspersoft_logo.png")
                .setLabel("Jaspersoft_logo.png")
                .setType(ClientFile.FileType.img));
        clientReportUnit.setJrxml(new ClientFile()
                .setLabel("JRXML file")
                .setType(ClientFile.FileType.jrxml)
                .setContent(RestClientTestUtil.fileToStringBase64Encoded(new File("report_upload_resources\\AllAccounts.jrxml"))));

        OperationResult<ClientReportUnit> repUnut =
                session.resourcesService()
                        .reportUnitResource(clientReportUnit)
                        .inFolder("/public")
                        .create();

        assertNotNull(repUnut);
    }

    @Deprecated
    @Test
    public void should_upload_report_with_jrxml() throws FileNotFoundException {
        ClientReferenceableFile jrxml = new ClientReferenceableFile() {
            @Override
            public String getUri() {
                return null;
            }
        };
        ClientReportUnit repunit = new ClientReportUnit();
        repunit.setJrxml(jrxml);
        repunit.setLabel("05_1.All accounts test report unit");
        repunit.setDataSource(new ClientJndiJdbcDataSource().
                setUri("/public/Samples/Data_Sources/JServerJNDIDS").
                setLabel("JServer JNDI Data Source").
                setJndiName("jdbc/sugarcrm"));
        repunit.setLabel("All accounts test report unit");

        ClientFile clifile = new ClientFile();
        clifile.setType(ClientFile.FileType.jrxml);
        clifile.setLabel("AllAccounts");

        OperationResult<ClientReportUnit> repUnut =
                session.resourcesService()
                        .resource(repunit)
                        .withJrxml(new FileInputStream("report_upload_resources\\AllAccounts.jrxml"), clifile)
                        .createInFolder("/temp");

        assertNotNull(repUnut);
    }


    @Deprecated
    @Test
    public void should_upload_report_with_jrxml_with_image() throws FileNotFoundException {
        ClientReferenceableFile jrxml = new ClientReferenceableFile() {
            @Override
            public String getUri() {
                return null;
            }
        };
        ClientReportUnit repunit = new ClientReportUnit();
        repunit.setJrxml(jrxml);
        repunit.setLabel("05_1.All accounts test report unit");
        repunit.setDataSource(new ClientJndiJdbcDataSource().
                setUri("/public/Samples/Data_Sources/JServerJNDIDS").
                setLabel("JServer JNDI Data Source").
                setJndiName("jdbc/sugarcrm"));
        repunit.setLabel("All accounts test report unit");


        ClientFile clifile = new ClientFile();
        clifile.setType(ClientFile.FileType.jrxml);
        clifile.setLabel("AllAccounts");

        OperationResult<ClientReportUnit> repUnut =
                session.resourcesService()
                        .resource(repunit)
                        .withJrxml(new FileInputStream("report_upload_resources\\AllAccounts.jrxml"), clifile)
                        .withNewFileReference("Jaspersoft_logo.png", new ClientReference().setUri("/public/Samples/Resources/Images/Jaspersoft_logo.png"))
                        .withNewFileReference("JRLogo", new ClientReference().setUri("/organizations/organization_1/images/JRLogo"))
                        .createInFolder("/temp");
        assertNotNull(repUnut);
    }

    @AfterClass
    public void after() {
        session.logout();
        session = null;
    }

}