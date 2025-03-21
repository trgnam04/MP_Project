import time
import json
import random
from kafka import KafkaProducer

# Cấu hình Kafka Producer để kết nối tới broker qua localhost:9092
producer = KafkaProducer(
    bootstrap_servers=['localhost:29092'],
    value_serializer=lambda v: json.dumps(v).encode('utf-8')  # Chuyển dict thành JSON bytes
)

topics = ['clicks-topic', 'buys-topic']

def generate_click_event():
    return {
        'event': 'click',
        'user_id': random.randint(1, 1000),
        'item_id': random.randint(100, 200),
        'timestamp': time.strftime("%Y-%m-%dT%H:%M:%SZ", time.gmtime())
    }

def generate_buy_event():
    return {
        'event': 'buy',
        'user_id': random.randint(1, 1000),
        'item_id': random.randint(100, 200),
        'price': round(random.uniform(10, 100), 2),
        'quantity': random.randint(1, 5),
        'timestamp': time.strftime("%Y-%m-%dT%H:%M:%SZ", time.gmtime())
    }

print("Producer started. Sending messages...")

def main():
    while True:
        # Chọn ngẫu nhiên một trong hai topic và tạo event tương ứng
        topic = random.choice(topics)
        data = generate_click_event() if topic == 'clicks-topic' else generate_buy_event()
        
        # Gửi message tới topic đã chọn
        producer.send(topic, value=data)
        print(f"Sent to {topic}: {data}")
        
        # Tạo độ trễ ngẫu nhiên giữa các lần gửi
        time.sleep(random.uniform(0.5, 2))
        
if __name__ == "__main__":
    main()
