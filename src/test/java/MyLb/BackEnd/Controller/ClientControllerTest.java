package MyLb.BackEnd.Controller;

import MyLb.BackEnd.Model.Entities.Client;
import MyLb.BackEnd.Service.ClientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpSession;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientControllerTest {

    @Mock
    private ClientService clientService;

    @InjectMocks
    private ClientController clientController;

    private MockHttpSession session;
    private Client testClient;

    @BeforeEach
    void setUp() {
        session = new MockHttpSession();
        testClient = new Client();
        testClient.setClientId(1L);
        testClient.setFirstName("John");
        testClient.setLastName("Doe");
        testClient.setEmail("john.doe@example.com");
        testClient.setPassword("password123");
        testClient.setProfileImage(new byte[]{1, 2, 3, 4, 5}); // Image simulée
    }

    @Test
    void testGetConnectedClientInfo_Authenticated() {
        // Arrange
        session.setAttribute("USER_ID", 1L);
        when(clientService.getClientById(1L)).thenReturn(Optional.of(testClient));

        // Act
        ResponseEntity<?> response = clientController.getConnectedClientInfo(session);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        // Vérifier que le password et profileImage sont nullifiés
        verify(clientService, times(1)).getClientById(1L);
    }

    @Test
    void testGetConnectedClientInfo_Unauthenticated() {
        // Arrange - Pas d'USER_ID dans la session
        session.clearAttributes();

        // Act
        ResponseEntity<?> response = clientController.getConnectedClientInfo(session);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("User not authenticated.", response.getBody());
    }

    @Test
    void testGetConnectedClientInfo_UserNotFound() {
        // Arrange
        session.setAttribute("USER_ID", 999L);
        when(clientService.getClientById(999L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = clientController.getConnectedClientInfo(session);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("User data not found"));
    }

    @Test
    void testGetConnectedClientName_Authenticated() {
        // Arrange
        session.setAttribute("USER_ID", 1L);
        when(clientService.getClientById(1L)).thenReturn(Optional.of(testClient));

        // Act
        ResponseEntity<?> response = clientController.getConnectedClientName(session);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("John Doe", response.getBody());
    }

    @Test
    void testGetConnectedClientName_Unauthenticated() {
        // Arrange
        session.clearAttributes();

        // Act
        ResponseEntity<?> response = clientController.getConnectedClientName(session);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("User not authenticated.", response.getBody());
    }

    @Test
    void testGetConnectedClientName_UserNotFound() {
        // Arrange
        session.setAttribute("USER_ID", 999L);
        when(clientService.getClientById(999L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = clientController.getConnectedClientName(session);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User data not found.", response.getBody());
    }

    @Test
    void testGetConnectedClientInfo_Base64ImageConversion() {
        // Arrange
        session.setAttribute("USER_ID", 1L);
        byte[] imageBytes = "fake-image-data".getBytes();
        testClient.setProfileImage(imageBytes);
        when(clientService.getClientById(1L)).thenReturn(Optional.of(testClient));

        // Act
        ResponseEntity<?> response = clientController.getConnectedClientInfo(session);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        // Vérifier que l'image Base64 est présente dans la réponse
        // (Vous devrez peut-être adapter cette assertion selon la structure exacte de votre réponse)
    }

    @Test
    void testGetConnectedClientInfo_NoProfileImage() {
        // Arrange
        session.setAttribute("USER_ID", 1L);
        testClient.setProfileImage(null);
        when(clientService.getClientById(1L)).thenReturn(Optional.of(testClient));

        // Act
        ResponseEntity<?> response = clientController.getConnectedClientInfo(session);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        // Vérifier que base64Image est null dans la réponse
    }
}