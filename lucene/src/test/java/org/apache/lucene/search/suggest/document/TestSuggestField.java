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
package org.apache.lucene.search.suggest.document;

import com.vmlens.api.AllInterleavings;
import com.vmlens.api.AllInterleavingsBuilder;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.PayloadAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.codecs.Codec;
import org.apache.lucene.codecs.FilterCodec;
import org.apache.lucene.codecs.PostingsFormat;
import org.apache.lucene.codecs.perfield.PerFieldPostingsFormat;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.suggest.document.TopSuggestDocs.SuggestScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.tests.analysis.MockAnalyzer;
import org.apache.lucene.tests.index.RandomIndexWriter;
import org.apache.lucene.tests.util.LuceneTestCase;
import org.apache.lucene.tests.util.TestUtil;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

public class TestSuggestField extends LuceneTestCase {

  //@Test
  public void testThreads() throws Exception {

    try (AllInterleavings allInterleavings = new AllInterleavingsBuilder()
            .build("lucene.testSuggestField")) {
      while (allInterleavings.hasNext()) {
        Directory dir= newDirectory();
    final Analyzer analyzer = new MockAnalyzer(random());
    RandomIndexWriter iw =
        new RandomIndexWriter(
            random(),
            dir,
            iwcWithSuggestField(analyzer, "suggest_field_1", "suggest_field_2", "suggest_field_3"));
    int num = 2;
    final String prefix1 = "abc1_";
    final String prefix2 = "abc2_";
    final String prefix3 = "abc3_";
    final Entry[] entries1 = new Entry[num];
    final Entry[] entries2 = new Entry[num];
    final Entry[] entries3 = new Entry[num];
    for (int i = 0; i < num; i++) {
      int weight = num - (i + 1);
      entries1[i] = new Entry(prefix1 + weight, weight);
      entries2[i] = new Entry(prefix2 + weight, weight);
      entries3[i] = new Entry(prefix3 + weight, weight);
    }
    for (int i = 0; i < num; i++) {
      Document doc = new Document();
      doc.add(new SuggestField("suggest_field_1", prefix1 + i, i));
      doc.add(new SuggestField("suggest_field_2", prefix2 + i, i));
      doc.add(new SuggestField("suggest_field_3", prefix3 + i, i));
      iw.addDocument(doc);

      if (rarely()) {
        iw.commit();
      }
    }

    DirectoryReader reader = iw.getReader();
    int numThreads = 2;
    Thread[] threads = new Thread[numThreads];
    final CopyOnWriteArrayList<Throwable> errors = new CopyOnWriteArrayList<>();
    final SuggestIndexSearcher indexSearcher = new SuggestIndexSearcher(reader);
    for (int i = 0; i < threads.length; i++) {
      threads[i] =
          new Thread() {
            @Override
            public void run() {
              try {
                PrefixCompletionQuery query =
                    new PrefixCompletionQuery(analyzer, new Term("suggest_field_1", prefix1));
                TopSuggestDocs suggest = indexSearcher.suggest(query, num, false);
                assertSuggestions(suggest, entries1);
                query = new PrefixCompletionQuery(analyzer, new Term("suggest_field_2", prefix2));
                suggest = indexSearcher.suggest(query, num, false);
                assertSuggestions(suggest, entries2);
                query = new PrefixCompletionQuery(analyzer, new Term("suggest_field_3", prefix3));
                suggest = indexSearcher.suggest(query, num, false);
                assertSuggestions(suggest, entries3);
              } catch (Throwable e) {
                errors.add(e);
              }
            }
          };
      threads[i].start();
    }


    for (Thread t : threads) {
      t.join();
    }
    assertTrue(errors.toString(), errors.isEmpty());

    reader.close();
    iw.close();
        dir.close();
      }
    }
  }

  static class Entry {
    final String output;
    final float value;
    final String context;
    final int id;

    Entry(String output, float value) {
      this(output, null, value);
    }

    Entry(String output, String context, float value) {
      this(output, context, value, -1);
    }

    Entry(String output, String context, float value, int id) {
      this.output = output;
      this.value = value;
      this.context = context;
      this.id = id;
    }

    @Override
    public String toString() {
      return "key=" + output + " score=" + value + " context=" + context + " id=" + id;
    }
  }

  static void assertSuggestions(TopDocs actual, Entry... expected) {
    SuggestScoreDoc[] suggestScoreDocs = (SuggestScoreDoc[]) actual.scoreDocs;
    for (int i = 0; i < Math.min(expected.length, suggestScoreDocs.length); i++) {
      SuggestScoreDoc lookupDoc = suggestScoreDocs[i];
      String msg =
          "Hit "
              + i
              + ": expected: "
              + toString(expected[i])
              + " but actual: "
              + toString(lookupDoc);
      assertEquals(msg, expected[i].output, lookupDoc.key.toString());
      assertEquals(msg, expected[i].value, lookupDoc.score, 0);
      assertEquals(msg, expected[i].context, lookupDoc.context);
    }
    assertEquals(expected.length, suggestScoreDocs.length);
  }

  private static String toString(Entry expected) {
    return "key:" + expected.output + " score:" + expected.value + " context:" + expected.context;
  }

  private static String toString(SuggestScoreDoc actual) {
    return "key:" + actual.key.toString() + " score:" + actual.score + " context:" + actual.context;
  }

  static IndexWriterConfig iwcWithSuggestField(Analyzer analyzer, String... suggestFields) {
    return iwcWithSuggestField(analyzer, asSet(suggestFields));
  }

  static IndexWriterConfig iwcWithSuggestField(Analyzer analyzer, final Set<String> suggestFields) {
    IndexWriterConfig iwc = newIndexWriterConfig(random(), analyzer);
    iwc.setMergePolicy(newLogMergePolicy());
    Codec filterCodec =
        new FilterCodec(TestUtil.getDefaultCodec().getName(), TestUtil.getDefaultCodec()) {
          final PostingsFormat postingsFormat = new Completion101PostingsFormat();

          @Override
          public PostingsFormat postingsFormat() {
            return new PerFieldPostingsFormat() {
              @Override
              public PostingsFormat getPostingsFormatForField(String field) {
                if (suggestFields.contains(field)) {
                  return postingsFormat;
                }
                return ((PerFieldPostingsFormat) delegate.postingsFormat())
                    .getPostingsFormatForField(field);
              }
            };
          }
        };
    iwc.setCodec(filterCodec);
    return iwc;
  }

  public static final class PayloadAttrToTypeAttrFilter extends TokenFilter {
    private final PayloadAttribute payload = addAttribute(PayloadAttribute.class);
    private final TypeAttribute type = addAttribute(TypeAttribute.class);

    protected PayloadAttrToTypeAttrFilter(TokenStream input) {
      super(input);
    }

    @Override
    public boolean incrementToken() throws IOException {
      if (input.incrementToken()) {
        // we move them over so we can assert them more easily in the tests
        type.setType(payload.getPayload().utf8ToString());
        return true;
      }
      return false;
    }
  }
}
