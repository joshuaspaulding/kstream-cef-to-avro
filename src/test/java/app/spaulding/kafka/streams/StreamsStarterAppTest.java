package app.spaulding.kafka.streams;

import static org.junit.Assert.*;

import java.util.Properties;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyTestDriver;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.test.ConsumerRecordFactory;
import org.apache.kafka.streams.test.OutputVerifier;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class StreamsStarterAppTest {

  private TopologyTestDriver testDriver;
  private ConsumerRecordFactory<String, String> recordFactory = new ConsumerRecordFactory<>(
      new StringSerializer(), new StringSerializer()
  );

  @Before
  public void setUp() {

    Properties config = new Properties();
    config.setProperty(StreamsConfig.APPLICATION_ID_CONFIG, "streams-starter-app");
    config.setProperty(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dummy:1234");
    config.setProperty(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG,
        Serdes.String().getClass().getName());
    config.setProperty(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG,
        Serdes.String().getClass().getName());

    StreamsBuilder builder = new StreamsBuilder();

    KStream<String, String> kStream = builder.stream("streams-input");
    // do stuff
    kStream.to("streams-output");

    testDriver = new TopologyTestDriver(builder.build(), config);
  }

  @After
  public void tearDown() {
    testDriver.close();
  }

  @Test
  public void shouldFlushStoreForFirstInput() {
    testDriver.pipeInput(recordFactory.create("streams-input", null, "hello"));
    OutputVerifier.compareKeyValue(testDriver.readOutput("streams-output",
        new StringDeserializer(),
        new StringDeserializer()),
        null,
        "hello");
    Assert.assertNull(testDriver.readOutput("streams-output"));
  }
}