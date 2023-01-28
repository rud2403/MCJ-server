package com.minecraft.job.common.user.service;

import com.minecraft.job.common.emailauth.domain.EmailAuthRepository;
import com.minecraft.job.common.fixture.EmailAuthFixture;
import com.minecraft.job.common.fixture.UserFixture;
import com.minecraft.job.common.support.MinecraftJobException;
import com.minecraft.job.common.user.domain.User;
import com.minecraft.job.common.user.domain.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import static com.minecraft.job.common.user.domain.UserSearchType.*;
import static com.minecraft.job.common.user.domain.UserStatus.ACTIVATED;
import static com.minecraft.job.common.user.domain.UserStatus.INACTIVATED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@SpringBootTest
@Transactional
class DomainUserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private EmailAuthRepository emailAuthRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = userRepository.save(UserFixture.getAntherUser("anotherEmail"));
        emailAuthRepository.save(EmailAuthFixture.getValidatedEmailAuth(user.getEmail()));
    }

    @Test
    void 유저_생성_성공() {
        emailAuthRepository.save(EmailAuthFixture.getValidatedEmailAuth("email"));

        User user = userService.create("email", "password", "nickname", "interest", 10L);

        User findUser = userRepository.findById(user.getId()).orElseThrow();

        assertThat(findUser.getId()).isEqualTo(user.getId());
    }

    @Test
    void 유저_생성_실패__이미_존재하는_이메일() {
        emailAuthRepository.save(EmailAuthFixture.getValidatedEmailAuth("email"));

        userService.create("email", "password", "nickname", "interest", 10L);

        assertThatExceptionOfType(MinecraftJobException.class).isThrownBy(
                () -> userService.create("email", "password", "nickname", "interest", 10L)
        );
    }

    @Test
    void 유저_정보_변경() {
        userService.changeInformation(user.getId(), "changeNickname", "changeInterest", 30L);

        assertThat(user.getNickname()).isEqualTo("changeNickname");
        assertThat(user.getInterest()).isEqualTo("changeInterest");
        assertThat(user.getAge()).isEqualTo(30L);
    }

    @Test
    void 유저_비밀번호_변경() {
        userService.changePassword(user.getId(), user.getPassword(), "newPassword");

        assertThat(user.getPassword()).isEqualTo("newPassword");
    }

    @Test
    void 유저_활성화() {
        user.inactivate();

        userService.activate(user.getId());

        assertThat(user.getStatus()).isEqualTo(ACTIVATED);
    }

    @Test
    void 유저_비활성화() {
        userService.inactivate(user.getId());

        assertThat(user.getStatus()).isEqualTo(INACTIVATED);
    }

    @Test
    void 유저_리스트_조회_성공__이메일이_일치하는_경우() {
        String email = "email";

        유저_목록_생성(10, email, "password", "nickname", "interest");

        Page<User> findUserList = userService.getUsers(EMAIL, email, 0);

        for (User user : findUserList) {
            assertThat(user.getEmail()).isEqualTo(email);
        }
    }

    @Test
    void 유저_리스트_조회_성공__닉네임이_일치하는_경우() {
        String nickname = "nickname";

        유저_목록_생성(10, "email", "password", nickname, "interest");

        Page<User> findUserList = userService.getUsers(NICKNAME, nickname, 0);

        for (User user : findUserList) {
            assertThat(user.getNickname()).isEqualTo(nickname);
        }
    }

    @Test
    void 유저_리스트_조회_성공__관심사가_포함되는_경우() {
        String interest = "Programing";

        유저_목록_생성(10, "email", "password", "nickname", interest);

        Page<User> findUserList = userService.getUsers(INTEREST, interest, 0);

        for (User user : findUserList) {
            assertThat(user.getInterest()).isEqualTo(interest);
        }
    }

    @Test
    void 유저_리스트_조회_성공__페이징_처리() {
        유저_목록_생성(10, "email", "password", "nickname", "interest");

        Page<User> findUserList = userService.getUsers(INTEREST, "interest", 0);

        assertThat(findUserList.getTotalPages()).isEqualTo(2);
    }

    private void 유저_목록_생성(int count, String email, String password, String nickname, String interest) {
        for (int i = 1; i <= count; i++) {
            User user = User.create(email + i, password + i, nickname, interest, 10L);
            userRepository.save(user);
        }
    }
}
