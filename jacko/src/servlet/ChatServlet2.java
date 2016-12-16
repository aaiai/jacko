package servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.comet.CometEvent;
import org.apache.catalina.comet.CometProcessor;

/**
 * Servlet implementation class ChatServlet2
 */
@WebServlet("/ChatServlet2")
public class ChatServlet2
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
    if (event.getEventType() == CometEvent.EventType.BEGIN) {
        log("Begin for session: " + request.getSession(true).getId());
        PrintWriter writer = response.getWriter();
        writer.println("<!DOCTYPE html>");
        writer.println("<head><title>Chat</title><meta charset='UTF-8'>");
        writer.println("<style>#dropzone{width:100%;height:100%;top:0px;position:absolute;}</style></head>");
        writer.println("<body><div id='dropzone'>");
        writer.println("<script type='text/javascript' src='Upload.js'>aaaaaaa</script>");
        try{
            PreparedStatement dbst = dbcon.prepareStatement("select message from(select message,time from chatlog where gid=? order by time desc limit 10) a order by time;");
            if(request.getParameterMap().containsKey("gid")){
                dbst.setString(1, request.getParameter("gid"));
            }else{
                dbst.setString(1,"1");
            }
            ResultSet dbrs= dbst.executeQuery();
            while(dbrs.next()){
                writer.println(dbrs.getString("message")+"<br>");
            }
        }catch(Exception e){
            log("error:"+e.getMessage());
        }finally{
            writer.flush();
        }
        synchronized(connections) {
            connections.add(response);
        }
    } else if (event.getEventType() == CometEvent.EventType.ERROR) {
        log("Error for session: " + request.getSession(true).getId());
        synchronized(connections) {
            connections.remove(response);
        }
        event.close();
    } else if (event.getEventType() == CometEvent.EventType.END) {
        log("End for session: " + request.getSession(true).getId());
        synchronized(connections) {
            connections.remove(response);
        }
        PrintWriter writer = response.getWriter();
        writer.println("</body></html>");
        event.close();
    } else if (event.getEventType() == CometEvent.EventType.READ) {
        String user = request.getParameter("user");
        String message = request.getParameter("message");
        messageSender.send(user, message);
        InputStream is = request.getInputStream();
        byte[] buf = new byte[512];

        do {
            int n = is.read(buf); //can throw an IOException
            if (n > 0) {
                log("Read " + n + " bytes: " + new String(buf, 0, n)
                        + " for session: " + request.getSession(true).getId());
            } else if (n < 0) {
                //error(event, request, response);
                return;
            }
        } while (is.available() > 0);
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
            messages.add("[" + user + "]: " + message);
            messages.notify();
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

}