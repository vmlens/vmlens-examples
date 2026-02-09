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
package org.apache.lucene.util;

import com.vmlens.api.AllInterleavings;
import com.vmlens.api.AllInterleavingsBuilder;
import org.apache.lucene.codecs.CodecUtil;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.IndexInput;
import org.apache.lucene.store.IndexOutput;
import org.apache.lucene.tests.util.LuceneTestCase;
import org.apache.lucene.util.OfflineSorter.ByteSequencesWriter;
import org.apache.lucene.util.OfflineSorter.SortInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/** Tests for on-disk merge sorting. */
public class TestOfflineSorter extends LuceneTestCase {

  private ExecutorService randomExecutorServiceOrNull() {
         return new ThreadPoolExecutor(
          2,
          2,
          Long.MAX_VALUE,
          TimeUnit.MILLISECONDS,
          new LinkedBlockingQueue<>(),
          new NamedThreadFactory("TestOfflineSorter"));

  }


  private byte[][] generateRandom(int howMuchDataInBytes) {
    ArrayList<byte[]> data = new ArrayList<>();
    while (howMuchDataInBytes > 0) {
      byte[] current = new byte[random().nextInt(256)];
      random().nextBytes(current);
      data.add(current);
      howMuchDataInBytes -= current.length;
    }
    byte[][] bytes = data.toArray(new byte[data.size()][]);
    return bytes;
  }

  static final Comparator<byte[]> unsignedByteOrderComparator =
      new Comparator<>() {
        @Override
        public int compare(byte[] left, byte[] right) {
          final int max = Math.min(left.length, right.length);
          for (int i = 0, j = 0; i < max; i++, j++) {
            int diff = (left[i] & 0xff) - (right[j] & 0xff);
            if (diff != 0) {
              return diff;
            }
          }
          return left.length - right.length;
        }
      };

  /** Check sorting data on an instance of {@link OfflineSorter}. */
  private SortInfo checkSort(Directory dir, OfflineSorter sorter, byte[][] data)
      throws IOException {

    IndexOutput unsorted = dir.createTempOutput("unsorted", "tmp", IOContext.DEFAULT);
    writeAll(unsorted, data);

    IndexOutput golden = dir.createTempOutput("golden", "tmp", IOContext.DEFAULT);
    Arrays.sort(data, unsignedByteOrderComparator);
    writeAll(golden, data);

    String sorted = sorter.sort(unsorted.getName());
    // System.out.println("Input size [MB]: " + unsorted.length() / (1024 * 1024));
    // System.out.println(sortInfo);
    assertFilesIdentical(dir, golden.getName(), sorted);

    return sorter.sortInfo;
  }

  /** Make sure two files are byte-byte identical. */
  private void assertFilesIdentical(Directory dir, String golden, String sorted)
      throws IOException {
    long numBytes = dir.fileLength(golden);
    assertEquals(numBytes, dir.fileLength(sorted));

    byte[] buf1 = new byte[64 * 1024];
    byte[] buf2 = new byte[64 * 1024];
    try (IndexInput in1 = dir.openInput(golden, IOContext.READONCE);
         IndexInput in2 = dir.openInput(sorted, IOContext.READONCE)) {
      long left = numBytes;
      while (left > 0) {
        int chunk = (int) Math.min(buf1.length, left);
        left -= chunk;
        in1.readBytes(buf1, 0, chunk);
        in2.readBytes(buf2, 0, chunk);
        for (int i = 0; i < chunk; i++) {
          assertEquals(buf1[i], buf2[i]);
        }
      }
    }
  }

  /** NOTE: closes the provided {@link IndexOutput} */
  private void writeAll(IndexOutput out, byte[][] data) throws IOException {
    try (ByteSequencesWriter w = new ByteSequencesWriter(out)) {
      for (byte[] datum : data) {
        w.write(datum);
      }
      CodecUtil.writeFooter(out);
    }
  }



  public void testThreadSafety() throws Exception {
    try (AllInterleavings allInterleavings = new AllInterleavingsBuilder()
            .build("lucene.testThreadSafety")) {
      while (allInterleavings.hasNext()) {
    Thread[] threads = new Thread[2];
    final AtomicBoolean failed = new AtomicBoolean();
    final int iters = 5;
    try (Directory dir = newDirectory()) {
      for (int i = 0; i < threads.length; i++) {
        final int threadID = i;
        threads[i] =
            new Thread() {
              @Override
              public void run() {
                try {
                  for (int iter = 0; iter < iters && failed.get() == false; iter++) {
                    checkSort(
                        dir,
                        new OfflineSorter(dir, "foo_" + threadID + "_" + iter),
                        generateRandom(1024));
                  }
                } catch (Throwable th) {
                  failed.set(true);
                  throw new RuntimeException(th);
                }
              }
            };
        threads[i].start();
      }
      for (Thread thread : threads) {
        thread.join();
      }
    }

    assertFalse(failed.get());
  }
    }
  }
}
