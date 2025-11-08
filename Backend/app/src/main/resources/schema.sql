-- =====================================================
-- SQL SCHEMA FOR TEST DATA MANAGEMENT SYSTEM
-- H2 Database Schema for Security Orchestrator Backend
-- =====================================================

-- Enable H2 extensions for better performance
CREATE ALIAS IF NOT EXISTS H2EXTRACTXML FOR "org.h2.tools.ExtractFile.extractXml";
CREATE ALIAS IF NOT EXISTS H2COMPRESS FOR "org.h2.tools.Compress.compress";
CREATE ALIAS IF NOT EXISTS H2REGEXP_REPLACE FOR "org.h2.tools.RegExp.replaceAll";

-- =====================================================
-- 1. TEST DATA SETS TABLE
-- =====================================================
CREATE TABLE test_data_sets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    data_set_id VARCHAR(255) UNIQUE NOT NULL,
    name VARCHAR(1000) NOT NULL,
    description VARCHAR(2000),
    version_id VARCHAR(255),
    data_content CLOB NOT NULL,
    metadata CLOB,
    privacy_classification VARCHAR(100),
    status VARCHAR(50) DEFAULT 'ACTIVE',
    data_type VARCHAR(100),
    schema_version VARCHAR(255),
    data_size BIGINT,
    record_count INTEGER,
    is_compressed BOOLEAN DEFAULT FALSE,
    is_encrypted BOOLEAN DEFAULT FALSE,
    is_archived BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_accessed TIMESTAMP,
    created_by VARCHAR(255),
    last_modified_by VARCHAR(255),
    last_accessed_by VARCHAR(255),
    notes VARCHAR(2000),
    
    -- Indexes
    CONSTRAINT uk_test_data_sets_data_set_id UNIQUE (data_set_id),
    CONSTRAINT uk_test_data_sets_name UNIQUE (name)
);

-- Create indexes for performance
CREATE INDEX idx_test_data_sets_status ON test_data_sets(status);
CREATE INDEX idx_test_data_sets_privacy_classification ON test_data_sets(privacy_classification);
CREATE INDEX idx_test_data_sets_data_type ON test_data_sets(data_type);
CREATE INDEX idx_test_data_sets_created_at ON test_data_sets(created_at);
CREATE INDEX idx_test_data_sets_last_accessed ON test_data_sets(last_accessed);
CREATE INDEX idx_test_data_sets_is_archived ON test_data_sets(is_archived);
CREATE INDEX idx_test_data_sets_created_by ON test_data_sets(created_by);

-- =====================================================
-- 2. TEST DATA VERSIONS TABLE
-- =====================================================
CREATE TABLE test_data_versions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    version_id VARCHAR(255) UNIQUE NOT NULL,
    data_set_id VARCHAR(255) NOT NULL,
    version_number INTEGER NOT NULL,
    version_name VARCHAR(1000),
    data_content CLOB NOT NULL,
    change_description CLOB,
    diff_data CLOB,
    parent_version_id VARCHAR(255),
    branch_name VARCHAR(255),
    status VARCHAR(50) DEFAULT 'ACTIVE',
    version_type VARCHAR(50),
    is_stable BOOLEAN DEFAULT FALSE,
    is_production_ready BOOLEAN DEFAULT FALSE,
    data_size BIGINT,
    record_count INTEGER,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    released_at TIMESTAMP,
    created_by VARCHAR(255),
    released_by VARCHAR(255),
    notes VARCHAR(2000),
    
    -- Foreign key constraint
    CONSTRAINT fk_test_data_versions_data_set_id FOREIGN KEY (data_set_id) 
        REFERENCES test_data_sets(data_set_id) ON DELETE CASCADE,
    CONSTRAINT uk_test_data_versions_version_id UNIQUE (version_id),
    CONSTRAINT uk_test_data_versions_data_set_version UNIQUE (data_set_id, version_number)
);

