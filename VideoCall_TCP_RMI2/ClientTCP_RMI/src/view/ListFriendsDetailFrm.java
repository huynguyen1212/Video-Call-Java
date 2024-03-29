package view;

import control.ClientCtr;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import model.Friend;
import model.ObjectWrapper;
import model.User;

public class ListFriendsDetailFrm extends JFrame implements ActionListener {

    private JButton btnDelete, btnCall;
    private ClientCtr mySocket;
    private User friend;
    private ArrayList<Integer> listUserActive;

    public ListFriendsDetailFrm(ClientCtr socket, User friendN) {
        super("Video call app");
        mySocket = socket;
        friend = friendN;
        JPanel content = new JPanel();
        content.setLayout(null);

        JLabel title = new JLabel("DETAIL FRIEND");
        title.setFont(new java.awt.Font("Dialog", 1, 20));
        title.setBounds(new Rectangle(250, 50, 200, 30));
        content.add(title, null);

        JLabel label1 = (new JLabel("Name:"));
        label1.setBounds(new Rectangle(190, 107, 96, 27));
        content.add(label1);
        JLabel label11 = (new JLabel(friend.getName()));
        label11.setBounds(new Rectangle(310, 106, 258, 30));
        content.add(label11);

        JLabel label2 = new JLabel("Address:");
        label2.setBounds(new Rectangle(190, 156, 96, 27));
        content.add(label2);
        JLabel label22 = new JLabel(friend.getAddress());
        label22.setBounds(new Rectangle(310, 156, 258, 30));
        content.add(label22);

        JLabel label3 = (new JLabel("Username:"));
        label3.setBounds(new Rectangle(190, 207, 96, 27));
        content.add(label3);
        JLabel label33 = (new JLabel(friend.getUsername()));
        label33.setBounds(new Rectangle(310, 206, 258, 30));
        content.add(label33);

        btnDelete = new JButton("Delete");
        btnDelete.setBounds(170, 310, 141, 31);
        content.add(btnDelete);
        btnDelete.addActionListener(this);

        btnCall = new JButton("Call");
        btnCall.setBounds(351, 310, 141, 31);
        content.add(btnCall);
        btnCall.addActionListener(this);

        this.setContentPane(content);
        this.pack();
        this.setSize(new Dimension(625, 428));
        this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mySocket.getActiveFunction().add(new ObjectWrapper(ObjectWrapper.REPLY_DELETE_FRIEND, this));
    }

    public void actionPerformed(ActionEvent e) {
        JButton btnClicked = (JButton) e.getSource();
        if (btnClicked.equals(btnDelete)) {
            mySocket.sendData(new ObjectWrapper(ObjectWrapper.DELETE_FRIEND, friend.getIdFriend()));

        } else if (btnClicked.equals(btnCall)) {
//            System.out.println(friend);
            if (mySocket.isInCall() == true) {
                JOptionPane.showMessageDialog(this, "You are in another call!");
            } else {
                //lấy id tất cả bạn bè 
                listUserActive = mySocket.getListUserId();
                
                //xem thằng đc gọi có online k
                int indexToFindActive = -1;
                
                for (int j = 0; j < listUserActive.size(); j++) {
                    if (friend.getId() == listUserActive.get(j)) {
                        indexToFindActive = j;
                        break;
                    }
                }
                
                if (indexToFindActive >= 0) {
                    //gửi bạn lên server để tạo cuộc gọi
                    
                    mySocket.sendData(new ObjectWrapper(ObjectWrapper.CREATE_PRIVATE_CALL, friend));
                    
                    //cho đang trong cuộc gọi
                    mySocket.setInCall(true);
                    
                    //cho thằng video lên
                    (new VideoFrm(mySocket)).setVisible(true);
                    
                    this.dispose();
                } else { // nếu không online thì báo k có online
                    JOptionPane.showMessageDialog(this, "Friend is not online!");
                }
            }
        }
    }

    public void receivedDataProcessing(ObjectWrapper data) {
        if (data.getPerformative() == ObjectWrapper.REPLY_CREATE_PRIVATE_CALL) {

        } else if (data.getData().equals("false")) {
            JOptionPane.showMessageDialog(this, "Error!");
        } else {
            JOptionPane.showMessageDialog(this, "Successful!");
            this.dispose();
        }
    }
}
