package org.example.services;

import org.example.domain.dto.test.ApiResponse;
import org.example.domain.dto.test.TestSessionCreateRequest;
import org.example.domain.entities.TestArtifact;
import org.example.domain.entities.TestProject;
import org.example.domain.entities.TestSession;
import org.example.infrastructure.repositories.TestArtifactRepository;
import org.example.infrastructure.repositories.TestProjectRepository;
import org.example.infrastructure.repositories.TestSessionRepository;
import org.example.infrastructure.services.SessionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for SessionService
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SessionService Unit Tests")
class SessionServiceTest {

    @Mock
    private TestSessionRepository testSessionRepository;

    @Mock
    private TestProjectRepository testProjectRepository;

    @Mock
    private TestArtifactRepository testArtifactRepository;

    @InjectMocks
    private SessionService sessionService;

    private TestSession testSession;
    private TestSessionCreateRequest createRequest;
    private TestProject testProject;
    private TestArtifact testArtifact;

    @BeforeEach
    void setUp() {
        // Setup test session
        testSession = new TestSession();
        testSession.setId(1L);
        testSession.setName("Test Session");
        testSession.setDescription("Test Description");
        testSession.setExecutor("test@example.com");
        testSession.setEnvironment("test");
        testSession.setTestType("API_TESTING");
        testSession.setStatus(TestSession.SessionStatus.CREATED);
        testSession.setIsActive(true);

        // Setup test project
        testProject = new TestProject();
        testProject.setId(1L);
        testProject.setName("Test Project");
        testProject.setOwner("test@example.com");

        // Setup test artifact
        testArtifact = new TestArtifact();
        testArtifact.setId(1L);
        testArtifact.setArtifactName("Test Artifact");
        testArtifact.setArtifactType(TestArtifact.ArtifactType.OPENAPI_SPEC);

        // Setup create request
        createRequest = new TestSessionCreateRequest();
        createRequest.setName("New Test Session");
        createRequest.setDescription("New Test Description");
        createRequest.setExecutor("new@example.com");
        createRequest.setEnvironment("dev");
        createRequest.setTestType("API_TESTING");
        createRequest.setArtifactIds(Arrays.asList("artifact-1", "artifact-2"));
    }

    @Test
    @DisplayName("Should create session successfully")
    void shouldCreateSessionSuccessfully() {
        // Given
        when(testSessionRepository.save(any(TestSession.class))).thenReturn(testSession);

        // When
        ApiResponse<TestSession> result = sessionService.createSession(createRequest);

        // Then
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals("Session created successfully", result.getMessage());
        verify(testSessionRepository, times(1)).save(any(TestSession.class));
    }

    @Test
    @DisplayName("Should return error when session creation fails")
    void shouldReturnErrorWhenSessionCreationFails() {
        // Given
        when(testSessionRepository.save(any(TestSession.class)))
                .thenThrow(new RuntimeException("Database error"));

        // When
        ApiResponse<TestSession> result = sessionService.createSession(createRequest);

        // Then
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Failed to create session"));
        verify(testSessionRepository, times(1)).save(any(TestSession.class));
    }

    @Test
    @DisplayName("Should get all sessions successfully")
    void shouldGetAllSessionsSuccessfully() {
        // Given
        List<TestSession> sessions = Arrays.asList(testSession);
        when(testSessionRepository.findAllByOrderByCreatedAtDesc()).thenReturn(sessions);

        // When
        ApiResponse<List<TestSession>> result = sessionService.getAllSessions();

        // Then
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals(1, result.getData().size());
        verify(testSessionRepository, times(1)).findAllByOrderByCreatedAtDesc();
    }

