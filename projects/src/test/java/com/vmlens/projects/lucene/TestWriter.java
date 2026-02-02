package com.vmlens.projects.lucene;

import com.vmlens.api.AllInterleavings;
import com.vmlens.api.AllInterleavingsBuilder;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static com.vmlens.api.Runner.runParallel;

public class TestWriter {

    @Test
    public void useWriterFromMultipleThreads() throws IOException {
        try(AllInterleavings allInterleavings =
                    new AllInterleavingsBuilder().build("luceneWriter")) {
            while(allInterleavings.hasNext()) {
                Directory directory = new ByteBuffersDirectory();
                IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
                config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
                IndexWriter indexWriter = new IndexWriter(directory, config);
                runParallel(() -> {
                            Document doc = new Document();

                            // Exact match / lookup
                            doc.add(new StringField("id", "1", Field.Store.YES));

                            // Full-text searchable fields
                            doc.add(new TextField("title", "test", Field.Store.YES));


                            try {
                                indexWriter.updateDocument(new Term("id", "1"), doc);
                                indexWriter.commit();
                            } catch (IOException e) {
                                e.printStackTrace();
                                throw new RuntimeException(e);
                            }
                        },
                        () -> {
                            Document doc = new Document();

                            // Exact match / lookup
                            doc.add(new StringField("id", "2", Field.Store.YES));

                            // Full-text searchable fields
                            doc.add(new TextField("title", "test2", Field.Store.YES));


                            try {
                                indexWriter.updateDocument(new Term("id", "1"), doc);
                                indexWriter.commit();
                            } catch (IOException e) {
                                e.printStackTrace();
                                throw new RuntimeException(e);
                            }
                        });
            }
        }

    }


}
