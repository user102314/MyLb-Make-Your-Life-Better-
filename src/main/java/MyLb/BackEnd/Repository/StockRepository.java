package MyLb.BackEnd.Repository;

import MyLb.BackEnd.Model.Entities.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
    // Trouver tous les stocks d'une company
    List<Stock> findByIdComponey(Long idComponey);

    // Trouver tous les stocks d'un propriétaire
    List<Stock> findByOwnerPO_ClientId(Long clientId);
}