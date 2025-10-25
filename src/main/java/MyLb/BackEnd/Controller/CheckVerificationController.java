package MyLb.BackEnd.Controller;

import MyLb.BackEnd.Model.CheckVerification;
import MyLb.BackEnd.Service.CheckVerificationService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/check-verification") // Nouvelle route
@CrossOrigin(origins = "http://localhost:8081", allowCredentials = "true")
public class CheckVerificationController {

    @Autowired
    private CheckVerificationService checkVerificationService;

    // Classe DTO pour les requêtes de mise à jour
    private static class StatusUpdateRequest {
        public int etatIndex; // 1, 2, 3 ou 4
        public boolean status;
    }


    // 1. Endpoint pour obtenir l'état de vérification actuel de l'utilisateur (Crée si non existant)
    @GetMapping("/status")
    public ResponseEntity<?> getVerificationStatus(HttpSession session) {
        Long userId = (Long) session.getAttribute("USER_ID");

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    Map.of("message", "Utilisateur non authentifié.")
            );
        }

        // Utilise la méthode qui crée l'enregistrement s'il est manquant
        CheckVerification verification = checkVerificationService.getOrCreateVerification(userId);

        return ResponseEntity.ok(verification);
    }

    // 🚨 3. NOUVEL ENDPOINT : Extrait uniquement l'état de vérification existant
    @GetMapping("/retrieve-status")
    public ResponseEntity<?> retrieveVerificationStatus(HttpSession session) {
        Long userId = (Long) session.getAttribute("USER_ID");

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    Map.of("message", "Utilisateur non authentifié.")
            );
        }

        // Utilise la méthode qui extrait seulement l'enregistrement s'il existe
        Optional<CheckVerification> verificationOpt = checkVerificationService.getVerificationByIduser(userId);

        if (verificationOpt.isPresent()) {
            return ResponseEntity.ok(verificationOpt.get());
        } else {
            // L'utilisateur n'a pas encore d'enregistrement de vérification (normal s'il vient de s'inscrire)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    Map.of("message", "Aucun enregistrement de vérification trouvé pour cet utilisateur. Veuillez initialiser.")
            );
        }
    }

    // 2. Endpoint pour mettre à jour une seule étape (Inchangé)
    @PostMapping("/update")
    public ResponseEntity<?> updateStatus(@RequestBody StatusUpdateRequest request, HttpSession session) {
        Long userId = (Long) session.getAttribute("USER_ID");

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    Map.of("message", "Utilisateur non authentifié.")
            );
        }

        try {
            CheckVerification updatedVerification = checkVerificationService.updateVerificationStatus(
                    userId,
                    request.etatIndex,
                    request.status
            );

            if (updatedVerification != null) {
                return ResponseEntity.ok(
                        Map.of("success", true, "message", "Statut mis à jour.", "verification", updatedVerification)
                );
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        Map.of("success", false, "message", "Enregistrement de vérification non trouvé.")
                );
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Map.of("success", false, "message", e.getMessage())
            );
        }
    }
}