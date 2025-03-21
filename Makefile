KAFKA_COMPOSE_FILE := docker-compose.kafka.yaml
DATABASE_COMPOSE_FILE := docker-compose.database.yaml
PYTHON := python3

up-kafka:
	docker compose -f $(KAFKA_COMPOSE_FILE) up -d 

up-database:
	docker compose -f $(DATABASE_COMPOSE_FILE) up -d --build

down-kafka:
	docker compose -f $(KAFKA_COMPOSE_FILE) down --remove-orphans

down-database:
	docker compose -f $(DATABASE_COMPOSE_FILE) down --remove-orphans

down-all: down-kafka down-database

up-all: up-kafka up-database

restart-kafka: down-kafka up-kafka

logs-kafka:
	docker compose -f $(KAFKA_COMPOSE_FILE) logs -f

clean:
	docker compose -f $(KAFKA_COMPOSE_FILE) down -v
	docker compose -f $(DATABASE_COMPOSE_FILE) down -v

stop:
	docker ps -q | xargs docker stop
	docker ps -aq | xargs docker rm
	docker network prune -f


activate:
	cd Src/Producer/ && . env/bin/activate

producer:
	cd Src/Producer/ && . env/bin/activate && python3 producer.py
consumer:
	cd Src/Consumer/ && . env/bin/activate && python3 consumer.py