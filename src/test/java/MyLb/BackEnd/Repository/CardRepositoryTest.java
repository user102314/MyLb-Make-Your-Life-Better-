package MyLb.BackEnd.Repository;

import MyLb.BackEnd.Model.Entities.Card;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CardRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CardRepository cardRepository;

    @Test
    void testFindByIdClient() {
        // Arrange - NE PAS définir l'ID manuellement
        Card card = createTestCard("1234567812345678", 1L);
        Card savedCard = entityManager.persistAndFlush(card); // ✅ Utiliser persistAndFlush

        // Act
        List<Card> cards = cardRepository.findByIdClient(1L);

        // Assert
        assertFalse(cards.isEmpty());
        assertEquals(1, cards.size());
        assertEquals("1234567812345678", cards.get(0).getCardNumber());
    }

    @Test
    void testFindByCardNumber() {
        // Arrange
        Card card = createTestCard("1234567812345678", 1L);
        Card savedCard = entityManager.persistAndFlush(card);

        // Act
        Optional<Card> found = cardRepository.findByCardNumber("1234567812345678");

        // Assert
        assertTrue(found.isPresent());
        assertEquals("1234567812345678", found.get().getCardNumber());
        assertEquals(savedCard.getId(), found.get().getId()); // Vérifier l'ID généré
    }

    @Test
    void testExistsByCardNumber() {
        // Arrange
        Card card = createTestCard("1234567812345678", 1L);
        entityManager.persistAndFlush(card);

        // Act
        boolean exists = cardRepository.existsByCardNumber("1234567812345678");
        boolean notExists = cardRepository.existsByCardNumber("9999999999999999");

        // Assert
        assertTrue(exists);
        assertFalse(notExists);
    }

    @Test
    void testFindByIdClientAndIsActiveTrue() {
        // Arrange
        Card activeCard = createTestCard("1234567812345678", 1L);
        Card inactiveCard = createTestCard("8765432187654321", 1L);
        inactiveCard.setIsActive(false);

        entityManager.persistAndFlush(activeCard);
        entityManager.persistAndFlush(inactiveCard);

        // Act
        List<Card> activeCards = cardRepository.findByIdClientAndIsActiveTrue(1L);

        // Assert
        assertEquals(1, activeCards.size());
        assertTrue(activeCards.get(0).getIsActive());
        assertEquals("1234567812345678", activeCards.get(0).getCardNumber());
    }

    @Test
    void testFindSoldById() {
        // Arrange
        Card card = createTestCard("1234567812345678", 1L);
        card.setSold(1500.0);
        Card savedCard = entityManager.persistAndFlush(card);

        // Act
        Optional<Double> solde = cardRepository.findSoldById(savedCard.getId());

        // Assert
        assertTrue(solde.isPresent());
        assertEquals(1500.0, solde.get());
    }

    @Test
    void testFindSoldById_CardNotFound() {
        // Act
        Optional<Double> solde = cardRepository.findSoldById(999L);

        // Assert
        assertFalse(solde.isPresent());
    }

    @Test
    void testFindSoldByCardNumber() {
        // Arrange
        Card card = createTestCard("1234567812345678", 1L);
        card.setSold(2000.0);
        entityManager.persistAndFlush(card);

        // Act
        Optional<Double> solde = cardRepository.findSoldByCardNumber("1234567812345678");

        // Assert
        assertTrue(solde.isPresent());
        assertEquals(2000.0, solde.get());
    }

    @Test
    void testFindSoldByCardNumber_CardNotFound() {
        // Act
        Optional<Double> solde = cardRepository.findSoldByCardNumber("9999999999999999");

        // Assert
        assertFalse(solde.isPresent());
    }

    @Test
    void testSaveCard() {
        // Arrange
        Card card = createTestCard("1111222233334444", 2L);

        // Act
        Card savedCard = cardRepository.save(card);

        // Assert
        assertNotNull(savedCard.getId());
        assertEquals("1111222233334444", savedCard.getCardNumber());
        assertEquals(2L, savedCard.getIdClient());
        assertTrue(savedCard.getIsActive());
    }

    private Card createTestCard(String cardNumber, Long clientId) {
        Card card = new Card();
        // ✅ NE PAS définir card.setId() - laisser Hibernate le générer
        card.setCardNumber(cardNumber);
        card.setCardHolderName("Test User");
        card.setExpiryDate("12/25");
        card.setCvv("123");
        card.setSold(1000.0);
        card.setIdClient(clientId);
        card.setIsActive(true);
        card.setCardType("VISA");
        card.setDailyLimit(5000.0);
        return card;
    }
}