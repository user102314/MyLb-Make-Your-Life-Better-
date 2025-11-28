package MyLb.BackEnd.Controller;

import MyLb.BackEnd.Model.Entities.Message;
import MyLb.BackEnd.Model.Entities.Client;
import MyLb.BackEnd.Service.MessageService;
import MyLb.BackEnd.Service.ClientService;
import MyLb.BackEnd.config.WebSocketEventListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@CrossOrigin(origins = "*")
public class MessageController {

    private static final Long ADMIN_ID = 2L;

    @Autowired
    private MessageService messageService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private WebSocketEventListener webSocketEventListener;

    /**
     * WebSocket endpoint to send message to admin
     * Client sends to: /app/message/toAdmin
     * Message is broadcast to: /topic/admin
     */
    @MessageMapping("/message/toAdmin")
    @SendTo("/topic/admin")
    public Message sendMessageToAdmin(@Payload Message message, SimpMessageHeaderAccessor headerAccessor) {
        Long userId = null;
        try {
            // Extract user ID from WebSocket session (more secure than trusting client message)
            String sessionId = headerAccessor.getSessionId();
            
            // Method 1: Try to get from WebSocketEventListener (tracked during subscription)
            String userIdFromSession = webSocketEventListener.getUserIdFromSession(sessionId);
            if (userIdFromSession != null) {
                try {
                    userId = Long.parseLong(userIdFromSession);
                    System.out.println("✅ [MessageController] User ID extracted from WebSocketEventListener: " + userId);
                } catch (NumberFormatException e) {
                    System.err.println("❌ [MessageController] Invalid user ID format from WebSocketEventListener: " + userIdFromSession);
                }
            }
            
            // Method 2: Try to extract from session attributes (set during handshake)
            if (userId == null) {
                Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
                if (sessionAttributes != null) {
                    Object userIdObj = sessionAttributes.get("userId");
                    if (userIdObj != null) {
                        try {
                            userId = Long.parseLong(userIdObj.toString());
                            System.out.println("✅ [MessageController] User ID extracted from session attributes: " + userId);
                        } catch (NumberFormatException e) {
                            System.err.println("❌ [MessageController] Invalid user ID in session attributes: " + userIdObj);
                        }
                    }
                }
            }
            
            // Method 3: Try to extract from STOMP headers (if client sends it)
            if (userId == null) {
                try {
                    Object userIdFromHeader = headerAccessor.getFirstNativeHeader("userId");
                    if (userIdFromHeader != null) {
                        userId = Long.parseLong(userIdFromHeader.toString());
                        System.out.println("✅ [MessageController] User ID extracted from STOMP header: " + userId);
                    }
                } catch (Exception e) {
                    System.err.println("⚠️ [MessageController] Could not extract userId from STOMP headers: " + e.getMessage());
                }
            }
            
            // Method 4: Fallback - use sendFrom from message (less secure, but better than nothing)
            if (userId == null) {
                if (message.getSendFrom() != null) {
                    userId = message.getSendFrom();
                    System.out.println("⚠️ [MessageController] Using sendFrom from message (fallback): " + userId);
                }
            }
            
            // Final validation
            if (userId == null) {
                String errorMsg = "Unable to determine user ID. Please ensure you are properly authenticated.";
                System.err.println("❌ [MessageController] " + errorMsg);
                messagingTemplate.convertAndSend("/queue/errors/" + sessionId, 
                    Map.of("error", errorMsg, "type", "AUTHENTICATION_ERROR"));
                return null; // Don't throw exception to avoid breaking WebSocket connection
            }
            
            // Check if the client exists
            if (clientService.getClientById(userId).isEmpty()) {
                String errorMsg = "Client with ID " + userId + " does not exist in database";
                System.err.println("❌ [MessageController] " + errorMsg);
                messagingTemplate.convertAndSend("/queue/errors/" + sessionId, 
                    Map.of("error", errorMsg, "type", "CLIENT_NOT_FOUND", "userId", userId));
                return null; // Don't throw exception
            }
            
            // Set sender and recipient
            message.setSendFrom(userId);
            message.setSendTo(ADMIN_ID);
            
            // Validate admin exists
            if (clientService.getClientById(ADMIN_ID).isEmpty()) {
                String errorMsg = "Admin with ID " + ADMIN_ID + " does not exist";
                System.err.println("❌ [MessageController] " + errorMsg);
                messagingTemplate.convertAndSend("/queue/errors/" + sessionId, 
                    Map.of("error", errorMsg, "type", "ADMIN_NOT_FOUND"));
                return null;
            }
            
            // Validate message content
            if (message.getMessage() == null || message.getMessage().trim().isEmpty()) {
                String errorMsg = "Message content cannot be empty";
                System.err.println("❌ [MessageController] " + errorMsg);
                messagingTemplate.convertAndSend("/queue/errors/" + sessionId, 
                    Map.of("error", errorMsg, "type", "VALIDATION_ERROR"));
                return null;
            }
            
            // Save message to database
            System.out.println("💾 [MessageController] Tentative d'insertion du message en base de données...");
            System.out.println("   - sendFrom: " + userId);
            System.out.println("   - sendTo: " + ADMIN_ID);
            System.out.println("   - message: " + (message.getMessage().length() > 50 ? message.getMessage().substring(0, 50) + "..." : message.getMessage()));
            
            Message savedMessage = messageService.saveMessage(message);
            
            if (savedMessage != null && savedMessage.getId() != null) {
                System.out.println("✅ [MessageController] Message inséré avec succès en base de données!");
                System.out.println("   - Message ID (DB): " + savedMessage.getId());
                System.out.println("   - sendFrom: " + savedMessage.getSendFrom());
                System.out.println("   - sendTo: " + savedMessage.getSendTo());
                System.out.println("   - date: " + savedMessage.getDate());
            } else {
                System.err.println("❌ [MessageController] Message sauvegardé mais ID est null!");
            }
            
            // Send to specific user queue for admin
            messagingTemplate.convertAndSend("/queue/admin", savedMessage);
            
            return savedMessage;
        } catch (Exception e) {
            System.err.println("❌ [MessageController] Unexpected error sending message to admin: " + e.getMessage());
            e.printStackTrace();
            // Send error to user's session
            String sessionId = headerAccessor.getSessionId();
            messagingTemplate.convertAndSend("/queue/errors/" + sessionId, 
                Map.of("error", "An unexpected error occurred: " + e.getMessage(), "type", "INTERNAL_ERROR"));
            return null; // Don't throw exception to avoid breaking WebSocket connection
        }
    }

