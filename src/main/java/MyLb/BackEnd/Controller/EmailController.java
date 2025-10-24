package MyLb.BackEnd.Controller;

import MyLb.BackEnd.Model.Client;
import MyLb.BackEnd.Service.ClientService;
import MyLb.BackEnd.Service.EmailService;
import MyLb.BackEnd.Service.VerificationService;
import MyLb.BackEnd.Service.CheckVerificationService; // 🚨 1. NOUVELLE IMPORTATION
import MyLb.BackEnd.dto.VerificationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/email")
public class EmailController {

    // Service utilisé pour la gestion des codes (selon votre structure actuelle)
    @Autowired
    private VerificationService verificationService;

    // 🚨 2. INJECTION DU SERVICE DE VÉRIFICATION DE STATUT
    @Autowired
    private CheckVerificationService checkVerificationService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ClientService clientService;

    // -------------------------------------------------------------------------
    // 1. POINT D'API POUR ENVOYER LE CODE DE VÉRIFICATION (INCHANGÉ)
    // -------------------------------------------------------------------------

    @PostMapping("/send-verification-code")
    public ResponseEntity<Map<String, String>> sendVerificationCode(HttpSession session) {

        // 1. Récupération de l'ID utilisateur de la session
        Long clientId = (Long) session.getAttribute("USER_ID");

        if (clientId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "error", "Accès refusé. Veuillez vous connecter (USER_ID manquant dans la session)."
            ));
        }

        // 2. Récupération de l'objet Client complet via le Service
        Optional<Client> clientOpt = clientService.getClientById(clientId);

        if (clientOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "error", "Client non trouvé. L'ID de session est invalide."
            ));
        }

        Client client = clientOpt.get();
        String userEmail = client.getEmail();

        try {
            String verificationCode = verificationService.generateCode();

            // 3. Stocke le code en utilisant l'ID Client réel de la session
            verificationService.storeCode(clientId, verificationCode);

            // 4. Envoi de l'e-mail à l'utilisateur connecté
            emailService.sendVerificationCodeEmail(userEmail, verificationCode);

            return ResponseEntity.ok(Map.of(
                    "message", "Le code de vérification a été envoyé à l'adresse de l'utilisateur connecté: " + userEmail
            ));

        } catch (Exception e) {
            System.err.println("Erreur critique lors de l'envoi de l'e-mail : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "error", "Échec de l'envoi de l'e-mail. Veuillez vérifier les logs du serveur."
            ));
        }
    }


    // -------------------------------------------------------------------------
    // 2. POINT D'API POUR VÉRIFIER LE CODE (MODIFIÉ)
    // -------------------------------------------------------------------------

    @PostMapping("/verify-code")
    public ResponseEntity<Map<String, Object>> verifyCode(
            @RequestBody VerificationRequest verificationRequest,
            HttpSession session) {

        // 1. Récupération de l'ID utilisateur de la session
        Long clientId = (Long) session.getAttribute("USER_ID");

        if (clientId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "success", false,
                    "message", "Accès refusé. Session utilisateur non trouvée."
            ));
        }

        String submittedCode = verificationRequest.getCode();

        if (submittedCode == null || submittedCode.length() != 6) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "success", false,
                    "message", "Le code soumis est invalide ou manquant."
            ));
        }

        // 2. Vérification du code
        boolean isValid = verificationService.verifyCode(clientId, submittedCode);

        if (isValid) {

            // 🚨 3. AJOUT DE LA LOGIQUE DE MISE À JOUR DU STATUT (ETAT 1 = VRAI)
            try {
                // L'index 1 correspond à l'étape 1 (etat1_email) dans CheckVerification
                checkVerificationService.updateVerificationStatus(clientId, 1, true);

                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "message", "Code vérifié avec succès. Votre email est validé."
                ));
            } catch (Exception e) {
                System.err.println("Erreur lors de la mise à jour du statut de vérification (étape 1) : " + e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                        "success", false,
                        "message", "Code vérifié, mais échec de la mise à jour du statut de vérification."
                ));
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "success", false,
                    "message", "Code invalide ou expiré. Veuillez renvoyer un code."
            ));
        }
    }
}