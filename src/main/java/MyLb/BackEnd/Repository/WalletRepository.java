package MyLb.BackEnd.Repository;

import MyLb.BackEnd.Model.Entities.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {

    /**
     * Trouver un wallet par ID client
     */
    Optional<Wallet> findByIdClient(Long idClient);

    /**
     * Vérifier si un wallet existe pour un client
     */
    boolean existsByIdClient(Long idClient);

    /**
     * Récupérer le solde par ID client
     */
    @Query("SELECT w.sold FROM Wallet w WHERE w.idClient = :idClient")
    Double findSoldByIdClient(@Param("idClient") Long idClient);
}