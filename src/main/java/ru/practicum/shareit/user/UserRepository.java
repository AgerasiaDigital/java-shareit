package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private final Map<String, Long> emailToUserId = new HashMap<>();
    private Long currentId = 1L;

    public User save(User user) {
        user.setId(currentId++);
        users.put(user.getId(), user);
        emailToUserId.put(user.getEmail(), user.getId());
        return user;
    }

    public User update(User user) {
        User oldUser = users.get(user.getId());
        if (oldUser != null && !oldUser.getEmail().equals(user.getEmail())) {
            emailToUserId.remove(oldUser.getEmail());
            emailToUserId.put(user.getEmail(), user.getId());
        }
        users.put(user.getId(), user);
        return user;
    }

    public Optional<User> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    public void delete(Long id) {
        User user = users.remove(id);
        if (user != null) {
            emailToUserId.remove(user.getEmail());
        }
    }

    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    public boolean existsByEmail(String email) {
        return emailToUserId.containsKey(email);
    }

    public boolean existsByEmailAndIdNot(String email, Long id) {
        Long userId = emailToUserId.get(email);
        return userId != null && !userId.equals(id);
    }
}