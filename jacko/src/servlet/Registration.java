package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/Registration")
public class Registration extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        PrintWriter writer=response.getWriter();

        writer.println("<!DOCTYPE html><html><head>");
        writer.println("<script type='text/javascript' src='SelectYear.js'></script>");
        writer.println("</head><body><form method='post'>");
        try{
            Connection dbcon = DbCon.con();
            PreparedStatement dbst = dbcon.prepareStatement("select * from department");
            ResultSet dbrs = dbst.executeQuery();
            writer.println("<p>学科<select id='did' name='did' onchange='selectyear()'>");
            writer.println("<option>選択してください</option>");
            while(dbrs.next()){
                writer.println("<option value="+dbrs.getString(1)+" year="+dbrs.getString(3)+">"+dbrs.getString(2)+"</option>");
            }
            writer.println("</select></p>");

            dbcon.close();
        }catch(Exception e){}
        writer.println("<p>学年<select id='year' name='year'><option value='-1'>学科を選択</option></select></p>");
        writer.println("<p>ID/学籍番号<input type='text' name='mid'></p>");
        writer.println("<p>名前<input type='text' name='name'></p>");
        writer.println("<p><input type='submit'></p>");
        writer.println("</form></body></html>");

    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        PrintWriter writer=response.getWriter();
        try {
            Connection dbcon=DbCon.con();
            PreparedStatement dbst = dbcon.prepareStatement("insert into member set mid=?,name=?,did=?,year=?,pass=?");
            dbst.setString(1, request.getParameter("mid"));
            dbst.setString(2, request.getParameter("name"));
            dbst.setString(3, request.getParameter("did"));
            dbst.setString(4, request.getParameter("year"));
            dbst.setString(5, Password.getHash(request.getParameter("mid"),request.getParameter("mid")));
            dbst.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        doGet(request,response);
    }

}
