package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Schedule
 */
@WebServlet("/Schedule")
public class Schedule extends HttpServlet {
    private static final long serialVersionUID = 1L;


    public Schedule() {
        // TODO Auto-generated constructor stub
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        Calendar cal = Calendar.getInstance();
        int year=new Integer(request.getParameter("year"));
        int month=new Integer(request.getParameter("month"));
        cal.set(year, month-1,1);
        log(cal.getTime().toString());
        int iweek = cal.get(Calendar.DAY_OF_WEEK);
        log("iweek:"+iweek);
        cal.add(Calendar.MONTH,1);
        cal.add(Calendar.DATE,-1);
        int maxdate = cal.get(Calendar.DATE);
        PrintWriter writer = response.getWriter();
        writer.println("<!DOCTYPE html><html>");
        writer.println("<head><meta charset='UTF-8'><style>table{table-layout:fixed;}th,td{width:100px;height:100px;border:solid 5px black;font-size:30px;background-color:white;overflow:hidden;}");
        writer.println("td{transition:all 0.1s ease-in;-webkit-transition:all 0.1s ease-in;}");
        writer.println("td:hover{transform:scale(3);-webkit-transform:scale(3);font-size:10px;transition:all 1s ease-in 1s;-webkit-transition:all 1s ease-in 1s;z-index:1;overflow:scroll;>}");
        writer.println("div{width:90px;height:90px;)}");
        writer.println("</style></head><body>");
        writer.println("<table><tr><th>にち</th><th>げつ</th><th>か</th><th>すい</th><th>もく</th><th>きん</th><th>ど</th></tr>");
        int row;
        int col;
        int day=0;
        for(row=0;row<6;row++){
            writer.println("<tr>");
            for(col=0;col<7;col++){

               if(row==0&&col<iweek-1||day>=maxdate){
                   writer.println("<td></td>");
               }else{
                   writer.println("<td><div>"+(++day)+"<ul><li>a</li><li>a</li><li>a</li><li>a</li><li>a</li><li>a</li><li>a</li><li>a</li><li>a</li><li>a</li></ul></div></td>");
               }

            }
            writer.println("</tr>");
        }
        writer.println("</table>");
        writer.println("</body></html>");
        log(cal.getTime().toString());
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

}
