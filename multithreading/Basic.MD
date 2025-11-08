# Java Multithreading: Complete Guide (Java 7-17)

## Part 1: Fundamentals

### What is Multithreading?

Multithreading allows a program to execute multiple tasks concurrently within a single process. Each task runs in a separate thread, sharing the same memory space but executing independently.

**Benefits:**

- Better CPU utilization
- Improved application responsiveness
- Efficient resource sharing
- Enhanced performance on multi-core processors

### Understanding Processes vs Threads

**Process:** An independent program with its own memory space
**Thread:** A lightweight unit of execution within a process that shares memory with other threads

---

## Part 2: Basic Thread Creation (Java 7+)

### Method 1: Extending Thread Class

```java
class MyThread extends Thread {
    @Override
    public void run() {
        for (int i = 0; i < 5; i++) {
            System.out.println(Thread.currentThread().getName() + ": " + i);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

// Usage
MyThread t1 = new MyThread();
t1.start(); // Don't call run() directly!
```

### Method 2: Implementing Runnable Interface (Preferred)

```java
class MyRunnable implements Runnable {
    @Override
    public void run() {
        System.out.println("Running in: " + Thread.currentThread().getName());
    }
}

// Usage
Thread t1 = new Thread(new MyRunnable());
t1.start();

// Or with lambda (Java 8+)
Thread t2 = new Thread(() -> {
    System.out.println("Lambda thread running");
});
t2.start();
```

### Thread Lifecycle States

1. **NEW**: Thread created but not started
2. **RUNNABLE**: Thread ready to run or running
3. **BLOCKED**: Waiting for a monitor lock
4. **WAITING**: Waiting indefinitely for another thread
5. **TIMED_WAITING**: Waiting for a specified time
6. **TERMINATED**: Thread completed execution

---

## Part 3: Thread Synchronization

### The Race Condition Problem

```java
class Counter {
    private int count = 0;

    public void increment() {
        count++; // Not atomic! Can cause race conditions
    }

    public int getCount() {
        return count;
    }
}
```

### Solution 1: Synchronized Methods

```java
class SafeCounter {
    private int count = 0;

    public synchronized void increment() {
        count++;
    }

    public synchronized int getCount() {
        return count;
    }
}
```

### Solution 2: Synchronized Blocks

```java
class Counter {
    private int count = 0;
    private final Object lock = new Object();

    public void increment() {
        synchronized(lock) {
            count++;
        }
    }
}
```

### Static Synchronization

```java
class Config {
    private static int value = 0;

    public static synchronized void setValue(int v) {
        value = v; // Locks on Config.class
    }
}
```

---

## Part 4: Inter-Thread Communication

### wait(), notify(), and notifyAll()

```java
class SharedResource {
    private boolean available = false;

    public synchronized void produce() throws InterruptedException {
        while (available) {
            wait(); // Release lock and wait
        }
        System.out.println("Produced");
        available = true;
        notify(); // Wake up one waiting thread
    }

    public synchronized void consume() throws InterruptedException {
        while (!available) {
            wait();
        }
        System.out.println("Consumed");
        available = false;
        notify();
    }
}
```

### join() - Wait for Thread Completion

```java
Thread t1 = new Thread(() -> {
    System.out.println("Task 1");
});

t1.start();
t1.join(); // Wait for t1 to complete
System.out.println("t1 finished");
```

---

## Part 5: Java Concurrency Utilities (java.util.concurrent)

### ExecutorService - Thread Pool Management

```java
import java.util.concurrent.*;

// Fixed thread pool
ExecutorService executor = Executors.newFixedThreadPool(5);

// Submit tasks
Future<String> future = executor.submit(() -> {
    Thread.sleep(1000);
    return "Task completed";
});

// Get result (blocking)
String result = future.get();

// Shutdown
executor.shutdown();
executor.awaitTermination(5, TimeUnit.SECONDS);
```

### Types of Thread Pools

```java
// Fixed size pool
ExecutorService fixed = Executors.newFixedThreadPool(10);

// Cached pool (creates threads as needed)
ExecutorService cached = Executors.newCachedThreadPool();

// Single thread executor
ExecutorService single = Executors.newSingleThreadExecutor();

// Scheduled executor
ScheduledExecutorService scheduled = Executors.newScheduledThreadPool(5);
scheduled.scheduleAtFixedRate(() -> {
    System.out.println("Periodic task");
}, 0, 1, TimeUnit.SECONDS);
```

