# Sử dụng Python 3.10 phiên bản nhẹ
FROM python:3.10-slim

# Đặt thư mục làm việc trong container
WORKDIR /app

# Copy file consumer.py vào container
COPY consumer.py /app/consumer.py

# Cài đặt thư viện kafka-python
RUN pip install --no-cache-dir kafka-python

# Chạy consumer.py khi container khởi động
CMD ["python", "consumer.py"]
