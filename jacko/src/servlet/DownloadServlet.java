package servlet;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/DownloadServlet")
public class DownloadServlet extends HttpServlet {

    Connection dbcon;

    public void init() throws ServletException {
        log("uploadservlet initializing");
        try {
            dbcon = DbCon.con();
        } catch (Exception e) {
            log(e.getMessage());
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        String gid = request.getParameter("gid");
        String name = request.getParameter("name");
        String sub = request.getParameter("sub");
        log(getServletContext().getRealPath("/uploaded/" + gid + "_" + sub + "_" + name));
        InputStream in = null;
        OutputStream out = null;
        try {
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "filename='" + name + "'");
            in = new FileInputStream(getServletContext().getRealPath("/uploaded/" + gid + "_" + sub + "_" + name));
            out = response.getOutputStream();
            byte[] buff = new byte[1024];
            int len = 0;
            while ((len = in.read(buff, 0, buff.length)) != -1) {
                out.write(buff, 0, len);
            }
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                }
            }
        }
    }
}
