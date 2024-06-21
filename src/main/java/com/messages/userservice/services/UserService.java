package com.messages.userservice.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.messages.userservice.dto.AuthResponse;
import com.messages.userservice.dto.RegisterAuthRequest;
import com.messages.userservice.dto.RegisterRequest;
import com.messages.userservice.dto.RoomResponse;
import com.messages.userservice.dto.TokenResponse;
import com.messages.userservice.dto.UserResponse;
import com.messages.userservice.models.User;
import com.messages.userservice.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RestTemplate rest;

    public UserResponse getUser(Integer id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));

        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .profile_image(user.getProfile_image())
                .temporal(user.getTemporal())
                .build();
    }

    public UserResponse getUserByName(String name) {
        try {
            System.out.println("NAME: " + name);
            User user = userRepository.findByName(name).orElseThrow(() -> new RuntimeException("User not found"));

            System.out.println("USER: " + user);
            return UserResponse.builder()
                    .id(user.getId())
                    .name(user.getName())
                    .profile_image(user.getProfile_image())
                    .temporal(user.getTemporal())
                    .build();
        } catch (Exception e) {
            System.out.println("ERROR: " + e);
            throw new RuntimeException("User not found");
        }
    }

    public UserResponse getUserById(Integer id) {
        try {
            User user = userRepository.findById(id).orElse(null);
            return UserResponse.builder()
                    .id(user.getId())
                    .name(user.getName())
                    .profile_image(user.getProfile_image())
                    .temporal(user.getTemporal())
                    .build();
        } catch (Exception e) {
            System.out.println("ERROR: " + e);
            throw new RuntimeException("User not found");
        }
    }

    public AuthResponse registerTemporalUser() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            User temporalUser = new User();
            temporalUser.setTemporal(true);
            temporalUser.setRooms("[]");

            User user_response = userRepository.save(temporalUser);

            user_response.setName("user_" + user_response.getId());

            userRepository.save(user_response);

            AuthResponse authResponse = AuthResponse.builder()
                    .id(user_response.getId())
                    .name(user_response.getName())
                    .profile_image(user_response.getProfile_image())
                    .temporal(true)
                    .build();

            System.out.println("authResponse: " + authResponse);

            HttpEntity<AuthResponse> requestBody = new HttpEntity<>(authResponse, headers);

            ResponseEntity<TokenResponse> response = rest.postForEntity("http://localhost:3004/token/create",
                    requestBody,
                    TokenResponse.class);

            TokenResponse tokenResponse = response.getBody();

            authResponse.setToken(tokenResponse.getToken());

            return authResponse;

        } catch (Exception e) {
            System.err.println("ERROR: " + e);
            throw new RuntimeException("Error registering user");
        }

    }

    public AuthResponse registerUser(RegisterRequest userInformation) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            User user = new User();

            // user.setId(userInformation.getId());

            User user_response = userRepository.save(user);

            // user_response.setId(user_response.getId());
            user_response.setName(userInformation.getName());
            user_response.setProfile_image(userInformation.getProfile_image());
            user_response.setTemporal(false);

            userRepository.save(user_response);

            AuthResponse authResponse = AuthResponse.builder()
                    .id(user_response.getId())
                    .name(user_response.getName())
                    .profile_image(user_response.getProfile_image())
                    .temporal(false)
                    .build();

            RegisterAuthRequest registerRequest = RegisterAuthRequest.builder()
                    .id(user_response.getId())
                    .password(userInformation.getPassword())
                    .build();

            HttpEntity<RegisterAuthRequest> requestBody = new HttpEntity<>(registerRequest, headers);

            ResponseEntity<TokenResponse> response = rest.postForEntity("http://localhost:3004/users/create",
                    requestBody,
                    TokenResponse.class);

            TokenResponse tokenResponse = response.getBody();

            authResponse.setToken(tokenResponse.getToken());

            return authResponse;

        } catch (Exception e) {
            System.err.println("ERROR: " + e);
            throw new RuntimeException("Error registering user");
        }
    }

    public void addRoom(Integer idUser, Integer idRoom) {
        User user = userRepository.findById(idUser).orElseThrow(() -> new RuntimeException("User not found"));

        String roomsStr = user.getRooms();

        // Convertir la cadena a una lista de números
        List<Integer> rooms = new ArrayList<>();
        if (roomsStr != null && !roomsStr.isEmpty()) {
            rooms = Arrays.stream(roomsStr.split(","))
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
        }

        // Agregar el nuevo idRoom a la lista
        rooms.add(idRoom);

        // Convertir la lista de números de vuelta a una cadena de texto
        String updatedRoomsStr = rooms.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        // Actualizar el campo rooms del usuario
        user.setRooms(updatedRoomsStr);

        rest.put(
                "http://localhost:8080/add_user/" + idRoom + "/" + idUser, RoomResponse.class);

        userRepository.save(user);
    }

}
