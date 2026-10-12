
package com.google.common.io;

import com.vmlens.api.testbuilder.AtomicTestBuilder;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.CharBuffer;

public class CharSequenceReaderTest {

    @Test
    public void testUpdate() throws InterruptedException {
        new AtomicTestBuilder<>(() -> new CharSequenceReader("test string content"))
                .addUpdate(reader -> {
                    try {
                        return reader.read(CharBuffer.allocate(5));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .addUpdate(reader -> {
                    try {
                        return reader.read(CharBuffer.allocate(10));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .addUpdate(reader -> {
                    try {
                        return reader.read();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .addUpdate(reader -> {
                    try {
                        return reader.read(new char[5], 0, 5);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .addUpdate(reader -> {
                    try {
                        return reader.read(new char[10], 0, 10);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .addUpdate(reader -> {
                    try {
                        return reader.skip(1L);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .addUpdate(reader -> {
                    try {
                        return reader.skip(2L);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .addReadOnly(reader -> {
                    try {
                        return reader.ready();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .addReadOnly(reader -> reader.markSupported())
                .addWrite(reader -> {
                    try {
                        reader.mark(5);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .addWrite(reader -> {
                    try {
                        reader.mark(10);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .addWrite(reader -> {
                    try {
                        reader.reset();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .addWrite(reader -> reader.close())
                .runTests();
    }
}
