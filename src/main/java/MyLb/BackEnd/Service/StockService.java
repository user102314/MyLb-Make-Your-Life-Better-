package MyLb.BackEnd.Service;

import MyLb.BackEnd.dto.CreateStockRequest;
import MyLb.BackEnd.dto.StockResponse;
import java.util.List;

public interface StockService {
    List<StockResponse> getAllStocks();

    StockResponse creerStock(CreateStockRequest request);

    List<StockResponse> getStocksByCompany(Long idComponey);

    StockResponse modifierEtatById(Long idStock, String nouvelEtat);

    StockResponse modifierStockDisponible(Long idStock, Integer nouveauStockDisponible);

    StockResponse modifierStockReste(Long idStock, Integer nouveauStockReste);

    StockResponse modifierPrixStock(Long idStock, Double nouveauPrixStock);
}