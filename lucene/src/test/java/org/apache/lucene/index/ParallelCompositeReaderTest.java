
package org.apache.lucene.index;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.junit.jupiter.api.Test;
import com.vmlens.api.testbuilder.AtomicTestBuilder;
import java.io.IOException;
import java.io.UncheckedIOException;

public class ParallelCompositeReaderTest {

    private static ParallelCompositeReader createReader() {
        try {
            ByteBuffersDirectory dir1 = new ByteBuffersDirectory();
            IndexWriterConfig config1 = new IndexWriterConfig(new StandardAnalyzer());
            try (IndexWriter writer = new IndexWriter(dir1, config1)) {
                writer.addDocument(new Document());
            }

            ByteBuffersDirectory dir2 = new ByteBuffersDirectory();
            IndexWriterConfig config2 = new IndexWriterConfig(new StandardAnalyzer());
            try (IndexWriter writer = new IndexWriter(dir2, config2)) {
                writer.addDocument(new Document());
            }

            CompositeReader reader1 = DirectoryReader.open(dir1);
            CompositeReader reader2 = DirectoryReader.open(dir2);

            return new ParallelCompositeReader(reader1, reader2);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    //@Test
    public void testUpdate() throws InterruptedException {
        new AtomicTestBuilder<>(ParallelCompositeReaderTest::createReader)
            // State-changing methods that do not return a value (addWrite)
            .addWrite(reader -> {
                try {
                    reader.incRef();
                } catch (Exception e) {
                    // Ignore exceptions related to closing during concurrent tests
                }
            })
            .addWrite(reader -> {
                try {
                    reader.decRef();
                } catch (Exception e) {
                    // Ignore exceptions related to closing during concurrent tests
                }
            })
            .addWrite(reader -> {
                try {
                    reader.close();
                } catch (IOException e) {
                    // Ignore exceptions related to closing during concurrent tests
                }
            })
            // State-changing methods that return a value (addUpdate)
            .addUpdate(reader -> {
                try {
                    return reader.tryIncRef();
                } catch (Exception e) {
                    return false;
                }
            })
            // Read-only methods (addReadOnly)
            .addReadOnly(ParallelCompositeReader::maxDoc)
            .addReadOnly(ParallelCompositeReader::numDocs)
            .addReadOnly(ParallelCompositeReader::getRefCount)
            .addReadOnly(ParallelCompositeReader::getReaderCacheHelper)
            .addReadOnly(ParallelCompositeReader::leaves)
            .addReadOnly(ParallelCompositeReader::hasDeletions)
            .runTests();
    }
}
