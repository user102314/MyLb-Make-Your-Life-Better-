package MyLb.BackEnd.Controller;

import MyLb.BackEnd.Model.UserIdentity;
import MyLb.BackEnd.Service.UserIdentityService;
// 🚨 CORRECTION: Utilise le service de vérification correct fourni
import MyLb.BackEnd.Service.CheckVerificationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/kyc")
// Assurez-vous que l'origine et les credentials sont corrects
@CrossOrigin(origins = "http://localhost:8081", allowCredentials = "true")
public class UserIdentityController {

    private final UserIdentityService userIdentityService;
    // 🚨 CORRECTION: Déclaration du champ avec le nom de service correct
    private final CheckVerificationService checkVerificationService;

    @Autowired
    public UserIdentityController(
            UserIdentityService userIdentityService,
            // 🚨 CORRECTION: Injection par constructeur du service correct
            CheckVerificationService checkVerificationService) {

        this.userIdentityService = userIdentityService;
        this.checkVerificationService = checkVerificationService; // Affectation du service correct
    }

    @PostMapping("/upload-documents")
    public ResponseEntity<Map<String, Object>> uploadKycDocuments(
            @RequestParam("cinRecto") MultipartFile cinRecto,
            @RequestParam("cinVerso") MultipartFile cinVerso,
            @RequestParam("selfie") MultipartFile selfie,
            HttpSession session) {

        // 1. Récupération de l'ID utilisateur de la session
        Long userId = (Long) session.getAttribute("USER_ID");

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "success", false,
                    "message", "Utilisateur non authentifié. Veuillez vous connecter."
            ));
        }

        if (cinRecto.isEmpty() || cinVerso.isEmpty() || selfie.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "success", false,
                    "message", "Tous les 3 documents (Recto, Verso, Selfie) sont requis."
            ));
        }

        try {
            // 2. Enregistrement des documents
            UserIdentity savedIdentity = userIdentityService.registerKycDocuments(
                    userId, cinRecto, cinVerso, selfie
            );

            // 🚨 MISE À JOUR CRUCIALE: Appel du service pour mettre à jour etat2 (2) à TRUE
            checkVerificationService.updateVerificationStatus(userId, 2, true);
            System.out.println("✅ KYC Upload réussi. Statut etat2 mis à jour à TRUE pour l'utilisateur: " + userId);

            // 3. Réponse au Front-end
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Documents d'identité soumis avec succès pour vérification. Statut mis à jour.",
                    "etat", savedIdentity.getEtat().toString(),
                    "idv", savedIdentity.getIdv()
            ));

        } catch (IOException e) {
            System.err.println("Erreur de lecture du fichier lors de l'upload KYC: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Échec de la lecture des données du fichier. " + e.getMessage()
            ));
        } catch (Exception e) {
            System.err.println("Erreur BDD/Service: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Internal Server Error: Une erreur est survenue lors de l'enregistrement ou de la mise à jour du statut. " + e.getMessage()
            ));
        }
    }
}