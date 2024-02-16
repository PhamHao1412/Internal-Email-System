/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package macmail.DAO;

import DTO.EmailDTO;
import com.mysql.jdbc.Statement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import macmail.Entity.Recipients;
import macmail.Entity.Reply;
import macmail.Entity.User;

/**
 *
 * @author asus
 */
public class RecipientDAO {
       private Connection conn;

    public RecipientDAO(Connection conn) {
       this.conn = conn;
    }
    public Recipients saveRecipient(int reply_id ,String recipient, String recipient_type) {
    String sql = "INSERT INTO Recipient (reply_id, email, recipient_type) VALUES ( ?, ?, ?)";
    try (PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        pst.setInt(1, reply_id);
        pst.setString(2, recipient);
        pst.setString(3, recipient_type);
        int affectedRows = pst.executeUpdate();
        if (affectedRows > 0) {
            UserDAO userDao = new UserDAO(conn);
            User recipientFound = userDao.findUserByEmail(recipient);
            return new Recipients(reply_id ,recipientFound,recipient_type);
        }
        return null;
    } catch (SQLException e) {
        e.printStackTrace();
        return null;
    }
    }
    public List<Recipients> getRecipientsByReply(int email_id){
     String selectSql = "SELECT * FROM Recipient WHERE reply_id = ?";
     List<Recipients> listRecipient = new ArrayList<>();
    try (PreparedStatement selectPst = conn.prepareStatement(selectSql)) {
            selectPst.setInt(1, email_id);
            ResultSet resultSet = selectPst.executeQuery();
            while (resultSet.next()) {
                // Assuming you have an Email class with appropriate attributes
                Recipients recipients = new Recipients();
                recipients.setEmail_id(resultSet.getInt("reply_id"));
                recipients.setRecipient_type(resultSet.getString("recipient_type"));
                UserDAO userDao = new UserDAO(conn);
                User recipient = userDao.findUserByEmail(resultSet.getString("email"));
                recipients.setRecipient(recipient);
                listRecipient.add(recipients);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
      return listRecipient; // Return null if not found or an error occurred
    }
        public List<Recipients> getRecipientsByEmail(int email_id){
     String selectSql = "select * from reply  where email_id= ?;";
     List<Recipients> listRecipient = new ArrayList<>();
    try (PreparedStatement selectPst = conn.prepareStatement(selectSql)) {
            selectPst.setInt(1, email_id);
            ResultSet resultSet = selectPst.executeQuery();
            while (resultSet.next()) {
                // Assuming you have an Email class with appropriate attributes
                Recipients recipients = new Recipients();
                UserDAO userDao = new UserDAO(conn);
                User recipient = userDao.findUserByEmail(resultSet.getString("recipient"));
                recipients.setRecipient(recipient);
                listRecipient.add(recipients);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
      return listRecipient; // Return null if not found or an error occurred
    }
}
