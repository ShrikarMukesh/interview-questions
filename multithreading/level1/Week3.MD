**real power of modern Java (8 → 17)**: **asynchronous and reactive programming** with `CompletableFuture`, parallel streams, and functional-style concurrency.

By the end of **Week 3**, you’ll be able to build **non-blocking, async microservice-style workflows** — something that’s used in _real-world banking, e-commerce, and distributed systems_.

---

## 🗓️ **WEEK 3: CompletableFuture & Asynchronous Programming (Java 8 → 17)**

### 🎯 **Goal:**

Master Java’s high-level asynchronous API using `CompletableFuture`, understand async chaining, exception handling, and real-world use cases.

---

## 🧩 **Day 1 — From Future to CompletableFuture**

### 🧠 Concepts:

- `Future` is blocking → `CompletableFuture` is non-blocking.
- Execute async tasks without manually managing threads.

### 📘 Example:

```java
import java.util.concurrent.*;

public class BasicCompletableFuture {
    public static void main(String[] args) throws Exception {
        CompletableFuture<Void> cf = CompletableFuture.runAsync(() -> {
            System.out.println(Thread.currentThread().getName() + " running async task");
        });

        cf.get(); // Waits for completion
        System.out.println("Main thread finished");
    }
}
```

### ✅ Key Points:

- `runAsync()` = no return value.
- `supplyAsync()` = returns a result.
- Uses ForkJoinPool by default.

### 💪 Exercise:

Use `supplyAsync()` to fetch mock data (e.g., "Fetching user profile...") and print the result when ready.

---

## 🧩 **Day 2 — Chaining Async Tasks (`thenApply`, `thenAccept`)**

### 🧠 Concepts:

- You can transform or consume results _asynchronously_ using chains.
- Each `thenApply` creates a new `CompletableFuture`.

### 📘 Example:

```java
import java.util.concurrent.*;

public class ChainingExample {
    public static void main(String[] args) throws Exception {
        CompletableFuture.supplyAsync(() -> {
            System.out.println("Fetching user...");
            return "Shrikar";
        })
        .thenApply(name -> "Hello, " + name)
        .thenAccept(greeting -> System.out.println(Thread.currentThread().getName() + " says: " + greeting))
        .get();
    }
}
```

### ✅ Key Points:

- `thenApply()` transforms results.
- `thenAccept()` consumes results (returns void).
- Each stage runs asynchronously.

### 💪 Exercise:

Chain three async tasks:
→ fetch user → calculate loyalty points → print reward eligibility.

---

## 🧩 **Day 3 — Combining Multiple Async Tasks**

### 🧠 Concepts:

- `thenCombine()` combines results of _two futures_.
- `allOf()` waits for _many futures_ to complete.
- `anyOf()` returns when _one completes_.

### 📘 Example:

```java
import java.util.concurrent.*;

public class CombineExample {
    public static void main(String[] args) throws Exception {
        CompletableFuture<String> fetchName = CompletableFuture.supplyAsync(() -> "Shrikar");
        CompletableFuture<Integer> fetchAge = CompletableFuture.supplyAsync(() -> 30);

        CompletableFuture<String> combined = fetchName.thenCombine(fetchAge,
                (name, age) -> name + " is " + age + " years old");

        System.out.println(combined.get());
    }
}
```

### 💪 Example: Wait for all

```java
CompletableFuture<Void> all = CompletableFuture.allOf(
    CompletableFuture.runAsync(() -> System.out.println("Service A")),
    CompletableFuture.runAsync(() -> System.out.println("Service B")),
    CompletableFuture.runAsync(() -> System.out.println("Service C"))
);
all.join();
```

### ✅ Key Points:

- `thenCombine()` merges results.
- `allOf()` → wait for all.
- `anyOf()` → first one done wins.

### 💪 Exercise:

Run 3 async “microservices” (user, balance, transaction) in parallel using `allOf()`.

---

## 🧩 **Day 4 — Exception Handling in Async Flows**

### 🧠 Concepts:

- Handle errors gracefully in async chains.
- Methods:

  - `exceptionally()`
  - `handle()`
  - `whenComplete()`

### 📘 Example:

```java
import java.util.concurrent.*;

public class ExceptionHandlingExample {
    public static void main(String[] args) {
        CompletableFuture.supplyAsync(() -> {
            System.out.println("Fetching data...");
            if (true) throw new RuntimeException("Data fetch failed!");
            return "Data";
        })
        .exceptionally(ex -> {
            System.out.println("Error: " + ex.getMessage());
            return "Default Data";
        })
        .thenAccept(System.out::println)
        .join();
    }
}
```

