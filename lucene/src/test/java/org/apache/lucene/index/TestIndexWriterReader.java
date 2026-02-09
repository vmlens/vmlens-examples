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
package org.apache.lucene.index;

import com.vmlens.api.AllInterleavings;
import com.vmlens.api.AllInterleavingsBuilder;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.tests.analysis.MockAnalyzer;
import org.apache.lucene.tests.index.DocHelper;
import org.apache.lucene.tests.store.MockDirectoryWrapper;
import org.apache.lucene.tests.util.LuceneTestCase;
import org.apache.lucene.tests.util.LuceneTestCase.SuppressCodecs;
import org.apache.lucene.tests.util.TestUtil;
import org.apache.lucene.util.Bits;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.ThreadInterruptedException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressCodecs("SimpleText") // too slow here
public class TestIndexWriterReader extends LuceneTestCase {

  private final int numThreads =  2;

  public static int count(Term t, IndexReader r) throws IOException {
    int count = 0;
    PostingsEnum td = TestUtil.docs(random(), r, t.field(), new BytesRef(t.text()), null, 0);

    if (td != null) {
      final Bits liveDocs = MultiBits.getLiveDocs(r);
      while (td.nextDoc() != DocIdSetIterator.NO_MORE_DOCS) {
        td.docID();
        if (liveDocs == null || liveDocs.get(td.docID())) {
          count++;
        }
      }
    }
    return count;
  }


/*
  public void do_NottestAddIndexesAndDoDeletesThreads() throws Throwable {
    try (AllInterleavings allInterleavings = new AllInterleavingsBuilder()
            .build("lucene.testAddIndexesAndDoDeletesThreads")) {

      while (allInterleavings.hasNext()) {
      final int numIter = 1;
      int numDirs = 2;

      Directory mainDir = getAssertNoDeletesDirectory(newDirectory());

      IndexWriter mainWriter =
              new IndexWriter(
                      mainDir,
                      newIndexWriterConfig(new MockAnalyzer(random()))
                              .setMergePolicy(newLogMergePolicy())
                              .setMaxFullFlushMergeWaitMillis(0));
      TestUtil.reduceOpenFiles(mainWriter);

      AddDirectoriesThreads addDirThreads = new AddDirectoriesThreads(numIter, mainWriter);
      addDirThreads.launchThreads(numDirs);
      addDirThreads.joinThreads();

      // assertEquals(100 + numDirs * (3 * numIter / 4) * addDirThreads.numThreads
      //    * addDirThreads.NUM_INIT_DOCS, addDirThreads.mainwriter.getDocStats().numDocs);
      assertEquals(addDirThreads.count.intValue(), addDirThreads.mainWriter.getDocStats().numDocs);

      addDirThreads.close(true);

      assertTrue(addDirThreads.failures.isEmpty());

      TestUtil.checkIndex(mainDir);

      IndexReader reader = DirectoryReader.open(mainDir);
      assertEquals(addDirThreads.count.intValue(), reader.numDocs());
      // assertEquals(100 + numDirs * (3 * numIter / 4) * addDirThreads.numThreads
      //    * addDirThreads.NUM_INIT_DOCS, reader.numDocs());
      reader.close();

      addDirThreads.closeDir();
      mainDir.close();
    }
  }
  }
*/
  private class AddDirectoriesThreads {
    Directory addDir;
    static final int NUM_INIT_DOCS = 2;
    int numDirs;
    final Thread[] threads = new Thread[numThreads];
    IndexWriter mainWriter;
    final List<Throwable> failures = new ArrayList<>();
    DirectoryReader[] readers;
    AtomicInteger count = new AtomicInteger(0);
    AtomicInteger numaddIndexes = new AtomicInteger(0);

    public AddDirectoriesThreads(int numDirs, IndexWriter mainWriter) throws Throwable {
      this.numDirs = numDirs;
      this.mainWriter = mainWriter;
      addDir = newDirectory();
      IndexWriter writer =
          new IndexWriter(
              addDir,
              newIndexWriterConfig(new MockAnalyzer(random()))
                  .setMaxFullFlushMergeWaitMillis(0)
                  .setMaxBufferedDocs(2));
      TestUtil.reduceOpenFiles(writer);
      for (int i = 0; i < NUM_INIT_DOCS; i++) {
        Document doc = DocHelper.createDocument(i, "addindex", 4);
        writer.addDocument(doc);
      }

      writer.close();

      readers = new DirectoryReader[numDirs];
      for (int i = 0; i < numDirs; i++) readers[i] = DirectoryReader.open(addDir);
    }

