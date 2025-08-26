package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.dal.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Import({UserDbStorage.class,
        UserRowMapper.class,
        FriendDbStorage.class})
public class UserDbStorageTest {
    private final UserDbStorage userDbStorage;
    private final FriendDbStorage friendDbStorage;
    User user1;
    User user2;
    User user3;

    @BeforeEach
    void beforeALL() {
        user1 = User.builder()
                .email("ivan@yandex.ru")
                .login("Ivan123")
                .name("Ivan")
                .birthday(LocalDate.of(1996, 12, 3))
                .build();
        user2 = User.builder()
                .email("email@yandex.ru")
                .login("Dan99")
                .name("Dan")
                .birthday(LocalDate.of(2000, 12, 3))
                .build();
        user3 = User.builder()
                .email("user3@yandex.ru")
                .login("User3")
                .name("Useron")
                .birthday(LocalDate.of(1980, 12, 3))
                .build();
    }

    @Test
    void createAndFindById() {
        userDbStorage.create(user1);
        assertThat(userDbStorage.findById(1L)).hasFieldOrPropertyWithValue("id", 1L);
    }

    @Test
    void findAll() {
        userDbStorage.create(user1);
        userDbStorage.create(user2);
        assertThat(userDbStorage.findAll()).hasSize(2);
    }

    @Test
    void update() {
        userDbStorage.create(user1);
        user1.setName("Update");
        userDbStorage.update(user1);
        assertThat(userDbStorage.findById(1L)).hasFieldOrPropertyWithValue("name", "Update");
    }

    @Test
    void delete() {
        userDbStorage.create(user1);
        userDbStorage.delete(1L);
        assertThat(userDbStorage.findAll()).isEmpty();
    }

    @Test
    void getCommonFriends() {
        User userId1 = userDbStorage.create(user1);
        User userId2 = userDbStorage.create(user2);
        User userId3 = userDbStorage.create(user3);
        friendDbStorage.addFriend(userId1.getId(), userId2.getId());
        friendDbStorage.addFriend(userId1.getId(), userId3.getId());
        friendDbStorage.addFriend(userId2.getId(), userId1.getId());
        friendDbStorage.addFriend(userId2.getId(), userId3.getId());
        Collection<User> commonFriends = userDbStorage.getCommonFriends(userId1.getId(), userId2.getId());
        assertThat(commonFriends).hasSize(1);
        User friend = commonFriends.iterator().next();
        assertThat(friend).hasFieldOrPropertyWithValue("name", "Useron");
    }

    @Test
    void getFriends() {
        User userId1 = userDbStorage.create(user1);
        User userId2 = userDbStorage.create(user2);
        User userId3 = userDbStorage.create(user3);
        friendDbStorage.addFriend(userId1.getId(), userId2.getId());
        friendDbStorage.addFriend(userId1.getId(), userId3.getId());
        assertThat(userDbStorage.getFriends(userId1.getId())).hasSize(2);
    }
}
