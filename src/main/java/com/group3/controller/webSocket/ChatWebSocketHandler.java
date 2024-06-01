package com.group3.controller.webSocket;

import com.group3.tool.SaveSession;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.*;

/**
 * @author group3
 * @date 2024/04
 */
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final static List<WebSocketSession> sessions = Collections.synchronizedList(new ArrayList<WebSocketSession>());

    //Receive text message and send it out
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
//        System.out.println(session.getId()+":send....");
//        chatTextMessageHandler(message.getPayload());
        try {
//            super.handleTextMessage(session, message);
//            System.out.println(session.getId()+" :"+message.getPayload() + "   " + new Date());
            String m = message.getPayload();
            String[] group3 = m.split(",");
            String phone = group3[0];
            long time = Long.parseLong(group3[1]);
            String action = group3[2];
            if (action.equals("start")){
                session.sendMessage(new TextMessage("success"));
                SaveSession.getInstance().save(phone,time);
                return;
            }
            boolean b = SaveSession.getInstance().isHave(phone,time);
            if (b) {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage("error"));
                }
            } else {
                if (session.isOpen())
                    session.sendMessage(new TextMessage("success"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                session.sendMessage(new TextMessage("error"));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    // Processing after connection establishment
    @SuppressWarnings("unchecked")
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
    }

    // Throw exception
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        if (session.isOpen()) {
            session.close();
        }
        sessions.remove(session);
    }

    // Connection closed after processing
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
        sessions.remove(session);
    }

}