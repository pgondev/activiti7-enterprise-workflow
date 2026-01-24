# Architecture Diagram

> **Last Updated**: 2026-01-24 12:56  
> **Version**: 1.1

## System Architecture

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                                   CLIENT LAYER                                        │
├─────────────────────────────────────────────────────────────────────────────────────┤
│  ┌──────────────────┐  ┌──────────────────┐  ┌──────────────────┐  ┌──────────────┐ │
│  │  Process Modeler │  │  Tasklist App    │  │  Admin Console   │  │ Reporting UI │ │
│  │  (React+bpmn.io) │  │  (React+form.io) │  │  (React)         │  │ (React)      │ │
│  └────────┬─────────┘  └────────┬─────────┘  └────────┬─────────┘  └──────┬───────┘ │
│           │                     │                     │                   │          │
│  ┌────────┴─────────────────────┴─────────────────────┴───────────────────┴────────┐ │
│  │                           Public Forms (React + form.io)                         │ │
│  │                              Embeddable on external sites                        │ │
│  └──────────────────────────────────────────────────────────────────────────────────┘ │
└───────────────────────────────────────────┬─────────────────────────────────────────┘
                                            │
┌───────────────────────────────────────────▼─────────────────────────────────────────┐
│                         API GATEWAY (Kong / Spring Cloud Gateway)                    │
│                     Load Balancing • Rate Limiting • Authentication                  │
└───────────────────────────────────────────┬─────────────────────────────────────────┘
                                            │
        ┌───────────────────────────────────┼───────────────────────────────┐
        │                                   │                               │
        ▼                                   ▼                               ▼
┌───────────────────┐            ┌─────────────────────────────┐   ┌───────────────────┐
│  IDENTITY LAYER   │            │      CORE SERVICES          │   │  CACHING LAYER    │
│                   │            │                             │   │                   │
│  ┌─────────────┐  │            │  ┌───────────────────────┐  │   │  ┌─────────────┐  │
│  │  Keycloak   │  │            │  │   WORKFLOW ENGINE     │  │   │  │    Redis    │  │
│  │  (Primary)  │  │            │  │   (BPMN - Activiti7)  │  │   │  │    Cache    │  │
│  │             │  │            │  │   Port: 8080          │  │   │  │             │  │
│  │  • OAuth2   │  │            │  └───────────────────────┘  │   │  │  • Sessions │  │
│  │  • OIDC     │  │            │                             │   │  │  • API Cache│  │
│  │  • SAML     │  │            │  ┌───────────────────────┐  │   │  │  • Tasks    │  │
│  │  • LDAP/AD  │  │            │  │   DECISION ENGINE     │  │   │  │             │  │
│  └─────────────┘  │            │  │   (DMN - Activiti)    │  │   │  └─────────────┘  │
│                   │            │  │   Port: 8085          │  │   │                   │
│  ┌─────────────┐  │            │  └───────────────────────┘  │   └───────────────────┘
│  │    Ping     │  │            │                             │
│  │ Federation  │  │            │  ┌───────────────────────┐  │   ┌───────────────────┐
│  │ (Alternate) │  │            │  │   CASE ENGINE         │  │   │  LOGGING/SEARCH   │
│  │             │  │            │  │   (CMMN - Flowable)   │  │   │                   │
│  │  • SSO      │  │            │  │   Port: 8086          │  │   │  ┌─────────────┐  │
│  │  • MFA      │  │            │  └───────────────────────┘  │   │  │   Splunk    │  │
│  │  • SCIM     │  │            │                             │   │  │  (Primary)  │  │
│  │  • SAML 2.0 │  │            │  ┌───────────────────────┐  │   │  │             │  │
│  └─────────────┘  │            │  │   TASK SERVICE        │  │   │  │  • Logs     │  │
│                   │            │  │   (User Tasks)        │  │   │  │  • Metrics  │  │
└───────────────────┘            │  │   Port: 8083          │  │   │  │  • Search   │  │
                                 │  └───────────────────────┘  │   │  │  • SIEM     │  │
                                 │                             │   │  └─────────────┘  │
                                 │  ┌───────────────────────┐  │   │                   │
                                 │  │   FORM SERVICE        │  │   │  ┌─────────────┐  │
                                 │  │   (form.io)           │  │   │  │ Elasticsearch│  │
                                 │  │   Port: 8084          │  │   │  │ (Alternate) │  │
                                 │  └───────────────────────┘  │   │  │             │  │
                                 │                             │   │  │  • Full-text│  │
                                 │  ┌───────────────────────┐  │   │  │  • Analytics│  │
                                 │  │   QUERY SERVICE       │  │   │  └─────────────┘  │
                                 │  │   (Read Queries)      │  │   │                   │
                                 │  │   Port: 8081          │  │   └───────────────────┘
                                 │  └───────────────────────┘  │
                                 │                             │
                                 │  ┌───────────────────────┐  │
                                 │  │   AUDIT SERVICE       │  │
                                 │  │   (Event Logging)     │  │
                                 │  │   Port: 8082          │  │
                                 │  └───────────────────────┘  │
                                 │                             │
                                 │  ┌───────────────────────┐  │
                                 │  │  REPORTING SERVICE    │  │
                                 │  │  (Dashboards/KPIs)    │  │
                                 │  │   Port: 8091          │  │
                                 │  └───────────────────────┘  │
                                 │                             │
                                 └─────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────────────┐
