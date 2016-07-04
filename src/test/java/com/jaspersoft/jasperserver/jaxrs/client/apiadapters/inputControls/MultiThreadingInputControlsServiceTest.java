/*
* Copyright (C) 2005 - 2014 Jaspersoft Corporation. All rights  reserved.
* http://www.jaspersoft.com.
*
* Unless you have purchased  a commercial license agreement from Jaspersoft,
* the following license terms  apply:
*
* This program is free software: you can redistribute it and/or  modify
* it under the terms of the GNU Affero General Public License  as
* published by the Free Software Foundation, either version 3 of  the
* License, or (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Affero  General Public License for more details.
*
* You should have received a copy of the GNU Affero General Public  License
* along with this program.&nbsp; If not, see <http://www.gnu.org/licenses/>.
*/
package com.jaspersoft.jasperserver.jaxrs.client.apiadapters.inputControls;

import com.jaspersoft.jasperserver.dto.reports.inputcontrols.ReportInputControlsListWrapper;
import com.jaspersoft.jasperserver.jaxrs.client.RestClientTestUtil;
import com.jaspersoft.jasperserver.jaxrs.client.core.Session;
import com.jaspersoft.jasperserver.jaxrs.client.core.operationresult.OperationResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * <p></p>
 *
 * @author Yaroslav.Kovalchyk
 * @version $Id$
 */

public class MultiThreadingInputControlsServiceTest extends RestClientTestUtil {

    private static final int threadsCount = 100;

    @BeforeClass
    public void before() {
        initClient();
    }

    @BeforeMethod
    public void init(){
        initSession();
    }


    @AfterMethod
    public void after() {
        session.logout();
    }



    public void testInputControlsOfReportUnit(String reportUnitUri, Session session) {
        // When
        OperationResult<ReportInputControlsListWrapper> operationResult = session
                .inputControlsService()
                .inputControls()
                .container(reportUnitUri)
                .get();
        // Then
        assertNotNull(operationResult);
        assertNotNull(operationResult.getEntity());
        assertTrue(operationResult.getEntity().getInputParameters().size() > 0);

    }

    @Test(enabled = false)
    public void multiThreading_superuser_sameSession() throws InterruptedException {
        multiThreading(threadsCount, "/organizations/organization_1/adhoc/topics/Cascading_multi_select_topic", "superuser", "superuser", true);
    }

    @Test(enabled = false)
    public void multiThreading_jasperadmin_sameSession() throws InterruptedException {
        multiThreading(threadsCount, "/adhoc/topics/Cascading_multi_select_topic", "jasperadmin|organization_1", "jasperadmin", true);
    }

    @Test(enabled = false)
    public void multiThreading_superuser_differentSession() throws InterruptedException {
        multiThreading(threadsCount, "/organizations/organization_1/adhoc/topics/Cascading_multi_select_topic", "superuser", "superuser", false);
    }

    @Test(enabled = false)
    public void multiThreading_jasperadmin_differentSession() throws InterruptedException {
        multiThreading(threadsCount, "/adhoc/topics/Cascading_multi_select_topic", "jasperadmin|organization_1", "jasperadmin", false);
    }


    protected void multiThreading(final int threadsCount, final String reportUnitUri,
            final String username, final String password, final boolean sameSession) throws InterruptedException {
        session.logout();
        initSession(username, password);
        final AtomicInteger doneCounter = new AtomicInteger(0);
        final CountDownLatch startSignal = new CountDownLatch(1);
        final CountDownLatch readySignal = new CountDownLatch(threadsCount);
        final CountDownLatch doneSignal = new CountDownLatch(threadsCount);

        for (int i = 0; i < threadsCount; ++i) {
            final Integer currentThreadNumber = i + 1;

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        readySignal.countDown();
                        startSignal.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Session threadSession = sameSession ? session : client.authenticate(username, password);
                    try {
                        testInputControlsOfReportUnit(reportUnitUri, threadSession);
                        doneCounter.getAndIncrement();
                    } catch (Exception e) {
                        System.out.println("Exception in thread " + currentThreadNumber + " " + e.getClass().getName() + ":" + e.getMessage());
                    }
                    doneSignal.countDown();
                }
            }).start();
        }
        readySignal.await(1, TimeUnit.MINUTES);
        startSignal.countDown();
        doneSignal.await(1, TimeUnit.MINUTES);
        assertEquals(doneCounter.get(), threadsCount);
    }

}
