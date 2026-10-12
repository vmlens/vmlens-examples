package org.apache.lucene.util;

import org.junit.jupiter.api.Test;
import com.vmlens.api.testbuilder.AtomicTestBuilder;
import org.apache.lucene.util.Version;

public class VersionTest {

    @Test
    public void testUpdate() throws InterruptedException {
        new AtomicTestBuilder<>(() -> Version.LATEST)
            /*
             * getPackageImplementationVersion changes the internal static state 
             * 'implementationVersion' and returns a value.
             */
            .addUpdate(v -> Version.getPackageImplementationVersion())
            /*
             * Read-only methods that do not change the state of the instance.
             */
            .addReadOnly(v -> v.onOrAfter(Version.LUCENE_9_0_0))
            .addReadOnly(v -> v.toString())
            .addReadOnly(v -> v.equals(Version.LATEST))
            .addReadOnly(v -> v.hashCode())
            .runTests();
    }
}
