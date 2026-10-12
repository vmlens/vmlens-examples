package org.apache.lucene.index;

import org.junit.jupiter.api.Test;
import com.vmlens.api.testbuilder.AtomicTestBuilder;
import org.apache.lucene.store.ByteBuffersDirectory;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

public class CheckIndexTest {

    @Test
    public void testCheckIndex() throws InterruptedException {
        new AtomicTestBuilder<>(() -> {
            try {
                return new CheckIndex(new ByteBuffersDirectory());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        })
        .addWrite(c -> c.setLevel(1))
        .addWrite(c -> c.setLevel(2))
        .addReadOnly(CheckIndex::getLevel)
        .addWrite(c -> c.setFailFast(true))
        .addWrite(c -> c.setFailFast(false))
        .addReadOnly(CheckIndex::getFailFast)
        .addWrite(c -> c.setThreadCount(1))
        .addWrite(c -> c.setThreadCount(4))
        .addWrite(c -> c.setInfoStream(System.out, true))
        .addWrite(c -> c.setInfoStream(null, false))
        .addWrite(c -> c.setInfoStream(System.out))
        .addWrite(c -> c.setInfoStream(null))
        .addUpdate(c -> {
            try {
                return c.checkIndex();
            } catch (IOException e) {
                return null;
            }
        })
        .addUpdate(c -> {
            try {
                return c.checkIndex(new ArrayList<String>());
            } catch (IOException e) {
                return null;
            }
        })
        .addUpdate(c -> {
            try {
                return c.checkIndex(null);
            } catch (IOException e) {
                return null;
            }
        })
        .addWrite(c -> {
            try {
                c.exorciseIndex(new CheckIndex.Status());
            } catch (Exception e) {
            }
        })
        .addWrite(c -> {
            try {
                c.exorciseIndex(null);
            } catch (Exception e) {
            }
        })
        .runTests();
    }
}