    /**
     * WebSocket endpoint to send message from admin to user
     * Client sends to: /app/message/fromAdmin
     */
    @MessageMapping("/message/fromAdmin")
    public Message sendMessageFromAdmin(@Payload Message message) {
        try {
            // Set sender as admin
            message.setSendFrom(ADMIN_ID);
            
            // Validate admin exists
            if (clientService.getClientById(ADMIN_ID).isEmpty()) {
                throw new IllegalArgumentException("Admin with ID " + ADMIN_ID + " does not exist");
            }
            
            // Validate recipient exists
            if (message.getSendTo() == null) {
                throw new IllegalArgumentException("sendTo cannot be null");
            }
            
            if (clientService.getClientById(message.getSendTo()).isEmpty()) {
                throw new IllegalArgumentException("Recipient with ID " + message.getSendTo() + " does not exist");
            }
            
            // Save message to database
            System.out.println("💾 [MessageController] Tentative d'insertion du message (admin -> user)...");
            System.out.println("   - sendFrom (admin): " + ADMIN_ID);
            System.out.println("   - sendTo: " + message.getSendTo());
            System.out.println("   - message: " + (message.getMessage() != null && message.getMessage().length() > 50 ? 
                message.getMessage().substring(0, 50) + "..." : message.getMessage()));
            
            Message savedMessage = messageService.saveMessage(message);
            
            if (savedMessage != null && savedMessage.getId() != null) {
                System.out.println("✅ [MessageController] Message inséré avec succès (admin -> user)!");
                System.out.println("   - Message ID (DB): " + savedMessage.getId());
            } else {
                System.err.println("❌ [MessageController] Message sauvegardé mais ID est null!");
            }
            
            // Send to specific user queue
            messagingTemplate.convertAndSend("/queue/user/" + message.getSendTo(), savedMessage);
            
            return savedMessage;
        } catch (Exception e) {
            System.err.println("Error sending message from admin: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to send message: " + e.getMessage(), e);
        }
    }

