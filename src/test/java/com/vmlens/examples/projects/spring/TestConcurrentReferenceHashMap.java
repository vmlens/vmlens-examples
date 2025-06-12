package com.vmlens.examples.projects.spring;

import com.vmlens.api.AllInterleavings;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.util.ConcurrentReferenceHashMap;

public class TestConcurrentReferenceHashMap {


    @Test
    @Disabled
    public void readWrite() throws InterruptedException {
        try(AllInterleavings allInterleavings = new AllInterleavings("testConcurrentReferenceHashMap")) {
            while (allInterleavings.hasNext()) {
                // Given
                ConcurrentReferenceHashMap<String,String> map = new ConcurrentReferenceHashMap<>();

                // When
                Thread first = new Thread() {
                    @Override
                    public void run() {
                        map.put("ab","cd");
                    }
                };
                first.start();
                map.get("ab");
                first.join();
            }
        }
    }

}
