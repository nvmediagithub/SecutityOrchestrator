package org.example.features.testdata.domain.repositories;

import org.example.features.testdata.domain.DataField;

import java.util.List;
import java.util.Optional;

/**
 * Domain repository interface for DataField entities
 */
public interface DataFieldRepository {

    /**
     * Find field by field ID
     */
    Optional<DataField> findByFieldId(String fieldId);

    /**
     * Find fields by field name containing
     */
    List<DataField> findByFieldNameContainingIgnoreCase(String fieldName);

    /**
     * Find fields by data type
     */
    List<DataField> findByDataType(String dataType);

    /**
     * Find active fields
     */
    List<DataField> findActiveFields();

    /**
     * Find fields by status
     */
    List<DataField> findByStatus(DataField.FieldStatus status);

    /**
     * Find fields by created by
     */
    List<DataField> findByCreatedBy(String createdBy);

    /**
     * Find fields by field IDs
     */
    List<DataField> findByFieldIds(List<String> fieldIds);

    /**
     * Count fields by data type
     */
    long countByDataType(String dataType);

    /**
     * Find fields with description containing
     */
    List<DataField> findByDescriptionContaining(String keyword);

    /**
     * Save a field
     */
    DataField save(DataField field);

    /**
     * Find by ID
     */
    Optional<DataField> findById(Long id);

    /**
     * Find all fields
     */
    List<DataField> findAll();

    /**
     * Delete a field
     */
    void delete(DataField field);
}