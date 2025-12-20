package ch.zhaw.learnsphere.model;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Unit tests for Progress model
 * Tests state transitions: NOT_STARTED → IN_PROGRESS → COMPLETED
 */
class ProgressTest {

    /**
     * Test NOT_STARTED state
     * When completedLessons = 0, status should be NOT_STARTED
     */
    @Test
    void testNotStarted() {
        // Arrange
        Progress progress = new Progress();
        progress.setCompletedLessons(0);
        int totalLessons = 3;

        // Act
        progress.updateStatus(totalLessons);

        // Assert
        assertEquals(ProgressStatus.NOT_STARTED, progress.getStatus(), 
            "Status should be NOT_STARTED when no lessons completed");
        assertNull(progress.getStartedAt(), 
            "startedAt should be null when not started");
        assertNull(progress.getCompletedAt(), 
            "completedAt should be null when not started");
    }

    /**
     * Test IN_PROGRESS state
     * When 0 < completedLessons < totalLessons, status should be IN_PROGRESS
     */
    @Test
    void testInProgress() {
        // Arrange
        Progress progress = new Progress();
        progress.setCompletedLessons(1);
        int totalLessons = 3;

        // Act
        progress.updateStatus(totalLessons);

        // Assert
        assertEquals(ProgressStatus.IN_PROGRESS, progress.getStatus(), 
            "Status should be IN_PROGRESS when some lessons completed");
        assertNotNull(progress.getStartedAt(), 
            "startedAt should be set when first lesson completed");
        assertNull(progress.getCompletedAt(), 
            "completedAt should be null when not finished");
    }

    /**
     * Test COMPLETED state
     * When completedLessons >= totalLessons, status should be COMPLETED
     */
    @Test
    void testCompleted() {
        // Arrange
        Progress progress = new Progress();
        progress.setCompletedLessons(3);
        int totalLessons = 3;

        // Act
        progress.updateStatus(totalLessons);

        // Assert
        assertEquals(ProgressStatus.COMPLETED, progress.getStatus(), 
            "Status should be COMPLETED when all lessons completed");
        assertNotNull(progress.getCompletedAt(), 
            "completedAt should be set when all lessons completed");
    }

    /**
     * Test startedAt timestamp is set correctly
     * Should be set when first lesson is completed (transition to IN_PROGRESS)
     */
    @Test
    void testStartedAtTimestamp() {
        // Arrange
        Progress progress = new Progress();
        progress.setCompletedLessons(0);
        int totalLessons = 3;

        // Act - start with NOT_STARTED
        progress.updateStatus(totalLessons);
        LocalDateTime beforeStart = LocalDateTime.now();
        
        // Complete first lesson
        progress.setCompletedLessons(1);
        progress.updateStatus(totalLessons);
        LocalDateTime afterStart = LocalDateTime.now();

        // Assert
        assertNotNull(progress.getStartedAt(), 
            "startedAt should be set after first lesson");
        assertTrue(
            progress.getStartedAt().isAfter(beforeStart.minusSeconds(1)) && 
            progress.getStartedAt().isBefore(afterStart.plusSeconds(1)),
            "startedAt should be set to current time"
        );
    }

    /**
     * Test completedAt timestamp is set correctly
     * Should be set when all lessons are completed (transition to COMPLETED)
     */
    @Test
    void testCompletedAtTimestamp() {
        // Arrange
        Progress progress = new Progress();
        progress.setCompletedLessons(2);
        int totalLessons = 3;
        progress.updateStatus(totalLessons); // Set to IN_PROGRESS first

        // Act
        LocalDateTime beforeComplete = LocalDateTime.now();
        progress.setCompletedLessons(3);
        progress.updateStatus(totalLessons);
        LocalDateTime afterComplete = LocalDateTime.now();

        // Assert
        assertNotNull(progress.getCompletedAt(), 
            "completedAt should be set when all lessons completed");
        assertTrue(
            progress.getCompletedAt().isAfter(beforeComplete.minusSeconds(1)) && 
            progress.getCompletedAt().isBefore(afterComplete.plusSeconds(1)),
            "completedAt should be set to current time"
        );
    }

    /**
     * Test that startedAt is not overwritten on subsequent updates
     */
    @Test
    void testStartedAtNotOverwritten() {
        // Arrange
        Progress progress = new Progress();
        progress.setCompletedLessons(1);
        int totalLessons = 3;
        
        // First update - sets startedAt
        progress.updateStatus(totalLessons);
        LocalDateTime firstStartedAt = progress.getStartedAt();
        
        // Wait a tiny bit
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Act - second update
        progress.setCompletedLessons(2);
        progress.updateStatus(totalLessons);

        // Assert
        assertEquals(firstStartedAt, progress.getStartedAt(), 
            "startedAt should not change after initial setting");
    }

    /**
     * Test that completedAt is not overwritten on subsequent updates
     */
    @Test
    void testCompletedAtNotOverwritten() {
        // Arrange
        Progress progress = new Progress();
        progress.setCompletedLessons(3);
        int totalLessons = 3;
        
        // First update - sets completedAt
        progress.updateStatus(totalLessons);
        LocalDateTime firstCompletedAt = progress.getCompletedAt();
        
        // Wait a tiny bit
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Act - second update (still completed)
        progress.updateStatus(totalLessons);

        // Assert
        assertEquals(firstCompletedAt, progress.getCompletedAt(), 
            "completedAt should not change after initial setting");
    }

