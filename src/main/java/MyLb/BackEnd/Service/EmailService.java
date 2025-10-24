package MyLb.BackEnd.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
// 🚨 Importez l'exception spécifique pour la gérer
import java.io.UnsupportedEncodingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // Injecte l'adresse Gmail (pilapipa31@gmail.com) pour l'utiliser comme expéditeur
    @Value("${spring.mail.username:pilapipa31@gmail.com}")
    private String senderEmail;


    public void sendVerificationCodeEmail(String toEmail, String code) {

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper;

        try {
            helper = new MimeMessageHelper(mimeMessage, true);

            // 🚨 Ces deux lignes peuvent lever UnsupportedEncodingException
            helper.setFrom(senderEmail, "L'équipe MyLB");
            helper.setTo(toEmail);

            helper.setSubject("Votre code de vérification de profil");

            String htmlContent = String.format(
                    "<html><body>" +
                            "<h2>Bonjour,</h2>" +
                            "<p>Votre code de v\u00e9rification est : <strong>%s</strong></p>" +
                            "<p>Ce code est valide pendant 5 minutes. Ne le partagez avec personne.</p>" +
                            "<p>Merci,</p>" +
                            "<p>L'\u00e9quipe de MyLB</p>" +
                            "</body></html>",
                    code
            );

            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);

        } catch (MessagingException e) {
            // Gère les erreurs de messagerie SMTP (connexion, authentification...)
            throw new RuntimeException("Échec de l'envoi de l'email ou de la construction du message.", e);

        } catch (UnsupportedEncodingException e) {
            // 🚨 NOUVEAU BLOC : Gère l'erreur de codage
            throw new RuntimeException("Erreur de codage lors de la configuration de l'expéditeur ou du destinataire.", e);
        }
    }
}