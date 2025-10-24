package MyLb.BackEnd.Repository;

import MyLb.BackEnd.Model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // <--- MAKE SURE THIS IS IMPORTED
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {

    // NOUVELLE MÉTHODE: Utilise JOIN FETCH pour charger l'OwnerPO immédiatement
    @Query("SELECT s FROM Stock s JOIN FETCH s.ownerPO")
    List<Stock> findAllWithOwner();
}