    /**
     * WebSocket endpoint for general message sending between users
     * Client sends to: /app/message/send
     */
    @MessageMapping("/message/send")
    public Message sendMessage(@Payload Message message) {
        try {
            // Validate sendFrom exists
            if (message.getSendFrom() == null) {
                throw new IllegalArgumentException("sendFrom cannot be null");
            }
            
            if (clientService.getClientById(message.getSendFrom()).isEmpty()) {
                throw new IllegalArgumentException("Sender with ID " + message.getSendFrom() + " does not exist");
            }
            
            // Validate sendTo exists
            if (message.getSendTo() == null) {
                throw new IllegalArgumentException("sendTo cannot be null");
            }
            
            if (clientService.getClientById(message.getSendTo()).isEmpty()) {
                throw new IllegalArgumentException("Recipient with ID " + message.getSendTo() + " does not exist");
            }
            
            // Save message to database
            System.out.println("💾 [MessageController] Tentative d'insertion du message (user -> user)...");
            System.out.println("   - sendFrom: " + message.getSendFrom());
            System.out.println("   - sendTo: " + message.getSendTo());
            System.out.println("   - message: " + (message.getMessage() != null && message.getMessage().length() > 50 ? 
                message.getMessage().substring(0, 50) + "..." : message.getMessage()));
            
            Message savedMessage = messageService.saveMessage(message);
            
            if (savedMessage != null && savedMessage.getId() != null) {
                System.out.println("✅ [MessageController] Message inséré avec succès (user -> user)!");
                System.out.println("   - Message ID (DB): " + savedMessage.getId());
            } else {
                System.err.println("❌ [MessageController] Message sauvegardé mais ID est null!");
            }
            
            // Send to specific recipient's queue
            messagingTemplate.convertAndSend("/queue/user/" + message.getSendTo(), savedMessage);
            
            return savedMessage;
        } catch (Exception e) {
            System.err.println("Error sending message: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to send message: " + e.getMessage(), e);
        }
    }

    /**
     * REST endpoint to get conversation between user and admin
     */
    @GetMapping("/api/messages/conversation/admin/{userId}")
    public ResponseEntity<List<Message>> getConversationWithAdmin(@PathVariable Long userId) {
        List<Message> messages = messageService.getConversationWithAdmin(userId);
        return ResponseEntity.ok(messages);
    }

    /**
     * REST endpoint to get conversation between two users
     */
    @GetMapping("/api/messages/conversation/{user1}/{user2}")
    public ResponseEntity<List<Message>> getConversation(
            @PathVariable Long user1, 
            @PathVariable Long user2) {
        List<Message> messages = messageService.getConversation(user1, user2);
        return ResponseEntity.ok(messages);
    }

    /**
     * REST endpoint to get all messages for a user
     */
    @GetMapping("/api/messages/user/{userId}")
    public ResponseEntity<List<Message>> getAllMessagesForUser(@PathVariable Long userId) {
        List<Message> messages = messageService.getAllMessagesForUser(userId);
        return ResponseEntity.ok(messages);
    }

