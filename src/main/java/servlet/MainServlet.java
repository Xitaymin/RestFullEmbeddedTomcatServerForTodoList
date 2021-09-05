package servlet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import entity.Task;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "MainServlet", urlPatterns = {"/todo"})
public class MainServlet extends HttpServlet {
    private Map<Integer, Task> tasks;

    //GET /todo - возвращает список задач
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        System.out.println("INSIDE GET");
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        Collection<Task> tasksList = tasks.values();
        String json = mapper.writeValueAsString(tasksList);
        System.out.println(json);
        PrintWriter out = resp.getWriter();
        out.print(json);
        out.flush();
        out.close();
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
        String body = req.getReader().lines().collect(Collectors.joining());
        System.out.println(body);

        ObjectMapper mapper =
                new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        Task task = mapper.readValue(body, Task.class);
        //todo set encoding in filter
        if (task.getId() == 0) {
            task.setId(body.hashCode());

            tasks.put(task.getId(), task);
            System.out.println(task);

            PrintWriter out = resp.getWriter();
            out.print(mapper.writeValueAsString(task));
            out.flush();
            out.close();
        } else {
            tasks.replace(task.getId(), task);
            PrintWriter out = resp.getWriter();
            out.print(mapper.writeValueAsString(task));
            out.flush();
            out.close();
            System.out.println("OUT OF POST");
        }
    }

    @Override
    public void init() {
        tasks = new HashMap<>();
    }

    //    //DELETE /todo - очистить список задач, ответ пустой, код возврата 200
    //    //
    //    //DELETE /todo?id={id} - где вместо {id} указан id задачи, по этому
    //    // запросу нужно удалить эту задачу. ответ пустой, код возврата 200
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("INSIDE DELETE");
        String body = req.getReader().lines().collect(Collectors.joining());
        System.out.println(body);

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
            tasks.remove(Integer.valueOf(id));
            resp.setStatus(HttpServletResponse.SC_OK);
        } else {
            tasks.clear();
            resp.setStatus(HttpServletResponse.SC_OK);
        }
        System.out.println("OUT OF DELETE");
    }
}
