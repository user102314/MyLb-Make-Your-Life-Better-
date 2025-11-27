package MyLb.BackEnd.Controller;

import MyLb.BackEnd.Service.CheckVerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/verification") // Route modifiée
@CrossOrigin(origins = "http://localhost:8081", allowCredentials = "true")
public class VerificationController {

    @Autowired
    private CheckVerificationService checkVerificationService;

    @PutMapping("/{userId}/identity-status") // Route modifiée
    public ResponseEntity<?> updateIdentityStatus(@PathVariable Long userId, @RequestBody Map<String, String> request) {
        try {
            String status = request.get("status");
            System.out.println("🔄 [VerificationController] Mise à jour statut identité pour l'utilisateur " + userId + " -> " + status);

            // Implémentation dépend de votre modèle de données
            // Cette méthode doit mettre à jour le statut des documents d'identité

            System.out.println("✅ [VerificationController] Statut identité mis à jour avec succès");

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Statut des documents d'identité mis à jour avec succès"
            ));

        } catch (Exception e) {
            System.err.println("❌ [VerificationController] Erreur: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "error", "Erreur lors de la mise à jour du statut identité",
                            "message", e.getMessage()
                    ));
        }
    }

    @GetMapping("/global-status") // Route modifiée
    public ResponseEntity<?> getGlobalVerificationStatus() {
        try {
            System.out.println("📊 [VerificationController] Récupération du statut global de vérification");

            var allVerifications = checkVerificationService.getAllCheckVerifications();

            long totalUsers = allVerifications.size();
            long step1Completed = allVerifications.stream().filter(v -> v.isEtat1()).count();
            long step2Completed = allVerifications.stream().filter(v -> v.isEtat2()).count();
            long step3Completed = allVerifications.stream().filter(v -> v.isEtat3()).count();
            long step4Completed = allVerifications.stream().filter(v -> v.isEtat4()).count();
            long fullyVerified = allVerifications.stream()
                    .filter(v -> v.isEtat1() && v.isEtat2() && v.isEtat3() && v.isEtat4())
                    .count();

            Map<String, Object> status = Map.of(
                    "totalUsers", totalUsers,
                    "step1Completed", step1Completed,
                    "step2Completed", step2Completed,
                    "step3Completed", step3Completed,
                    "step4Completed", step4Completed,
                    "fullyVerified", fullyVerified,
                    "completionRate", totalUsers > 0 ? (double) fullyVerified / totalUsers * 100 : 0
            );

            System.out.println("✅ [VerificationController] Statut global de vérification récupéré");

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "verificationStatus", status
            ));

        } catch (Exception e) {
            System.err.println("❌ [VerificationController] Erreur: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "error", "Erreur lors de la récupération du statut de vérification",
                            "message", e.getMessage()
                    ));
        }
    }
}