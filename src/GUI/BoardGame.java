/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import java.awt.Font;
import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.JButton;
import javax.swing.text.DefaultCaret;

/**
 *
 * @author Fujitsu
 */
public class BoardGame extends MyFrame {

    /**
     * Creates new form BoardGame
     */
    public BoardGame() {
        initComponents();
    }
    
    public int virtualId;
    public Color[] colors = {Color.BLACK, Color.BLUE, Color.CYAN, Color.DARK_GRAY, Color.GREEN, Color.ORANGE, Color.PINK, Color.RED, Color.YELLOW};
    
    
    public void printRoomList(String[] args)
    {
        String ret = "";
        for(int i = 0; i < args.length; i++)
        {
            ret += args[i];
        }
        ListOfPlayerText.setText(ret);
    }
    
    public void printLog(String[] args)
    {
        String ret = "";
        for(int i = 0; i < args.length; i++)
        {
            ret += args[i];
        }
        ChatBoxText.append(ret);
    }
    
    public void drawCoordinate(int x, int y, int virtualId)
    {
        int index = ((x * 20) + y);
        //buttons[index].setText(playerId + "");
        buttons[index].setBackground(colors[virtualId]);
    }
    
    public void joinRoom(int roomId)
    {
        RoomIDLabel.setText(roomId + "");
    }
    
    public void sendPlayerData(String name, int virtualId)
    {
        this.virtualId = virtualId;
        PlayerIDLabel.setText((virtualId+1) + "");
        
        NameLabel.setText(name);
        NameLabel.setForeground(colors[virtualId]);
    }
    
    public static MyButton buttons[] = new MyButton[400];

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        BoardPanel = new javax.swing.JPanel();
        InfoGamePanel = new javax.swing.JPanel();
        ListOfPlayerPane = new javax.swing.JScrollPane();
        ListOfPlayerText = new javax.swing.JTextArea();
        RoomIDLabel = new javax.swing.JLabel();
        ChatBoxPane = new javax.swing.JScrollPane();
        ChatBoxText = new javax.swing.JTextArea();
        ChatInputField = new javax.swing.JTextField();
        ChatBoxLabel = new javax.swing.JLabel();
        ChatSendButton = new javax.swing.JButton();
        PlayerIDLabel = new javax.swing.JLabel();
        NameLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Gomuku - Play");
        setResizable(false);

        BoardPanel.setPreferredSize(new java.awt.Dimension(900, 700));
        BoardPanel.setLayout(new java.awt.GridLayout(20, 20));

        ListOfPlayerText.setEditable(false);
        ListOfPlayerText.setColumns(20);
        ListOfPlayerText.setLineWrap(true);
        ListOfPlayerText.setRows(5);
        ListOfPlayerPane.setViewportView(ListOfPlayerText);

        RoomIDLabel.setText("Room ID");

        ChatBoxText.setEditable(false);
        ChatBoxText.setColumns(20);
        ChatBoxText.setLineWrap(true);
        ChatBoxText.setRows(24);
        ChatBoxText.setWrapStyleWord(true);
        ChatBoxText.setDragEnabled(true);
        ChatBoxPane.setViewportView(ChatBoxText);
        DefaultCaret caret = (DefaultCaret)ChatBoxText.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        ChatInputField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ChatInputFieldActionPerformed(evt);
            }
        });
        ChatInputField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                EnterPressed(evt);
            }
        });

        ChatBoxLabel.setText("Chat Box");

        ChatSendButton.setText("Send");
        ChatSendButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ChatSendButtonActionPerformed(evt);
            }
        });

        PlayerIDLabel.setText("PID");

        NameLabel.setText("jLabel2");

        javax.swing.GroupLayout InfoGamePanelLayout = new javax.swing.GroupLayout(InfoGamePanel);
        InfoGamePanel.setLayout(InfoGamePanelLayout);
        InfoGamePanelLayout.setHorizontalGroup(
            InfoGamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(InfoGamePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(InfoGamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(InfoGamePanelLayout.createSequentialGroup()
                        .addComponent(PlayerIDLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(NameLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(RoomIDLabel))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, InfoGamePanelLayout.createSequentialGroup()
                        .addComponent(ChatInputField, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ChatSendButton))
                    .addComponent(ChatBoxPane, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ListOfPlayerPane, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ChatBoxLabel, javax.swing.GroupLayout.Alignment.LEADING))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        InfoGamePanelLayout.setVerticalGroup(
            InfoGamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(InfoGamePanelLayout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addGroup(InfoGamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(RoomIDLabel)
                    .addComponent(PlayerIDLabel)
                    .addComponent(NameLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ListOfPlayerPane, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(ChatBoxLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ChatBoxPane, javax.swing.GroupLayout.PREFERRED_SIZE, 451, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(InfoGamePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ChatInputField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ChatSendButton))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(BoardPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 700, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(InfoGamePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(BoardPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 691, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addComponent(InfoGamePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        for (int i = 0; i < 400; i++)
        {
            buttons[i] = new MyButton();
            buttons[i].absis = (i / 20);
            buttons[i].ordinat = (i % 20);
            BoardPanel.add(buttons[i]);
        }

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void ChatSendButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChatSendButtonActionPerformed
        // TODO add your handling code here:
        adaCommand = true;
        type = "message";
        paramString = ChatInputField.getText();
        ChatInputField.setText(" ");
    }//GEN-LAST:event_ChatSendButtonActionPerformed

    private void ChatInputFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ChatInputFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ChatInputFieldActionPerformed

    private void EnterPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_EnterPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            adaCommand = true;
            type = "message";
            paramString = ChatInputField.getText();
            ChatInputField.setText(" ");
        }
    }//GEN-LAST:event_EnterPressed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new BoardGame().setVisible(true);
            }
        });
    }

    public class MyButton extends JButton implements ActionListener
    {
        int playagain = 1000;
        boolean win = false;
        boolean end = false;
        String letter;
        public int absis;
        public int ordinat;
        public MyButton()
        {
            super();
            letter = " ";
            setFont(new Font("Dialog", 1, 10));
            setText(letter);
            addActionListener(this);
        }

        public void actionPerformed(ActionEvent e)
        {
            adaCommand = true;
            type = "coordinate";
            paramInt[0] = absis;
            paramInt[1] = ordinat;
            
            //System.out.println("absis = " + absis + " ordinat = " + ordinat);
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel BoardPanel;
    private javax.swing.JLabel ChatBoxLabel;
    private javax.swing.JScrollPane ChatBoxPane;
    private javax.swing.JTextArea ChatBoxText;
    private javax.swing.JTextField ChatInputField;
    private javax.swing.JButton ChatSendButton;
    private javax.swing.JPanel InfoGamePanel;
    private javax.swing.JScrollPane ListOfPlayerPane;
    private javax.swing.JTextArea ListOfPlayerText;
    private javax.swing.JLabel NameLabel;
    private javax.swing.JLabel PlayerIDLabel;
    private javax.swing.JLabel RoomIDLabel;
    // End of variables declaration//GEN-END:variables
}
