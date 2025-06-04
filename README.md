# 🧪 Kafka Beginner Project

Welcome to the Kafka Beginner Project! This repository provides a simple, beginner-friendly example of how Apache Kafka can be used for messaging between microservices.

---

## 📸 Architecture Overview

![Kafka Microservices Implementation](./implementation.png)

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
