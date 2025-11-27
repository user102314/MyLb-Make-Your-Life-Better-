package MyLb.BackEnd.Repository;

import MyLb.BackEnd.Model.Entities.CheckVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CheckVerificationRepository extends JpaRepository<CheckVerification, Long> {
    // NOUVELLE MÉTHODE AJOUTÉE
    @Query("SELECT cv FROM CheckVerification cv WHERE cv.iduser = :userId")
    Optional<CheckVerification> findByUserId(@Param("userId") Long userId);    Optional<CheckVerification> findByIduser(Long iduser);
}