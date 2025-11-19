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