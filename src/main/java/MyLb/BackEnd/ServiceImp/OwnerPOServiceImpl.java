package MyLb.BackEnd.ServiceImp;

import MyLb.BackEnd.Model.Entities.Client;
import MyLb.BackEnd.Model.Entities.OwnerPO;
import MyLb.BackEnd.Model.Entities.OwnerPOId;
import MyLb.BackEnd.Repository.OwnerPORepository;
import MyLb.BackEnd.Service.ClientService;
import MyLb.BackEnd.Service.OwnerPOService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.NoSuchElementException;

@Service
public class OwnerPOServiceImpl implements OwnerPOService {

    private final OwnerPORepository ownerPORepository;
    private final ClientService clientService;

    @Autowired
    public OwnerPOServiceImpl(OwnerPORepository ownerPORepository, ClientService clientService) {
        this.ownerPORepository = ownerPORepository;
        this.clientService = clientService;
    }

    @Override
    @Transactional
    public OwnerPO createOwnerProfile(Long clientId, OwnerPO ownerDetails) {
        System.out.println("📥 [OwnerPOService] Création du profil Owner pour Client ID: " + clientId);

        // Le client doit exister pour créer un OwnerPO
        Client client = clientService.getClientById(clientId)
                .orElseThrow(() -> new NoSuchElementException("Client non trouvé avec l'ID: " + clientId));

        System.out.println("   ✅ Client trouvé: " + client.getFirstName() + " " + client.getLastName());

        // Vérifier que companyId est fourni
        if (ownerDetails.getCompanyId() == null) {
            throw new IllegalArgumentException("L'ID de la company est obligatoire pour créer un OwnerPO");
        }

        // Vérifier si OwnerPO existe déjà pour cette combinaison
        boolean exists = ownerPORepository.existsByClientIdAndCompanyId(clientId, ownerDetails.getCompanyId());
        if (exists) {
            System.out.println("   ⚠️ OwnerPO existe déjà pour Client " + clientId + " et Company " + ownerDetails.getCompanyId());
            throw new IllegalStateException("Un profil OwnerPO existe déjà pour ce client et cette company");
        }

        // Créer le nouveau OwnerPO
        OwnerPO newOwner = new OwnerPO();
        newOwner.setClientId(clientId);
        newOwner.setCompanyId(ownerDetails.getCompanyId());
        newOwner.setCinNumber(ownerDetails.getCinNumber());
        newOwner.setRole(ownerDetails.getRole() != null ? ownerDetails.getRole() : "OWNER");
        newOwner.setClient(client);

        // Mettre à jour le rôle du client (seulement si ce n'est pas déjà PO)
        if (!"PO".equals(client.getRole())) {
            client.setRole("PO");
            clientService.saveClient(client);
            System.out.println("   ✅ Rôle du client mis à jour: PO");
        }

        OwnerPO savedOwner = ownerPORepository.save(newOwner);
        System.out.println("   ✅ OwnerPO créé avec succès");

        return savedOwner;
    }

    @Override
    public Optional<OwnerPO> getOwnerPOByClientId(Long clientId) {
        System.out.println("🔍 [OwnerPOService] Recherche OwnerPO pour Client ID: " + clientId);

        // Comme un client peut avoir plusieurs companies, on retourne le premier
        List<OwnerPO> ownerPOList = ownerPORepository.findByClientId(clientId);

        if (ownerPOList.isEmpty()) {
            System.out.println("   ❌ Aucun OwnerPO trouvé pour Client ID: " + clientId);
            return Optional.empty();
        }

        System.out.println("   ✅ " + ownerPOList.size() + " OwnerPO trouvé(s) pour Client ID: " + clientId);
        return Optional.of(ownerPOList.get(0)); // Retourner le premier
    }

    @Override
    public Optional<OwnerPO> getOwnerPOByCompanyId(Long companyId) {
        System.out.println("🔍 [OwnerPOService] Recherche OwnerPO pour Company ID: " + companyId);

        // Une company peut avoir plusieurs propriétaires, on retourne le premier
        List<OwnerPO> ownerPOList = ownerPORepository.findByCompanyId(companyId);

        if (ownerPOList.isEmpty()) {
            System.out.println("   ❌ Aucun OwnerPO trouvé pour Company ID: " + companyId);
            return Optional.empty();
        }

        System.out.println("   ✅ " + ownerPOList.size() + " OwnerPO trouvé(s) pour Company ID: " + companyId);
        return Optional.of(ownerPOList.get(0)); // Retourner le premier
    }

    /**
     * Nouvelle méthode pour récupérer un OwnerPO spécifique par clientId ET companyId
     */
    public Optional<OwnerPO> getOwnerPOByClientIdAndCompanyId(Long clientId, Long companyId) {
        System.out.println("🔍 [OwnerPOService] Recherche OwnerPO pour Client ID: " + clientId +
                " et Company ID: " + companyId);

        return ownerPORepository.findByClientIdAndCompanyId(clientId, companyId);
    }

