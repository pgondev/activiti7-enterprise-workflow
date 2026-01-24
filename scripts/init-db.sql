-- Initialize database schemas for workflow platform
-- This script runs automatically on first PostgreSQL startup

-- Create schemas for different services
CREATE SCHEMA IF NOT EXISTS activiti;
CREATE SCHEMA IF NOT EXISTS workflow;
CREATE SCHEMA IF NOT EXISTS forms;
CREATE SCHEMA IF NOT EXISTS audit;
CREATE SCHEMA IF NOT EXISTS reports;

-- Grant permissions
GRANT ALL PRIVILEGES ON SCHEMA activiti TO workflow;
GRANT ALL PRIVILEGES ON SCHEMA workflow TO workflow;
GRANT ALL PRIVILEGES ON SCHEMA forms TO workflow;
GRANT ALL PRIVILEGES ON SCHEMA audit TO workflow;
GRANT ALL PRIVILEGES ON SCHEMA reports TO workflow;

-- Create extension for UUID support
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Audit log table for compliance
CREATE TABLE IF NOT EXISTS audit.event_log (
    id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    event_type VARCHAR(100) NOT NULL,
    event_timestamp TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    user_id VARCHAR(255),
    tenant_id VARCHAR(100),
    process_instance_id VARCHAR(255),
    task_id VARCHAR(255),
    resource_type VARCHAR(100),
    resource_id VARCHAR(255),
    action VARCHAR(50),
    details JSONB,
    ip_address VARCHAR(45),
    user_agent TEXT
);

CREATE INDEX idx_event_log_timestamp ON audit.event_log(event_timestamp);
CREATE INDEX idx_event_log_user ON audit.event_log(user_id);
CREATE INDEX idx_event_log_process ON audit.event_log(process_instance_id);
CREATE INDEX idx_event_log_type ON audit.event_log(event_type);

-- Form definitions table
CREATE TABLE IF NOT EXISTS forms.form_definition (
    id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    key VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    version INTEGER DEFAULT 1,
    tenant_id VARCHAR(100),
    definition JSONB NOT NULL,
    created_by VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE INDEX idx_form_definition_key ON forms.form_definition(key);
CREATE INDEX idx_form_definition_tenant ON forms.form_definition(tenant_id);

-- Form submissions table
CREATE TABLE IF NOT EXISTS forms.form_submission (
    id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    form_key VARCHAR(255) NOT NULL,
    process_instance_id VARCHAR(255),
    task_id VARCHAR(255),
    tenant_id VARCHAR(100),
    submitted_by VARCHAR(255),
    submitted_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    data JSONB NOT NULL
);

CREATE INDEX idx_form_submission_form ON forms.form_submission(form_key);
CREATE INDEX idx_form_submission_process ON forms.form_submission(process_instance_id);
CREATE INDEX idx_form_submission_task ON forms.form_submission(task_id);

-- Custom reports table
CREATE TABLE IF NOT EXISTS reports.custom_report (
    id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    process_definition_key VARCHAR(255),
    report_type VARCHAR(50),
    configuration JSONB NOT NULL,
    is_public BOOLEAN DEFAULT FALSE,
    created_by VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE INDEX idx_custom_report_process ON reports.custom_report(process_definition_key);
CREATE INDEX idx_custom_report_user ON reports.custom_report(created_by);

COMMENT ON SCHEMA activiti IS 'Activiti7 engine tables';
COMMENT ON SCHEMA workflow IS 'Custom workflow tables';
COMMENT ON SCHEMA forms IS 'Form.io form definitions and submissions';
COMMENT ON SCHEMA audit IS 'Audit trail and compliance';
COMMENT ON SCHEMA reports IS 'Custom reports and dashboards';
