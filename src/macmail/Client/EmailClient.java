package macmail.Client;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 *
 * @author asus
 */
public class EmailClient {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 12345);

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in));

            // Đọc và in thông báo chào mừng từ máy chủ
            String welcomeMessage = reader.readLine();
            System.out.println(welcomeMessage);

            // Nhập tên đăng nhập
            System.out.println("Enter your username:");
            String username = consoleReader.readLine();
            writer.println(username);

            // Gửi và nhận tin nhắn
            while (true) {
                System.out.println("Enter your message (type 'exit' to quit):");
                String message = consoleReader.readLine();
                writer.println(message);

                if (message.equalsIgnoreCase("exit")) {
                    break;
                }

                // Nhận tin nhắn từ máy chủ và in ra màn hình
                String receivedMessage = reader.readLine();
                System.out.println("Received: " + receivedMessage);
            }

            // Đóng kết nối
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}