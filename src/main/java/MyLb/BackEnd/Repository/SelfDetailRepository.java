package MyLb.BackEnd.Repository;

import MyLb.BackEnd.Model.SelfDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SelfDetailRepository extends JpaRepository<SelfDetail, Long> {

    // JpaRepository fournit déjà :
    // - SelfDetail save(SelfDetail detail)
    // - Optional<SelfDetail> findById(Long selfDetailId)
    // - void deleteById(Long selfDetailId)

    // Méthode personnalisée pour trouver le SelfDetail par le numéro CIN,
    // utile pour vérifier l'unicité ou l'existence (si l'unicité est requise).
    Optional<SelfDetail> findByCinNumber(String cinNumber);
}