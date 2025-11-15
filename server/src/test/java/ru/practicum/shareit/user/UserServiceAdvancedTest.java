package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceAdvancedTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void updateUser_shouldUpdateOnlyName() {
        User existingUser = new User(1L, "Old Name", "email@email.com");
        UserDto updateDto = new UserDto(null, "New Name", null);
        User updatedUser = new User(1L, "New Name", "email@email.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        UserDto result = userService.updateUser(1L, updateDto);

        assertThat(result.getName(), equalTo("New Name"));
        assertThat(result.getEmail(), equalTo("email@email.com"));
    }

    @Test
    void updateUser_shouldUpdateOnlyEmail() {
        User existingUser = new User(1L, "Name", "old@email.com");
        UserDto updateDto = new UserDto(null, null, "new@email.com");
        User updatedUser = new User(1L, "Name", "new@email.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmailAndIdNot(anyString(), anyLong())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        UserDto result = userService.updateUser(1L, updateDto);

        assertThat(result.getName(), equalTo("Name"));
        assertThat(result.getEmail(), equalTo("new@email.com"));
    }

    @Test
    void updateUser_shouldNotUpdateWhenEmailIsSame() {
        User existingUser = new User(1L, "Name", "email@email.com");
        UserDto updateDto = new UserDto(null, "New Name", "email@email.com");
        User updatedUser = new User(1L, "New Name", "email@email.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        UserDto result = userService.updateUser(1L, updateDto);

        assertThat(result.getName(), equalTo("New Name"));
        assertThat(result.getEmail(), equalTo("email@email.com"));
    }

    @Test
    void updateUser_shouldThrowConflictException_whenEmailExists() {
        User existingUser = new User(1L, "Name", "old@email.com");
        UserDto updateDto = new UserDto(null, null, "new@email.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmailAndIdNot(anyString(), anyLong())).thenReturn(true);

        assertThrows(ConflictException.class, () -> userService.updateUser(1L, updateDto));
    }

    @Test
    void updateUser_shouldNotUpdateWhenNameIsBlank() {
        User existingUser = new User(1L, "Name", "email@email.com");
        UserDto updateDto = new UserDto(null, "  ", null);
        User updatedUser = new User(1L, "Name", "email@email.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        UserDto result = userService.updateUser(1L, updateDto);

        assertThat(result.getName(), equalTo("Name"));
    }

    @Test
    void getUser_shouldThrowNotFoundException_whenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUser(1L));
    }

    @Test
    void deleteUser_shouldThrowNotFoundException_whenUserNotFound() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> userService.deleteUser(1L));
    }
}