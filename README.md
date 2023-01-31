# Apache Kafka 101 Examples

## Setup Apache Kafka locally

In order to set up locally Apache Kafka, `docker` should be installed on the machine.

[`docker-compose`](/docker/docker-compose.yml) file is provided in the root directory, under docker folder.

By simply running the command `docker-compose up -d`, an instance of zookeeper, kafka broker, and confluent schema-registry are started.

## Working with Kafka broker from the terminal

To be able to communicate with kafka broker from terminal, we need to enter the broker container with the following command:

`docker exec -it broker bash`

### Topics

To work with kafka __topics__ the following commands can be executed:

#### Create

`kafka-topics --bootstrap-server localhost:9092 --topic <topic-name> --create [--partitions <number-of-partitions> --replication-factor <number>]`

#### Update

`kafka-topics --bootstrap-server broker:9092 --topic <topic-name> --alter [--partitions <number-of-partitions> --replication-factor <number>]`

#### List

`kafka-topics --bootstrap-server localhost:9092 --list`

#### Describe

`kafka-topics --bootstrap-server localhost:9092 --describe --topic <topic-name>`

#### Delete

`kafka-topics --bootstrap-server localhost:9092 --delete --topic <topic-name>`
 
### Consumer

We can create a consumer to the kafka broker topic from the terminal with the following command:

`kafka-console-consumer --bootstrap-server localhost:9092 --topic <topic-name> [--from-beginning --property parse.key=true --property key.separator="-"]`

### Producer

To produce messages to the kafka broker from the terminal, there is a producer command line interface:

`kafka-console-producer --bootstrap-server broker:9092 --topic <topic-name> [--property print.key=true --property key.separator=":"]`

`kafka-console-producer --bootstrap-server broker:9092 --topic <topic-name> < topic-input.txt`

## Schema Registry

### Registering a schema

To register a schema, we can use Confluent Schema Registry's REST API like in the following example:

```
POST http://localhost:8081/subjects/<subject-name>/versions
Content-Type: application/json

{
  "schemaType": "PROTOBUF",
  "schema": "syntax = \"proto3\";\npackage org.example.proto;\n\noption java_outer_classname = \"PurchaseProto\";\n\nmessage Purchase {\n  string item = 1;\n  double total_cost = 2;\n  string customer_ide = 3;\n}\n"
}
```

Schema is provided as a _stringified_ version of the actual __.proto__ schema, which is _linearized_ as well. 

Another way to register a schema is to use a Maven plugin provided in both [consumer-app](/consumer-app/pom.xml) and [producer-app](/producer-app/pom.xml) modules.

```
<groupId>io.confluent</groupId>
<artifactId>kafka-schema-registry-maven-plugin</artifactId>
```

The Confluent's Schema Registry Maven plugin provides a goal with name `schema-registry:register`.

### Downloading a schema

Downloading a schema can be done via Confluent Schema Registry's REST API. The HTTP request for downloading a schema is provided in the following example:

```
GET http://localhost:8081/subjects/<subject-name>/versions/latest
```

Schema can be also download via the Confluent's Schema Registry Maven plugin which provides the goal `schema-registry:download`.

## Resources

- https://developer.confluent.io/tutorials/kafka-console-consumer-producer-basics/kafka.html
- https://www.conduktor.io/kafka/kafka-cli-tutorial
- https://developer.confluent.io/learn-kafka/apache-kafka/events/
- https://developer.confluent.io/learn-kafka/schema-registry/key-concepts/