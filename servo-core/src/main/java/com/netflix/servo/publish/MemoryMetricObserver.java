/**
 * Copyright 2013 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.netflix.servo.publish;

import com.netflix.servo.Metric;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Keeps the last N observations in-memory.
 */
public final class MemoryMetricObserver extends BaseMetricObserver {

    private static final int DEFAULT_N = 10;

    private final List<Metric>[] observations;
    private int next;

    /**
     * Creates a new instance that keeps 10 copies in memory.
     */
    public MemoryMetricObserver() {
        this("unamed observer", DEFAULT_N);
    }

    /**
     * Creates a new instance that keeps {@code num} copies in memory.
     */
    @SuppressWarnings("unchecked")
    public MemoryMetricObserver(String name, int num) {
        super(name);
        observations = (List<Metric>[]) new List[num];
        next = 0;
    }

    /**
     * {@inheritDoc}
     */
    public void updateImpl(List<Metric> metrics) {
        observations[next] = metrics;
        next = (next + 1) % observations.length;
    }

    /**
     * Returns the current set of observations.
     */
    public List<List<Metric>> getObservations() {
        List<List<Metric>> builder = new ArrayList<List<Metric>>();
        int pos = next;
        for (List<Metric> ignored : observations) {
            if (observations[pos] != null) {
                builder.add(observations[pos]);
            }
            pos = (pos + 1) % observations.length;
        }
        return Collections.unmodifiableList(builder);
    }
}
