package org.example.domain.dto.dependency;

import java.util.List;
import java.util.Set;
import java.util.Objects;

/**
 * Represents business rule dependencies and constraints
 */
public class BusinessRuleDependency {
    private String ruleId;
    private String ruleName;
    private String ruleDescription;
    private String ruleType;
    private RuleScope scope;
    private Set<String> affectedFields;
    private Set<String> validationFields;
    private Set<String> dependencyFields;
    private String validationLogic;
    private String constraintExpression;
    private String roleRequirement;
    private String permissionLevel;
    private String dataClassification;
    private String complianceRule;
    private String regulatoryRequirement;
    private String lifeCyclePhase;
    private String auditRequirement;
    private String dataRetention;
    private String dataArchival;
    private String crossFieldValidation;
    String parentRule;
    private List<String> childRules;
    private String exceptionHandling;
    private String businessContext;
    private String dataSource;
    private String dataOwner;
    private String rulePriority;
    private String ruleVersion;
    private String effectiveDate;
    private String expirationDate;
    
    public enum RuleScope {
        FIELD_LEVEL,
        RECORD_LEVEL,
        PROCESS_LEVEL,
        SYSTEM_LEVEL,
        ORGANIZATIONAL_LEVEL,
        COMPLIANCE_LEVEL
    }
    
    public enum RuleType {
        VALIDATION,
        CONSTRAINT,
        BUSINESS_LOGIC,
        PERMISSION,
        COMPLIANCE,
        AUDIT,
        RETENTION,
        ARCHIVAL,
        CROSS_FIELD_VALIDATION,
        WORKFLOW_RULE
    }
    
    public BusinessRuleDependency() {}
    
    public BusinessRuleDependency(String ruleId, String ruleName, String ruleType, RuleScope scope) {
        this.ruleId = ruleId;
        this.ruleName = ruleName;
        this.ruleType = ruleType;
        this.scope = scope;
    }
    
    // Getters and Setters
    public String getRuleId() {
        return ruleId;
    }
    
    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }
    
    public String getRuleName() {
        return ruleName;
    }
    
    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }
    
    public String getRuleDescription() {
        return ruleDescription;
    }
    
    public void setRuleDescription(String ruleDescription) {
        this.ruleDescription = ruleDescription;
    }
    
    public String getRuleType() {
        return ruleType;
    }
    
    public void setRuleType(String ruleType) {
        this.ruleType = ruleType;
    }
    
    public RuleScope getScope() {
        return scope;
    }
    
    public void setScope(RuleScope scope) {
        this.scope = scope;
    }
    
    public Set<String> getAffectedFields() {
        return affectedFields;
    }
    
    public void setAffectedFields(Set<String> affectedFields) {
        this.affectedFields = affectedFields;
    }
    
    public Set<String> getValidationFields() {
        return validationFields;
    }
    
    public void setValidationFields(Set<String> validationFields) {
        this.validationFields = validationFields;
    }
    
    public Set<String> getDependencyFields() {
        return dependencyFields;
    }
    
    public void setDependencyFields(Set<String> dependencyFields) {
        this.dependencyFields = dependencyFields;
    }
    
    public String getValidationLogic() {
        return validationLogic;
    }
    
    public void setValidationLogic(String validationLogic) {
        this.validationLogic = validationLogic;
    }
    
    public String getConstraintExpression() {
        return constraintExpression;
    }
    
    public void setConstraintExpression(String constraintExpression) {
        this.constraintExpression = constraintExpression;
    }
    
    public String getRoleRequirement() {
        return roleRequirement;
    }
    
    public void setRoleRequirement(String roleRequirement) {
        this.roleRequirement = roleRequirement;
    }
    
    public String getPermissionLevel() {
        return permissionLevel;
    }
    
    public void setPermissionLevel(String permissionLevel) {
        this.permissionLevel = permissionLevel;
    }
    
    public String getDataClassification() {
        return dataClassification;
    }
    
    public void setDataClassification(String dataClassification) {
        this.dataClassification = dataClassification;
    }
    
    public String getComplianceRule() {
        return complianceRule;
    }
    
    public void setComplianceRule(String complianceRule) {
        this.complianceRule = complianceRule;
    }
    
    public String getRegulatoryRequirement() {
        return regulatoryRequirement;
    }
    
    public void setRegulatoryRequirement(String regulatoryRequirement) {
        this.regulatoryRequirement = regulatoryRequirement;
    }
    
    public String getLifeCyclePhase() {
        return lifeCyclePhase;
    }
    
    public void setLifeCyclePhase(String lifeCyclePhase) {
        this.lifeCyclePhase = lifeCyclePhase;
    }
    
    public String getAuditRequirement() {
        return auditRequirement;
    }
    
    public void setAuditRequirement(String auditRequirement) {
        this.auditRequirement = auditRequirement;
    }
    
    public String getDataRetention() {
        return dataRetention;
    }
    
    public void setDataRetention(String dataRetention) {
        this.dataRetention = dataRetention;
    }
    
    public String getDataArchival() {
        return dataArchival;
    }
    
    public void setDataArchival(String dataArchival) {
        this.dataArchival = dataArchival;
    }
    
    public String getCrossFieldValidation() {
        return crossFieldValidation;
    }
    
    public void setCrossFieldValidation(String crossFieldValidation) {
        this.crossFieldValidation = crossFieldValidation;
    }
    
    public String getParentRule() {
        return parentRule;
    }
    
    public void setParentRule(String parentRule) {
        this.parentRule = parentRule;
    }
    
    public List<String> getChildRules() {
        return childRules;
    }
    
    public void setChildRules(List<String> childRules) {
        this.childRules = childRules;
    }
    
    public String getExceptionHandling() {
        return exceptionHandling;
    }
    
    public void setExceptionHandling(String exceptionHandling) {
        this.exceptionHandling = exceptionHandling;
    }
    
    public String getBusinessContext() {
        return businessContext;
    }
    
    public void setBusinessContext(String businessContext) {
        this.businessContext = businessContext;
    }
    
    public String getDataSource() {
        return dataSource;
    }
    
    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }
    
    public String getDataOwner() {
        return dataOwner;
    }
    
    public void setDataOwner(String dataOwner) {
        this.dataOwner = dataOwner;
    }
    
    public String getRulePriority() {
        return rulePriority;
    }
    
    public void setRulePriority(String rulePriority) {
        this.rulePriority = rulePriority;
    }
    
    public String getRuleVersion() {
        return ruleVersion;
    }
    
    public void setRuleVersion(String ruleVersion) {
        this.ruleVersion = ruleVersion;
    }
    
    public String getEffectiveDate() {
        return effectiveDate;
    }
    
    public void setEffectiveDate(String effectiveDate) {
        this.effectiveDate = effectiveDate;
    }
    
    public String getExpirationDate() {
        return expirationDate;
    }
    
    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BusinessRuleDependency that = (BusinessRuleDependency) o;
        return Objects.equals(ruleId, that.ruleId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(ruleId);
    }
    
    @Override
    public String toString() {
        return "BusinessRuleDependency{" +
                "ruleId='" + ruleId + '\'' +
                ", ruleName='" + ruleName + '\'' +
                ", ruleType='" + ruleType + '\'' +
                ", scope=" + scope +
                '}';
    }
}