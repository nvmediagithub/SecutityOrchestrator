package org.example.features.testdata.infrastructure.repositories;

import org.example.features.testdata.domain.repositories.DataFieldRepository;
import org.example.shared.domain.entities.DataField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DataFieldRepositoryAdapter extends JpaRepository<DataField, Long>, DataFieldRepository {

    @Override
    default Optional<DataField> findByFieldId(String fieldId) {
        return findByFieldIdQuery(fieldId);
    }

    @Query("SELECT f FROM DataField f WHERE f.fieldId = :fieldId")
    Optional<DataField> findByFieldIdQuery(@Param("fieldId") String fieldId);

    @Override
    default List<DataField> findByFieldNameContainingIgnoreCase(String fieldName) {
        return findByFieldNameContainingIgnoreCaseQuery(fieldName);
    }

    @Query("SELECT f FROM DataField f WHERE LOWER(f.fieldName) LIKE LOWER(CONCAT('%', :fieldName, '%'))")
    List<DataField> findByFieldNameContainingIgnoreCaseQuery(@Param("fieldName") String fieldName);

    @Override
    default List<DataField> findByDataType(String dataType) {
        return findByDataTypeQuery(dataType);
    }

    @Query("SELECT f FROM DataField f WHERE f.dataType = :dataType")
    List<DataField> findByDataTypeQuery(@Param("dataType") String dataType);

    @Override
    default List<DataField> findActiveFields() {
        return findActiveFieldsQuery();
    }

    @Query("SELECT f FROM DataField f WHERE f.status = 'ACTIVE'")
    List<DataField> findActiveFieldsQuery();

    @Override
    default List<DataField> findByStatus(DataField.FieldStatus status) {
        return findByStatusQuery(status);
    }

    @Query("SELECT f FROM DataField f WHERE f.status = :status")
    List<DataField> findByStatusQuery(@Param("status") DataField.FieldStatus status);

    @Override
    default List<DataField> findByCreatedBy(String createdBy) {
        return findByCreatedByQuery(createdBy);
    }

    @Query("SELECT f FROM DataField f WHERE f.createdBy = :createdBy")
    List<DataField> findByCreatedByQuery(@Param("createdBy") String createdBy);

    @Override
    default List<DataField> findByFieldIds(List<String> fieldIds) {
        return findByFieldIdsQuery(fieldIds);
    }

    @Query("SELECT f FROM DataField f WHERE f.fieldId IN :fieldIds")
    List<DataField> findByFieldIdsQuery(@Param("fieldIds") List<String> fieldIds);

    @Override
    default long countByDataType(String dataType) {
        return countByDataTypeQuery(dataType);
    }

    @Query("SELECT COUNT(f) FROM DataField f WHERE f.dataType = :dataType")
    long countByDataTypeQuery(@Param("dataType") String dataType);

    @Override
    default List<DataField> findByDescriptionContaining(String keyword) {
        return findByDescriptionContainingQuery(keyword);
    }

    @Query("SELECT f FROM DataField f WHERE f.description LIKE CONCAT('%', :keyword, '%')")
    List<DataField> findByDescriptionContainingQuery(@Param("keyword") String keyword);

    @Override
    default DataField save(DataField field) {
        return save(field);
    }

    @Override
    default Optional<DataField> findById(Long id) {
        return findById(id);
    }

    @Override
    default List<DataField> findAll() {
        return findAll();
    }

    @Override
    default void delete(DataField field) {
        delete(field);
    }
}