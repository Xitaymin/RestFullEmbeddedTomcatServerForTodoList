package servlet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import dao.TaskDAO;
import entity.Task;

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
    private static final String QUERY_WITH_ID_PREFIX = "id=";
    private final TaskDAO taskDAO = new TaskDAO();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void init() {
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Collection<Task> tasksList = taskDAO.getAllTasks();
        String json = mapper.writeValueAsString(tasksList);
        sendResponse(resp.getWriter(), json);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String json = getRequestBody(req.getReader());
        Task task = null;
        try {
            task = taskDAO.createTaskOrUpdateIfExist(json);
        } catch (JsonProcessingException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
        sendResponse(resp.getWriter(), mapper.writeValueAsString(task));
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) {
        String query = req.getQueryString();
        if (query == null) {
            taskDAO.deleteAll();
            resp.setStatus(HttpServletResponse.SC_OK);
        } else if (query.startsWith(QUERY_WITH_ID_PREFIX)) {
            Integer id = getIdFromQuery(query);
            if (taskDAO.deleteById(id)) {
                resp.setStatus(HttpServletResponse.SC_OK);
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_IMPLEMENTED);
        }
    }

    private String getRequestBody(BufferedReader reader) {
        return reader.lines().collect(Collectors.joining());
    }

    private void sendResponse(PrintWriter out, String body) {
        out.print(body);
        out.flush();
        out.close();
    }

    private Integer getIdFromQuery(String query) {
        String id = query.substring(QUERY_WITH_ID_PREFIX.length());
        return Integer.valueOf(id);
    }
}
