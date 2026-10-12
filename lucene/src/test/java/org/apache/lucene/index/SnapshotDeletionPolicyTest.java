
package org.apache.lucene.index;

import com.vmlens.api.testbuilder.AtomicTestBuilder;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.lucene.store.Directory;
import org.junit.jupiter.api.Test;

public class SnapshotDeletionPolicyTest {

  private static class MockIndexCommit extends IndexCommit {
    private final long gen;

    MockIndexCommit(long gen) {
      this.gen = gen;
    }

    @Override
    public String getSegmentsFileName() {
      return "segments_" + gen;
    }

    @Override
    public Collection<String> getFileNames() {
      return Collections.emptyList();
    }

    @Override
    public Directory getDirectory() {
      return null;
    }

    @Override
    public void delete() {}

    @Override
    public boolean isDeleted() {
      return false;
    }

    @Override
    public int getSegmentCount() {
      return 1;
    }

    @Override
    public long getGeneration() {
      return gen;
    }

    @Override
    public Map<String, String> getUserData() {
      return Collections.emptyMap();
    }
  }

  @Test
  public void testUpdate() throws InterruptedException {
    IndexCommit commit1 = new MockIndexCommit(1);
    IndexCommit commit2 = new MockIndexCommit(2);
    IndexCommit commit3 = new MockIndexCommit(3);
    IndexCommit commit4 = new MockIndexCommit(4);

    List<IndexCommit> commitsInit1 = Arrays.asList(commit1, commit2);
    List<IndexCommit> commitsInit2 = Arrays.asList(commit3, commit4);
    List<IndexCommit> commitsCommit1 = Collections.singletonList(commit3);
    List<IndexCommit> commitsCommit2 = Collections.singletonList(commit4);

    IndexDeletionPolicy primary =
        new IndexDeletionPolicy() {
          @Override
          public void onInit(List<? extends IndexCommit> commits) {}

          @Override
          public void onCommit(List<? extends IndexCommit> commits) {}
        };

    new AtomicTestBuilder<>(
            () -> {
              SnapshotDeletionPolicy policy = new SnapshotDeletionPolicy(primary);
              try {
                policy.onInit(commitsInit1);
                policy.snapshot();
                policy.onCommit(Collections.singletonList(commit1));
                policy.snapshot();
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
              return policy;
            })
        .addWrite(
            p -> {
              try {
                p.onCommit(commitsCommit1);
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
            })
        .addWrite(
            p -> {
              try {
                p.onCommit(commitsCommit2);
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
            })
        .addWrite(
            p -> {
              try {
                p.onInit(commitsInit1);
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
            })
        .addWrite(
            p -> {
              try {
                p.onInit(commitsInit2);
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
            })
        .addWrite(
            p -> {
              try {
                p.release(commit1);
              } catch (Exception e) {
                // Ignore illegal argument/state during concurrent calls
              }
            })
        .addWrite(
            p -> {
              try {
                p.release(commit2);
              } catch (Exception e) {
                // Ignore illegal argument/state during concurrent calls
              }
            })
        .addUpdate(
            p -> {
              try {
                return p.snapshot();
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
            })
        .addReadOnly(SnapshotDeletionPolicy::getSnapshots)
        .addReadOnly(SnapshotDeletionPolicy::getSnapshotCount)
        .addReadOnly(p -> p.getIndexCommit(1L))
        .runTests();
  }
}