### Callable vs Runnable

```java
// Runnable - no return value, no checked exceptions
Runnable runnable = () -> System.out.println("Running");

// Callable - returns value, can throw exceptions
Callable<Integer> callable = () -> {
    Thread.sleep(1000);
    return 42;
};

ExecutorService executor = Executors.newSingleThreadExecutor();
Future<Integer> future = executor.submit(callable);
Integer result = future.get(); // result = 42
```

---

## Part 6: Advanced Synchronization

### ReentrantLock - More Flexible Locking

```java
import java.util.concurrent.locks.*;

class BankAccount {
    private double balance = 0;
    private final ReentrantLock lock = new ReentrantLock();

    public void deposit(double amount) {
        lock.lock();
        try {
            balance += amount;
        } finally {
            lock.unlock(); // Always unlock in finally
        }
    }

    public boolean tryTransfer(BankAccount other, double amount) {
        if (lock.tryLock()) { // Non-blocking attempt
            try {
                if (balance >= amount) {
                    balance -= amount;
                    other.deposit(amount);
                    return true;
                }
                return false;
            } finally {
                lock.unlock();
            }
        }
        return false;
    }
}
```

### ReadWriteLock - Multiple Readers, Single Writer

```java
class Cache {
    private Map<String, String> data = new HashMap<>();
    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final Lock readLock = rwLock.readLock();
    private final Lock writeLock = rwLock.writeLock();

    public String get(String key) {
        readLock.lock();
        try {
            return data.get(key);
        } finally {
            readLock.unlock();
        }
    }

    public void put(String key, String value) {
        writeLock.lock();
        try {
            data.put(key, value);
        } finally {
            writeLock.unlock();
        }
    }
}
```

### Semaphore - Control Access Count

```java
import java.util.concurrent.Semaphore;

class ConnectionPool {
    private final Semaphore semaphore = new Semaphore(5); // Max 5 connections

    public void useConnection() throws InterruptedException {
        semaphore.acquire(); // Wait if no permits available
        try {
            System.out.println("Using connection");
            Thread.sleep(1000);
        } finally {
            semaphore.release(); // Return permit
        }
    }
}
```

### CountDownLatch - Wait for Multiple Threads

```java
import java.util.concurrent.CountDownLatch;

CountDownLatch latch = new CountDownLatch(3); // Wait for 3 threads

for (int i = 0; i < 3; i++) {
    new Thread(() -> {
        System.out.println("Task completed");
        latch.countDown(); // Decrement count
    }).start();
}

latch.await(); // Block until count reaches 0
System.out.println("All tasks completed");
```

### CyclicBarrier - Synchronize at Barrier Point

```java
import java.util.concurrent.CyclicBarrier;

CyclicBarrier barrier = new CyclicBarrier(3, () -> {
    System.out.println("All threads reached barrier");
});

for (int i = 0; i < 3; i++) {
    new Thread(() -> {
        try {
            System.out.println("Working...");
            barrier.await(); // Wait at barrier
            System.out.println("Continuing...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }).start();
}
```

---

## Part 7: Concurrent Collections

### Thread-Safe Collections

```java
import java.util.concurrent.*;

// ConcurrentHashMap - Thread-safe map
ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
map.put("key", 1);
map.computeIfAbsent("key2", k -> 42);

// CopyOnWriteArrayList - Thread-safe list (good for reads)
CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<>();
list.add("item");

// BlockingQueue - Producer-Consumer pattern
BlockingQueue<String> queue = new LinkedBlockingQueue<>(10);

// Producer
new Thread(() -> {
    try {
        queue.put("item"); // Blocks if full
    } catch (InterruptedException e) {}
}).start();

// Consumer
new Thread(() -> {
    try {
        String item = queue.take(); // Blocks if empty
    } catch (InterruptedException e) {}
}).start();
```

### ConcurrentLinkedQueue - Non-blocking Queue

```java
ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();
queue.offer("item"); // Add
String item = queue.poll(); // Remove and return (null if empty)
```

---

## Part 8: Atomic Variables

### Atomic Classes - Lock-Free Thread Safety

