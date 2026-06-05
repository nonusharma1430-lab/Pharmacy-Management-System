import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.sql.*;

class ChangePass extends JFrame implements ActionListener {

    JLabel luid, luser, loldpass, lnewpass, lconfpass, llabel;
    JTextField tuid, tuser;
    JPasswordField toldpass, tnewpass, tconfpass;
    JCheckBox cbpass;
    JButton bsubmit, bexit, bclear;

    Connection con;
    PreparedStatement pst;
    ResultSet rs;

    ChangePass() {
        setTitle("Change Password");
        setSize(500,600);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                getContentPane().setBackground(new Color(194, 217, 220));


        llabel = new JLabel("CHANGE PASSWORD");
        llabel.setBounds(150, 10, 200, 40);
        add(llabel);

        // User ID
        luid = new JLabel("User ID");
        luid.setBounds(90, 70, 120, 30);
        add(luid);

        tuid = new JTextField();
        tuid.setBounds(240, 70, 120, 30);
        add(tuid);

        // Username
        luser = new JLabel("Username");
        luser.setBounds(90, 110, 120, 30);
        add(luser);

        tuser = new JTextField();
        tuser.setBounds(240, 110, 120, 30);
        add(tuser);

        // Old Password
        loldpass = new JLabel("Old Password");
        loldpass.setBounds(90, 150, 120, 30);
        add(loldpass);

        toldpass = new JPasswordField();
        toldpass.setBounds(240, 150, 120, 30);
        add(toldpass);

        // New Password
        lnewpass = new JLabel("New Password");
        lnewpass.setBounds(90, 190, 120, 30);
        add(lnewpass);

        tnewpass = new JPasswordField();
        tnewpass.setBounds(240, 190, 120, 30);
        add(tnewpass);

        // Confirm Password
        lconfpass = new JLabel("Confirm Password");
        lconfpass.setBounds(90, 250, 140, 30);
        add(lconfpass);

        tconfpass = new JPasswordField();
        tconfpass.setBounds(240, 250, 120, 30);
        add(tconfpass);

        // Show Password
        cbpass = new JCheckBox("Show Password");
        cbpass.setBounds(240, 290, 150, 30);
        add(cbpass);

        // Buttons
        bsubmit = new JButton("Submit");
        bsubmit.setBounds(120, 350, 120, 30);
        add(bsubmit);

        bclear = new JButton("Clear");
        bclear.setBounds(250, 350, 120, 30);
        add(bclear);

        bexit = new JButton("Exit");
        bexit.setBounds(180, 400, 120, 30);
        add(bexit);

        bsubmit.addActionListener(this);
        bclear.addActionListener(this);
        bexit.addActionListener(this);

        // Show/hide password
        cbpass.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                toldpass.setEchoChar((char)0);
                tnewpass.setEchoChar((char)0);
                tconfpass.setEchoChar((char)0);
            } else {
                toldpass.setEchoChar('*');
                tnewpass.setEchoChar('*');
                tconfpass.setEchoChar('*');
            }
        });

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if(ae.getSource() == bexit) {
            new LoginForm();
            dispose();
        }
         

        if(ae.getSource() == bclear) {
            tuid.setText("");
            tuser.setText("");
            toldpass.setText("");
            tnewpass.setText("");
            tconfpass.setText("");
        }

        if(ae.getSource() == bsubmit) {
            String id = tuid.getText();
            String username = tuser.getText();
            String oldpass = new String(toldpass.getPassword());
            String newpass = new String(tnewpass.getPassword());
            String confpass = new String(tconfpass.getPassword());

            if(id.isEmpty() || username.isEmpty() || oldpass.isEmpty() || newpass.isEmpty() || confpass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required!");
                return;
            }

            if(!newpass.equals(confpass)) {
                JOptionPane.showMessageDialog(this, "New Password and Confirm Password do not match!");
                return;
            }

            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                con = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/qems",
                        "root",
                        "VISHESHSQL123@"
                );

                // Check if User ID and Username exist and old password matches
                String checkSql = "SELECT password FROM admin WHERE userid=? AND username=?";
                pst = con.prepareStatement(checkSql);
                pst.setString(1, id);
                pst.setString(2, username);
                rs = pst.executeQuery();

                if(rs.next()) {
                    String dbPass = rs.getString("password");
                    if(!dbPass.equals(oldpass)) {
                        JOptionPane.showMessageDialog(this, "Old password is incorrect!");
                        rs.close();
                        pst.close();
                        con.close();
                        return;
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "User ID and Username not found!");
                    rs.close();
                    pst.close();
                    con.close();
                    return;
                }

                rs.close();
                pst.close();

                // Update password
                String updateSql = "UPDATE admin SET password=? WHERE userid=? AND username=?";
                pst = con.prepareStatement(updateSql);
                pst.setString(1, newpass);
                pst.setString(2, id);
                pst.setString(3, username);

                int x = pst.executeUpdate();
                if(x > 0) {
                    JOptionPane.showMessageDialog(this, "Password changed successfully!");
                   
                    new LoginForm();  // open welcome page
                    dispose();                  // close login form
                

                } else {
                    JOptionPane.showMessageDialog(this, "Password change failed!");
                }

                pst.close();
                con.close();

            } catch(Exception e) {
                JOptionPane.showMessageDialog(this, e);
            }
        }
    }

    public static void main(String args[]) {
        new ChangePass();
    }
}
