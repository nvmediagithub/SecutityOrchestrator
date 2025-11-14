package org.example.features.bpmn.domain.repositories;

import org.example.features.bpmn.domain.entities.BpmnDiagram;
import org.example.shared.core.valueobjects.ModelId;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for BPMN diagram entities
 */
public interface BpmnDiagramRepository {

    /**
     * Saves a BPMN diagram
     * @param diagram The diagram to save
     * @return The saved diagram
     */
    BpmnDiagram save(BpmnDiagram diagram);

    /**
     * Finds a diagram by its ID
     * @param id The diagram ID
     * @return Optional containing the diagram if found
     */
    Optional<BpmnDiagram> findById(ModelId id);

    /**
     * Finds a diagram by its diagram ID (string identifier)
     * @param diagramId The diagram ID string
     * @return Optional containing the diagram if found
     */
    Optional<BpmnDiagram> findByDiagramId(String diagramId);

    /**
     * Finds all active diagrams
     * @return List of active diagrams
     */
    List<BpmnDiagram> findAllActive();

    /**
     * Finds diagrams by name containing the search term
     * @param name The search term
     * @return List of matching diagrams
     */
    List<BpmnDiagram> findByNameContaining(String name);

    /**
     * Finds all diagrams ordered by creation date (newest first)
     * @return List of diagrams ordered by creation date
     */
    List<BpmnDiagram> findAllOrderByCreatedAtDesc();

    /**
     * Deletes a diagram by its ID
     * @param id The diagram ID to delete
     * @return true if deleted, false otherwise
     */
    boolean deleteById(ModelId id);

    /**
     * Counts all active diagrams
     * @return Number of active diagrams
     */
    long countActive();
}