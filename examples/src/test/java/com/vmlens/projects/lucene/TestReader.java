package com.vmlens.projects.lucene;

import com.vmlens.api.AllInterleavings;
import com.vmlens.api.AllInterleavingsBuilder;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static com.vmlens.api.Runner.runParallel;

public class TestReader {

    @Test
    public void useWriterFromMultipleThreads() throws IOException {
        int  count = 0;
        try(AllInterleavings allInterleavings =
                    new AllInterleavingsBuilder()
                           .build("luceneReader")) {
            while(allInterleavings.hasNext()) {
                Directory directory = new ByteBuffersDirectory();
                IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
                config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
                IndexWriter indexWriter = new IndexWriter(directory, config);
                Document doc = new Document();

                // Exact match / lookup
                doc.add(new StringField("id", "2", Field.Store.YES));

                // Full-text searchable fields
                doc.add(new TextField("title", "test2", Field.Store.YES));
                indexWriter.updateDocument(new Term("id", "1"), doc);
                indexWriter.commit();


                DirectoryReader reader = DirectoryReader.open(directory);
                IndexSearcher searcher = new IndexSearcher(reader);


                runParallel(() -> {
                    QueryParser parser = new QueryParser("content",  new StandardAnalyzer());
                            Query query = null;
                            try {
                                query = parser.parse("test");
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }

                            try {
                                TopDocs topDocs = searcher.search(query, 1);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }

                        },
                        () -> {
                            QueryParser parser = new QueryParser("content",  new StandardAnalyzer());
                            Query query = null;
                            try {
                                query = parser.parse("test");
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }

                            try {
                                TopDocs topDocs = searcher.search(query, 1);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }

                        });
            }
        }

    }



}
