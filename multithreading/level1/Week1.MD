Perfect 👍 Let's kick off your **Java Multithreading Mastery (Week 1)** — starting from **absolute basics** and gradually building up to advanced concepts.

You’ll write small hands-on code every day. Each concept is tied to **Java 7+ fundamentals**, so you’ll have a rock-solid base before moving to `ExecutorService`, `CompletableFuture`, etc.

---

## 🗓️ **WEEK 1: Java Thread Basics**

### 🎯 **Goal:**

Understand what a thread is, how to create and control it, and learn synchronization fundamentals.

---

## 🧩 **Day 1 — Introduction to Threads**

### 🧠 Concepts:

- What is a thread?
- Process vs Thread
- Benefits of multithreading
- The JVM Thread Model

### 📘 Example 1: Creating a Thread by extending `Thread`

```java
class MyThread extends Thread {
    @Override
    public void run() {
        for (int i = 1; i <= 5; i++) {
            System.out.println(Thread.currentThread().getName() + " - Count: " + i);
        }
    }
}

public class ThreadExample1 {
    public static void main(String[] args) {
        MyThread t1 = new MyThread();
        t1.start(); // Start thread
        System.out.println("Main thread: " + Thread.currentThread().getName());
    }
}
```

### ✅ Key Takeaways:

- `start()` → creates a new thread and calls `run()` asynchronously.
- `run()` directly → executes in the current thread (not new one).

### 💪 Exercise:

Create two threads — one printing numbers 1–5, another printing letters A–E — and run them simultaneously.

---

## 🧩 **Day 2 — Creating Threads with Runnable Interface**

### 🧠 Concepts:

- Why prefer `Runnable` over extending `Thread`
- Separation of task (`Runnable`) and execution (`Thread`)

### 📘 Example:

```java
class MyRunnable implements Runnable {
    @Override
    public void run() {
        for (int i = 1; i <= 3; i++) {
            System.out.println(Thread.currentThread().getName() + " is running iteration " + i);
        }
    }
}

public class RunnableExample {
    public static void main(String[] args) {
        Thread t1 = new Thread(new MyRunnable(), "Worker-1");
        Thread t2 = new Thread(new MyRunnable(), "Worker-2");

        t1.start();
        t2.start();
    }
}
```

### ✅ Key Takeaways:

- Use `Runnable` when your class already extends another class.
- Thread creation and task logic are decoupled.

### 💪 Exercise:

Create 3 threads using `Runnable` — each calculating the factorial of a different number.

---

## 🧩 **Day 3 — Thread Lifecycle & Control Methods**

### 🧠 Concepts:

- Thread states: NEW → RUNNABLE → BLOCKED → WAITING → TERMINATED
- Thread methods:

  - `sleep(ms)`
  - `join()`
  - `isAlive()`
  - `setPriority()`

### 📘 Example:

```java
public class ThreadLifecycle {
    public static void main(String[] args) throws InterruptedException {
        Thread worker = new Thread(() -> {
            try {
                for (int i = 1; i <= 3; i++) {
                    System.out.println("Worker: " + i);
                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                System.out.println("Worker interrupted!");
            }
        });

        System.out.println("Before start: " + worker.getState());
        worker.start();
        System.out.println("After start: " + worker.getState());

        worker.join(); // Wait for worker to finish
        System.out.println("After join: " + worker.getState());
    }
}
```

### ✅ Key Takeaways:

- `join()` makes one thread wait for another.
- `sleep()` pauses current thread.
- Use `isAlive()` to check if a thread is running.

### 💪 Exercise:

Simulate a restaurant where:

- The **chef** thread cooks for 2 seconds.
- The **waiter** thread starts serving **after** the chef finishes (`join()`).

---

## 🧩 **Day 4 — Synchronization Basics**

### 🧠 Concepts:

- What is race condition
- Why synchronization is needed
- `synchronized` methods and blocks

### 📘 Example: Without Synchronization

```java
class Counter {
    private int count = 0;
    void increment() {
        count++;
    }
    int getCount() {
        return count;
    }
}

public class RaceConditionDemo {
    public static void main(String[] args) throws InterruptedException {
        Counter counter = new Counter();

        Runnable task = () -> {
            for (int i = 0; i < 1000; i++) {
                counter.increment();
            }
        };

        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);
        t1.start(); t2.start();
        t1.join(); t2.join();

        System.out.println("Final count: " + counter.getCount());
    }
}
```

