package com.tourguide.util;

import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.function.Function;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

public class ConcurrentThrottler<T, R> {
    private final Function<T, CompletableFuture<R>> function;
    private final Integer logLimit;
    private final Semaphore semaphore;
    private final Queue<PendingElement<T, R>> pendingQueue;

    @lombok.Builder(builderClassName = "Builder")
    private ConcurrentThrottler(
            Integer limit,
            Semaphore semaphore,
            @NonNull Function<T, CompletableFuture<R>> function
    ) {
        if (limit != null && semaphore != null) {
            throw new IllegalArgumentException("limit and semaphore are exclusive");
        }
        if (limit == null && semaphore == null) {
            throw new IllegalArgumentException("limit or semaphore is required");
        }

        this.function = function;
        this.logLimit = limit;
        this.semaphore = limit != null ? new Semaphore(limit) : semaphore;
        this.pendingQueue = new ConcurrentLinkedQueue<>();
    }

    public CompletableFuture<R> call(T element) {
        if (semaphore.tryAcquire()) {
            return startThenPollAndRelease(element);
        }

        PendingElement<T, R> ret = new PendingElement<>(element);
        pendingQueue.add(ret);
        pollPendingQueue(false);
        return ret;
    }

    public CompletableFuture<Void> callAll(Collection<T> elements) {
        return elements.isEmpty()
                ? CompletableFuture.completedFuture(null)
                : CompletableFuture.allOf(elements.stream().map(this::call).toArray(CompletableFuture[]::new));
    }

    private CompletableFuture<R> startThenPollAndRelease(T element) {
        CompletableFuture<R> ret;
        try {
            ret = function.apply(element);
        } catch (Throwable t) {
            ret = new CompletableFuture<>();
            ret.completeExceptionally(t);
        }
        ret.whenComplete((ignored1, ignored2) -> {
            if (pollPendingQueue(true)) {
                return;
            }
            semaphore.release();
        });
        return ret;
    }

    private boolean pollPendingQueue(boolean alreadyAcquired) {
        if (pendingQueue.isEmpty()) {
            return false;
        }

        if (!alreadyAcquired && !semaphore.tryAcquire()) {
            return false;
        }

        PendingElement<T, R> pending = pendingQueue.poll();
        if (pending == null) {
            if (!alreadyAcquired) {
                semaphore.release();
            }
            return false;
        }

        startThenPollAndRelease(pending.element).whenComplete((res, ex) -> {
            if (ex != null) {
                pending.completeExceptionally(ex);
            } else {
                pending.complete(res);
            }
        });
        return true;
    }

    public String logString() {
        int availablePermits = semaphore.availablePermits();
        int queueSize = pendingQueue.size();
        return (logLimit != null
                ? "running: " + (logLimit - availablePermits)
                : "available: " + availablePermits)
                + ", queued: " + queueSize;
    }

    @RequiredArgsConstructor
    private static class PendingElement<T, R> extends CompletableFuture<R> {
        private final T element;
    }
}
