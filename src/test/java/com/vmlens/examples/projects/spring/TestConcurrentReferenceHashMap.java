package com.vmlens.examples.projects.spring;

import com.vmlens.api.AllInterleavings;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledForJreRange;
import org.junit.jupiter.api.condition.JRE;
import org.springframework.util.ConcurrentReferenceHashMap;

public class TestConcurrentReferenceHashMap {

    @EnabledForJreRange(min = JRE.JAVA_24  )
    @Test
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
