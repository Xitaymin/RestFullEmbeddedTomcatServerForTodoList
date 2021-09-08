package dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import entity.Task;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TaskDAO {
    private final ObjectMapper mapper = new ObjectMapper();
    private final Map<Integer, Task> taskMap = new HashMap<>();

    public TaskDAO() {
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                         false);
    }

    //    public Task addOrUpdateIfExist(Task task) {
    //        if (task.getId() == 0) {
    //            task.setId(task.getText().hashCode());
    //            System.out.println(task.getId());
    //            taskMap.put(task.getId(), task);
    //        } else {
    //            taskMap.replace(task.getId(), task);
    //        }
    //        return task;
    //    }

    public Collection<Task> getAllTasks() {
        return taskMap.values();
    }

    public boolean deleteById(Integer key) {
        return taskMap.remove(key) != null;
    }

    public void deleteAll() {
        taskMap.clear();
    }

    public Task createTask(String json) throws JsonProcessingException {
        Task task = mapper.readValue(json, Task.class);
        System.out.println(task.getId());
        return task;
    }

    //todo check for exceptions here
    public Task createTaskOrUpdateIfExist(String json) throws JsonProcessingException {
        Task task = mapper.readValue(json, Task.class);
        System.out.println(task);
        if (task.getId() == 0) {
            task.setId(task.getText().hashCode());
            System.out.println(task.getId());
            taskMap.put(task.getId(), task);
        } else {
            taskMap.replace(task.getId(), task);
        }
        return task;
    }
}
