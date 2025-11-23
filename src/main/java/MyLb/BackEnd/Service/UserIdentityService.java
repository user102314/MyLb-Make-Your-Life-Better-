package MyLb.BackEnd.Service;

import MyLb.BackEnd.Model.Entities.UserIdentity;
import MyLb.BackEnd.Repository.UserIdentityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
public class UserIdentityService {

    @Autowired
    private UserIdentityRepository userIdentityRepository;

    /**
     * Enregistre les documents KYC (données BLOB) directement en base de données.
     * @param iduser L'ID de l'utilisateur connecté.
     */
    public UserIdentity registerKycDocuments(
            Long iduser,
            MultipartFile cinRecto,
            MultipartFile cinVerso,
            MultipartFile selfie) throws IOException { // 🚨 Lève une IOException

        // 1. Vérifie si l'utilisateur a déjà soumis des documents
        Optional<UserIdentity> existingIdentityOpt = userIdentityRepository.findByIduser(iduser);
        UserIdentity identity;

        if (existingIdentityOpt.isPresent()) {
            // Mise à jour de l'enregistrement existant
            identity = existingIdentityOpt.get();
        } else {
            // Création d'un nouvel enregistrement
            identity = new UserIdentity();
            identity.setIduser(iduser);
        }

        // 2. 🚨 MODIFICATION CRITIQUE: Conversion du fichier MultipartFile en tableau d'octets (byte[])
        identity.setPhotocinRecto(cinRecto.getBytes());
        identity.setPhotocinVerso(cinVerso.getBytes());
        identity.setPhotocompletSelfie(selfie.getBytes());

        // 3. Met à jour l'état
        identity.setEtat(UserIdentity.ValidationStatus.PENDING); // Remis à 'PENDING' après soumission

        // 4. Sauvegarde en base de données (inclut les données BLOB)
        return userIdentityRepository.save(identity);
    }
}