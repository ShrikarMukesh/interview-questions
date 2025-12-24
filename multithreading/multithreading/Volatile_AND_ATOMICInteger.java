public class Volatile_AND_ATOMICInteger {
    static class VolatileFlag {
        volatile boolean stop = false;
    }

    static class Counter {
        int value = 0; // non-atomic on purpose for demo
        void increment() { value++; }
    }

    static void runVolatileDemo() throws InterruptedException {
        VolatileFlag flag = new VolatileFlag();
        Thread worker = new Thread(() -> {
            System.out.println("[volatile] worker started");
            int i = 0;
            while (!flag.stop) {
                // do some work
                i++;
                if ((i & 0xFFFF) == 0) Thread.yield();
            }
            System.out.println("[volatile] worker observed stop and exits");
        });
        worker.start();
        Thread.sleep(200);
        System.out.println("[volatile] main sets stop = true");
        flag.stop = true;
        worker.join();
    }

    static void runAtomicDemo() throws InterruptedException {
        System.out.println("[atomic] demo starting");
        java.util.concurrent.atomic.AtomicInteger atomic = new java.util.concurrent.atomic.AtomicInteger(0);
        int threads = 8;
        int incrementsPerThread = 50_000;

        Thread[] ts = new Thread[threads];
        for (int t = 0; t < threads; t++) {
            ts[t] = new Thread(() -> {
                for (int i = 0; i < incrementsPerThread; i++) {
                    atomic.incrementAndGet();
                }
            });
            ts[t].start();
        }
        for (Thread t : ts) t.join();
        System.out.println("[atomic] final=" + atomic.get() + " expected=" + (threads * incrementsPerThread));

        System.out.println("[non-atomic] demo starting");
        Counter nonAtomic = new Counter();
        Thread[] ts2 = new Thread[threads];
        for (int t = 0; t < threads; t++) {
            ts2[t] = new Thread(() -> {
                for (int i = 0; i < incrementsPerThread; i++) {
                    nonAtomic.increment();
                }
            });
            ts2[t].start();
        }
        for (Thread t : ts2) t.join();
        System.out.println("[non-atomic] final=" + nonAtomic.value + " expected=" + (threads * incrementsPerThread));
    }

    public static void main(String[] args) throws Exception {
        runVolatileDemo();
        runAtomicDemo();
    }
}
