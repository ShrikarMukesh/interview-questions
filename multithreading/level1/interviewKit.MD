---
---

# 🎯 JAVA MULTITHREADING INTERVIEW PREPARATION KIT

## _(Covers Java 7 → 17)_

## 🧩 **1. Core Theory Quick Summary**

| Concept                            | Description                                                                                     |
| ---------------------------------- | ----------------------------------------------------------------------------------------------- |
| **Thread**                         | Lightweight unit of execution inside a process.                                                 |
| **Runnable / Callable**            | Encapsulates a task — Runnable returns `void`, Callable returns a value.                        |
| **Thread Lifecycle**               | NEW → RUNNABLE → BLOCKED/WAITING → TERMINATED                                                   |
| **Thread Safety**                  | A program is thread-safe if multiple threads can execute it without causing inconsistent state. |
| **Race Condition**                 | Two or more threads access shared data simultaneously, leading to unpredictable results.        |
| **synchronized**                   | Ensures only one thread can execute a block/method at a time.                                   |
| **volatile**                       | Guarantees visibility of changes between threads (not atomicity).                               |
| **ExecutorService**                | Manages a pool of threads — improves performance and control.                                   |
| **Future / CompletableFuture**     | Represents the result of an asynchronous computation.                                           |
| **Locks**                          | Fine-grained control over synchronization — e.g., `ReentrantLock`.                              |
| **CountDownLatch / CyclicBarrier** | Coordination utilities for waiting or synchronization among threads.                            |
| **Semaphore**                      | Controls access to a resource with limited permits.                                             |
| **ThreadLocal**                    | Thread-specific variable (each thread gets its own copy).                                       |
| **Flow API (Reactive Streams)**    | Publisher–Subscriber pattern for non-blocking data streams (Java 9+).                           |
| **Virtual Threads**                | Lightweight threads from Project Loom (Java 17/19).                                             |

---

## ⚙️ **2. Common Multithreading Pitfalls**

| Pitfall                                       | Explanation                                                                     |
| --------------------------------------------- | ------------------------------------------------------------------------------- |
| ❌ Calling `run()` instead of `start()`       | Executes on the same thread (no parallelism).                                   |
| ❌ Synchronizing on wrong object              | Leads to inconsistent locking.                                                  |
| ❌ Deadlocks                                  | Two threads waiting for each other’s lock.                                      |
| ❌ Starvation                                 | Thread never gets CPU time due to others’ high priority.                        |
| ❌ Busy waiting                               | Looping until a condition changes instead of using `wait()` or blocking queues. |
| ❌ Forgetting to `shutdown()` ExecutorService | Leads to non-terminating JVM.                                                   |
| ❌ Using `volatile` for atomic ops            | Doesn’t make increments atomic — use `AtomicInteger` or `synchronized`.         |

---

## 💡 **3. 50+ Most Commonly Asked Interview Questions**

### 🧱 **Basic Level**

1. **What’s the difference between a process and a thread?**
   → Process has its own memory; threads share memory within a process.

2. **Ways to create a thread in Java?**

   - Extend `Thread`
   - Implement `Runnable`
   - Use `ExecutorService`
   - Use `CompletableFuture.runAsync/supplyAsync`

3. **Difference between `Runnable` and `Callable`?**

   - `Runnable` → no return, no checked exception
   - `Callable<T>` → returns value, can throw checked exception

4. **What happens if you call `run()` instead of `start()`?**
   → It runs on the current thread, not in a new one.

5. **What are daemon threads?**
   → Background threads (e.g., GC). JVM exits when only daemons are left.

6. **What is thread priority in Java?**
   → `Thread.MIN_PRIORITY (1)` → `Thread.NORM_PRIORITY (5)` → `Thread.MAX_PRIORITY (10)`
   OS may or may not respect it.

7. **Difference between `sleep()` and `wait()`?**

   - `sleep()` pauses thread but keeps the lock.
   - `wait()` releases the lock and waits for `notify()`.

---

### ⚙️ **Intermediate Level**

8. **What is synchronization?**
   → Mechanism to control access to shared resources.

9. **What’s a synchronized block?**

   ```java
   synchronized (lock) {
       // critical section
   }
   ```

10. **Difference between synchronized method and synchronized block?**
    → Blocks are finer-grained (faster); methods lock the entire object.

11. **What’s a reentrant lock?**
    → Allows the same thread to re-acquire a lock it already holds.

12. **What’s the difference between `synchronized` and `ReentrantLock`?**
    | Feature | synchronized | ReentrantLock |
    |----------|---------------|----------------|
    | Automatic release | ✅ | ❌ (must release manually) |
    | Try-lock timeout | ❌ | ✅ |
    | Fairness policy | ❌ | ✅ |
    | Condition variables | ❌ | ✅ |

13. **Explain `volatile`.**
    → Ensures variable visibility across threads but not atomicity.

14. **Example of atomic variable:**

```java
AtomicInteger counter = new AtomicInteger(0);
counter.incrementAndGet();
```

15. **Difference between `wait()` and `notify()` vs blocking queues?**

- `wait()/notify()` are low-level and error-prone.
- `BlockingQueue` handles synchronization internally.

16. **Explain `ThreadLocal`.**
    → Each thread has its own isolated variable copy.

17. **What is thread starvation?**
    → A thread never gets CPU because others monopolize it.

18. **Explain a deadlock scenario.**

```java
synchronized(A) {
    synchronized(B) {}
}
// Another thread locks B then A → deadlock
```