    void joinThreads() {
      for (int i = 0; i < numThreads; i++)
        try {
          threads[i].join();
        } catch (InterruptedException ie) {
          throw new ThreadInterruptedException(ie);
        }
    }

    void close(boolean doWait) throws Throwable {
      if (doWait) {
        mainWriter.close();
      } else {
        mainWriter.rollback();
      }
    }

    void closeDir() throws Throwable {
      for (int i = 0; i < numDirs; i++) {
        readers[i].close();
      }
      addDir.close();
    }

    void handle(Throwable t) {
      t.printStackTrace(System.out);
      synchronized (failures) {
        failures.add(t);
      }
    }

    void launchThreads(final int numIter) {
      for (int i = 0; i < numThreads; i++) {
        threads[i] =
            new Thread() {
              @Override
              public void run() {
                try {
                  final Directory[] dirs = new Directory[numDirs];
                  for (int k = 0; k < numDirs; k++)
                    dirs[k] = new MockDirectoryWrapper(random(), TestUtil.ramCopyOf(addDir));
                  // int j = 0;
                  // while (true) {
                  // System.out.println(Thread.currentThread().getName() + ": iter
                  // j=" + j);
                  for (int x = 0; x < numIter; x++) {
                    // only do addIndexes
                    doBody(x, dirs);
                  }
                  // if (numIter > 0 && j == numIter)
                  //  break;
                  // doBody(j++, dirs);
                  // doBody(5, dirs);
                  // }
                } catch (Throwable t) {
                  handle(t);
                }
              }
            };
      }
      for (int i = 0; i < numThreads; i++) threads[i].start();
    }

    void doBody(int j, Directory[] dirs) throws Throwable {
      switch (j % 4) {
        case 0:
          mainWriter.addIndexes(dirs);
          mainWriter.forceMerge(1);
          break;
        case 1:
          mainWriter.addIndexes(dirs);
          numaddIndexes.incrementAndGet();
          break;
        case 2:
          TestUtil.addIndexesSlowly(mainWriter, readers);
          break;
        case 3:
          mainWriter.commit();
      }
      count.addAndGet(dirs.length * NUM_INIT_DOCS);
    }
  }



  /*
   * Delete a document by term and return the doc id
   *
   * public static int deleteDocument(Term term, IndexWriter writer) throws
   * IOException { IndexReader reader = writer.getReader(); TermDocs td =
   * reader.termDocs(term); int doc = -1; //if (td.next()) { // doc = td.storedFields().document();
   * //} //writer.deleteDocuments(term); td.close(); return doc; }
   */

  public static void createIndex(
      Random random, Directory dir1, String indexName, boolean multiSegment) throws IOException {
    IndexWriter w =
        new IndexWriter(
            dir1,
            LuceneTestCase.newIndexWriterConfig(random, new MockAnalyzer(random))
                .setMergePolicy(new LogDocMergePolicy()));
    for (int i = 0; i < 100; i++) {
      w.addDocument(DocHelper.createDocument(i, indexName, 4));
    }
    if (!multiSegment) {
      w.forceMerge(1);
    }
    w.close();
  }