⚠️ Output will vary (not 2000) → **race condition**!

### 📘 Example: With Synchronization

```java
class SafeCounter {
    private int count = 0;

    synchronized void increment() {  // synchronized method
        count++;
    }

    int getCount() {
        return count;
    }
}
```

### ✅ Key Takeaways:

- Synchronization ensures only one thread accesses a block at a time.
- Reduces performance if used excessively.

### 💪 Exercise:

Simulate a **bank account deposit** from multiple threads, both with and without synchronization.

---

## 🧩 **Day 5 — Inter-Thread Communication (`wait`, `notify`, `notifyAll`)**

### 🧠 Concepts:

- Threads can communicate via object monitors.
- `wait()` pauses the thread and releases the lock.
- `notify()` wakes one waiting thread.

### 📘 Example: Producer–Consumer Problem

```java
class SharedData {
    private int data;
    private boolean available = false;

    synchronized void produce(int value) throws InterruptedException {
        while (available) wait();
        data = value;
        available = true;
        System.out.println("Produced: " + value);
        notify();
    }

    synchronized int consume() throws InterruptedException {
        while (!available) wait();
        available = false;
        System.out.println("Consumed: " + data);
        notify();
        return data;
    }
}

public class ProducerConsumerDemo {
    public static void main(String[] args) {
        SharedData shared = new SharedData();

        Thread producer = new Thread(() -> {
            for (int i = 1; i <= 5; i++) {
                try { shared.produce(i); Thread.sleep(500); } catch (Exception ignored) {}
            }
        });

        Thread consumer = new Thread(() -> {
            for (int i = 1; i <= 5; i++) {
                try { shared.consume(); Thread.sleep(800); } catch (Exception ignored) {}
            }
        });

        producer.start();
        consumer.start();
    }
}
```

### ✅ Key Takeaways:

- Always use `wait()` and `notify()` inside synchronized blocks.
- Use `while` (not `if`) to recheck the condition after waking.

---

## 🧩 **Day 6 — The `volatile` Keyword**

### 🧠 Concepts:

- Ensures **visibility** (not atomicity) between threads.
- Example: A shared flag between threads.

### 📘 Example:

```java
class VolatileDemo {
    private volatile boolean running = true;

    void runTask() {
        while (running) {
            // doing work
        }
        System.out.println("Stopped!");
    }

    void stop() {
        running = false;
    }

    public static void main(String[] args) throws InterruptedException {
        VolatileDemo demo = new VolatileDemo();
        Thread t = new Thread(demo::runTask);
        t.start();

        Thread.sleep(2000);
        demo.stop();  // Without volatile, thread may never stop
    }
}
```

---

## 🧩 **Day 7 — Mini Project: Ticket Booking System**

### 🎯 Problem:

Simulate multiple users booking tickets for an event with limited seats.

### 📘 Example:

```java
class TicketCounter {
    private int availableTickets = 5;

    public synchronized void bookTicket(String name, int count) {
        if (availableTickets >= count) {
            System.out.println(name + " booked " + count + " tickets.");
            availableTickets -= count;
        } else {
            System.out.println(name + " tried to book " + count + " tickets, but only " + availableTickets + " left!");
        }
    }
}

public class TicketBookingApp {
    public static void main(String[] args) {
        TicketCounter counter = new TicketCounter();

        Thread t1 = new Thread(() -> counter.bookTicket("Alice", 3));
        Thread t2 = new Thread(() -> counter.bookTicket("Bob", 2));
        Thread t3 = new Thread(() -> counter.bookTicket("Charlie", 2));

        t1.start();
        t2.start();
        t3.start();
    }
}
```

✅ Demonstrates **race condition handling** and **thread synchronization** in a real scenario.

---

## 🏁 **End of Week 1 Recap**

You learned:

- ✅ Thread basics (`Thread`, `Runnable`)
- ✅ Thread lifecycle and control
- ✅ Synchronization (`synchronized`, `wait/notify`)
- ✅ Volatile keyword
- ✅ Real-world examples (Producer–Consumer, Ticket Booking)

---

Next Step:
👉 **Week 2: Concurrency Utilities & Executors Framework**
You’ll learn:

- `ExecutorService`, `ThreadPoolExecutor`
- `Callable`, `Future`
- `CountDownLatch`, `CyclicBarrier`, `Semaphore`
- `ThreadLocal`
