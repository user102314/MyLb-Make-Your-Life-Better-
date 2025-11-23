package MyLb.BackEnd.Repository;

import MyLb.BackEnd.Model.Entities.StockHistoryPrices;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StockHistoryPricesRepository extends JpaRepository<StockHistoryPrices, Long> {

    /**
     * Get all history by stock ID
     */
    List<StockHistoryPrices> findByIdStockOrderByDateCreationAsc(Long idStock);

    /**
     * Get last price by stock ID
     */
    @Query("SELECT shp FROM StockHistoryPrices shp WHERE shp.idStock = :idStock ORDER BY shp.dateCreation DESC LIMIT 1")
    StockHistoryPrices findLatestPriceByIdStock(@Param("idStock") Long idStock);

    /**
     * Get max price for the last day
     */
    @Query("SELECT MAX(shp.prix) FROM StockHistoryPrices shp WHERE shp.idStock = :idStock AND shp.dateCreation >= :startDate")
    Double findMaxPriceLast24Hours(@Param("idStock") Long idStock, @Param("startDate") LocalDateTime startDate);

    /**
     * Get min price for the last day
     */
    @Query("SELECT MIN(shp.prix) FROM StockHistoryPrices shp WHERE shp.idStock = :idStock AND shp.dateCreation >= :startDate")
    Double findMinPriceLast24Hours(@Param("idStock") Long idStock, @Param("startDate") LocalDateTime startDate);
}