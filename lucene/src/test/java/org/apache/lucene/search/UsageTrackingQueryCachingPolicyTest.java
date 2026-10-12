package org.apache.lucene.search;

import com.vmlens.api.testbuilder.AtomicTestBuilder;
import java.io.IOException;
import org.junit.jupiter.api.Test;

public class UsageTrackingQueryCachingPolicyTest {

  private static class DummyQuery extends Query {
    private final int id;

    DummyQuery(int id) {
      this.id = id;
    }

    @Override
    public String toString(String field) {
      return "dummy" + id;
    }

    @Override
    public boolean equals(Object obj) {
      return obj instanceof DummyQuery other && this.id == other.id;
    }

    @Override
    public int hashCode() {
      return id;
    }

    @Override
    public void visit(QueryVisitor visitor) {
      visitor.visitLeaf(this);
    }
  }

  @Test
  public void testUpdate() throws InterruptedException {
    Query query1 = new DummyQuery(1);
    Query query2 = new DummyQuery(2);

    new AtomicTestBuilder<>(UsageTrackingQueryCachingPolicy::new)
        .addWrite(policy -> policy.onUse(query1))
        .addWrite(policy -> policy.onUse(query2))
        .addReadOnly(policy -> policy.frequency(query1))
        .addReadOnly(
            policy -> {
              try {
                return policy.shouldCache(query1);
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
            })
        .runTests();
  }
}