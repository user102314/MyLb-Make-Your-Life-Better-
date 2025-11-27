package MyLb.BackEnd.Service;

import MyLb.BackEnd.dto.NotificationDTO;
import java.util.List;
import java.util.Map;

public interface NotificationService {

    /**
     * Récupérer toutes les notifications du client connecté
     */
    List<NotificationDTO> getAllNotificationsByClient(Long idClient);

    /**
     * Compter le nombre de notifications non lues
     */
    Long countNonLues(Long idClient);

    /**
     * Marquer une notification comme lue
     */
    NotificationDTO marquerCommeLue(Long idNotification, Long idClient);

    /**
     * Marquer toutes les notifications non lues comme lues
     */
    Map<String, Object> marquerToutesCommeLues(Long idClient);

    /**
     * Supprimer une notification spécifique
     */
    Map<String, Object> supprimerNotification(Long idNotification, Long idClient);

    /**
     * Nettoyer les notifications anciennes (plus de 30 jours)
     */
    Map<String, Object> nettoyerNotificationsAnciennes(Long idClient);

    /**
     * Créer une nouvelle notification (pour utilisation interne)
     */
    NotificationDTO creerNotification(String sujet, String description, Long idClient);
}