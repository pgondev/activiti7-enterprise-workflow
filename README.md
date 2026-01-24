# Activiti7 Enterprise Workflow Platform

[![Build Status](https://img.shields.io/badge/build-passing-brightgreen)](https://github.com/YOUR_ORG/activiti7-enterprise-workflow)
[![License](https://img.shields.io/badge/license-Apache%202.0-blue)](LICENSE)
[![OpenShift](https://img.shields.io/badge/OpenShift-Ready-red)](https://www.redhat.com/en/technologies/cloud-computing/openshift)

Enterprise-grade workflow automation platform based on **Activiti7 Cloud** with feature parity from Camunda, Flowable, Hyland Automate, and Alfresco Process Services.

## 🌟 Features

### Core Engines
- **BPMN 2.0** - Full process modeling and execution (Activiti7)
- **DMN 1.3** - Decision tables and business rules
- **CMMN 1.1** - Case management (Flowable integration)

### User Experience
- **Process Modeler** - Visual designer using [bpmn.io](https://bpmn.io)
- **Form Builder** - Dynamic forms using [form.io](https://form.io)
- **Tasklist** - User task management with filtering and search
- **Admin Console** - Monitoring, deployment, and user management

### Enterprise Features
- **Dashboards & Analytics** - Per-process insights and KPIs
- **Cross-instance Queries** - Analyze form data across all instances
- **Multi-tenancy** - Secure tenant isolation
- **Audit Trail** - Complete event logging

### Integrations
| Category | Connectors |
|----------|------------|
| **Microsoft 365** | Teams, SharePoint, Outlook, OneDrive, Planner |
| **Communication** | Email/SMTP, Slack |
| **Cloud Storage** | AWS S3, Azure Blob, Google Drive |
| **Enterprise** | SAP, Salesforce, REST |

## 📋 Prerequisites

- Java 17+
- Gradle 8.x
- Docker & Docker Compose (or Podman)
- Node.js 20+ (for frontend apps)
- OpenShift CLI (oc) or kubectl

## 🏗️ Project Structure

```
activiti7-enterprise-workflow/
├── libs/                      # Shared libraries
│   ├── common/                # DTOs, utils, exceptions
│   ├── security-common/       # OAuth2/OIDC
│   └── messaging-common/      # RabbitMQ/Kafka
│
├── services/                  # Backend microservices
│   ├── workflow-engine/       # BPMN Engine (Activiti7)
│   ├── task-service/          # User task management
│   ├── form-service/          # Form.io integration
│   ├── decision-engine/       # DMN Engine
│   ├── case-engine/           # CMMN Engine
│   ├── reporting-service/     # Analytics & dashboards
│   └── ...
│
├── apps/                      # Frontend applications
│   ├── modeler-ui/            # Process designer (bpmn-js)
│   ├── tasklist-ui/           # Task interface (form.io)
│   ├── admin-ui/              # Admin console
│   ├── forms-ui/              # Public forms
│   └── reporting-ui/          # Dashboards
│
├── connectors/                # Integration connectors
│   ├── ms365-teams-connector/
│   ├── ms365-sharepoint-connector/
│   ├── ms365-outlook-connector/
│   └── ...
│
├── k8s/                       # Kubernetes manifests
└── openshift/                 # OpenShift configs
```

## 🚀 Quick Start

### 1. Start Infrastructure

```bash
docker-compose up -d
```

Services started:
- PostgreSQL (5432)
- RabbitMQ (5672, UI: 15672)
- Redis (6379)
- Elasticsearch (9200)
- Keycloak (8180)
- MinIO (9000, Console: 9001)

### 2. Build the Project

```bash
./gradlew build
```

### 3. Run Services

```bash
# Start workflow engine
./gradlew :services:workflow-engine:bootRun

# Start task service (another terminal)
./gradlew :services:task-service:bootRun
```

### 4. Build Frontend

```bash
cd apps/modeler-ui
npm install
npm run dev
```


## 🔗 Access Links

### Frontend Applications
| Application | URL | Description | Login (Dev) |
|-------------|-----|-------------|-------------|
| **Modeler UI** | http://localhost:3000 | BPMN/DMN Designer | N/A |
| **Tasklist UI** | http://localhost:3001 | User Task Inbox | N/A |
| **Admin UI** | http://localhost:3002 | Platform Admin | N/A |

### Backend Services
| Service | API URL | Swagger UI | Port |
|---------|---------|------------|------|
| **Workflow Engine** | `/api/v1` | [Swagger UI](http://localhost:8080/swagger-ui.html) | 8080 |
| **Task Service** | `/api/v1` | [Swagger UI](http://localhost:8081/swagger-ui.html) | 8081 |
| **Form Service** | `/api/v1` | [Swagger UI](http://localhost:8082/swagger-ui.html) | 8082 |
| **Decision Engine** | `/api/v1` | [Swagger UI](http://localhost:8083/swagger-ui.html) | 8083 |

### Infrastructure
| Service | URL | Credentials (Dev) |
|---------|-----|-------------------|
| **Keycloak** | http://localhost:8180 | admin / admin123 |
| **RabbitMQ** | http://localhost:15672 | workflow / workflow123 |
| **MinIO** | http://localhost:9001 | workflow / workflow123 |
| **Grafana** | http://localhost:3030 | admin / admin123 |

## 🐳 Container Build

Using JIB (no Docker daemon required):

```bash
# Build all images
./gradlew buildAllImages

# Push to registry
./gradlew pushAllImages
```

## ☸️ Kubernetes Deployment

```bash
# Apply base configs
kubectl apply -f k8s/base/

# Deploy services
kubectl apply -f k8s/services/

# Deploy frontend apps
kubectl apply -f k8s/apps/

# Configure ingress
kubectl apply -f k8s/ingress/
```

## 🔴 OpenShift Deployment

```bash
# Create project
oc new-project workflow

# Apply image streams
oc apply -f openshift/imagestreams.yaml

# Apply build configs
oc apply -f openshift/buildconfigs.yaml

# Apply routes
oc apply -f openshift/routes.yaml

# Start builds
oc start-build workflow-engine-build
```

## 🔧 Configuration

### Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `DB_HOST` | localhost | PostgreSQL host |
| `DB_PORT` | 5432 | PostgreSQL port |
| `DB_NAME` | workflowdb | Database name |
| `RABBITMQ_HOST` | localhost | RabbitMQ host |
| `KEYCLOAK_URL` | http://localhost:8180 | Keycloak URL |
| `KEYCLOAK_REALM` | workflow | Keycloak realm |

### Keycloak Setup

1. Access Keycloak Admin: http://localhost:8180
2. Login: admin / admin123
3. Create realm: `workflow`
4. Create clients for each app
5. Configure users and roles

## 📚 API Documentation

Swagger UI available for each service:

- Workflow Engine: http://localhost:8080/swagger-ui.html
- Task Service: http://localhost:8083/swagger-ui.html
- Form Service: http://localhost:8084/swagger-ui.html
- Reporting Service: http://localhost:8091/swagger-ui.html

## 🧪 Testing

```bash
# Run all tests
./gradlew test

# Run integration tests
./gradlew integrationTest

# Generate coverage report
./gradlew jacocoTestReport
```

## 📊 Architecture

See [ARCHITECTURE.md](ARCHITECTURE.md) for detailed system diagrams.

## 📝 Development Log

See [DEVLOG.md](DEVLOG.md) for development history and time tracking.

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Submit a pull request

## 📄 License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

## 🔗 References

- [Activiti7 Cloud Documentation](https://activiti.gitbook.io/activiti-7-developers-guide/)
- [bpmn.io](https://bpmn.io/) - BPMN modeling toolkit
- [form.io](https://form.io/) - Form building platform
- [Camunda Platform 8 Docs](https://docs.camunda.io/)
- [Flowable Documentation](https://www.flowable.com/open-source/docs/)
