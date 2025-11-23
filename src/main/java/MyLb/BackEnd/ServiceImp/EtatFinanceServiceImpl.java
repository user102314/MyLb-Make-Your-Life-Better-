package MyLb.BackEnd.ServiceImp;

import MyLb.BackEnd.Model.Entities.EtatFinance;
import MyLb.BackEnd.Repository.EtatFinanceRepository;
import MyLb.BackEnd.Service.EtatFinanceService;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import jakarta.persistence.EntityNotFoundException;

import java.io.IOException;
import java.util.Optional;

@Service
public class EtatFinanceServiceImpl implements EtatFinanceService {

    private final EtatFinanceRepository etatFinanceRepository;

    @Autowired
    public EtatFinanceServiceImpl(EtatFinanceRepository etatFinanceRepository) {
        this.etatFinanceRepository = etatFinanceRepository;
    }

    /**
     * Méthode principale de sauvegarde/mise à jour des données financières, y compris le rapport.
     */
    @Override
    @Transactional
    public EtatFinance saveFinancialData(Long companyId, EtatFinance financialData, MultipartFile rapport) throws IOException {

        // 1. Chercher ou créer l'entité par companyId
        // C'est correct, car companyId est la clé.
        Optional<EtatFinance> existingData = etatFinanceRepository.findById(companyId);

        // Si les données existent, les récupérer. Sinon, créer une nouvelle entité.
        EtatFinance entity = existingData.orElse(new EtatFinance());

        // ************************************************
        // 2. CORRECTION CRITIQUE : AFFECTER LA CLÉ PRIMAIRE
        // ************************************************
        // L'ID doit toujours être défini (même s'il est déjà existant)
        // car si c'est une nouvelle entité, companyId = null par défaut.
        entity.setCompanyId(companyId);

        // 3. Transférer les données numériques
        if (financialData != null) {
            entity.setActifTotal(financialData.getActifTotal());
            entity.setActifImmobilise(financialData.getActifImmobilise());
            entity.setActifCirculant(financialData.getActifCirculant());
            entity.setPassifTotal(financialData.getPassifTotal());
            entity.setCapitauxPropres(financialData.getCapitauxPropres());
            entity.setDettes(financialData.getDettes());
            entity.setProduitsTotal(financialData.getProduitsTotal());
            entity.setChargesTotal(financialData.getChargesTotal());
            entity.setResultatNet(financialData.getResultatNet());
            entity.setChiffreAffaires(financialData.getChiffreAffaires());
            entity.setFluxOperationnels(financialData.getFluxOperationnels());
            entity.setFluxInvestissement(financialData.getFluxInvestissement());
            entity.setFluxFinancement(financialData.getFluxFinancement());
            entity.setVariationNetteTresorerie(financialData.getVariationNetteTresorerie());

            // NOTE: Si le 'financialData' contient aussi le 'companyId',
            // il est préférable d'utiliser la variable 'companyId' du paramètre de la méthode
            // pour garantir la cohérence avec le findById() et l'URL/le contexte.
            // Si vous aviez décommenté 'entity.setCompanyId(financialData.getCompanyId());'
            // cela n'aurait pas fonctionné si 'financialData.getCompanyId()' était nul.
        }

        // 4. Gestion du fichier rapport
        if (rapport != null && !rapport.isEmpty()) {
            entity.setRapportEtatFinancier(rapport.getBytes());
        }

        // 5. Sauvegarde
        // Maintenant, 'entity' a un ID (companyId) défini, Hibernate peut le persister.
        return etatFinanceRepository.save(entity);
    }

    /**
     * Méthode pour récupérer les données financières.
     */
    @Override
    public EtatFinance getFinancialData(Long companyId) {
        return etatFinanceRepository.findById(companyId)
                .orElseThrow(() -> new EntityNotFoundException("Données financières non trouvées pour la société: " + companyId));
    }
}