package org.example.repositories;

import org.example.config.TestConfig;
import org.example.domain.entities.TestProject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Repository tests for TestProject
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("TestProject Repository Tests")
class TestProjectRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private org.example.infrastructure.repositories.TestProjectRepository testProjectRepository;

    private TestProject testProject;

    @BeforeEach
    void setUp() {
        // Setup test project
        testProject = new TestProject();
        testProject.setName("Test Project");
        testProject.setDescription("Test Description");
        testProject.setOwner("test@example.com");
        testProject.setVersion("1.0.0");
        testProject.setEnvironment("test");
        testProject.setStatus(TestProject.ProjectStatus.ACTIVE);
        testProject.setIsActive(true);
    }

    @Test
    @DisplayName("Should save and find project by ID")
    void shouldSaveAndFindProjectById() {
        // When
        TestProject saved = entityManager.persistAndFlush(testProject);

        // Then
        Optional<TestProject> found = testProjectRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("Test Project", found.get().getName());
        assertEquals("test@example.com", found.get().getOwner());
    }

    @Test
    @DisplayName("Should find project by projectId")
    void shouldFindProjectByProjectId() {
        // When
        TestProject saved = entityManager.persistAndFlush(testProject);

        // Then
        Optional<TestProject> found = testProjectRepository.findByProjectId(saved.getProjectId());
        assertTrue(found.isPresent());
        assertEquals("Test Project", found.get().getName());
    }

    @Test
    @DisplayName("Should find projects by name containing")
    void shouldFindProjectsByNameContaining() {
        // Given
        TestProject project1 = new TestProject("API Testing Project", "Description 1");
        TestProject project2 = new TestProject("UI Testing Project", "Description 2");
        TestProject project3 = new TestProject("Database Project", "Description 3");
        
        entityManager.persistAndFlush(project1);
        entityManager.persistAndFlush(project2);
        entityManager.persistAndFlush(project3);

        // When
        List<TestProject> found = testProjectRepository.findByNameContainingIgnoreCase("Testing");

        // Then
        assertEquals(2, found.size());
        assertTrue(found.stream().anyMatch(p -> p.getName().equals("API Testing Project")));
        assertTrue(found.stream().anyMatch(p -> p.getName().equals("UI Testing Project")));
    }

    @Test
    @DisplayName("Should find projects by owner")
    void shouldFindProjectsByOwner() {
        // Given
        TestProject project1 = new TestProject("Project 1", "Description 1");
        project1.setOwner("user1@example.com");
        TestProject project2 = new TestProject("Project 2", "Description 2");
        project2.setOwner("user2@example.com");
        TestProject project3 = new TestProject("Project 3", "Description 3");
        project3.setOwner("user1@example.com");
        
        entityManager.persistAndFlush(project1);
        entityManager.persistAndFlush(project2);
        entityManager.persistAndFlush(project3);

        // When
        List<TestProject> found = testProjectRepository.findByOwner("user1@example.com");

        // Then
        assertEquals(2, found.size());
        assertTrue(found.stream().allMatch(p -> p.getOwner().equals("user1@example.com")));
    }

    @Test
    @DisplayName("Should find projects by status")
    void shouldFindProjectsByStatus() {
        // Given
        TestProject activeProject = new TestProject("Active Project", "Description");
        activeProject.setStatus(TestProject.ProjectStatus.ACTIVE);
        TestProject draftProject = new TestProject("Draft Project", "Description");
        draftProject.setStatus(TestProject.ProjectStatus.DRAFT);
        TestProject completedProject = new TestProject("Completed Project", "Description");
        completedProject.setStatus(TestProject.ProjectStatus.COMPLETED);
        
        entityManager.persistAndFlush(activeProject);
        entityManager.persistAndFlush(draftProject);
        entityManager.persistAndFlush(completedProject);

        // When
        List<TestProject> found = testProjectRepository.findByStatus(TestProject.ProjectStatus.ACTIVE);

        // Then
        assertEquals(1, found.size());
        assertEquals("Active Project", found.get(0).getName());
    }

    @Test
    @DisplayName("Should find active projects")
    void shouldFindActiveProjects() {
        // Given
        TestProject activeProject = new TestProject("Active Project", "Description");
        activeProject.setIsActive(true);
        TestProject inactiveProject = new TestProject("Inactive Project", "Description");
        inactiveProject.setIsActive(false);
        
        entityManager.persistAndFlush(activeProject);
        entityManager.persistAndFlush(inactiveProject);

        // When
        List<TestProject> found = testProjectRepository.findByIsActiveTrue();

        // Then
        assertEquals(1, found.size());
        assertEquals("Active Project", found.get(0).getName());
    }

    @Test
    @DisplayName("Should count active projects")
    void shouldCountActiveProjects() {
        // Given
        TestProject activeProject1 = new TestProject("Active Project 1", "Description");
        activeProject1.setIsActive(true);
        TestProject activeProject2 = new TestProject("Active Project 2", "Description");
        activeProject2.setIsActive(true);
        TestProject inactiveProject = new TestProject("Inactive Project", "Description");
        inactiveProject.setIsActive(false);
        
        entityManager.persistAndFlush(activeProject1);
        entityManager.persistAndFlush(activeProject2);
        entityManager.persistAndFlush(inactiveProject);

        // When
        long count = testProjectRepository.countActiveProjects();

        // Then
        assertEquals(2, count);
    }

    @Test
    @DisplayName("Should check if project exists by name")
    void shouldCheckIfProjectExistsByName() {
        // Given
        TestProject project = new TestProject("Unique Project Name", "Description");
        entityManager.persistAndFlush(project);

        // When
        boolean exists = testProjectRepository.existsByName("Unique Project Name");
        boolean notExists = testProjectRepository.existsByName("Non-existent Name");

        // Then
        assertTrue(exists);
        assertFalse(notExists);
    }

    @Test
    @DisplayName("Should find all projects ordered by creation date")
    void shouldFindAllProjectsOrderedByCreationDate() {
        // Given
        TestProject project1 = new TestProject("First Project", "Description");
        TestProject project2 = new TestProject("Second Project", "Description");
        TestProject project3 = new TestProject("Third Project", "Description");
        
        entityManager.persist(project1);
        entityManager.persist(project2);
        entityManager.persist(project3);
        entityManager.flush();

        // When
        List<TestProject> found = testProjectRepository.findAllByOrderByCreatedAtDesc();

        // Then
        assertEquals(3, found.size());
        // Projects should be ordered by createdAt desc (newest first)
        assertTrue(found.get(0).getCreatedAt().isAfter(found.get(1).getCreatedAt()));
        assertTrue(found.get(1).getCreatedAt().isAfter(found.get(2).getCreatedAt()));
    }

    @Test
    @DisplayName("Should find projects by environment")
    void shouldFindProjectsByEnvironment() {
        // Given
        TestProject devProject = new TestProject("Dev Project", "Description");
        devProject.setEnvironment("development");
        TestProject testProject = new TestProject("Test Project", "Description");
        testProject.setEnvironment("test");
        TestProject prodProject = new TestProject("Prod Project", "Description");
        prodProject.setEnvironment("production");
        
        entityManager.persistAndFlush(devProject);
        entityManager.persistAndFlush(testProject);
        entityManager.persistAndFlush(prodProject);

        // When
        List<TestProject> found = testProjectRepository.findByEnvironment("development");

        // Then
        assertEquals(1, found.size());
        assertEquals("Dev Project", found.get(0).getName());
    }

    @Test
    @DisplayName("Should find projects by tag")
    void shouldFindProjectsByTag() {
        // Given
        TestProject project1 = new TestProject("Tagged Project 1", "Description");
        project1.addTag("automated");
        TestProject project2 = new TestProject("Tagged Project 2", "Description");
        project2.addTag("manual");
        TestProject project3 = new TestProject("Tagged Project 3", "Description");
        project3.addTag("automated");
        
        entityManager.persistAndFlush(project1);
        entityManager.persistAndFlush(project2);
        entityManager.persistAndFlush(project3);

        // When
        List<TestProject> found = testProjectRepository.findByTag("automated");

        // Then
        assertEquals(2, found.size());
        assertTrue(found.stream().anyMatch(p -> p.getName().equals("Tagged Project 1")));
        assertTrue(found.stream().anyMatch(p -> p.getName().equals("Tagged Project 3")));
    }

    @Test
    @DisplayName("Should handle empty result for non-existent project")
    void shouldHandleEmptyResultForNonExistentProject() {
        // When
        Optional<TestProject> found = testProjectRepository.findByProjectId("non-existent");
        List<TestProject> foundByName = testProjectRepository.findByNameContainingIgnoreCase("Non-existent");
        List<TestProject> foundByOwner = testProjectRepository.findByOwner("non-existent@example.com");

        // Then
        assertFalse(found.isPresent());
        assertTrue(foundByName.isEmpty());
        assertTrue(foundByOwner.isEmpty());
    }
}