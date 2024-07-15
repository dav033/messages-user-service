package com.messages.userservice.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.messages.userservice.dto.AuthResponse;
import com.messages.userservice.dto.CreateAuthUserRequest;
import com.messages.userservice.dto.RegisterRequest;
import com.messages.userservice.dto.RoomResponse;
import com.messages.userservice.dto.TokenResponse;
import com.messages.userservice.dto.TransformRequest;
import com.messages.userservice.dto.UpdateMessagesUsernameRequest;
import com.messages.userservice.dto.UserResponse;
import com.messages.userservice.models.User;
import com.messages.userservice.repositories.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RestTemplate rest;

    public UserResponse getUser(Integer id) {
        try {
            User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
            return buildUserResponse(user);
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving user", e);
        }
    }

    public UserResponse getUserByName(String name) {
        try {
            User user = userRepository.findByName(name).orElseThrow(() -> new RuntimeException("User not found"));
            return buildUserResponse(user);
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving user by name", e);
        }
    }

    public AuthResponse registerTemporalUser() {
        try {
            User temporalUser = new User();
            temporalUser.setTemporal(true);
            temporalUser.setRooms("[]");

            User savedUser = userRepository.save(temporalUser);
            savedUser.setName("user_" + savedUser.getId());
            userRepository.save(savedUser);

            return getToken(savedUser.getId());
        } catch (Exception e) {
            throw new RuntimeException("Error registering temporal user", e);
        }
    }

    public Void createAuthUser(Integer idUser, String password) {
        try {
            CreateAuthUserRequest request = CreateAuthUserRequest.builder()
                    .id(idUser)
                    .password(password)
                    .build();

            HttpHeaders headers = createJsonHeaders();
            HttpEntity<CreateAuthUserRequest> requestBody = new HttpEntity<>(request, headers);
            rest.postForEntity("http://localhost:3004/users/create", requestBody, Void.class);
            return null;
        } catch (RestClientException e) {
            throw new RuntimeException("Error creating auth user", e);
        }
    }

    public AuthResponse registerUser(RegisterRequest userInformation) {
        try {
            User user = new User();
            user.setName(userInformation.getName());
            user.setProfile_image("");
            user.setTemporal(false);
            user.setRooms("[]");
            User savedUser = userRepository.save(user);

            createAuthUser(savedUser.getId(), userInformation.getPassword());

            return getToken(savedUser.getId());
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            throw new RuntimeException("Error registering user", e);
        }
    }

    public void addRoom(Integer idUser, Integer idRoom) {
        try {
            User user = userRepository.findById(idUser).orElseThrow(() -> new RuntimeException("User not found"));
            List<Integer> rooms = parseRooms(user.getRooms());
            rooms.add(idRoom);

            user.setRooms(rooms.stream().map(String::valueOf).collect(Collectors.joining(",")));
            rest.put("http://localhost:8080/add_user/" + idRoom + "/" + idUser, RoomResponse.class);
            userRepository.save(user);
        } catch (Exception e) {
            throw new RuntimeException("Error adding room", e);
        }
    }

    public AuthResponse getToken(Integer idUser) {
        try {
            User user = userRepository.findById(idUser).orElseThrow(() -> new RuntimeException("User not found"));
            AuthResponse authResponse = buildAuthResponse(user);

            ResponseEntity<TokenResponse> response = rest.postForEntity("http://localhost:3004/token/create",
                    authResponse,
                    TokenResponse.class);
            authResponse.setToken(response.getBody().getToken());

            return authResponse;
        } catch (RestClientException e) {
            throw new RuntimeException("Error getting token", e);
        }
    }

    public Void updateUsername(Integer idUser, String username) {
        try {
            UpdateMessagesUsernameRequest request = UpdateMessagesUsernameRequest.builder()
                    .username(username)
                    .build();

            User user = userRepository.findById(idUser).orElseThrow(() -> new RuntimeException("User not found"));
            user.setName(username);
            userRepository.save(user);

            HttpEntity<UpdateMessagesUsernameRequest> requestEntity = new HttpEntity<>(request, createJsonHeaders());
            rest.exchange("http://localhost:8082/messages/update_username/" + idUser, HttpMethod.PUT, requestEntity,
                    Void.class);
            return null;
        } catch (RestClientException e) {
            throw new RuntimeException("Error updating username", e);
        }
    }

    public AuthResponse transformUser(Integer idUser, String username, String password) {
        try {
            createAuthUser(idUser, password);
            updateUsername(idUser, username);
            return getToken(idUser);
        } catch (Exception e) {
            throw new RuntimeException("Error transforming user", e);
        }
    }

    private UserResponse buildUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .profile_image(user.getProfile_image())
                .temporal(user.getTemporal())
                .build();
    }

    private AuthResponse buildAuthResponse(User user) {
        return AuthResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .profile_image(user.getProfile_image())
                .temporal(user.getTemporal())
                .build();
    }

    private HttpHeaders createJsonHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private List<Integer> parseRooms(String roomsStr) {
        return (roomsStr != null && !roomsStr.isEmpty())
                ? Arrays.stream(roomsStr.split(","))
                        .map(Integer::parseInt)
                        .collect(Collectors.toList())
                : new ArrayList<>();
    }
}
