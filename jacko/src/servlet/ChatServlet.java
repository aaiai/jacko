package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

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

protected ArrayList<HttpServletResponse> connections =
    new ArrayList<HttpServletResponse>();
protected MessageSender messageSender = null;

public void init() throws ServletException {
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
        writer.println("<head><title>JSP Chat</title></head><body>");
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
        writer.println("</body></html>");
        event.close();
    } else if (event.getEventType() == CometEvent.EventType.READ) {
        String user = request.getParameter("user");
        String message = request.getParameter("message");
        try{
            if(user.length()>0&&message.length()>0){
                messageSender.send(user, message);
                user=null;
                message=null;
            }
        }catch(Exception e){
            log(e.getMessage());
        }
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