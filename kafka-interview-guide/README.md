
---

## 🔹 1. Kafka Basics

1. What is Apache Kafka, and how is it different from traditional message brokers (RabbitMQ, ActiveMQ)?
2. Explain Kafka’s core components: **Topic, Partition, Broker, Consumer Group**.
3. How does Kafka ensure **scalability** and **fault tolerance**?
4. What is the role of **Zookeeper** in Kafka?
5. Difference between **queue semantics** and **publish-subscribe semantics** in Kafka.

---

## 🔹 2. Kafka Producers

6. What are **acks=0, acks=1, acks=all**?
7. How do **idempotent producers** work in Kafka?
8. Explain **linger.ms**, **batch.size**, and **compression.type** in producers.
9. How does Kafka ensure message ordering within a partition?
10. When would you choose **keyed messages** in a producer?

---

## 🔹 3. Kafka Consumers

11. Explain **consumer groups** and **partition rebalancing**.
12. How does Kafka manage **offsets**? (auto vs manual commits)
13. What’s the difference between **commitSync()** and **commitAsync()**?
14. What happens if a consumer crashes before committing offsets?
15. Explain **rebalance listeners** in Kafka consumers.

---

## 🔹 4. Kafka Internals

16. How does Kafka achieve **high throughput**?
17. What is a **replication factor** and **in-sync replica (ISR)**?
18. Explain **leader election** in Kafka.
19. What happens if a Kafka broker goes down?
20. How does Kafka handle **data durability** (log segments, retention, compaction)?

---

## 🔹 5. Spring Boot + Kafka

21. How do you configure a **Kafka producer/consumer** in Spring Boot?
22. Explain the use of **@KafkaListener** and **KafkaTemplate**.
23. How do you implement a **retry mechanism** in Spring Kafka (e.g., retry after 60s)?
24. How to implement **Dead Letter Topic (DLT)** in Spring Boot Kafka?
25. How do you ensure **exactly-once delivery semantics** with Spring Kafka?

---

## 🔹 6. Advanced Kafka

26. Explain **at-most-once**, **at-least-once**, and **exactly-once** delivery guarantees.
27. How does Kafka handle **backpressure** if consumers are slow?
28. Difference between **Kafka Streams** and **Kafka Connect**.
29. Explain **schema evolution** with Avro/Protobuf + Schema Registry.
30. How do you handle **poison messages** in Kafka?

---

## 🔹 7. Real-World Scenarios (Senior-level)

31. How would you design a **Kafka-based banking transaction system** ensuring no double spending?
32. How would you migrate a microservice from **REST to event-driven** using Kafka?
33. How do you ensure **ordering of events across multiple partitions**?
34. How do you **scale consumers horizontally**?
35. How do you secure Kafka with **SSL, SASL, OAuth2** in production?

---

## 🔹 8. Monitoring & DevOps

36. How do you monitor Kafka cluster health? (Prometheus, Grafana, Confluent Control Center)
37. What is **consumer lag**, and how do you detect/fix it?
38. How do you tune Kafka performance? (disk, batch, compression)
39. How to set up Kafka in **Kubernetes** with persistence?
40. How to handle **multi-datacenter Kafka setup** (MirrorMaker, Confluent Replicator)?

---
