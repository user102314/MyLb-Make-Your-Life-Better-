package MyLb.BackEnd.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async; // 🚨 Importation pour @Async
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

    @Async // 🚨 LA MÉTHODE S'EXÉCUTE DÉSORMAIS DANS UN THREAD SÉPARÉ
    public void sendLoginAlertEmail(String toEmail, String firstName) {

        String subject = "Alerte de Sécurité: Connexion réussie à votre compte MyLB";
        String dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm:ss"));
        String location = "Emplacement non vérifié (IP: Non spécifiée)";

        String htmlContent = String.format(
                "<!DOCTYPE html>" +
                        "<html>" +
                        // ... (Styles) ...
                        "<body>" +
                        "  <p>Bonjour %s,</p>" +
                        "  <div class=\"alert-box\">" +
                        "    <h3>Notification de Connexion à votre Compte</h3>" +
                        "    <p>Nous vous informons qu'une connexion r\u00e9ussie \u00e0 votre compte MyLB a eu lieu \u00e0 l'instant.</p>" +
                        "    <div class=\"details\">" +
                        "      <strong>Quand :</strong> %s<br/>" +
                        "      <strong>O\u00f9 :</strong> %s<br/>" +
                        "    </div>" +
                        "  </div>" +
                        "  <p>Si c'\u00e9tait bien vous, vous pouvez ignorer cet email.</p>" +
                        "  <p>Si vous ne reconnaissez pas cette activit\u00e9, veuillez <a href=\"#\">changer votre mot de passe imm\u00e9diatement</a> et contacter notre support technique.</p>" +
                        "  <p>Merci pour votre vigilance,</p>" +
                        "  <p>L'\u00e9quipe de S\u00e9curit\u00e9 MyLB</p>" +
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
            // Log l'erreur dans ce thread asynchrone
            System.err.println("ERREUR ASYNCHRONE lors de l'envoi de l'alerte de sécurité: " + e.getMessage());
        }
    }

    // --- ENVOI D'EMAIL : CODE DE VÉRIFICATION (Méthode non async) ---

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
                            // ... (HTML de vérification) ...
                            "<body>" +
                            // ... (Contenu de l'email) ...
                            "<div class=\"code-box\">%s</div>" +
                            // ...
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
}