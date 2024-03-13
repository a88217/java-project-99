package hexlet.code.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.tasks.TaskCreateDTO;
import hexlet.code.dto.tasks.TaskUpdateDTO;
import hexlet.code.model.User;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.utils.ModelGenerator;
import org.instancio.Instancio;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import java.time.format.DateTimeFormatter;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired UserRepository userRepository;

    @Autowired
    private ModelGenerator modelGenerator;

    private User testUser;

    private TaskStatus testTaskStatus;

    private Task testTask;

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor token;

    @BeforeEach
    public void setUp() {
        testUser = Instancio.of(modelGenerator.getUserModel()).create();
        userRepository.save(testUser);
        testTaskStatus = Instancio.of(modelGenerator.getTaskStatusModel()).create();
        taskStatusRepository.save(testTaskStatus);
        testTask = Instancio.of(modelGenerator.getTaskModel()).create();
        testTask.setAssignee(testUser);
        testTask.setTaskStatus(testTaskStatus);
        taskRepository.save(testTask);
        token = jwt().jwt(builder -> builder.subject(testUser.getEmail()));
    }

    @AfterEach
    public void clean() {
        taskRepository.deleteAll();
        taskStatusRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void testIndex() throws Exception {
        var request = get("/api/tasks")
                .with(token);
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).isArray().hasSize(1);
    }

    @Test
    public void testShow() throws Exception {
        var request = get("/api/tasks/" + testTask.getId())
                .with(token);
        var result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();
        var body = result.getResponse().getContentAsString();
        assertThatJson(body).and(
                a -> a.node("id").isEqualTo(testTask.getId()),
                a -> a.node("title").isEqualTo(testTask.getName()),
                a -> a.node("index").isEqualTo(testTask.getIndex()),
                a -> a.node("content").isEqualTo(testTask.getDescription()),
                a -> a.node("status").isEqualTo(testTaskStatus.getSlug()),
                a -> a.node("assignee_id").isEqualTo(testUser.getId()),
                a -> a.node("createdAt").isEqualTo(testTask.getCreatedAt()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                );
    }

    @Test
    public void testCreate() throws Exception {
        var newUser = Instancio.of(modelGenerator.getUserModel()).create();
        userRepository.save(newUser);
        var newTaskStatus = Instancio.of(modelGenerator.getTaskStatusModel()).create();
        taskStatusRepository.save(newTaskStatus);
        var newTask = Instancio.of(modelGenerator.getTaskModel()).create();
        var data = new TaskCreateDTO();
        data.setTitle(newTask.getName());
        data.setIndex(newTask.getIndex());
        data.setContent(newTask.getDescription());
        data.setStatus(newTaskStatus.getSlug());
        data.setAssigneeId(newUser.getId());
        var request = post("/api/tasks")
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));
        mockMvc.perform(request)
                .andExpect(status().isCreated());
        var task = taskRepository.findByName(newTask.getName()).get();
        assertNotNull(task);
        assertThat(task.getIndex()).isEqualTo(newTask.getIndex());
        assertThat(task.getDescription()).isEqualTo(newTask.getDescription());
        assertThat(task.getTaskStatus()).isEqualTo(newTaskStatus);
        assertThat(task.getAssignee()).isEqualTo(newUser);
    }

    @Test
    public void testUpdate() throws Exception {
        var data = new TaskUpdateDTO();
        data.setTitle(JsonNullable.of("New title"));
        data.setIndex(JsonNullable.of(12345));
        data.setContent(JsonNullable.of("New content"));
        var request = put("/api/tasks/" + testTask.getId())
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(data));
        mockMvc.perform(request)
                .andExpect(status().isOk());
        var task = taskRepository.findById(testTask.getId()).get();
        assertThat(task.getName()).isEqualTo("New title");
        assertThat(task.getIndex()).isEqualTo(12345);
        assertThat(task.getDescription()).isEqualTo("New content");
    }

    @Test
    public void testDelete() throws Exception {
        var request = delete("/api/tasks/" + testTask.getId())
                .with(token);
        mockMvc.perform(request)
                .andExpect(status().isNoContent());
        assertThat(taskRepository.existsById(testTask.getId())).isFalse();
    }

}