│                           AI & INTEGRATION LAYER                                      │
├─────────────────────────────────────────────────────────────────────────────────────┤
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐ │
│  │ AI AUTOMATION   │  │ CONTENT SERVICE │  │ NOTIFICATION    │  │ CONNECTOR SVC   │ │
│  │ SERVICE         │  │ (Documents)     │  │ SERVICE         │  │                 │ │
│  │ Port: 8090      │  │ Port: 8087      │  │ Port: 8089      │  │ Port: 8092      │ │
│  │                 │  │                 │  │                 │  │                 │ │
│  │ • IDP           │  │ • Upload/Store  │  │ • Email         │  │ • REST          │ │
│  │ • Smart Routing │  │ • Versioning    │  │ • Push          │  │ • SOAP          │ │
│  │ • ML Models     │  │ • Metadata      │  │ • SMS           │  │ • GraphQL       │ │
│  │ • AI Agents     │  │ • Full-text     │  │ • Webhooks      │  │ • gRPC          │ │
│  └─────────────────┘  └─────────────────┘  └─────────────────┘  └─────────────────┘ │
└─────────────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────────────┐
│                              CONNECTORS LAYER                                         │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                       │
│  ┌───────────────────────────────────────────────────────────────────────────────┐  │
│  │                        MICROSOFT 365 CONNECTORS                                │  │
│  │  ┌─────────┐  ┌─────────┐  ┌─────────┐  ┌─────────┐  ┌─────────┐             │  │
│  │  │ Outlook │  │SharePoint│  │  Teams  │  │OneDrive │  │ Planner │             │  │
│  │  │ Email/  │  │ Sites/  │  │ Chat/   │  │ Files/  │  │ Tasks/  │             │  │
│  │  │ Calendar│  │  Lists  │  │ Channels│  │ Sharing │  │ Projects│             │  │
│  │  └─────────┘  └─────────┘  └─────────┘  └─────────┘  └─────────┘             │  │
│  │                                                                                │  │
│  │                    Using Microsoft Graph API + Azure Identity                  │  │
│  └───────────────────────────────────────────────────────────────────────────────┘  │
│                                                                                       │
│  ┌───────────────────────────────────────────────────────────────────────────────┐  │
│  │                         OTHER CONNECTORS                                       │  │
│  │  ┌─────────┐  ┌─────────┐  ┌─────────┐  ┌─────────┐  ┌─────────┐             │  │
│  │  │  Slack  │  │  Email  │  │   SAP   │  │Salesforce│  │  REST   │             │  │
│  │  └─────────┘  └─────────┘  └─────────┘  └─────────┘  └─────────┘             │  │
│  │                                                                                │  │
│  │  ┌─────────┐  ┌─────────┐  ┌─────────┐                                        │  │
│  │  │ AWS S3  │  │  Azure  │  │ Google  │                                        │  │
│  │  │         │  │  Blob   │  │  Drive  │                                        │  │
│  │  └─────────┘  └─────────┘  └─────────┘                                        │  │
│  └───────────────────────────────────────────────────────────────────────────────┘  │
│                                                                                       │
└─────────────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────────────┐
│                              MESSAGING LAYER                                          │
│                                                                                       │
│   ┌─────────────────────────────────────────────────────────────────────────────┐   │
│   │                    RABBITMQ / KAFKA MESSAGE BROKER                           │   │
│   │                                                                              │   │
│   │   Exchanges/Topics:                                                          │   │
│   │   • engine-events     (Process lifecycle events)                            │   │
│   │   • task-events       (Task lifecycle events)                               │   │
│   │   • audit-events      (Audit trail → Splunk)                                │   │
│   │   • notification-events (Notifications trigger)                             │   │
│   │   • integration-events (External system events)                             │   │
│   │   • connector-events   (Connector results)                                  │   │
│   │                                                                              │   │
│   └─────────────────────────────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────────────┐
│                              DATA LAYER                                               │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                       │
│  ┌─────────────────────────┐        ┌─────────────────────────┐                     │
│  │      POSTGRESQL         │        │        MINIO            │                     │
│  │    Primary Database     │        │    Object Storage       │                     │
│  │                         │        │                         │                     │
│  │  Schemas:               │        │  Buckets:               │                     │
│  │  • activiti (engine)    │        │  • process-attachments  │                     │
│  │  • workflow (custom)    │        │  • task-attachments     │                     │
│  │  • forms (form.io)      │        │  • documents            │                     │
│  │  • audit (events)       │        │  • forms                │                     │
│  │  • reports (analytics)  │        │  • ai-models            │                     │
│  │                         │        │                         │                     │
│  └─────────────────────────┘        └─────────────────────────┘                     │
│                                                                                       │
└─────────────────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────────────────┐
│                       DEPLOYMENT (OpenShift / Kubernetes)                             │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                       │
│  ┌───────────────────────────────────────────────────────────────────────────────┐  │
│  │                           KUBERNETES CLUSTER                                   │  │
│  │                                                                                │  │
│  │  ┌─────────────────────────────────────────────────────────────────────────┐  │  │
│  │  │  Namespace: workflow-prod                                                │  │  │
│  │  │                                                                          │  │  │
│  │  │  Deployments:        Services:           ConfigMaps:    Secrets:        │  │  │
│  │  │  • workflow-engine   • ClusterIP         • app-config   • db-creds      │  │  │
│  │  │  • task-service      • LoadBalancer      • idp-config   • mq-creds      │  │  │
│  │  │  • form-service      • Ingress/Route     • splunk-cfg   • splunk-token  │  │  │
│  │  │  • decision-engine                       • ms365-cfg    • ms365-creds   │  │  │
│  │  │  • case-engine       HPA (Auto-scaling): • ping-config  • ping-creds    │  │  │
│  │  │  • reporting-service • CPU/Memory based                                  │  │  │
│  │  │  • ai-service        • Custom metrics    PVC (Storage):                  │  │  │
│  │  │  • ...               • Queue depth       • postgres-pvc                  │  │  │
│  │  │                                          • minio-pvc                     │  │  │
│  │  └─────────────────────────────────────────────────────────────────────────┘  │  │
│  │                                                                                │  │
│  │  ┌─────────────────────────────────────────────────────────────────────────┐  │  │
│  │  │  OpenShift Additions:                                                    │  │  │
│  │  │  • Routes (TLS termination)                                             │  │  │
│  │  │  • ImageStreams (container registry)                                     │  │  │
│  │  │  • BuildConfigs (S2I, Dockerfile)                                        │  │  │
│  │  │  • ServiceMesh (Istio/OpenShift Service Mesh)                            │  │  │
│  │  │  • Operators (PostgreSQL, RabbitMQ, Keycloak)                            │  │  │
│  │  └─────────────────────────────────────────────────────────────────────────┘  │  │
│  └───────────────────────────────────────────────────────────────────────────────┘  │
│                                                                                       │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

