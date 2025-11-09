package org.example.infrastructure.repository;

import org.example.domain.entities.bpmn.BusinessProcess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for BusinessProcess entity
 */
@Repository
public interface BusinessProcessRepository extends JpaRepository<BusinessProcess, Long> {

    Optional<BusinessProcess> findByProcessId(String processId);

    List<BusinessProcess> findByNameContainingIgnoreCase(String name);

    List<BusinessProcess> findByFilePath(String filePath);

    List<BusinessProcess> findByParseStatus(BusinessProcess.ParseStatus parseStatus);

    @Query("SELECT bp FROM BusinessProcess bp WHERE bp.createdAt >= :since ORDER BY bp.createdAt DESC")
    List<BusinessProcess> findByCreatedAfter(@Param("since") LocalDateTime since);

    @Query("SELECT bp FROM BusinessProcess bp WHERE bp.executable = :executable ORDER BY bp.name")
    List<BusinessProcess> findByExecutable(@Param("executable") boolean executable);

    @Query("SELECT bp FROM BusinessProcess bp WHERE bp.version = :version ORDER BY bp.name")
    List<BusinessProcess> findByVersion(@Param("version") String version);

    boolean existsByProcessId(String processId);

    void deleteByProcessId(String processId);
}