-- Create indexes
CREATE INDEX idx_test_data_versions_data_set_id ON test_data_versions(data_set_id);
CREATE INDEX idx_test_data_versions_status ON test_data_versions(status);
CREATE INDEX idx_test_data_versions_version_type ON test_data_versions(version_type);
CREATE INDEX idx_test_data_versions_created_at ON test_data_versions(created_at);
CREATE INDEX idx_test_data_versions_is_stable ON test_data_versions(is_stable);
CREATE INDEX idx_test_data_versions_is_production_ready ON test_data_versions(is_production_ready);
CREATE INDEX idx_test_data_versions_parent_version_id ON test_data_versions(parent_version_id);
CREATE INDEX idx_test_data_versions_branch_name ON test_data_versions(branch_name);

-- =====================================================
-- 3. DATA PRIVACY CLASSIFICATION TABLE
-- =====================================================
CREATE TABLE data_privacy_classification (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    classification_id VARCHAR(255) UNIQUE NOT NULL,
    name VARCHAR(1000) NOT NULL,
    description VARCHAR(2000),
    privacy_level VARCHAR(50) NOT NULL,
    data_category VARCHAR(50) NOT NULL,
    compliance_standard VARCHAR(1000),
    retention_period VARCHAR(2000),
    requires_consent BOOLEAN DEFAULT FALSE,
    has_right_to_erasure BOOLEAN DEFAULT FALSE,
    has_data_portability BOOLEAN DEFAULT FALSE,
    access_restrictions VARCHAR(2000),
    processing_rules VARCHAR(2000),
    effective_from TIMESTAMP,
    effective_to TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    priority INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    last_modified_by VARCHAR(255),
    notes VARCHAR(2000),
    
    CONSTRAINT uk_data_privacy_classification_id UNIQUE (classification_id)
);

-- Create indexes
CREATE INDEX idx_data_privacy_classification_privacy_level ON data_privacy_classification(privacy_level);
CREATE INDEX idx_data_privacy_classification_data_category ON data_privacy_classification(data_category);
CREATE INDEX idx_data_privacy_classification_is_active ON data_privacy_classification(is_active);
CREATE INDEX idx_data_privacy_classification_effective_from ON data_privacy_classification(effective_from);

-- =====================================================
-- 4. RETENTION POLICIES TABLE
-- =====================================================
CREATE TABLE retention_policies (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    policy_id VARCHAR(255) UNIQUE NOT NULL,
    name VARCHAR(1000) NOT NULL,
    description VARCHAR(2000),
    data_type VARCHAR(100) NOT NULL,
    retention_days INTEGER NOT NULL,
    archive_days INTEGER NOT NULL,
    deletion_policy VARCHAR(50),
    scope VARCHAR(100),
    applies_to_id VARCHAR(255),
    archive_action VARCHAR(50) DEFAULT 'ARCHIVE_TO_COLD_STORAGE',
    deletion_action VARCHAR(50) DEFAULT 'SECURE_DELETE',
    conditions CLOB,
    exceptions CLOB,
    is_automatic BOOLEAN DEFAULT TRUE,
    is_compliance_required BOOLEAN DEFAULT FALSE,
    has_grace_period BOOLEAN DEFAULT FALSE,
    grace_period_days INTEGER DEFAULT 0,
    effective_from TIMESTAMP,
    effective_to TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    priority INTEGER DEFAULT 0,
    usage_count INTEGER DEFAULT 0,
    last_executed TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    last_modified_by VARCHAR(255),
    last_executed_by VARCHAR(255),
    compliance_notes CLOB,
    audit_trail CLOB,
    notes VARCHAR(2000),
    
    CONSTRAINT uk_retention_policies_id UNIQUE (policy_id),
    CONSTRAINT ck_retention_policies_retention_days CHECK (retention_days >= 0),
    CONSTRAINT ck_retention_policies_archive_days CHECK (archive_days >= 0),
    CONSTRAINT ck_retention_policies_grace_period_days CHECK (grace_period_days >= 0)
);

