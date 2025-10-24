package MyLb.BackEnd.Service;

import MyLb.BackEnd.dto.StockResponse; // NOUVEL IMPORT
import java.util.List;

public interface StockService {
    // La méthode retourne maintenant des DTOs
    List<StockResponse> getAllStocks();
    // ... autres méthodes (createStock, etc.)
}