package com.vmlens.projects.h2;

import com.vmlens.api.AllInterleavings;
import com.vmlens.api.AllInterleavingsBuilder;
import org.h2.mvstore.MVStore;
import org.h2.mvstore.tx.Transaction;
import org.h2.mvstore.tx.TransactionStore;
import org.junit.jupiter.api.Test;

import static com.vmlens.api.Runner.runParallel;

public class TestTransactionStore {

    @Test
    public void testTransactionStore()  {
        final MVStore s = new MVStore.Builder().open();
        TransactionStore ts = new TransactionStore(s);
        ts.init();
        try (AllInterleavings allInterleavings =
                     new AllInterleavings("h2TransactionStore")) {
            while (allInterleavings.hasNext()) {
                runParallel(() -> {Transaction transaction = ts.begin();
                                   transaction.rollback();
                        } ,
                        () -> {Transaction transaction = ts.begin();
                               transaction.commit();} );
            }
        }
        s.close();
    }

}
