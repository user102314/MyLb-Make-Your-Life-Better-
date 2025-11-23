package MyLb.BackEnd.Service;

import MyLb.BackEnd.Model.Entities.OwnerPO;
import java.util.List;
import java.util.Optional;

public interface OwnerPOService {

    /**
     * Créer un profil OwnerPO pour un client
     */
    OwnerPO createOwnerProfile(Long clientId, OwnerPO ownerDetails);

    /**
     * Récupérer UN OwnerPO d'un client (le premier trouvé)
     */
    Optional<OwnerPO> getOwnerPOByClientId(Long clientId);

    /**
     * Récupérer UN OwnerPO d'une company (le premier trouvé)
     */
    Optional<OwnerPO> getOwnerPOByCompanyId(Long companyId);

    /**
     * Récupérer un OwnerPO spécifique par clientId ET companyId
     */
    Optional<OwnerPO> getOwnerPOByClientIdAndCompanyId(Long clientId, Long companyId);

    /**
     * Récupérer TOUS les OwnerPO d'un client
     */
    List<OwnerPO> getAllOwnerPOsByClientId(Long clientId);

    /**
     * Mettre à jour le CIN pour TOUS les OwnerPO d'un client
     */
    OwnerPO updateOwnerCIN(Long clientId, String newCinNumber);

    /**
     * Mettre à jour le CIN pour un OwnerPO spécifique
     */
    OwnerPO updateOwnerCINForCompany(Long clientId, Long companyId, String newCinNumber);

    /**
     * Supprimer TOUS les OwnerPO d'un client
     */
    void deleteOwnerProfile(Long clientId);

    /**
     * Supprimer un OwnerPO spécifique
     */
    void deleteOwnerProfileForCompany(Long clientId, Long companyId);

    /**
     * Compter le nombre de companies d'un client
     */
    long countCompaniesByClientId(Long clientId);
}