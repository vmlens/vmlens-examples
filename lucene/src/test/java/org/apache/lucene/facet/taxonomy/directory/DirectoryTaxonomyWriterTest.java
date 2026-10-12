package org.apache.lucene.facet.taxonomy.directory;

import com.vmlens.api.testbuilder.AtomicTestBuilder;
import java.io.IOException;
import java.util.Collections;
import org.apache.lucene.facet.taxonomy.FacetLabel;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.junit.jupiter.api.Test;

public class DirectoryTaxonomyWriterTest {

  //@Test
  public void testUpdate() throws InterruptedException {
    new AtomicTestBuilder<>(() -> {
          try {
            return new DirectoryTaxonomyWriter(new ByteBuffersDirectory());
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        })
        .addUpdate(writer -> {
          try {
            return writer.addCategory(new FacetLabel("author", "bob"));
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        })
        .addUpdate(writer -> {
          try {
            return writer.addCategory(new FacetLabel("author", "alice"));
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        })
        .addUpdate(writer -> {
          try {
            return writer.commit();
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        })
        .addUpdate(writer -> {
          try {
            return writer.prepareCommit();
          } catch (IllegalStateException e) {
            return -1L;
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        })
        .addWrite(writer -> writer.setCacheMissesUntilFill(5))
        .addWrite(writer -> writer.setCacheMissesUntilFill(10))
        .addWrite(writer -> writer.setLiveCommitData(Collections.singletonMap("epoch", "1").entrySet()))
        .addWrite(writer -> writer.setLiveCommitData(Collections.singletonMap("epoch", "2").entrySet()))
        .addReadOnly(DirectoryTaxonomyWriter::getSize)
        .addReadOnly(writer -> {
          try {
            return writer.getParent(0);
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        })
        .addReadOnly(DirectoryTaxonomyWriter::getTaxonomyEpoch)
        .addReadOnly(DirectoryTaxonomyWriter::getDirectory)
        .addReadOnly(DirectoryTaxonomyWriter::getCache)
        .addReadOnly(DirectoryTaxonomyWriter::getLiveCommitData)
        .runTests();
  }
}
