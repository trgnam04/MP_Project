from kafka import KafkaConsumer
import json

# Cấu hình Kafka Consumer để kết nối tới broker qua localhost:9092
consumer = KafkaConsumer(
    'clicks-topic', 'buys-topic',  # Đăng ký nhận từ cả hai topic
    bootstrap_servers=['localhost:29092'],
    auto_offset_reset='earliest',  # Đọc từ offset đầu tiên nếu chưa có offset lưu trữ
    group_id='my-consumer-group',  # Tên nhóm consumer, giúp phân chia tải nếu có nhiều consumer
    value_deserializer=lambda m: json.loads(m.decode('utf-8'))  # Giải mã JSON từ bytes
)

print("Consumer started. Waiting for messages...")

try:
    for message in consumer:
        print(f"Received message from {message.topic}: {message.value}")
except KeyboardInterrupt:
    print("Consumer is shutting down...")
finally:
    consumer.close()