### 💪 Exercise:

Simulate a network call that fails randomly — handle the error using `handle()` and provide a fallback response.

---

## 🧩 **Day 5 — Custom Executors & Timeouts**

### 🧠 Concepts:

- By default, `CompletableFuture` uses **ForkJoinPool.commonPool()**.
- You can pass your own `Executor`.
- Also handle timeouts with:

  - `orTimeout()`
  - `completeOnTimeout()`

### 📘 Example:

```java
import java.util.concurrent.*;

public class TimeoutExample {
    public static void main(String[] args) {
        ExecutorService pool = Executors.newFixedThreadPool(2);

        CompletableFuture<String> cf = CompletableFuture
                .supplyAsync(() -> {
                    try { Thread.sleep(2000); } catch (Exception ignored) {}
                    return "Result from async task";
                }, pool)
                .orTimeout(1, TimeUnit.SECONDS)
                .exceptionally(ex -> "Timed out!");

        System.out.println(cf.join());
        pool.shutdown();
    }
}
```

### 💪 Exercise:

Build a `CompletableFuture` chain that retries a task up to 3 times on timeout.

---

## 🧩 **Day 6 — Parallel Streams vs CompletableFuture**

### 🧠 Concepts:

- Both use the ForkJoin framework.
- `parallelStream()` is simpler but less flexible.
- `CompletableFuture` allows fine-grained control.

### 📘 Example:

```java
import java.util.List;

public class ParallelStreamExample {
    public static void main(String[] args) {
        List<String> users = List.of("A", "B", "C", "D", "E");

        users.parallelStream()
             .map(u -> {
                 System.out.println(Thread.currentThread().getName() + " processing " + u);
                 return u.toLowerCase();
             })
             .forEach(System.out::println);
    }
}
```

### ✅ When to Use:

| Use Case                               | Recommended         |
| -------------------------------------- | ------------------- |
| Simple data transformations            | `parallelStream()`  |
| API calls, async workflows             | `CompletableFuture` |
| Error handling, chaining, custom pools | `CompletableFuture` |

### 💪 Exercise:

Implement a parallel transformation pipeline using both approaches and compare execution times.

---

## 🧩 **Day 7 — Mini Project: Async Microservice Aggregator**

### 🎯 Problem:

Simulate a dashboard fetching data from 3 microservices in parallel:

- **AccountService**
- **LoanService**
- **CreditScoreService**

### 📘 Example:

```java
import java.util.concurrent.*;

public class AsyncMicroserviceAggregator {
    static CompletableFuture<String> getAccountService() {
        return CompletableFuture.supplyAsync(() -> {
            sleep(1000);
            return "Account: ₹10,000";
        });
    }

    static CompletableFuture<String> getLoanService() {
        return CompletableFuture.supplyAsync(() -> {
            sleep(1500);
            return "Loan: ₹2,00,000";
        });
    }

    static CompletableFuture<String> getCreditScoreService() {
        return CompletableFuture.supplyAsync(() -> {
            sleep(800);
            return "Credit Score: 790";
        });
    }

    static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (Exception ignored) {}
    }

    public static void main(String[] args) {
        CompletableFuture<Void> all = CompletableFuture.allOf(
                getAccountService(), getLoanService(), getCreditScoreService()
        );

        all.thenRun(() -> {
            try {
                System.out.println("\n=== Dashboard Data ===");
                System.out.println(getAccountService().get());
                System.out.println(getLoanService().get());
                System.out.println(getCreditScoreService().get());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).join();

        System.out.println("Dashboard loaded asynchronously!");
    }
}
```

### ✅ Demonstrates:

- Parallel async calls
- Non-blocking aggregation
- Real-world pattern for microservices / REST APIs

---

## 🏁 **End of Week 3 Recap**

You mastered:
✅ `CompletableFuture` basics
✅ Chaining & combining async tasks
✅ Exception handling & timeouts
✅ Custom executors
✅ Parallel streams vs CompletableFuture
✅ Async microservice orchestration

---

### 🪜 **Next: Week 4 — Advanced Concurrency (Flow API, Virtual Threads & Reactive Streams)**

In Week 4, we’ll go deep into:

- Java 9+ **Flow API** (Reactive Streams)
- **Virtual Threads** (Java 17/19 Project Loom)
- Structured concurrency
- Designing async pipelines for performance and resilience
- Final mini project: _Reactive event-driven banking system_

---
