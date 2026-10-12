
package org.apache.lucene.analysis;

import com.vmlens.api.testbuilder.AtomicTestBuilder;
import org.junit.jupiter.api.Test;

public class AnalysisSPILoaderTest {

  @Test
  public void testUpdate() throws InterruptedException {
    ClassLoader cl1 = AnalysisSPILoader.class.getClassLoader();
    ClassLoader cl2 = new ClassLoader(cl1) {};

    new AtomicTestBuilder<>(() -> new AnalysisSPILoader<>(AbstractAnalysisFactory.class))
        .addWrite(loader -> loader.reload(cl1))
        .addWrite(loader -> loader.reload(cl2))
        .addReadOnly(AnalysisSPILoader::availableServices)
        .runTests();
  }
}
