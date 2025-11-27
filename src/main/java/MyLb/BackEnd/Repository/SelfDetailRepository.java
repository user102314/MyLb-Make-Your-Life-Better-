package MyLb.BackEnd.Repository;

import MyLb.BackEnd.Model.Entities.SelfDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SelfDetailRepository extends JpaRepository<SelfDetail, Long> {

    @Modifying
    @Query("DELETE FROM SelfDetail sd WHERE sd.client.clientId = :clientId")
    void deleteByClientId(@Param("clientId") Long clientId);
    // Méthode personnalisée pour trouver le SelfDetail par le numéro CIN,
    // utile pour vérifier l'unicité ou l'existence (si l'unicité est requise).
    Optional<SelfDetail> findByCinNumber(String cinNumber);
}