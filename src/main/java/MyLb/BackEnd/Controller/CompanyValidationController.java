package MyLb.BackEnd.Controller;

import MyLb.BackEnd.dto.CompanyValidationRequest;
import MyLb.BackEnd.Model.Entities.CompanyValidation;
import MyLb.BackEnd.Service.CompanyValidationService;
import MyLb.BackEnd.Service.CompanyService;

import jakarta.validation.Valid;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@RestController
@RequestMapping("/api/company/validation")
public class CompanyValidationController {

    // 1. DÉCLARATION DES CHAMPS DE SERVICE COMME FINAL
    // Cette étape est essentielle pour l'injection par constructeur.
    private final CompanyValidationService validationService;
    private final CompanyService companyService;

    // 2. CONSTRUCTEUR AVEC @Autowired
    // Le constructeur garantit que les champs final sont initialisés.
    @Autowired
    public CompanyValidationController(
            CompanyValidationService validationService,
            CompanyService companyService)
    {
        this.validationService = validationService; // INITIALISATION DE 'validationService'
        this.companyService = companyService;      // INITIALISATION DE 'companyService'
    }

    /**
     * Point de terminaison pour soumettre les données et les documents de validation.
     * (Le reste de la méthode submitValidation est inchangé)
     */
    @PostMapping(value = "/submit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CompanyValidation> submitValidation(
            // ... (Corps de la méthode omis pour la concision)
            @Valid @RequestPart("validationData") CompanyValidationRequest request,
            @RequestPart("certificatImmatriculation") MultipartFile certificatImmatriculationFile,
            @RequestPart("pieceIdentiteLegal") MultipartFile pieceIdentiteLegalFile,
            @RequestPart("statutsSociete") MultipartFile statutsSocieteFile,
            @RequestPart("justificatifDomiciliation") MultipartFile justificatifDomiciliationFile,
            HttpSession session) throws IOException
    {
        // --- 1. Vérification de l'Authentification et de l'Autorisation ---
        Long authenticatedUserId = (Long) session.getAttribute("USER_ID");
        Long companyId = request.getCompanyId();

        if (authenticatedUserId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Accès refusé. Veuillez vous connecter.");
        }

        // **Ici, les services sont utilisés et ne génèrent plus d'erreur d'initialisation.**
        if (!companyService.isOwner(authenticatedUserId, companyId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Accès refusé. Vous n'êtes pas autorisé à valider cette société.");
        }

        // --- 2. Création et Remplissage de l'Entité CompanyValidation ---
        CompanyValidation validation = new CompanyValidation();

        // Mappage des champs simples (issus du DTO)
        validation.setCompanyId(companyId);
        validation.setNomLegalComplet(request.getNomLegalComplet());
        validation.setNumeroImmatriculation(request.getNumeroImmatriculation());
        validation.setAdresseSiegeSocial(request.getAdresseSiegeSocial());
        validation.setNomPrenomPresidentLegal(request.getNomPrenomPresidentLegal());
        validation.setNumeroTvaTaxe(request.getNumeroTvaTaxe());

        // Mappage des fichiers (Conversion de MultipartFile en byte[])
        try {
            validation.setCertificatImmatriculation(certificatImmatriculationFile.getBytes());
            validation.setPieceIdentiteRepresentantLegal(pieceIdentiteLegalFile.getBytes());
            validation.setStatutsSociete(statutsSocieteFile.getBytes());
            validation.setJustificatifDomiciliationCommerciale(justificatifDomiciliationFile.getBytes());
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur lors de la lecture des fichiers soumis.", e);
        }

        // --- 3. Sauvegarde via le Service ---
        CompanyValidation savedValidation = validationService.saveValidation(validation);

        // --- 4. Retourner la réponse ---
        return new ResponseEntity<>(savedValidation, HttpStatus.CREATED);
    }
}