    @Test
    @DisplayName("Should get session by ID successfully")
    void shouldGetSessionByIdSuccessfully() {
        // Given
        when(testSessionRepository.findById(1L)).thenReturn(Optional.of(testSession));

        // When
        ApiResponse<TestSession> result = sessionService.getSessionById(1L);

        // Then
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals(1L, result.getData().getId());
        verify(testSessionRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should return error when session not found by ID")
    void shouldReturnErrorWhenSessionNotFoundById() {
        // Given
        when(testSessionRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        ApiResponse<TestSession> result = sessionService.getSessionById(999L);

        // Then
        assertFalse(result.isSuccess());
        assertEquals("Session not found", result.getMessage());
        verify(testSessionRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Should get session by sessionId successfully")
    void shouldGetSessionBySessionIdSuccessfully() {
        // Given
        testSession.setSessionId("session-123");
        when(testSessionRepository.findBySessionId("session-123")).thenReturn(Optional.of(testSession));

        // When
        ApiResponse<TestSession> result = sessionService.getSessionBySessionId("session-123");

        // Then
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals("session-123", result.getData().getSessionId());
        verify(testSessionRepository, times(1)).findBySessionId("session-123");
    }

    @Test
    @DisplayName("Should update session successfully")
    void shouldUpdateSessionSuccessfully() {
        // Given
        when(testSessionRepository.findById(1L)).thenReturn(Optional.of(testSession));
        when(testSessionRepository.save(any(TestSession.class))).thenReturn(testSession);

        // When
        ApiResponse<TestSession> result = sessionService.updateSession(1L, createRequest);

        // Then
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals("Session updated successfully", result.getMessage());
        verify(testSessionRepository, times(1)).findById(1L);
        verify(testSessionRepository, times(1)).save(any(TestSession.class));
    }

    @Test
    @DisplayName("Should start session successfully")
    void shouldStartSessionSuccessfully() {
        // Given
        when(testSessionRepository.findById(1L)).thenReturn(Optional.of(testSession));
        when(testSessionRepository.save(any(TestSession.class))).thenReturn(testSession);

        // When
        ApiResponse<TestSession> result = sessionService.startSession(1L);

        // Then
        assertTrue(result.isSuccess());
        assertEquals(TestSession.SessionStatus.RUNNING, result.getData().getStatus());
        assertEquals("Session started successfully", result.getMessage());
        verify(testSessionRepository, times(1)).findById(1L);
        verify(testSessionRepository, times(1)).save(any(TestSession.class));
    }

    @Test
    @DisplayName("Should stop running session successfully")
    void shouldStopRunningSessionSuccessfully() {
        // Given
        testSession.setStatus(TestSession.SessionStatus.RUNNING);
        when(testSessionRepository.findById(1L)).thenReturn(Optional.of(testSession));
        when(testSessionRepository.save(any(TestSession.class))).thenReturn(testSession);

        // When
        ApiResponse<TestSession> result = sessionService.stopSession(1L);

        // Then
        assertTrue(result.isSuccess());
        assertEquals(TestSession.SessionStatus.COMPLETED, result.getData().getStatus());
        assertEquals("Session stopped successfully", result.getMessage());
        verify(testSessionRepository, times(1)).findById(1L);
        verify(testSessionRepository, times(1)).save(any(TestSession.class));
    }

    @Test
    @DisplayName("Should update session progress successfully")
    void shouldUpdateSessionProgressSuccessfully() {
        // Given
        when(testSessionRepository.findById(1L)).thenReturn(Optional.of(testSession));
        when(testSessionRepository.save(any(TestSession.class))).thenReturn(testSession);

        // When
        ApiResponse<TestSession> result = sessionService.updateSessionProgress(1L, 5, 2, 1, 10);

        // Then
        assertTrue(result.isSuccess());
        assertEquals("Session progress updated", result.getMessage());
        verify(testSessionRepository, times(1)).findById(1L);
        verify(testSessionRepository, times(1)).save(any(TestSession.class));
    }

    @Test
    @DisplayName("Should get sessions by status successfully")
    void shouldGetSessionsByStatusSuccessfully() {
        // Given
        List<TestSession> sessions = Arrays.asList(testSession);
        TestSession.SessionStatus status = TestSession.SessionStatus.CREATED;
        when(testSessionRepository.findByStatus(status)).thenReturn(sessions);

        // When
        ApiResponse<List<TestSession>> result = sessionService.getSessionsByStatus(status);

        // Then
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals(1, result.getData().size());
        verify(testSessionRepository, times(1)).findByStatus(status);
    }

    @Test
    @DisplayName("Should get running sessions successfully")
    void shouldGetRunningSessionsSuccessfully() {
        // Given
        List<TestSession> sessions = Arrays.asList(testSession);
        when(testSessionRepository.findRunningSessions()).thenReturn(sessions);

        // When
        ApiResponse<List<TestSession>> result = sessionService.getRunningSessions();

        // Then
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals(1, result.getData().size());
        verify(testSessionRepository, times(1)).findRunningSessions();
    }

    @Test
    @DisplayName("Should get completed sessions successfully")
    void shouldGetCompletedSessionsSuccessfully() {
        // Given
        List<TestSession> sessions = Arrays.asList(testSession);
        when(testSessionRepository.findCompletedSessions()).thenReturn(sessions);

        // When
        ApiResponse<List<TestSession>> result = sessionService.getCompletedSessions();

        // Then
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals(1, result.getData().size());
        verify(testSessionRepository, times(1)).findCompletedSessions();
    }

    @Test
    @DisplayName("Should count active sessions successfully")
    void shouldCountActiveSessionsSuccessfully() {
        // Given
        when(testSessionRepository.countActiveSessions()).thenReturn(3L);

        // When
        ApiResponse<Long> result = sessionService.countActiveSessions();

        // Then
        assertTrue(result.isSuccess());
        assertEquals(3L, result.getData());
        verify(testSessionRepository, times(1)).countActiveSessions();
    }

    @Test
    @DisplayName("Should delete session successfully")
    void shouldDeleteSessionSuccessfully() {
        // Given
        when(testSessionRepository.findById(1L)).thenReturn(Optional.of(testSession));

        // When
        ApiResponse<String> result = sessionService.deleteSession(1L);

        // Then
        assertTrue(result.isSuccess());
        assertEquals("Session deleted successfully", result.getMessage());
        verify(testSessionRepository, times(1)).findById(1L);
        verify(testSessionRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should return error when deleting non-existent session")
    void shouldReturnErrorWhenDeletingNonExistentSession() {
        // Given
        when(testSessionRepository.findById(999L)).thenReturn(Optional.empty());

        // When
        ApiResponse<String> result = sessionService.deleteSession(999L);

        // Then
        assertFalse(result.isSuccess());
        assertEquals("Session not found", result.getMessage());
        verify(testSessionRepository, times(1)).findById(999L);
        verify(testSessionRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Should get sessions by executor successfully")
    void shouldGetSessionsByExecutorSuccessfully() {
        // Given
        List<TestSession> sessions = Arrays.asList(testSession);
        when(testSessionRepository.findByExecutor("test@example.com")).thenReturn(sessions);

        // When
        ApiResponse<List<TestSession>> result = sessionService.getSessionsByExecutor("test@example.com");

        // Then
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals(1, result.getData().size());
        verify(testSessionRepository, times(1)).findByExecutor("test@example.com");
    }

    @Test
    @DisplayName("Should get sessions by project ID successfully")
    void shouldGetSessionsByProjectIdSuccessfully() {
        // Given
        List<TestSession> sessions = Arrays.asList(testSession);
        when(testSessionRepository.findByTestTypeAndEnvironment("PROJECT_TESTING", "1"))
                .thenReturn(sessions);

        // When
        ApiResponse<List<TestSession>> result = sessionService.getSessionsByProjectId(1L);

        // Then
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals(1, result.getData().size());
        verify(testSessionRepository, times(1))
                .findByTestTypeAndEnvironment("PROJECT_TESTING", "1");
    }

    @Test
    @DisplayName("Should handle exception when getting active sessions")
    void shouldHandleExceptionWhenGettingActiveSessions() {
        // Given
        when(testSessionRepository.findByIsActiveTrue())
                .thenThrow(new RuntimeException("Database error"));

        // When
        ApiResponse<List<TestSession>> result = sessionService.getActiveSessions();

        // Then
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Failed to fetch active sessions"));
        verify(testSessionRepository, times(1)).findByIsActiveTrue();
    }

    @Test
    @DisplayName("Should handle exception when starting session fails")
    void shouldHandleExceptionWhenStartingSessionFails() {
        // Given
        when(testSessionRepository.findById(1L)).thenThrow(new RuntimeException("Database error"));

        // When
        ApiResponse<TestSession> result = sessionService.startSession(1L);

        // Then
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Failed to start session"));
        verify(testSessionRepository, times(1)).findById(1L);
    }
}