-- Create indexes
CREATE INDEX idx_retention_policies_data_type ON retention_policies(data_type);
CREATE INDEX idx_retention_policies_scope ON retention_policies(scope);
CREATE INDEX idx_retention_policies_is_active ON retention_policies(is_active);
CREATE INDEX idx_retention_policies_effective_from ON retention_policies(effective_from);
CREATE INDEX idx_retention_policies_last_executed ON retention_policies(last_executed);

-- =====================================================
-- 5. DATA MASKING RULES TABLE
-- =====================================================
CREATE TABLE data_masking_rules (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    rule_id VARCHAR(255) UNIQUE NOT NULL,
    name VARCHAR(1000) NOT NULL,
    description VARCHAR(2000),
    field_name VARCHAR(1000),
    masking_type VARCHAR(50) NOT NULL,
    masking_pattern CLOB,
    replacement_value VARCHAR(1000),
    masking_algorithm VARCHAR(1000),
    format VARCHAR(1000),
    applies_to_classification VARCHAR(255),
    applies_to_data_type VARCHAR(255),
    scope VARCHAR(1000),
    applies_to_id VARCHAR(255),
    conditions CLOB,
    is_active BOOLEAN DEFAULT TRUE,
    is_reversible BOOLEAN DEFAULT FALSE,
    preserve_format BOOLEAN DEFAULT TRUE,
    preserve_length BOOLEAN DEFAULT TRUE,
    preserve_patterns BOOLEAN DEFAULT TRUE,
    priority INTEGER DEFAULT 0,
    validation_rules CLOB,
    exceptions CLOB,
    effective_from TIMESTAMP,
    effective_to TIMESTAMP,
    usage_count INTEGER DEFAULT 0,
    last_executed TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    last_modified_by VARCHAR(255),
    last_executed_by VARCHAR(255),
    performance_notes CLOB,
    security_notes CLOB,
    notes VARCHAR(2000),
    
    CONSTRAINT uk_data_masking_rules_id UNIQUE (rule_id),
    CONSTRAINT ck_data_masking_rules_priority CHECK (priority >= 0)
);

-- Create indexes
CREATE INDEX idx_data_masking_rules_masking_type ON data_masking_rules(masking_type);
CREATE INDEX idx_data_masking_rules_scope ON data_masking_rules(scope);
CREATE INDEX idx_data_masking_rules_is_active ON data_masking_rules(is_active);
CREATE INDEX idx_data_masking_rules_applies_to_classification ON data_masking_rules(applies_to_classification);
CREATE INDEX idx_data_masking_rules_applies_to_data_type ON data_masking_rules(applies_to_data_type);
CREATE INDEX idx_data_masking_rules_priority ON data_masking_rules(priority);
CREATE INDEX idx_data_masking_rules_last_executed ON data_masking_rules(last_executed);

-- =====================================================
-- 6. TEST DATA AUDIT TABLE
-- =====================================================
CREATE TABLE test_data_audit (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    audit_id VARCHAR(255) UNIQUE NOT NULL,
    data_set_id VARCHAR(255),
    action VARCHAR(50) NOT NULL,
    user_id VARCHAR(255) NOT NULL,
    old_values CLOB,
    new_values CLOB,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ip_address VARCHAR(45),
    user_agent VARCHAR(500),
    session_id VARCHAR(255),
    additional_info CLOB,
    
    CONSTRAINT uk_test_data_audit_id UNIQUE (audit_id),
    CONSTRAINT fk_test_data_audit_data_set_id FOREIGN KEY (data_set_id) 
        REFERENCES test_data_sets(data_set_id) ON DELETE SET NULL
);

-- Create indexes
CREATE INDEX idx_test_data_audit_data_set_id ON test_data_audit(data_set_id);
CREATE INDEX idx_test_data_audit_action ON test_data_audit(action);
CREATE INDEX idx_test_data_audit_user_id ON test_data_audit(user_id);
CREATE INDEX idx_test_data_audit_timestamp ON test_data_audit(timestamp);
CREATE INDEX idx_test_data_audit_session_id ON test_data_audit(session_id);

