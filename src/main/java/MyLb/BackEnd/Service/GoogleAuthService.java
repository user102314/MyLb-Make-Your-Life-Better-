package MyLb.BackEnd.Service;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
// NOTE: L'import de GoogleAuthenticatorQRGenerator n'est plus strictement nécessaire
// si vous construisez l'URL manuellement, mais nous le laissons si vous en avez besoin ailleurs.
// import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Service
public class GoogleAuthService {

    private final GoogleAuthenticator gAuth = new GoogleAuthenticator();
    private static final String APP_NAME = "MyLbApp"; // Nom de votre application (à changer)

    /**
     * Étape 1 : Génère la clé secrète.
     */
    public GoogleAuthenticatorKey generateNewSecret() {
        return gAuth.createCredentials();
    }

    /**
     * 🚨 CORRECTION FINALE : Génère l'URL de provisionnement (otpauth://) manuellement.
     * C'est le format que Google Authenticator et autres applications TOTP attendent.
     * @param secret La clé secrète Base32.
     * @param username L'email ou nom d'utilisateur.
     * @return L'URL encodée pour la génération du QR Code.
     */
    public String getQrCodeUrl(String secret, String username) {
        try {
            // S'assurer que les caractères spéciaux dans le nom d'utilisateur et l'émetteur sont encodés
            String encodedIssuer = URLEncoder.encode(APP_NAME, "UTF-8");
            String encodedUsername = URLEncoder.encode(username, "UTF-8");

            // Format standard: otpauth://totp/ISSUER:USER?secret=SECRET&issuer=ISSUER
            return String.format(
                    "otpauth://totp/%s:%s?secret=%s&issuer=%s",
                    encodedIssuer,
                    encodedUsername,
                    secret,
                    encodedIssuer
            );
        } catch (UnsupportedEncodingException e) {
            // Cette erreur est très peu probable avec UTF-8
            throw new RuntimeException("Erreur d'encodage de l'URL du QR Code.", e);
        }
    }

    /**
     * Étape 3 : Vérifie si le code TOTP soumis est valide.
     */
    public boolean isCodeValid(String secret, int code) {
        return gAuth.authorize(secret, code);
    }
}