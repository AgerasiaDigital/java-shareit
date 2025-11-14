package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceIntegrationTest {
    private final UserService userService;

    @Test
    void createUser_shouldCreateUser() {
        UserDto userDto = new UserDto(null, "Test User", "test@email.com");

        UserDto result = userService.createUser(userDto);

        assertThat(result.getId(), notNullValue());
        assertThat(result.getName(), equalTo("Test User"));
        assertThat(result.getEmail(), equalTo("test@email.com"));
    }

    @Test
    void updateUser_shouldUpdateUserFields() {
        UserDto userDto = new UserDto(null, "Test User", "test@email.com");
        UserDto created = userService.createUser(userDto);

        UserDto updateDto = new UserDto(null, "Updated Name", null);
        UserDto updated = userService.updateUser(created.getId(), updateDto);

        assertThat(updated.getId(), equalTo(created.getId()));
        assertThat(updated.getName(), equalTo("Updated Name"));
        assertThat(updated.getEmail(), equalTo("test@email.com"));
    }
}