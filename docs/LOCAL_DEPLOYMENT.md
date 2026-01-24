# Local Development & Deployment Guide

This guide covers deploying the Activiti7 Enterprise Workflow Platform locally using Docker and Kubernetes.

## Prerequisites

### Required Software (All Open Source)

| Software | Version | Purpose |
|----------|---------|---------|
| Docker Desktop | 4.x+ | Container runtime with Kubernetes |
| Java | 17+ | Backend development |
| Node.js | 20+ | Frontend development |
| Gradle | 8.x | Build tool (uses wrapper) |
| Git | 2.x+ | Version control |

### Enable Kubernetes in Docker Desktop

1. Open Docker Desktop
2. Go to **Settings** → **Kubernetes**
3. Check **Enable Kubernetes**
4. Click **Apply & Restart**
5. Wait for Kubernetes to start (green icon)

Verify installation:
```bash
kubectl version --client
kubectl cluster-info
```

---

## Quick Start (Docker Compose)

### 1. Start Infrastructure

```bash
# Navigate to project directory
cd activiti7-enterprise-workflow

# Start all infrastructure services
docker-compose up -d

# Check status
docker-compose ps
```

### 2. Verify Services

| Service | URL | Credentials |
|---------|-----|-------------|
| **PostgreSQL** | localhost:5432 | workflow / workflow123 |
| **RabbitMQ** | http://localhost:15672 | workflow / workflow123 |
| **Redis** | localhost:6379 | - |
| **OpenSearch** | http://localhost:9200 | - |
| **OpenSearch Dashboards** | http://localhost:5601 | - |
| **Keycloak** | http://localhost:8180 | admin / admin123 |
| **MinIO Console** | http://localhost:9001 | workflow / workflow123 |
| **Prometheus** | http://localhost:9090 | - |
| **Grafana** | http://localhost:3000 | admin / admin123 |

### 3. Build Backend Services

```bash
# Build all services
./gradlew build

# Or on Windows
gradlew.bat build
```

### 4. Run a Service

```bash
# Terminal 1: Workflow Engine
./gradlew :services:workflow-engine:bootRun

# Terminal 2: Task Service
./gradlew :services:task-service:bootRun

# Terminal 3: Form Service
./gradlew :services:form-service:bootRun
```

### 5. Build & Run Frontend

```bash
# Modeler UI
cd apps/modeler-ui
npm install
npm run dev
# Access at http://localhost:5173

# Tasklist UI (new terminal)
cd apps/tasklist-ui
npm install
npm run dev
# Access at http://localhost:5174
```

---

## Kubernetes Local Deployment

### 1. Create Namespace

```bash
kubectl create namespace workflow
kubectl config set-context --current --namespace=workflow
```

### 2. Deploy Infrastructure

```bash
# Apply base configurations
kubectl apply -f k8s/base/namespace.yaml

# Deploy PostgreSQL
kubectl apply -f k8s/base/postgresql.yaml

# Wait for PostgreSQL to be ready
kubectl wait --for=condition=ready pod -l app=postgresql --timeout=120s
```

### 3. Build Container Images

```bash
# Build images locally (uses JIB)
./gradlew jibDockerBuild

# Verify images
docker images | grep workflow
```

### 4. Deploy Services

```bash
# Deploy backend services
kubectl apply -f k8s/services/

# Deploy frontend apps
kubectl apply -f k8s/apps/

# Check deployment status
kubectl get pods -w
```

### 5. Access Services

For local Kubernetes, use port-forwarding:

```bash
# Workflow Engine API
kubectl port-forward svc/workflow-engine 8080:8080

# Task Service API
kubectl port-forward svc/task-service 8083:8080

# Modeler UI
kubectl port-forward svc/modeler-ui 3001:80

# Tasklist UI
kubectl port-forward svc/tasklist-ui 3002:80
```

---

## Test Users

Pre-configured in Keycloak:

| Username | Password | Roles |
|----------|----------|-------|
| admin | admin123 | Admin, Modeler, Analyst, User |
| modeler | modeler123 | Modeler, User |
| user | user123 | User |
| analyst | analyst123 | Analyst, User |

---

## Development Workflow

### 1. Start Infrastructure Only

```bash
docker-compose up -d postgres rabbitmq redis keycloak
```

### 2. Run Services in IDE

Import the Gradle project into IntelliJ IDEA or VS Code, then run:
- `WorkflowEngineApplication`
- `TaskServiceApplication`
- etc.

### 3. Hot Reload Frontend

```bash
cd apps/modeler-ui
npm run dev  # Vite hot reload enabled
```

---

## Troubleshooting

### Docker Issues

```bash
# View logs
docker-compose logs -f [service-name]

# Restart a service
docker-compose restart [service-name]

# Reset everything
docker-compose down -v
docker-compose up -d
```

### Kubernetes Issues

```bash
# Check pod status
kubectl get pods

# View pod logs
kubectl logs -f [pod-name]

# Describe pod for events
kubectl describe pod [pod-name]

# Restart deployment
kubectl rollout restart deployment [deployment-name]
```

### Database Reset

```bash
# Stop services
docker-compose stop

# Remove PostgreSQL volume
docker volume rm activiti7-enterprise-workflow_postgres_data

# Restart
docker-compose up -d postgres
```

---

## Resource Requirements

### Minimum (Development)
- CPU: 4 cores
- RAM: 8 GB
- Disk: 20 GB

### Recommended
- CPU: 8 cores
- RAM: 16 GB
- Disk: 50 GB

---

## Service URLs Summary

### Development (Docker Compose + Local Services)

| Component | URL |
|-----------|-----|
| Workflow Engine API | http://localhost:8080/swagger-ui.html |
| Task Service API | http://localhost:8083/swagger-ui.html |
| Form Service API | http://localhost:8084/swagger-ui.html |
| Modeler UI | http://localhost:5173 |
| Tasklist UI | http://localhost:5174 |
| Admin UI | http://localhost:5175 |
| Reporting UI | http://localhost:5176 |
| Keycloak | http://localhost:8180 |
| RabbitMQ UI | http://localhost:15672 |
| Grafana | http://localhost:3000 |
| Prometheus | http://localhost:9090 |
| OpenSearch Dashboards | http://localhost:5601 |
| MinIO Console | http://localhost:9001 |
