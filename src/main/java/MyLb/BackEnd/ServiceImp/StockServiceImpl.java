package MyLb.BackEnd.ServiceImp;

import MyLb.BackEnd.Model.Entities.Company;
import MyLb.BackEnd.Model.Entities.OwnerPO;
import MyLb.BackEnd.Model.Entities.OwnerPOId;
import MyLb.BackEnd.Model.Entities.Stock;
import MyLb.BackEnd.Repository.CompanyRepository;
import MyLb.BackEnd.Repository.OwnerPORepository;
import MyLb.BackEnd.Repository.StockRepository;
import MyLb.BackEnd.Service.StockService;
import MyLb.BackEnd.dto.CreateStockRequest;
import MyLb.BackEnd.dto.StockResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;
    private final CompanyRepository companyRepository;
    private final OwnerPORepository ownerPORepository;

    @Autowired
    public StockServiceImpl(StockRepository stockRepository,
                            CompanyRepository companyRepository,
                            OwnerPORepository ownerPORepository) {
        this.stockRepository = stockRepository;
        this.companyRepository = companyRepository;
        this.ownerPORepository = ownerPORepository;
    }

    private Stock findByIdOrThrow(Long idStock) {
        return stockRepository.findById(idStock)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Stock non trouvé avec l'ID: " + idStock
                ));
    }

    @Override
    public List<StockResponse> getAllStocks() {
        List<Stock> stocks = stockRepository.findAll();
        return stocks.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public StockResponse creerStock(CreateStockRequest request) {
        System.out.println("🔍 [Service] Début création stock");
        System.out.println("   Company ID: " + request.getIdComponey());

        // 1. Vérifier que la company existe
        Company company = companyRepository.findById(request.getIdComponey())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Company non trouvée avec l'ID: " + request.getIdComponey()
                ));

        System.out.println("   ✅ Company trouvée: " + company.getCompanyName());
        System.out.println("   Owner ID de la company: " + company.getOwnerID());

        // 2. Utiliser l'owner de la company (au lieu de celui de la requête)
        Long ownerIdToUse = company.getOwnerID();
        Long companyIdToUse = request.getIdComponey();

        System.out.println("   🔄 Recherche OwnerPO pour Client ID: " + ownerIdToUse +
                " et Company ID: " + companyIdToUse);

        // 3. Créer la clé composite pour rechercher OwnerPO
        OwnerPOId ownerPOId = new OwnerPOId(ownerIdToUse, companyIdToUse);

        OwnerPO owner = ownerPORepository.findById(ownerPOId)
                .orElseThrow(() -> {
                    System.err.println("   ❌ OwnerPO non trouvé pour la clé composite: " + ownerPOId);
                    return new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Propriétaire non trouvé pour Client ID: " + ownerIdToUse +
                                    " et Company ID: " + companyIdToUse +
                                    ". Assurez-vous que la company a été créée correctement."
                    );
                });

        System.out.println("   ✅ OwnerPO trouvé: Client ID=" + owner.getClientId() +
                ", Company ID=" + owner.getCompanyId());

        // 4. Créer le nouveau stock
        Stock stock = new Stock();
        stock.setNomStock(request.getNomStock());
        stock.setStockDisponible(request.getStockDisponible());
        stock.setStockReste(request.getStockReste());
        stock.setPrixStock(request.getPrixStock());
        stock.setEtat(request.getEtat() != null ? request.getEtat() : "DISPONIBLE");
        stock.setIdComponey(request.getIdComponey());
        stock.setOwnerPO(owner);

        // 5. Sauvegarder le stock
        Stock savedStock = stockRepository.save(stock);

        System.out.println("   ✅ Stock créé avec ID: " + savedStock.getIdStock());

        return convertToDto(savedStock);
    }

    @Override
    public List<StockResponse> getStocksByCompany(Long idComponey) {
        // Vérifier que la company existe
        if (!companyRepository.existsById(idComponey)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Company non trouvée avec l'ID: " + idComponey
            );
        }

        List<Stock> stocks = stockRepository.findByIdComponey(idComponey);
        return stocks.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public StockResponse modifierEtatById(Long idStock, String nouvelEtat) {
        Stock stock = findByIdOrThrow(idStock);
        stock.setEtat(nouvelEtat);
        Stock updatedStock = stockRepository.save(stock);
        return convertToDto(updatedStock);
    }

    @Override
    @Transactional
    public StockResponse modifierStockDisponible(Long idStock, Integer nouveauStockDisponible) {
        Stock stock = findByIdOrThrow(idStock);
        stock.setStockDisponible(nouveauStockDisponible);
        Stock updatedStock = stockRepository.save(stock);
        return convertToDto(updatedStock);
    }

    @Override
    @Transactional
    public StockResponse modifierStockReste(Long idStock, Integer nouveauStockReste) {
        Stock stock = findByIdOrThrow(idStock);
        stock.setStockReste(nouveauStockReste);
        Stock updatedStock = stockRepository.save(stock);
        return convertToDto(updatedStock);
    }

    @Override
    @Transactional
    public StockResponse modifierPrixStock(Long idStock, Double nouveauPrixStock) {
        Stock stock = findByIdOrThrow(idStock);
        stock.setPrixStock(nouveauPrixStock);
        Stock updatedStock = stockRepository.save(stock);
        return convertToDto(updatedStock);
    }

    private StockResponse convertToDto(Stock stock) {
        return new StockResponse(
                stock.getIdStock(),
                stock.getNomStock(),
                stock.getStockDisponible(),
                stock.getStockReste(),
                stock.getPrixStock(),
                stock.getEtat(),
                stock.getIdComponey(),
                stock.getOwnerPO() != null ? stock.getOwnerPO().getClientId() : null
        );
    }
}