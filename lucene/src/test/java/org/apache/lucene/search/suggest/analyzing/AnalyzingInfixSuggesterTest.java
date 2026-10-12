
package org.apache.lucene.search.suggest.analyzing;

import com.vmlens.api.testbuilder.AtomicTestBuilder;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.util.BytesRef;
import org.junit.jupiter.api.Test;

public class AnalyzingInfixSuggesterTest {

  @Test
  public void testUpdate() throws InterruptedException {
    new AtomicTestBuilder<AnalyzingInfixSuggester>(
            () -> {
              try {
                return new AnalyzingInfixSuggester(
                    new ByteBuffersDirectory(), new StandardAnalyzer());
              } catch (Exception e) {
                throw new RuntimeException(e);
              }
            })
        .addWrite(
            s -> {
              try {
                s.add(new BytesRef("apple"), null, 10L, null);
              } catch (Exception e) {
                // ignore exceptions during concurrent state transitions
              }
            })
        .addWrite(
            s -> {
              try {
                s.add(new BytesRef("banana"), null, 20L, null);
              } catch (Exception e) {
                // ignore exceptions during concurrent state transitions
              }
            })
        .addWrite(
            s -> {
              try {
                s.update(new BytesRef("apple"), null, 15L, null);
              } catch (Exception e) {
                // ignore exceptions during concurrent state transitions
              }
            })
        .addWrite(
            s -> {
              try {
                s.update(new BytesRef("banana"), null, 25L, null);
              } catch (Exception e) {
                // ignore exceptions during concurrent state transitions
              }
            })
        .addWrite(
            s -> {
              try {
                s.commit();
              } catch (Exception e) {
                // ignore exceptions during concurrent state transitions
              }
            })
        .addWrite(
            s -> {
              try {
                s.refresh();
              } catch (Exception e) {
                // ignore exceptions during concurrent state transitions
              }
            })
        .addWrite(
            s -> {
              try {
                s.close();
              } catch (Exception e) {
                // ignore exceptions during concurrent state transitions
              }
            })
        .addReadOnly(
            s -> {
              try {
                return s.getCount();
              } catch (Exception e) {
                return -1L;
              }
            })
        .addReadOnly(
            s -> {
              try {
                return s.lookup("app", null, false, 5);
              } catch (Exception e) {
                return null;
              }
            })
        .addReadOnly(AnalyzingInfixSuggester::ramBytesUsed)
        .runTests();
  }
}
