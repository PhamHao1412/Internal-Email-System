/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package macmail.DAO;

import DTO.EmailDTO;
import com.mysql.jdbc.Statement;
import macmail.DAO.UserDAO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import macmail.Entity.Email;
import macmail.Entity.User;
/**
 *
 * @author asus
 */
public class EmailDAO {
private Connection conn;

        public EmailDAO(Connection conn) {
            this.conn = conn;
        }
        
        public Email findEmailById(int email_id) {
        String selectSql = "SELECT * FROM Email WHERE id = ?";
        try (PreparedStatement selectPst = conn.prepareStatement(selectSql)) {
            selectPst.setInt(1, email_id);
            ResultSet resultSet = selectPst.executeQuery();
            if (resultSet.next()) {
                // Assuming you have an Email class with appropriate attributes
                Email email = new Email();
                email.setId(resultSet.getInt("id"));
                email.setSubject(resultSet.getString("subject"));
                UserDAO userDao = new UserDAO(conn);
                User sender = userDao.findUserByEmail(resultSet.getString("sender"));
                email.setSender(sender);
                return email;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
         return null; // Return null if not found or an error occurred

    }
     public void updateIsRead(int email_id, String recipient) {
            String updateSql = "UPDATE notify SET isRead = 1 WHERE email_id = ? and recipient =?";
            try (PreparedStatement updatePst = conn.prepareStatement(updateSql)) {
                updatePst.setInt(1, email_id);
                updatePst.setString(2, recipient);
                updatePst.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            } 
    }
       public void updateIsUnRead(int email_id, String recipient) {
            String updateSql = "UPDATE Notify SET isRead = 0 WHERE email_id = ? and recipient =?";
            try (PreparedStatement updatePst = conn.prepareStatement(updateSql)) {
                updatePst.setInt(1, email_id);
                updatePst.setString(2,recipient);
                updatePst.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
    }
          public void reportSpamEmail(int email_id, String recipient) {
            String updateSql = "UPDATE Notify SET isSpam = 1 WHERE email_id = ? and recipient=?";
            try (PreparedStatement updatePst = conn.prepareStatement(updateSql)) {
                updatePst.setInt(1, email_id);
                updatePst.setString(2,recipient);
                updatePst.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
 
    }
     public void deleteEmail(int email_id, String recipient) {
        String updateSql = "UPDATE Notify SET isBin = 1 WHERE email_id = ? and recipient=?";
        try (PreparedStatement updatePst = conn.prepareStatement(updateSql)) {
            updatePst.setInt(1, email_id);
            updatePst.setString(2,recipient);
            updatePst.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
     }
     public List<Email> findEmailSpam(String sender, String recipient) {
        String selectSql = "SELECT * FROM Email WHERE isSpam = 1 and sender = ? and recipient =?";
         List<Email> emails = new ArrayList<>();
        try (PreparedStatement selectPst = conn.prepareStatement(selectSql)) {
            selectPst.setString(1, sender);
            selectPst.setString(2, recipient);
            ResultSet resultSet = selectPst.executeQuery();
            if (resultSet.next()) {
                // Assuming you have an Email class with appropriate attributes
                 Email email = new Email();
                 email.setId(resultSet.getInt("id"));
                emails.add(email);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
         return emails; // Return null if not found or an error occurred
    }
        public List<Email> findEmailSpamByUser(String user) {
        String selectSql = "SELECT * FROM Email WHERE isSpam = 1 and recipient= ?";
         List<Email> emails = new ArrayList<>();
         UserDAO userDao = new UserDAO(conn);
        try (PreparedStatement selectPst = conn.prepareStatement(selectSql)) {
            selectPst.setString(1, user);
            ResultSet resultSet = selectPst.executeQuery();
            while (resultSet.next()) {
                // Assuming you have an Email class with appropriate attributes
                 Email email = new Email();
                 User emailSender = userDao.findUserByEmail(resultSet.getString("sender"));
                 email.setId(resultSet.getInt("id"));
                 email.setSender(emailSender);
                 email.setSubject(resultSet.getString("subject"));
                emails.add(email);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
         return emails; // Return null if not found or an error occurred
    }
    public EmailDTO saveEmail(String sender, String subject) {
        String sql = "INSERT INTO Email(sender,subject) VALUES(?, ?)";
        try (PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, sender);
            pst.setString(2, subject);
            int affectedRows = pst.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pst.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int generatedId = generatedKeys.getInt(1);
                        EmailDTO email = new EmailDTO();
                        email.setId(generatedId);
                        return email;
                    }
                }
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public EmailDTO autoFilterEmailSpam(String sender, String recipient, String subject, String content){
    String sql = "INSERT INTO Email (sender, recipient, subject, content, isSpam) VALUES (?, ?, ?, ?, 1)";
        try (PreparedStatement pst = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, sender);
            pst.setString(2, recipient);
            pst.setString(3, subject);
            pst.setString(4, content);
           int affectedRows = pst.executeUpdate();
            if (affectedRows > 0) {
                // Lấy ResultSet chứa các giá trị sinh ra tự động
                try (ResultSet generatedKeys = pst.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        // Lấy giá trị ID từ ResultSet
                        int generatedId = generatedKeys.getInt(1);
                        EmailDTO email = new EmailDTO();
                        email.setId(generatedId);
                        return email;
                  }
            }
        }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }  
}

