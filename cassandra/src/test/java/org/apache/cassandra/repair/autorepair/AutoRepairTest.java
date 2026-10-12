
package org.apache.cassandra.repair.autorepair;

import com.vmlens.api.testbuilder.AtomicTestBuilder;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;

public class AutoRepairTest {

    @Test
    public void testUpdate() throws InterruptedException {
        new AtomicTestBuilder<AutoRepair>(() -> {
            try {
                Constructor<AutoRepair> constructor = AutoRepair.class.getDeclaredConstructor();
                constructor.setAccessible(true);
                return constructor.newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        })
        .addWrite(AutoRepair::setup)
        .addWrite(ar -> ar.repair(AutoRepairConfig.RepairType.FULL))
        .addWrite(ar -> ar.repair(AutoRepairConfig.RepairType.INCREMENTAL))
        .addWrite(ar -> {
            try {
                ar.shutdownBlocking();
            } catch (Exception ignored) {
            }
        })
        .addReadOnly(AutoRepair::currentTimeMs)
        .addReadOnly(ar -> ar.getRepairState(AutoRepairConfig.RepairType.FULL))
        .addReadOnly(AutoRepair::getRepairExecutors)
        .addReadOnly(AutoRepair::getRepairRunnableExecutors)
        .runTests();
    }
}
