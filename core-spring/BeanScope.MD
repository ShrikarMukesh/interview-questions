Excellent 👍 — let’s go **deep but clear** into **Spring Boot Bean Scopes**, including how they behave, when to use which, and **code examples** that you can run in a small demo project.

---

## 🌱 **Spring Boot Bean Scopes — Deep Explanation with Examples**

Spring beans live inside the **IoC container**, and each bean has a **scope** that tells the container **how many instances to create** and **how long to keep them**.

---

# 🧩 1. **Singleton Scope (Default)**

### 🔹 Meaning:

- Only **one instance** of the bean is created **per Spring container**.
- Every time you inject that bean, you get the **same instance**.

### 🔹 Example:

```java
package com.example.demo.scope;

import org.springframework.stereotype.Component;

@Component
public class SingletonBean {
    public SingletonBean() {
        System.out.println("👉 SingletonBean instance created");
    }
}
```

```java
package com.example.demo.controller;

import com.example.demo.scope.SingletonBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ScopeTestController {

    @Autowired
    private SingletonBean bean1;

    @Autowired
    private SingletonBean bean2;

    @GetMapping("/singleton")
    public String testSingleton() {
        return "bean1 == bean2 ? " + (bean1 == bean2);
    }
}
```

**Output:**

```
👉 SingletonBean instance created
bean1 == bean2 ? true
```

🧠 **Explanation:**

- Only one object created.
- Both injections (`bean1`, `bean2`) refer to the same instance.

✅ **Use it for:** Services, DAOs, or any **stateless component**.

---

# 🧩 2. **Prototype Scope**

### 🔹 Meaning:

- A **new instance** is created **every time** the bean is requested.
- Spring creates a **fresh object** for each injection or lookup.

### 🔹 Example:

```java
package com.example.demo.scope;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class PrototypeBean {
    public PrototypeBean() {
        System.out.println("👉 PrototypeBean instance created");
    }
}
```

```java
@GetMapping("/prototype")
public String testPrototype() {
    PrototypeBean p1 = context.getBean(PrototypeBean.class);
    PrototypeBean p2 = context.getBean(PrototypeBean.class);
    return "p1 == p2 ? " + (p1 == p2);
}
```

**Output:**

```
👉 PrototypeBean instance created
👉 PrototypeBean instance created
p1 == p2 ? false
```

🧠 **Explanation:**

- A new instance is created **each time** it’s requested.
- Spring does **not manage its full lifecycle** (no automatic destroy).

✅ **Use it for:** Short-lived or **stateful beans** (like a per-operation handler).

---

# 🧩 3. **Request Scope** (Web Applications)

### 🔹 Meaning:

- One bean instance **per HTTP request**.
- Each web request gets its **own bean instance**.

### 🔹 Example:

```java
package com.example.demo.scope;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST)
public class RequestBean {
    public RequestBean() {
        System.out.println("👉 RequestBean instance created");
    }
}
```

```java
@GetMapping("/request")
public String testRequest(@Autowired RequestBean requestBean) {
    return "RequestBean hashCode: " + requestBean.hashCode();
}
```

**Output:**

```
👉 RequestBean instance created
RequestBean hashCode: 105493874
```

🧠 **Explanation:**

- Each HTTP request creates a new bean.
- If two requests hit `/request`, two beans are created.

✅ **Use it for:** Request-specific data, validation contexts, etc.

---

# 🧩 4. **Session Scope**

### 🔹 Meaning:

- One bean instance **per HTTP session** (per user).
- All requests from the same user session share the same bean.

### 🔹 Example:

```java
package com.example.demo.scope;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

@Component
@Scope(value = WebApplicationContext.SCOPE_SESSION)
public class SessionBean {
    public SessionBean() {
        System.out.println("👉 SessionBean instance created");
    }
}
```

```java
@GetMapping("/session")
public String testSession(@Autowired SessionBean sessionBean) {
    return "SessionBean hashCode: " + sessionBean.hashCode();
}
```

**Output:**

```
👉 SessionBean instance created
SessionBean hashCode: 20457893
```

🧠 **Explanation:**

- One per session.
- Same user = same bean.
- New session = new bean.

✅ **Use it for:** User login details, shopping carts, etc.

---

# 🧩 5. **Application Scope**

### 🔹 Meaning:

- One bean instance **per web application context** (shared by all users and requests).

### 🔹 Example:

```java
package com.example.demo.scope;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

@Component
@Scope(WebApplicationContext.SCOPE_APPLICATION)
public class ApplicationScopeBean {
    public ApplicationScopeBean() {
        System.out.println("👉 ApplicationScopeBean created");
    }
}
```

🧠 **Explanation:**

- Similar to singleton but specific to the **ServletContext**.
- Common for web-wide caches or metrics.

✅ **Use it for:** App-wide configuration or data shared by all sessions.

---

# 🧩 6. **WebSocket Scope**

### 🔹 Meaning:

- One bean instance per WebSocket session.

```java
@Component
@Scope("websocket")
public class WebSocketScopeBean {
    public WebSocketScopeBean() {
        System.out.println("👉 WebSocketScopeBean created");
    }
}
```

✅ **Use it for:** Storing per-session WebSocket state or message context.

---

# ⚙️ **How to test scopes easily**

```java
@RestController
public class BeanScopeController {

    @Autowired private SingletonBean singletonBean1;
    @Autowired private SingletonBean singletonBean2;

    @Autowired private PrototypeBean prototypeBean1;
    @Autowired private PrototypeBean prototypeBean2;

    @GetMapping("/compare")
    public String compare() {
        return """
            Singleton same? %s
            Prototype same? %s
            """.formatted(
                (singletonBean1 == singletonBean2),
                (prototypeBean1 == prototypeBean2)
        );
    }
}
```

**Output:**

```
Singleton same? true
Prototype same? false
```

---

# 🧠 **Key Differences Summary**

| Scope           | Instances Created  | Lifecycle               | Typical Use Case    |
| --------------- | ------------------ | ----------------------- | ------------------- |
| **singleton**   | 1 per container    | Till container shutdown | Stateless services  |
| **prototype**   | New each time      | Created on demand       | Stateful tasks      |
| **request**     | 1 per HTTP request | Request end             | Web request data    |
| **session**     | 1 per user session | Session end             | User-specific info  |
| **application** | 1 per web app      | App context end         | Shared config/cache |
| **websocket**   | 1 per WebSocket    | Connection end          | WS session data     |

---

# 💬 **Important Notes**

- Spring beans are **singleton by default**.
- Scopes like `request`, `session`, `application`, and `websocket` only work in **web-aware ApplicationContexts**.
- Use **`@Scope(value, proxyMode = ScopedProxyMode.TARGET_CLASS)`** if you inject request/session scoped beans into singleton beans.
