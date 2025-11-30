package MyLb.BackEnd.Controller;

import MyLb.BackEnd.Service.CardService;
import MyLb.BackEnd.dto.CardOperationRequest;
import MyLb.BackEnd.dto.CardResponse;
import MyLb.BackEnd.dto.CreateCardRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpSession;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardControllerTest {

    @Mock
    private CardService cardService;

    @InjectMocks
    private CardController cardController;

    private MockHttpSession session;
    private CardResponse testCardResponse;

    @BeforeEach
    void setUp() {
        session = new MockHttpSession();
        session.setAttribute("USER_ID", 1L);

        testCardResponse = new CardResponse(
                1L, "1234********5678", "John Doe", "12/25",
                1000.0, 1L, true, "VISA", 5000.0
        );
    }

    @Test
    void testAddCard_Success() {
        // Arrange
        CreateCardRequest request = new CreateCardRequest();
        request.setCardNumber("1234567812345678");
        request.setCardHolderName("John Doe");
        request.setExpiryDate("12/25");
        request.setCvv("123");
        request.setCardType("VISA");

        when(cardService.addCard(any())).thenReturn(testCardResponse);

        // Act
        ResponseEntity<?> response = cardController.addCard(session, request);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(cardService, times(1)).addCard(any());
    }

    @Test
    void testAddCard_InvalidCardNumber() {
        // Arrange
        CreateCardRequest request = new CreateCardRequest();
        request.setCardNumber("123"); // Numéro invalide
        request.setCardHolderName("John Doe");
        request.setExpiryDate("12/25");
        request.setCvv("123");
        request.setCardType("VISA");

        // Act
        ResponseEntity<?> response = cardController.addCard(session, request);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        verify(cardService, never()).addCard(any());
    }

    @Test
    void testAddCard_Unauthenticated() {
        // Arrange
        session.clearAttributes();
        CreateCardRequest request = new CreateCardRequest();

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            cardController.addCard(session, request);
        });
    }

    @Test
    void testCheckSold_Success() {
        // Arrange
        when(cardService.checkSold(1L)).thenReturn(1000.0);

        // Act
        ResponseEntity<?> response = cardController.checkSold(session, 1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(cardService, times(1)).checkSold(1L);
    }

    @Test
    void testAddSold_Success() {
        // Arrange
        CardOperationRequest request = new CardOperationRequest();
        request.setMontant(500.0);

        when(cardService.addSold(1L, 500.0)).thenReturn(testCardResponse);

        // Act
        ResponseEntity<?> response = cardController.addSold(session, 1L, request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(cardService, times(1)).addSold(1L, 500.0);
    }

    @Test
    void testWithdrawSold_Success() {
        // Arrange
        CardOperationRequest request = new CardOperationRequest();
        request.setMontant(200.0);

        when(cardService.withdrawSold(1L, 200.0)).thenReturn(testCardResponse);

        // Act
        ResponseEntity<?> response = cardController.withdrawSold(session, 1L, request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(cardService, times(1)).withdrawSold(1L, 200.0);
    }

    @Test
    void testGetCardsByClient_Success() {
        // Arrange
        List<CardResponse> cards = Arrays.asList(testCardResponse);
        when(cardService.getCardsByClient(1L)).thenReturn(cards);

        // Act
        ResponseEntity<?> response = cardController.getCardsByClient(session);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(cardService, times(1)).getCardsByClient(1L);
    }

    @Test
    void testDeactivateCard_Success() {
        // Arrange
        when(cardService.deactivateCard(1L)).thenReturn(testCardResponse);

        // Act
        ResponseEntity<?> response = cardController.deactivateCard(session, 1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(cardService, times(1)).deactivateCard(1L);
    }

    @Test
    void testTransferToWallet_Success() {
        // Arrange
        CardOperationRequest request = new CardOperationRequest();
        request.setMontant(300.0);

        when(cardService.transferToWallet(1L, 300.0)).thenReturn(testCardResponse);

        // Act
        ResponseEntity<?> response = cardController.transferToWallet(session, 1L, request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(cardService, times(1)).transferToWallet(1L, 300.0);
    }

    @Test
    void testTransferFromWallet_Success() {
        // Arrange
        CardOperationRequest request = new CardOperationRequest();
        request.setMontant(400.0);

        when(cardService.transferFromWallet(1L, 400.0)).thenReturn(testCardResponse);

        // Act
        ResponseEntity<?> response = cardController.transferFromWallet(session, 1L, request);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(cardService, times(1)).transferFromWallet(1L, 400.0);
    }

    @Test
    void testHealthCheck() {
        // Act
        ResponseEntity<?> response = cardController.healthCheck();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}