```java
import java.util.concurrent.atomic.*;

// AtomicInteger
AtomicInteger counter = new AtomicInteger(0);
counter.incrementAndGet(); // Atomic increment
counter.compareAndSet(5, 10); // CAS operation

// AtomicLong
AtomicLong longCounter = new AtomicLong();
longCounter.addAndGet(100);

// AtomicBoolean
AtomicBoolean flag = new AtomicBoolean(false);
flag.compareAndSet(false, true);

// AtomicReference
AtomicReference<String> ref = new AtomicReference<>("initial");
ref.updateAndGet(s -> s + " updated");
```

---

## Part 9: CompletableFuture (Java 8+)

### Asynchronous Programming Made Easy

```java
import java.util.concurrent.CompletableFuture;

// Simple async task
CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
    // Runs in ForkJoinPool.commonPool()
    return "Result";
});

String result = future.get(); // Blocking

// Chain operations
CompletableFuture.supplyAsync(() -> "Hello")
    .thenApply(s -> s + " World")
    .thenAccept(System.out::println); // Prints: Hello World

// Combine multiple futures
CompletableFuture<Integer> f1 = CompletableFuture.supplyAsync(() -> 10);
CompletableFuture<Integer> f2 = CompletableFuture.supplyAsync(() -> 20);

CompletableFuture<Integer> combined = f1.thenCombine(f2, (a, b) -> a + b);
System.out.println(combined.get()); // 30

// Handle errors
CompletableFuture.supplyAsync(() -> {
    if (true) throw new RuntimeException("Error");
    return "Success";
}).exceptionally(ex -> "Fallback value")
  .thenAccept(System.out::println); // Prints: Fallback value
```

### Common CompletableFuture Patterns

```java
// Run multiple tasks in parallel
CompletableFuture<Void> allOf = CompletableFuture.allOf(
    CompletableFuture.runAsync(() -> task1()),
    CompletableFuture.runAsync(() -> task2()),
    CompletableFuture.runAsync(() -> task3())
);
allOf.join(); // Wait for all

// Get first completed
CompletableFuture<String> anyOf = CompletableFuture.anyOf(
    CompletableFuture.supplyAsync(() -> "Task1"),
    CompletableFuture.supplyAsync(() -> "Task2")
).thenApply(result -> (String) result);

// Async with custom executor
ExecutorService executor = Executors.newFixedThreadPool(10);
CompletableFuture.supplyAsync(() -> "Result", executor);
```

---

## Part 10: Fork/Join Framework (Java 7+)

### Divide and Conquer Parallelism

```java
import java.util.concurrent.*;

class SumTask extends RecursiveTask<Long> {
    private final long[] array;
    private final int start, end;
    private static final int THRESHOLD = 1000;

    public SumTask(long[] array, int start, int end) {
        this.array = array;
        this.start = start;
        this.end = end;
    }

    @Override
    protected Long compute() {
        if (end - start <= THRESHOLD) {
            // Small enough, compute directly
            long sum = 0;
            for (int i = start; i < end; i++) {
                sum += array[i];
            }
            return sum;
        } else {
            // Split into subtasks
            int mid = (start + end) / 2;
            SumTask left = new SumTask(array, start, mid);
            SumTask right = new SumTask(array, mid, end);

            left.fork(); // Execute asynchronously
            long rightResult = right.compute(); // Execute in current thread
            long leftResult = left.join(); // Wait for result

            return leftResult + rightResult;
        }
    }
}

// Usage
ForkJoinPool pool = new ForkJoinPool();
long[] array = new long[10000];
SumTask task = new SumTask(array, 0, array.length);
long result = pool.invoke(task);
```

---

## Part 11: Parallel Streams (Java 8+)

### Easy Parallelism with Streams

```java
import java.util.stream.*;

List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8);

// Sequential stream
int sum = numbers.stream()
    .map(n -> n * n)
    .reduce(0, Integer::sum);

// Parallel stream - automatic parallelization
int parallelSum = numbers.parallelStream()
    .map(n -> n * n)
    .reduce(0, Integer::sum);

// Custom parallel processing
List<String> results = IntStream.range(0, 100)
    .parallel()
    .mapToObj(i -> processItem(i))
    .collect(Collectors.toList());
```

**Warning:** Parallel streams use the common ForkJoinPool. Use wisely for CPU-bound tasks with independent operations.

---

## Part 12: ThreadLocal - Thread-Specific Data

