package app.spaulding.kafka.streams;

import java.util.Collections;
import java.util.Map;
import java.util.Properties;

import app.spaulding.cef.avro.CEF;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;

import static app.spaulding.cef.CEFParser.parseToCEF;

/**
 * Demo Kakfa Streams app. Foundation for the other Stream classes.
 *
 * @author joshua.spaulding
 *
 */
public class StreamsStarterApp {

	public static void main(String[] args) {

		Properties config = new Properties();
		config.put(StreamsConfig.APPLICATION_ID_CONFIG, "streams-starter-app");
		config.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
		config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		config.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
		config.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

		StreamsBuilder builder = new StreamsBuilder();

		KStream<String, String> source = builder.stream("cef");
		KStream<String, CEF> kStream = source.mapValues(value -> parseToCEF(value));

		// Override default String serde
		final Map<String, String> serdeConfig = Collections.singletonMap("schema.registry.url",
				"http://localhost:8081");
		final SpecificAvroSerde<CEF> cefSpecificAvroSerde = new SpecificAvroSerde<CEF>();
		cefSpecificAvroSerde.configure(serdeConfig, false);

		kStream.to("kstream-cef-avro", Produced.valueSerde(cefSpecificAvroSerde));

		KafkaStreams streams = new KafkaStreams(builder.build(), config);
		streams.cleanUp(); // only do this in dev - not in prod
		streams.start();

		// print the topology
		System.out.println(streams.localThreadsMetadata().toString());

		// shutdown hook to correctly close the streams application
		Runtime.getRuntime().addShutdownHook(new Thread(streams::close));

	}

}
