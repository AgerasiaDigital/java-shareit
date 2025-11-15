package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

class UserMapperTest {

    @Test
    void toUserDto_shouldMapCorrectly() {
        User user = new User(1L, "Test User", "test@email.com");

        UserDto result = UserMapper.toUserDto(user);

        assertThat(result.getId(), equalTo(1L));
        assertThat(result.getName(), equalTo("Test User"));
        assertThat(result.getEmail(), equalTo("test@email.com"));
    }

    @Test
    void toUser_shouldMapCorrectly() {
        UserDto dto = new UserDto(1L, "Test User", "test@email.com");

        User result = UserMapper.toUser(dto);

        assertThat(result.getId(), equalTo(1L));
        assertThat(result.getName(), equalTo("Test User"));
        assertThat(result.getEmail(), equalTo("test@email.com"));
    }
}