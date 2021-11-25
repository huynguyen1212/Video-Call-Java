/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import control.ClientCtr;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel; 
import model.GroupMember;
import model.ObjectWrapper;
import model.User;

/**
 *
 * @author Little Coconut
 */
public class ListRequestJoinGroup extends JFrame implements ActionListener {

    private ArrayList<GroupMember> listMembers;
    private ArrayList<Integer> listUserActive;
    private JTable tblResult;
    private ClientCtr mySocket;

    public ListRequestJoinGroup(ClientCtr socket) {
        super("Video call app");
        mySocket = socket;
        listMembers = new ArrayList<GroupMember>();
        listUserActive = mySocket.getListUserId();

        JPanel pnMain = new JPanel();
        pnMain.setSize(this.getSize().width - 5, this.getSize().height - 20);
        pnMain.setLayout(new BoxLayout(pnMain, BoxLayout.Y_AXIS));
        pnMain.add(Box.createRigidArea(new Dimension(0, 10)));

        JLabel title = new JLabel("LIST REQUESTS");
        title.setFont(new java.awt.Font("Dialog", 1, 20));
        title.setBounds(new Rectangle(270, 250, 200, 30));
        pnMain.add(title, null);

        JPanel pn2 = new JPanel();
//        pn2.setLayout(new BoxLayout(pn2, BoxLayout.Y_AXIS));
        tblResult = new JTable();
        JScrollPane scrollPane = new JScrollPane(tblResult);
        tblResult.setFillsViewportHeight(false);
        scrollPane.setPreferredSize(new Dimension(scrollPane.getPreferredSize().width, 250));

        tblResult.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int column = tblResult.getColumnModel().getColumnIndexAtX(e.getX()); // get the coloum of the button
                int row = e.getY() / tblResult.getRowHeight(); // get the row of the button

                // *Checking the row or column is valid or not
                if (row < tblResult.getRowCount() && row >= 0 && column < tblResult.getColumnCount() && column >= 0) {
                    //search and delete all existing previous view
                    ObjectWrapper existed = null;
                    for (ObjectWrapper func : mySocket.getActiveFunction()) {
                        if (func.getData() instanceof ListFriendsDetailFrm) {
                            ((ListFriendsDetailFrm) func.getData()).dispose();
                            existed = func;
                        }
                    }
                    if (existed != null) {
                        mySocket.getActiveFunction().remove(existed);
                    }

                    //create new instance
                    (new RequestJoinGroupDetail(mySocket, listMembers.get(row))).setVisible(true);
                    dispose();
                }
            }
        });

        pn2.add(scrollPane);
        pnMain.add(pn2);
        this.add(pnMain);
        this.setSize(600, 300);
        this.setLocation(200, 10);
        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mySocket.getActiveFunction().add(new ObjectWrapper(ObjectWrapper.REPLY_LIST_REQUEST_JOIN_GROUP, this));
        
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                listUserActive = mySocket.getListUserId();
                String[] columnNames = {"Name"};
                String[][] value = new String[listMembers.size()][columnNames.length];
                for (int i = 0; i < listMembers.size(); i++) {
                    value[i][0] = listMembers.get(i).getId()+"";
                }
                DefaultTableModel tableModel = new DefaultTableModel(value, columnNames) {
                    @Override
                    public boolean isCellEditable(int row, int column) {
                        //unable to edit cells
                        return false;
                    }
                };
                tblResult.setModel(tableModel);
            }
        }, 0, 500);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton btnClicked = (JButton) e.getSource();
    }

    public void receivedDataProcessing(ObjectWrapper data) {
        if (data.getData() instanceof ArrayList<?>) {
            listMembers = (ArrayList<GroupMember>) data.getData(); 
            String[] columnNames = {"Name", "Username"};
            String[][] value = new String[listMembers.size()][columnNames.length];
            for (int i = 0; i < listMembers.size(); i++) {
                    value[i][0] = listMembers.get(i).getId()+"";
                }
            DefaultTableModel tableModel = new DefaultTableModel(value, columnNames) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    //unable to edit cells
                    return false;
                }
            };
            tblResult.setModel(tableModel);
        } else {
            JOptionPane.showMessageDialog(this, "Error when receiving!");
        }
    }
}
