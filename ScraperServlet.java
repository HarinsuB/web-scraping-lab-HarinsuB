import com.google.gson.Gson;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@WebServlet("/scrape")
public class ScraperServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Session Tracking
        HttpSession session = request.getSession();
        Integer visitCount = (Integer) session.getAttribute("visitCount");
        if (visitCount == null) visitCount = 0;
        session.setAttribute("visitCount", visitCount + 1);

        String url = request.getParameter("url");
        String[] options = request.getParameterValues("option");

        Map<String, Object> resultMap = new LinkedHashMap<>();

        try {
            if (url != null && options != null) {
                // Clean up URL input
                url = url.trim().replaceAll("[()]", "");

                Document doc = Jsoup.connect(url).get();

                for (String option : options) {
                    switch (option) {
                        case "title":
                            resultMap.put("Title", doc.title());
                            break;
                        case "headings":
                            List<String> headings = new ArrayList<>();
                            for (int i = 1; i <= 6; i++) {
                                Elements els = doc.select("h" + i);
                                for (Element el : els) headings.add(el.text());
                            }
                            resultMap.put("Headings", headings);
                            break;
                        case "links":
                            List<String> links = new ArrayList<>();
                            Elements linkEls = doc.select("a[href]");
                            for (Element el : linkEls)
                                links.add(el.absUrl("href"));
                            resultMap.put("Links", links);
                            break;
                        case "images":
                            List<String> images = new ArrayList<>();
                            Elements imgEls = doc.select("img");
                            for (Element el : imgEls)
                                images.add(el.absUrl("src"));
                            resultMap.put("Images", images);
                            break;
                    }
                }
            }

            // Generate HTML output
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();

            out.println("<html><body>");
            out.println("<h2>You have visited this page " + (visitCount + 1) + " times.</h2>");
            out.println("<h3>Scraped Results:</h3>");
            out.println("<table border='1'>");

            for (Map.Entry<String, Object> entry : resultMap.entrySet()) {
                out.println("<tr><td><strong>" + entry.getKey() + "</strong></td><td>");

                if (entry.getValue() instanceof List) {
                    for (Object item : (List<?>) entry.getValue()) {
                        out.println(item.toString() + "<br>");
                    }
                } else {
                    out.println(entry.getValue());
                }

                out.println("</td></tr>");
            }

            out.println("</table>");

            // JSON output
            Gson gson = new Gson();
            String json = gson.toJson(resultMap);

            out.println("<br><h3>JSON Output:</h3>");
            out.println("<pre>" + json + "</pre>");

            // CSV download setup
            out.println("<form method='POST' action='download'>");
            out.println("<input type='hidden' name='csvdata' value='" + json.replace("\"", "'") + "'>");
            out.println("<button type='submit'>Download CSV</button>");
            out.println("</form>");

            out.println("</body></html>");

        } catch (Exception e) {
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            out.println("<h2 style='color:red'>Error: " + e.getMessage() + "</h2>");
        }
    }
}
