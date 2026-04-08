/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.cassandra.db.monitoring;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.vmlens.api.AllInterleavings;
import com.vmlens.api.AllInterleavingsBuilder;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import static java.lang.Thread.currentThread;
import static java.util.UUID.randomUUID;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.NANOSECONDS;
import static org.apache.cassandra.utils.Clock.Global.nanoTime;
import static org.apache.cassandra.utils.MonotonicClock.Global.approxTime;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MonitoringTaskTest
{
    private static final long timeout = MILLISECONDS.toNanos(100);
    private static final long slowTimeout = MILLISECONDS.toNanos(10);

    private static final long MAX_SPIN_TIME_NANOS = TimeUnit.SECONDS.toNanos(5);

    private static final int REPORT_INTERVAL_MS = 600000; // long enough so that it won't check unless told to do so
    private static final int MAX_TIMEDOUT_OPERATIONS = -1; // unlimited

    @BeforeClass
    public static void setup()
    {
        MonitoringTask.instance = MonitoringTask.make(REPORT_INTERVAL_MS, MAX_TIMEDOUT_OPERATIONS);
    }

    @After
    public void cleanUp()
    {
        // these clear the queues of the monitorint task
        MonitoringTask.instance.getSlowOperations();
        MonitoringTask.instance.getFailedOperations();
    }

    private static final class TestMonitor extends MonitorableImpl
    {
        private final String name;

        TestMonitor(String name, long timestamp, boolean isCrossNode, long timeout, long slow)
        {
            this.name = name;
            setMonitoringTime(timestamp, isCrossNode, timeout, slow);
        }

        public String name()
        {
            return name;
        }

        @Override
        public String toString()
        {
            return name();
        }
    }

    private static void waitForOperationsToComplete(Monitorable... operations) throws InterruptedException
    {
        waitForOperationsToComplete(Arrays.asList(operations));
    }

    private static void waitForOperationsToComplete(List<Monitorable> operations) throws InterruptedException
    {
        long timeout = operations.stream().map(Monitorable::timeoutNanos).reduce(0L, Long::max);
        Thread.sleep(NANOSECONDS.toMillis(timeout * 2 + approxTime.error()));

        long start = nanoTime();
        while(nanoTime() - start <= MAX_SPIN_TIME_NANOS)
        {
            long numInProgress = operations.stream().filter(Monitorable::isInProgress).count();
            if (numInProgress == 0)
                return;
        }
    }

    private static void waitForOperationsToBeReportedAsSlow(List<Monitorable> operations) throws InterruptedException
    {
        long timeout = operations.stream().map(Monitorable::slowTimeoutNanos).reduce(0L, Long::max);
        Thread.sleep(NANOSECONDS.toMillis(timeout * 2 + approxTime.error()));

        long start = nanoTime();
        while(nanoTime() - start <= MAX_SPIN_TIME_NANOS)
        {
            long numSlow = operations.stream().filter(Monitorable::isSlow).count();
            if (numSlow == operations.size())
                return;
        }
    }

    @Test
    public void testMultipleThreads() throws InterruptedException
    {
        try (AllInterleavings allInterleavings = new AllInterleavingsBuilder()
                .build("cassandra.testMultipleThreads")) {

            while (allInterleavings.hasNext()) {
                final int opCount = 2;
                final ExecutorService executorService = Executors.newFixedThreadPool(2);
                final List<Monitorable> operations = Collections.synchronizedList(new ArrayList<>(opCount));

                for (int i = 0; i < opCount; i++) {
                    executorService.submit(() ->
                            operations.add(new TestMonitor(randomUUID().toString(), nanoTime(), false, timeout, slowTimeout))
                    );
                }

                executorService.shutdown();
                assertTrue(executorService.awaitTermination(1, TimeUnit.MINUTES));
                assertEquals(opCount, operations.size());

                waitForOperationsToComplete(operations);
                assertEquals(opCount, MonitoringTask.instance.getFailedOperations().size());
                assertEquals(0, MonitoringTask.instance.getSlowOperations().size());
            }
        }
    }



    @Ignore
    @Test
    public void testMultipleThreadsSameNameFailed() throws InterruptedException
    {
        try (AllInterleavings allInterleavings = new AllInterleavingsBuilder()
                .build("cassandra.testMultipleThreadsSameNameFailed")) {

            while (allInterleavings.hasNext()) {
                final int threadCount = 2;
                final List<Monitorable> operations = new ArrayList<>(threadCount);
                ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
                final CountDownLatch finished = new CountDownLatch(threadCount);

                for (int i = 0; i < threadCount; i++) {
                    executorService.submit(() -> {
                        try {
                            Monitorable operation = new TestMonitor("Test testMultipleThreadsSameName failed",
                                    nanoTime(),
                                    false,
                                    timeout,
                                    slowTimeout);
                            operations.add(operation);
                        } finally {
                            finished.countDown();
                        }
                    });
                }

                finished.await();
                assertEquals(0, executorService.shutdownNow().size());

                waitForOperationsToComplete(operations);
                assertEquals(1, MonitoringTask.instance.getFailedOperations().size());
            }
        }
    }

    @Ignore
    @Test
    public void testMultipleThreadsSameNameSlow() throws InterruptedException
    {
        try (AllInterleavings allInterleavings = new AllInterleavingsBuilder()
                .build("cassandra.testMultipleThreadsSameNameSlow")) {

            while (allInterleavings.hasNext()) {

        final int threadCount = 2;
        final List<Monitorable> operations = new ArrayList<>(threadCount);
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        final CountDownLatch finished = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++)
        {
            executorService.submit(() -> {
                try
                {
                    Monitorable operation = new TestMonitor("Test testMultipleThreadsSameName slow",
                                                            nanoTime(),
                                                            false,
                                                            timeout,
                                                            slowTimeout);
                    operations.add(operation);
                }
                finally {
                    finished.countDown();
                }
            });
        }

        finished.await();
        assertEquals(0, executorService.shutdownNow().size());

        waitForOperationsToBeReportedAsSlow(operations);
        operations.forEach(o -> o.complete());

        assertEquals(1, MonitoringTask.instance.getSlowOperations().size());
            }
        }
    }

    @Ignore
    @Test
    public void testMultipleThreadsNoFailedOps() throws InterruptedException
    {
        try (AllInterleavings allInterleavings = new AllInterleavingsBuilder()
                .build("cassandra.testMultipleThreadsNoFailedOps")) {
            while (allInterleavings.hasNext()) {
        final int threadCount = 2;
        final List<Monitorable> operations = new ArrayList<>(threadCount);
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        final CountDownLatch finished = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++)
        {
            executorService.submit(() -> {
                try
                {
                    Monitorable operation = new TestMonitor("Test thread " + currentThread().getName(),
                                                            nanoTime(),
                                                            false,
                                                            timeout,slowTimeout);
                    operations.add(operation);
                    operation.complete();
                }
                finally
                {
                    finished.countDown();
                }
            });
        }
        finished.await();
        assertEquals(0, executorService.shutdownNow().size());

        waitForOperationsToComplete(operations);
        assertEquals(0, MonitoringTask.instance.getFailedOperations().size());
            }
        }
    }
}
