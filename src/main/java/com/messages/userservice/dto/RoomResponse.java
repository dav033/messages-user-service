package com.messages.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomResponse {

    Integer id;
    String name;
    String type_room;
    String Messages;
    String Users;

}
