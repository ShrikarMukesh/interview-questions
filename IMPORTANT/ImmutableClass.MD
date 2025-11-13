**Immutable classes in Java** — from **concept** → **rules** → **examples** → **internal JVM behavior** → **common interview traps** 🔥

---

## 🧩 1️⃣ What is an Immutable Class?

> An **immutable class** is a class whose **state (data/fields)** cannot be changed once the object is created.

That means:

- Once you construct it, you can never modify its internal fields.
- Any “change” returns a **new object** instead of modifying the existing one.

---

### ✅ Example of Immutable Class — `String`

```java
String s1 = "Java";
String s2 = s1.concat(" Developer");

System.out.println(s1); // Java
System.out.println(s2); // Java Developer
```

Notice — `concat()` didn’t modify `s1`; it created a **new String object**.
That’s the essence of immutability.

---

## 🧱 2️⃣ How to create your own Immutable Class

Let’s build an example.

```java
public final class Employee {
    private final String name;
    private final int age;

    public Employee(String name, int age) {
        this.name = name;
        this.age = age;
    }

    // Only getters, no setters
    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    @Override
    public String toString() {
        return "Employee{name='" + name + "', age=" + age + "}";
    }
}
```

---

### ✅ Test:

```java
Employee e1 = new Employee("Shrikar", 30);
System.out.println(e1); // Employee{name='Shrikar', age=30}

// No way to change internal fields
// e1.setAge(40); ❌ (doesn't exist)
```

---

## 🔒 3️⃣ Rules for Making a Class Immutable

| #   | Rule                                                                                                           | Why                                                         |
| --- | -------------------------------------------------------------------------------------------------------------- | ----------------------------------------------------------- |
| 1   | Declare the class as **`final`**                                                                               | Prevent subclass modification of behavior.                  |
| 2   | Make **all fields `private final`**                                                                            | Prevent direct modification and allow assignment only once. |
| 3   | Initialize all fields **in the constructor**                                                                   | So that state is complete at creation.                      |
| 4   | Do **not provide any setters**                                                                                 | Prevent modification after construction.                    |
| 5   | For mutable fields (like arrays, lists, or custom objects), **perform deep copies** in constructor and getter. | Prevent indirect modification via references.               |

---

## 🧠 4️⃣ Example — Immutable Class with Mutable Object Inside

If your class has a mutable field (like `Date` or `List`), you must protect it!

### ❌ Wrong (Mutable)

```java
public final class Student {
    private final String name;
    private final Date dob; // mutable type

    public Student(String name, Date dob) {
        this.name = name;
        this.dob = dob; // direct assignment (BAD)
    }

    public Date getDob() {
        return dob; // exposes internal mutable object
    }
}
```

### ✅ Correct (Immutable)

```java
import java.util.Date;

public final class Student {
    private final String name;
    private final Date dob;

    public Student(String name, Date dob) {
        this.name = name;
        // defensive copy
        this.dob = new Date(dob.getTime());
    }

    public String getName() {
        return name;
    }

    public Date getDob() {
        // return a copy instead of the original
        return new Date(dob.getTime());
    }
}
```

Now external code can’t mutate the internal `dob`:

```java
Date d = new Date();
Student s = new Student("John", d);

d.setTime(0); // modifies 'd' but not s.dob
System.out.println(s.getDob()); // unaffected
```

---

## ⚙️ 5️⃣ Internal Working in JVM

Immutable objects are stored just like any other object, but:

- Once created, **their fields never change**, so they can be **cached or shared safely**.
- The JVM can reuse objects (like String literals) because it knows they’ll never mutate.
- Immutable objects are **thread-safe** by design — no synchronization needed.

Example:

```java
String a = "Java";
String b = "Java";
System.out.println(a == b); // true (string pool reuse)
```

---

## 🧵 6️⃣ Thread-Safety Advantage

Immutable objects are automatically **thread-safe** because no thread can change their internal state after creation.

That’s why classes like `String`, `Integer`, `LocalDate`, and `BigDecimal` are immutable.

---

## ⚡ 7️⃣ Common Immutable Classes in Java

| Package     | Class                                                  |
| ----------- | ------------------------------------------------------ |
| `java.lang` | `String`, `Integer`, `Boolean`, `Long`, `Double`, etc. |
| `java.time` | `LocalDate`, `LocalTime`, `LocalDateTime`, `Instant`   |
| `java.math` | `BigInteger`, `BigDecimal`                             |
| `java.util` | `Optional`                                             |

---

## 🧩 8️⃣ When to Use Immutable Classes

✅ Prefer immutability when:

- You want **thread safety without synchronization**.
- You need **caching or sharing** (e.g., flyweight pattern, value objects).
- You want **predictable behavior** (objects don’t change unexpectedly).

🚫 Avoid when:

- You frequently modify state — immutability could cause **object churn** (too many new instances).

---

## 🧠 9️⃣ Common Interview Questions

### Q1: Why should immutable classes be `final`?

To prevent subclassing. A subclass could override a method to mutate internal state.

### Q2: Are all wrapper classes immutable?

Yes — `Integer`, `Long`, `Double`, `Boolean`, etc. are immutable.

### Q3: Is `StringBuilder` immutable?

No! It’s **mutable** — you can modify contents via `append()` or `delete()`.

### Q4: Can an immutable class have mutable objects inside?

Yes — but it must use **deep copies** (defensive copying) to protect internal state.

---

## ✅ TL;DR Summary

| Concept                | Rule / Behavior                                |
| ---------------------- | ---------------------------------------------- |
| Definition             | State can’t change after creation              |
| Key Keyword            | `final` class + `private final` fields         |
| Thread-safe?           | ✅ Yes                                         |
| Common Examples        | `String`, `Integer`, `LocalDate`, `BigDecimal` |
| Protect mutable fields | Use **deep copy**                              |
| Advantage              | Safe, reusable, easy to reason about           |
| Disadvantage           | More object creation = potential GC pressure   |

---