---

## Identity Provider Options

| Provider | Use Case | Features |
|----------|----------|----------|
| **Keycloak** (Default) | Self-hosted, full control | OAuth2, OIDC, SAML, LDAP/AD, User Federation |
| **Ping Federation** (Enterprise) | Enterprise SSO | SAML 2.0, OAuth2, MFA, SCIM provisioning, Policy engine |

### Configuration Toggle

```yaml
# application.yml
identity:
  provider: keycloak  # or 'ping'
  
  keycloak:
    url: https://keycloak.example.com
    realm: workflow
    client-id: workflow-app
    
  ping:
    issuer-url: https://sso.enterprise.com
    client-id: workflow-app
    authorization-endpoint: /as/authorization.oauth2
    token-endpoint: /as/token.oauth2
```

---

## Logging & Observability Options

| Platform | Use Case | Features |
|----------|----------|----------|
| **Splunk** (Primary) | Enterprise SIEM | Log aggregation, search, dashboards, alerts, compliance |
| **Elasticsearch** (Alternate) | Self-hosted search | Full-text search, analytics, Kibana visualization |

### Splunk Integration Architecture

```
┌─────────────┐     ┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│   Services  │────▶│  Audit Svc  │────▶│  RabbitMQ   │────▶│   Splunk    │
│             │     │             │     │ audit-events│     │   HEC API   │
└─────────────┘     └─────────────┘     └─────────────┘     └─────────────┘
       │                                                           │
       │                                                           ▼
       │                                                    ┌─────────────┐
       └───────────────────────────────────────────────────▶│   Splunk    │
                        Direct log forwarding               │  Dashboard  │
                        (Splunk Universal Forwarder)        └─────────────┘
```

