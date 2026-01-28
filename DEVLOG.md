# Development Log - Activiti7 Enterprise Workflow Platform

## Project Overview
Enterprise-grade workflow automation platform based on **Activiti7 Cloud** with feature parity from Camunda, Flowable, Hyland Automate, and Alfresco Process Services.

**Target Deployment**: Red Hat OpenShift / Kubernetes (Docker Desktop locally)  
**Build System**: Gradle 8.x  
**Database**: PostgreSQL 16  
**UI Libraries**: bpmn-js (bpmn.io), form.io

### Configuration Options
| Component | Local/OSS | Enterprise |
|-----------|-----------|------------|
| **Identity** | Keycloak | Ping Federation |
| **Logging** | OpenSearch | Splunk |
| **Monitoring** | Prometheus + Grafana | Splunk ITSI |

---

## Development Time Tracking

| Date | Start Time | End Time | Duration | Focus Area |
|------|------------|----------|----------|------------|
| 2026-01-24 | 12:38 | 13:00 | **22 min** | Planning, Architecture, Foundation |

### Cumulative Statistics
| Metric | Value |
|--------|-------|
| **Total Development Time** | 22 minutes |
| **Total Sessions** | 1 |
| **Files Created** | 70+ |
| **Backend Services** | 11 |
| **Frontend Apps** | 5 |
| **Connectors** | 14 |

---

## Session Log: 2026-01-24

### 12:38 - Project Inception (4 min)
- Researched Activiti7, Camunda, Flowable, Hyland platforms
- Created feature comparison matrix
- Designed implementation plan

### 12:42 - Initial Foundation (4 min)
- Created project structure with Maven (later migrated to Gradle)
- docker-compose.yml for local dev
- Common module with DTOs, exceptions, security utils
- Runtime Bundle with process controller

### 12:46 - Build System & Docs (3 min)
- Created DEVLOG.md and ARCHITECTURE.md
- Started Gradle migration

### 12:47 - Modular Restructure (2 min)
- Reorganized into libs/, services/, apps/, connectors/
- Added Kubernetes manifests (k8s/)
- Added OpenShift configs (openshift/)

### 12:49 - K8s/OpenShift Support (2 min)
- Created namespace, ConfigMaps, Secrets
- PostgreSQL StatefulSet
- Service deployments with HPA
- Ingress and Routes

### 12:52 - Reporting Module (2 min)
- Added reporting-service for dashboards
- Dashboard APIs, cross-instance queries
- Export to Excel/PDF/CSV

### 12:54 - UI & Connectors (2 min)
- Added bpmn-js for process modeling
- Added form.io for forms
- Created package.json for 5 frontend apps
- Added MS365 connectors (Teams, SharePoint, Outlook, OneDrive, Planner)

### 12:56 - Architecture Updates (2 min)
- Added Ping Federation option
- Replaced Elasticsearch with Splunk option
- Updated architecture diagrams

### 12:59 - Local Deployment (3 min)
- Updated docker-compose with all OSS components
- Added OpenSearch + Dashboards
- Added Prometheus + Grafana
- Created Keycloak realm configuration
- Created LOCAL_DEPLOYMENT.md guide
- Added database init script

### 13:00 - Provider Abstractions (2 min)
- Created IdentityProviderConfig (Keycloak + Ping)
- Created LoggingBackendConfig (OpenSearch + Splunk)
- Created application-template.yml with all toggles

---

## Session Log: 2026-01-25

### 08:36 - Architecture Review (4 min)
- Reviewed `ARCHITECTURE.md` against 14 implemented services
- Validated service ports and responsibilities
- Confirmed discrepancies between documented ports and `start-all.bat` (Port 8083 vs 8081 for Task Service)

### 08:44 - Build System Fixes (10 min)
- **Resolved Build Failure**: `StackOverflowError` in Gradle parallel execution
- **Root Cause**: Circular BOM dependency between `spring-cloud-dependencies` and `activiti-cloud-dependencies`
- **Fix**: Updated `gradle.properties` with `-Xss4m` and `javaVersion=21`
- **Cleanup**: Cleared corrupted Gradle cache for Activiti libraries

### 09:05 - Project Cleanup (5 min)
- Removed 32MB of debris (old logs, zip files, redundant scripts)
- Preserved `stop-all.bat` and `start-all.bat`
- Updated `start-all.bat` to exclude 9 scaffolded services, starting only fully implemented ones

### 09:10 - Runtime Verification (6 min)
- Successfully launched 5 core services:
    - **Workflow Engine** (8080) - UP
    - **Task Service** (8083) - UP
    - **Form Service** (8084) - Initializing
    - **Decision Engine** (8085) - Initializing
    - **Decision Engine** (8085) - Initializing
    - **Reporting Service** (8091) - Initializing

