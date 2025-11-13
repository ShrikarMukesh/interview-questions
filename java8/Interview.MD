---

 **why `Comparator` is still a _functional interface_** even though it _appears_ to have multiple abstract methods.

---

## 🧩 First: What is a Functional Interface?

A **functional interface** is an interface that has **exactly one abstract method**.
It can have:

- Any number of **default methods** (with implementations),
- Any number of **static methods**, and
- Methods inherited from `Object` (like `equals`, `hashCode`, `toString`).

👉 Only **one abstract method** counts toward the _functional interface contract_.

---

## 🧠 Now, Look at `Comparator` Source (Java 8+)

Here’s the key part of the `java.util.Comparator` interface (simplified):

```java
@FunctionalInterface
public interface Comparator<T> {
    int compare(T o1, T o2);

    // Many other methods — but most are default or static
    boolean equals(Object obj); // from Object

    default Comparator<T> reversed() { ... }

    default Comparator<T> thenComparing(Comparator<? super T> other) { ... }

    static <T, U extends Comparable<? super U>> Comparator<T> comparing(
            Function<? super T, ? extends U> keyExtractor) { ... }
}
```

---

## 🔍 Let’s analyze the methods

| Method                       | Type          | Abstract?  | Explanation                                      |
| ---------------------------- | ------------- | ---------- | ------------------------------------------------ |
| `int compare(T o1, T o2)`    | Instance      | ✅ Yes     | The **only abstract method** you must implement. |
| `boolean equals(Object obj)` | From `Object` | 🚫 Ignored | Not counted for functional interface rule.       |
| `reversed()`                 | Default       | 🚫 No      | Has a body → not abstract.                       |
| `thenComparing()`            | Default       | 🚫 No      | Has a body → not abstract.                       |
| `comparing()`                | Static        | 🚫 No      | Static → not abstract.                           |

---

## ✅ So Java says: “Comparator has **one** abstract method — `compare()`”

That’s why this is valid:

```java
Comparator<Integer> c = (a, b) -> a - b;
```

Under the hood, Java sees:

- `Comparator` has one abstract method → `compare(T o1, T o2)`
- Therefore, your lambda `(a, b) -> a - b` implements that method.

---

## 🧩 What about `equals(Object obj)`?

Every interface **implicitly declares** the methods of `Object`, but **they don’t count** toward the abstract method count for functional interfaces.

From the Java Language Specification (§9.8):

> “A functional interface type is an interface that has just one abstract method, aside from the methods of `Object` that it overrides.”

So `equals(Object)` is ignored.

---

## ⚙️ Proof — You can check annotation in source code

If you open the actual JDK source:

```java
@FunctionalInterface
public interface Comparator<T> {
    int compare(T o1, T o2);
    ...
}
```

It’s **explicitly annotated** as a functional interface.

If it really had more than one abstract method, the compiler would throw an error:

```
Unexpected @FunctionalInterface annotation
multiple non-overriding abstract methods found
```

…but it doesn’t — because only one abstract method (`compare`) exists.

---

## 🧩 Summary Table

| Type of method          | Counts toward functional interface rule? | Example in `Comparator`         |
| ----------------------- | ---------------------------------------- | ------------------------------- |
| Abstract                | ✅ Yes                                   | `compare()`                     |
| Default                 | 🚫 No                                    | `thenComparing()`, `reversed()` |
| Static                  | 🚫 No                                    | `comparing()`                   |
| Inherited from `Object` | 🚫 No                                    | `equals()`                      |

---

## 🔥 Quick Example

```java
Comparator<String> comp = (s1, s2) -> s1.compareToIgnoreCase(s2);

System.out.println(comp.compare("apple", "Banana")); // returns negative
```

✅ Works fine because the lambda matches `int compare(T o1, T o2)`.

---

## ✅ Summary in one line

> `Comparator` is a **functional interface** because it has only **one abstract method (`compare`)**;
> all others are **default, static, or inherited from `Object`**, so they don’t count.

---

---
