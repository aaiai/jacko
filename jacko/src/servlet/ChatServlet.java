package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.catalina.comet.CometEvent;
import org.apache.catalina.comet.CometProcessor;

/**
 * Servlet implementation class ChatServlet
 */
@WebServlet("/ChatServlet")
@MultipartConfig(maxFileSize=1048576)
public class ChatServlet
extends HttpServlet implements CometProcessor {

protected ArrayList<HttpServletResponse> connections =
    new ArrayList<HttpServletResponse>();
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

    if (event.getEventType() == CometEvent.EventType.BEGIN) {
        PrintWriter writer = response.getWriter();
        writer.println("<!DOCTYPE html>");
        writer.println("<head><title>Chat</title><meta charset='UTF-8'>");
        writer.println("<style>#dropzone{width:100%;height:100%;top:0px;position:absolute;}</style></head>");
        writer.println("<body><div id='dropzone'>");
        writer.println("<script type='text/javascript' src='Upload.js'>aaaaaaa</script>");
        try{
            PreparedStatement dbst = dbcon.prepareStatement("select message from(select message,time from chatlog order by time desc limit 10) a order by time;");
            ResultSet dbrs= dbst.executeQuery();
            while(dbrs.next()){
                writer.println(dbrs.getString("message")+"<br>");
            }
        }catch(Exception e){
            log("error:"+e.getMessage());
        }
        writer.flush();
        synchronized(connections) {
            connections.add(response);
        }
    } else if (event.getEventType() == CometEvent.EventType.ERROR) {
        synchronized(connections) {
            connections.remove(response);
        }
        event.close();
    } else if (event.getEventType() == CometEvent.EventType.END) {
        synchronized(connections) {
            connections.remove(response);
        }
        PrintWriter writer = response.getWriter();
        writer.println("</div></body></html>");
        event.close();
    } else if (event.getEventType() == CometEvent.EventType.READ) {
        String user = request.getParameter("user");
        String message = request.getParameter("message");
        log(request.getContentType());
        if(request.getContentType().startsWith("multipart/form-data")){
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
                    log(getServletContext().getRealPath("/uploaded/" + 1 +"_" + sub + "_" + name));
                    part.write(getServletContext().getRealPath("/uploaded/" + 1 +"_" + sub + "_" + name));
                    messageSender.send("file","<button onclick=\"download(1,'"+name+"',"+sub+")\">"+name+"</button>");
                }catch(Exception e){
                    log("error:"+e.getMessage());
                }
            }
        }else{messageSender.send(user, message);}
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
    public void send(String user, String message) {
        synchronized (messages) {
            try{
                log("[" + user + "]: " + message);
                PreparedStatement dbst = dbcon.prepareStatement("insert into chatlog(message) values(?)");
                dbst.setString(1, "[" + user + "]: " + message);
                dbst.executeUpdate();
                messages.add("[" + user + "]: " + message);
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
                for (int i = 0; i < connections.size(); i++) {
                    try {
                        PrintWriter writer = connections.get(i).getWriter();
                        for (int j = 0; j < pendingMessages.length; j++) {
                            writer.println(pendingMessages[j] + "<br>");
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