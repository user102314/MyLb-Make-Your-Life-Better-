package MyLb.BackEnd.Repository;

import MyLb.BackEnd.Model.Entities.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * Récupérer toutes les notifications d'un client spécifique
     * Triées par date de création décroissante (plus récentes en premier)
     */
    List<Notification> findByIdClientOrderByDateCreationDesc(Long idClient);

    /**
     * Compter le nombre de notifications non lues pour un client
     */
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.idClient = :idClient AND n.etat = 0")
    Long countNonLuesParClient(@Param("idClient") Long idClient);

    /**
     * Récupérer les notifications non lues d'un client
     */
    @Query("SELECT n FROM Notification n WHERE n.idClient = :idClient AND n.etat = 0 ORDER BY n.dateCreation DESC")
    List<Notification> findNonLuesParClient(@Param("idClient") Long idClient);

    /**
     * Marquer toutes les notifications non lues comme lues pour un client
     */
    @Modifying
    @Query("UPDATE Notification n SET n.etat = 1 WHERE n.idClient = :idClient AND n.etat = 0")
    int marquerToutesCommeLues(@Param("idClient") Long idClient);

    /**
     * Supprimer les notifications anciennes (plus de X jours) pour un client
     */
    @Modifying
    @Query("DELETE FROM Notification n WHERE n.idClient = :idClient AND n.dateCreation < :dateLimit")
    int supprimerNotificationsAnciennes(@Param("idClient") Long idClient, @Param("dateLimit") LocalDateTime dateLimit);

    /**
     * Récupérer une notification spécifique par son ID et l'ID du client (sécurité)
     */
    @Query("SELECT n FROM Notification n WHERE n.idNotification = :idNotification AND n.idClient = :idClient")
    Notification findByIdAndClient(@Param("idNotification") Long idNotification, @Param("idClient") Long idClient);
}