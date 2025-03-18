# Sử dụng Python 3.10 phiên bản nhẹ
FROM python:3.10-slim

# Đặt thư mục làm việc trong container
WORKDIR /app

# Copy file producer.py vào container
COPY producer.py /app/producer.py

# Cài đặt thư viện kafka-python
RUN pip install --no-cache-dir kafka-python

# Chạy producer.py khi container khởi động
CMD ["python", "producer.py"]