19. **How to avoid deadlocks?**

- Lock ordering
- Timeout locks (`tryLock()`)
- Use higher-level abstractions (Executors, concurrent collections)

20. **What’s the difference between concurrency and parallelism?**

- Concurrency: multiple tasks _progress together_
- Parallelism: multiple tasks _execute simultaneously_

---

### 🚀 **Advanced Level (Executors, Futures, Reactive)**

21. **What is an ExecutorService?**
    → Manages and reuses threads efficiently.

22. **Difference between `execute()` and `submit()`?**

- `execute()` → no result.
- `submit()` → returns a `Future`.

23. **How to shut down an ExecutorService?**

```java
executor.shutdown();
executor.awaitTermination(5, TimeUnit.SECONDS);
```

24. **What are the types of thread pools?**

- `newFixedThreadPool()`
- `newCachedThreadPool()`
- `newSingleThreadExecutor()`
- `newScheduledThreadPool()`

25. **What’s a `Callable` and `Future` used for?**
    → Callable returns result; Future holds it until ready.

26. **How do you handle timeouts with Future?**

```java
future.get(2, TimeUnit.SECONDS);
```

27. **What is `CompletableFuture`?**
    → Non-blocking, async, chainable future introduced in Java 8.

28. **How do you combine two CompletableFutures?**

```java
f1.thenCombine(f2, (a, b) -> a + b);
```

29. **Difference between `thenApply` and `thenCompose`?**

- `thenApply` → transforms result
- `thenCompose` → flattens nested CompletableFutures

30. **How to handle exceptions in CompletableFuture?**

- `.exceptionally(ex -> defaultValue)`
- `.handle((res, ex) -> ...)`

31. **What’s the difference between `allOf()` and `anyOf()`?**

- `allOf()` waits for all to complete.
- `anyOf()` completes when first finishes.

32. **What’s ForkJoinPool?**
    → A thread pool for divide-and-conquer parallelism (used by streams & CompletableFuture).

---

### 🔄 **Reactive & Modern Java**

33. **What is the Flow API?**
    → Java 9+ reactive streams implementation (`Publisher`, `Subscriber`, `Subscription`, `Processor`).

34. **What is backpressure in Flow API?**
    → Mechanism for subscriber to control how much data it receives.

35. **What is the difference between `SubmissionPublisher` and custom publishers?**
    → `SubmissionPublisher` is a convenience class; custom publishers provide full control.

36. **What are Virtual Threads (Project Loom)?**
    → Lightweight, managed by JVM (can create thousands without OS overhead).

37. **How do virtual threads differ from platform threads?**
    | Platform Thread | Virtual Thread |
    |------------------|----------------|
    | 1:1 with OS thread | Many:1 with OS thread |
    | Expensive | Cheap |
    | Manual pooling needed | No pooling needed |

38. **Can Virtual Threads use synchronized blocks?**
    ✅ Yes — they behave like normal threads (but block less often).

39. **What is Structured Concurrency?**
    → Model where tasks are grouped and managed hierarchically. If one fails, the whole group is canceled.

40. **When should you prefer Virtual Threads?**
    → For I/O-bound workloads (HTTP calls, DB access, etc.)

---

### 🧪 **Practical / Coding Questions**

41. Implement Producer–Consumer using `BlockingQueue`.
42. Implement parallel sum of an array using `ForkJoinPool`.
43. Write code to detect a deadlock using `ThreadMXBean`.
44. Write code using `CountDownLatch` to wait for multiple tasks.
45. Use `Semaphore` to limit access to a shared resource.
46. Demonstrate `CompletableFuture` chaining with `thenCombine`.
47. Create a `ThreadLocal` variable for request tracking IDs.
48. Write a service aggregator using `CompletableFuture.allOf()`.
49. Use Flow API to transform a stream of events.
50. Launch 1000 virtual threads and measure total time taken.

---

## 🧠 **4. Java Multithreading Quick Cheat Sheet**

| Task                    | API / Tool                             |
| ----------------------- | -------------------------------------- |
| Run code in parallel    | `ExecutorService`, `CompletableFuture` |
| Wait for multiple tasks | `CountDownLatch`, `allOf()`            |
| Limit concurrency       | `Semaphore`, thread pool size          |
| Schedule tasks          | `ScheduledExecutorService`             |
| Handle async results    | `Future`, `CompletableFuture`          |
| Share variable safely   | `AtomicInteger`, `volatile`            |
| Avoid deadlock          | Lock ordering, timeouts                |
| Thread isolation        | `ThreadLocal`                          |
| Stream processing       | `parallelStream()`, `Flow API`         |
| Light threads           | `VirtualThread`                        |
| Clean async design      | Structured Concurrency                 |

---

## 🧩 **5. Mock Interview Tips**

✅ **Be ready to write code on a whiteboard or IDE**
→ Show how you use `ExecutorService`, `CompletableFuture`, or `synchronized`.

✅ **Be conceptual + practical**
→ Explain _why_ you chose an approach, not just _what_.

✅ **Be aware of performance**
→ Thread creation cost, context switching, CPU vs I/O bound tasks.

✅ **Demonstrate awareness of modern features (Java 17)**
→ Mention `Virtual Threads` and `CompletableFuture` — this shows you’re up-to-date.

✅ **Common Interview Scenarios**

- Bank transaction system (synchronized + volatile)
- Microservice aggregator (CompletableFuture)
- Event stream handler (Flow API)
- Web server simulation (Virtual Threads)
