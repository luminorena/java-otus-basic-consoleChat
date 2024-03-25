package ru.otus.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler {
    private Server server;
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private String username;

    private static int usersCounter = 0;

    private void generateUsername() {
        usersCounter++;
        this.username = "user" + usersCounter;
    }
/*
Реализуйте возможность отправки личных сообщений: если клиент пишет «/w tom Hello»,
 то сообщение Hello должно быть отправлено только клиенту с ником tom
 */
    public ClientHandler(Server server, Socket socket) throws IOException {
        this.server = server;
        this.socket = socket;
        this.in = new DataInputStream(socket.getInputStream());
        this.out = new DataOutputStream(socket.getOutputStream());
        this.generateUsername();
        new Thread(() -> {
            try {
                System.out.println("Подключился новый клиент");
                while (true) {
                    String msg = in.readUTF();
                    if (msg.startsWith("/")) {
                        if (msg.startsWith("/exit")) {
                            disconnect();
                            break;
                        }
                        if (msg.equals("/w " + username)){
                            server.unicastMessage(this, msg);
                        }
                        continue;
                    }
                    server.broadcastMessage(username + ": " + msg);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                disconnect();
            }
        }).start();
    }

    public void sendMessage(String msg) {
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        server.unsubscribe(this);
        try {
            if (in != null) {
                in.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (out != null) {
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
