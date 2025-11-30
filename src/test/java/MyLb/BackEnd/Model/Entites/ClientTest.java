package MyLb.BackEnd.Model.Entites;


import MyLb.BackEnd.Model.Entities.Client;
import MyLb.BackEnd.Model.Entities.ClientSecurity;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ClientTest {

    @Test
    void testClientCreation() {
        // Arrange & Act
        Client client = new Client();
        client.setClientId(1L);
        client.setFirstName("John");
        client.setLastName("Doe");
        client.setEmail("john.doe@example.com");
        client.setPassword("password123");
        client.setBirthDate(LocalDate.of(1990, 1, 1));
        client.setRole("USER");
        client.setIsVerified(true);

        // Assert
        assertNotNull(client);
        assertEquals(1L, client.getClientId());
        assertEquals("John", client.getFirstName());
        assertEquals("Doe", client.getLastName());
        assertEquals("john.doe@example.com", client.getEmail());
        assertEquals("password123", client.getPassword());
        assertEquals(LocalDate.of(1990, 1, 1), client.getBirthDate());
        assertEquals("USER", client.getRole());
        assertTrue(client.getIsVerified());
        assertTrue(client.isVerified());
    }

    @Test
    void testClientDefaultValues() {
        // Arrange & Act
        Client client = new Client();

        // Assert
        assertNotNull(client.getCreatedAt());
        assertFalse(client.getIsVerified());
        assertNotNull(client.getClientSecurity());
    }

    @Test
    void testProfileImageHandling() {
        // Arrange
        Client client = new Client();
        byte[] imageData = new byte[]{1, 2, 3, 4, 5};

        // Act
        client.setProfileImage(imageData);

        // Assert
        assertArrayEquals(imageData, client.getProfileImage());
    }

    @Test
    void testIdentityDocuments() {
        // Arrange
        Client client = new Client();
        byte[] recto = new byte[]{1, 2, 3};
        byte[] verso = new byte[]{4, 5, 6};
        byte[] selfie = new byte[]{7, 8, 9};

        // Act
        client.setPhotocinRecto(recto);
        client.setPhotocinVerso(verso);
        client.setPhotocompletSelfie(selfie);
        client.setIdentityStatus("VERIFIED");

        // Assert
        assertArrayEquals(recto, client.getPhotocinRecto());
        assertArrayEquals(verso, client.getPhotocinVerso());
        assertArrayEquals(selfie, client.getPhotocompletSelfie());
        assertEquals("VERIFIED", client.getIdentityStatus());
    }

    @Test
    void testClientSecurityAssociation() {
        // Arrange
        Client client = new Client();
        ClientSecurity security = new ClientSecurity();

        // Act
        client.setClientSecurity(security);

        // Assert
        assertNotNull(client.getClientSecurity());
        assertEquals(client, security.getClient());
    }
}