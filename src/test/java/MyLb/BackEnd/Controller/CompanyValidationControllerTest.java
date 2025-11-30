package MyLb.BackEnd.Controller;

import MyLb.BackEnd.Model.Entities.CompanyValidation;
import MyLb.BackEnd.Service.CompanyValidationService;
import MyLb.BackEnd.Service.CompanyService;
import MyLb.BackEnd.dto.CompanyValidationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompanyValidationControllerTest {

    @Mock
    private CompanyValidationService validationService;

    @Mock
    private CompanyService companyService;

    @InjectMocks
    private CompanyValidationController companyValidationController;

    private MockHttpSession session;
    private CompanyValidationRequest validationRequest;
    private MultipartFile[] files;

    @BeforeEach
    void setUp() {
        session = new MockHttpSession();
        session.setAttribute("USER_ID", 1L);

        validationRequest = new CompanyValidationRequest();
        validationRequest.setCompanyId(1L);
        validationRequest.setNomLegalComplet("Test Company");
        validationRequest.setNumeroImmatriculation("123456789");
        validationRequest.setAdresseSiegeSocial("123 Test Street");
        validationRequest.setNomPrenomPresidentLegal("John Doe");
        validationRequest.setNumeroTvaTaxe("FR12345678901");

        files = new MultipartFile[]{
                new MockMultipartFile("certificatImmatriculation", "certificat.pdf", "application/pdf", "test content".getBytes()),
                new MockMultipartFile("pieceIdentiteLegal", "piece.pdf", "application/pdf", "test content".getBytes()),
                new MockMultipartFile("statutsSociete", "statuts.pdf", "application/pdf", "test content".getBytes()),
                new MockMultipartFile("justificatifDomiciliation", "domiciliation.pdf", "application/pdf", "test content".getBytes())
        };
    }

    @Test
    void testSubmitValidation_Success() throws IOException {
        // Arrange
        when(companyService.isOwner(1L, 1L)).thenReturn(true);
        when(validationService.saveValidation(any(CompanyValidation.class))).thenReturn(new CompanyValidation());

        // Act
        ResponseEntity<CompanyValidation> response = companyValidationController.submitValidation(
                validationRequest, files[0], files[1], files[2], files[3], session
        );

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(companyService, times(1)).isOwner(1L, 1L);
        verify(validationService, times(1)).saveValidation(any(CompanyValidation.class));
    }

    @Test
    void testSubmitValidation_Unauthenticated() {
        // Arrange
        session.clearAttributes();

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> {
            companyValidationController.submitValidation(
                    validationRequest, files[0], files[1], files[2], files[3], session
            );
        });
    }

    @Test
    void testSubmitValidation_NotOwner() {
        // Arrange
        when(companyService.isOwner(1L, 1L)).thenReturn(false);

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> {
            companyValidationController.submitValidation(
                    validationRequest, files[0], files[1], files[2], files[3], session
            );
        });
    }

    @Test
    void testSubmitValidation_FileReadError() throws IOException {
        // Arrange
        when(companyService.isOwner(1L, 1L)).thenReturn(true);
        MockMultipartFile corruptFile = new MockMultipartFile("corrupt", "test.pdf", "application/pdf", new byte[0]) {
            @Override
            public byte[] getBytes() throws IOException {
                throw new IOException("File read error");
            }
        };

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> {
            companyValidationController.submitValidation(
                    validationRequest, corruptFile, files[1], files[2], files[3], session
            );
        });
    }
}