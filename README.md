# 🧪 Kafka Beginner Project

Welcome to the Kafka Beginner Project! This repository provides a simple, beginner-friendly example of how Apache Kafka can be used for messaging between microservices.

---

## 📸 Architecture Overview

![Kafka Microservices Implementation](Implementation.png)

---

## 🧩 Project Structure

This project consists of **three services** that communicate using Kafka as the messaging backbone.

### 🔹 Service 1 (`Order service`)
- **Role**: Producer
- **Description**: Generates messages (events) and pushes them to a Kafka topic.

### 🔹 Service 2 (`Stocks service`)
- **Role**: Consumer
- **Description**: Listens to messages from Kafka and processes them.

### 🔹 Service 3 (`Email service`)
- **Role**: Consumer / Logger
- **Description**: Another consumer that logs or stores the messages for auditing or secondary processing.

---

## 🔄 Kafka Messaging Flow

1. `Order service` publishes messages to a Kafka topic.
2. `Stocks` and `Email` both subscribe to that topic.
3. Kafka ensures that each consumer gets a copy of the message (fan-out model).

---

## 🚀 Getting Started

### Prerequisites
- Docker & Docker Compose (or install Kafka & Zookeeper manually)
- Spring boot  / Java installed and set up

### 🛠️ To Run Using Docker
```bash
docker-compose up --build
```
# Kafka Best Practices Checklist  

---

## ⚙️ Setup & Infrastructure
- Latest stable Kafka version
- ≥ 3 brokers (odd number)
- Use partitions for scalability
- Replication factor ≥ 3
- KRaft mode if new cluster
- Fast SSDs; separate OS/log disks
- Monitor broker & disk health

## 🛡 Security
- Enable TLS/SSL encryption
- Use SASL/SCRAM for authentication
- Configure ACLs for topic access
- Secure schema registry & Connect

## 📦 Topic Design
- Clear naming (`orders.created`)
- Choose partition count wisely
- Set cleanup policy (delete vs compact)
- Avoid too many small topics

## 📝 Producer Best Practices
- Enable idempotence
- Use compression (lz4/zstd)
- Batch records (linger.ms, batch.size)
- Handle retries/timeouts
- Use keys to control partitioning

## 📥 Consumer Best Practices
- Use consumer groups
- Understand offset commit strategies
- Tune poll configs
- Handle rebalances
- Avoid slow consumers (prevent lag)

## 📡 Inter-service Design
- Prefer async/event-driven
- Version schemas & use registry
- Support backward/forward compatibility

## 📊 Monitoring & Observability
- Prometheus + Grafana
- Kafka UI tools (AKHQ, Conduktor)
- Distributed tracing (Jaeger, Zipkin)
- Monitor consumer lag & broker metrics

## 🔄 Operations
- Automate topic creation (IaC)
- CI/CD for configs & deployments
- Plan scaling (partitions, brokers)
- Backup & test disaster recovery
- Rolling restarts for upgrades

## ⚡ Application Coding
- Avoid blocking calls in reactive apps
- Use retries & dead letter topics
- Keep consumers idempotent
- Add context (correlation IDs)

## 🧪 Testing & Quality
- Use testcontainers for integration
- Mock Kafka only in unit tests
- Contract testing for schema changes

---
**Summary:**
- Secure by default
- Partition wisely
- Monitor everything
- Automate deployment
- Idempotent consumers
- Schema registry for evolution

  # 🐳 Kafka with KRaft — Quick Reference

Direct, essential CLI & setup notes for running Apache Kafka in KRaft mode (no ZooKeeper).

---

## ⚙️ Setup Kafka in KRaft Mode

