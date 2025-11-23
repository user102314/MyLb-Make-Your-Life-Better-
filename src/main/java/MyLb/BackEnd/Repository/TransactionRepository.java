package MyLb.BackEnd.Repository;

import MyLb.BackEnd.Model.Entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByIdClientOrderByDateCreationDesc(Long idClient);

    List<Transaction> findByIdClientAndTypeOperationOrderByDateCreationDesc(Long idClient, String typeOperation);

    List<Transaction> findByIdClientAndDateCreationBetweenOrderByDateCreationDesc(
            Long idClient, LocalDateTime startDate, LocalDateTime endDate);

    List<Transaction> findByDateCreationBefore(LocalDateTime date);

    Long countByIdClient(Long idClient);

    @Query("SELECT COALESCE(SUM(t.montant), 0) FROM Transaction t WHERE t.idClient = :idClient AND t.typeOperation = 'DEPOSIT' AND t.statut = 'COMPLETED'")
    Double getTotalDepositsByClient(@Param("idClient") Long idClient);

    @Query("SELECT COALESCE(SUM(t.montant), 0) FROM Transaction t WHERE t.idClient = :idClient AND t.typeOperation = 'WITHDRAW' AND t.statut = 'COMPLETED'")
    Double getTotalWithdrawalsByClient(@Param("idClient") Long idClient);

    @Query("SELECT t FROM Transaction t WHERE t.idClient = :idClient AND t.dateCreation >= :startDate ORDER BY t.dateCreation DESC")
    List<Transaction> findRecentTransactions(@Param("idClient") Long idClient, @Param("startDate") LocalDateTime startDate);
}