    /**
     * Récupérer tous les OwnerPO d'un client
     */
    public List<OwnerPO> getAllOwnerPOsByClientId(Long clientId) {
        System.out.println("🔍 [OwnerPOService] Récupération de tous les OwnerPO pour Client ID: " + clientId);

        List<OwnerPO> ownerPOList = ownerPORepository.findByClientId(clientId);
        System.out.println("   ✅ " + ownerPOList.size() + " OwnerPO trouvé(s)");

        return ownerPOList;
    }

    @Override
    @Transactional
    public OwnerPO updateOwnerCIN(Long clientId, String newCinNumber) {
        System.out.println("📝 [OwnerPOService] Mise à jour CIN pour Client ID: " + clientId);

        // Récupérer tous les OwnerPO du client
        List<OwnerPO> ownerPOList = ownerPORepository.findByClientId(clientId);

        if (ownerPOList.isEmpty()) {
            throw new RuntimeException("Profil OwnerPO non trouvé pour le Client ID: " + clientId);
        }

        // Mettre à jour le CIN pour TOUS les OwnerPO du client
        for (OwnerPO owner : ownerPOList) {
            owner.setCinNumber(newCinNumber);
            ownerPORepository.save(owner);
        }

        System.out.println("   ✅ CIN mis à jour pour " + ownerPOList.size() + " OwnerPO");

        // Retourner le premier
        return ownerPOList.get(0);
    }

    /**
     * Mettre à jour le CIN pour un OwnerPO spécifique
     */
    @Transactional
    public OwnerPO updateOwnerCINForCompany(Long clientId, Long companyId, String newCinNumber) {
        System.out.println("📝 [OwnerPOService] Mise à jour CIN pour Client ID: " + clientId +
                " et Company ID: " + companyId);

        OwnerPOId ownerPOId = new OwnerPOId(clientId, companyId);

        OwnerPO owner = ownerPORepository.findById(ownerPOId)
                .orElseThrow(() -> new RuntimeException(
                        "Profil OwnerPO non trouvé pour Client ID: " + clientId +
                                " et Company ID: " + companyId));

        owner.setCinNumber(newCinNumber);
        OwnerPO savedOwner = ownerPORepository.save(owner);

        System.out.println("   ✅ CIN mis à jour avec succès");

        return savedOwner;
    }

    @Override
    @Transactional
    public void deleteOwnerProfile(Long clientId) {
        System.out.println("🗑️ [OwnerPOService] Suppression du profil Owner pour Client ID: " + clientId);

        // Récupérer tous les OwnerPO du client
        List<OwnerPO> ownerPOList = ownerPORepository.findByClientId(clientId);

        if (ownerPOList.isEmpty()) {
            throw new NoSuchElementException("Profil OwnerPO non trouvé pour le Client ID: " + clientId);
        }

        // Supprimer TOUS les OwnerPO du client
        ownerPORepository.deleteAll(ownerPOList);

        System.out.println("   ✅ " + ownerPOList.size() + " OwnerPO supprimé(s)");

        // Récupérer le Client pour changer son rôle
        Optional<Client> clientOptional = clientService.getClientById(clientId);

        if (clientOptional.isPresent()) {
            Client client = clientOptional.get();
            client.setRole("CIVIL"); // Revenir au rôle de base
            clientService.saveClient(client);
            System.out.println("   ✅ Rôle du client restauré: CIVIL");
        }
    }

    /**
     * Supprimer un OwnerPO spécifique par clientId ET companyId
     */
    @Transactional
    public void deleteOwnerProfileForCompany(Long clientId, Long companyId) {
        System.out.println("🗑️ [OwnerPOService] Suppression OwnerPO pour Client ID: " + clientId +
                " et Company ID: " + companyId);

        OwnerPOId ownerPOId = new OwnerPOId(clientId, companyId);

        OwnerPO ownerToDelete = ownerPORepository.findById(ownerPOId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Profil OwnerPO non trouvé pour Client ID: " + clientId +
                                " et Company ID: " + companyId));

        ownerPORepository.delete(ownerToDelete);

        System.out.println("   ✅ OwnerPO supprimé avec succès");

        // Vérifier si le client a encore d'autres OwnerPO
        List<OwnerPO> remainingOwnerPOs = ownerPORepository.findByClientId(clientId);

        if (remainingOwnerPOs.isEmpty()) {
            // Plus aucun OwnerPO, restaurer le rôle CIVIL
            Optional<Client> clientOptional = clientService.getClientById(clientId);
            if (clientOptional.isPresent()) {
                Client client = clientOptional.get();
                client.setRole("CIVIL");
                clientService.saveClient(client);
                System.out.println("   ✅ Rôle du client restauré: CIVIL (aucun OwnerPO restant)");
            }
        } else {
            System.out.println("   ℹ️ Client conserve le rôle PO (" + remainingOwnerPOs.size() + " OwnerPO restant(s))");
        }
    }

    /**
     * Compter le nombre de companies d'un client
     */
    public long countCompaniesByClientId(Long clientId) {
        return ownerPORepository.countCompaniesByClientId(clientId);
    }
}