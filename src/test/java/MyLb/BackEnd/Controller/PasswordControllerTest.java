package MyLb.BackEnd.Controller;

import MyLb.BackEnd.Service.ClientService;
import MyLb.BackEnd.dto.PasswordChangeRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpSession;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordControllerTest {

    @Mock
    private ClientService clientService;

    @InjectMocks
    private PasswordController passwordController;

    private MockHttpSession session;
    private PasswordChangeRequest passwordChangeRequest;

    @BeforeEach
    void setUp() {
        session = new MockHttpSession();
        session.setAttribute("USER_ID", 1L);

        passwordChangeRequest = new PasswordChangeRequest();
        passwordChangeRequest.setCurrentPassword("oldPassword");
        passwordChangeRequest.setNewPassword("newPassword");
    }

    @Test
    void testChangePassword_Success() {
        // Arrange
        when(clientService.changePassword(1L, passwordChangeRequest)).thenReturn(true);

        // Act
        ResponseEntity<?> response = passwordController.changePassword(passwordChangeRequest, session);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Mot de passe mis à jour avec succès.", response.getBody());
        verify(clientService, times(1)).changePassword(1L, passwordChangeRequest);
    }

    @Test
    void testChangePassword_WrongCurrentPassword() {
        // Arrange
        when(clientService.changePassword(1L, passwordChangeRequest)).thenReturn(false);

        // Act
        ResponseEntity<?> response = passwordController.changePassword(passwordChangeRequest, session);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("L'ancien mot de passe est incorrect.", response.getBody());
        verify(clientService, times(1)).changePassword(1L, passwordChangeRequest);
    }

    @Test
    void testChangePassword_Unauthenticated() {
        // Arrange
        session.clearAttributes();

        // Act
        ResponseEntity<?> response = passwordController.changePassword(passwordChangeRequest, session);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Utilisateur non authentifié.", response.getBody());
        verify(clientService, never()).changePassword(anyLong(), any());
    }

    @Test
    void testChangePassword_UserNotFound() {
        // Arrange
        when(clientService.changePassword(1L, passwordChangeRequest))
                .thenThrow(new RuntimeException("User not found"));

        // Act
        ResponseEntity<?> response = passwordController.changePassword(passwordChangeRequest, session);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found", response.getBody());
        verify(clientService, times(1)).changePassword(1L, passwordChangeRequest);
    }

    @Test
    void testChangePassword_ServiceException() {
        // Arrange
        when(clientService.changePassword(1L, passwordChangeRequest))
                .thenThrow(new RuntimeException("Database error"));

        // Act
        ResponseEntity<?> response = passwordController.changePassword(passwordChangeRequest, session);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Database error", response.getBody());
    }
}