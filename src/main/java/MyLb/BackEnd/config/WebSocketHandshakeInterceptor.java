package MyLb.BackEnd.config;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

public class WebSocketHandshakeInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            
            // Try multiple methods to get the session
            HttpSession session = null;
            
            // Method 1: Try to get existing session
            try {
                session = servletRequest.getServletRequest().getSession(false);
            } catch (Exception e) {
                System.err.println("⚠️ [WebSocket] Error getting session (false): " + e.getMessage());
            }
            
            // Method 2: If no session, try to create one (but this might not work if session was created in different context)
            if (session == null) {
                try {
                    session = servletRequest.getServletRequest().getSession(true);
                    System.out.println("🔐 [WebSocket] Created new session: " + session.getId());
                } catch (Exception e) {
                    System.err.println("⚠️ [WebSocket] Error creating session: " + e.getMessage());
                }
            }
            
            // Try to extract user ID from session
            if (session != null) {
                // Get user ID from HTTP session (stored as Long in AuthController)
                Object userId = session.getAttribute("USER_ID");
                if (userId != null) {
                    String userIdStr = userId.toString();
                    attributes.put("userId", userIdStr);
                    System.out.println("✅ [WebSocket] User ID extracted from session: " + userIdStr + " (session: " + session.getId() + ")");
                    return true; // Successfully extracted, no need to try other methods
                } else {
                    System.out.println("⚠️ [WebSocket] Session exists but USER_ID attribute is null (session: " + session.getId() + ")");
                    // Log all session attributes for debugging
                    java.util.Enumeration<String> attributeNames = session.getAttributeNames();
                    System.out.println("📋 [WebSocket] Session attributes:");
                    while (attributeNames.hasMoreElements()) {
                        String attrName = attributeNames.nextElement();
                        System.out.println("   - " + attrName + " = " + session.getAttribute(attrName));
                    }
                }
            } else {
                System.out.println("⚠️ [WebSocket] No HTTP session available");
            }
            
            // Fallback: Try to get from request parameters (for SockJS or manual connections)
            String userIdParam = servletRequest.getServletRequest().getParameter("userId");
            if (userIdParam != null && !userIdParam.isEmpty()) {
                attributes.put("userId", userIdParam);
                System.out.println("✅ [WebSocket] User ID extracted from parameter: " + userIdParam);
                return true;
            }
            
            // Fallback: Try to get from query string
            String queryString = servletRequest.getServletRequest().getQueryString();
            if (queryString != null && queryString.contains("userId=")) {
                String[] params = queryString.split("&");
                for (String param : params) {
                    if (param.startsWith("userId=")) {
                        String userIdFromQuery = param.substring("userId=".length());
                        if (!userIdFromQuery.isEmpty()) {
                            attributes.put("userId", userIdFromQuery);
                            System.out.println("✅ [WebSocket] User ID extracted from query string: " + userIdFromQuery);
                            return true;
                        }
                    }
                }
            }
            
            System.out.println("❌ [WebSocket] Could not extract user ID from session, parameters, or query string");
        }
        
        return true; // Always allow handshake, even if userId is not found (will be handled later)
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                              WebSocketHandler wsHandler, Exception exception) {
        // Nothing to do after handshake
    }
}

