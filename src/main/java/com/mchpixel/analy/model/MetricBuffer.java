package com.mchpixel.analy.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

public class MetricBuffer {

    // The actual queue — holds metrics until they get flushed
    // 500 is the max size, matching what we defined in config.yml
    private final ArrayBlockingQueue<Metric> queue;
    private final int maxSize;

    public MetricBuffer(int maxSize) {
        this.maxSize = maxSize;
        this.queue = new ArrayBlockingQueue<>(maxSize);
    }

    // Called by collectors to add a metric to the buffer
    // Returns true if it was added, false if the buffer is full
    public boolean add(Metric metric) {
        boolean added = queue.offer(metric);
        if (!added) {
            // Buffer is full — this is worth knowing about
            System.out.println("[Analy] Buffer full! Dropping metric: " + metric.getMetric());
        }
        return added;
    }

    // Called by the flush scheduler — drains everything currently in the queue
    // into a list and returns it. The queue is empty after this.
    public List<Metric> flush() {
        List<Metric> batch = new ArrayList<>();
        queue.drainTo(batch);
        return batch;
    }


    // ___________________________________
    // |                                 |
    // |    For Debugging and Logging    |
    // |                                 |

    // Just useful for logging/debugging
    public int size() {
        return queue.size();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public int getMaxSize() {
        return maxSize;
    }
}