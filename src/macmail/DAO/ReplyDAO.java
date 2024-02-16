/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package macmail.DAO;

import DTO.EmailDTO;
import DTO.ReplyDTO;
import com.mysql.jdbc.Statement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import macmail.Entity.Email;
import macmail.Entity.Reply;
import macmail.Entity.User;

/**
 *
 * @author asus
 */
public class ReplyDAO {
    private Connection conn;

    public ReplyDAO(Connection conn) {
       this.conn = conn;
    }
    public List<Reply> getReplyByEmail(int email_id,String email){
    String selectSql = "SELECT * FROM REPLY WHERE EMAIL_ID = ? AND (SENDER = ? OR RECIPIENT = ?);";
    List<Reply> listReply = new ArrayList<>();
    try (PreparedStatement selectPst = conn.prepareStatement(selectSql)) {
            selectPst.setInt(1, email_id);
            selectPst.setString(2, email);
            selectPst.setString(3, email);
            ResultSet resultSet = selectPst.executeQuery();
            while (resultSet.next()) {
                // Assuming you have an Email class with appropriate attributes
                Reply reply = new Reply();
                reply.setId(resultSet.getInt("id"));
                reply.setContent(resultSet.getString("content"));
                reply.setTimestamp(resultSet.getString("timestamp"));
                UserDAO userDao = new UserDAO(conn);
                User sender = userDao.findUserByEmail(resultSet.getString("sender"));
                reply.setSender(sender);
                User recipient = userDao.findUserByEmail(resultSet.getString("recipient"));
                reply.setRecipient(recipient);
                reply.setEmailId(resultSet.getInt("email_id"));
                listReply.add(reply);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
      return listReply; // Return null if not found or an error occurred
    }
      public Reply getReplyByNoti(int noti_id) {
        String selectSql = "SELECT * FROM `reply`where noti_id= ? ORDER BY timestamp DESC LIMIT 1;";
        try (PreparedStatement selectPst = conn.prepareStatement(selectSql)) {
            selectPst.setInt(1, noti_id);
            ResultSet resultSet = selectPst.executeQuery();
            if (resultSet.next()) {
                // Assuming you have an Email class with appropriate attributes
                Reply reply = new Reply();
                reply.setId(resultSet.getInt("id"));
                reply.setContent(resultSet.getString("content"));
                UserDAO userDao = new UserDAO(conn);
                User sender = userDao.findUserByEmail(resultSet.getString("sender"));
                reply.setSender(sender);
                reply.setTimestamp(resultSet.getString("timestamp"));
                return reply;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
         return null;
    }
    public ReplyDTO saveReply( String content,String sender, String recipient, int email_id,int noti_id){
    String sql = "INSERT INTO Reply(content, sender, recipient, email_id, noti_id) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement pst = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)) {
                pst.setString(1, content);
                pst.setString(2, sender);
                pst.setString(3, recipient);
                pst.setInt(4, email_id);
                pst.setInt(5, noti_id);
              int affectedRows = pst.executeUpdate();
              if (affectedRows > 0) {
              try (ResultSet generatedKeys = pst.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);
                    ReplyDTO replyDTO = new ReplyDTO();
                    replyDTO.setId(generatedId);
                    return replyDTO;
                }
            }
        }
        return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    public Reply getReplyByEmailSender(int id) {
        String selectSql = "SELECT * FROM `reply`where email_id= ? ORDER BY timestamp ASC LIMIT 1;";
        try (PreparedStatement selectPst = conn.prepareStatement(selectSql)) {
            selectPst.setInt(1, id);
            ResultSet resultSet = selectPst.executeQuery();
            if (resultSet.next()) {
                // Assuming you have an Email class with appropriate attributes
                Reply reply = new Reply();
                reply.setId(resultSet.getInt("id"));
                reply.setContent(resultSet.getString("content"));
                UserDAO userDao = new UserDAO(conn);
                User recipient = userDao.findUserByEmail(resultSet.getString("recipient"));
                reply.setRecipient(recipient);
                reply.setTimestamp(resultSet.getString("timestamp"));
                return reply;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
         return null;
    }
}
