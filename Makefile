.PHONY: help build up down restart logs clean test shell-app shell-db ps

# Kolory dla outputu
GREEN  := $(shell tput -Txterm setaf 2)
YELLOW := $(shell tput -Txterm setaf 3)
RESET  := $(shell tput -Txterm sgr0)

help: ## Pokaż pomoc
	@echo "${GREEN}Dostępne komendy:${RESET}"
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | awk 'BEGIN {FS = ":.*?## "}; {printf "  ${YELLOW}%-15s${RESET} %s\n", $$1, $$2}'

build: ## Zbuduj obrazy Docker
	@echo "${GREEN}Budowanie obrazów Docker...${RESET}"
	docker-compose build

up: ## Uruchom aplikację w tle
	@echo "${GREEN}Uruchamianie aplikacji...${RESET}"
	docker-compose up -d
	@echo "${GREEN}Aplikacja uruchomiona!${RESET}"
	@echo "Frontend: http://localhost:3000"
	@echo "Backend: http://localhost:8080"
	@echo "SWAGGER: http://localhost:8080"
	@echo "RabbitMQ Management: http://localhost:15672"

up-build: ## Zbuduj i uruchom aplikację
	@echo "${GREEN}Budowanie i uruchamianie aplikacji...${RESET}"
	docker-compose up -d --build
	@echo "${GREEN}Aplikacja uruchomiona!${RESET}"
	@echo "Aplikacja: http://localhost:8080"
	@echo "RabbitMQ Management: http://localhost:15672"

down: ## Zatrzymaj aplikację
	@echo "${YELLOW}Zatrzymywanie aplikacji...${RESET}"
	docker-compose down

restart: down up ## Restart aplikacji

logs: ## Pokaż logi wszystkich serwisów
	docker-compose logs -f

logs-app: ## Pokaż logi aplikacji
	docker-compose logs -f app

logs-db: ## Pokaż logi bazy danych
	docker-compose logs -f postgres

logs-rabbitmq: ## Pokaż logi RabbitMQ
	docker-compose logs -f rabbitmq

ps: ## Pokaż status kontenerów
	docker-compose ps

clean: ## Zatrzymaj i usuń kontenery, sieci, volumeny
	@echo "${YELLOW}Czyszczenie środowiska Docker...${RESET}"
	docker-compose down -v --remove-orphans
	@echo "${GREEN}Środowisko wyczyszczone!${RESET}"

clean-all: clean ## Usuń również obrazy
	@echo "${YELLOW}Usuwanie obrazów Docker...${RESET}"
	docker-compose down -v --rmi all --remove-orphans
	@echo "${GREEN}Wszystko wyczyszczone!${RESET}"

shell-app: ## Otwórz shell w kontenerze aplikacji
	docker-compose exec app sh

shell-db: ## Otwórz PostgreSQL shell
	docker-compose exec postgres psql -U elearning_user -d elearning_db

test: ## Uruchom testy (lokalnie z Maven)
	@echo "${GREEN}Uruchamianie testów...${RESET}"
	./mvnw test

package: ## Zbuduj pakiet JAR (lokalnie z Maven)
	@echo "${GREEN}Budowanie pakietu JAR...${RESET}"
	./mvnw clean package -DskipTests

dev: ## Uruchom aplikację w trybie deweloperskim (z logami)
	@echo "${GREEN}Uruchamianie w trybie deweloperskim...${RESET}"
	docker-compose up --build

status: ps ## Alias dla ps

# Domyślna komenda
.DEFAULT_GOAL := help
