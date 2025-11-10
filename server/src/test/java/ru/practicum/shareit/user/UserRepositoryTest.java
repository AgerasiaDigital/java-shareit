package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private UserRepository userRepository;

    @Test
    void existsByEmail_shouldReturnTrue_whenEmailExists() {
        User user = new User(null, "Test", "test@example.com");
        em.persist(user);
        em.flush();

        boolean exists = userRepository.existsByEmail("test@example.com");

        assertTrue(exists);
    }

    @Test
    void existsByEmail_shouldReturnFalse_whenEmailNotExists() {
        boolean exists = userRepository.existsByEmail("notexist@example.com");

        assertFalse(exists);
    }

    @Test
    void existsByEmailAndIdNot_shouldReturnTrue_whenEmailUsedByOtherUser() {
        User user1 = new User(null, "User1", "user1@example.com");
        User user2 = new User(null, "User2", "user2@example.com");
        em.persist(user1);
        em.persist(user2);
        em.flush();

        boolean exists = userRepository.existsByEmailAndIdNot("user1@example.com", user2.getId());

        assertTrue(exists);
    }

    @Test
    void existsByEmailAndIdNot_shouldReturnFalse_whenEmailUsedBySameUser() {
        User user = new User(null, "User", "user@example.com");
        em.persist(user);
        em.flush();

        boolean exists = userRepository.existsByEmailAndIdNot("user@example.com", user.getId());

        assertFalse(exists);
    }
}