```java
class UserContext {
    private static final ThreadLocal<String> userId = new ThreadLocal<>();

    public static void setUserId(String id) {
        userId.set(id);
    }

    public static String getUserId() {
        return userId.get();
    }

    public static void clear() {
        userId.remove(); // Prevent memory leaks
    }
}

// Each thread has its own value
new Thread(() -> {
    UserContext.setUserId("User1");
    System.out.println(UserContext.getUserId()); // User1
}).start();

new Thread(() -> {
    UserContext.setUserId("User2");
    System.out.println(UserContext.getUserId()); // User2
}).start();
```

---

## Part 13: Volatile Keyword

### Visibility Guarantee

```java
class Flag {
    private volatile boolean running = true; // Ensures visibility across threads

    public void stop() {
        running = false; // Visible to all threads immediately
    }

    public void run() {
        while (running) {
            // Do work
        }
    }
}
```

**volatile ensures:**

- Visibility: Changes are immediately visible to other threads
- No caching: Always read from main memory
- No reordering: Prevents instruction reordering

**volatile does NOT ensure atomicity** (use atomic classes or synchronization for that)

---

## Part 14: Deadlock Prevention

### Common Deadlock Example

```java
class Account {
    private double balance;

    // BAD: Can cause deadlock
    public void transfer(Account other, double amount) {
        synchronized(this) {
            synchronized(other) { // Deadlock if two threads transfer in opposite directions
                this.balance -= amount;
                other.balance += amount;
            }
        }
    }
}
```

### Solution: Lock Ordering

```java
class SafeAccount {
    private double balance;
    private final int id;

    public void transfer(SafeAccount other, double amount) {
        SafeAccount first = this.id < other.id ? this : other;
        SafeAccount second = this.id < other.id ? other : this;

        synchronized(first) {
            synchronized(second) {
                this.balance -= amount;
                other.balance += amount;
            }
        }
    }
}
```

### Deadlock Detection

```java
// Use tryLock with timeout
ReentrantLock lock1 = new ReentrantLock();
ReentrantLock lock2 = new ReentrantLock();

try {
    if (lock1.tryLock(50, TimeUnit.MILLISECONDS)) {
        try {
            if (lock2.tryLock(50, TimeUnit.MILLISECONDS)) {
                try {
                    // Critical section
                } finally {
                    lock2.unlock();
                }
            }
        } finally {
            lock1.unlock();
        }
    }
} catch (InterruptedException e) {
    Thread.currentThread().interrupt();
}
```

---

## Part 15: Virtual Threads (Java 19+ Preview, Java 21 Stable)

### Lightweight Threads for Massive Concurrency

```java
// Traditional platform thread
Thread platformThread = new Thread(() -> {
    System.out.println("Platform thread");
});

// Virtual thread (Java 21+)
Thread virtualThread = Thread.ofVirtual().start(() -> {
    System.out.println("Virtual thread");
});

// Or simpler
Thread.startVirtualThread(() -> {
    System.out.println("Virtual thread");
});

// ExecutorService with virtual threads
try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
    for (int i = 0; i < 100_000; i++) { // Can handle millions!
        executor.submit(() -> {
            // Blocking I/O is fine with virtual threads
            Thread.sleep(1000);
            return "Result";
        });
    }
} // Auto-shutdown
```

**Virtual Threads Benefits:**

- Extremely lightweight (can create millions)
- Perfect for I/O-bound tasks
- Blocking operations don't block OS threads
- Simplifies async code

**When to use:**

- High-concurrency I/O operations
- Microservices with many requests
- Database/network operations

---

## Part 16: Structured Concurrency (Java 19+ Preview, Java 21+)

### Manage Thread Lifecycles as Units

```java
import java.util.concurrent.StructuredTaskScope;

// Execute subtasks with automatic cleanup
try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
    Future<String> user = scope.fork(() -> fetchUser());
    Future<String> order = scope.fork(() -> fetchOrder());

    scope.join();           // Wait for all
    scope.throwIfFailed();  // Propagate errors

    String userData = user.resultNow();
    String orderData = order.resultNow();

    return processData(userData, orderData);
}
```

**Benefits:**

- Automatic thread cleanup
- Error propagation
- Cancellation of subtasks if parent fails
- Clear parent-child relationships

---

## Part 17: Best Practices

### 1. Thread Pool Sizing

