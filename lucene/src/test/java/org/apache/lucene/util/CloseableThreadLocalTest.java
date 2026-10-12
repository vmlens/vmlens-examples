
package org.apache.lucene.util;

import com.vmlens.api.testbuilder.AtomicTestBuilder;
import org.junit.jupiter.api.Test;

public class CloseableThreadLocalTest {

    @Test
    public void testUpdate() throws InterruptedException {
        new AtomicTestBuilder<CloseableThreadLocal<String>>(CloseableThreadLocal::new)
            .addWrite(ctl -> ctl.set("first"))
            .addWrite(ctl -> ctl.set("second"))
            .addReadOnly(CloseableThreadLocal::get)
            .runTests();
    }
}
