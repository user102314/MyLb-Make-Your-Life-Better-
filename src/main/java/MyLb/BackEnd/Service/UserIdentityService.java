package MyLb.BackEnd.Service;

import MyLb.BackEnd.Model.Entities.UserIdentity;
import MyLb.BackEnd.Repository.UserIdentityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class UserIdentityService {

    @Autowired
    private UserIdentityRepository userIdentityRepository;

    public UserIdentity registerKycDocuments(
            Long iduser,
            MultipartFile cinRecto,
            MultipartFile cinVerso,
            MultipartFile selfie) throws IOException {

        Optional<UserIdentity> existingIdentityOpt = userIdentityRepository.findByIduser(iduser);
        UserIdentity identity;

        if (existingIdentityOpt.isPresent()) {
            identity = existingIdentityOpt.get();
        } else {
            identity = new UserIdentity();
            identity.setIduser(iduser);
        }

        identity.setPhotocinRecto(cinRecto.getBytes());
        identity.setPhotocinVerso(cinVerso.getBytes());
        identity.setPhotocompletSelfie(selfie.getBytes());
        identity.setEtat(UserIdentity.ValidationStatus.PENDING);

        return userIdentityRepository.save(identity);
    }

    // NOUVELLES MÉTHODES AJOUTÉES
    public Optional<UserIdentity> getUserIdentityByUserId(Long userId) {
        return userIdentityRepository.findByIduser(userId);
    }

    public List<UserIdentity> getAllUserIdentities() {
        return userIdentityRepository.findAll();
    }

    public UserIdentity updateValidationStatus(Long userId, UserIdentity.ValidationStatus status) {
        UserIdentity userIdentity = userIdentityRepository.findByIduser(userId)
                .orElseThrow(() -> new RuntimeException("UserIdentity not found for user id: " + userId));
        userIdentity.setEtat(status);
        return userIdentityRepository.save(userIdentity);
    }

    public boolean existsByUserId(Long userId) {
        return userIdentityRepository.findByIduser(userId).isPresent();
    }

    public void deleteUserIdentity(Long id) {
        userIdentityRepository.deleteById(id);
    }
}