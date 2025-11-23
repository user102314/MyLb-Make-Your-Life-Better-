package MyLb.BackEnd.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:pilapipa31@gmail.com}")
    private String senderEmail;

    private static final int CODE_VALIDITY_MINUTES = 5;
    private final Map<Long, CodeData> verificationCodes = new HashMap<>();

    private static class CodeData {
        String code;
        LocalDateTime expirationTime;

        public CodeData(String code, LocalDateTime expirationTime) {
            this.code = code;
            this.expirationTime = expirationTime;
        }
    }

    // --- Méthodes de Gestion des Codes ---

    public String generateCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    public void storeCode(Long clientId, String code) {
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(CODE_VALIDITY_MINUTES);
        verificationCodes.put(clientId, new CodeData(code, expirationTime));
    }

    public boolean verifyCode(Long clientId, String submittedCode) {
        CodeData storedData = verificationCodes.get(clientId);

        if (storedData == null || storedData.expirationTime.isBefore(LocalDateTime.now())) {
            verificationCodes.remove(clientId);
            return false;
        }

        if (storedData.code.equals(submittedCode)) {
            verificationCodes.remove(clientId);
            return true;
        }
        return false;
    }

    // --- ENVOI D'EMAIL : ALERTE DE SÉCURITÉ ---

    @Async
    public void sendLoginAlertEmail(String toEmail, String firstName) {

        String subject = "Alerte de Sécurité: Connexion réussie à votre compte MyLB";
        String dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm:ss"));
        String location = "Emplacement non vérifié (IP: Non spécifiée)";

        String htmlContent = String.format(
                "<!DOCTYPE html>" +
                        "<html>" +
                        "<head>" +
                        "<style>" +
                        "body { font-family: Arial, sans-serif; margin: 0; padding: 20px; background-color: #f4f4f4; }" +
                        ".container { max-width: 600px; margin: 0 auto; background: white; padding: 20px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }" +
                        ".alert-box { background: #fff3cd; border: 1px solid #ffeaa7; border-radius: 5px; padding: 15px; margin: 20px 0; }" +
                        ".details { background: #f8f9fa; padding: 10px; border-radius: 5px; margin: 10px 0; }" +
                        "</style>" +
                        "</head>" +
                        "<body>" +
                        "<div class=\"container\">" +
                        "  <p>Bonjour %s,</p>" +
                        "  <div class=\"alert-box\">" +
                        "    <h3>Notification de Connexion à votre Compte</h3>" +
                        "    <p>Nous vous informons qu'une connexion réussie à votre compte MyLB a eu lieu à l'instant.</p>" +
                        "    <div class=\"details\">" +
                        "      <strong>Quand :</strong> %s<br/>" +
                        "      <strong>Où :</strong> %s<br/>" +
                        "    </div>" +
                        "  </div>" +
                        "  <p>Si c'était bien vous, vous pouvez ignorer cet email.</p>" +
                        "  <p>Si vous ne reconnaissez pas cette activité, veuillez <a href=\"#\">changer votre mot de passe immédiatement</a> et contacter notre support technique.</p>" +
                        "  <p>Merci pour votre vigilance,</p>" +
                        "  <p>L'équipe de Sécurité MyLB</p>" +
                        "</div>" +
                        "</body>" +
                        "</html>",
                firstName, dateTime, location
        );

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            helper.setFrom(senderEmail, "L'équipe MyLB");

            mailSender.send(message);
            System.out.println("--- THREAD SÉPARÉ : ALERTE DE SÉCURITÉ ENVOYÉE RÉELLEMENT --- Connexion réussie pour: " + toEmail);

        } catch (MessagingException | UnsupportedEncodingException e) {
            System.err.println("ERREUR ASYNCHRONE lors de l'envoi de l'alerte de sécurité: " + e.getMessage());
        }
    }

    // --- ENVOI D'EMAIL : CODE DE VÉRIFICATION ---

    public void sendVerificationCodeEmail(String toEmail, String code) {

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper;

        try {
            helper = new MimeMessageHelper(mimeMessage, true);

            helper.setFrom(senderEmail, "L'équipe MyLB");
            helper.setTo(toEmail);

            helper.setSubject("Votre code de vérification de profil");

            String htmlContent = String.format(
                    "<!DOCTYPE html>" +
                            "<html>" +
                            "<head>" +
                            "<style>" +
                            "body { font-family: Arial, sans-serif; margin: 0; padding: 20px; background-color: #f4f4f4; }" +
                            ".container { max-width: 600px; margin: 0 auto; background: white; padding: 30px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }" +
                            ".code-box { font-size: 32px; font-weight: bold; text-align: center; background: #007bff; color: white; padding: 20px; border-radius: 8px; margin: 20px 0; letter-spacing: 5px; }" +
                            ".warning { background: #fff3cd; border: 1px solid #ffeaa7; border-radius: 5px; padding: 15px; margin: 20px 0; }" +
                            "</style>" +
                            "</head>" +
                            "<body>" +
                            "<div class=\"container\">" +
                            "  <h2>Vérification de votre compte MyLB</h2>" +
                            "  <p>Utilisez le code suivant pour vérifier votre profil :</p>" +
                            "  <div class=\"code-box\">%s</div>" +
                            "  <div class=\"warning\">" +
                            "    <strong>Attention :</strong> Ce code expirera dans 5 minutes." +
                            "  </div>" +
                            "  <p>Si vous n'avez pas demandé ce code, veuillez ignorer cet email.</p>" +
                            "  <p>Cordialement,<br>L'équipe MyLB</p>" +
                            "</div>" +
                            "</body>" +
                            "</html>",
                    code
            );

            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
            System.out.println("--- EMAIL VÉRIFICATION ENVOYÉ RÉELLEMENT --- Code: " + code + " à: " + toEmail);

        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException("Échec de l'envoi de l'email ou de la construction du message.", e);
        }
    }

    // --- NOUVELLE MÉTHODE : ENVOI D'EMAIL DE SUPPORT ---

    @Async
    public void sendSupportEmail(String toEmail, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject(subject);

            // Formatage du contenu en HTML
            String htmlContent = formatSupportEmailContent(content);
            helper.setText(htmlContent, true);

            helper.setFrom(senderEmail, "Support MyLB");

            mailSender.send(message);
            System.out.println("--- EMAIL DE SUPPORT ENVOYÉ --- À: " + toEmail + " | Sujet: " + subject);

        } catch (MessagingException | UnsupportedEncodingException e) {
            System.err.println("ERREUR lors de l'envoi de l'email de support: " + e.getMessage());
            throw new RuntimeException("Échec de l'envoi de l'email de support", e);
        }
    }

    // --- MÉTHODE : ENVOI D'EMAIL DE BIENVENUE ---

    @Async
    public void sendWelcomeEmail(String toEmail, String firstName) {
        String subject = "Bienvenue sur MyLB - Votre compte a été créé avec succès";

        String htmlContent = String.format(
                "<!DOCTYPE html>" +
                        "<html>" +
                        "<head>" +
                        "<style>" +
                        "body { font-family: Arial, sans-serif; margin: 0; padding: 20px; background-color: #f4f4f4; }" +
                        ".container { max-width: 600px; margin: 0 auto; background: white; padding: 30px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }" +
                        ".welcome-header { background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); color: white; padding: 30px; border-radius: 10px; text-align: center; }" +
                        ".features { margin: 20px 0; }" +
                        ".feature-item { background: #f8f9fa; padding: 15px; margin: 10px 0; border-radius: 5px; border-left: 4px solid #007bff; }" +
                        "</style>" +
                        "</head>" +
                        "<body>" +
                        "<div class=\"container\">" +
                        "  <div class=\"welcome-header\">" +
                        "    <h1>Bienvenue sur MyLB !</h1>" +
                        "    <p>Votre plateforme de trading et gestion d'entreprises</p>" +
                        "  </div>" +
                        "  <p>Bonjour <strong>%s</strong>,</p>" +
                        "  <p>Félicitations ! Votre compte MyLB a été créé avec succès.</p>" +
                        "  <div class=\"features\">" +
                        "    <h3>Découvrez nos fonctionnalités :</h3>" +
                        "    <div class=\"feature-item\">" +
                        "      <strong>📈 Trading en temps réel</strong><br>" +
                        "      Accédez aux marchés financiers avec des outils avancés" +
                        "    </div>" +
                        "    <div class=\"feature-item\">" +
                        "      <strong>🏢 Gestion d'entreprise</strong><br>" +
                        "      Gérez votre société en toute simplicité" +
                        "    </div>" +
                        "    <div class=\"feature-item\">" +
                        "      <strong>🛡️ Sécurité renforcée</strong><br>" +
                        "      Vos données et investissements sont protégés" +
                        "    </div>" +
                        "  </div>" +
                        "  <p>Pour commencer, connectez-vous à votre compte et explorez nos services.</p>" +
                        "  <p>Besoin d'aide ? Contactez notre support : support@mylb.fr</p>" +
                        "  <p>Cordialement,<br><strong>L'équipe MyLB</strong></p>" +
                        "</div>" +
                        "</body>" +
                        "</html>",
                firstName
        );

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            helper.setFrom(senderEmail, "Équipe MyLB");

            mailSender.send(message);
            System.out.println("--- EMAIL DE BIENVENUE ENVOYÉ --- À: " + toEmail);

        } catch (MessagingException | UnsupportedEncodingException e) {
            System.err.println("ERREUR lors de l'envoi de l'email de bienvenue: " + e.getMessage());
        }
    }

    // --- MÉTHODE : ENVOI D'EMAIL DE RÉINITIALISATION DE MOT DE PASSE ---

    @Async
    public void sendPasswordResetEmail(String toEmail, String resetToken, String firstName) {
        String subject = "Réinitialisation de votre mot de passe MyLB";

        String resetLink = "https://votre-domaine.com/reset-password?token=" + resetToken;

        String htmlContent = String.format(
                "<!DOCTYPE html>" +
                        "<html>" +
                        "<head>" +
                        "<style>" +
                        "body { font-family: Arial, sans-serif; margin: 0; padding: 20px; background-color: #f4f4f4; }" +
                        ".container { max-width: 600px; margin: 0 auto; background: white; padding: 30px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }" +
                        ".reset-button { display: inline-block; background: #dc3545; color: white; padding: 12px 30px; text-decoration: none; border-radius: 5px; font-weight: bold; margin: 20px 0; }" +
                        ".warning { background: #fff3cd; border: 1px solid #ffeaa7; border-radius: 5px; padding: 15px; margin: 20px 0; }" +
                        "</style>" +
                        "</head>" +
                        "<body>" +
                        "<div class=\"container\">" +
                        "  <h2>Réinitialisation de mot de passe</h2>" +
                        "  <p>Bonjour %s,</p>" +
                        "  <p>Vous avez demandé la réinitialisation de votre mot de passe MyLB.</p>" +
                        "  <p>Cliquez sur le bouton ci-dessous pour créer un nouveau mot de passe :</p>" +
                        "  <a href=\"%s\" class=\"reset-button\">Réinitialiser mon mot de passe</a>" +
                        "  <div class=\"warning\">" +
                        "    <strong>Important :</strong> Ce lien expirera dans 1 heure. Si vous n'avez pas demandé cette réinitialisation, veuillez ignorer cet email." +
                        "  </div>" +
                        "  <p>Si le bouton ne fonctionne pas, copiez et collez ce lien dans votre navigateur :</p>" +
                        "  <p><code>%s</code></p>" +
                        "  <p>Cordialement,<br>L'équipe de sécurité MyLB</p>" +
                        "</div>" +
                        "</body>" +
                        "</html>",
                firstName, resetLink, resetLink
        );

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            helper.setFrom(senderEmail, "Sécurité MyLB");

            mailSender.send(message);
            System.out.println("--- EMAIL RÉINITIALISATION ENVOYÉ --- À: " + toEmail);

        } catch (MessagingException | UnsupportedEncodingException e) {
            System.err.println("ERREUR lors de l'envoi de l'email de réinitialisation: " + e.getMessage());
        }
    }

    // --- MÉTHODE UTILITAIRE : FORMATAGE DU CONTENU SUPPORT ---

    private String formatSupportEmailContent(String plainTextContent) {
        // Convertit le texte brut en HTML avec préservation des sauts de ligne
        String htmlContent = plainTextContent.replace("\n", "<br>");

        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; margin: 0; padding: 20px; background-color: #f4f4f4; }" +
                ".container { max-width: 600px; margin: 0 auto; background: white; padding: 30px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }" +
                ".content { background: #f8f9fa; padding: 20px; border-radius: 5px; border-left: 4px solid #007bff; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class=\"container\">" +
                "  <h3>Message de Support MyLB</h3>" +
                "  <div class=\"content\">" + htmlContent + "</div>" +
                "  <p><em>Cet email a été envoyé via le système de support MyLB</em></p>" +
                "</div>" +
                "</body>" +
                "</html>";
    }

    // --- MÉTHODE : NETTOYAGE DES CODES EXPIRÉS (OPTIONNEL) ---

    public void cleanupExpiredCodes() {
        LocalDateTime now = LocalDateTime.now();
        verificationCodes.entrySet().removeIf(entry -> entry.getValue().expirationTime.isBefore(now));
        System.out.println("--- NETTOYAGE DES CODES EXPIRÉS EFFECTUÉ ---");
    }

    // --- MÉTHODE : VÉRIFICATION DE LA VALIDITÉ D'UN CODE ---

    public boolean isCodeValid(Long clientId) {
        CodeData storedData = verificationCodes.get(clientId);
        return storedData != null && storedData.expirationTime.isAfter(LocalDateTime.now());
    }

    // --- MÉTHODE : OBTENIR LE TEMPS RESTANT POUR UN CODE ---

    public long getRemainingTimeMinutes(Long clientId) {
        CodeData storedData = verificationCodes.get(clientId);
        if (storedData == null) {
            return 0;
        }
        return java.time.Duration.between(LocalDateTime.now(), storedData.expirationTime).toMinutes();
    }
}