Awesome 🔥 — you’ve now mastered **Week 1: Core Threading Fundamentals**, which means you already understand how threads behave and how to synchronize them safely.

Now it’s time for **⚙️ Week 2 — Java Concurrency Utilities & Executors Framework (Java 7 → 17)**
This is where **real-world concurrency** begins — moving from manual thread management to powerful, production-ready APIs.

---

## 🗓️ **WEEK 2: Executors, Callables & Concurrency Utilities**

### 🎯 **Goal:**

Learn to manage thread pools, handle task execution efficiently, and master concurrency utilities (`CountDownLatch`, `CyclicBarrier`, `Semaphore`, etc.).

---

## 🧩 **Day 1 — Introduction to Executors Framework**

### 🧠 Concepts:

- Why manual thread creation doesn’t scale
- `Executor` vs `ExecutorService`
- Thread pools & task submission

### 📘 Example:

```java
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutorExample {
    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(3);

        for (int i = 1; i <= 5; i++) {
            int taskId = i;
            executor.execute(() ->
                System.out.println(Thread.currentThread().getName() + " executing task " + taskId)
            );
        }

        executor.shutdown();
    }
}
```

### ✅ Key Takeaways:

- `execute()` submits a `Runnable`.
- Always call `shutdown()` to release threads.
- Thread pools reuse threads efficiently.

### 💪 Exercise:

Create a **cached thread pool** that simulates handling incoming API requests.

---

## 🧩 **Day 2 — Callable & Future (Return Values from Threads)**

### 🧠 Concepts:

- `Callable<T>` returns a value and can throw exceptions.
- `Future<T>` represents the result of an async computation.

### 📘 Example:

```java
import java.util.concurrent.*;

public class CallableFutureExample {
    public static void main(String[] args) throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        Callable<Integer> task = () -> {
            Thread.sleep(1000);
            return 42;
        };

        Future<Integer> result = executor.submit(task);
        System.out.println("Doing other work...");
        System.out.println("Result: " + result.get()); // blocks until result ready

        executor.shutdown();
    }
}
```

### 💪 Exercise:

Use `Callable` to simulate **three parallel services** returning prices, then aggregate the results.

---

## 🧩 **Day 3 — ThreadPoolExecutor Deep Dive**

### 🧠 Concepts:

- `ThreadPoolExecutor` = customizable Executor.
- Core size, max size, queue, rejection policies.

### 📘 Example:

```java
import java.util.concurrent.*;

public class CustomThreadPool {
    public static void main(String[] args) {
        ThreadPoolExecutor pool = new ThreadPoolExecutor(
                2, 4, 10, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(2),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );

        for (int i = 1; i <= 8; i++) {
            int taskId = i;
            pool.execute(() ->
                System.out.println(Thread.currentThread().getName() + " executing task " + taskId)
            );
        }

        pool.shutdown();
    }
}
```

### ✅ Key Takeaways:

- Core threads stay alive; extra threads are created on demand.
- Queues manage task backlogs.
- Rejection policies define what happens when the pool is full.

### 💪 Exercise:

Tune a thread pool for a **web crawler** that handles 1000 pages efficiently.

---

## 🧩 **Day 4 — CountDownLatch & CyclicBarrier**

### 🧠 Concepts:

- `CountDownLatch` — wait until multiple tasks finish.
- `CyclicBarrier` — wait for all threads to reach a barrier point.

### 📘 Example: `CountDownLatch`

```java
import java.util.concurrent.*;

public class LatchExample {
    public static void main(String[] args) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(3);

        for (int i = 1; i <= 3; i++) {
            new Thread(() -> {
                try {
                    System.out.println(Thread.currentThread().getName() + " done.");
                } finally {
                    latch.countDown();
                }
            }).start();
        }

        latch.await(); // waits for all threads
        System.out.println("All workers finished!");
    }
}
```

### 💪 Exercise:

Use `CountDownLatch` to make your main thread wait for **3 data loaders** (e.g., user, account, transaction services).

---

## 🧩 **Day 5 — Semaphore & ThreadLocal**

### 🧠 Concepts:

