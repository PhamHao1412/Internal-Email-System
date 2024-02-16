package macmail.Client;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */


import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import macmail.Entity.User;
import macmail.Entity.Reply;
import macmail.Entity.Email;
import macmail.DAO.ConnectDB;
import macmail.DAO.ReplyDAO;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import macmail.DAO.RecipientDAO;
import macmail.Entity.Recipients;

/**
 *
 * @author asus
 */
public class frmEmailDetail extends javax.swing.JFrame {
    private static Email email;
    private static User loggedInUser;
    private Connection conn;
             ConnectDB cnn = new ConnectDB();


  public frmEmailDetail(Email email ,User loggedInUser) {
        initComponents();
         conn = cnn.getConnect();
        panelReply.setVisible(false);
        this.email = email;
        this.loggedInUser = loggedInUser;
//        jPanel1.setVisible(false);
        loadForm(conn);
    }

  private void loadForm(Connection conn) {
    ReplyDAO replyDao = new ReplyDAO(conn);
    List<Reply> listReply = replyDao.getReplyByEmail(email.getId(), loggedInUser.getEmail());
    JPanel panelReplies = new JPanel();
    panelReplies.setLayout(new BoxLayout(panelReplies, BoxLayout.Y_AXIS));
    txtPTitle.setText(email.getSubject());
    Set<String> uniqueTimeStamps = new HashSet<>();

    for (Reply reply : listReply) {
        String timeStamp = reply.getTimestamp();
        if (!uniqueTimeStamps.contains(timeStamp)) {
           JPanel replyPanel = createReplyPanel(reply);
            panelReplies.add(replyPanel);
            uniqueTimeStamps.add(timeStamp);
        } 
    }
//    panelReplyAll.setVisible(false);
    jScrollPane5.setViewportView(panelReplies);
}
private JPanel createReplyPanel(Reply reply) {
    boolean enableCcRecip =false;
    boolean enableBccRecip = false;
     RecipientDAO recipientDao = new RecipientDAO(conn);
    JPanel replyPanel = new JPanel();
    replyPanel.setBorder(BorderFactory.createEtchedBorder());
    replyPanel.setLayout(new GridLayout(6, 1));
    JLabel lblSenderValue = new JLabel(reply.getSender().getFullname() + " <" + reply.getSender().getEmail() + ">");
    JLabel lblTimeStamp = new JLabel(reply.getTimestamp());
     JLabel lblRecipientValue = new JLabel();
     JLabel lblRecpientCc = new JLabel();
     JLabel lblRecpientBcc = new JLabel();
     List<Recipients> recipients =   recipientDao.getRecipientsByReply(reply.getId());
     String bccEmail =  "";
     StringBuilder recipientStringBuilder = new StringBuilder();
     StringBuilder ccRecipientStringBuilder = new StringBuilder();
     StringBuilder bccRecipientStringBuilder = new StringBuilder();
     StringBuilder listBcc = new StringBuilder();

     for(Recipients user : recipients)
     {  
        if(loggedInUser.getEmail().equals(user.getRecipient().getEmail())){
               if(user.getRecipient_type().equals("cc")){
                    enableCcRecip = true;
                    ccRecipientStringBuilder.append("me");
               }else if(user.getRecipient_type().equals("bcc")){
                   bccRecipientStringBuilder.append("me");
                   listBcc.append(user.getRecipient().getEmail());
               }
               else{
                   recipientStringBuilder.append("me");
                } 
      }else{
              if(user.getRecipient_type().equals("cc")){
                   enableCcRecip = true;
                  ccRecipientStringBuilder.append(user.getRecipient().getEmail());
               }else if(user.getRecipient_type().equals("bcc")){
                   if(listBcc.length() >0)
                       listBcc.append(", ");
                   listBcc.append(user.getRecipient().getEmail());
               }
              else{
                   recipientStringBuilder.append(user.getRecipient().getEmail());
                }
        }
        lblRecpientCc.setText("CC: "+ccRecipientStringBuilder.toString());
        lblRecipientValue.setText("To "+recipientStringBuilder.toString());
       lblRecpientBcc.setText("Bcc: "+bccRecipientStringBuilder.toString());

    }
    JTextArea txtContentValue = new JTextArea(reply.getContent());
    txtContentValue.setEditable(false); // Make the content uneditable
    String bccEmails = listBcc.toString();
    System.out.println("bccEmails: "+email.getSender().getEmail());
    // Set left alignment for JLabels
    lblSenderValue.setHorizontalAlignment(SwingConstants.LEFT);
    lblRecipientValue.setHorizontalAlignment(SwingConstants.LEFT);
    // Add components to replyPanel
    replyPanel.add(lblSenderValue);
    replyPanel.add(lblTimeStamp);
    replyPanel.add(lblRecipientValue);
    if(enableCcRecip == true){
    replyPanel.add(lblRecpientCc);
    }
    if(loggedInUser.getEmail().equals(email.getSender().getEmail())){
        lblRecpientBcc.setText("Bcc: "+listBcc.toString());
        replyPanel.add(lblRecpientBcc);
    }
    if(bccEmails.contains(loggedInUser.getEmail()) ){
        replyPanel.add(lblRecpientBcc);
    }
    replyPanel.add(txtContentValue);
    return replyPanel;
} 
    private static void handleReplyEMail(){
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton4 = new javax.swing.JButton();
        btnReply = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        btnBack = new javax.swing.JButton();
        panelReply = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtReply = new javax.swing.JTextArea();
        jButton1 = new javax.swing.JButton();
        btnSend = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        txtPReplyAll = new javax.swing.JTextPane();
        jScrollPane5 = new javax.swing.JScrollPane();
        btnReplyAll = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtPTitle = new javax.swing.JTextPane();

        jButton4.setText("jButton4");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        btnReply.setText("Reply");
        btnReply.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReplyActionPerformed(evt);
            }
        });

        jButton3.setText("Delete");

        btnBack.setText("Back");
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });

        txtReply.setColumns(20);
        txtReply.setRows(5);
        jScrollPane3.setViewportView(txtReply);

        jButton1.setText("X");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        btnSend.setBackground(new java.awt.Color(51, 102, 255));
        btnSend.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        btnSend.setForeground(new java.awt.Color(255, 255, 255));
        btnSend.setText("Send");
        btnSend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSendActionPerformed(evt);
            }
        });

        jScrollPane4.setViewportView(txtPReplyAll);

        javax.swing.GroupLayout panelReplyLayout = new javax.swing.GroupLayout(panelReply);
        panelReply.setLayout(panelReplyLayout);
        panelReplyLayout.setHorizontalGroup(
            panelReplyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelReplyLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(panelReplyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnSend, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 371, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panelReplyLayout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 363, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(15, Short.MAX_VALUE))
        );
        panelReplyLayout.setVerticalGroup(
            panelReplyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelReplyLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 23, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panelReplyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panelReplyLayout.createSequentialGroup()
                        .addComponent(jButton1)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(panelReplyLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSend, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        btnReplyAll.setText("Reply all");
        btnReplyAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReplyAllActionPerformed(evt);
            }
        });

        txtPTitle.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        txtPTitle.setEnabled(false);
        jScrollPane1.setViewportView(txtPTitle);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(btnBack)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton3)
                .addGap(19, 19, 19))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(panelReply, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 342, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnReply)
                                .addGap(18, 18, 18)
                                .addComponent(btnReplyAll)))))
                .addContainerGap(193, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 301, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnReply)
                    .addComponent(btnReplyAll))
                .addGap(18, 18, 18)
                .addComponent(panelReply, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton3)
                    .addComponent(btnBack))
                .addGap(10, 10, 10))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        // TODO add your handling code here:
        FrmMain main = new FrmMain();
        main.setUser(loggedInUser);
        main.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnBackActionPerformed
    private void btnReplyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReplyActionPerformed
      txtPReplyAll.setText("");
        
        if(!loggedInUser.getEmail().equals(email.getSender().getEmail())){
                txtPReplyAll.setText(email.getSender().getFullname()+" ("+email.getSender().getEmail()+")");
        }else{
          RecipientDAO recipientDao = new RecipientDAO(conn);
           List<Recipients> recipients =  recipientDao.getRecipientsByEmail(email.getId());
            StringBuilder recipientStringBuilder = new StringBuilder();
            Set<String> uniqueRecipient = new HashSet<>();
         for(Recipients user : recipients){
              String recip = user.getRecipient().getEmail();
            if(!uniqueRecipient.contains(recip)&& !recip.equals(email.getSender().getEmail())){
                if(recipientStringBuilder.length() >0){
                    recipientStringBuilder.append(" ,");
                }
                   recipientStringBuilder.append(user.getRecipient().getFullname()+ " ("+user.getRecipient().getEmail()+")");
                uniqueRecipient.add(recip);
             }
         }
        txtPReplyAll.setText(recipientStringBuilder.toString());
        }
        panelReply.setVisible(true);
    }//GEN-LAST:event_btnReplyActionPerformed
    
    private void ExitReply(){
        txtPReplyAll.setText("");
//        panelReplyAll.setVisible(false);
        panelReply.setVisible(false);
    }
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
    ExitReply();

    }//GEN-LAST:event_jButton1ActionPerformed

    private void btnSendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSendActionPerformed
        // TODO add your handling code here]
         try {
           String serverAddress = "localhost";
           int serverPort = 1234;
           ConnectDB cnn = new ConnectDB();
         conn = cnn.getConnect();
           try (Socket socket = new Socket(serverAddress, serverPort);
                DataInputStream dis = new DataInputStream(socket.getInputStream());
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream())) {
                int email_id  = email.getId();
               String content = txtReply.getText();
               String sender = loggedInUser.getEmail();
                String recipient = txtPReplyAll.getText();
                dos.writeUTF("REPLY_MAIL");
                dos.writeUTF(content);
                dos.writeUTF(sender);
                dos.writeUTF(recipient);
                dos.writeInt(email_id);   
                String response  = dis.readUTF();
                if("Mail sent successfully".equals(response)){
                    JOptionPane.showMessageDialog(this, "Send successfully");
                     ExitReply();

                }else{
                    JOptionPane.showMessageDialog(this, "Send failed");
                    ExitReply();
                }
              loadForm(conn);
         }
       } catch (IOException ex) {
           JOptionPane.showMessageDialog(this, "Lỗi khi giao tiếp với máy chủ: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
           ex.printStackTrace();
       }

    }//GEN-LAST:event_btnSendActionPerformed

    private void btnReplyAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReplyAllActionPerformed
        // TODO add your handling code here:
         txtPReplyAll.setText("");
        RecipientDAO recipientDao = new RecipientDAO(conn);
        List<Recipients> recipients =   recipientDao.getRecipientsByEmail(email.getId());
        StringBuilder recipientStringBuilder = new StringBuilder();
     if(!loggedInUser.getEmail().equals(email.getSender().getEmail())){
         recipientStringBuilder.append(email.getSender().getFullname()+ " ("+email.getSender().getEmail()+")");
          Set<String> uniqueRecipient = new HashSet<>();
          uniqueRecipient.add(email.getSender().getEmail());
          for(Recipients user : recipients){
             String recip = user.getRecipient().getEmail();
             if(!uniqueRecipient.contains(recip) && !user.getRecipient().getEmail().equals(loggedInUser.getEmail())){
                 if(recipientStringBuilder.length()>0){
                 recipientStringBuilder.append(", ");
                 }
                 recipientStringBuilder.append(user.getRecipient().getFullname()+ " ("+user.getRecipient().getEmail()+")");
                 uniqueRecipient.add(recip);
             }
        }
     }else{
        Set<String> uniqueRecipient = new HashSet<>();
        for(Recipients user : recipients){
             String recip = user.getRecipient().getEmail();
            if(!uniqueRecipient.contains(recip)&& !recip.equals(email.getSender().getEmail())){
                if(recipientStringBuilder.length()>0){
                     recipientStringBuilder.append(", ");
                 }
                 recipientStringBuilder.append(user.getRecipient().getFullname()+ " ("+user.getRecipient().getEmail()+")");
                 uniqueRecipient.add(recip);
             }
         }
     }
        txtPReplyAll.setText(recipientStringBuilder.toString());
//        panelReplyAll.setVisible(true);
        panelReply.setVisible(true);
    }//GEN-LAST:event_btnReplyAllActionPerformed

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
            java.util.logging.Logger.getLogger(frmEmailDetail.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(frmEmailDetail.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(frmEmailDetail.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(frmEmailDetail.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
       EventQueue.invokeLater(() -> {
            try {
                frmEmailDetail frame = new frmEmailDetail(email, loggedInUser);
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JButton btnReply;
    private javax.swing.JButton btnReplyAll;
    private javax.swing.JButton btnSend;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JPanel panelReply;
    private javax.swing.JTextPane txtPReplyAll;
    private javax.swing.JTextPane txtPTitle;
    private javax.swing.JTextArea txtReply;
    // End of variables declaration//GEN-END:variables
}
