package MyLb.BackEnd.Controller;

import MyLb.BackEnd.Model.Entities.Client;
import MyLb.BackEnd.Service.ClientService;
import MyLb.BackEnd.Service.EmailService;
import MyLb.BackEnd.Service.VerificationService;
import MyLb.BackEnd.Service.CheckVerificationService;
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

    @Autowired
    private VerificationService verificationService;

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

    // -------------------------------------------------------------------------
    // 3. NOUVEL ENDPOINT POUR ENVOYER UN EMAIL À MYLBMAKEYOULIFEBETTER@GMAIL.COM
    // -------------------------------------------------------------------------

    @PostMapping("/send-to-support")
    public ResponseEntity<Map<String, Object>> sendEmailToSupport(
            @RequestBody Map<String, String> emailData,
            HttpSession session) {

        // 1. Récupération de l'ID utilisateur de la session
        Long clientId = (Long) session.getAttribute("USER_ID");

        if (clientId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "success", false,
                    "message", "Accès refusé. Veuillez vous connecter."
            ));
        }

        // 2. Récupération des données de l'email
        String subject = emailData.get("subject");
        String content = emailData.get("content");
        String userEmail = emailData.get("userEmail"); // Email de l'utilisateur pour réponse

        // Validation des données requises
        if (subject == null || subject.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "success", false,
                    "message", "Le sujet de l'email est requis."
            ));
        }

        if (content == null || content.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "success", false,
                    "message", "Le contenu de l'email est requis."
            ));
        }

        // 3. Récupération des informations du client
        Optional<Client> clientOpt = clientService.getClientById(clientId);
        if (clientOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "success", false,
                    "message", "Client non trouvé."
            ));
        }

        Client client = clientOpt.get();
        String clientEmail = userEmail != null ? userEmail : client.getEmail();
        String clientName = client.getFirstName() + " " + client.getLastName();

        try {
            // 4. Construction du contenu complet de l'email
            String fullContent = buildSupportEmailContent(clientName, clientEmail, content, clientId);

            // 5. Envoi de l'email au support MyLb
            emailService.sendSupportEmail("mylbmakeyoulifebetter@gmail.com", subject, fullContent);

            // 6. Optionnel: Envoi d'une copie à l'utilisateur
            try {
                String userConfirmationContent = buildUserConfirmationEmailContent(subject, content);
                emailService.sendSupportEmail(clientEmail,
                        "Confirmation: " + subject,
                        userConfirmationContent);
            } catch (Exception e) {
                System.err.println("Erreur lors de l'envoi de la confirmation à l'utilisateur: " + e.getMessage());
                // On continue même si l'envoi de confirmation échoue
            }

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Votre message a été envoyé à notre équipe de support. Nous vous répondrons dans les plus brefs délais."
            ));

        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi de l'email au support: " + e.getMessage());
            e.printStackTrace();

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Erreur lors de l'envoi de l'email. Veuillez réessayer plus tard."
            ));
        }
    }

    /**
     * Construit le contenu complet de l'email pour le support
     */
    private String buildSupportEmailContent(String clientName, String clientEmail, String userContent, Long clientId) {
        return String.format(
                "Nouveau message de support reçu via l'application MyLb\n\n" +
                        "=== INFORMATIONS DU CLIENT ===\n" +
                        "Nom: %s\n" +
                        "Email: %s\n" +
                        "ID Client: %d\n" +
                        "Date: %s\n\n" +
                        "=== MESSAGE DU CLIENT ===\n%s\n\n" +
                        "=== INSTRUCTIONS ===\n" +
                        "Merci de répondre à cet email dans les 24 heures.\n" +
                        "Répondre à: %s",
                clientName,
                clientEmail,
                clientId,
                java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                userContent,
                clientEmail
        );
    }

    /**
     * Construit le contenu de l'email de confirmation pour l'utilisateur
     */
    private String buildUserConfirmationEmailContent(String subject, String userContent) {
        return String.format(
                "Bonjour,\n\n" +
                        "Nous accusons réception de votre message envoyé à notre équipe de support MyLb.\n\n" +
                        "=== RÉCAPITULATIF DE VOTRE MESSAGE ===\n" +
                        "Sujet: %s\n" +
                        "Votre message: %s\n\n" +
                        "=== PROCHAINES ÉTAPES ===\n" +
                        "Notre équipe traitera votre demande dans les plus brefs délais.\n" +
                        "Temps de réponse moyen: 24 heures\n" +
                        "Email de contact: mylbmakeyoulifebetter@gmail.com\n\n" +
                        "Cordialement,\n" +
                        "L'équipe MyLb Support",
                subject,
                userContent
        );
    }

    // -------------------------------------------------------------------------
    // 4. ENDPOINT POUR ENVOYER UN EMAIL DE SUPPORT SANS AUTHENTIFICATION
    // (Pour le chat support public)
    // -------------------------------------------------------------------------

    @PostMapping("/public/support-request")
    public ResponseEntity<Map<String, Object>> sendPublicSupportRequest(
            @RequestBody Map<String, String> supportData) {

        // 1. Récupération des données
        String subject = supportData.get("subject");
        String content = supportData.get("content");
        String userName = supportData.get("userName");
        String userEmail = supportData.get("userEmail");
        String phone = supportData.get("phone");

        // Validation des données requises
        if (subject == null || subject.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "success", false,
                    "message", "Le sujet du message est requis."
            ));
        }

        if (content == null || content.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "success", false,
                    "message", "Le contenu du message est requis."
            ));
        }

        if (userEmail == null || userEmail.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "success", false,
                    "message", "L'adresse email est requise pour vous répondre."
            ));
        }

        try {
            // 2. Construction du contenu pour le support
            String fullContent = buildPublicSupportEmailContent(userName, userEmail, phone, content);

            // 3. Envoi de l'email au support
            emailService.sendSupportEmail("mylbmakeyoulifebetter@gmail.com", subject, fullContent);

            // 4. Envoi de la confirmation à l'utilisateur
            try {
                String userConfirmationContent = buildPublicUserConfirmationEmailContent(subject, content, userName);
                emailService.sendSupportEmail(userEmail,
                        "Confirmation: Votre demande de support MyLb",
                        userConfirmationContent);
            } catch (Exception e) {
                System.err.println("Erreur lors de l'envoi de la confirmation: " + e.getMessage());
            }

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Votre demande de support a été envoyée. Nous vous contacterons à l'adresse: " + userEmail
            ));

        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi de la demande de support public: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Erreur lors de l'envoi de votre demande. Veuillez réessayer."
            ));
        }
    }

    /**
     * Construit le contenu pour les demandes de support public
     */
    private String buildPublicSupportEmailContent(String userName, String userEmail, String phone, String content) {
        return String.format(
                "NOUVELLE DEMANDE DE SUPPORT PUBLIC - MYLB\n\n" +
                        "=== INFORMATIONS DU CONTACT ===\n" +
                        "Nom: %s\n" +
                        "Email: %s\n" +
                        "Téléphone: %s\n" +
                        "Date: %s\n\n" +
                        "=== MESSAGE ===\n%s\n\n" +
                        "=== INSTRUCTIONS ===\n" +
                        "Type: Demande publique (utilisateur non connecté)\n" +
                        "Priorité: Normale\n" +
                        "Répondre à: %s",
                userName != null ? userName : "Non spécifié",
                userEmail,
                phone != null ? phone : "Non spécifié",
                java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                content,
                userEmail
        );
    }

    /**
     * Construit la confirmation pour les demandes publiques
     */
    private String buildPublicUserConfirmationEmailContent(String subject, String content, String userName) {
        String salutation = userName != null ? "Bonjour " + userName + "," : "Bonjour,";

        return String.format(
                "%s\n\n" +
                        "Nous accusons réception de votre demande de support envoyée à MyLb.\n\n" +
                        "=== RÉCAPITULATIF ===\n" +
                        "Sujet: %s\n" +
                        "Votre message: %s\n\n" +
                        "=== INFORMATIONS IMPORTANTES ===\n" +
                        "• Temps de réponse: 24-48 heures\n" +
                        "• Email du support: mylbmakeyoulifebetter@gmail.com\n" +
                        "• Support téléphonique: 01 23 45 67 89\n\n" +
                        "Nous vous remercions pour votre confiance.\n\n" +
                        "Cordialement,\n" +
                        "L'équipe MyLb",
                salutation,
                subject,
                content
        );
    }
}