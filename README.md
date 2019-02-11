# Kafka Stream Starter Application
Base skeleton application structure for a Kafka Streams Application.

To build and run application
```bash
➜  ~ ./gradlew build
➜  ~ java -jar build/libs/kafka-streams-starterapp-fat-1.0-SNAPSHOT.jar
```

The starter application simply consumes from an input topic and produces to the output topic.
Additional application logic goes here:
```java
public static void main(String[] args) {

    Properties config = new Properties();
    config.put(StreamsConfig.APPLICATION_ID_CONFIG, "streams-starter-app");
    config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
    config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

    StreamsBuilder builder = new StreamsBuilder();

    KStream<String, String> kStream = builder.stream("streams-input");
    // do stuff
    kStream.to("streams-output");

    KafkaStreams streams = new KafkaStreams(builder.build(), config);
    streams.cleanUp(); // only do this in dev - not in prod
    streams.start();

    // print the topology
    System.out.println(streams.localThreadsMetadata().toString());

    // shutdown hook to correctly close the streams application
    Runtime.getRuntime().addShutdownHook(new Thread(streams::close));

}
```

Test sample application using Confluent CLI:
1. Start Kafka Cluster
```bash
➜  ~ confluent start kafka
This CLI is intended for development only, not for production
https://docs.confluent.io/current/cli/index.html

Using CONFLUENT_CURRENT: /tmp/confluent.SbpXQ9A4
Starting zookeeper
zookeeper is [UP]
Starting kafka
kafka is [UP]
```
2. Start confluent consumer cli
```bash
➜  ~ confluent consume streams-output
This CLI is intended for development only, not for production
https://docs.confluent.io/current/cli/index.html

hello
```
3. Start confluent producer cli
```bash
➜  ~ confluent produce streams-input
This CLI is intended for development only, not for production
https://docs.confluent.io/current/cli/index.html

>hello
>
```
