package MyLb.BackEnd.Repository;

import MyLb.BackEnd.Model.CheckVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CheckVerificationRepository extends JpaRepository<CheckVerification, Long> {
    // Méthode pour trouver l'enregistrement de vérification par l'ID de l'utilisateur
    Optional<CheckVerification> findByIduser(Long iduser);
}