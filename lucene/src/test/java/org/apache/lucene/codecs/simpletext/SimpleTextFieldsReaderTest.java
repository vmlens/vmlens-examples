package org.apache.lucene.codecs.simpletext;

import org.junit.jupiter.api.Test;
import com.vmlens.api.testbuilder.AtomicTestBuilder;
import org.apache.lucene.codecs.simpletext.SimpleTextFieldsReader;
import org.apache.lucene.codecs.simpletext.SimpleTextCodec;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.CodecReader;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SimpleTextFieldsReaderTest {

  private static SimpleTextFieldsReader createReader() {
    try {
      Directory dir = new ByteBuffersDirectory();
      IndexWriterConfig config = new IndexWriterConfig(null);
      config.setCodec(new SimpleTextCodec());
      try (IndexWriter writer = new IndexWriter(dir, config)) {
        Document doc = new Document();
        doc.add(new StringField("field1", "value1", Field.Store.NO));
        doc.add(new StringField("field2", "value2", Field.Store.NO));
        writer.addDocument(doc);
        writer.commit();
      }
      DirectoryReader reader = DirectoryReader.open(dir);
      CodecReader codecReader = (CodecReader) reader.leaves().get(0).reader();
      return (SimpleTextFieldsReader) codecReader.getPostingsReader();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  public void testUpdate() throws InterruptedException {
    new AtomicTestBuilder<>(SimpleTextFieldsReaderTest::createReader)
        .addUpdate(
            reader -> {
              try {
                return reader.terms("field1");
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
            })
        .addUpdate(
            reader -> {
              try {
                return reader.terms("field2");
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
            })
        .addWrite(
            reader -> {
              try {
                reader.close();
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
            })
        .addWrite(
            reader -> {
              try {
                reader.checkIntegrity();
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
            })
        .addReadOnly(
            reader -> {
              List<String> list = new ArrayList<>();
              reader.iterator().forEachRemaining(list::add);
              return list;
            })
        .addReadOnly(SimpleTextFieldsReader::size)
        .addReadOnly(SimpleTextFieldsReader::toString)
        .runTests();
  }
}
