package hexlet.code.component;

import hexlet.code.dto.users.UserCreateDTO;
import hexlet.code.mapper.UserMapper;
import hexlet.code.model.Label;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private static final List<TaskStatus> STATUSES = List.of(
            getTaskStatus("Draft", "draft"),
            getTaskStatus("To review", "to_review"),
            getTaskStatus("To be fixed", "to_be_fixed"),
            getTaskStatus("To publish", "to_publish"),
            getTaskStatus("Published", "published"));

    private static final List<Label> LABELS = List.of(
            getLabel("feature"),
            getLabel("bug"));

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final TaskStatusRepository taskStatusRepository;

    @Autowired
    private final LabelRepository labelRepository;

    @Autowired
    private final UserMapper userMapper;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (userRepository.findByEmail("hexlet@example.com").isEmpty()) {
            var userData = new UserCreateDTO();
            userData.setEmail("hexlet@example.com");
            userData.setPassword("qwerty");
            var user = userMapper.map(userData);
            userRepository.save(user);
        }
        for (var status : STATUSES) {
            if (taskStatusRepository.findBySlug(status.getSlug()).isEmpty()) {
                taskStatusRepository.save(status);
            }
        }
        for (var label : LABELS) {
            if (labelRepository.findByName(label.getName()).isEmpty()) {
                labelRepository.save(label);
            }
        }
    }

    public static TaskStatus getTaskStatus(String name, String slug) {
        var taskStatus = new TaskStatus();
        taskStatus.setName(name);
        taskStatus.setSlug(slug);
        return taskStatus;
    }

    public static Label getLabel(String name) {
        var label = new Label();
        label.setName(name);
        return label;
    }
}
