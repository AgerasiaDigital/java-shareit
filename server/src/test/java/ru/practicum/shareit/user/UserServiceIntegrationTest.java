package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceIntegrationTest {

    private final UserService userService;
    private final UserRepository userRepository;

    @Test
    void createUser_shouldSaveUserToDatabase() {
        UserDto userDto = new UserDto(null, "Integration Test", "integration@test.com");

        UserDto created = userService.createUser(userDto);

        assertNotNull(created.getId());
        assertTrue(userRepository.existsById(created.getId()));

        User savedUser = userRepository.findById(created.getId()).orElseThrow();
        assertEquals("Integration Test", savedUser.getName());
        assertEquals("integration@test.com", savedUser.getEmail());
    }

    @Test
    void updateUser_shouldUpdateUserInDatabase() {
        UserDto userDto = new UserDto(null, "Original Name", "original@test.com");
        UserDto created = userService.createUser(userDto);

        UserDto updateDto = new UserDto(null, "Updated Name", null);
        UserDto updated = userService.updateUser(created.getId(), updateDto);

        assertEquals("Updated Name", updated.getName());
        assertEquals("original@test.com", updated.getEmail());

        User savedUser = userRepository.findById(created.getId()).orElseThrow();
        assertEquals("Updated Name", savedUser.getName());
    }

    @Test
    void deleteUser_shouldRemoveUserFromDatabase() {
        UserDto userDto = new UserDto(null, "To Delete", "delete@test.com");
        UserDto created = userService.createUser(userDto);

        userService.deleteUser(created.getId());

        assertFalse(userRepository.existsById(created.getId()));
    }
}