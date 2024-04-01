package ru.otus.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

/*
Добавьте пользователям роли: USER, ADMIN
Для пользователей с ролью ADMIN реализуйте возможность
отключения пользователей от чата с помощью команды «/kick username»
 */

public class ClientApplication {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try (
                Socket socket = new Socket("localhost", 8189);
                DataInputStream in = new DataInputStream(socket.getInputStream());
                DataOutputStream out = new DataOutputStream(socket.getOutputStream())
        ) {
            System.out.println("Подключились к серверу");
            new Thread(() -> {
                try {
                    while (true) {
                        if (in.available() > 0) {
                            String inMessage = in.readUTF();
                            System.out.println(inMessage);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
            while (true) {
                    String msg = scanner.nextLine();
                    out.writeUTF(msg);

                    if (msg.equals("/exit")) {
                        break;
                    }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}