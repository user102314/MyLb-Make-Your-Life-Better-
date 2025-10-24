package MyLb.BackEnd.ServiceImp;

import MyLb.BackEnd.Model.Stock;
import MyLb.BackEnd.Repository.StockRepository;
import MyLb.BackEnd.Service.StockService;
import MyLb.BackEnd.dto.StockResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;

    @Autowired
    public StockServiceImpl(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    @Override
    public List<StockResponse> getAllStocks() {
        // Utilisation de findAllWithOwner() pour charger correctement les données
        List<Stock> stocks = stockRepository.findAllWithOwner();

        return stocks.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Méthode utilitaire pour la conversion Entité -> DTO (Mise à jour)
    private StockResponse convertToDto(Stock stock) {
        // Le DTO est instancié SANS l'ID du Owner
        return new StockResponse(
                stock.getIdStock(),
                stock.getNomStock(),
                stock.getStockDisponible(),
                stock.getStockReste(),
                stock.getPrixStock() // Le dernier argument (ownerId) est retiré
        );
    }

    // ... Autres méthodes
}