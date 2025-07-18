# REST Assured API Automation Framework Makefile

# Default environment
ENV ?= development

# Default target
.DEFAULT_GOAL := help

# Colors for output
RED=\033[0;31m
GREEN=\033[0;32m
YELLOW=\033[1;33m
BLUE=\033[0;34m
NC=\033[0m # No Color

.PHONY: help clean compile test test-dev test-staging test-prod smoke regression report serve-report install setup

help: ## Show this help message
	@echo "$(BLUE)REST Assured API Automation Framework$(NC)"
	@echo "$(YELLOW)Available commands:$(NC)"
	@awk 'BEGIN {FS = ":.*?## "} /^[a-zA-Z_-]+:.*?## / {printf "  $(GREEN)%-15s$(NC) %s\n", $$1, $$2}' $(MAKEFILE_LIST)

clean: ## Clean the project
	@echo "$(YELLOW)Cleaning project...$(NC)"
	mvn clean

compile: ## Compile the project
	@echo "$(YELLOW)Compiling project...$(NC)"
	mvn compile test-compile

install: ## Install dependencies
	@echo "$(YELLOW)Installing dependencies...$(NC)"
	mvn clean install -DskipTests

setup: install ## Setup the project (alias for install)
	@echo "$(GREEN)Project setup completed!$(NC)"

test: ## Run all tests (default environment: development)
	@echo "$(YELLOW)Running all tests in $(ENV) environment...$(NC)"
	mvn clean test -P$(ENV)

test-dev: ## Run tests in development environment
	@echo "$(YELLOW)Running tests in development environment...$(NC)"
	mvn clean test -Pdevelopment

test-staging: ## Run tests in staging environment
	@echo "$(YELLOW)Running tests in staging environment...$(NC)"
	mvn clean test -Pstaging

test-prod: ## Run tests in production environment
	@echo "$(YELLOW)Running tests in production environment...$(NC)"
	mvn clean test -Pproduction

smoke: ## Run smoke tests
	@echo "$(YELLOW)Running smoke tests...$(NC)"
	mvn clean test -Dgroups=smoke

regression: ## Run regression tests
	@echo "$(YELLOW)Running regression tests...$(NC)"
	mvn clean test -Dgroups=regression,critical

test-get: ## Run GET operation tests only
	@echo "$(YELLOW)Running GET tests...$(NC)"
	mvn clean test -Dtest=UserGetTests

test-post: ## Run POST operation tests only
	@echo "$(YELLOW)Running POST tests...$(NC)"
	mvn clean test -Dtest=UserPostTests

test-patch: ## Run PATCH operation tests only
	@echo "$(YELLOW)Running PATCH tests...$(NC)"
	mvn clean test -Dtest=UserPatchTests

test-delete: ## Run DELETE operation tests only
	@echo "$(YELLOW)Running DELETE tests...$(NC)"
	mvn clean test -Dtest=UserDeleteTests

test-parallel: ## Run tests in parallel
	@echo "$(YELLOW)Running tests in parallel...$(NC)"
	mvn clean test -DthreadCount=5

report: ## Generate Allure report
	@echo "$(YELLOW)Generating Allure report...$(NC)"
	mvn allure:report
	@echo "$(GREEN)Report generated at target/site/allure-maven-plugin/$(NC)"

serve-report: ## Generate and serve Allure report
	@echo "$(YELLOW)Generating and serving Allure report...$(NC)"
	mvn allure:serve

logs: ## View recent logs
	@echo "$(YELLOW)Recent application logs:$(NC)"
	@if [ -f target/logs/api-automation.log ]; then \
		tail -f target/logs/api-automation.log; \
	else \
		echo "$(RED)No log file found. Run tests first.$(NC)"; \
	fi

logs-rest: ## View REST Assured logs
	@echo "$(YELLOW)Recent REST Assured logs:$(NC)"
	@if [ -f target/logs/rest-assured.log ]; then \
		tail -f target/logs/rest-assured.log; \
	else \
		echo "$(RED)No REST Assured log file found. Run tests first.$(NC)"; \
	fi

check-env: ## Check if environment files exist
	@echo "$(YELLOW)Checking environment configurations...$(NC)"
	@for env in development staging production; do \
		if [ -f src/test/resources/environments/$$env.properties ]; then \
			echo "$(GREEN)✓ $$env.properties exists$(NC)"; \
		else \
			echo "$(RED)✗ $$env.properties missing$(NC)"; \
		fi; \
	done

validate: ## Validate project structure
	@echo "$(YELLOW)Validating project structure...$(NC)"
	@make check-env
	@if [ -f pom.xml ]; then \
		echo "$(GREEN)✓ pom.xml exists$(NC)"; \
	else \
		echo "$(RED)✗ pom.xml missing$(NC)"; \
	fi
	@if [ -f src/test/resources/testng.xml ]; then \
		echo "$(GREEN)✓ testng.xml exists$(NC)"; \
	else \
		echo "$(RED)✗ testng.xml missing$(NC)"; \
	fi

info: ## Show project information
	@echo "$(BLUE)REST Assured API Automation Framework$(NC)"
	@echo "$(YELLOW)Project Information:$(NC)"
	@echo "  Framework: REST Assured"
	@echo "  Test Runner: TestNG"
	@echo "  Build Tool: Maven"
	@echo "  Reporting: Allure"
	@echo "  Java Version: 11+"
	@echo ""
	@echo "$(YELLOW)Supported Environments:$(NC)"
	@echo "  - development (default)"
	@echo "  - staging"
	@echo "  - production"
	@echo ""
	@echo "$(YELLOW)HTTP Methods Covered:$(NC)"
	@echo "  - GET"
	@echo "  - POST"
	@echo "  - PATCH"
	@echo "  - DELETE"

docker-build: ## Build Docker image (if Dockerfile exists)
	@if [ -f Dockerfile ]; then \
		echo "$(YELLOW)Building Docker image...$(NC)"; \
		docker build -t rest-assured-automation .; \
	else \
		echo "$(RED)Dockerfile not found$(NC)"; \
	fi

docker-run: ## Run tests in Docker container
	@if [ -f Dockerfile ]; then \
		echo "$(YELLOW)Running tests in Docker container...$(NC)"; \
		docker run --rm rest-assured-automation; \
	else \
		echo "$(RED)Dockerfile not found$(NC)"; \
	fi

# Environment-specific shortcuts
dev: test-dev ## Shortcut for development tests
staging: test-staging ## Shortcut for staging tests
prod: test-prod ## Shortcut for production tests

# Quick test combinations
quick: smoke ## Quick smoke test run
full: regression ## Full regression test run

# Development helpers
deps: install ## Install dependencies (alias)
build: compile ## Build project (alias)
run: test ## Run tests (alias)