package MyLb.BackEnd.Controller;

import MyLb.BackEnd.Service.CheckVerificationService;
import jakarta.servlet.http.HttpSession; // 👈 NÉCESSAIRE
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import MyLb.BackEnd.Model.Entities.CheckVerification;

@RestController
@RequestMapping("/api/access-key")
@CrossOrigin(origins = "http://localhost:8081", allowCredentials = "true")
public class AccessKeyController {

    private final CheckVerificationService verificationService;

    @Autowired
    public AccessKeyController(CheckVerificationService verificationService) {
        this.verificationService = verificationService;
    }

    /**
     * API unique: Récupère l'ID via la SESSION HTTP (méthode manuelle),
     * met à jour etat4=true, et renvoie le message.
     * URL: POST /api/access-key/request
     */
    @PostMapping("/request")
    public ResponseEntity<String> requestAccessKey(HttpSession session) { // 👈 Injection de HttpSession

        // 1. RÉCUPÉRATION MANUELLE DE L'ID DE LA SESSION
        Long userId = (Long) session.getAttribute("USER_ID");

        if (userId == null) {
            // L'utilisateur n'est pas dans la session (non connecté ou session expirée)
            return ResponseEntity.status(401).body(
                    "Non autorisé : La session utilisateur n'est pas active. Veuillez vous reconnecter."
            );
        }

        try {
            // 2. CHANGEMENT D'ÉTAT (Appel au service)
            CheckVerification updatedVerification = verificationService.updateVerificationStatus(
                    userId,
                    4,      // Index correspondant à etat4
                    true    // Statut à TRUE
            );

            if (updatedVerification == null) {
                return ResponseEntity.status(404).body("Erreur: L'enregistrement de vérification n'a pas été trouvé pour cet utilisateur.");
            }

            // 3. MESSAGE DE SUCCÈS
            String successMessage =
                    "Demande de clé d'accès et le dépôt de votre dossier a été terminé. " +
                            "Veuillez attendre 24 à 48 heures pour la réponse, s'il vous plaît.";

            return ResponseEntity.ok(successMessage);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        } catch (Exception e) {
            System.err.println("Erreur fatale lors de la soumission du dossier (ID: " + userId + "): " + e.getMessage());
            return ResponseEntity.status(500).body("Erreur interne du serveur. Réessayez plus tard.");
        }
    }
}