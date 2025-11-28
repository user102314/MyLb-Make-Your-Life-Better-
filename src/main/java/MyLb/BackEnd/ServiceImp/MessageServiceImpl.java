package MyLb.BackEnd.ServiceImp;

import MyLb.BackEnd.Model.Entities.Message;
import MyLb.BackEnd.Repository.MessageRepository;
import MyLb.BackEnd.Service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class MessageServiceImpl implements MessageService {

    private static final Long ADMIN_ID = 1L;

    @Autowired
    private MessageRepository messageRepository;

    @Override
    public Message saveMessage(Message message) {
        // Validation et logging avant insertion
        System.out.println("📝 [MessageService] Début de l'insertion du message...");
        
        // Validation des champs obligatoires
        if (message == null) {
            System.err.println("❌ [MessageService] Message est null - insertion annulée");
            throw new IllegalArgumentException("Message cannot be null");
        }
        
        // Validation sendFrom
        if (message.getSendFrom() == null) {
            System.err.println("❌ [MessageService] sendFrom est null - insertion annulée");
            throw new IllegalArgumentException("sendFrom cannot be null");
        }
        System.out.println("✅ [MessageService] sendFrom: " + message.getSendFrom());
        
        // Validation sendTo
        if (message.getSendTo() == null) {
            System.err.println("❌ [MessageService] sendTo est null - insertion annulée");
            throw new IllegalArgumentException("sendTo cannot be null");
        }
        System.out.println("✅ [MessageService] sendTo: " + message.getSendTo());
        
        // Validation message content
        if (message.getMessage() == null || message.getMessage().trim().isEmpty()) {
            System.err.println("❌ [MessageService] Message content est null ou vide - insertion annulée");
            throw new IllegalArgumentException("Message content cannot be null or empty");
        }
        System.out.println("✅ [MessageService] Message content: " + 
            (message.getMessage().length() > 50 ? message.getMessage().substring(0, 50) + "..." : message.getMessage()));
        
        // Validation et initialisation de la date
        if (message.getDate() == null) {
            message.setDate(LocalDateTime.now());
            System.out.println("✅ [MessageService] Date initialisée à: " + message.getDate());
        } else {
            System.out.println("✅ [MessageService] Date fournie: " + message.getDate());
        }
        
        // Log des informations complètes avant insertion
        System.out.println("📋 [MessageService] Informations du message à insérer:");
        System.out.println("   - sendFrom: " + message.getSendFrom());
        System.out.println("   - sendTo: " + message.getSendTo());
        System.out.println("   - message length: " + message.getMessage().length() + " caractères");
        System.out.println("   - date: " + message.getDate());
        
        try {
            // Insertion dans la base de données
            Message savedMessage = messageRepository.save(message);
            
            // Vérification après insertion
            if (savedMessage.getId() != null) {
                System.out.println("✅ [MessageService] Message inséré avec succès!");
                System.out.println("   - Message ID: " + savedMessage.getId());
                System.out.println("   - sendFrom: " + savedMessage.getSendFrom());
                System.out.println("   - sendTo: " + savedMessage.getSendTo());
                System.out.println("   - date: " + savedMessage.getDate());
                System.out.println("   - content preview: " + 
                    (savedMessage.getMessage().length() > 50 ? 
                        savedMessage.getMessage().substring(0, 50) + "..." : 
                        savedMessage.getMessage()));
            } else {
                System.err.println("⚠️ [MessageService] Message sauvegardé mais ID est null");
            }
            
            return savedMessage;
        } catch (Exception e) {
            System.err.println("❌ [MessageService] Erreur lors de l'insertion du message:");
            System.err.println("   - sendFrom: " + message.getSendFrom());
            System.err.println("   - sendTo: " + message.getSendTo());
            System.err.println("   - Erreur: " + e.getMessage());
            e.printStackTrace();
            throw e; // Re-throw pour que le contrôleur puisse gérer l'erreur
        }
    }

    @Override
    public Message saveMessage(Long sendFrom, Long sendTo, String messageContent) {
        System.out.println("📝 [MessageService] Création d'un nouveau message avec paramètres...");
        System.out.println("   - sendFrom: " + sendFrom);
        System.out.println("   - sendTo: " + sendTo);
        System.out.println("   - content length: " + (messageContent != null ? messageContent.length() : 0) + " caractères");
        
        // Validation des paramètres
        if (sendFrom == null) {
            throw new IllegalArgumentException("sendFrom cannot be null");
        }
        if (sendTo == null) {
            throw new IllegalArgumentException("sendTo cannot be null");
        }
        if (messageContent == null || messageContent.trim().isEmpty()) {
            throw new IllegalArgumentException("messageContent cannot be null or empty");
        }
        
        Message message = new Message(sendFrom, sendTo, messageContent);
        return saveMessage(message); // Utiliser la méthode principale pour bénéficier du logging
    }

    @Override
    public List<Message> getConversation(Long user1, Long user2) {
        return messageRepository.findConversationBetweenUsers(user1, user2);
    }

    @Override
    public List<Message> getAllMessagesForUser(Long userId) {
        return messageRepository.findAllMessagesForUser(userId);
    }

    @Override
    public List<Message> getMessagesSentByUser(Long userId) {
        return messageRepository.findMessagesSentByUser(userId);
    }

    @Override
    public List<Message> getMessagesReceivedByUser(Long userId) {
        return messageRepository.findMessagesReceivedByUser(userId);
    }

    @Override
    public List<Message> getConversationWithAdmin(Long userId) {
        return messageRepository.findConversationBetweenUsers(userId, ADMIN_ID);
    }
}
