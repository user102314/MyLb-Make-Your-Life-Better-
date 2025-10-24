package MyLb.BackEnd.Controller;

import MyLb.BackEnd.Model.Client;
import MyLb.BackEnd.Model.SelfDetail;
import MyLb.BackEnd.Service.ClientService;
import MyLb.BackEnd.Service.CheckVerificationService; // 🚨 NOUVELLE IMPORTATION
import MyLb.BackEnd.dto.SignUpRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:8081", allowCredentials = "true")
public class SignUpController {

    private final ClientService clientService;
    private final CheckVerificationService checkVerificationService; // 🚨 NOUVELLE DÉCLARATION

    @Autowired
    public SignUpController(ClientService clientService, CheckVerificationService checkVerificationService) {
        this.clientService = clientService;
        this.checkVerificationService = checkVerificationService; // 🚨 NOUVELLE INJECTION
    }

    /**
     * Endpoint: POST /api/auth/signup
     */
    @PostMapping(value = "/signup", consumes = {"multipart/form-data"})
    public ResponseEntity<?> registerUser(
            @ModelAttribute SignUpRequest signUpRequest,
            @RequestParam("profileImage") MultipartFile profileImage
    ) {
        if (!clientService.isEmailUnique(signUpRequest.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email déjà utilisé.");
        }
        if (profileImage.isEmpty() || profileImage.getSize() == 0) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("L'image de profil est manquante ou vide.");
        }

        try {
            // ... Préparation des entités Client et SelfDetail (Aucun changement) ...
            String email = signUpRequest.getEmail();
            String customSelfDetailId = email + "IdSelfDetail";

            SelfDetail selfDetail = new SelfDetail();
            selfDetail.setSelfDetailId(customSelfDetailId);

            Client client = new Client();
            client.setFirstName(signUpRequest.getFirstName());
            client.setLastName(signUpRequest.getLastName());
            client.setBirthDate(signUpRequest.getBirthDate());
            client.setEmail(email);
            client.setPassword(signUpRequest.getPassword());
            client.setProfileImage(profileImage.getBytes());
            client.setRole("CIVIL");
            client.setIsVerified(false);

            client.setSelfDetail(selfDetail);
            selfDetail.setClient(client);

            // 1. Sauvegarder le Client
            Client savedClient = clientService.saveClient(client);

            // 🚨 ÉTAPE CLÉ MODIFIÉE: Initialisation de l'enregistrement CheckVerification
            checkVerificationService.getOrCreateVerification(savedClient.getClientId());

            // 2. Nettoyage avant la réponse
            savedClient.setPassword(null);
            savedClient.setProfileImage(null);

            return ResponseEntity.status(HttpStatus.CREATED).body(savedClient);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la lecture du fichier image: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Échec de l'inscription: " + e.getMessage());
        }
    }
}