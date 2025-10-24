package MyLb.BackEnd.ServiceImp;

import MyLb.BackEnd.Model.Client;
import MyLb.BackEnd.Model.Investor;
import MyLb.BackEnd.Repository.InvestorRepository;
import MyLb.BackEnd.Service.ClientService;
import MyLb.BackEnd.Service.InvestorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.NoSuchElementException; // ⬅️ NOUVEL IMPORT NÉCESSAIRE

@Service
public class InvestorServiceImpl implements InvestorService {

    private final InvestorRepository investorRepository;
    private final ClientService clientService;

    @Autowired
    public InvestorServiceImpl(InvestorRepository investorRepository, ClientService clientService) {
        this.investorRepository = investorRepository;
        this.clientService = clientService;
    }

    @Override
    public Investor createInvestorProfile(Long clientId, Investor investorDetails) {
        // 1. Vérifier si le Client existe et déballer l'Optional.
        // 🚨 CORRECTION : Utilisation de orElseThrow pour extraire Client ou lancer une exception.
        Client client = clientService.getClientById(clientId)
                .orElseThrow(() -> new NoSuchElementException("Client non trouvé avec l'ID: " + clientId));

        // 2. Créer l'entité Investor
        Investor newInvestor = new Investor();
        newInvestor.setClientId(clientId);
        newInvestor.setCinNumber(investorDetails.getCinNumber());
        newInvestor.setGradeType("Bronze"); // Grade initial par défaut
        newInvestor.setClient(client); // Utilise l'objet 'client' déballé

        // Mettre à jour le rôle du client (très important)
        client.setRole("INVESTOR");
        clientService.saveClient(client);

        return investorRepository.save(newInvestor);
    }

    @Override
    public Optional<Investor> getInvestorByClientId(Long clientId) {
        return investorRepository.findById(clientId);
    }

    @Override
    public Investor updateInvestorGrade(Long clientId, String newGrade) {
        Investor investor = getInvestorByClientId(clientId)
                .orElseThrow(() -> new RuntimeException("Profil Investor non trouvé pour le Client ID: " + clientId));

        investor.setGradeType(newGrade);
        return investorRepository.save(investor);
    }

    @Override
    public void deleteInvestorProfile(Long clientId) {
        investorRepository.deleteById(clientId);

        // 🚨 CORRECTION : Utilisation de l'Optional pour récupérer le client de manière sécurisée
        Optional<Client> clientOptional = clientService.getClientById(clientId);

        // Optionnel : Revenir au rôle 'CIVIL' pour le client
        if (clientOptional.isPresent()) {
            Client client = clientOptional.get();
            client.setRole("CIVIL");
            clientService.saveClient(client);
        }
    }

    @Override
    public String calculateGrade(Long clientId) {
        // Logique métier : Calculer le grade en fonction du montant investi, de l'ancienneté, etc.
        return "Not Implemented";
    }
}