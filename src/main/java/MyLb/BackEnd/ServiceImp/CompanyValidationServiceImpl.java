package MyLb.BackEnd.ServiceImp;

import MyLb.BackEnd.Model.Entities.CompanyValidation;
import MyLb.BackEnd.Repository.CompanyValidationRepository;
import MyLb.BackEnd.Service.CompanyValidationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import jakarta.persistence.EntityNotFoundException;

import java.io.IOException;
import java.util.Optional;

@Service
public class CompanyValidationServiceImpl implements CompanyValidationService {

    private final CompanyValidationRepository validationRepository;

    @Autowired
    public CompanyValidationServiceImpl(CompanyValidationRepository validationRepository) {
        this.validationRepository = validationRepository;
    }

    @Override
    public CompanyValidation saveValidationData(Long companyId, CompanyValidation validationData,
                                                MultipartFile certificatImmatriculation,
                                                MultipartFile pieceIdentiteRepresentantLegal,
                                                MultipartFile statutsSociete,
                                                MultipartFile justificatifDomiciliationCommerciale) throws IOException {

        Optional<CompanyValidation> existingValidation = validationRepository.findById(companyId);
        CompanyValidation validation = existingValidation.orElse(new CompanyValidation());
        validation.setCompanyId(companyId);

        // 1. Mise à jour des données textuelles
        if (validationData != null) {
            validation.setNomLegalComplet(validationData.getNomLegalComplet());
            validation.setNumeroImmatriculation(validationData.getNumeroImmatriculation());
            validation.setAdresseSiegeSocial(validationData.getAdresseSiegeSocial());
            validation.setNomPrenomPresidentLegal(validationData.getNomPrenomPresidentLegal());
            validation.setNumeroTvaTaxe(validationData.getNumeroTvaTaxe());
        }

        // 2. Traitement des fichiers (MultipartFile -> byte[])
        if (certificatImmatriculation != null && !certificatImmatriculation.isEmpty()) {
            validation.setCertificatImmatriculation(certificatImmatriculation.getBytes());
        }
        if (pieceIdentiteRepresentantLegal != null && !pieceIdentiteRepresentantLegal.isEmpty()) {
            validation.setPieceIdentiteRepresentantLegal(pieceIdentiteRepresentantLegal.getBytes());
        }
        if (statutsSociete != null && !statutsSociete.isEmpty()) {
            validation.setStatutsSociete(statutsSociete.getBytes());
        }
        if (justificatifDomiciliationCommerciale != null && !justificatifDomiciliationCommerciale.isEmpty()) {
            validation.setJustificatifDomiciliationCommerciale(justificatifDomiciliationCommerciale.getBytes());
        }

        return validationRepository.save(validation);
    }

    @Override
    public CompanyValidation getValidationData(Long companyId) {
        return validationRepository.findById(companyId)
                .orElseThrow(() -> new EntityNotFoundException("Données de validation (KYC) non trouvées pour la société: " + companyId));
    }
    @Override
    public CompanyValidation saveValidation(CompanyValidation validation) {
        // La logique de sauvegarde utilise le Repository JPA
        // Ici, vous pourriez ajouter de la logique métier (horodatage, etc.)
        return validationRepository.save(validation);
    }
    @Override
    public boolean isLegallyComplete(Long companyId) {
        try {
            CompanyValidation validation = getValidationData(companyId);

            // Vérification des champs critiques (texte et documents binaires)
            return validation.getNomLegalComplet() != null && !validation.getNomLegalComplet().isEmpty() &&
                    validation.getNumeroImmatriculation() != null && !validation.getNumeroImmatriculation().isEmpty() &&
                    validation.getCertificatImmatriculation() != null &&
                    validation.getPieceIdentiteRepresentantLegal() != null;

        } catch (EntityNotFoundException e) {
            return false;
        }
    }
}