    /**
     * Test state transition: NOT_STARTED → IN_PROGRESS
     */
    @Test
    void testTransitionNotStartedToInProgress() {
        // Arrange
        Progress progress = new Progress();
        progress.setCompletedLessons(0);
        int totalLessons = 3;
        progress.updateStatus(totalLessons);

        // Act
        progress.setCompletedLessons(1);
        progress.updateStatus(totalLessons);

        // Assert
        assertEquals(ProgressStatus.IN_PROGRESS, progress.getStatus(), 
            "Should transition from NOT_STARTED to IN_PROGRESS");
    }

    /**
     * Test state transition: IN_PROGRESS → COMPLETED
     */
    @Test
    void testTransitionInProgressToCompleted() {
        // Arrange
        Progress progress = new Progress();
        progress.setCompletedLessons(1);
        int totalLessons = 3;
        progress.updateStatus(totalLessons);

        // Act
        progress.setCompletedLessons(3);
        progress.updateStatus(totalLessons);

        // Assert
        assertEquals(ProgressStatus.COMPLETED, progress.getStatus(), 
            "Should transition from IN_PROGRESS to COMPLETED");
    }

    /**
     * Test state transition: IN_PROGRESS → NOT_STARTED (regression)
     * If completed lessons is reset to 0, should go back to NOT_STARTED
     */
    @Test
    void testTransitionInProgressToNotStarted() {
        // Arrange
        Progress progress = new Progress();
        progress.setCompletedLessons(2);
        int totalLessons = 3;
        progress.updateStatus(totalLessons);

        // Act - reset to 0 (e.g., course reset)
        progress.setCompletedLessons(0);
        progress.updateStatus(totalLessons);

        // Assert
        assertEquals(ProgressStatus.NOT_STARTED, progress.getStatus(), 
            "Should transition back to NOT_STARTED when lessons reset");
        assertNull(progress.getStartedAt(), 
            "startedAt should be cleared when reset");
        assertNull(progress.getCompletedAt(), 
            "completedAt should be cleared when reset");
    }

    /**
     * Parameterized test for multiple scenarios
     * CSV format: completedLessons, totalLessons, expectedStatus
     */
    @ParameterizedTest
    @CsvSource({
        "0, 3, NOT_STARTED",
        "1, 3, IN_PROGRESS",
        "2, 3, IN_PROGRESS",
        "3, 3, COMPLETED",
        "0, 5, NOT_STARTED",
        "1, 5, IN_PROGRESS",
        "3, 5, IN_PROGRESS",
        "4, 5, IN_PROGRESS",
        "5, 5, COMPLETED",
        "0, 1, NOT_STARTED",
        "1, 1, COMPLETED",
        "10, 10, COMPLETED",
        "9, 10, IN_PROGRESS"
    })
    void testStatusWithDifferentValues(int completedLessons, int totalLessons, String expectedStatus) {
        // Arrange
        Progress progress = new Progress();
        progress.setCompletedLessons(completedLessons);

        // Act
        progress.updateStatus(totalLessons);

        // Assert
        assertEquals(ProgressStatus.valueOf(expectedStatus), progress.getStatus(),
            String.format("With %d/%d lessons, status should be %s", 
                completedLessons, totalLessons, expectedStatus));
    }

    /**
     * Test percent calculation (if implemented in updateStatus)
     * Note: This test assumes percent is calculated separately
     */
    @Test
    void testPercentCalculation() {
        // Arrange
        Progress progress = new Progress();
        progress.setCompletedLessons(2);
        int totalLessons = 4;
        
        // Manually calculate percent (if your code does this)
        double expectedPercent = (2.0 / 4.0) * 100;
        progress.setPercent(expectedPercent);

        // Act
        progress.updateStatus(totalLessons);

        // Assert
        assertEquals(50.0, progress.getPercent(), 0.01, 
            "Percent should be 50% when 2/4 lessons completed");
        assertEquals(ProgressStatus.IN_PROGRESS, progress.getStatus());
    }

    /**
     * Test edge case: more completed lessons than total
     * Should still be COMPLETED
     */
    @Test
    void testMoreCompletedThanTotal() {
        // Arrange
        Progress progress = new Progress();
        progress.setCompletedLessons(5);
        int totalLessons = 3;

        // Act
        progress.updateStatus(totalLessons);

        // Assert
        assertEquals(ProgressStatus.COMPLETED, progress.getStatus(), 
            "Should be COMPLETED even if completedLessons > totalLessons");
    }

    /**
     * Test default constructor values
     */
    @Test
    void testDefaultConstructor() {
        // Act
        Progress progress = new Progress();

        // Assert
        assertEquals(ProgressStatus.NOT_STARTED, progress.getStatus(), 
            "Default status should be NOT_STARTED");
        assertEquals(0, progress.getCompletedLessons(), 
            "Default completedLessons should be 0");
    }

    /**
     * Test backward compatible constructor (without status)
     */
    @Test
    void testBackwardCompatibleConstructor() {
        // Act
        Progress progress = new Progress("id123", "course456", "student789", 2, 66.67);

        // Assert
        assertEquals(ProgressStatus.NOT_STARTED, progress.getStatus(), 
            "Status should default to NOT_STARTED in backward compatible constructor");
        assertEquals("id123", progress.getId());
        assertEquals("course456", progress.getCourseId());
        assertEquals("student789", progress.getStudentSub());
        assertEquals(2, progress.getCompletedLessons());
        assertEquals(66.67, progress.getPercent(), 0.01);
    }
}