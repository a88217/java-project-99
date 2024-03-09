package hexlet.code.component;

import hexlet.code.dto.UserCreateDTO;
import hexlet.code.mapper.UserMapper;
import hexlet.code.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class DataInitializer implements ApplicationRunner {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final UserMapper userMapper;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        if (userRepository.findByEmail("hexlet@example.com").isEmpty()) {
            var userData = new UserCreateDTO();
            userData.setEmail("hexlet@example.com");
            userData.setPassword("qwerty");
            System.out.println("password before mapping " + userData.getPassword());
            var user = userMapper.map(userData);
            System.out.println("password after mapping " + user.getPasswordDigest());
            System.out.println("password after mapping " + user.getPassword());
            userRepository.save(user);
        }
    }
}
