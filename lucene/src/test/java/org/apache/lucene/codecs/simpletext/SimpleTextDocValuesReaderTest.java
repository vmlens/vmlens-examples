
package org.apache.lucene.codecs.simpletext;

import com.vmlens.api.testbuilder.AtomicTestBuilder;
import org.apache.lucene.index.FieldInfo;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;
import java.io.IOException;
import java.io.UncheckedIOException;

public class SimpleTextDocValuesReaderTest {

    //@Test
    public void testSimpleTextDocValuesReader() throws InterruptedException {
        FieldInfo fieldInfo1 = mock(FieldInfo.class);
        FieldInfo fieldInfo2 = mock(FieldInfo.class);

        new AtomicTestBuilder<>(() -> mock(SimpleTextDocValuesReader.class))
            .addUpdate(reader -> {
                try {
                    return reader.getNumeric(fieldInfo1);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            })
            .addUpdate(reader -> {
                try {
                    return reader.getNumeric(fieldInfo2);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            })
            .addUpdate(reader -> {
                try {
                    return reader.getBinary(fieldInfo1);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            })
            .addUpdate(reader -> {
                try {
                    return reader.getBinary(fieldInfo2);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            })
            .addUpdate(reader -> {
                try {
                    return reader.getSorted(fieldInfo1);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            })
            .addUpdate(reader -> {
                try {
                    return reader.getSorted(fieldInfo2);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            })
            .addUpdate(reader -> {
                try {
                    return reader.getSortedNumeric(fieldInfo1);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            })
            .addUpdate(reader -> {
                try {
                    return reader.getSortedNumeric(fieldInfo2);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            })
            .addUpdate(reader -> {
                try {
                    return reader.getSortedSet(fieldInfo1);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            })
            .addUpdate(reader -> {
                try {
                    return reader.getSortedSet(fieldInfo2);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            })
            .addUpdate(reader -> {
                try {
                    return reader.getNumericNonIterator(fieldInfo1);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            })
            .addUpdate(reader -> {
                try {
                    return reader.getNumericNonIterator(fieldInfo2);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            })
            .addUpdate(reader -> reader.getSkipper(fieldInfo1))
            .addUpdate(reader -> reader.getSkipper(fieldInfo2))
            .addWrite(reader -> {
                try {
                    reader.checkIntegrity();
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            })
            .addWrite(reader -> {
                try {
                    reader.close();
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            })
            .addReadOnly(reader -> reader.toString())
            .runTests();
    }
}
