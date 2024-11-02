package com.example.demossoswagger.service;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final Keycloak keycloak;
    private final String realm;

    public UserService(
            @Value("${keycloak.auth-server-url}") String keycloakUrl,
            @Value("${keycloak.realm}") String realm,
            @Value("${keycloak.resource}") String clientId,
            @Value("${keycloak.credentials.secret}") String clientSecret,
            @Value("${keycloak-admin.username}") String adminUsername,
            @Value("${keycloak-admin.password}") String adminPassword
    ) {
        this.keycloak = KeycloakBuilder.builder()
                .serverUrl(keycloakUrl)
                .realm("dev-api")
                .grantType(OAuth2Constants.PASSWORD)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .username(adminUsername)
                .password(adminPassword)
                .build();
        this.realm = realm;
    }

    private RealmResource getRealm() {
        return keycloak.realm(realm);
    }

    // Existing Methods (getAllUsers, getUserById, createUser, deleteUser, assignRoleToUser, removeRoleFromUser)

    /**
     * Self-register a new user.
     *
     * @param username Username of the new user.
     * @param email    Email of the new user.
     * @param password Password of the new user.
     * @return Response from Keycloak indicating success or failure.
     */
    public Response selfRegisterUser(String username, String email, String password) {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(username);
        user.setEmail(email);
        user.setEnabled(true);

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);
        credential.setTemporary(false);

        user.setCredentials(Collections.singletonList(credential));

        return getRealm().users().create(user);
    }

    /**
     * Reset a user's password.
     *
     * @param userId      ID of the user in Keycloak.
     * @param newPassword New password for the user.
     * @return Response indicating success or failure.
     */
    public Response resetPassword(String userId, String newPassword) {
        CredentialRepresentation newCredential = new CredentialRepresentation();
        newCredential.setType(CredentialRepresentation.PASSWORD);
        newCredential.setValue(newPassword);
        newCredential.setTemporary(false);

        UserResource userResource = getRealm().users().get(userId);
        userResource.resetPassword(newCredential);
        return Response.ok().build();
    }

    /**
     * Assign a composite role to a user.
     *
     * @param userId   ID of the user in Keycloak.
     * @param roleName Name of the composite role to assign.
     */
    public void assignCompositeRoleToUser(String userId, String roleName) {
        UserResource userResource = getRealm().users().get(userId);
        RoleRepresentation role = getRealm().roles().get(roleName).toRepresentation();
        userResource.roles().realmLevel().add(Collections.singletonList(role));
    }

    /**
     * Retrieve the roles assigned to a specific user.
     *
     * @param userId ID of the user in Keycloak.
     * @return List of role names assigned to the user.
     */
    public List<String> getUserRoles(String userId) {
        UserResource userResource = getRealm().users().get(userId);
        return userResource.roles().realmLevel().listEffective().stream()
                .map(RoleRepresentation::getName)
                .collect(Collectors.toList());
    }

    /**
     * Update a user's profile information.
     *
     * @param userId   ID of the user in Keycloak.
     * @param newEmail New email for the user.
     * @param newFirstName New first name for the user.
     * @param newLastName New last name for the user.
     * @return Response indicating success or failure.
     */
    public Response updateUserProfile(String userId, String newEmail, String newFirstName, String newLastName) {
        UserResource userResource = getRealm().users().get(userId);
        UserRepresentation user = userResource.toRepresentation();
        user.setEmail(newEmail);
        user.setFirstName(newFirstName);
        user.setLastName(newLastName);
        userResource.update(user);
        return Response.ok().build();
    }

    /**
     * Log user activity (basic logging example).
     *
     * @param userId   ID of the user in Keycloak.
     * @param activity Description of the activity.
     */
    public void logUserActivity(String userId, String activity) {
        // Basic logging; in a real application, you would log this to a file or external system.
        System.out.println("User ID: " + userId + " performed activity: " + activity);
    }
}