    /**
     * REST endpoint to send a message (alternative to WebSocket)
     */
    @PostMapping("/api/messages/send")
    public ResponseEntity<?> sendMessageRest(@RequestBody Message message) {
        try {
            // Validate sendFrom exists
            if (message.getSendFrom() == null) {
                return ResponseEntity.badRequest().body("sendFrom cannot be null");
            }
            
            if (clientService.getClientById(message.getSendFrom()).isEmpty()) {
                return ResponseEntity.badRequest()
                    .body("Sender with ID " + message.getSendFrom() + " does not exist");
            }
            
            // Validate sendTo exists
            if (message.getSendTo() == null) {
                return ResponseEntity.badRequest().body("sendTo cannot be null");
            }
            
            if (clientService.getClientById(message.getSendTo()).isEmpty()) {
                return ResponseEntity.badRequest()
                    .body("Recipient with ID " + message.getSendTo() + " does not exist");
            }
            
            // Save message to database
            System.out.println("💾 [MessageController] Tentative d'insertion du message (REST API)...");
            System.out.println("   - sendFrom: " + message.getSendFrom());
            System.out.println("   - sendTo: " + message.getSendTo());
            System.out.println("   - message: " + (message.getMessage() != null && message.getMessage().length() > 50 ? 
                message.getMessage().substring(0, 50) + "..." : message.getMessage()));
            
            Message savedMessage = messageService.saveMessage(message);
            
            if (savedMessage != null && savedMessage.getId() != null) {
                System.out.println("✅ [MessageController] Message inséré avec succès (REST API)!");
                System.out.println("   - Message ID (DB): " + savedMessage.getId());
            } else {
                System.err.println("❌ [MessageController] Message sauvegardé mais ID est null!");
            }
            
            // Also send via WebSocket if recipient is connected
            messagingTemplate.convertAndSend("/queue/user/" + message.getSendTo(), savedMessage);
            
            return ResponseEntity.ok(savedMessage);
        } catch (Exception e) {
            System.err.println("Error sending message via REST: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error sending message: " + e.getMessage());
        }
    }

    /**
     * REST endpoint to get list of connected users (for admin)
     */
    @GetMapping("/api/messages/connected-users")
    public ResponseEntity<?> getConnectedUsers() {
        try {
            Map<String, String> connectedUserSessions = webSocketEventListener.getConnectedUsers();
            List<Map<String, Object>> connectedUsersList = new ArrayList<>();
            
            for (Map.Entry<String, String> entry : connectedUserSessions.entrySet()) {
                String userIdStr = entry.getKey();
                try {
                    Long userId = Long.parseLong(userIdStr);
                    Optional<Client> clientOpt = clientService.getClientById(userId);
                    
                    if (clientOpt.isPresent()) {
                        Client client = clientOpt.get();
                        Map<String, Object> userInfo = new HashMap<>();
                        userInfo.put("clientId", client.getClientId());
                        userInfo.put("firstName", client.getFirstName());
                        userInfo.put("lastName", client.getLastName());
                        userInfo.put("email", client.getEmail());
                        userInfo.put("role", client.getRole());
                        userInfo.put("isConnected", true);
                        userInfo.put("sessionId", entry.getValue());
                        connectedUsersList.add(userInfo);
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Invalid user ID format: " + userIdStr);
                }
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("connectedUsers", connectedUsersList);
            response.put("count", connectedUsersList.size());
            response.put("message", "Liste des utilisateurs connectés récupérée avec succès");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error getting connected users: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "message", "Erreur lors de la récupération des utilisateurs connectés: " + e.getMessage()));
        }
    }