### Splunk Event Types

| Event Type | Index | Description |
|------------|-------|-------------|
| `workflow:process:started` | workflow_events | Process instance started |
| `workflow:process:completed` | workflow_events | Process instance completed |
| `workflow:task:created` | workflow_events | User task created |
| `workflow:task:completed` | workflow_events | User task completed |
| `workflow:decision:executed` | workflow_events | DMN decision executed |
| `workflow:connector:invoked` | integration_events | Connector called |
| `workflow:error` | workflow_errors | Error occurred |
| `workflow:audit` | audit_events | Security audit trail |

---

## Data Flow Diagram

```
┌──────────┐     ┌──────────┐     ┌──────────────┐     ┌──────────────┐
│  User    │────▶│  Browser │────▶│  API Gateway │────▶│   Services   │
│          │     │  (React) │     │    (Kong)    │     │              │
└──────────┘     └──────────┘     └──────────────┘     └──────────────┘
                                         │                     │
                      ┌──────────────────┼─────────────────────┤
                      ▼                  ▼                     ▼
               ┌──────────────┐   ┌──────────────┐      ┌──────────────┐
               │   Keycloak   │   │   RabbitMQ   │      │   Splunk     │
               │     or       │   │  (Events)    │─────▶│  (Logging)   │
               │Ping Federation│  └──────────────┘      └──────────────┘
               └──────────────┘          │
                                         ▼
                      ┌──────────────────┼──────────────────┐
                      ▼                  ▼                  ▼
               ┌──────────────┐   ┌──────────────┐   ┌──────────────┐
               │  PostgreSQL  │   │    Redis     │   │    MinIO     │
               │  (Data)      │   │   (Cache)    │   │  (Storage)   │
               └──────────────┘   └──────────────┘   └──────────────┘
```

---

## Module Dependencies

```
                                    ┌─────────────────┐
                                    │   libs/common   │
                                    │   (DTOs, Utils) │
                                    └────────┬────────┘
                                             │
              ┌──────────────────────────────┼──────────────────────────────┐
              │                              │                              │
              ▼                              ▼                              ▼
     ┌─────────────────┐          ┌─────────────────┐          ┌─────────────────┐
     │ libs/security   │          │ libs/messaging  │          │ libs/splunk     │
     │ (keycloak+ping) │          │ (rabbitmq/kafka)│          │ (logging)       │
     └────────┬────────┘          └────────┬────────┘          └────────┬────────┘
              │                            │                            │
              └────────────────────────────┼────────────────────────────┘
                                           │
              ┌───────────────┬────────────┼────────────┬───────────────┐
              ▼               ▼            ▼            ▼               ▼
     ┌─────────────┐  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ ┌─────────────┐
     │  workflow   │  │   task      │ │    form     │ │  decision   │ │   case      │
     │   engine    │  │  service    │ │   service   │ │   engine    │ │   engine    │
     └─────────────┘  └─────────────┘ └─────────────┘ └─────────────┘ └─────────────┘
              │               │            │                │               │
              └───────────────┴────────────┼────────────────┴───────────────┘
                                           │
                        ┌──────────────────┼──────────────────┐
                        ▼                  ▼                  ▼
               ┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐
               │   reporting     │ │    content      │ │  ai-automation  │
               │    service      │ │    service      │ │    service      │
               └─────────────────┘ └─────────────────┘ └─────────────────┘
```

