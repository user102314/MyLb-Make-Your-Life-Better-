package MyLb.BackEnd.ServiceImp;

import MyLb.BackEnd.Model.Entities.StockHistoryPrices;
import MyLb.BackEnd.Repository.StockHistoryPricesRepository;
import MyLb.BackEnd.Service.StockHistoryPricesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StockHistoryPricesServiceImpl implements StockHistoryPricesService {

    private final StockHistoryPricesRepository stockHistoryPricesRepository;

    @Autowired
    public StockHistoryPricesServiceImpl(StockHistoryPricesRepository stockHistoryPricesRepository) {
        this.stockHistoryPricesRepository = stockHistoryPricesRepository;
    }

    @Override
    public List<StockHistoryPrices> getStockHistory(Long idStock) {
        System.out.println("📊 [StockHistoryService] Getting all history for stock ID: " + idStock);
        return stockHistoryPricesRepository.findByIdStockOrderByDateCreationAsc(idStock);
    }

    @Override
    public Double getLastPriceByIdStock(Long idStock) {
        System.out.println("📊 [StockHistoryService] Getting last price for stock ID: " + idStock);
        StockHistoryPrices latest = stockHistoryPricesRepository.findLatestPriceByIdStock(idStock);
        return latest != null ? latest.getPrix() : null;
    }

    @Override
    public Double getMaxPriceLast24Hours(Long idStock) {
        System.out.println("📊 [StockHistoryService] Getting max price last 24h for stock ID: " + idStock);
        LocalDateTime startDate = LocalDateTime.now().minusHours(24);
        return stockHistoryPricesRepository.findMaxPriceLast24Hours(idStock, startDate);
    }

    @Override
    public Double getMinPriceLast24Hours(Long idStock) {
        System.out.println("📊 [StockHistoryService] Getting min price last 24h for stock ID: " + idStock);
        LocalDateTime startDate = LocalDateTime.now().minusHours(24);
        return stockHistoryPricesRepository.findMinPriceLast24Hours(idStock, startDate);
    }
}