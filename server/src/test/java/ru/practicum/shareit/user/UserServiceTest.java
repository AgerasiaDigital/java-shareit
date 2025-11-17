package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void createUser_shouldCreateUser() {
        UserDto userDto = new UserDto(null, "Test User", "test@email.com");
        User user = new User(1L, "Test User", "test@email.com");

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.createUser(userDto);

        assertThat(result.getId(), equalTo(1L));
        assertThat(result.getName(), equalTo("Test User"));
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void createUser_shouldThrowConflictException_whenEmailExists() {
        UserDto userDto = new UserDto(null, "Test User", "test@email.com");

        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(ConflictException.class, () -> userService.createUser(userDto));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_shouldUpdateUser() {
        User existingUser = new User(1L, "Old Name", "old@email.com");
        UserDto updateDto = new UserDto(null, "New Name", "new@email.com");
        User updatedUser = new User(1L, "New Name", "new@email.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmailAndIdNot(anyString(), anyLong())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        UserDto result = userService.updateUser(1L, updateDto);

        assertThat(result.getName(), equalTo("New Name"));
        assertThat(result.getEmail(), equalTo("new@email.com"));
    }

    @Test
    void updateUser_shouldThrowNotFoundException_whenUserNotFound() {
        UserDto updateDto = new UserDto(null, "New Name", null);

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.updateUser(1L, updateDto));
    }

    @Test
    void getUser_shouldReturnUser() {
        User user = new User(1L, "Test User", "test@email.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDto result = userService.getUser(1L);

        assertThat(result.getId(), equalTo(1L));
        assertThat(result.getName(), equalTo("Test User"));
    }

    @Test
    void deleteUser_shouldDeleteUser() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userService.deleteUser(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void getAllUsers_shouldReturnAllUsers() {
        User user1 = new User(1L, "User 1", "user1@email.com");
        User user2 = new User(2L, "User 2", "user2@email.com");

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        List<UserDto> result = userService.getAllUsers();

        assertThat(result, hasSize(2));
        assertThat(result.get(0).getId(), equalTo(1L));
        assertThat(result.get(1).getId(), equalTo(2L));
    }
}