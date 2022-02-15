package com.regservice.application.mapper;

import com.regservice.application.dto.UserDTO;
import com.regservice.domain.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User fromDTO(UserDTO userDTO) {
        User user = new User();
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setPassword(userDTO.getPassword());
        return user;
    }
    public UserDTO toDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setName(user.getName());
        userDTO.setEmail(user.getEmail());
        userDTO.setPassword(user.getPassword());
        return userDTO;
    }
}
