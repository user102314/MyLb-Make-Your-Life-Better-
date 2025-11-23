package MyLb.BackEnd.Repository;

import MyLb.BackEnd.Model.Entities.OwnerPO;
import MyLb.BackEnd.Model.Entities.OwnerPOId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OwnerPORepository extends JpaRepository<OwnerPO, OwnerPOId> {

    /**
     * Trouver tous les OwnerPO d'un client spécifique
     */
    List<OwnerPO> findByClientId(Long clientId);

    /**
     * Trouver tous les OwnerPO d'une company spécifique
     */
    List<OwnerPO> findByCompanyId(Long companyId);

    /**
     * Vérifier si un client est propriétaire d'une company spécifique
     */
    boolean existsByClientIdAndCompanyId(Long clientId, Long companyId);

    /**
     * Trouver un OwnerPO spécifique par clientId et companyId
     */
    Optional<OwnerPO> findByClientIdAndCompanyId(Long clientId, Long companyId);

    /**
     * Compter le nombre de companies d'un client
     */
    @Query("SELECT COUNT(o) FROM OwnerPO o WHERE o.clientId = :clientId")
    long countCompaniesByClientId(@Param("clientId") Long clientId);
}