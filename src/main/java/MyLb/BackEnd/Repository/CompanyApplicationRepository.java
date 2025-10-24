package MyLb.BackEnd.Repository;

import MyLb.BackEnd.Model.CompanyApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CompanyApplicationRepository extends JpaRepository<CompanyApplication, Long> {

    // Trouver toutes les applications par statut (ex: PENDING, APPROVED, REJECTED)
    List<CompanyApplication> findByStatus(String status);

    // Trouver une application par l'ID du propriétaire (OwnerPO) lié
    Optional<CompanyApplication> findByOwnerClientId(Long ownerId);
}