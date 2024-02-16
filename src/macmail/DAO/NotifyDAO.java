/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package macmail.DAO;

import DTO.EmailDTO;
import DTO.NotifyDTO;
import com.mysql.jdbc.Statement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import macmail.Entity.Email;
import macmail.Entity.Notify;
import macmail.Entity.User;

/**
 *
 * @author asus
 */
public class NotifyDAO {
    Connection conn;
     public NotifyDAO(Connection conn) {
            this.conn = conn;
        }
    public NotifyDTO saveNoti(int email_id, String recipient){
        String sql = "INSERT INTO Notify(email_id,recipient) VALUES(?, ?)";
        try (PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pst.setInt(1, email_id);
            pst.setString(2, recipient);
            int affectedRows = pst.executeUpdate();
            if (affectedRows > 0) {
            // Lấy ResultSet chứa các giá trị sinh ra tự động
            try (ResultSet generatedKeys = pst.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);
                    NotifyDTO notifyDTO = new NotifyDTO();
                    notifyDTO.setId(generatedId);
                    return notifyDTO;
                }
            }
        }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
   public NotifyDTO updateNewNoti(int email_id, String recipient) {
    String updateSql = "UPDATE notify SET isRead = 0 WHERE email_id = ? AND recipient = ?";
    String selectSql = "SELECT id FROM notify WHERE email_id = ? AND recipient = ?";
    try (PreparedStatement pstUpdate = conn.prepareStatement(updateSql);
         PreparedStatement pstSelect = conn.prepareStatement(selectSql)) {

        pstUpdate.setInt(1, email_id);
        pstUpdate.setString(2, recipient);
        
        int affectedRows = pstUpdate.executeUpdate();
        
        if (affectedRows > 0) {
            // Nếu có bản ghi được ảnh hưởng, thực hiện SELECT để lấy id
            pstSelect.setInt(1, email_id);
            pstSelect.setString(2, recipient);
            
            try (ResultSet resultSet = pstSelect.executeQuery()) {
                if (resultSet.next()) {
                    int updatedId = resultSet.getInt("id");
                    NotifyDTO notifyDTO = new NotifyDTO();
                    notifyDTO.setId(updatedId);
                    return notifyDTO;
                }
            }
        }
        return null; // Nếu không có bản ghi nào được ảnh hưởng hoặc không tìm thấy id
    } catch(SQLException e) {
        e.printStackTrace();
        return null;
    }
}
    public void updateTimeStamp(int id, String timestamp) {
            String updateSql = "UPDATE notify SET timestamp = ? WHERE id = ?";
            try (PreparedStatement updatePst = conn.prepareStatement(updateSql)) {
                updatePst.setString(1, timestamp);
                updatePst.setInt(2, id);
                updatePst.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            } 
    }

    public Notify getNotifyByUser(int email_id, String recipient){
     String selectSql = "SELECT * FROM Notify WHERE email_id = ? and recipient =?";
        try (PreparedStatement selectPst = conn.prepareStatement(selectSql)) {
            selectPst.setInt(1, email_id);
            selectPst.setString(2, recipient);
            ResultSet resultSet = selectPst.executeQuery();
            if (resultSet.next()) {
                // Assuming you have an Email class with appropriate attributes
                Notify notify = new Notify();
                notify.setId(resultSet.getInt("id"));
                notify.setId(resultSet.getInt("email_id"));
                UserDAO userDao = new UserDAO(conn);
                User userFound = userDao.findUserByEmail(resultSet.getString("recipient"));
                notify.setRecipient(userFound);
                return notify;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
         return null; 
    }
}
