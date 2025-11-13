
# 🌳 **Java HashMap Internal Implementation**

---

## 1. **Basic Idea**

A `HashMap<K,V>` stores data in the form of **key-value pairs**.
It uses **hashing** to provide almost `O(1)` average-time complexity for `get()` and `put()` operations.

---

## 2. **Important Terms**

* **Capacity**: Number of buckets (default = 16).
* **Load Factor**: Threshold to decide resizing (default = 0.75).
  Threshold = `capacity × load factor`.
* **Size**: Actual number of key-value pairs.
* **Node<K,V>**: Each entry in the map is stored as a `Node` object.

```java
static class Node<K,V> implements Map.Entry<K,V> {
    final int hash;
    final K key;
    V value;
    Node<K,V> next;   // linked list chaining
}
```

---

## 3. **How Data is Stored (put operation)**

Let’s say:

```java
map.put(10, "A");
```

Steps:

1. **Compute hash** of the key → `hash = hash(key)`.

    * HashMap uses a **hash function** to reduce collisions:

      ```java
      hash = (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
      ```
2. **Find index** in the table:

   ```java
   index = hash & (capacity - 1);
   ```

   (Bitwise AND is faster than `%`).
3. **Check bucket**:

    * If empty → create new Node and insert.
    * If not empty (collision):

        * Compare keys (using `equals()`):

            * If key exists → update value.
            * Else:

                * Before Java 8 → add to **LinkedList** (chaining).
                * In Java 8 → If bucket has many nodes (≥ 8), convert LinkedList → **Balanced Red-Black Tree**.

---

## 4. **Retrieval (get operation)**

```java
map.get(10);
```

Steps:

1. Compute `hash` and `index` same way.
2. Go to that bucket.
3. Traverse linked list (or tree if converted).
4. Compare key using `equals()`.
5. Return the value if found.

---

## 5. **Resizing / Rehashing**

When size > threshold (`capacity × load factor`):

1. Create new table with **double capacity**.
2. Rehash all existing entries into new table.
3. This is an expensive operation ⚡, so HashMap grows gradually.

---

## 6. **Java 8 Optimization**

Before Java 8:

* Buckets used **LinkedList** only.
* Worst-case → `O(n)` if many collisions.

Since Java 8:

* If collisions exceed **TREEIFY_THRESHOLD (8 nodes)**, LinkedList → **Red-Black Tree** (`O(log n)` lookup).
* If size reduces below **UNTREEIFY_THRESHOLD (6 nodes)**, Tree → LinkedList again.

This prevents **performance degradation due to hash collisions (DOS attack protection)**.

---

## 7. **Null Handling**

* HashMap allows **one null key** (stored in `table[0]`).
* Allows **multiple null values**.

---

## 8. **Time Complexity**

* **put/get/remove**:

    * Average case → `O(1)`
    * Worst case → `O(log n)` (with Tree), earlier it was `O(n)` with LinkedList.

---

## 9. **Example Walkthrough**

```java
HashMap<Integer, String> map = new HashMap<>();
map.put(1, "One");    // stored at index = hash(1) & 15
map.put(17, "Seventeen"); // collision with index of 1 (since 17%16=1)
map.put(33, "ThirtyThree"); // another collision
```

* Index 1 bucket:

    * Before Java 8 → LinkedList: [1=One] → [17=Seventeen] → [33=ThirtyThree]
    * In Java 8 → If bucket grows ≥ 8, convert to Red-Black Tree.

---

✅ **In short**:

* Hashing → Indexing
* Collision Handling → LinkedList/Tree
* Resizing → Doubling capacity
* Java 8 → Tree improves worst-case performance.

---


When you create:

```java
HashMap<Integer, String> map = new HashMap<>();
```

👉 By default, **HashMap** in Java has:

* **Initial capacity = 16** (number of buckets in the table).
* **Load factor = 0.75** (threshold before resizing).

So, even though the map is *empty*, internally the **initial size (capacity)** is **16 buckets**, and it will expand when the number of entries exceeds `16 * 0.75 = 12`.

⚡ Difference to note:

* `size()` → number of key-value pairs (here it’s `0`).
* *Capacity* → number of buckets (here it’s `16`).

Would you like me to also explain **how capacity changes (doubling strategy)** when more elements are added?


---

# 🌳 HashMap Collision Handling in Java 8+

### 1️⃣ Initial Insertion — LinkedList

* When few keys collide, they are stored as a **LinkedList** in the same bucket.

```
Bucket[5] → [Key-1] → [Key-2] → [Key-3] → [Key-4] ...
```

👉 Uses `Node<K,V>` (singly linked list).

---

### 2️⃣ Exceed TREEIFY_THRESHOLD (8) → Treeify

* Once collisions in a single bucket exceed **8 elements**, and the table size is at least **64**,
  the bucket transforms into a **balanced Red-Black Tree**.

```
Bucket[5] → TreeNode (Root)
             /       \
         Key-1      Key-5
         /   \      /   \
     Key-2  Key-3 Key-6 Key-7
                 \
                 Key-8
```

👉 Uses `TreeNode<K,V>` (Red-Black Tree).
👉 Improves lookup from **O(n)** → **O(log n)**.

---

### 3️⃣ Deletions → Drop below UNTREEIFY_THRESHOLD (6)

* If enough elements are removed, reducing the bucket size to **6 or fewer**,
  the tree **converts back to a LinkedList**.

```
Bucket[5] → [Key-3] → [Key-4] → [Key-6] → [Key-7] → [Key-8]
```

