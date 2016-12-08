package servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

/**
 * Servlet implementation class UploadServlet
 */
@WebServlet("/UploadServlet")
@MultipartConfig(maxFileSize=1048576)


public class UploadServlet extends HttpServlet {

    Connection dbcon;

    public void init() throws ServletException {
        log("uploadservlet initializing");
        try{
            Class.forName("com.mysql.jdbc.Driver");
            dbcon = DriverManager.getConnection("jdbc:mysql://localhost/jacko", "a", "a");
        } catch (Exception e){
           log("error:"+e.getMessage());
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        for(Part part:request.getParts()){
            log(String.valueOf(part.getSize()));
            String name = this.getFileName(part);
            try{
        	PreparedStatement dbst = dbcon.prepareStatement("select max(sub)+1 from file where gid=? and name=?");
                dbst.setInt(1, 1);
                dbst.setString(2,name);
                ResultSet dbrs= dbst.executeQuery();
                dbrs.next();
                int sub = dbrs.getInt(1);
                dbst = dbcon.prepareStatement("insert into file(gid,name,sub)values(?,?,?)");
                dbst.setInt(1, 1);
                dbst.setString(2, name);
                dbst.setInt(3, sub);
                dbst.executeUpdate();
                part.write(getServletContext().getRealPath("/uploaded/" + 1 +"_" + sub + "_" + name));
            }catch(Exception e){
                log("error:"+e.getMessage());
            }

        }
    }

    private String getFileName(Part part) {
        String name = null;
        for (String dispotion : part.getHeader("Content-Disposition").split(";")) {
            if (dispotion.trim().startsWith("filename")) {
                name = dispotion.substring(dispotion.indexOf("=") + 1).replace("\"", "").trim();
                name = name.substring(name.lastIndexOf("\\") + 1);
                break;
            }
        }
        return name;
    }
}
