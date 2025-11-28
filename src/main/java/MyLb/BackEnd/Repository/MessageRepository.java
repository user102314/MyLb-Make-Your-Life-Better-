package MyLb.BackEnd.Repository;

import MyLb.BackEnd.Model.Entities.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    /**
     * Find all messages between two users (bidirectional conversation)
     */
    @Query("SELECT m FROM Message m WHERE " +
           "(m.sendFrom = :user1 AND m.sendTo = :user2) OR " +
           "(m.sendFrom = :user2 AND m.sendTo = :user1) " +
           "ORDER BY m.date ASC")
    List<Message> findConversationBetweenUsers(@Param("user1") Long user1, @Param("user2") Long user2);

    /**
     * Find all messages sent by a specific user
     */
    @Query("SELECT m FROM Message m WHERE m.sendFrom = :userId ORDER BY m.date DESC")
    List<Message> findMessagesSentByUser(@Param("userId") Long userId);

    /**
     * Find all messages received by a specific user
     */
    @Query("SELECT m FROM Message m WHERE m.sendTo = :userId ORDER BY m.date DESC")
    List<Message> findMessagesReceivedByUser(@Param("userId") Long userId);

    /**
     * Find all messages for a specific user (sent or received)
     */
    @Query("SELECT m FROM Message m WHERE m.sendFrom = :userId OR m.sendTo = :userId ORDER BY m.date DESC")
    List<Message> findAllMessagesForUser(@Param("userId") Long userId);
}
