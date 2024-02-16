package macmail.Client;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */


import macmail.Entity.User;
import macmail.Entity.Email;
import macmail.DAO.ConnectDB;
import macmail.DAO.EmailDAO;
import macmail.DAO.UserDAO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import macmail.DAO.NotifyDAO;
import macmail.DAO.ReplyDAO;
import macmail.Entity.Reply;

/**
 *
 * @author asus
 */
public class FrmMain extends javax.swing.JFrame {
    private User loggedInUser;
    private  Connection conn;
    private  List<Integer> listEmail = new ArrayList<>(); 

    /**
     * Creates new form FrmMain
     */
    public FrmMain() {
        initComponents();
        tbEmail.addMouseListener(new java.awt.event.MouseAdapter() {
        @Override
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            int row = tbEmail.rowAtPoint(evt.getPoint());
            int col = tbEmail.columnAtPoint(evt.getPoint());
            if (row >= 0 && col >= 0) {
            DefaultTableModel model = (DefaultTableModel) tbEmail.getModel();
            int email_id = (int) model.getValueAt(row, model.findColumn("ID"));
            EmailDAO emailDao = new EmailDAO(conn);
            Email emailFound =  emailDao.findEmailById(email_id);
            if(col !=1 ){
             if(emailFound != null){
                emailDao.updateIsRead(emailFound.getId(),loggedInUser.getEmail());
             }
             frmEmailDetail emailDetail = new frmEmailDetail(emailFound,loggedInUser);
             emailDetail.setVisible(true);
             dispose();
            }if(col == 1){
                Boolean selected = (Boolean) model.getValueAt(row, 1);  
               if(selected == true ){
                  listEmail.add(email_id);
                }
                else{
                listEmail.remove(Integer.valueOf(email_id));
             }
            }
          }
        }
    });
    }

    public void setUser(User user) {
        this.loggedInUser = user;
        // Gọi phương thức LoadMain() hoặc thực hiện các thao tác khác ở đây
        LoadMain();
    }
    public void LoadMain(){
        if (loggedInUser != null) {
                jLabel2.setText( loggedInUser.getFullname());
        }
    }
    private void hiddenColumnID(JTable table) {
    table.getColumnModel().getColumn(6).setMinWidth(0);
    table.getColumnModel().getColumn(6).setMaxWidth(0);
    table.getColumnModel().getColumn(6).setWidth(0);
}
    
   public DefaultTableModel inboxMailList() {
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 1 ? Boolean.class : super.getColumnClass(columnIndex);
            }
        };
        model.addColumn("STT");
        model.addColumn("Select");
        model.addColumn("Sender");
        model.addColumn("Subject");
        model.addColumn("Content");
        model.addColumn("Timestamp");
        model.addColumn("ID"); 

       ConnectDB cnn = new ConnectDB();
        try {
             conn = cnn.getConnect();
             ReplyDAO replyDao = new ReplyDAO(conn);
              NotifyDAO notifyDAO = new NotifyDAO(conn);
            if (conn == null) {
                JOptionPane.showMessageDialog(this, "Không thể kết nối đến cơ sở dữ liệu");
                return null;
            } else {
                String sql = "select n.id, n.email_id,e.subject,n.timestamp from notify n, email e"
                   + " where n.isRead =0 and n.recipient=? and n.isSpam = 0 and n.isBin =0 and e.id=n.email_id ORDER BY n.timestamp DESC";
                try (PreparedStatement pst = conn.prepareStatement(sql)) {
                    pst.setString(1, loggedInUser.getEmail());
                    try (ResultSet rs = pst.executeQuery()) {
                        int count = 1;
                        while (rs.next()) {
                         int id = rs.getInt("id");
                            System.out.println("id "+id);
                        Reply replyNoti = replyDao.getReplyByNoti(id);
                        String sender = replyNoti.getSender().getEmail();
                        String subject = rs.getString("subject");
                        String content = replyNoti.getContent();
                        notifyDAO.updateTimeStamp(id, replyNoti.getTimestamp());
                        String timestamp = rs.getString("timestamp");
                        int email_id = rs.getInt("email_id");
                        model.addRow(new Object[]{count, Boolean.FALSE,sender, subject, content, timestamp, email_id});
                        model.setValueAt(email_id, count - 1, model.findColumn("ID"));
                        count++;
                        } 
                    }
                }
            }
         
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.toString());
        }  
         btnMarkRead.show();
        btnMarkUnread.hide();
        return model;
    }

   public DefaultTableModel readMailList() {
          DefaultTableModel model = new DefaultTableModel() {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 1 ? Boolean.class : super.getColumnClass(columnIndex);
            }
        };
        
        model.addColumn("STT");
         model.addColumn("Select");
        model.addColumn("Sender");
        model.addColumn("Subject");
        model.addColumn("Content");
        model.addColumn("Timestamp");
        model.addColumn("ID"); 

       ConnectDB cnn = new ConnectDB();
        try {
             conn = cnn.getConnect();
             ReplyDAO replyDao = new ReplyDAO(conn);
            if (conn == null) {
                JOptionPane.showMessageDialog(this, "Không thể kết nối đến cơ sở dữ liệu", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return null;
            } else {
                String sql = "select n.id, n.email_id,e.subject from notify n, email e where n.isRead =1 and n.recipient=? and n.isSpam = 0 and n.isBin =0 and e.id=n.email_id";
                try (PreparedStatement pst = conn.prepareStatement(sql)) {
                    pst.setString(1, loggedInUser.getEmail());
                    try (ResultSet rs = pst.executeQuery()) {
                        int count = 1;
                        while (rs.next()) {
                         int id = rs.getInt("id");
                            System.out.println("id "+id);
                        Reply replyNoti = replyDao.getReplyByNoti(id);
                        String sender = replyNoti.getSender().getEmail();
                        String subject = rs.getString("subject");
                        String content = replyNoti.getContent();
                        String timestamp = replyNoti.getTimestamp();
                        int email_id = rs.getInt("email_id");
                        model.addRow(new Object[]{count, Boolean.FALSE,sender, subject, content, timestamp,email_id});
                        model.setValueAt(email_id, count - 1, model.findColumn("ID"));
                        count++;
                        } 
                    }
                }
            }
         
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.toString());
        }  
         btnMarkRead.hide();
        btnMarkUnread.show();
        return model;
    }
   public DefaultTableModel spamMailList() {
       DefaultTableModel model = new DefaultTableModel() {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 1 ? Boolean.class : super.getColumnClass(columnIndex);
            }
        };
        
        model.addColumn("STT");
         model.addColumn("Select");
        model.addColumn("Sender");
        model.addColumn("Subject");
        model.addColumn("Content");
        model.addColumn("Timestamp");
        model.addColumn("ID"); 

       ConnectDB cnn = new ConnectDB();
        try {
             conn = cnn.getConnect();
             ReplyDAO replyDao = new ReplyDAO(conn);
            if (conn == null) {
                JOptionPane.showMessageDialog(this, "Không thể kết nối đến cơ sở dữ liệu", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return null;
            } else {
                String sql = "select n.id, n.email_id,e.subject from notify n, email e where n.recipient=? and n.isSpam = 1 and isBin =0 and e.id=n.email_id";
                try (PreparedStatement pst = conn.prepareStatement(sql)) {
                    pst.setString(1, loggedInUser.getEmail());
                    try (ResultSet rs = pst.executeQuery()) {
                        int count = 1;
                        while (rs.next()) {
                         int id = rs.getInt("id");
                            System.out.println("id "+id);
                        Reply replyNoti = replyDao.getReplyByNoti(id);
                        String sender = replyNoti.getSender().getEmail();
                        String subject = rs.getString("subject");
                        String content = replyNoti.getContent();
                        String timestamp = replyNoti.getTimestamp();
                        int email_id = rs.getInt("email_id");
                        model.addRow(new Object[]{count, Boolean.FALSE,sender, subject, content, timestamp,email_id});
                        model.setValueAt(email_id, count - 1, model.findColumn("ID"));
                        count++;
                        } 
                    }
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.toString());
        }  
         btnMarkRead.show();
        btnMarkUnread.hide();
        return model;
    }
    public DefaultTableModel sentMailList() {
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 1 ? Boolean.class : super.getColumnClass(columnIndex);
            }
        };
        
        
        model.addColumn("STT");
         model.addColumn("Select");
        model.addColumn("Sender");
        model.addColumn("Subject");
        model.addColumn("Content");
        model.addColumn("Timestamp");
        model.addColumn("ID"); 

       ConnectDB cnn = new ConnectDB();
        try {
             conn = cnn.getConnect();
             ReplyDAO replyDAO = new ReplyDAO(conn);
            if (conn == null) {
                JOptionPane.showMessageDialog(this, "Không thể kết nối đến cơ sở dữ liệu", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return null;
            } else {
                String sql = "SELECT * FROM Email WHERE sender = ?";
                try (PreparedStatement pst = conn.prepareStatement(sql)) {
                    pst.setString(1, loggedInUser.getEmail());
                    try (ResultSet rs = pst.executeQuery()) {
                        int count = 1;
                        while (rs.next()) {
                        int email_id = rs.getInt("id");
                        String subject = rs.getString("subject");
                        Reply reply = replyDAO.getReplyByEmailSender(email_id);
                        String recipient = reply.getRecipient().getEmail();
                        String content = reply.getContent();
                        String timestamp = reply.getTimestamp();
                        model.addRow(new Object[]{count, Boolean.FALSE,recipient, subject, content, timestamp,email_id});
                        count++;
                        } 
                    }
                }
            }
         
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.toString());
        }  
         btnMarkRead.show();
        btnMarkUnread.hide();
        return model;
    }
     public DefaultTableModel binEmailList() {
          DefaultTableModel model = new DefaultTableModel() {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 1 ? Boolean.class : super.getColumnClass(columnIndex);
            }
        };
        
        model.addColumn("STT");
         model.addColumn("Select");
        model.addColumn("Sender");
        model.addColumn("Subject");
        model.addColumn("Content");
        model.addColumn("Timestamp");
        model.addColumn("ID"); 

       ConnectDB cnn = new ConnectDB();
        try {
             conn = cnn.getConnect();
             ReplyDAO replyDao = new ReplyDAO(conn);
            if (conn == null) {
                JOptionPane.showMessageDialog(this, "Không thể kết nối đến cơ sở dữ liệu", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return null;
            } else {
                String sql = "select n.id, n.email_id,e.subject from notify n, email e where n.isBin =1 and n.recipient=? and n.isBin =1 and e.id=n.email_id";
                try (PreparedStatement pst = conn.prepareStatement(sql)) {
                    pst.setString(1, loggedInUser.getEmail());
                    try (ResultSet rs = pst.executeQuery()) {
                        int count = 1;
                        while (rs.next()) {
                         int id = rs.getInt("id");
                            System.out.println("id "+id);
                        Reply replyNoti = replyDao.getReplyByNoti(id);
                        String sender = replyNoti.getSender().getEmail();
                        String subject = rs.getString("subject");
                        String content = replyNoti.getContent();
                        String timestamp = replyNoti.getTimestamp();
                        int email_id = rs.getInt("email_id");
                        model.addRow(new Object[]{count, Boolean.FALSE,sender, subject, content, timestamp,email_id});
                        model.setValueAt(email_id, count - 1, model.findColumn("ID"));
                        count++;
                        } 
                    }
                }
            }
         
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, ex.toString());
        }  
         btnMarkRead.hide();
        btnMarkUnread.show();
        return model;
    }
    public void loadDataTable() {
        DefaultTableModel model = inboxMailList();
        tbEmail.setModel(model);
        hiddenColumnID(tbEmail);
        
      
    }
    public void handleMarkasRead(List<Integer> listMaskRead){
        EmailDAO emailDao = new EmailDAO(conn);
        int count = 0;
        if(listMaskRead.isEmpty()){
           JOptionPane.showMessageDialog(this," Please select email to mask as read.","Warning", JOptionPane.WARNING_MESSAGE);
        }
        else{
          for(int selected : listMaskRead){
             Email emailFound = emailDao.findEmailById(selected);
             if(emailFound != null){
                emailDao.updateIsRead(emailFound.getId(),loggedInUser.getEmail());
                count ++;
             }
         }
        listMaskRead.clear();
        JOptionPane.showMessageDialog(this, count +" conversations masked as read.");
        loadDataTable();
        }
    }
     public void handleMarkasUnRead(List<Integer> listMaskRead){
        EmailDAO emailDao = new EmailDAO(conn);
        int count = 0;
        if(listMaskRead.isEmpty()){
           JOptionPane.showMessageDialog(this," Please select email to mask as unread.","Warning", JOptionPane.WARNING_MESSAGE);
        }
        else{
          for(int selected : listMaskRead){
             Email emailFound = emailDao.findEmailById(selected);
             if(emailFound != null){
                emailDao.updateIsUnRead(emailFound.getId(),loggedInUser.getEmail());
                count ++;
             }
         }
        listMaskRead.clear();
        JOptionPane.showMessageDialog(this, count +" conversations masked as unread.","Success", JOptionPane.INFORMATION_MESSAGE);
        loadDataTable();
        }
    }
    public void handleReportSpam(List<Integer> listSpam){
        EmailDAO emailDao = new EmailDAO(conn);
        int count = 0;
        if(listSpam.isEmpty()){
           JOptionPane.showMessageDialog(this," Please select email to mask as spam.","Warning", JOptionPane.WARNING_MESSAGE);
        }
        else{
        int result = JOptionPane.showConfirmDialog(this, "Report spam & unsubscribe?", "Report spam", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                for(int selected : listSpam){
             Email emailFound = emailDao.findEmailById(selected);
             if(emailFound != null){
                emailDao.reportSpamEmail(emailFound.getId(),loggedInUser.getEmail());
                count ++;
                }
            }
            listSpam.clear();
            JOptionPane.showMessageDialog(this, count +" conversations masked as spam.","Success", JOptionPane.INFORMATION_MESSAGE);
            loadDataTable();
          }
        }
    }
        public void deleteEmail(List<Integer> listMaskRead){
        EmailDAO emailDao = new EmailDAO(conn);
        int count = 0;
        if(listMaskRead.isEmpty()){
           JOptionPane.showMessageDialog(this," Please select email to remove.","Warning", JOptionPane.WARNING_MESSAGE);
        }
        else{
          for(int selected : listMaskRead){
             Email emailFound = emailDao.findEmailById(selected);
             if(emailFound != null){
                emailDao.deleteEmail(emailFound.getId(),loggedInUser.getEmail());
                count ++;
             }
         }
            listMaskRead.clear();
            JOptionPane.showMessageDialog(this, count +" conversations removed.");
            loadDataTable();
            }
        }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jFrame1 = new javax.swing.JFrame();
        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        buttonGroup3 = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbEmail = new javax.swing.JTable();
        btnInbox = new javax.swing.JButton();
        btnSent = new javax.swing.JButton();
        btnRead = new javax.swing.JButton();
        btnSpam = new javax.swing.JButton();
        btnBin = new javax.swing.JButton();
        btnLogout = new javax.swing.JButton();
        btnMarkRead = new javax.swing.JButton();
        btnMarkSpam = new javax.swing.JButton();
        btnDel = new javax.swing.JButton();
        btnMarkUnread = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();

        javax.swing.GroupLayout jFrame1Layout = new javax.swing.GroupLayout(jFrame1.getContentPane());
        jFrame1.getContentPane().setLayout(jFrame1Layout);
        jFrame1Layout.setHorizontalGroup(
            jFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        jFrame1Layout.setVerticalGroup(
            jFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setForeground(new java.awt.Color(153, 204, 255));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(102, 102, 102));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/test2/images/email_main.png"))); // NOI18N
        jLabel1.setText("Email");
        jLabel1.setPreferredSize(new java.awt.Dimension(60, 16));

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/test2/images/iconuuser.png"))); // NOI18N
        jLabel2.setText("Hello ");

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/test2/images/pencil.png"))); // NOI18N
        jButton1.setText("Compose");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        tbEmail.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(tbEmail);

        btnInbox.setText("Inbox");
        btnInbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnInboxActionPerformed(evt);
            }
        });

        btnSent.setText("Sent");
        btnSent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSentActionPerformed(evt);
            }
        });

        btnRead.setText("Read");
        btnRead.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReadActionPerformed(evt);
            }
        });

        btnSpam.setText("Spam");
        btnSpam.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSpamActionPerformed(evt);
            }
        });

        btnBin.setText("Bin");
        btnBin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBinActionPerformed(evt);
            }
        });

        btnLogout.setText("Log out");
        btnLogout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLogoutActionPerformed(evt);
            }
        });

        btnMarkRead.setText("Mask as read");
        btnMarkRead.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMarkReadActionPerformed(evt);
            }
        });

        btnMarkSpam.setText("Report spam");
        btnMarkSpam.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMarkSpamActionPerformed(evt);
            }
        });

        btnDel.setText("Delete");
        btnDel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDelActionPerformed(evt);
            }
        });

        btnMarkUnread.setText("Mask as unread");
        btnMarkUnread.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMarkUnreadActionPerformed(evt);
            }
        });

        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });

        jButton2.setFont(new java.awt.Font("Segoe UI", 1, 10)); // NOI18N
        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/test2/images/btn_Search.png"))); // NOI18N
        jButton2.setText("Search");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnInbox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnRead, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnSent, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnSpam, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnBin, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 36, Short.MAX_VALUE)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 364, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(38, 38, 38)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnLogout)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnMarkRead)
                                .addGap(9, 9, 9)
                                .addComponent(btnMarkUnread)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnMarkSpam)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnDel))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 635, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnLogout))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(btnDel)
                                    .addComponent(btnMarkSpam)
                                    .addComponent(btnMarkRead)
                                    .addComponent(btnMarkUnread)))))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(3, 3, 3)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(13, 13, 13)
                        .addComponent(btnInbox)
                        .addGap(18, 18, 18)
                        .addComponent(btnRead)
                        .addGap(17, 17, 17)
                        .addComponent(btnSent)
                        .addGap(18, 18, 18)
                        .addComponent(btnSpam)
                        .addGap(18, 18, 18)
                        .addComponent(btnBin))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(63, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        frmSendEmail main = new frmSendEmail();
        main.setUser(loggedInUser);
        main.setVisible(true);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        // TODO add your handling code here:
        loadDataTable();
        btnInbox.doClick();
    }//GEN-LAST:event_formWindowOpened

    private void btnLogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLogoutActionPerformed
        // TODO add your handling code here:
        frmLogin login = new frmLogin();
         login.setVisible(true);
         this.dispose();

    }//GEN-LAST:event_btnLogoutActionPerformed

    private void btnReadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReadActionPerformed
           DefaultTableModel model = readMailList();
           tbEmail.setModel(model);
          hiddenColumnID(tbEmail);
            btnMarkUnread .show();
            btnMarkSpam.show();

    }//GEN-LAST:event_btnReadActionPerformed
 
    private void btnInboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnInboxActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = inboxMailList();
        tbEmail.setModel(model);
            hiddenColumnID(tbEmail);
            btnMarkRead .show();
            btnMarkSpam.show();

    }//GEN-LAST:event_btnInboxActionPerformed

    private void btnMarkReadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMarkReadActionPerformed
        // TODO add your handling code here:
        handleMarkasRead(listEmail);
    }//GEN-LAST:event_btnMarkReadActionPerformed

    private void btnMarkUnreadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMarkUnreadActionPerformed
        // TODO add your handling code here:
        handleMarkasUnRead(listEmail);
    }//GEN-LAST:event_btnMarkUnreadActionPerformed

    private void btnMarkSpamActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMarkSpamActionPerformed
        // TODO add your handling code here:
        handleReportSpam(listEmail);
    }//GEN-LAST:event_btnMarkSpamActionPerformed

    private void btnSpamActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSpamActionPerformed
        // TODO add your handling code here:
              DefaultTableModel model = spamMailList();
         tbEmail.setModel(model);
          hiddenColumnID(tbEmail);
          btnMarkSpam.show();

    }//GEN-LAST:event_btnSpamActionPerformed

    private void btnSentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSentActionPerformed
        // TODO add your handling code here:
            DefaultTableModel model = sentMailList();
             tbEmail.setModel(model);
            hiddenColumnID(tbEmail);
            btnMarkSpam.show();
    }//GEN-LAST:event_btnSentActionPerformed

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void btnBinActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBinActionPerformed
        // TODO add your handling code here:
           DefaultTableModel model = binEmailList();
             tbEmail.setModel(model);
            hiddenColumnID(tbEmail);
            btnMarkRead .hide();
            btnMarkUnread .hide();
            btnMarkSpam.hide();

        
    }//GEN-LAST:event_btnBinActionPerformed

    private void btnDelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDelActionPerformed
        // TODO add your handling code here:
        deleteEmail(listEmail);
    }//GEN-LAST:event_btnDelActionPerformed
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(FrmMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FrmMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FrmMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FrmMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FrmMain().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBin;
    private javax.swing.JButton btnDel;
    private javax.swing.JButton btnInbox;
    private javax.swing.JButton btnLogout;
    private javax.swing.JButton btnMarkRead;
    private javax.swing.JButton btnMarkSpam;
    private javax.swing.JButton btnMarkUnread;
    private javax.swing.JButton btnRead;
    private javax.swing.JButton btnSent;
    private javax.swing.JButton btnSpam;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.ButtonGroup buttonGroup3;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JFrame jFrame1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTable tbEmail;
    // End of variables declaration//GEN-END:variables
}
