package MyLb.BackEnd.Repository;

import MyLb.BackEnd.Model.Entities.Client;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ClientRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ClientRepository clientRepository;

    @Test
    void testFindByEmailAndPassword_Success() {
        // Arrange
        Client client = new Client();
        client.setFirstName("Test");
        client.setLastName("User");
        client.setEmail("test@example.com");
        client.setPassword("password123");

        entityManager.persist(client);
        entityManager.flush();

        // Act
        Optional<Client> result = clientRepository.findByEmailAndPassword("test@example.com", "password123");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("test@example.com", result.get().getEmail());
    }

    @Test
    void testFindByEmailAndPassword_WrongPassword() {
        // Arrange
        Client client = new Client();
        client.setEmail("test@example.com");
        client.setPassword("password123");
        entityManager.persist(client);
        entityManager.flush();

        // Act
        Optional<Client> result = clientRepository.findByEmailAndPassword("test@example.com", "wrongpassword");

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testFindByEmail_Success() {
        // Arrange
        Client client = new Client();
        client.setEmail("test@example.com");
        entityManager.persist(client);
        entityManager.flush();

        // Act
        Optional<Client> result = clientRepository.findByEmail("test@example.com");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("test@example.com", result.get().getEmail());
    }

    @Test
    void testFindByEmail_NotFound() {
        // Act
        Optional<Client> result = clientRepository.findByEmail("nonexistent@example.com");

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testCountByIsVerifiedTrue() {
        // Arrange
        Client verifiedClient = new Client();
        verifiedClient.setEmail("verified@example.com");
        verifiedClient.setIsVerified(true);
        entityManager.persist(verifiedClient);

        Client unverifiedClient = new Client();
        unverifiedClient.setEmail("unverified@example.com");
        unverifiedClient.setIsVerified(false);
        entityManager.persist(unverifiedClient);

        entityManager.flush();

        // Act
        Long count = clientRepository.countByIsVerifiedTrue();

        // Assert
        assertEquals(1L, count);
    }
}