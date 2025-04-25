import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.*;

@WebServlet("/download")
public class DownloadServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Get JSON string from hidden field
        String json = request.getParameter("csvdata");

        // Convert single quotes back to double quotes
        json = json.replace("'", "\"");

        // Convert JSON back to Map
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Object>>() {}.getType();
        Map<String, Object> data = gson.fromJson(json, type);

        // Set response headers
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"scraped_data.csv\"");

        PrintWriter writer = response.getWriter();

        // Write CSV
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            writer.print(entry.getKey());
            writer.print(",");
            if (entry.getValue() instanceof List) {
                List<?> list = (List<?>) entry.getValue();
                writer.print("\"");
                for (Object item : list) {
                    writer.print(item.toString().replace(",", ";"));
                    writer.print(" | ");
                }
                writer.print("\"");
            } else {
                writer.print(entry.getValue().toString().replace(",", ";"));
            }
            writer.println();
        }

        writer.flush();
        writer.close();
    }
}
