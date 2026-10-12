
package org.apache.lucene.search.suggest.analyzing;

import com.vmlens.api.testbuilder.AtomicTestBuilder;
import java.io.IOException;
import java.util.Set;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.search.suggest.InputIterator;
import org.apache.lucene.store.ByteArrayDataInput;
import org.apache.lucene.store.ByteBuffersDataOutput;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.BytesRef;
import org.junit.jupiter.api.Test;

public class AnalyzingSuggesterTest {

  private static final byte[] DATA_1 = createValidData("apple", 10);
  private static final byte[] DATA_2 = createValidData("banana", 20);

  private static byte[] createValidData(String term, long weight) {
    try {
      Directory dir = new ByteBuffersDirectory();
      Analyzer analyzer = new StandardAnalyzer();
      AnalyzingSuggester suggester = new AnalyzingSuggester(dir, "temp", analyzer);
      suggester.build(new SingleEntryIterator(term, weight));
      ByteBuffersDataOutput out = new ByteBuffersDataOutput();
      suggester.store(out);
      return out.toArrayCopy();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static class SingleEntryIterator implements InputIterator {
    private final BytesRef term;
    private final long weight;
    private boolean done = false;

    SingleEntryIterator(String term, long weight) {
      this.term = new BytesRef(term);
      this.weight = weight;
    }

    @Override
    public long weight() {
      return weight;
    }

    @Override
    public BytesRef payload() {
      return null;
    }

    @Override
    public boolean hasPayloads() {
      return false;
    }

    @Override
    public Set<BytesRef> contexts() {
      return null;
    }

    @Override
    public boolean hasContexts() {
      return false;
    }

    @Override
    public BytesRef next() {
      if (!done) {
        done = true;
        return term;
      }
      return null;
    }
  }

  private static AnalyzingSuggester createSuggester() {
    Directory dir = new ByteBuffersDirectory();
    Analyzer analyzer = new StandardAnalyzer();
    return new AnalyzingSuggester(dir, "temp", analyzer);
  }

  @Test
  public void testUpdate() throws InterruptedException {
    new AtomicTestBuilder<>(AnalyzingSuggesterTest::createSuggester)
        .addWrite(
            suggester -> {
              try {
                suggester.build(new SingleEntryIterator("apple", 10));
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
            })
        .addWrite(
            suggester -> {
              try {
                suggester.build(new SingleEntryIterator("banana", 20));
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
            })
        .addUpdate(
            suggester -> {
              try {
                return suggester.load(new ByteArrayDataInput(DATA_1));
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
            })
        .addUpdate(
            suggester -> {
              try {
                return suggester.load(new ByteArrayDataInput(DATA_2));
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
            })
        .addReadOnly(AnalyzingSuggester::getCount)
        .addReadOnly(AnalyzingSuggester::ramBytesUsed)
        .addReadOnly(AnalyzingSuggester::getChildResources)
        .addReadOnly(suggester -> suggester.lookup("app", null, false, 5))
        .addReadOnly(
            suggester -> {
              try {
                return suggester.store(new ByteBuffersDataOutput());
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
            })
        .runTests();
  }
}
