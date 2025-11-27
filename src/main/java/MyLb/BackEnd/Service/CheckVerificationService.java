package MyLb.BackEnd.Service;

import MyLb.BackEnd.Model.Entities.CheckVerification;
import MyLb.BackEnd.Model.Entities.UserIdentity;
import MyLb.BackEnd.Repository.CheckVerificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class CheckVerificationService {

    @Autowired
    private CheckVerificationRepository checkVerificationRepository;

    public CheckVerification getOrCreateVerification(Long iduser) {
        return checkVerificationRepository.findByIduser(iduser)
                .orElseGet(() -> {
                    CheckVerification newVerification = new CheckVerification(iduser);
                    return checkVerificationRepository.save(newVerification);
                });
    }

    public Optional<CheckVerification> getVerificationByIduser(Long iduser) {
        return checkVerificationRepository.findByIduser(iduser);
    }

    public CheckVerification updateVerificationStatus(Long iduser, int etatIndex, boolean status) {
        Optional<CheckVerification> verificationOpt = checkVerificationRepository.findByIduser(iduser);

        if (verificationOpt.isPresent()) {
            CheckVerification verification = verificationOpt.get();
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

    // NOUVELLE MÉTHODE AJOUTÉE
    public boolean isUserFullyVerified(Long userId) {
        Optional<CheckVerification> verification = checkVerificationRepository.findByIduser(userId);
        return verification.map(v -> v.isEtat1() && v.isEtat2() && v.isEtat3() && v.isEtat4())
                .orElse(false);
    }

    // NOUVELLE MÉTHODE AJOUTÉE
    public List<CheckVerification> getAllCheckVerifications() {
        return checkVerificationRepository.findAll();
    }

    public void deleteCheckVerification(Long idverification) {
    }
}