👉 Back to `Node<K,V>`.
👉 Avoids unnecessary tree overhead for small collisions.

---

## ⚖ Threshold Constants in `HashMap`

```java
static final int TREEIFY_THRESHOLD = 8;   // Treeify when bucket size >= 8
static final int UNTREEIFY_THRESHOLD = 6; // Untreeify when bucket size <= 6
static final int MIN_TREEIFY_CAPACITY = 64; // Table must be >= 64 before treeify
```

---

### 📊 Summary Table

| Condition                     | Data Structure Used                    |
| ----------------------------- | -------------------------------------- |
| Collisions < 8                | **LinkedList** (`Node`)                |
| Collisions ≥ 8 and table ≥ 64 | **Tree (Red-Black Tree)** (`TreeNode`) |
| Collisions drop ≤ 6           | Convert back to **LinkedList**         |

---

✅ This mechanism balances **performance**:

* Small buckets → simpler LinkedList (low memory).
* Large buckets → Red-Black Tree (fast lookups).

---

![img.png](img.png)

---

## 1. **LinkedList Node (used in HashMap before Java 8)**

A **Node** in a linked list has:

* A **key**
* A **value**
* A **hash** (cached hash of the key)
* A **next** pointer (to the next node in the same bucket)

```java
static class Node<K,V> implements Map.Entry<K,V> {
    final int hash;
    final K key;
    V value;
    Node<K,V> next;  // points to the next node (linked list)
}
```

### Characteristics:

* **Structure**: Simple linear chain.
* **Performance**: Lookup is **O(n)** in worst case (if all keys hash to the same bucket).
* **Memory**: Lightweight (just 1 pointer to `next`).
* **Usage**: Before Java 8, HashMap buckets always used this.

---

## 2. **TreeNode (introduced in Java 8)**

When collisions become large, HashMap transforms the bucket into a **balanced Red-Black Tree**.
A **TreeNode** has:

* Key, value, hash (same as Node).
* Left, right, parent pointers (like a binary tree).
* A boolean **color** (red or black for balancing).

```java
static final class TreeNode<K,V> extends LinkedHashMap.Entry<K,V> {
    TreeNode<K,V> parent;
    TreeNode<K,V> left;
    TreeNode<K,V> right;
    TreeNode<K,V> prev; 
    boolean red;
}
```

### Characteristics:

* **Structure**: Red-Black Tree inside a bucket.
* **Performance**: Lookup is **O(log n)** instead of O(n).
* **Memory**: Heavier (extra pointers: parent, left, right, plus color flag).
* **Usage**: Activated when bucket size > 8 (TREEIFY_THRESHOLD).

---

## 🔑 Key Differences:

| Feature              | **LinkedList Node** | **TreeNode (RB Tree)**              |
| -------------------- | ------------------- | ----------------------------------- |
| Structure            | Singly linked list  | Balanced Red-Black Tree             |
| Lookup (worst case)  | O(n)                | O(log n)                            |
| Memory overhead      | Low                 | Higher (extra pointers + balancing) |
| Introduced in        | Java 1.2            | Java 8                              |
| Conversion threshold | Default             | When bucket > 8 entries             |

---

👉 In summary:

* **LinkedList Node** is simple, efficient for small collisions.
* **TreeNode** improves performance for large collisions by avoiding long chains.

Perfect 👌 Let’s go deeper into **Treeify ↔ Untreeify** in Java 8 HashMap.

---

## 🌳 When HashMap **Treeifies** (LinkedList → TreeNode)

HashMap decides to convert a bucket from a **LinkedList** to a **Red-Black Tree** if:

1. **Collision count > TREEIFY_THRESHOLD (8 by default)**

    * If a single bucket (same hash index) has **more than 8 nodes**, HashMap tries to treeify.

2. **BUT**: Treeification only happens if the **table (array of buckets) size ≥ MIN_TREEIFY_CAPACITY (64)**.

    * If the table is too small (<64), HashMap resizes (doubles capacity) instead of treeifying.
    * This avoids expensive tree nodes for small maps.

📌 Code constants from `HashMap.java`:

```java
static final int TREEIFY_THRESHOLD = 8;
static final int UNTREEIFY_THRESHOLD = 6;
static final int MIN_TREEIFY_CAPACITY = 64;
```

---

## 🍂 When HashMap **Untreeifies** (TreeNode → LinkedList)

If a bucket (which is a tree) shrinks due to **removals**:

* When the number of nodes in that bucket becomes **≤ UNTREEIFY_THRESHOLD (6 by default)**,
  HashMap converts the **tree back into a LinkedList**.

Reason:

* Maintaining a Red-Black tree for very few nodes is more costly than a simple linked list.
* Switching back saves memory and overhead.

---

## 🔑 Summary Table

| Condition                           | Action                                 |
| ----------------------------------- | -------------------------------------- |
| Bucket size ≤ 8                     | Keep as LinkedList                     |
| Bucket size > 8 AND table size ≥ 64 | Convert to TreeNode (treeify)          |
| Bucket size drops ≤ 6               | Convert back to LinkedList (untreeify) |

---

## ⚡ Example Scenario

1. Start with collisions in a bucket → nodes form a LinkedList.
2. Once **9th element** collides → if table size ≥ 64 → LinkedList → TreeNode.
3. If later some elements are removed and only **6 remain** → TreeNode → LinkedList again.

---

👉 So HashMap dynamically balances **memory (LinkedList)** vs **performance (Tree)** depending on load.



