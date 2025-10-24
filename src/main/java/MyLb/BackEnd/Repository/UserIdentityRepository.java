package MyLb.BackEnd.Repository;

import MyLb.BackEnd.Model.UserIdentity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserIdentityRepository extends JpaRepository<UserIdentity, Long> {
    // Permet de vérifier si l'utilisateur a déjà soumis des documents
    Optional<UserIdentity> findByIduser(Long iduser);
}