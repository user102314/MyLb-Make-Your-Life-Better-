package MyLb.BackEnd.utils;

import MyLb.BackEnd.Model.Entities.Client;

import java.time.LocalDate;

public class TestUtils {

    public static Client createTestClient(Long id, String firstName, String lastName, String email) {
        Client client = new Client();
        client.setClientId(id);
        client.setFirstName(firstName);
        client.setLastName(lastName);
        client.setEmail(email);
        client.setPassword("password123");
        client.setBirthDate(LocalDate.of(1990, 1, 1));
        client.setRole("USER");
        client.setIsVerified(true);
        return client;
    }

    public static Client createTestClientWithImage(Long id, String email, byte[] profileImage) {
        Client client = createTestClient(id, "Test", "User", email);
        client.setProfileImage(profileImage);
        return client;
    }
}