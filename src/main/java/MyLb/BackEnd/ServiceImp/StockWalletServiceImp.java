// MyLb.BackEnd.ServiceImp.StockWalletServiceImp.java
package MyLb.BackEnd.ServiceImp;

import MyLb.BackEnd.Model.Entities.StockWallet;
import MyLb.BackEnd.Repository.StockWalletRepository;
import MyLb.BackEnd.Service.StockWalletService;
import MyLb.BackEnd.dto.StockWalletResponse;
import MyLb.BackEnd.dto.CreateStockWalletRequest;
import MyLb.BackEnd.dto.UpdateStockWalletRequest;
import MyLb.BackEnd.dto.StockWalletStats;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class StockWalletServiceImp implements StockWalletService {

    private final StockWalletRepository stockWalletRepository;

    @Autowired
    public StockWalletServiceImp(StockWalletRepository stockWalletRepository) {
        this.stockWalletRepository = stockWalletRepository;
    }

    @Override
    public StockWalletResponse addStockToWallet(CreateStockWalletRequest request) {
        // Vérifier si le stock existe déjà pour ce client
        stockWalletRepository.findByIdStockAndIdClient(request.getIdStock(), request.getIdClient())
                .ifPresent(existingStock -> {
                    throw new RuntimeException("Ce stock est déjà dans votre wallet");
                });

        // Calculer le prix total
        Double prixTotal = request.getPrix() * request.getQuantite();

        // Créer et sauvegarder le stock
        StockWallet stockWallet = new StockWallet(
                request.getIdClient(),
                request.getIdStock(),
                request.getNomStock(),
                request.getPrix(),
                request.getQuantite(),
                prixTotal
        );

        StockWallet savedStock = stockWalletRepository.save(stockWallet);

        return convertToResponse(savedStock);
    }

    @Override
    public List<StockWalletResponse> getClientStocks(Long idClient) {
        List<StockWallet> stocks = stockWalletRepository.findByIdClient(idClient);
        return stocks.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public StockWalletResponse getStockWalletById(Long id, Long idClient) {
        StockWallet stockWallet = stockWalletRepository.findByIdAndIdClient(id, idClient)
                .orElseThrow(() -> new RuntimeException("Stock non trouvé dans votre wallet"));
        return convertToResponse(stockWallet);
    }

    @Override
    public StockWalletResponse updateStockWallet(Long id, Long idClient, UpdateStockWalletRequest request) {
        StockWallet stockWallet = stockWalletRepository.findByIdAndIdClient(id, idClient)
                .orElseThrow(() -> new RuntimeException("Stock non trouvé dans votre wallet"));

        // Mettre à jour les champs
        if (request.getQuantite() != null) {
            stockWallet.setQuantite(request.getQuantite());
        }
        if (request.getPrix() != null) {
            stockWallet.setPrix(request.getPrix());
        }

        // Recalculer le prix total
        stockWallet.setPrixTotal(stockWallet.getPrix() * stockWallet.getQuantite());

        StockWallet updatedStock = stockWalletRepository.save(stockWallet);
        return convertToResponse(updatedStock);
    }

    @Override
    public void deleteStockFromWallet(Long id, Long idClient) {
        if (!stockWalletRepository.existsByIdAndIdClient(id, idClient)) {
            throw new RuntimeException("Stock non trouvé dans votre wallet");
        }
        stockWalletRepository.deleteByIdAndIdClient(id, idClient);
    }

    @Override
    public Double getTotalInvestment(Long idClient) {
        Double total = stockWalletRepository.getTotalInvestmentByClient(idClient);
        return total != null ? total : 0.0;
    }

    @Override
    public StockWalletStats getWalletStats(Long idClient) {
        List<StockWallet> stocks = stockWalletRepository.findByIdClient(idClient);
        Double totalInvestment = getTotalInvestment(idClient);
        Long distinctStocks = stockWalletRepository.countDistinctStocksByClient(idClient);
        Double totalQuantity = stocks.stream()
                .mapToDouble(StockWallet::getQuantite)
                .sum();

        return new StockWalletStats(
                stocks.size(),
                distinctStocks,
                totalInvestment,
                totalQuantity
        );
    }

    private StockWalletResponse convertToResponse(StockWallet stockWallet) {
        return new StockWalletResponse(
                stockWallet.getId(),
                stockWallet.getIdClient(),
                stockWallet.getIdStock(),
                stockWallet.getNomStock(),
                stockWallet.getPrix(),
                stockWallet.getQuantite(),
                stockWallet.getPrixTotal(),
                stockWallet.getDateAchat()
        );
    }
}