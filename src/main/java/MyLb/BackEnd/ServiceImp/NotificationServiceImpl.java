package MyLb.BackEnd.ServiceImp;

import MyLb.BackEnd.Model.Entities.Notification;
import MyLb.BackEnd.Repository.NotificationRepository;
import MyLb.BackEnd.Service.NotificationService;
import MyLb.BackEnd.dto.NotificationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Override
    public List<NotificationDTO> getAllNotificationsByClient(Long idClient) {
        List<Notification> notifications = notificationRepository.findByIdClientOrderByDateCreationDesc(idClient);
        return notifications.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Long countNonLues(Long idClient) {
        return notificationRepository.countNonLuesParClient(idClient);
    }

    @Override
    public NotificationDTO marquerCommeLue(Long idNotification, Long idClient) {
        Notification notification = notificationRepository.findByIdAndClient(idNotification, idClient);

        if (notification == null) {
            throw new RuntimeException("Notification non trouvée ou accès refusé");
        }

        if (notification.getEtat() == 0) {
            notification.marquerCommeLu();
            notification = notificationRepository.save(notification);
        }

        return convertToDTO(notification);
    }

    @Override
    public Map<String, Object> marquerToutesCommeLues(Long idClient) {
        int count = notificationRepository.marquerToutesCommeLues(idClient);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", count + " notification(s) marquée(s) comme lue(s)");
        response.put("count", count);

        return response;
    }

    @Override
    public Map<String, Object> supprimerNotification(Long idNotification, Long idClient) {
        Notification notification = notificationRepository.findByIdAndClient(idNotification, idClient);

        if (notification == null) {
            throw new RuntimeException("Notification non trouvée ou accès refusé");
        }

        notificationRepository.delete(notification);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Notification supprimée avec succès");

        return response;
    }

    @Override
    public Map<String, Object> nettoyerNotificationsAnciennes(Long idClient) {
        // Supprimer les notifications de plus de 30 jours
        LocalDateTime dateLimit = LocalDateTime.now().minusDays(30);
        int count = notificationRepository.supprimerNotificationsAnciennes(idClient, dateLimit);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", count + " notification(s) ancienne(s) supprimée(s)");
        response.put("count", count);

        return response;
    }

    @Override
    public NotificationDTO creerNotification(String sujet, String description, Long idClient) {
        Notification notification = new Notification(sujet, description, idClient);
        notification = notificationRepository.save(notification);
        return convertToDTO(notification);
    }

    // Méthode utilitaire pour convertir Entity vers DTO
    private NotificationDTO convertToDTO(Notification notification) {
        return new NotificationDTO(
                notification.getIdNotification(),
                notification.getSujet(),
                notification.getDescription(),
                notification.getDateCreation(),
                notification.getEtat(),
                notification.getIdClient()
        );
    }
}