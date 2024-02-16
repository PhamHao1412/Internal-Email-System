/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package macmail;

import macmail.Entity.Email;
import macmail.DAO.ConnectDB;
import macmail.DAO.EmailDAO;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import DTO.EmailDTO;
import DTO.NotifyDTO;
import DTO.ReplyDTO;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import macmail.DAO.NotifyDAO;
import macmail.DAO.RecipientDAO;
import macmail.DAO.ReplyDAO;
import macmail.DAO.UserDAO;
import macmail.Entity.Notify;
import macmail.Entity.Recipients;
import macmail.Entity.Reply;
import macmail.Entity.User;

/**
 *
 * @author asus
 */
public class Server {
    private static Connection conn;
    private static  EmailDAO emailDao;
    private static  UserDAO userDao;
    private static  ReplyDAO replyDao;
    private static  RecipientDAO recipientDao;
    private static  NotifyDAO notifyDao;
     public static void main(String[] args) {
        int port = 1234;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is running, waiting to connect.....");
            while (true) {
                try {
                    Socket socket = serverSocket.accept();
                    System.out.println(socket.getInetAddress().getHostAddress()+" has connected");
                    Thread clientHandler = new Thread(() -> handleClient(socket));
                    clientHandler.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
       private static void handleClient(Socket socket) {
        try (DataInputStream dis = new DataInputStream(socket.getInputStream());
             DataOutputStream dos = new DataOutputStream(socket.getOutputStream())) {

            // Read the command from the client
            String command = dis.readUTF();

            // Based on the command, call the appropriate handler method
            switch (command) {
                case "SEND_MAIL":
                    handleSendMail(dis, dos);
                    break;
                case "REPLY_MAIL":
                    handleReplyMail(dis, dos);
                    break;
                // Add more cases for other commands as needed
                default:
                    // Handle unknown command
                    dos.writeUTF("Unknown command");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String sendMail(String sender, String recipients, String subject, String content) {
        ConnectDB cnn = new ConnectDB();
         conn = cnn.getConnect();
         emailDao = new EmailDAO(conn);
         userDao = new UserDAO(conn);
         replyDao = new ReplyDAO(conn);
         recipientDao = new RecipientDAO(conn);
         notifyDao = new NotifyDAO(conn);
        if (conn == null) {
            return "Không thể kết nối đến cơ sở dữ liệu";
        }else {
            String filterRecipType = recipients.replaceAll("\\(cc\\)|\\(bcc\\)", "");
            String[] recipientArray = filterRecipType.split(",");
            List<String> listEmail = new ArrayList<>();
            String response = "";
            for (String recipient : recipientArray) {
                recipient = recipient.trim();
                User userFound = userDao.findUserByEmail(recipient);
                if(userFound != null){
                    listEmail.add(recipient);
                }else{
                      response = "Recipient not found: " + recipient;
                      return response;
                }
            }
            EmailDTO emailDTO = emailDao.saveEmail(sender, subject); 
            for (String recipient : listEmail) {
                Notify notiFound = notifyDao.getNotifyByUser(emailDTO.getId(), recipient);
                NotifyDTO notifyDTO = null;
               if(notiFound == null){
                  notifyDTO =  notifyDao.saveNoti(emailDTO.getId(), recipient);
               }else{
                notifyDTO = notifyDao.updateNewNoti(emailDTO.getId(), recipient);
               }
                ReplyDTO replyDTO  = replyDao.saveReply(content, sender, recipient, emailDTO.getId(), notifyDTO.getId());            
                String[] listEmailRecip = recipients.split(",");
                for (String recip : listEmailRecip) {
                          if(recip.contains("(cc)")){
                             String cc_recip = recip.replaceAll("\\(cc\\)", "").trim();
                             recipientDao.saveRecipient(replyDTO.getId(), cc_recip, "cc"); 
                          }
                         else if(recip.contains("(bcc)")){
                          String bcc_recip = recip.replaceAll("\\(bcc\\)", "").trim();
                            recipientDao.saveRecipient(replyDTO.getId(), bcc_recip, "bcc");
                          }
                          else{
                            recipientDao.saveRecipient(replyDTO.getId(), recip, "default"); 
                          }
                      }
                    response = "Mail sent successfully";
            }
            return response;
        }
    }
   public static String replyMail(String content,String sender, String recipients,int email_id) {
        Pattern pattern = Pattern.compile("\\(([^)]+)\\)");
        Matcher matcher = pattern.matcher(recipients);
        List<String> listEmail = new ArrayList<>();
       ConnectDB cnn = new ConnectDB();
       String response = "";
         conn = cnn.getConnect();
         replyDao = new ReplyDAO(conn);
         notifyDao = new NotifyDAO(conn);
        if (conn == null) {
            return "Cannot connect database";
        } else {
            while (matcher.find()){
                listEmail.add(matcher.group(1));
            }
            for(String recipient : listEmail){
               Notify notiFound = notifyDao.getNotifyByUser(email_id, recipient);
               NotifyDTO notifyDTO = null;
               if(notiFound == null){
                  notifyDTO =  notifyDao.saveNoti(email_id, recipient);
               }else{
                notifyDTO = notifyDao.updateNewNoti(email_id, recipient);
               }     
                ReplyDTO replyDTO  = replyDao.saveReply(content, sender, recipient, email_id, notifyDTO.getId());                
                if(replyDTO != null){
                     for (String user : listEmail) {
                     recipientDao.saveRecipient(replyDTO.getId(), user, null); 
                      }
                    response = "Mail sent successfully";
                }
            }
        }
        return response;
    }  
    private static Email filterSpamEmail(String sender, String recipient,String subject){
          EmailDAO emailDao = new EmailDAO(conn);
           List<Email> listEmailSpamUser = emailDao.findEmailSpamByUser(recipient);
           for(Email email : listEmailSpamUser){
               if(email.getSender().getEmail().equals(sender)){
                   if(email.getSubject().equals(subject)){
                       return email;
                  }
               }
             }
           return null;
      }
    private static void handleSendMail(DataInputStream dis, DataOutputStream dos) throws IOException {
        String sender = dis.readUTF();
        String recipients = dis.readUTF();
        String subject = dis.readUTF();
        String content = dis.readUTF();
        String response = sendMail(sender, recipients, subject, content);
        dos.writeUTF(response);
    }
    private static void handleReplyMail(DataInputStream dis, DataOutputStream dos) throws IOException {
        String content = dis.readUTF();
        String sender = dis.readUTF();
        String recipient = dis.readUTF();
       int email_id = dis.readInt();
        String response = replyMail(content, sender, recipient, email_id);
        dos.writeUTF(response);
    }
    
}
