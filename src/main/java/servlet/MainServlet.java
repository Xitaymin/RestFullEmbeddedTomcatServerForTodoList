package servlet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import entity.Task;
import dao.TaskDAO;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.stream.Collectors;

@WebServlet(name = "MainServlet", urlPatterns = {"/todo"})
public class MainServlet extends HttpServlet {
    private final TaskDAO taskDAO = new TaskDAO();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        //todo found out what is it
        Collection<Task> tasksList = taskDAO.getAllTasks();
        String json = mapper.writeValueAsString(tasksList);
        sendResponse(resp.getWriter(), json);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String json = getRequestBody(req.getReader());
        System.out.println(json);
        //todo think about creation task in task storage
        //        Task task = mapper.readValue(json, Task.class);
        Task task = null;
        try {
            //            todo process case with empty id
            task = taskDAO.createTaskOrUpdateIfExist(json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        //        task = taskDAO.addOrUpdateIfExist(task);
        sendResponse(resp.getWriter(), mapper.writeValueAsString(task));
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
        //        mapper.configure(DeserializationFeature
        //        .FAIL_ON_UNKNOWN_PROPERTIES,
        //                         false);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        //todo check status codes
        //todo create better logic for retrieving id
        String query = req.getQueryString();
        if (query == null) {
            taskDAO.deleteAll();
            resp.setStatus(HttpServletResponse.SC_OK);
            //todo replace with constant
        } else if (query.matches("id=.*")) {
            Integer id = getIdFromQuery(query);
            if (taskDAO.deleteById(id)) {
                resp.setStatus(HttpServletResponse.SC_OK);
            } else {
                //todo what is correct sc for this case
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);
        }
    }

    private Integer getIdFromQuery(String query) {
        String id = query.substring(3);
        return Integer.valueOf(id);
    }
}
