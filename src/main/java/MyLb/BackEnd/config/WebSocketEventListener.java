package MyLb.BackEnd.config;

import MyLb.BackEnd.Service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketEventListener {

    // Map to store connected users: userId -> sessionId
    private final Map<String, String> connectedUsers = new ConcurrentHashMap<>();
    
    // Map to store session to user mapping: sessionId -> userId
    private final Map<String, String> sessionToUser = new ConcurrentHashMap<>();
    
    private final SimpMessagingTemplate messagingTemplate;
    
    @Autowired(required = false)
    private ClientService clientService;

    public WebSocketEventListener(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        
        System.out.println("🔌 [WebSocket] New connection: " + sessionId);
        
        // Try to extract user ID from headers or session attributes (set during handshake)
        Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
        if (sessionAttributes != null) {
            Object userId = sessionAttributes.get("userId");
            if (userId != null) {
                String userIdStr = userId.toString();
                connectedUsers.put(userIdStr, sessionId);
                sessionToUser.put(sessionId, userIdStr);
                
                System.out.println("✅ [WebSocket] User " + userIdStr + " connected (session: " + sessionId + ")");
                
                // Notify admin about new user connection
                notifyAdminUserStatusChanged(userIdStr, true);
            } else {
                System.out.println("⚠️ [WebSocket] Session " + sessionId + " connected but no userId in session attributes");
                // Log all session attributes for debugging
                System.out.println("📋 [WebSocket] Session attributes for " + sessionId + ":");
                for (Map.Entry<String, Object> entry : sessionAttributes.entrySet()) {
                    System.out.println("   - " + entry.getKey() + " = " + entry.getValue());
                }
            }
        } else {
            System.out.println("⚠️ [WebSocket] Session " + sessionId + " connected but no session attributes available");
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        
        System.out.println("🔌 [WebSocket] Disconnection: " + sessionId);
        
        // Find user ID from session
        String userId = sessionToUser.remove(sessionId);
        if (userId != null) {
            connectedUsers.remove(userId);
            System.out.println("❌ [WebSocket] User " + userId + " disconnected (session: " + sessionId + ")");
            
            // Notify admin about user disconnection
            notifyAdminUserStatusChanged(userId, false);
        }
    }

    @EventListener
    public void handleWebSocketSubscribeListener(SessionSubscribeEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        String destination = headerAccessor.getDestination();
        
        System.out.println("📡 [WebSocket] Subscribe: session=" + sessionId + ", destination=" + destination);
        
        // If subscribing to user-specific queue, extract user ID
        if (destination != null && destination.startsWith("/queue/user/")) {
            String userIdStr = destination.substring("/queue/user/".length());
            
            // Also store in session attributes for later retrieval
            Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
            if (sessionAttributes != null) {
                sessionAttributes.put("userId", userIdStr);
                System.out.println("💾 [WebSocket] Stored userId in session attributes: " + userIdStr);
            }
            
            // Validate user exists in database if ClientService is available
            boolean isValidUser = true;
            if (clientService != null) {
                try {
                    Long userId = Long.parseLong(userIdStr);
                    if (clientService.getClientById(userId).isEmpty()) {
                        System.err.println("⚠️ [WebSocket] User ID " + userIdStr + " does not exist in database, but allowing subscription");
                        isValidUser = false;
                        // Still allow subscription, but log warning
                    } else {
                        System.out.println("✅ [WebSocket] User " + userIdStr + " validated in database");
                    }
                } catch (NumberFormatException e) {
                    System.err.println("⚠️ [WebSocket] Invalid user ID format: " + userIdStr);
                    isValidUser = false;
                }
            }
            
            // Store the mapping even if user doesn't exist (to track the session)
            connectedUsers.put(userIdStr, sessionId);
            sessionToUser.put(sessionId, userIdStr);
            
            if (isValidUser) {
                System.out.println("✅ [WebSocket] User " + userIdStr + " subscribed to their queue (session: " + sessionId + ")");
                notifyAdminUserStatusChanged(userIdStr, true);
            } else {
                System.out.println("⚠️ [WebSocket] Session " + sessionId + " subscribed with invalid user ID: " + userIdStr);
            }
        }
        
        // If subscribing to admin queue, try to identify as admin
        if (destination != null && (destination.equals("/queue/admin") || destination.equals("/topic/admin"))) {
            // Try to get user ID from session
            Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
            if (sessionAttributes != null) {
                Object userId = sessionAttributes.get("userId");
                if (userId != null && userId.toString().equals("1")) {
                    System.out.println("👑 [WebSocket] Admin connected");
                }
            }
        }
    }

    /**
     * Notify admin when a user connects or disconnects
     */
    private void notifyAdminUserStatusChanged(String userId, boolean isConnected) {
        try {
            Map<String, Object> notification = Map.of(
                "userId", userId,
                "isConnected", isConnected,
                "timestamp", System.currentTimeMillis()
            );
            
            // Send to admin queue
            messagingTemplate.convertAndSend("/queue/admin", Map.of(
                "type", "userStatusChange",
                "data", notification
            ));
            
            // Also broadcast to admin topic
            messagingTemplate.convertAndSend("/topic/admin", Map.of(
                "type", "userStatusChange",
                "data", notification
            ));
        } catch (Exception e) {
            System.err.println("Error notifying admin about user status: " + e.getMessage());
        }
    }

    /**
     * Get list of connected user IDs
     */
    public Map<String, String> getConnectedUsers() {
        return new ConcurrentHashMap<>(connectedUsers);
    }

    /**
     * Check if a user is connected
     */
    public boolean isUserConnected(String userId) {
        return connectedUsers.containsKey(userId);
    }

    /**
     * Get number of connected users
     */
    public int getConnectedUsersCount() {
        return connectedUsers.size();
    }

    /**
     * Get user ID from session ID
     */
    public String getUserIdFromSession(String sessionId) {
        return sessionToUser.get(sessionId);
    }
}

