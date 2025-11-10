package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    @Test
    void toUserDto_shouldMapUserToDto() {
        User user = new User(1L, "Test User", "test@example.com");

        UserDto dto = UserMapper.toUserDto(user);

        assertEquals(1L, dto.getId());
        assertEquals("Test User", dto.getName());
        assertEquals("test@example.com", dto.getEmail());
    }

    @Test
    void toUser_shouldMapDtoToUser() {
        UserDto dto = new UserDto(1L, "Test User", "test@example.com");

        User user = UserMapper.toUser(dto);

        assertEquals(1L, user.getId());
        assertEquals("Test User", user.getName());
        assertEquals("test@example.com", user.getEmail());
    }
}