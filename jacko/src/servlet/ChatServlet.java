package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.comet.CometEvent;
import org.apache.catalina.comet.CometProcessor;

/**
 * Servlet implementation class ChatServlet
 */
@WebServlet("/ChatServlet")
public class ChatServlet
extends HttpServlet implements CometProcessor {

protected HashMap<HttpServletResponse,Integer> connections =
    new HashMap<HttpServletResponse,Integer>();
protected ArrayList<Integer> congid=new ArrayList<Integer>();
protected MessageSender messageSender = null;

Connection dbcon;

public void init() throws ServletException {
    log("chatservlet initializing");
    try{
        dbcon = DbCon.con();
    } catch (Exception e){
        log("error:"+e.getMessage());
    }
    messageSender = new MessageSender();
    Thread messageSenderThread =
        new Thread(messageSender, "MessageSender[" + getServletContext().getContextPath() + "]");
    messageSenderThread.setDaemon(true);
    messageSenderThread.start();
}

public void destroy() {
    connections.clear();
    messageSender.stop();
    messageSender = null;
}

/**
 * Process the given Comet event.
 *
 * @param event The Comet event that will be processed
 * @throws IOException
 * @throws ServletException
 */
public void event(CometEvent event)
    throws IOException, ServletException {
    HttpServletRequest request = event.getHttpServletRequest();
    HttpServletResponse response = event.getHttpServletResponse();
    request.setCharacterEncoding("UTF-8");
    response.setCharacterEncoding("UTF-8");
    log(event.getEventType().toString());
    if (event.getEventType() == CometEvent.EventType.BEGIN) {
        int gid=0;
        if(request.getMethod().equals("GET")){
            gid=Integer.valueOf(request.getParameter("gid"));
        }
        PrintWriter writer = response.getWriter();
        writer.println("<!DOCTYPE html>");
        writer.println("<head><title>Chat</title><meta charset='UTF-8'>");
        writer.println("<style>#dropzone{width:100%;height:100%;top:0px;position:absolute;}</style></head>");
        writer.println("<script>gid="+gid+"</script>");
        writer.println("<body><div id='dropzone'>");
        writer.println("<script type='text/javascript' src='Upload.js'>aaaaaaa</script>");

        synchronized(connections) {
            connections.put(response,gid);
        }
        try{
            PreparedStatement dbst = dbcon.prepareStatement("select message from(select message,time from chatlog where gid=? order by time desc limit 10) a order by time;");
            if(request.getMethod().equals("GET")){
                dbst.setString(1,request.getParameter("gid"));
            }else{
                dbst.setString(1,"0");
            }
            ResultSet dbrs= dbst.executeQuery();
            while(dbrs.next()){
                writer.println(dbrs.getString("message")+"<br>");
            }
        }catch(Exception e){
            log("error:"+e.getMessage());
        }
        writer.flush();

        log("begin");
    } else if (event.getEventType() == CometEvent.EventType.ERROR) {
        try{
        synchronized(connections) {
            connections.remove(response);
        }
        }catch(Exception e){log("errorerror");}finally{event.close();}
    } else if (event.getEventType() == CometEvent.EventType.END) {
        try{
        synchronized(connections) {
            connections.remove(response);
        }
        PrintWriter writer = response.getWriter();
        writer.println("</div></body></html>");
        }catch(Exception e){log("enderror");}finally{event.close();}
    } else if (event.getEventType() == CometEvent.EventType.READ) {
        String user = request.getParameter("user");
        String message = request.getParameter("message");
        String gid = request.getParameter("gid");
        log(request.getContentType());
        if(user.equals("file")){
            try{
            PreparedStatement dbst = dbcon.prepareStatement("select max(sub) from file where gid=? and name=?");
            dbst.setString(1, gid);
            dbst.setString(2,message);
            ResultSet dbrs= dbst.executeQuery();
            dbrs.next();
            int sub = dbrs.getInt(1);
            message="<button onclick=\"download("+gid+",'"+message+"',"+sub+")\">"+message+"</button>";
            }catch(Exception e){log("aaaaa");}
        }
        messageSender.send(gid, user, message);

    }
}

public class MessageSender implements Runnable {

    protected boolean running = true;
    protected ArrayList<String> messages = new ArrayList<String>();
    public MessageSender() {
    }

    public void stop() {
        running = false;
    }

    /**
     * Add message for sending.
     */
    public void send(String gid, String user, String message) {
        synchronized (messages) {
            try{
                log(gid+"[" + user + "]: " + message);
                PreparedStatement dbst = dbcon.prepareStatement("insert into chatlog(gid,message) values(?,?)");
                dbst.setString(1, gid);
                dbst.setString(2, "[" + user + "]: " + message);
                dbst.executeUpdate();
                messages.add(gid+"," + user + "," + message);
                messages.notify();
            }catch(Exception e){
            }
        }
    }

    public void run() {

        while (running) {

            if (messages.size() == 0) {
                try {
                    synchronized (messages) {
                        messages.wait();
                    }
                } catch (InterruptedException e) {
                    // Ignore
                }
            }

            synchronized (connections) {
                String[] pendingMessages = null;
                synchronized (messages) {
                    pendingMessages = messages.toArray(new String[0]);
                    messages.clear();
                }
                // Send any pending message on all the open connections
                PrintWriter writer;
                int gid;
                String[] messages=new String[3];
                for (Entry<HttpServletResponse,Integer> entry:connections.entrySet()) {
                    try {
                        writer = entry.getKey().getWriter();
                        gid=entry.getValue();
                        for (int j = 0; j < pendingMessages.length; j++) {
                            messages=pendingMessages[j].split(",",3);
                            if(gid==Integer.valueOf(messages[0])){
                                writer.println("["+messages[1]+"]: "+messages[2] + "<br>");
                            }
                        }
                        writer.flush();
                    } catch (IOException e) {
                        log("IOExeption sending message", e);
                    }
                }
            }
        }
    }
}

}