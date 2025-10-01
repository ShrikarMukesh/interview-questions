In **Spring Boot with JPA/Hibernate**, **Lazy Loading** and **Eager Loading** define **how related entities are fetched from the database**.

---

## 🔹 1. Lazy Loading (default in JPA)

- **Definition:** Associated entities are **not loaded immediately** with the parent entity.
- They are fetched **only when you access them** (on-demand).
- Uses **proxies** under the hood.

✅ **Example:**

```java
@Entity
public class Order {
    @Id
    private Long id;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)  // Lazy (default)
    private List<Item> items;
}
```

```java
Order order = orderRepository.findById(1L).get();
// Only Order is loaded, Items are not yet fetched

order.getItems().size();
// At this moment, Hibernate fires a query to load items
```

👉 Advantage: Saves memory & improves performance if you don’t always need child entities.
👉 Disadvantage: May cause **N+1 query problem** or `LazyInitializationException` if accessed outside a transaction/session (e.g., in Controller after session closed).

---

## 🔹 2. Eager Loading

- **Definition:** Associated entities are **loaded immediately** along with the parent entity.
- Hibernate fetches them in the **same query (via join)** or with **multiple select queries**.

✅ **Example:**

```java
@Entity
public class Order {
    @Id
    private Long id;

    @OneToMany(mappedBy = "order", fetch = FetchType.EAGER)  // Eager
    private List<Item> items;
}
```

```java
Order order = orderRepository.findById(1L).get();
// Order + Items loaded together in a single query (JOIN)
```

👉 Advantage: No `LazyInitializationException`, easy to use when child entities are **always needed**.
👉 Disadvantage: Might load **huge data unnecessarily**, hurting performance.

---

## 🔹 3. Best Practices

- Default to **Lazy** for collections (`@OneToMany`, `@ManyToMany`).
- Use **Eager** only when you are **sure** you’ll always need the relationship.
- To fetch lazily loaded data when required → use **`JOIN FETCH`** or **EntityGraph**.

✅ Example with `JOIN FETCH` (avoids N+1 problem):

```java
@Query("SELECT o FROM Order o JOIN FETCH o.items WHERE o.id = :id")
Order findOrderWithItems(@Param("id") Long id);
```

---

📌 Quick Summary:

- **Lazy** → Loads related data **when accessed** (default for collections).
- **Eager** → Loads related data **immediately** (default for `@ManyToOne`, `@OneToOne`).

---
