import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TaskStorage<Task> {
    private final Map<Integer, Task> taskMap = new HashMap<>();

    public Collection<Task> getAllTasks() {
        return taskMap.values();
    }

    //todo think what this methods should return and is id necessary
    public Task addTask(Integer id, Task task) {
        return taskMap.put(id, task);
    }

    public Task updateTask(Integer id, Task task) {
        return taskMap.replace(id, task);
    }
}