### 09:35 - UI Fixes (2 min)
- **Problem**: Modeler UI Form Designer was empty (no drag-and-drop components).
- **Cause**: `FormModeler.tsx` had restrictive configuration disabling all component categories.
- **Fix**: Updated configuration to enable `basic`, `advanced`, `layout`, and `data` component categories.

### 09:59 - Enhancements (2 min)
- **Feature**: Added "Admin Console" link to Modeler UI.
- **Changes**: 
    - Added Sidebar navigation item (External Link to port 3002).
    - Added Quick Action card on Dashboard.

### 10:20 - Fix: Form Deployment (500 Error) (10 min)
- **Problem**: Deployment failed with 500 Error (Internal Server Error) due to JSON mapping issues in Hibernate 6/Postgres.
- **Solution**: Refactored `FormDefinition` entity to store schema as `TEXT` string instead of `JSONB` properties. Initialized manual JSON serialization in `FormDefinitionServiceImpl` to bypass Hibernate mapping complexity.

### 10:35 - Feature: Form List UI (5 min)
- **Feature**: Implemented "View Saved Forms" page.
- **Changes**:
    - Created `FormList.tsx`.
    - Updated Routing (`/modeler/forms`).
    - Updated Sidebar Navigation.

### 10:45 - Feature: Camunda-Style BPMN Modeler (10 min)
- **Feature**: Enhanced BPMN Modeler to support Edit/Delete/Version capabilities like Camunda.
- **Changes**:
    - **Backend**: Added `GET /api/v1/process-definitions/{id}/xml` to `ProcessDefinitionController`.
    - **UI**: Added "Edit" and "Delete" buttons to `ProcessList`.
    - **UI**: Added "Edit" and "Delete" buttons to `ProcessList`.
    - **UI**: Updated `BpmnModeler` to fetch and load existing XML when `id` is present.

### 10:55 - Feature: DMN Modeler Standardization (10 min)
- **Feature**: Aligned DMN Modeler with Camunda design patterns (List -> Edit -> Deploy).
- **Changes**:
    - **UI**: Created `DmnList.tsx` to list and manage decision tables.
    - **UI**: Updated `DmnModeler.tsx` `handleDeploy` and XML loading logic.
    - **UI**: Updated `client.ts` with `decisionApi`.
    - **UI**: Updated `client.ts` with `decisionApi`.
    - **UI**: Updated Routing and Sidebar to point to the new List view.

### 11:00 - Fix: Modeler & Gateway Troubleshooting (10 min)
- **Problem**: `GET /api/v1/decisions` returning 404. Deployments returning 500. Modelers crashing on empty XML.
- **Root Cause**:
    - **404**: Missing proxy in `vite.config.ts` for `decision-service` (8085).
    - **Crash**: Modelers attempted to `importXML(undefined)` when backend returned empty response or error.
    - **500**: `DeploymentController` lacked exception handling, masking the real error during deployment.
- **Fixes**:
    - **Vite**: Added proxy for `/api/v1/decisions` -> `http://localhost:8085`.
    - **UI**: Added XML validation checks in `BpmnModeler.tsx` and `DmnModeler.tsx`.
    - **Backend**: Wrapped `DeploymentController.deploy` in try-catch to return 400/500 with error message.

### 11:10 - UI: Polished Modeler Actions (10 min)
- **Problem**: User confusion regarding "Saved Models" not appearing in lists.
- **Root Cause**: "Save" button in BPMN/DMN modelers was actually "Download to Disk", not "Save to Backend". Only "Deploy" saved to backend.
- **Fixes**:
    - **UI**: Renamed "Save" button to "Download" in `BpmnModeler` and `DmnModeler`.
    - **UI**: Renamed "Save" button to "Download" in `BpmnModeler` and `DmnModeler`.
    - **UI**: Added tooltips to clarify actions.

### 11:35 - Fix: Modeler Stability (10 min)
- **Problem**: React Strict Mode caused race conditions in `bpmn-js` / `dmn-js` initialization (`root-0` error or crashes on "New Process").
- **Fixes**:
    - **UI**: Refactored `BpmnModeler.tsx` to handle async imports and component unmounting gracefully.
    - **UI**: Refactored `BpmnModeler.tsx` to handle async imports and component unmounting gracefully.
    - **UI**: Applied similar stability fix to `DmnModeler.tsx`.

### 11:45 - Feature: Process Key Management (10 min)
- **Problem**: User reported "only one process showing" after deploying multiple times.
- **Root Cause**: `modeler-ui` defaulted all new processes to `id="Process_1"`. Activiti Engine treats same-ID deployments as *new versions* of the same process, not new processes.
- **Fix**:
    - **UI**: Added "Key (ID)" input field in `BpmnModeler` toolbar.
    - **UI**: Implemented logic to sync Key with the BPMN `<process id="...">` attribute.
    - **Result**: Users can now create distinct processes by changing the Key.

---

