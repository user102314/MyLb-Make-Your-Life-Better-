package MyLb.BackEnd.Controller;

import MyLb.BackEnd.Service.CheckVerificationService;
import MyLb.BackEnd.Service.GoogleAuthService;
import MyLb.BackEnd.Service.ClientService;
import MyLb.BackEnd.Service.ClientSecurityService;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.NoSuchElementException; // Import requis

@RestController
@RequestMapping("/api/auth/google")
@CrossOrigin(origins = "http://localhost:8081", allowCredentials = "true") // 🚨 Assurez-vous que c'est le bon port Front-end
public class GoogleAuthController {

    private final GoogleAuthService googleAuthService;
    private final CheckVerificationService checkVerificationService;
    private final ClientService clientService;
    private final ClientSecurityService clientSecurityService;

    @Autowired
    public GoogleAuthController(
            GoogleAuthService googleAuthService,
            CheckVerificationService checkVerificationService,
            ClientService clientService,
            ClientSecurityService clientSecurityService) {
        this.googleAuthService = googleAuthService;
        this.checkVerificationService = checkVerificationService;
        this.clientService = clientService;
        this.clientSecurityService = clientSecurityService;
    }

    private Long getUserId(HttpSession session) {
        // Remplacez par votre mécanisme d'obtention de l'ID utilisateur
        return (Long) session.getAttribute("USER_ID");
    }

    // ----------------------------------------------------
    // ENDPOINT 1 : Génération du QR Code (Enrôlement)
    // ----------------------------------------------------
    @PostMapping("/generate-qr")
    public ResponseEntity<Map<String, Object>> generateQr(HttpSession session) {
        Long userId = getUserId(session);

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Utilisateur non authentifié."));
        }

        try {
            String username = clientService.getEmailById(userId);
            GoogleAuthenticatorKey key = googleAuthService.generateNewSecret();
            String secretKey = key.getKey();

            // L'enregistrement ClientSecurity existe déjà, nous ne faisons que la mise à jour
            clientSecurityService.saveGoogleAuthSecret(userId, secretKey);

            String qrCodeUrl = googleAuthService.getQrCodeUrl(secretKey, username);

            return ResponseEntity.ok(Map.of(
                    "qrCodeUrl", qrCodeUrl,
                    "secret", secretKey,
                    "message", "QR Code généré. Scannez-le pour activer l'authentification à deux facteurs."
            ));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Utilisateur non trouvé: " + e.getMessage()));
        } catch (Exception e) {
            System.err.println("Erreur lors de la génération/sauvegarde du secret 2FA: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Erreur interne lors de la préparation du 2FA."));
        }
    }

    // ----------------------------------------------------
    // ENDPOINT 2 : Validation du code TOTP
    // ----------------------------------------------------
    @PostMapping("/validate-totp")
    public ResponseEntity<Map<String, Object>> validateTotp(@RequestBody Map<String, String> request, HttpSession session) {
        Long userId = getUserId(session);
        String totpCode = request.get("code");

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("success", false, "message", "Utilisateur non authentifié."));
        }
        if (totpCode == null || totpCode.length() != 6) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("success", false, "message", "Le code TOTP doit contenir 6 chiffres."));
        }

        try {
            Optional<String> secretOpt = clientSecurityService.getGoogleAuthSecret(userId);

            if (secretOpt.isEmpty() || secretOpt.get() == null || secretOpt.get().isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("success", false, "message", "Clé secrète 2FA introuvable. Veuillez d'abord générer le QR Code."));
            }

            String secret = secretOpt.get();
            int code = Integer.parseInt(totpCode);

            boolean isVerified = googleAuthService.isCodeValid(secret, code);

            if (isVerified) {
                // Mettre à jour les statuts dans les deux services
                checkVerificationService.updateVerificationStatus(userId, 3, true);
                clientSecurityService.set2FaEnabled(userId, true);

                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "message", "Vérification Google Authenticator réussie. Étape 3 validée."
                ));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                        "success", false,
                        "message", "Code TOTP invalide ou expiré."
                ));
            }
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("success", false, "message", "Le code doit être un nombre de 6 chiffres."));
        } catch (Exception e) {
            System.err.println("Erreur validation TOTP: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("success", false, "message", "Erreur interne lors de la validation."));
        }
    }
}