  public static void createIndexNoClose(boolean multiSegment, String indexName, IndexWriter w)
      throws IOException {
    for (int i = 0; i < 100; i++) {
      w.addDocument(DocHelper.createDocument(i, indexName, 4));
    }
    if (!multiSegment) {
      w.forceMerge(1);
    }
  }

/*
  public void testAfterCommit() throws Exception {
    try (AllInterleavings allInterleavings = new AllInterleavingsBuilder()
            .withMaximumIterations(1)
            .build("lucene.testAddIndexesAndDoDeletesThreads")) {

      while (allInterleavings.hasNext()) {
        Directory dir1 = getAssertNoDeletesDirectory(newDirectory());
        IndexWriter writer =
                new IndexWriter(
                        dir1,
                        newIndexWriterConfig(new MockAnalyzer(random()))
                                .setMergeScheduler(new ConcurrentMergeScheduler())
                                .setMaxFullFlushMergeWaitMillis(0));
        writer.commit();

        // create the index
        createIndexNoClose(false, "test", writer);

        // get a reader to put writer into near real-time mode
        DirectoryReader r1 = DirectoryReader.open(writer);
        TestUtil.checkIndex(dir1);
        writer.commit();
        TestUtil.checkIndex(dir1);
        assertEquals(100, r1.numDocs());

        for (int i = 0; i < 10; i++) {
          writer.addDocument(DocHelper.createDocument(i, "test", 4));
        }
        ((ConcurrentMergeScheduler) writer.getConfig().getMergeScheduler()).sync();

        DirectoryReader r2 = DirectoryReader.openIfChanged(r1);
        if (r2 != null) {
          r1.close();
          r1 = r2;
        }
        assertEquals(110, r1.numDocs());
        writer.close();
        r1.close();
        dir1.close();
      }
    }
  }
*/



  private Directory getAssertNoDeletesDirectory(Directory directory) {
    if (directory instanceof MockDirectoryWrapper) {
      ((MockDirectoryWrapper) directory).setAssertNoDeleteOpenFile(true);
    }
    return directory;
  }

  /*
  // Stress test reopen during add/delete
  public void doNotTestDuringAddDelete() throws Exception {
    try (AllInterleavings allInterleavings = new AllInterleavingsBuilder()
            .build("lucene.testAddIndexesAndDoDeletesThreads")) {

      while (allInterleavings.hasNext()) {
        Directory dir1 = newDirectory();
        IndexWriterConfig iwc =
                newIndexWriterConfig(new MockAnalyzer(random())).setMergePolicy(newLogMergePolicy(2));

        final IndexWriter writer = new IndexWriter(dir1, iwc);

        // create the index
        createIndexNoClose(false, "test", writer);
        writer.commit();

        DirectoryReader r = DirectoryReader.open(writer);

        final int iters = 1;
        final List<Throwable> excs = Collections.synchronizedList(new ArrayList<>());

        final Thread[] threads = new Thread[numThreads];
        final AtomicInteger remainingThreads = new AtomicInteger(numThreads);
        for (int i = 0; i < numThreads; i++) {
          threads[i] =
                  new Thread() {
                    final Random r = new Random(random().nextLong());

                    @Override
                    public void run() {
                      int count = 0;
                      do {
                        try {
                          for (int docUpto = 0; docUpto < 10; docUpto++) {
                            writer.addDocument(DocHelper.createDocument(10 * count + docUpto, "test", 4));
                          }
                          count++;
                          final int limit = count * 10;
                          for (int delUpto = 0; delUpto < 5; delUpto++) {
                            int x = r.nextInt(limit);
                            writer.deleteDocuments(new Term("field3", "b" + x));
                          }
                        } catch (Throwable t) {
                          excs.add(t);
                          throw new RuntimeException(t);
                        }
                      } while (count < iters);
                      remainingThreads.decrementAndGet();
                    }
                  };
          threads[i].setDaemon(true);
          threads[i].start();
        }

        int sum = 0;
        while (remainingThreads.get() > 0) {
          DirectoryReader r2 = DirectoryReader.openIfChanged(r);
          if (r2 != null) {
            r.close();
            r = r2;
            Query q = new TermQuery(new Term("indexname", "test"));
            IndexSearcher searcher = newSearcher(r);
            sum += searcher.count(q);
          }
        }

        for (int i = 0; i < numThreads; i++) {
          threads[i].join();
        }
        // at least search once
        DirectoryReader r2 = DirectoryReader.openIfChanged(r);
        if (r2 != null) {
          r.close();
          r = r2;
        }
        Query q = new TermQuery(new Term("indexname", "test"));
        IndexSearcher searcher = newSearcher(r);
        sum += searcher.count(q);
        assertTrue("no documents found at all", sum > 0);

        assertEquals(0, excs.size());
        writer.close();

        r.close();
        dir1.close();
      }
    }
  }
 */


}
