package MyLb.BackEnd.ServiceImp;

import MyLb.BackEnd.Model.Entities.Card;
import MyLb.BackEnd.Model.Entities.Transaction;
import MyLb.BackEnd.Repository.CardRepository;
import MyLb.BackEnd.Service.TransactionService;
import MyLb.BackEnd.Service.WalletService;
import MyLb.BackEnd.dto.CardResponse;
import MyLb.BackEnd.dto.WalletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceImplTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private TransactionService transactionService;

    @Mock
    private WalletService walletService;

    @InjectMocks
    private CardServiceImpl cardService;

    private Card testCard;
    private WalletResponse walletResponse;

    @BeforeEach
    void setUp() {
        testCard = new Card();
        testCard.setId(1L);
        testCard.setCardNumber("1234567812345678");
        testCard.setCardHolderName("John Doe");
        testCard.setExpiryDate("12/25");
        testCard.setCvv("123");
        testCard.setSold(1000.0);
        testCard.setIdClient(1L);
        testCard.setIsActive(true);
        testCard.setCardType("VISA");
        testCard.setDailyLimit(5000.0);

        walletResponse = new WalletResponse();
    }

    // ... autres tests restent les mêmes ...

    @Test
    void testGetCardTransactions_Success() {
        // Arrange
        when(cardRepository.findById(1L)).thenReturn(Optional.of(testCard));

        Transaction transaction = new Transaction();
        transaction.setIdCarte(1L);
        transaction.setDescription("Transaction carte 1234********5678");

        List<Transaction> transactions = Arrays.asList(transaction);
        when(transactionService.getClientTransactions(1L)).thenReturn(transactions);

        // Act
        List<Object> cardTransactions = cardService.getCardTransactions(1L);

        // Assert
        assertNotNull(cardTransactions);
        assertEquals(1, cardTransactions.size());
        verify(transactionService, times(1)).getClientTransactions(1L);
    }

    @Test
    void testGetCardTransactions_EmptyList() {
        // Arrange
        when(cardRepository.findById(1L)).thenReturn(Optional.of(testCard));
        when(transactionService.getClientTransactions(1L)).thenReturn(Collections.emptyList());

        // Act
        List<Object> cardTransactions = cardService.getCardTransactions(1L);

        // Assert
        assertNotNull(cardTransactions);
        assertTrue(cardTransactions.isEmpty());
    }

    @Test
    void testGetCardTransactions_CardNotFound() {
        // Arrange
        when(cardRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            cardService.getCardTransactions(1L);
        });
    }

    // Ajouter cette méthode utilitaire si nécessaire
    private Card createValidTestCard() {
        Card card = new Card();
        card.setCardNumber("1234567812345678");
        card.setCardHolderName("Test User");
        card.setExpiryDate("12/30"); // Date future valide
        card.setCvv("123");
        card.setSold(1000.0);
        card.setIdClient(1L);
        card.setIsActive(true);
        card.setCardType("VISA");
        card.setDailyLimit(5000.0);
        return card;
    }
}