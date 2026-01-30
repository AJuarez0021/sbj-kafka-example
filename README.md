# Spring Boot Kafka Example

Ejemplo completo de productor y consumidor de Apache Kafka con Spring Boot 4.0.

## Requisitos

- Java 17+
- Maven 3.8+
- Apache Kafka (o Docker)

## Estructura del Proyecto

```
src/main/java/com/work/broker/
├── SbjKafkaExampleApplication.java  # Clase principal
├── config/
│   └── KafkaConfig.java             # Configuración del topic
├── model/
│   └── Message.java                 # Modelo de mensaje
├── producer/
│   └── MessageProducer.java         # Servicio productor
├── consumer/
│   └── MessageConsumer.java         # Servicio consumidor
└── controller/
    └── MessageController.java       # API REST
```

## Configuración

El archivo `application.properties` contiene la configuración de Kafka:

| Propiedad | Valor | Descripción |
|-----------|-------|-------------|
| `spring.kafka.bootstrap-servers` | localhost:9092 | Servidor Kafka |
| `spring.kafka.consumer.group-id` | my-group | Grupo de consumidores |
| `app.kafka.topic` | messages-topic | Nombre del topic |

## Iniciar Kafka

### Opción 1: Docker (recomendado)

```bash
docker run -d --name kafka -p 9092:9092 apache/kafka:latest
```

### Opción 2: Docker Compose

Crea un archivo `docker-compose.yml`:

```yaml
version: '3.8'
services:
  kafka:
    image: apache/kafka:latest
    ports:
      - "9092:9092"
    environment:
      KAFKA_NODE_ID: 1
      KAFKA_PROCESS_ROLES: broker,controller
      KAFKA_LISTENERS: PLAINTEXT://0.0.0.0:9092,CONTROLLER://0.0.0.0:9093
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
      KAFKA_CONTROLLER_LISTENER_NAMES: CONTROLLER
      KAFKA_CONTROLLER_QUORUM_VOTERS: 1@localhost:9093
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
```

Ejecuta:

```bash
docker-compose up -d
```

## Ejecutar la Aplicación

```bash
mvn spring-boot:run
```

## API REST

### Enviar mensaje (JSON)

```bash
curl -X POST http://localhost:8080/api/messages \
  -H "Content-Type: application/json" \
  -d '{
    "content": "Hola Kafka!",
    "sender": "Usuario1"
  }'
```

**Respuesta:**

```json
{
  "status": "Message sent",
  "messageId": "550e8400-e29b-41d4-a716-446655440000"
}
```

### Enviar mensaje (Query Params)

```bash
curl -X POST "http://localhost:8080/api/messages/simple?content=Hola&sender=Test"
```

## Componentes

### MessageProducer

Servicio para enviar mensajes a Kafka:

```java
@Service
public class MessageProducer {

    // Envío asíncrono simple
    public void sendMessage(String content, String sender);

    // Envío asíncrono con objeto Message
    public void sendMessage(Message message);

    // Envío síncrono con CompletableFuture
    public CompletableFuture<SendResult<String, Message>> sendMessageSync(Message message);
}
```

### MessageConsumer

Consumidor que escucha el topic configurado:

```java
@Service
public class MessageConsumer {

    @KafkaListener(topics = "${app.kafka.topic}")
    public void consume(Message message, ...);
}
```

## Logs de Ejemplo

**Productor:**

```
INFO  Sending message: Message(id=abc-123, content=Hola, sender=User1, ...)
INFO  Message sent successfully to topic: messages-topic, partition: 0, offset: 5
```

**Consumidor:**

```
INFO  ============================================
INFO  Message received!
INFO  Topic: messages-topic
INFO  Partition: 0
INFO  Offset: 5
INFO  Message ID: abc-123
INFO  Content: Hola
INFO  Sender: User1
INFO  ============================================
```

## Tecnologías

- Spring Boot 4.0.1
- Spring Kafka
- Apache Kafka
- Lombok
- Java 17
