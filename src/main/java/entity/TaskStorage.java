package entity;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TaskStorage {
    private final Map<Integer, Task> taskMap = new HashMap<>();

    public Task addOrUpdateIfExist(Task task) {
        if (task.getId() == 0) {
            task.setId(task.getText().hashCode());
            taskMap.put(task.getId(), task);
        } else {
            taskMap.replace(task.getId(), task);
        }
        return task;
    }

    public Collection<Task> getAllTasks() {
        return taskMap.values();
    }

    public boolean deleteById(Integer key) {
        return taskMap.remove(key) != null;
    }

    public void deleteAll() {
        taskMap.clear();
    }
}
