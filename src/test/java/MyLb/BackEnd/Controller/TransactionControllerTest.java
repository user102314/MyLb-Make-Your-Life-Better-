package MyLb.BackEnd.Controller;

import MyLb.BackEnd.Model.Entities.Transaction;
import MyLb.BackEnd.Service.TransactionService;
import MyLb.BackEnd.dto.TransactionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpSession;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionController transactionController;

    private MockHttpSession session;
    private Transaction testTransaction;

    @BeforeEach
    void setUp() {
        session = new MockHttpSession();
        session.setAttribute("USER_ID", 1L);

        testTransaction = new Transaction();
        testTransaction.setId(1L);
        testTransaction.setIdClient(1L);
        testTransaction.setTypeOperation("DEPOSIT");
        testTransaction.setMontant(1000.0);
        testTransaction.setDescription("Test transaction");
        testTransaction.setStatut("COMPLETED");
        testTransaction.setDateCreation(LocalDateTime.now());
        testTransaction.setSoldeApresOperation(2000.0);
    }

    @Test
    void testGetClientTransactions_Success() {
        // Arrange
        List<Transaction> transactions = Arrays.asList(testTransaction);
        when(transactionService.getClientTransactions(1L)).thenReturn(transactions);

        // Act
        ResponseEntity<List<TransactionResponse>> response = transactionController.getClientTransactions(session);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(transactionService, times(1)).getClientTransactions(1L);
    }

    @Test
    void testGetClientTransactions_Unauthenticated() {
        // Arrange
        session.clearAttributes();

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            transactionController.getClientTransactions(session);
        });
    }

    @Test
    void testGetRecentTransactions_Success() {
        // Arrange
        List<Transaction> transactions = Arrays.asList(testTransaction);
        when(transactionService.getRecentTransactions(1L)).thenReturn(transactions);

        // Act
        ResponseEntity<List<TransactionResponse>> response = transactionController.getRecentTransactions(session);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(transactionService, times(1)).getRecentTransactions(1L);
    }

    @Test
    void testGetTransactionStats_Success() {
        // Arrange
        when(transactionService.getTotalDeposits(1L)).thenReturn(5000.0);
        when(transactionService.getTotalWithdrawals(1L)).thenReturn(2000.0);

        // Act
        ResponseEntity<Map<String, Object>> response = transactionController.getTransactionStats(session);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        Map<String, Object> stats = response.getBody();
        assertEquals(5000.0, stats.get("totalDeposits"));
        assertEquals(2000.0, stats.get("totalWithdrawals"));
        assertEquals(3000.0, stats.get("netFlow"));
    }

    @Test
    void testGetTransactionsByType_Success() {
        // Arrange
        List<Transaction> transactions = Arrays.asList(testTransaction);
        when(transactionService.getTransactionsByType(1L, "DEPOSIT")).thenReturn(transactions);

        // Act
        ResponseEntity<List<TransactionResponse>> response = transactionController.getTransactionsByType(session, "DEPOSIT");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(transactionService, times(1)).getTransactionsByType(1L, "DEPOSIT");
    }

    @Test
    void testGetAllStockTransactions_Success() {
        // Arrange
        Transaction stockTransaction = new Transaction();
        stockTransaction.setId(1L);
        stockTransaction.setDescription("Achat de 100 actions Apple à 150.0 DT");

        List<Transaction> allTransactions = Arrays.asList(stockTransaction, testTransaction);
        when(transactionService.getAllTransactions()).thenReturn(allTransactions);

        // Act
        ResponseEntity<?> response = transactionController.getAllStockTransactions();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertNotNull(responseBody.get("stockTransactions"));
        assertNotNull(responseBody.get("topStocks"));
    }

    @Test
    void testHealthCheck() {
        // Act
        ResponseEntity<Map<String, String>> response = transactionController.healthCheck();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("UP", response.getBody().get("status"));
    }
}