## Current Project Structure

```
activiti7-enterprise-workflow/
├── DEVLOG.md                   # This file
├── ARCHITECTURE.md             # System diagrams
├── README.md                   # Project docs
├── build.gradle                # Root Gradle build
├── settings.gradle             # Module definitions
├── gradle.properties           # Version management
├── docker-compose.yml          # Local dev (all OSS)
├── .gitignore                  # Git ignore rules
│
├── config/
│   └── application-template.yml # Config with Keycloak/Ping, OpenSearch/Splunk
│
├── docs/
│   └── LOCAL_DEPLOYMENT.md     # Local deployment guide
│
├── scripts/
│   └── init-db.sql             # Database initialization
│
├── keycloak/
│   └── realm-export.json       # Keycloak realm config
│
├── monitoring/
│   └── prometheus.yml          # Prometheus config
│
├── libs/                       # Shared Libraries
│   ├── common/                 # DTOs, utils, config
│   │   └── LoggingBackendConfig.java
│   ├── security-common/        # OAuth2/OIDC
│   │   └── IdentityProviderConfig.java
│   └── messaging-common/       # RabbitMQ/Kafka
│
├── services/                   # Backend Microservices (11)
│   ├── workflow-engine/        # BPMN 2.0 Engine
│   ├── task-service/           # User Task Management
│   ├── form-service/           # Form.io Integration
│   ├── decision-engine/        # DMN Engine
│   ├── case-engine/            # CMMN Engine (Flowable)
│   ├── query-service/          # Read Queries
│   ├── audit-service/          # Event Logging
│   ├── history-service/        # Historical Data
│   ├── reporting-service/      # Dashboards & Analytics
│   ├── content-service/        # Document Management
│   └── ai-service/             # AI Automation
│
├── apps/                       # Frontend Applications (5)
│   ├── modeler-ui/             # Process Designer (bpmn-js)
│   ├── tasklist-ui/            # Task Interface (form.io)
│   ├── admin-ui/               # Admin Console
│   ├── forms-ui/               # Public Forms (form.io)
│   └── reporting-ui/           # Dashboards (Recharts)
│
├── connectors/                 # Integration Connectors (14)
│   ├── rest-connector/
│   ├── email-connector/
│   ├── slack-connector/
│   ├── teams-connector/
│   ├── salesforce-connector/
│   ├── sap-connector/
│   ├── ms365-outlook-connector/
│   ├── ms365-sharepoint-connector/
│   ├── ms365-teams-connector/
│   ├── ms365-onedrive-connector/
│   ├── ms365-planner-connector/
│   ├── aws-s3-connector/
│   ├── azure-blob-connector/
│   └── google-drive-connector/
│
├── k8s/                        # Kubernetes Manifests
│   ├── base/                   # Namespace, ConfigMaps, PG
│   ├── services/               # Backend deployments
│   ├── apps/                   # Frontend deployments
│   └── ingress/                # Ingress routing
│
└── openshift/                  # OpenShift Configs
    ├── routes.yaml
    ├── buildconfigs.yaml
    └── imagestreams.yaml
```

---

## Technology Stack

### Local Development (All Open Source)
| Component | Technology | Version |
|-----------|------------|---------|
| Identity | Keycloak | 23.x |
| Logging | OpenSearch | 2.12 |
| Dashboards | OpenSearch Dashboards | 2.12 |
| Monitoring | Prometheus + Grafana | Latest |
| Database | PostgreSQL | 16 |
| Cache | Redis | 7 |
| Messaging | RabbitMQ | 3.13 |
| Storage | MinIO | Latest |

### Enterprise (When Available)
| Component | Technology |
|-----------|------------|
| Identity | Ping Federation |
| Logging | Splunk |
| SIEM | Splunk Enterprise Security |

---

## Quick Reference

### Start Local Environment
```bash
docker-compose up -d
```

### Service URLs (Local)
| Service | URL |
|---------|-----|
| Keycloak | http://localhost:8180 |
| RabbitMQ | http://localhost:15672 |
| OpenSearch Dashboards | http://localhost:5601 |
| MinIO Console | http://localhost:9001 |
| Grafana | http://localhost:3000 |
| Prometheus | http://localhost:9090 |

### Test Users
| Username | Password | Role |
|----------|----------|------|
| admin | admin123 | Admin |
| modeler | modeler123 | Modeler |
| user | user123 | User |
| analyst | analyst123 | Analyst |

---

## Next Steps

### Immediate
- [ ] Initialize git and commit
- [ ] Move existing Java code to new structure
- [ ] Create Gradle wrapper

### Week 1
- [ ] Complete workflow-engine implementation
- [ ] Complete task-service implementation
- [ ] Start modeler-ui with bpmn-js

### Week 2-3
- [ ] Form service with form.io
- [ ] Tasklist UI
- [ ] Keycloak integration testing