---

## Technology Stack

| Layer | Primary | Alternate | Purpose |
|-------|---------|-----------|---------|
| **Frontend** | React 18, TypeScript | - | UI Applications |
| **BPMN Editor** | bpmn-js (bpmn.io) | - | Process Modeling |
| **Form Builder** | form.io | - | Dynamic Forms |
| **Backend** | Java 17, Spring Boot 3.x | - | Microservices |
| **BPM Engine** | Activiti 7 Cloud | - | BPMN Execution |
| **DMN Engine** | Activiti DMN | - | Decision Tables |
| **CMMN Engine** | Flowable CMMN | - | Case Management |
| **Identity** | **Keycloak** | **Ping Federation** | SSO/OAuth2 |
| **Build** | Gradle 8.x | - | Build Automation |
| **Database** | PostgreSQL 16 | - | Persistence |
| **Cache** | Redis 7 | - | Distributed Cache |
| **Logging/Search** | **Splunk** | Elasticsearch | Log Aggregation |
| **Messaging** | RabbitMQ 3.13 | Kafka | Event Streaming |
| **Storage** | MinIO | S3/Azure Blob | Object Storage |
| **Container** | Podman/Docker | - | Containerization |
| **Orchestration** | OpenShift | Kubernetes | Deployment |
| **CI/CD** | GitHub Actions + OpenShift Pipelines | - | Automation |

---

## Port Assignments

| Service | Port | Description |
|---------|------|-------------|
| workflow-engine | 8080 | BPMN 2.0 execution |
| query-service | 8081 | Read-optimized queries |
| audit-service | 8082 | Event logging |
| task-service | 8083 | User task management |
| form-service | 8084 | Form.io forms |
| decision-engine | 8085 | DMN decisions |
| case-engine | 8086 | CMMN cases |
| content-service | 8087 | Documents |
| notification-service | 8089 | Alerts |
| ai-service | 8090 | AI automation |
| reporting-service | 8091 | Dashboards |
| connector-service | 8092 | Integration orchestration |

---

## Security Architecture

```
┌─────────────────────────────────────────────────────────────────────┐
│                        SECURITY LAYERS                               │
├─────────────────────────────────────────────────────────────────────┤
│                                                                       │
│  ┌─────────────────────────────────────────────────────────────────┐ │
│  │  Layer 1: Edge Security                                         │ │
│  │  • WAF (Web Application Firewall)                               │ │
│  │  • DDoS Protection                                              │ │
│  │  • TLS 1.3 Termination                                          │ │
│  └─────────────────────────────────────────────────────────────────┘ │
│                                                                       │
│  ┌─────────────────────────────────────────────────────────────────┐ │
│  │  Layer 2: Identity & Access                                     │ │
│  │  • Keycloak / Ping Federation                                   │ │
│  │  • OAuth2 + OIDC tokens                                         │ │
│  │  • RBAC (Role-Based Access Control)                             │ │
│  │  • Multi-tenancy isolation                                      │ │
│  └─────────────────────────────────────────────────────────────────┘ │
│                                                                       │
│  ┌─────────────────────────────────────────────────────────────────┐ │
│  │  Layer 3: Service-to-Service                                    │ │
│  │  • mTLS (mutual TLS) via Service Mesh                           │ │
│  │  • Service accounts                                             │ │
│  │  • Network policies                                             │ │
│  └─────────────────────────────────────────────────────────────────┘ │
│                                                                       │
│  ┌─────────────────────────────────────────────────────────────────┐ │
│  │  Layer 4: Data Security                                         │ │
│  │  • Encryption at rest (PostgreSQL TDE)                          │ │
│  │  • Encryption in transit (TLS)                                  │ │
│  │  • Secrets management (Vault/OpenShift Secrets)                 │ │
│  └─────────────────────────────────────────────────────────────────┘ │
│                                                                       │
│  ┌─────────────────────────────────────────────────────────────────┐ │
│  │  Layer 5: Audit & Compliance                                    │ │
│  │  • Splunk SIEM integration                                      │ │
│  │  • Complete audit trail                                         │ │
│  │  • Compliance reporting (SOC2, GDPR)                            │ │
│  └─────────────────────────────────────────────────────────────────┘ │
│                                                                       │
└─────────────────────────────────────────────────────────────────────┘
```