    /**
     * REST endpoint to check if a specific user is connected
     */
    @GetMapping("/api/messages/user/{userId}/status")
    public ResponseEntity<?> getUserConnectionStatus(@PathVariable Long userId) {
        try {
            boolean isConnected = webSocketEventListener.isUserConnected(userId.toString());
            
            Map<String, Object> response = new HashMap<>();
            response.put("userId", userId);
            response.put("isConnected", isConnected);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error checking user connection status: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "message", "Erreur lors de la vérification du statut: " + e.getMessage()));
        }
    }

    /**
     * REST endpoint to verify messages in database (for debugging)
     * GET /api/messages/verify/{userId} - Get all messages for a user
     */
    @GetMapping("/api/messages/verify/{userId}")
    public ResponseEntity<?> verifyMessagesInDatabase(@PathVariable Long userId) {
        try {
            System.out.println("🔍 [MessageController] Vérification des messages pour l'utilisateur: " + userId);
            
            // Get all messages sent by user
            List<Message> sentMessages = messageService.getMessagesSentByUser(userId);
            System.out.println("   - Messages envoyés: " + sentMessages.size());
            
            // Get all messages received by user
            List<Message> receivedMessages = messageService.getMessagesReceivedByUser(userId);
            System.out.println("   - Messages reçus: " + receivedMessages.size());
            
            // Get all messages for user
            List<Message> allMessages = messageService.getAllMessagesForUser(userId);
            System.out.println("   - Total messages: " + allMessages.size());
            
            // Get conversation with admin
            List<Message> adminConversation = messageService.getConversationWithAdmin(userId);
            System.out.println("   - Messages avec admin: " + adminConversation.size());
            
            // Build detailed response
            List<Map<String, Object>> messagesDetails = new ArrayList<>();
            for (Message msg : allMessages) {
                Map<String, Object> msgDetail = new HashMap<>();
                msgDetail.put("id", msg.getId());
                msgDetail.put("sendFrom", msg.getSendFrom());
                msgDetail.put("sendTo", msg.getSendTo());
                msgDetail.put("message", msg.getMessage());
                msgDetail.put("date", msg.getDate());
                msgDetail.put("isSent", msg.getSendFrom().equals(userId));
                messagesDetails.add(msgDetail);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("userId", userId);
            response.put("statistics", Map.of(
                "sentCount", sentMessages.size(),
                "receivedCount", receivedMessages.size(),
                "totalCount", allMessages.size(),
                "adminConversationCount", adminConversation.size()
            ));
            response.put("messages", messagesDetails);
            response.put("sentMessages", sentMessages);
            response.put("receivedMessages", receivedMessages);
            response.put("adminConversation", adminConversation);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("❌ [MessageController] Erreur lors de la vérification des messages: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "message", "Erreur lors de la vérification: " + e.getMessage()));
        }
    }

    /**
     * REST endpoint to get the last N messages (for debugging)
     * GET /api/messages/recent?limit=10
     */
    @GetMapping("/api/messages/recent")
    public ResponseEntity<?> getRecentMessages(@RequestParam(defaultValue = "10") int limit) {
        try {
            System.out.println("🔍 [MessageController] Récupération des " + limit + " derniers messages...");
            
            // Get all messages and sort by date descending, then limit
            List<Message> allMessages = messageService.getAllMessagesForUser(1L); // Get all messages (using admin ID as placeholder)
            
            // Since we don't have a direct "get all messages" method, we'll use a workaround
            // For now, return messages from admin conversation as example
            List<Message> recentMessages = messageService.getConversationWithAdmin(1L);
            
            // Sort by date descending and limit
            recentMessages.sort((a, b) -> b.getDate().compareTo(a.getDate()));
            if (recentMessages.size() > limit) {
                recentMessages = recentMessages.subList(0, limit);
            }
            
            System.out.println("   - Messages récupérés: " + recentMessages.size());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("limit", limit);
            response.put("count", recentMessages.size());
            response.put("messages", recentMessages);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("❌ [MessageController] Erreur lors de la récupération des messages récents: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "message", "Erreur: " + e.getMessage()));
        }
    }
}
