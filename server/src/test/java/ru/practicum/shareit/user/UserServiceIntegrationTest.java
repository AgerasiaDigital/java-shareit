package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.user.dto.UserDto;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DataJpaTest
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import(UserServiceImpl.class)
class UserServiceIntegrationTest {
    private final UserService userService;
    private final TestEntityManager entityManager;

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
        User user = new User();
        user.setName("Test User");
        user.setEmail("test@email.com");
        entityManager.persist(user);
        entityManager.flush();

        UserDto updateDto = new UserDto(null, "Updated Name", null);
        UserDto updated = userService.updateUser(user.getId(), updateDto);

        assertThat(updated.getId(), equalTo(user.getId()));
        assertThat(updated.getName(), equalTo("Updated Name"));
        assertThat(updated.getEmail(), equalTo("test@email.com"));
    }
}