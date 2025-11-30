package MyLb.BackEnd.ServiceImp;

import MyLb.BackEnd.Model.Entities.Client;
import MyLb.BackEnd.Model.Entities.SelfDetail;
import MyLb.BackEnd.Model.Estnum.ActionType;
import MyLb.BackEnd.Repository.ClientRepository;
import MyLb.BackEnd.Service.ClientActionService;
import MyLb.BackEnd.Service.ClientSecurityService;
import MyLb.BackEnd.Service.GoogleAuthService;
import MyLb.BackEnd.Service.WalletService;
import MyLb.BackEnd.dto.PasswordChangeRequest;
import MyLb.BackEnd.dto.WalletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceImplTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ClientSecurityService clientSecurityService;

    @Mock
    private GoogleAuthService googleAuthService;

    @Mock
    private ClientActionService clientActionService;

    @Mock
    private WalletService walletService;

    @InjectMocks
    private ClientServiceImpl clientService;

    private Client testClient;

    @BeforeEach
    void setUp() {
        testClient = new Client();
        testClient.setClientId(1L);
        testClient.setFirstName("John");
        testClient.setLastName("Doe");
        testClient.setEmail("john.doe@example.com");
        testClient.setPassword("password123");
    }

    @Test
    void testAuthenticate_Success() {
        // Arrange
        String email = "john.doe@example.com";
        String password = "password123";
        when(clientRepository.findByEmail(email)).thenReturn(Optional.of(testClient));

        // Act
        Long result = clientService.authenticate(email, password);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result);
        verify(clientActionService, times(1)).logAction(1L, ActionType.LOGIN_SUCCESS, "Connexion réussie.");
    }

    @Test
    void testAuthenticate_WrongPassword() {
        // Arrange
        String email = "john.doe@example.com";
        String wrongPassword = "wrongpassword";
        when(clientRepository.findByEmail(email)).thenReturn(Optional.of(testClient));

        // Act
        Long result = clientService.authenticate(email, wrongPassword);

        // Assert
        assertNull(result);
        verify(clientActionService, times(1)).logAction(1L, ActionType.SECURITY_ALERT, "Tentative de connexion échouée (Mot de passe incorrect).");
    }

    @Test
    void testAuthenticate_UserNotFound() {
        // Arrange
        String email = "nonexistent@example.com";
        String password = "password123";
        when(clientRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act
        Long result = clientService.authenticate(email, password);

        // Assert
        assertNull(result);
        verify(clientActionService, never()).logAction(anyLong(), any(ActionType.class), anyString());
    }

    @Test
    void testSaveClientWithWallet_Success() {
        // Arrange
        WalletResponse walletResponse = new WalletResponse(); // Créer un WalletResponse mock
        when(clientRepository.save(testClient)).thenReturn(testClient);
        when(walletService.createWalletIfNotExists(1L)).thenReturn(walletResponse); // ✅ CORRECTION

        // Act
        Client result = clientService.saveClientWithWallet(testClient);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getClientId());
        verify(clientRepository, times(1)).save(testClient);
        verify(walletService, times(1)).createWalletIfNotExists(1L);
    }
    @Test
    void testIsEmailUnique_EmailAvailable() {
        // Arrange
        String email = "new@example.com";
        when(clientRepository.findByEmail(email)).thenReturn(Optional.empty());

        // Act
        boolean result = clientService.isEmailUnique(email);

        // Assert
        assertTrue(result);
    }

    @Test
    void testIsEmailUnique_EmailTaken() {
        // Arrange
        String email = "existing@example.com";
        when(clientRepository.findByEmail(email)).thenReturn(Optional.of(testClient));

        // Act
        boolean result = clientService.isEmailUnique(email);

        // Assert
        assertFalse(result);
    }

    @Test
    void testGetClientById_Found() {
        // Arrange
        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));

        // Act
        Optional<Client> result = clientService.getClientById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("John", result.get().getFirstName());
    }

    @Test
    void testGetClientById_NotFound() {
        // Arrange
        when(clientRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<Client> result = clientService.getClientById(999L);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void testChangePassword_Success() {
        // Arrange
        PasswordChangeRequest request = new PasswordChangeRequest();
        request.setCurrentPassword("password123");
        request.setNewPassword("newpassword123");

        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));
        when(clientSecurityService.is2FaEnabled(1L)).thenReturn(false);
        when(clientRepository.save(any(Client.class))).thenReturn(testClient);

        // Act
        boolean result = clientService.changePassword(1L, request);

        // Assert
        assertTrue(result);
        verify(clientActionService, times(1)).logAction(1L, ActionType.PASSWORD_CHANGE, "Le mot de passe a été modifié avec succès.");
    }

    @Test
    void testChangePassword_WrongCurrentPassword() {
        // Arrange
        PasswordChangeRequest request = new PasswordChangeRequest();
        request.setCurrentPassword("wrongpassword");
        request.setNewPassword("newpassword123");

        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));

        // Act
        boolean result = clientService.changePassword(1L, request);

        // Assert
        assertFalse(result);
        verify(clientActionService, times(1)).logAction(1L, ActionType.SECURITY_ALERT, "Tentative de changement de mot de passe échouée (ancien mot de passe invalide).");
    }

    @Test
    void testChangePassword_With2FA_Success() {
        // Arrange
        PasswordChangeRequest request = new PasswordChangeRequest();
        request.setCurrentPassword("password123");
        request.setNewPassword("newpassword123");
        request.setAuthCode("123456");

        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));
        when(clientSecurityService.is2FaEnabled(1L)).thenReturn(true);
        when(clientSecurityService.getGoogleAuthSecret(1L)).thenReturn(Optional.of("secret"));
        when(googleAuthService.isCodeValid("secret", 123456)).thenReturn(true);
        when(clientRepository.save(any(Client.class))).thenReturn(testClient);

        // Act
        boolean result = clientService.changePassword(1L, request);

        // Assert
        assertTrue(result);
        verify(clientActionService, times(1)).logAction(1L, ActionType.PASSWORD_CHANGE, "Le mot de passe a été modifié avec succès.");
    }

    @Test
    void testUpdateUserVerification_Success() {
        // Arrange
        when(clientRepository.findById(1L)).thenReturn(Optional.of(testClient));
        when(clientRepository.save(any(Client.class))).thenReturn(testClient);

        // Act
        Client result = clientService.updateUserVerification(1L, true);

        // Assert
        assertNotNull(result);
        assertTrue(result.getIsVerified());
    }
}