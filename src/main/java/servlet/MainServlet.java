package servlet;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import entity.Task;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(
        name = "MainServlet",
        urlPatterns = {"/todo"}
    )
public class MainServlet extends HttpServlet {
    private Map<Integer, Task> tasks;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ServletOutputStream out = resp.getOutputStream();
        out.write("hello heroku".getBytes());
        out.flush();
        out.close();
    }

    @Override
    //POST /todo в теле запроса JSON с задачей, схема ниже. если id объявлен
    // - заменить ассоциированную с ним.
    //Если id в теле запроса отсутствует или null, сгенерировать id и
    // сохранить задачу как новую. Результатом вернуть JSON с сохраненной
    // задачей.
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        //        String body = req.getReader().lines().reduce("",
        //        (accumulator, actual) -> accumulator + actual);
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
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
            resp.setContentType("application/json");
            out.print(mapper.writeValueAsString(task));
            out.flush();
            out.close();
        } else {
            tasks.replace(task.getId(), task);
            PrintWriter out = resp.getWriter();
            resp.setContentType("application/json");
            out.print(mapper.writeValueAsString(task));
            out.flush();
            out.close();
        }
    }

    @Override
    public void init() {
        tasks = new HashMap<>();
    }
}