```java
// CPU-intensive tasks
int cores = Runtime.getRuntime().availableProcessors();
ExecutorService cpuBound = Executors.newFixedThreadPool(cores);

// I/O-intensive tasks
ExecutorService ioBound = Executors.newFixedThreadPool(cores * 2);

// Or use ThreadPoolExecutor for fine control
ThreadPoolExecutor executor = new ThreadPoolExecutor(
    10,                      // Core pool size
    50,                      // Max pool size
    60L, TimeUnit.SECONDS,   // Keep-alive time
    new LinkedBlockingQueue<>(1000) // Work queue
);
```

### 2. Always Use try-finally with Locks

```java
Lock lock = new ReentrantLock();
lock.lock();
try {
    // Critical section
} finally {
    lock.unlock(); // Always release
}
```

### 3. Prefer Higher-Level Concurrency Utilities

Instead of:

- `wait/notify` → Use `BlockingQueue`, `CountDownLatch`, etc.
- `synchronized` → Use `ReentrantLock` for advanced features
- Manual thread management → Use `ExecutorService`

### 4. Handle InterruptedException Properly

```java
try {
    Thread.sleep(1000);
} catch (InterruptedException e) {
    Thread.currentThread().interrupt(); // Restore interrupt status
    // Handle interruption
}
```

### 5. Avoid Shared Mutable State

```java
// BAD: Shared mutable state
class BadCounter {
    private int count = 0;
    public void increment() { count++; }
}

// BETTER: Immutable or atomic
class GoodCounter {
    private final AtomicInteger count = new AtomicInteger(0);
    public void increment() { count.incrementAndGet(); }
}
```

---

## Part 18: Common Patterns

### Producer-Consumer Pattern

```java
BlockingQueue<Task> queue = new LinkedBlockingQueue<>(100);

// Producer
ExecutorService producers = Executors.newFixedThreadPool(5);
for (int i = 0; i < 5; i++) {
    producers.submit(() -> {
        while (true) {
            Task task = createTask();
            queue.put(task);
        }
    });
}

// Consumer
ExecutorService consumers = Executors.newFixedThreadPool(10);
for (int i = 0; i < 10; i++) {
    consumers.submit(() -> {
        while (true) {
            Task task = queue.take();
            processTask(task);
        }
    });
}
```

### Thread-Safe Singleton

```java
// Initialization-on-demand holder idiom
class Singleton {
    private Singleton() {}

    private static class Holder {
        static final Singleton INSTANCE = new Singleton();
    }

    public static Singleton getInstance() {
        return Holder.INSTANCE;
    }
}

// Or use enum (simplest)
enum SingletonEnum {
    INSTANCE;

    public void doSomething() {
        // ...
    }
}
```

### Thread Pool with Graceful Shutdown

```java
ExecutorService executor = Executors.newFixedThreadPool(10);

// Submit tasks...

// Graceful shutdown
executor.shutdown(); // No new tasks accepted
try {
    if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
        executor.shutdownNow(); // Force shutdown
        if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
            System.err.println("Executor did not terminate");
        }
    }
} catch (InterruptedException e) {
    executor.shutdownNow();
    Thread.currentThread().interrupt();
}
```

---

## Summary: Java Version Evolution

**Java 7:**

- Fork/Join Framework
- Enhanced ThreadPoolExecutor

**Java 8:**

- CompletableFuture
- Parallel Streams
- Lambda expressions for Runnable/Callable

**Java 9:**

- Reactive Streams (Flow API)
- CompletableFuture enhancements

**Java 11:**

- Local-Variable Syntax for lambda parameters

**Java 17 (LTS):**

- Sealed classes (indirect benefit for thread-safe hierarchies)

**Java 19-21:**

- Virtual Threads (Project Loom)
- Structured Concurrency
- Scoped Values

---

## Learning Path Recommendation

1. **Weeks 1-2:** Basic thread creation, lifecycle, synchronization
2. **Weeks 3-4:** Concurrent collections, ExecutorService, thread pools
3. **Weeks 5-6:** Advanced locks, atomic variables, CompletableFuture
4. **Weeks 7-8:** Parallel streams, Fork/Join, patterns and best practices
5. **Week 9+:** Virtual threads, structured concurrency, real-world projects

Practice with real projects: web scraper, concurrent cache, parallel file processor, or chat server!
