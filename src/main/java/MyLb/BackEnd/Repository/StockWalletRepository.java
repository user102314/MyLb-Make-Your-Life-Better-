// MyLb.BackEnd.Repository.StockWalletRepository.java
package MyLb.BackEnd.Repository;

import MyLb.BackEnd.Model.Entities.StockWallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockWalletRepository extends JpaRepository<StockWallet, Long> {

    // Trouver tous les stocks d'un client
    List<StockWallet> findByIdClient(Long idClient);

    // Trouver un stock spécifique d'un client
    Optional<StockWallet> findByIdAndIdClient(Long id, Long idClient);

    // Trouver un stock par idStock et idClient
    Optional<StockWallet> findByIdStockAndIdClient(Long idStock, Long idClient);

    // Vérifier si un stock existe pour un client
    boolean existsByIdStockAndIdClient(Long idStock, Long idClient);

    // Vérifier si un stock existe par ID et client (méthode manquante)
    boolean existsByIdAndIdClient(Long id, Long idClient);

    // Supprimer un stock par id et idClient
    @Modifying
    @Query("DELETE FROM StockWallet s WHERE s.id = :id AND s.idClient = :idClient")
    void deleteByIdAndIdClient(@Param("id") Long id, @Param("idClient") Long idClient);

    // Calculer le total investi par client
    @Query("SELECT SUM(s.prixTotal) FROM StockWallet s WHERE s.idClient = :idClient")
    Double getTotalInvestmentByClient(@Param("idClient") Long idClient);

    // Compter le nombre de stocks différents par client
    @Query("SELECT COUNT(DISTINCT s.idStock) FROM StockWallet s WHERE s.idClient = :idClient")
    Long countDistinctStocksByClient(@Param("idClient") Long idClient);
}