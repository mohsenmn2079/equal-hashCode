# `equals()` and `hashCode()` in Java

## Table of Contents
- [What is `equals()`?](#what-is-equals)
- [What is `hashCode()`?](#what-is-hashcode)
- [The Contract Between `equals()` and `hashCode()`](#the-contract)
- [Default Behavior (Without Override)](#default-behavior)
- [Practice Walkthrough](#practice-walkthrough)
  - [User.java](#userjava)
  - [Main.java](#mainjava)
  - [Step-by-step Output Explanation](#step-by-step-output-explanation)
- [Key Takeaways](#key-takeaways)

---

## What is `equals()`?

`equals()` is a method defined in `java.lang.Object`, the root of every Java class.  
Its job is to answer the question:

> **"Are these two objects logically equal?"**

```java
public boolean equals(Object obj)
```

- By default it compares **references** (memory addresses), meaning two different objects are never equal even if they hold the same data.
- You **override** it to define what "equal" means for your specific class — usually comparing the meaningful fields.

### Rules you must follow when overriding `equals()`:

| Rule | Meaning |
|------|---------|
| **Reflexive** | `x.equals(x)` must be `true` |
| **Symmetric** | If `x.equals(y)` is `true`, then `y.equals(x)` must also be `true` |
| **Transitive** | If `x.equals(y)` and `y.equals(z)`, then `x.equals(z)` must be `true` |
| **Consistent** | Multiple calls return the same result as long as nothing changes |
| **Null-safe** | `x.equals(null)` must return `false`, never throw an exception |

---

## What is `hashCode()`?

`hashCode()` is also defined in `java.lang.Object`.  
Its job is to return an **integer number** that represents the object — a "fingerprint".

```java
public int hashCode()
```

This number is used internally by **hash-based collections** like `HashMap`, `HashSet`, and `Hashtable` to quickly locate where an object is stored in memory (its "bucket").

### How a `HashMap` uses `hashCode()`:

```
put(key, value)
  │
  ├─► call key.hashCode()  →  get an int  →  calculate bucket index
  │
  └─► store value in that bucket

get(key)
  │
  ├─► call key.hashCode()  →  find the bucket
  │
  └─► call key.equals(candidate)  →  confirm it's the right object
```

---

## The Contract

> **If two objects are equal according to `equals()`, they MUST have the same `hashCode()`.**

```
a.equals(b) == true  →  a.hashCode() == b.hashCode()   ✅ required

a.hashCode() == b.hashCode()  →  a.equals(b) == true   ❌ NOT required (collision is OK)
```

Breaking this contract causes **silent bugs** in `HashMap` and `HashSet`:
objects that *should* be found will be **invisible** because the map looks in the wrong bucket.

---

## Default Behavior (Without Override)

| Method | Default behavior |
|--------|-----------------|
| `equals()` | `this == obj` — compares memory addresses |
| `hashCode()` | Based on memory address (native JVM implementation) |

So without overriding, two `new User("ali1")` objects are **not** equal and produce **different** hash codes — even though they represent the same user.

---

## Practice Walkthrough

### `User.java`

```java
public class User {
    int id;
    String name;

    public User(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof User && ((User) obj).name.equals(name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
```

#### What `equals()` does here:
1. `obj instanceof User` — checks that the other object is actually a `User` (and not `null` or a different type). This satisfies the null-safety and type-safety rules.
2. `((User) obj).name.equals(name)` — casts `obj` to `User` and compares the `name` fields using `String`'s own `equals()`.

So two `User` objects are considered equal **if and only if they have the same name**.

#### What `hashCode()` does here:
- Delegates entirely to `String.hashCode()` on the `name` field.
- Since `equals()` is based only on `name`, `hashCode()` is also based only on `name`. This correctly satisfies the contract.

---

### `Main.java`

```java
User user1 = new User("ali1");
User user2 = new User("ali2");

// 1. equals test
boolean result = user1.equals(user2);
System.out.printf("result: %s\n", result);

// 2. hashCode test
int user1HashCode = user1.hashCode();
int user2HashCode = user2.hashCode();
System.out.printf("user1HashCode: %d, user2HashCode: %d\n", user1HashCode, user2HashCode);

// 3. HashMap test
Map<User, String> map = new HashMap<>();
map.put(user1, "ali1");
map.put(user2, "ali2");

System.out.printf("map size: %s\n", map.size());
System.out.printf("map.get(user2): %s\n", map.get(user2));
```

---

### Step-by-step Output Explanation

#### Step 1 — `equals()` test

```
result: true
```

`user1` and `user2` are two **separate objects** in memory, but both have `name = "ali"`.  
Because `equals()` is overridden to compare `name`, the result is `true`.  
Without the override, it would have been `false`.

---

#### Step 2 — `hashCode()` test

```
user1HashCode: 96834, user2HashCode: 96834
```

Both objects have `name = "ali1"`, so `"ali2".hashCode()` returns the same integer for both.  
This satisfies the contract: equal objects → equal hash codes.

---

#### Step 3 — `HashMap` test

```
map size: 1
map.get(user2): ali2
```

This is the most interesting result.

- `map.put(user1, "ali1")` — `HashMap` calls `user1.hashCode()` → finds a bucket → stores `"ali1"`.
- `map.put(user2, "ali2")` — `HashMap` calls `user2.hashCode()` → **same bucket** → calls `user2.equals(user1)` → **`true`** → treats `user2` as the **same key** and **overwrites** the value with `"ali1"`.

So the map ends up with **only 1 entry** (not 2), and `map.get(user2)` returns `"ali2"` — the last value written.

This perfectly demonstrates why both methods must be overridden together:  
the `HashMap` relies on **both** `hashCode()` (to find the bucket) **and** `equals()` (to confirm the match).

---

## Key Takeaways

1. **Always override both methods together.** Overriding only one breaks hash-based collections.
2. **Base both methods on the same fields.** Here both are based on `name` alone.
3. **`equals()` defines logical identity**; two different objects can be logically the same.
4. **`hashCode()` is a performance optimization** — it narrows down the search space so `equals()` doesn't have to scan every element.
5. **Same hash ≠ equal.** Hash collisions are allowed, but equal objects must share a hash.
6. A `HashMap` with logically equal keys will **merge entries**, not create duplicates — a crucial behavior that depends entirely on a correct `equals()`/`hashCode()` implementation.
