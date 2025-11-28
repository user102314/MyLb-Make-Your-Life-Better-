package MyLb.BackEnd.Service;

import MyLb.BackEnd.Model.Entities.Message;

import java.util.List;

public interface MessageService {

    /**
     * Save a message to the database
     */
    Message saveMessage(Message message);

    /**
     * Save a message with specific parameters
     */
    Message saveMessage(Long sendFrom, Long sendTo, String messageContent);

    /**
     * Get conversation between two users
     */
    List<Message> getConversation(Long user1, Long user2);

    /**
     * Get all messages for a specific user
     */
    List<Message> getAllMessagesForUser(Long userId);

    /**
     * Get messages sent by a user
     */
    List<Message> getMessagesSentByUser(Long userId);

    /**
     * Get messages received by a user
     */
    List<Message> getMessagesReceivedByUser(Long userId);

    /**
     * Get conversation with admin (ID = 1)
     */
    List<Message> getConversationWithAdmin(Long userId);
}