-- =====================================================
-- 7. TEST DATA METADATA TABLE
-- =====================================================
CREATE TABLE test_data_metadata (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    metadata_id VARCHAR(255) UNIQUE NOT NULL,
    data_set_id VARCHAR(255) NOT NULL,
    key VARCHAR(255) NOT NULL,
    value CLOB,
    data_type VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT uk_test_data_metadata_id UNIQUE (metadata_id),
    CONSTRAINT fk_test_data_metadata_data_set_id FOREIGN KEY (data_set_id) 
        REFERENCES test_data_sets(data_set_id) ON DELETE CASCADE,
    CONSTRAINT uk_test_data_metadata_data_set_key UNIQUE (data_set_id, key)
);

-- Create indexes
CREATE INDEX idx_test_data_metadata_data_set_id ON test_data_metadata(data_set_id);
CREATE INDEX idx_test_data_metadata_key ON test_data_metadata(key);
CREATE INDEX idx_test_data_metadata_data_type ON test_data_metadata(data_type);
CREATE INDEX idx_test_data_metadata_created_at ON test_data_metadata(created_at);

-- =====================================================
-- SAMPLE DATA FOR TESTING
-- =====================================================

-- Insert sample privacy classifications
INSERT INTO data_privacy_classification (classification_id, name, description, privacy_level, data_category, compliance_standard, requires_consent, created_by) VALUES
('DPC_PUBLIC', 'Public Data', 'Data that can be freely shared', 'PUBLIC', 'OTHER', NULL, FALSE, 'SYSTEM'),
('DPC_INTERNAL', 'Internal Data', 'Data for internal use only', 'INTERNAL', 'TECHNICAL_DATA', NULL, FALSE, 'SYSTEM'),
('DPC_CONFIDENTIAL', 'Confidential Data', 'Sensitive business data', 'CONFIDENTIAL', 'PERSONAL_DATA', 'GDPR', TRUE, 'SYSTEM'),
('DPC_RESTRICTED', 'Restricted Data', 'Highly sensitive personal data', 'RESTRICTED', 'PERSONAL_DATA', 'GDPR,HIPAA', TRUE, 'SYSTEM');

-- Insert sample masking rules
INSERT INTO data_masking_rules (rule_id, name, description, field_name, masking_type, masking_pattern, replacement_value, scope, priority, created_by) VALUES
('DMR_EMAIL', 'Mask Email Addresses', 'Mask email addresses in data', 'email', 'PATTERN_REPLACEMENT', '\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\\b', 'email@masked.com', 'GLOBAL', 1, 'SYSTEM'),
('DMR_PHONE', 'Mask Phone Numbers', 'Mask phone numbers in data', 'phone', 'PATTERN_REPLACEMENT', '\\b\\d{3}[-.]?\\d{3}[-.]?\\d{4}\\b', 'XXX-XXX-XXXX', 'GLOBAL', 2, 'SYSTEM'),
('DMR_SSN', 'Mask SSN', 'Mask social security numbers', 'ssn', 'PATTERN_REPLACEMENT', '\\b\\d{3}-\\d{2}-\\d{4}\\b', 'XXX-XX-XXXX', 'GLOBAL', 3, 'SYSTEM');

-- Insert sample retention policies
INSERT INTO retention_policies (policy_id, name, description, data_type, retention_days, archive_days, deletion_policy, scope, is_automatic, created_by) VALUES
('RP_TEMP_DATA', 'Temporary Data Policy', 'Auto-delete temporary test data after 30 days', 'TEMP', 30, 7, 'IMMEDIATE', 'GLOBAL', TRUE, 'SYSTEM'),
('RP_PROD_DATA', 'Production Data Policy', 'Archive production data after 1 year, delete after 7 years', 'PRODUCTION', 2555, 365, 'COMPLIANCE_BASED', 'GLOBAL', TRUE, 'SYSTEM'),
('RP_TEST_DATA', 'Test Data Policy', 'Archive test data after 90 days, delete after 1 year', 'TEST', 365, 90, 'GRADUAL', 'GLOBAL', TRUE, 'SYSTEM');

-- =====================================================
-- END OF SCHEMA
-- =====================================================