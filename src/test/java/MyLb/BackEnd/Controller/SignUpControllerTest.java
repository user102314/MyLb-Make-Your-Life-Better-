package MyLb.BackEnd.Controller;

import MyLb.BackEnd.Model.Entities.Client;
import MyLb.BackEnd.Model.Entities.SelfDetail;
import MyLb.BackEnd.Model.Entities.CheckVerification;
import MyLb.BackEnd.Service.ClientService;
import MyLb.BackEnd.Service.CheckVerificationService;
import MyLb.BackEnd.Service.WalletService;
import MyLb.BackEnd.dto.SignUpRequest;
import MyLb.BackEnd.dto.WalletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SignUpControllerTest {

    @Mock
    private ClientService clientService;

    @Mock
    private CheckVerificationService checkVerificationService;

    @Mock
    private WalletService walletService;

    @InjectMocks
    private SignUpController signUpController;

    private SignUpRequest signUpRequest;
    private MockMultipartFile profileImage;
    private Client savedClient;
    private CheckVerification checkVerification;
    private WalletResponse walletResponse;

    @BeforeEach
    void setUp() {
        signUpRequest = new SignUpRequest();
        signUpRequest.setFirstName("John");
        signUpRequest.setLastName("Doe");
        signUpRequest.setEmail("john.doe@test.com");
        signUpRequest.setPassword("password123");
        signUpRequest.setBirthDate(LocalDate.of(1990, 1, 1));

        profileImage = new MockMultipartFile(
                "profileImage",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        savedClient = new Client();
        savedClient.setClientId(1L);
        savedClient.setFirstName("John");
        savedClient.setLastName("Doe");
        savedClient.setEmail("john.doe@test.com");
        savedClient.setRole("CIVIL");
        savedClient.setIsVerified(false);

        SelfDetail selfDetail = new SelfDetail();
        selfDetail.setSelfDetailId("john.doe@test.comIdSelfDetail");
        savedClient.setSelfDetail(selfDetail);

        checkVerification = new CheckVerification();
        walletResponse = new WalletResponse();
    }

    @Test
    void testRegisterUser_Success() {
        // Arrange
        when(clientService.isEmailUnique("john.doe@test.com")).thenReturn(true);
        when(clientService.saveClient(any(Client.class))).thenReturn(savedClient);

        // CORRECTION : Ces méthodes retournent des objets
        when(walletService.createWalletIfNotExists(1L)).thenReturn(walletResponse);
        when(checkVerificationService.getOrCreateVerification(1L)).thenReturn(checkVerification);

        // Act
        ResponseEntity<?> response = signUpController.registerUser(signUpRequest, profileImage);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(clientService, times(1)).isEmailUnique("john.doe@test.com");
        verify(clientService, times(1)).saveClient(any(Client.class));
        verify(walletService, times(1)).createWalletIfNotExists(1L);
        verify(checkVerificationService, times(1)).getOrCreateVerification(1L);
    }

    @Test
    void testRegisterUser_DuplicateEmail() {
        // Arrange
        when(clientService.isEmailUnique("john.doe@test.com")).thenReturn(false);

        // Act
        ResponseEntity<?> response = signUpController.registerUser(signUpRequest, profileImage);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Email déjà utilisé.", response.getBody());
        verify(clientService, times(1)).isEmailUnique("john.doe@test.com");
        verify(clientService, never()).saveClient(any(Client.class));
    }

    @Test
    void testRegisterUser_EmptyProfileImage() {
        // Arrange
        MockMultipartFile emptyImage = new MockMultipartFile(
                "profileImage",
                "test.jpg",
                "image/jpeg",
                new byte[0]
        );

        when(clientService.isEmailUnique("john.doe@test.com")).thenReturn(true);

        // Act
        ResponseEntity<?> response = signUpController.registerUser(signUpRequest, emptyImage);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("L'image de profil est manquante ou vide.", response.getBody());
        verify(clientService, never()).saveClient(any(Client.class));
    }

    @Test
    void testRegisterUser_WalletCreationError() {
        // Arrange
        when(clientService.isEmailUnique("john.doe@test.com")).thenReturn(true);
        when(clientService.saveClient(any(Client.class))).thenReturn(savedClient);

        // CORRECTION : Utiliser when().thenThrow() pour les méthodes non-void
        when(walletService.createWalletIfNotExists(1L))
                .thenThrow(new RuntimeException("Wallet service unavailable"));

        when(checkVerificationService.getOrCreateVerification(1L)).thenReturn(checkVerification);

        // Act
        ResponseEntity<?> response = signUpController.registerUser(signUpRequest, profileImage);

        // Assert
        // Should still succeed even if wallet creation fails
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(walletService, times(1)).createWalletIfNotExists(1L);
        // Client should still be saved and verification created
        verify(clientService, times(1)).saveClient(any(Client.class));
        verify(checkVerificationService, times(1)).getOrCreateVerification(1L);
    }

    @Test
    void testRegisterUser_GeneralException() {
        // Arrange
        when(clientService.isEmailUnique("john.doe@test.com")).thenReturn(true);
        when(clientService.saveClient(any(Client.class))).thenThrow(new RuntimeException("Database error"));

        // Act
        ResponseEntity<?> response = signUpController.registerUser(signUpRequest, profileImage);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Échec de l'inscription: Database error", response.getBody());
        verify(clientService, times(1)).saveClient(any(Client.class));
    }
}