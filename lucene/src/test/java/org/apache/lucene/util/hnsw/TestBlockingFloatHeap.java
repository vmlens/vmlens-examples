/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.lucene.util.hnsw;

import com.vmlens.api.AllInterleavings;
import com.vmlens.api.AllInterleavingsBuilder;
import org.apache.lucene.tests.util.LuceneTestCase;

import static com.carrotsearch.randomizedtesting.RandomizedTest.randomIntBetween;

public class TestBlockingFloatHeap extends LuceneTestCase {


  public void testMultipleThreadsPoll() throws Exception {
    try (AllInterleavings allInterleavings = new AllInterleavingsBuilder()
            .build("lucene.testMultipleThreadsPoll")) {
      while (allInterleavings.hasNext()) {
        Thread[] threads = new Thread[2];
        BlockingFloatHeap globalHeap = new BlockingFloatHeap(1);

        for (int i = 0; i < threads.length; i++) {
          threads[i] =
                  new Thread(
                          () -> {
                            try {
                              int numIterations = 1;
                              float bottomValue = 0;

                              while (numIterations-- > 0) {
                                bottomValue += 1;
                                globalHeap.offer(bottomValue);

                                float globalBottomValue = globalHeap.poll();
                                bottomValue = globalBottomValue;
                              }
                            } catch (Exception e) {
                              throw new RuntimeException(e);
                            }
                          });
          threads[i].start();
        }

        for (Thread t : threads) {
          t.join();
        }

      }
    }


  }

  public void testMultipleThreadsPeek() throws Exception {
    try (AllInterleavings allInterleavings = new AllInterleavingsBuilder()
            .build("lucene.testMultipleThreadsPeek")) {
      while (allInterleavings.hasNext()) {


    Thread[] threads = new Thread[2];
    BlockingFloatHeap globalHeap = new BlockingFloatHeap(1);

    for (int i = 0; i < threads.length; i++) {
      threads[i] =
              new Thread(
                      () -> {
                        try {

                          int numIterations = 1;
                          float bottomValue = 0;

                          while (numIterations-- > 0) {
                            bottomValue += randomIntBetween(0, 5);
                            globalHeap.offer(bottomValue);
                            Thread.sleep(randomIntBetween(0, 50));

                            float globalBottomValue = globalHeap.peek();
                            assertTrue(globalBottomValue >= bottomValue);
                            bottomValue = globalBottomValue;
                          }
                        } catch (Exception e) {
                          throw new RuntimeException(e);
                        }
                      });
      threads[i].start();
    }


    for (Thread t : threads) {
      t.join();
    }
  }
    }
  }
}
