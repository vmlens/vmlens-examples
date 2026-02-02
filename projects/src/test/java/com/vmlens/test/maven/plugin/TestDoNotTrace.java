package com.vmlens.test.maven.plugin;

import com.vmlens.api.AllInterleavings;
import org.junit.jupiter.api.Test;

import static com.vmlens.api.AllInterleavings.doNotTrace;
import static com.vmlens.api.Runner.runParallel;

public class TestDoNotTrace {

    private int j = 0;

    @Test
    public void testReadWrite() {
        try(AllInterleavings allInterleavings = new AllInterleavings("testVMLAPIDoNotTrace")) {
            while (allInterleavings.hasNext()) {
                j = 0;
                runParallel( () -> doNotTrace( () -> { j++;} ),
                        () ->  doNotTrace(  () -> { j++;}  ));
            }
        }
    }

}
