DOCKER_COMPOSE = docker-compose
.PHONY: up down logs clean rebuild

up:
	$(DOCKER_COMPOSE) up -d

down:
	$(DOCKER_COMPOSE) down

logs:
	$(DOCKER_COMPOSE) logs -f --tail=200

clean: down
	docker volume prune -f
	rm -rf file-storage-service/target identity-service/target

rebuild: clean up



DOCKER_COMPOSE = docker-compose
NEXUS_URL = http://localhost:8081

.PHONY: all up start stop clean logs rebuild build-artifacts

all: up build-artifacts start

ifeq ($(OS),Windows_NT)
WAIT_CMD = powershell -Command "while ($$true) { \
		try { \
			Invoke-WebRequest -UseBasicParsing -Uri $(NEXUS_URL)/service/rest/v1/status -ErrorAction Stop; \
			break \
		} \
		catch { \
			Write-Host 'Nexus not ready, sleeping...'; \
			Start-Sleep -Seconds 5 \
		} \
	}"
else
WAIT_CMD = until curl -sf $(NEXUS_URL)/service/rest/v1/status; do \
	echo 'Nexus not ready, sleeping...'; sleep 5; \
done
endif

up:
	$(DOCKER_COMPOSE) up -d nexus
	@echo "Waiting for Nexus to be healthy..."
	@$(WAIT_CMD)
	@echo "Nexus is healthy!"

build-artifacts:
	$(DOCKER_COMPOSE) build api --no-cache
	mvn deploy -pl api -DskipTests

start:
	$(DOCKER_COMPOSE) up -d

stop:
	$(DOCKER_COMPOSE) down

clean: stop
	$(DOCKER_COMPOSE) rm -f
	docker volume rm $$(docker volume ls -qf dangling=true) 2>/dev/null || true
	rm -rf ./api/target

logs:
	$(DOCKER_COMPOSE) logs -f --tail=200

rebuild: clean all