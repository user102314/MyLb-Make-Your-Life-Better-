package MyLb.BackEnd.Controller;

import MyLb.BackEnd.Model.Entities.Client;
import MyLb.BackEnd.Model.Entities.SelfDetail;
import MyLb.BackEnd.Service.ClientService;
import MyLb.BackEnd.Service.CheckVerificationService;
import MyLb.BackEnd.Service.WalletService; // 🆕 NOUVELLE IMPORTATION
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
    private final CheckVerificationService checkVerificationService;
    private final WalletService walletService; // 🆕 NOUVELLE DÉCLARATION

    @Autowired
    public SignUpController(ClientService clientService,
                            CheckVerificationService checkVerificationService,
                            WalletService walletService) { // 🆕 NOUVELLE INJECTION
        this.clientService = clientService;
        this.checkVerificationService = checkVerificationService;
        this.walletService = walletService; // 🆕 INITIALISATION
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
            // Préparation des entités Client et SelfDetail
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
            Long clientId = savedClient.getClientId(); // 🆕 RÉCUPÉRATION DE L'ID

            System.out.println("✅ [SignUpController] Client créé avec ID: " + clientId);

            // 2. 🆕 CRÉATION AUTOMATIQUE DU WALLET AVEC SOLDE 0
            try {
                walletService.createWalletIfNotExists(clientId);
                System.out.println("💰 [SignUpController] Wallet créé avec succès pour le client ID: " + clientId);
            } catch (Exception walletException) {
                System.err.println("⚠️ [SignUpController] Erreur lors de la création du wallet: " + walletException.getMessage());
                // On ne bloque pas l'inscription si le wallet échoue
                // Mais on log l'erreur pour debugging
            }

            // 3. Initialisation de l'enregistrement CheckVerification
            checkVerificationService.getOrCreateVerification(clientId);
            System.out.println("✅ [SignUpController] CheckVerification initialisé pour le client ID: " + clientId);

            // 4. Nettoyage avant la réponse
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