```bash
# Generate a unique cluster ID
bin/kafka-storage.sh random-uuid

# Format storage using the generated cluster ID
bin/kafka-storage.sh format -t <cluster-id> -c config/kraft/server.properties

# Start the Kafka broker
bin/kafka-server-start.sh config/kraft/server.properties
```
# 🌍 Avoid Using localhost
Edit config/kraft/server.properties:
```bash
listeners=PLAINTEXT://0.0.0.0:9092
advertised.listeners=PLAINTEXT://192.168.1.50:9092
```
## 📌 Topic Management
```bash

(LEGACY ONES YOU HAD TO USE PLAYGROUND FOR CONFIGS- --producer.config playground.config)
# Create topic
kafka-topics.sh --bootstrap-server <host:port> --create --topic my_topic

# Create topic with partitions and replication factor(1 since locally we have 1 broker)
kafka-topics.sh --bootstrap-server localhost:9092 --create --topic second_topic --partitions 3 --replication-factor 1

# List topics
kafka-topics.sh --bootstrap-server <host:port> --list

# Describe topic
kafka-topics.sh --bootstrap-server <host:port> --describe --topic my_topic

# Produce to a topic
kafka-console-producer.sh --bootstrap-server localhost:9092 --topic first_topic

# producing with properties
kafka-console-producer.sh --bootstrap-server localhost:9092 --topic first_topic --producer-property acks=all

# producing to a non existing topic- first takes long and then throws an error...It will create though after some time
# Best practice is disable auto topic creation
kafka-console-producer.sh --bootstrap-server localhost:9092 --topic first_topic1

# produce with keys
kafka-console-producer.sh --bootstrap-server localhost:9092 --topic first_topic --property parse.key=true --property key.separator=:


# Delete topic
kafka-topics.sh --bootstrap-server <host:port> --delete --topic my_topic

#Consume from a topic:
kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic second_topic

#Produce using round robin partitioner:
kafka-console-producer.sh --bootstrap-server localhost:9092 --producer-property partitioner.class=org.apache.kafka.clients.producer.RoundRobinPartitioner --topic second_topic


# Consume from beginning
kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic second_topic --from-beginning

#Format consume in an ordered manner
kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic second_topic --formatter kafka.tools.DefaultMessageFormatter --property print.timestamp=true --property print.key=true --property print.value=true --property print.partition=true --from-beginning


#Consume from a specific group
kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic third_topic --group my-first-application

# list consumer groups
kafka-consumer-groups.sh --bootstrap-server localhost:9092 --list

# describe one specific group
kafka-consumer-groups.sh --bootstrap-server localhost:9092 --describe --group my-second-application

# Dry Run: reset the offsets to the beginning of each partition <CONSUMER MUST NOT BE CONSUMING WHEN YOU ARE RESETTING>
kafka-consumer-groups.sh  --bootstrap-server localhost:9092 --group my-first-application --reset-offsets --to-earliest --topic third_topic --dry-run

# execute flag is needed
kafka-consumer-groups.sh --bootstrap-server localhost:9092 --group my-first-application --reset-offsets --to-earliest --topic third_topic --execute

```
## ✏️ Produce & Consume Messages
```bash
# Produce messages
kafka-console-producer.sh --bootstrap-server <host:port> --topic my_topic

# Consume from beginning
kafka-console-consumer.sh --bootstrap-server <host:port> --topic my_topic --from-beginning

# Consume new messages
kafka-console-consumer.sh --bootstrap-server <host:port> --topic my_topic

```

## 🛠️ Manage Partitions & Configs
```bash
# Increase partitions
kafka-topics.sh --bootstrap-server <host:port> --alter --topic my_topic --partitions 3

# Add/update topic config
kafka-configs.sh --bootstrap-server <host:port> --entity-type topics \
  --entity-name my_topic --alter --add-config retention.ms=86400000

# Describe topic config
kafka-configs.sh --bootstrap-server <host:port> --entity-type topics \
  --entity-name my_topic --describe
```
## 👥 Consumer Groups
```bash
# List groups
kafka-consumer-groups.sh --bootstrap-server <host:port> --list

# Describe group
kafka-consumer-groups.sh --bootstrap-server <host:port> --describe --group my_group

# Delete group
kafka-consumer-groups.sh --bootstrap-server <host:port> --delete --group my_group

```




