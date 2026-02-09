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
package org.apache.lucene.analysis.icu.segmentation;


import com.vmlens.api.AllInterleavings;
import com.vmlens.api.AllInterleavingsBuilder;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.tests.analysis.BaseTokenStreamTestCase;

import java.io.StringReader;

public class TestICUTokenizer extends BaseTokenStreamTestCase {


  /** test for bugs like http://bugs.icu-project.org/trac/ticket/10767 */
  public void testICUConcurrency() throws Exception {
    try (AllInterleavings allInterleavings = new AllInterleavingsBuilder()
            .build("lucene.testICUTokenizer")) {
      while (allInterleavings.hasNext()) {
    int numThreads = 2;
    Thread[] threads = new Thread[numThreads];
    for (int i = 0; i < threads.length; i++) {
      threads[i] =
          new Thread() {
            @Override
            public void run() {
              try {
                long tokenCount = 0;
                final String contents = "英 เบียร์ ビール ເບຍ abc";
                for (int i = 0; i < 3; i++) {
                  try (Tokenizer tokenizer = new ICUTokenizer()) {
                    tokenizer.setReader(new StringReader(contents));
                    tokenizer.reset();
                    while (tokenizer.incrementToken()) {
                      tokenCount++;
                    }
                    tokenizer.end();
                  }
                }
              } catch (Exception e) {
                throw new RuntimeException(e);
              }
            }
          };
      threads[i].start();
    }
    for (int i = 0; i < threads.length; i++) {
      threads[i].join();
    }
  }
    }
  }

}
