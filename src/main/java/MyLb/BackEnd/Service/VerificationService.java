package MyLb.BackEnd.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class VerificationService {

    // Simule une base de données ou un cache pour stocker les codes et leur expiration
    // Clé: ID de l'utilisateur (Long), Valeur: Objet VerificationData
    private final Map<Long, VerificationData> verificationCodes = new ConcurrentHashMap<>();

    // Durée de validité du code en minutes
    private static final int CODE_VALIDITY_MINUTES = 5;

    // Classe interne pour stocker le code et sa date d'expiration
    private static class VerificationData {
        String code;
        LocalDateTime expirationTime;

        public VerificationData(String code, LocalDateTime expirationTime) {
            this.code = code;
            this.expirationTime = expirationTime;
        }
    }

    /**
     * Génère un code aléatoire à 6 chiffres.
     */
    public String generateCode() {
        Random random = new Random();
        // Garantit 6 chiffres, y compris les zéros de début
        return String.format("%06d", random.nextInt(1000000));
    }

    /**
     * Stocke le code de vérification pour un utilisateur.
     */
    public void storeCode(Long clientId, String code) {
        LocalDateTime expiration = LocalDateTime.now().plusMinutes(CODE_VALIDITY_MINUTES);
        verificationCodes.put(clientId, new VerificationData(code, expiration));
    }

    /**
     * Vérifie si le code soumis est valide et non expiré.
     */
    public boolean verifyCode(Long clientId, String submittedCode) {
        VerificationData data = verificationCodes.get(clientId);

        if (data == null) {
            // Aucun code n'a été envoyé ou le client ID est inconnu
            return false;
        }

        // 1. Vérifie l'expiration
        if (LocalDateTime.now().isAfter(data.expirationTime)) {
            // Code expiré
            verificationCodes.remove(clientId);
            return false;
        }

        // 2. Vérifie le code
        boolean isValid = data.code.equals(submittedCode);

        if (isValid) {
            // Supprimer le code après une vérification réussie pour usage unique
            verificationCodes.remove(clientId);
        }

        return isValid;
    }
}