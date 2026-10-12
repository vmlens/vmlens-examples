
package org.apache.cassandra.schema;

import java.nio.ByteBuffer;
import org.junit.jupiter.api.Test;
import com.vmlens.api.testbuilder.AtomicTestBuilder;

public class PartitionDenylistTest {

    @Test
    public void testUpdate() throws InterruptedException {
        new AtomicTestBuilder<>(PartitionDenylist::new)
            .addWrite(PartitionDenylist::initialLoad)
            .addWrite(PartitionDenylist::load)
            .addUpdate(denylist -> denylist.addKeyToDenylist("ks1", "tbl1", ByteBuffer.wrap(new byte[]{ 1 })))
            .addUpdate(denylist -> denylist.addKeyToDenylist("ks2", "tbl2", ByteBuffer.wrap(new byte[]{ 2 })))
            .addUpdate(denylist -> denylist.removeKeyFromDenylist("ks1", "tbl1", ByteBuffer.wrap(new byte[]{ 1 })))
            .addUpdate(denylist -> denylist.removeKeyFromDenylist("ks2", "tbl2", ByteBuffer.wrap(new byte[]{ 2 })))
            .addReadOnly(PartitionDenylist::getLoadAttempts)
            .addReadOnly(PartitionDenylist::getLoadSuccesses)
            .addReadOnly(denylist -> denylist.isKeyPermitted("ks1", "tbl1", ByteBuffer.wrap(new byte[]{ 1 })))
            .addReadOnly(denylist -> denylist.isKeyPermitted((TableId) null, ByteBuffer.wrap(new byte[]{ 1 })))
            .addReadOnly(denylist -> denylist.getDeniedKeysInRangeCount("ks1", "tbl1", null))
            .addReadOnly(denylist -> denylist.getDeniedKeysInRangeCount((TableId) null, null))
            .runTests();
    }
}
