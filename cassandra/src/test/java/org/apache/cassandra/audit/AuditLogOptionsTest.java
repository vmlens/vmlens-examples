
package org.apache.cassandra.audit;

import com.vmlens.api.testbuilder.AtomicTestBuilder;
import org.junit.jupiter.api.Test;

public class AuditLogOptionsTest {

    @Test
    public void testUpdate() throws InterruptedException {
        new AtomicTestBuilder<>(AuditLogOptions::new)
            .addReadOnly(AuditLogOptions::toString)
            .runTests();
    }
}
