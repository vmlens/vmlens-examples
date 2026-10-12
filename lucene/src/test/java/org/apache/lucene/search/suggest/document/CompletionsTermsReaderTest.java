
package org.apache.lucene.search.suggest.document;

import com.vmlens.api.testbuilder.AtomicTestBuilder;
import java.io.IOException;
import org.apache.lucene.store.IndexInput;
import org.junit.jupiter.api.Test;

public class CompletionsTermsReaderTest {

  private static CompletionsTermsReader createCompletionsTermsReader() {
    IndexInput dictIn =
        new IndexInput("test") {
          @Override
          public void close() {}

          @Override
          public long getFilePointer() {
            return 0;
          }

          @Override
          public void seek(long pos) {}

          @Override
          public long length() {
            return 100L;
          }

          @Override
          public IndexInput slice(String sliceDescription, long offset, long length) {
            return this;
          }

          @Override
          public byte readByte() {
            return 0;
          }

          @Override
          public void readBytes(byte[] b, int offset, int len) {}
        };
    return new CompletionsTermsReader(dictIn, 0L, 0L, 10L, (byte) 0);
  }

  @Test
  public void testUpdate() throws InterruptedException {
    new AtomicTestBuilder<>(CompletionsTermsReaderTest::createCompletionsTermsReader)
        .addUpdate(
            reader -> {
              try {
                return reader.suggester();
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
            })
        .addReadOnly(CompletionsTermsReader::ramBytesUsed)
        .addReadOnly(CompletionsTermsReader::getChildResources)
        .runTests();
  }
}
