package servlet;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import entity.Task;
import entity.TaskStorage;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@WebServlet(name = "MainServlet", urlPatterns = {"/todo"})
public class MainServlet extends HttpServlet {
    //    private Map<Integer, Task> tasks;
    private final TaskStorage taskStorage = new TaskStorage();
    private final ObjectMapper mapper =
            new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    //GET /todo - возвращает список задач
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        System.out.println("INSIDE GET");

        //        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        //        Collection<Task> tasksList = tasks.values();
        Collection<Task> tasksList = taskStorage.getAllTasks();
        String json = mapper.writeValueAsString(tasksList);
        System.out.println(json);
        sendResponse(resp.getWriter(), json);

        //        PrintWriter out = resp.getWriter();
        //        out.print(json);
        //        out.flush();
        //        out.close();

        System.out.println("OUT OF GET");
    }

    @Override
    //POST /todo в теле запроса JSON с задачей, схема ниже. если id объявлен
    // - заменить ассоциированную с ним.
    //Если id в теле запроса отсутствует или null, сгенерировать id и
    // сохранить задачу как новую. Результатом вернуть JSON с сохраненной
    // задачей.
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        System.out.println("INSIDE POST");
        String json = getRequestBody(req.getReader());
        System.out.println(json);
        Task task = mapper.readValue(json, Task.class);
        task = taskStorage.addOrUpdateIfExist(task);
        sendResponse(resp.getWriter(), mapper.writeValueAsString(task));
        System.out.println("OUT OF POST");
    }

    private void sendResponse(PrintWriter out, String body) {
        out.print(body);
        out.flush();
        out.close();
    }

    private String getRequestBody(BufferedReader reader) {
        return reader.lines().collect(Collectors.joining());
    }

    @Override
    public void init() {
        //        tasks = new HashMap<>();
    }

    //    //DELETE /todo - очистить список задач, ответ пустой, код возврата 200
    //    //
    //    //DELETE /todo?id={id} - где вместо {id} указан id задачи, по этому
    //    // запросу нужно удалить эту задачу. ответ пустой, код возврата 200
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        System.out.println("INSIDE DELETE");
        String json = getRequestBody(req.getReader());
        System.out.println(json);

        String uri = req.getScheme() + "://" +   // "http" + "://
                req.getServerName() +       // "myhost"
                ":" +                           // ":"
                req.getServerPort() +       // "8080"
                req.getRequestURI() +       // "/people"
                "?" +                           // "?"
                req.getQueryString();       // "lastname=Fox&age=30"
        System.out.println(uri);

        //todo check status codes
        String query = req.getQueryString();
        if (query != null && query.matches("id=.*")) {
            var id = query.substring(3);
            System.out.println(id + " received id");
            //            tasks.remove(Integer.valueOf(id));
            taskStorage.deleteById(Integer.valueOf(id));
            resp.setStatus(HttpServletResponse.SC_OK);
        } else {
            taskStorage.deleteAll();
            resp.setStatus(HttpServletResponse.SC_OK);
        }
        System.out.println("OUT OF DELETE");
    }
}