- `Semaphore` controls access to limited resources (e.g., DB connections).
- `ThreadLocal` provides per-thread storage (thread-specific variables).

### 📘 Example: Semaphore

```java
import java.util.concurrent.Semaphore;

public class SemaphoreExample {
    public static void main(String[] args) {
        Semaphore semaphore = new Semaphore(2); // only 2 permits

        for (int i = 1; i <= 5; i++) {
            int id = i;
            new Thread(() -> {
                try {
                    semaphore.acquire();
                    System.out.println("Task " + id + " acquired permit.");
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    semaphore.release();
                }
            }).start();
        }
    }
}
```

### 📘 Example: ThreadLocal

```java
public class ThreadLocalExample {
    private static ThreadLocal<Integer> counter = ThreadLocal.withInitial(() -> 0);

    public static void main(String[] args) {
        Runnable task = () -> {
            counter.set(counter.get() + 1);
            System.out.println(Thread.currentThread().getName() + " counter: " + counter.get());
        };

        new Thread(task, "T1").start();
        new Thread(task, "T2").start();
    }
}
```

### 💪 Exercise:

Use a `Semaphore` to control access to **3 printers** shared among **10 users**.

---

## 🧩 **Day 6 — BlockingQueue & Producer-Consumer (Advanced)**

### 🧠 Concepts:

- `BlockingQueue` removes the need for manual wait/notify.
- Perfect for producer-consumer models.

### 📘 Example:

```java
import java.util.concurrent.*;

public class BlockingQueueExample {
    public static void main(String[] args) {
        BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(5);

        Runnable producer = () -> {
            for (int i = 1; i <= 10; i++) {
                try {
                    queue.put(i);
                    System.out.println("Produced: " + i);
                } catch (InterruptedException ignored) {}
            }
        };

        Runnable consumer = () -> {
            while (true) {
                try {
                    Integer val = queue.take();
                    System.out.println("Consumed: " + val);
                } catch (InterruptedException ignored) {}
            }
        };

        new Thread(producer).start();
        new Thread(consumer).start();
    }
}
```

### 💪 Exercise:

Implement a **log processing system** using a `LinkedBlockingQueue` to store messages from multiple producers.

---

## 🧩 **Day 7 — Mini Project: Parallel Service Aggregator**

### 🎯 Problem:

Simulate a banking dashboard where 3 services (Balance, Loan, CreditScore) run in parallel and the main thread aggregates their results.

### 📘 Example:

```java
import java.util.concurrent.*;

public class ParallelServiceAggregator {
    public static void main(String[] args) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(3);

        Callable<String> balanceService = () -> {
            Thread.sleep(1000);
            return "Balance: ₹10,000";
        };

        Callable<String> loanService = () -> {
            Thread.sleep(2000);
            return "Loan: ₹2,50,000";
        };

        Callable<String> creditService = () -> {
            Thread.sleep(1500);
            return "Credit Score: 790";
        };

        Future<String> f1 = executor.submit(balanceService);
        Future<String> f2 = executor.submit(loanService);
        Future<String> f3 = executor.submit(creditService);

        System.out.println("=== Aggregated Report ===");
        System.out.println(f1.get());
        System.out.println(f2.get());
        System.out.println(f3.get());

        executor.shutdown();
    }
}
```

### ✅ Demonstrates:

- Parallel task execution
- Coordination using `Future`
- Thread pool management

---

## 🏁 **End of Week 2 Recap**

You learned:
✅ `ExecutorService`, `ThreadPoolExecutor`
✅ `Callable`, `Future`
✅ `CountDownLatch`, `CyclicBarrier`, `Semaphore`, `ThreadLocal`
✅ `BlockingQueue` and advanced Producer–Consumer
✅ Mini project combining concurrency utilities

---

### 🎯 **Next: Week 3 — CompletableFuture & Asynchronous Programming (Java 8–17)**

You’ll learn:

- `CompletableFuture` basics (`supplyAsync`, `thenApply`, `thenCombine`, etc.)
- Exception handling in async flows
- Combining and chaining async tasks
- Parallel Streams vs CompletableFutures
- Mini project: Async Microservice Orchestrator
