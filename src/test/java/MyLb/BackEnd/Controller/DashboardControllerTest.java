package MyLb.BackEnd.Controller;

import MyLb.BackEnd.Model.Entities.CheckVerification;
import MyLb.BackEnd.Repository.ClientRepository;
import MyLb.BackEnd.Service.CompanyService;
import MyLb.BackEnd.Service.CheckVerificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardControllerTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private CompanyService companyService;

    @Mock
    private CheckVerificationService checkVerificationService;

    @InjectMocks
    private DashboardController dashboardController;

    private CheckVerification verifiedVerification;
    private CheckVerification pendingVerification;

    @BeforeEach
    void setUp() {
        verifiedVerification = new CheckVerification();
        verifiedVerification.setEtat1(true);
        verifiedVerification.setEtat2(true);
        verifiedVerification.setEtat3(true);
        verifiedVerification.setEtat4(true);

        pendingVerification = new CheckVerification();
        pendingVerification.setEtat1(true);
        pendingVerification.setEtat2(true);
        pendingVerification.setEtat3(false);
        pendingVerification.setEtat4(false);
    }

    @Test
    void testGetDashboardStats_Success() {
        // Arrange
        when(clientRepository.count()).thenReturn(100L);
        when(companyService.getTotalCompanies()).thenReturn(50L);
        when(clientRepository.countByIsVerifiedTrue()).thenReturn(80L);

        List<CheckVerification> verifications = Arrays.asList(verifiedVerification, pendingVerification);
        when(checkVerificationService.getAllCheckVerifications()).thenReturn(verifications);

        // Act
        ResponseEntity<?> response = dashboardController.getDashboardStats("month");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertTrue((Boolean) responseBody.get("success"));

        @SuppressWarnings("unchecked")
        Map<String, Object> stats = (Map<String, Object>) responseBody.get("stats");
        assertEquals(100L, stats.get("totalUsers"));
        assertEquals(50L, stats.get("totalCompanies"));
        assertEquals(80L, stats.get("verifiedUsers"));
        assertEquals(1L, stats.get("pendingVerification"));
        assertEquals(1L, stats.get("fullyVerifiedUsers"));
    }

    @Test
    void testGetDashboardStats_EmptyVerifications() {
        // Arrange
        when(clientRepository.count()).thenReturn(100L);
        when(companyService.getTotalCompanies()).thenReturn(50L);
        when(clientRepository.countByIsVerifiedTrue()).thenReturn(80L);
        when(checkVerificationService.getAllCheckVerifications()).thenReturn(Arrays.asList());

        // Act
        ResponseEntity<?> response = dashboardController.getDashboardStats("month");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertTrue((Boolean) responseBody.get("success"));
    }

    @Test
    void testGetDashboardStats_Exception() {
        // Arrange
        when(clientRepository.count()).thenThrow(new RuntimeException("Database error"));

        // Act
        ResponseEntity<?> response = dashboardController.getDashboardStats("month");

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertFalse((Boolean) responseBody.get("success"));
        assertNotNull(responseBody.get("error"));
    }

    @Test
    void testGetDashboardStats_DifferentRanges() {
        // Arrange
        when(clientRepository.count()).thenReturn(100L);
        when(companyService.getTotalCompanies()).thenReturn(50L);
        when(clientRepository.countByIsVerifiedTrue()).thenReturn(80L);
        when(checkVerificationService.getAllCheckVerifications()).thenReturn(Arrays.asList(verifiedVerification));

        // Test different ranges
        String[] ranges = {"today", "week", "month", "all"};

        for (String range : ranges) {
            // Act
            ResponseEntity<?> response = dashboardController.getDashboardStats(range);

            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());

            @SuppressWarnings("unchecked")
            Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
            assertTrue((Boolean) responseBody.get("success"));
        }
    }
}