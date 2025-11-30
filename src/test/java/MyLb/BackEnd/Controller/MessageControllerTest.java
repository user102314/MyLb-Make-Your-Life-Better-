package MyLb.BackEnd.Controller;

import MyLb.BackEnd.Model.Entities.Message;
import MyLb.BackEnd.Model.Entities.Client;
import MyLb.BackEnd.Service.MessageService;
import MyLb.BackEnd.Service.ClientService;
import MyLb.BackEnd.config.WebSocketEventListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageControllerTest {

    @Mock
    private MessageService messageService;

    @Mock
    private ClientService clientService;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private WebSocketEventListener webSocketEventListener;

    @Mock
    private SimpMessageHeaderAccessor headerAccessor;

    @InjectMocks
    private MessageController messageController;

    private Message testMessage;
    private Client testClient;

    @BeforeEach
    void setUp() {
        testMessage = new Message();
        testMessage.setId(1L);
        testMessage.setSendFrom(1L);
        testMessage.setSendTo(2L);
        testMessage.setMessage("Test message");
        testMessage.setDate(LocalDateTime.now());

        testClient = new Client();
        testClient.setClientId(1L);
        testClient.setFirstName("John");
        testClient.setLastName("Doe");
        testClient.setEmail("john@test.com");
        testClient.setRole("USER");
    }

    @Test
    void testSendMessageToAdmin_Success() {
        // Arrange
        when(headerAccessor.getSessionId()).thenReturn("session123");
        when(webSocketEventListener.getUserIdFromSession("session123")).thenReturn("1");
        when(clientService.getClientById(1L)).thenReturn(Optional.of(testClient));
        when(clientService.getClientById(2L)).thenReturn(Optional.of(new Client()));
        when(messageService.saveMessage(any(Message.class))).thenReturn(testMessage);

        // Act
        Message result = messageController.sendMessageToAdmin(testMessage, headerAccessor);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getSendFrom());
        assertEquals(2L, result.getSendTo());
        verify(messageService, times(1)).saveMessage(any(Message.class));
        verify(messagingTemplate, times(1)).convertAndSend("/queue/admin", testMessage);
    }

    @Test
    void testSendMessageFromAdmin_Success() {
        // Arrange
        testMessage.setSendFrom(2L); // Admin
        testMessage.setSendTo(1L); // User

        when(clientService.getClientById(2L)).thenReturn(Optional.of(new Client()));
        when(clientService.getClientById(1L)).thenReturn(Optional.of(testClient));
        when(messageService.saveMessage(any(Message.class))).thenReturn(testMessage);

        // Act
        Message result = messageController.sendMessageFromAdmin(testMessage);

        // Assert
        assertNotNull(result);
        assertEquals(2L, result.getSendFrom());
        assertEquals(1L, result.getSendTo());
        verify(messageService, times(1)).saveMessage(testMessage);
        verify(messagingTemplate, times(1)).convertAndSend("/queue/user/1", testMessage);
    }

    @Test
    void testSendMessage_Success() {
        // Arrange
        when(clientService.getClientById(1L)).thenReturn(Optional.of(testClient));
        when(clientService.getClientById(2L)).thenReturn(Optional.of(new Client()));
        when(messageService.saveMessage(any(Message.class))).thenReturn(testMessage);

        // Act
        Message result = messageController.sendMessage(testMessage);

        // Assert
        assertNotNull(result);
        verify(messageService, times(1)).saveMessage(testMessage);
        verify(messagingTemplate, times(1)).convertAndSend("/queue/user/2", testMessage);
    }

    @Test
    void testGetConversationWithAdmin_Success() {
        // Arrange
        List<Message> messages = Arrays.asList(testMessage);
        when(messageService.getConversationWithAdmin(1L)).thenReturn(messages);

        // Act
        ResponseEntity<List<Message>> response = messageController.getConversationWithAdmin(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(messageService, times(1)).getConversationWithAdmin(1L);
    }

    @Test
    void testGetConversation_Success() {
        // Arrange
        List<Message> messages = Arrays.asList(testMessage);
        when(messageService.getConversation(1L, 2L)).thenReturn(messages);

        // Act
        ResponseEntity<List<Message>> response = messageController.getConversation(1L, 2L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(messageService, times(1)).getConversation(1L, 2L);
    }

    @Test
    void testGetAllMessagesForUser_Success() {
        // Arrange
        List<Message> messages = Arrays.asList(testMessage);
        when(messageService.getAllMessagesForUser(1L)).thenReturn(messages);

        // Act
        ResponseEntity<List<Message>> response = messageController.getAllMessagesForUser(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(messageService, times(1)).getAllMessagesForUser(1L);
    }

    @Test
    void testSendMessageRest_Success() {
        // Arrange
        when(clientService.getClientById(1L)).thenReturn(Optional.of(testClient));
        when(clientService.getClientById(2L)).thenReturn(Optional.of(new Client()));
        when(messageService.saveMessage(any(Message.class))).thenReturn(testMessage);

        // Act
        ResponseEntity<?> response = messageController.sendMessageRest(testMessage);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(messageService, times(1)).saveMessage(testMessage);
        verify(messagingTemplate, times(1)).convertAndSend("/queue/user/2", testMessage);
    }

    @Test
    void testSendMessageRest_InvalidSender() {
        // Arrange
        testMessage.setSendFrom(null);

        // Act
        ResponseEntity<?> response = messageController.sendMessageRest(testMessage);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("sendFrom cannot be null", response.getBody());
    }

    @Test
    void testGetConnectedUsers_Success() {
        // Arrange
        Map<String, String> connectedUsers = Map.of("1", "session123");
        when(webSocketEventListener.getConnectedUsers()).thenReturn(connectedUsers);
        when(clientService.getClientById(1L)).thenReturn(Optional.of(testClient));

        // Act
        ResponseEntity<?> response = messageController.getConnectedUsers();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertTrue((Boolean) responseBody.get("success"));
        assertNotNull(responseBody.get("connectedUsers"));
    }

    @Test
    void testGetUserConnectionStatus_Success() {
        // Arrange
        when(webSocketEventListener.isUserConnected("1")).thenReturn(true);

        // Act
        ResponseEntity<?> response = messageController.getUserConnectionStatus(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertEquals(1L, responseBody.get("userId"));
        assertTrue((Boolean) responseBody.get("isConnected"));
    }

    @Test
    void testVerifyMessagesInDatabase_Success() {
        // Arrange
        List<Message> sentMessages = Arrays.asList(testMessage);
        List<Message> receivedMessages = Arrays.asList(testMessage);
        List<Message> allMessages = Arrays.asList(testMessage);
        List<Message> adminConversation = Arrays.asList(testMessage);

        when(messageService.getMessagesSentByUser(1L)).thenReturn(sentMessages);
        when(messageService.getMessagesReceivedByUser(1L)).thenReturn(receivedMessages);
        when(messageService.getAllMessagesForUser(1L)).thenReturn(allMessages);
        when(messageService.getConversationWithAdmin(1L)).thenReturn(adminConversation);

        // Act
        ResponseEntity<?> response = messageController.verifyMessagesInDatabase(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());

        @SuppressWarnings("unchecked")
        Map<String, Object> responseBody = (Map<String, Object>) response.getBody();
        assertTrue((Boolean) responseBody.get("success"));
        assertNotNull(responseBody.get("statistics"));
    }
}