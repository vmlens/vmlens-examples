
package org.apache.lucene.queryparser.xml.builders;

import com.vmlens.api.testbuilder.AtomicTestBuilder;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class UserInputQueryBuilderTest {

  @Test
  public void testUpdate() throws Exception {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder documentBuilder = factory.newDocumentBuilder();
    Document doc = documentBuilder.newDocument();

    Element element1 = doc.createElement("UserInputQuery");
    element1.appendChild(doc.createTextNode("field:term1"));

    Element element2 = doc.createElement("UserInputQuery");
    element2.appendChild(doc.createTextNode("field:term2"));

    new AtomicTestBuilder<>(() -> new UserInputQueryBuilder(new QueryParser("field", new StandardAnalyzer())))
        .addUpdate(
            builder -> {
              try {
                return builder.getQuery(element1);
              } catch (Exception e) {
                throw new RuntimeException(e);
              }
            })
        .addUpdate(
            builder -> {
              try {
                return builder.getQuery(element2);
              } catch (Exception e) {
                throw new RuntimeException(e);
              }
            })
        .runTests();
  }
}
