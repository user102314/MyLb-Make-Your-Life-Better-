package MyLb.BackEnd.Model.Entites;

import MyLb.BackEnd.Model.Entities.Card;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CardTest {

    @Test
    void testCardCreation() {
        // Arrange & Act
        Card card = new Card();
        card.setCardNumber("1234567812345678"); // ✅ Pas d'ID défini manuellement
        card.setCardHolderName("John Doe");
        card.setExpiryDate("12/25");
        card.setCvv("123");
        card.setSold(1000.0);
        card.setIdClient(1L);
        card.setIsActive(true);
        card.setCardType("VISA");
        card.setDailyLimit(5000.0);

        // Assert
        assertNotNull(card);
        assertNull(card.getId()); // ✅ L'ID doit être null avant sauvegarde
        assertEquals("1234567812345678", card.getCardNumber());
        assertEquals("John Doe", card.getCardHolderName());
        assertEquals("12/25", card.getExpiryDate());
        assertEquals("123", card.getCvv());
        assertEquals(1000.0, card.getSold());
        assertEquals(1L, card.getIdClient());
        assertTrue(card.getIsActive());
        assertEquals("VISA", card.getCardType());
        assertEquals(5000.0, card.getDailyLimit());
    }

    @Test
    void testCardConstructor() {
        // Arrange & Act
        Card card = new Card("1234567812345678", "John Doe", "12/25", "123", 1L, "VISA");

        // Assert
        assertNotNull(card);
        assertNull(card.getId()); // ✅ ID doit être null
        assertEquals("1234567812345678", card.getCardNumber());
        assertEquals("John Doe", card.getCardHolderName());
        assertEquals("12/25", card.getExpiryDate());
        assertEquals("123", card.getCvv());
        assertEquals(1L, card.getIdClient());
        assertEquals("VISA", card.getCardType());
        assertEquals(0.0, card.getSold());
        assertTrue(card.getIsActive());
        assertEquals(5000.0, card.getDailyLimit());
        assertNotNull(card.getCreatedAt());
    }

    @Test
    void testCardDefaultValues() {
        // Arrange & Act
        Card card = new Card();

        // Assert
        assertNull(card.getId()); // ✅ ID null par défaut
        assertNotNull(card.getCreatedAt());
        assertEquals(0.0, card.getSold());
        assertTrue(card.getIsActive());
        assertEquals(5000.0, card.getDailyLimit());
    }

    @Test
    void testCardToString() {
        // Arrange
        Card card = new Card("1234567812345678", "John Doe", "12/25", "123", 1L, "VISA");

        // Act
        String toString = card.toString();

        // Assert
        assertNotNull(toString);
        assertTrue(toString.contains("1234********5678"));
        assertTrue(toString.contains("John Doe"));
        assertTrue(toString.contains("VISA"));
    }

    @Test
    void testCardLastUsed() {
        // Arrange
        Card card = new Card();
        LocalDateTime now = LocalDateTime.now();

        // Act
        card.setLastUsed(now);

        // Assert
        assertEquals(now, card.getLastUsed());
    }
}