package Flink;

import Deserializer.JSONValueDeserializationSchema;
import Dto.BuyEvent;
import Dto.ClickEvent;
import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.connector.jdbc.JdbcConnectionOptions;
import org.apache.flink.connector.jdbc.JdbcExecutionOptions;
import org.apache.flink.connector.jdbc.JdbcSink;
import org.apache.flink.connector.kafka.source.KafkaSource;
import org.apache.flink.connector.kafka.source.enumerator.initializer.OffsetsInitializer;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.sql.Timestamp;

public class DataStreamJob {

	private static final String jdbcUrl = "jdbc:postgresql://postgres:5432/postgres";
	private static final String username = "postgres";
	private static final String password = "postgres";
	private static final String kafkaBootstrapServers = "broker:9092";

	public static void main(String[] args) throws Exception {
		// Thiết lập môi trường Flink
		final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

		// Kafka Source cho ClickEvent
		KafkaSource<ClickEvent> clickEventSource = KafkaSource.<ClickEvent>builder()
				.setBootstrapServers(kafkaBootstrapServers)
				.setTopics("clicks-topic")
				.setGroupId("click-event-group")
				.setStartingOffsets(OffsetsInitializer.latest())
				.setValueOnlyDeserializer(new JSONValueDeserializationSchema<>(ClickEvent.class))
				.build();

		// Kafka Source cho BuyEvent
		KafkaSource<BuyEvent> buyEventSource = KafkaSource.<BuyEvent>builder()
				.setBootstrapServers(kafkaBootstrapServers)
				.setTopics("buys-topic")
				.setGroupId("buy-event-group") // Sử dụng groupId khác để tránh xung đột
				.setStartingOffsets(OffsetsInitializer.latest())
				.setValueOnlyDeserializer(new JSONValueDeserializationSchema<>(BuyEvent.class))
				.build();

		// Tạo DataStream từ Kafka Source
		DataStream<ClickEvent> clickEventDataStream = env.fromSource(
				clickEventSource, WatermarkStrategy.noWatermarks(), "Click Event Source");
		DataStream<BuyEvent> buyEventDataStream = env.fromSource(
				buyEventSource, WatermarkStrategy.noWatermarks(), "Buy Event Source");

		// In dữ liệu ra console để kiểm tra
		clickEventDataStream.print("ClickEvent");
		buyEventDataStream.print("BuyEvent");

		// Cấu hình JDBC Sink
		JdbcExecutionOptions execOptions = new JdbcExecutionOptions.Builder()
				.withBatchSize(1000)
				.withBatchIntervalMs(200)
				.withMaxRetries(5)
				.build();

		JdbcConnectionOptions connOptions = new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
				.withUrl(jdbcUrl)
				.withDriverName("org.postgresql.Driver")
				.withUsername(username)
				.withPassword(password)
				.build();

		// Sink cho BuyEvent
		buyEventDataStream.addSink(JdbcSink.sink(
				"INSERT INTO buy_events (userId, itemId, price, quantity, timestamp) " +
						"VALUES (?, ?, ?, ?, ?) " +
						"ON CONFLICT (userId, itemId) DO UPDATE SET " +
						"price = EXCLUDED.price, " +
						"quantity = EXCLUDED.quantity, timestamp = EXCLUDED.timestamp",
				(preparedStatement, buyEvent) -> {
					preparedStatement.setInt(1, buyEvent.getUserId());
					preparedStatement.setInt(2, buyEvent.getItemId());
					preparedStatement.setDouble(3, buyEvent.getPrice());
					preparedStatement.setInt(4, buyEvent.getQuantity());
					preparedStatement.setTimestamp(5, Timestamp.from(buyEvent.getTimestamp())); // Chuyển Instant -> Timestamp
				},
				execOptions,
				connOptions
		)).name("BuyEvent JDBC Sink");

		// Sink cho ClickEvent
		clickEventDataStream.addSink(JdbcSink.sink(
				"INSERT INTO click_events (userId, itemId, timestamp) " +
						"VALUES (?, ?, ?) " +
						"ON CONFLICT (userId, itemId) DO NOTHING",
				(preparedStatement, clickEvent) -> {
					preparedStatement.setInt(1, clickEvent.getUserId());
					preparedStatement.setInt(2, clickEvent.getItemId());
					preparedStatement.setTimestamp(3, Timestamp.from(clickEvent.getTimestamp())); // Đảm bảo timestamp đúng format
				},
				execOptions,
				connOptions
		)).name("ClickEvent JDBC Sink");


		// Chạy job
		env.execute("Flink DataStream Job");
	}
}