package MyLb.BackEnd.Service;

import MyLb.BackEnd.Model.CheckVerification;
import MyLb.BackEnd.Repository.CheckVerificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class CheckVerificationService {

    @Autowired
    private CheckVerificationRepository checkVerificationRepository;

    /**
     * Crée ou récupère l'enregistrement de vérification pour un utilisateur donné.
     */
    public CheckVerification getOrCreateVerification(Long iduser) {
        return checkVerificationRepository.findByIduser(iduser)
                .orElseGet(() -> {
                    CheckVerification newVerification = new CheckVerification(iduser);
                    return checkVerificationRepository.save(newVerification);
                });
    }

    /**
     * Récupère l'enregistrement de vérification par ID utilisateur.
     */
    public Optional<CheckVerification> getVerificationByIduser(Long iduser) {
        return checkVerificationRepository.findByIduser(iduser);
    }


    /**
     * Met à jour le statut d'une étape de vérification.
     * @param iduser L'ID de l'utilisateur.
     * @param etatIndex L'étape à mettre à jour (1, 2, 3 ou 4).
     * @param status La nouvelle valeur (true/false).
     * @return L'objet CheckVerification mis à jour, ou null si non trouvé.
     */
    public CheckVerification updateVerificationStatus(Long iduser, int etatIndex, boolean status) {
        Optional<CheckVerification> verificationOpt = checkVerificationRepository.findByIduser(iduser);

        if (verificationOpt.isPresent()) {
            CheckVerification verification = verificationOpt.get();

            // Logique de mise à jour basée sur l'index
            switch (etatIndex) {
                case 1:
                    verification.setEtat1(status);
                    break;
                case 2:
                    verification.setEtat2(status);
                    break;
                case 3:
                    verification.setEtat3(status);
                    break;
                case 4:
                    verification.setEtat4(status);
                    break;
                default:
                    throw new IllegalArgumentException("Index d'étape invalide: " + etatIndex);
            }

            return checkVerificationRepository.save(verification);
        }
        return null;
    }
}