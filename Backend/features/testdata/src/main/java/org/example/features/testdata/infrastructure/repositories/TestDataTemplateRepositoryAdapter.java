package org.example.features.testdata.infrastructure.repositories;

import org.example.features.testdata.domain.repositories.TestDataTemplateRepository;
import org.example.shared.domain.entities.TestDataTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface TestDataTemplateRepositoryAdapter extends JpaRepository<TestDataTemplate, Long>, TestDataTemplateRepository {

    @Override
    @Query("SELECT t FROM TestDataTemplate t WHERE t.templateId = :templateId")
    Optional<TestDataTemplate> findByTemplateId(@Param("templateId") String templateId);

    @Override
    @Query("SELECT t FROM TestDataTemplate t WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<TestDataTemplate> findByNameContainingIgnoreCase(@Param("name") String name);

    @Override
    @Query("SELECT t FROM TestDataTemplate t WHERE t.createdBy = :createdBy")
    List<TestDataTemplate> findByCreatedBy(@Param("createdBy") String createdBy);

    @Query("SELECT COUNT(t) > 0 FROM TestDataTemplate t WHERE t.templateId = :templateId")
    boolean existsByTemplateId(@Param("templateId") String templateId);

    @Modifying
    @Transactional
    @Query("DELETE FROM TestDataTemplate t WHERE t.templateId = :templateId")
    void deleteByTemplateId(@Param("templateId") String templateId);

    @Override
    @Query("SELECT t FROM TestDataTemplate t WHERE t.status = 'ACTIVE'")
    List<TestDataTemplate> findActiveTemplates();

}