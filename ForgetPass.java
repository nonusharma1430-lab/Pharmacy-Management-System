import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.sql.*;

class ForgetPass extends JFrame implements ActionListener {

    JLabel luid, luser, lquestion, lanswer, lpass;
    JTextField tuid, tuser, tanswer;
    JPasswordField tpass;
    JCheckBox cbpass;
    JComboBox  cbselect;
    JButton bsubmit, bexit, bclear, bverify;

    Connection con;
    PreparedStatement pst;
    ResultSet rs;

    ForgetPass() {
        setTitle("Forget Password");
        setSize(500, 500);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(194, 217, 220));

        // User ID
        luid = new JLabel("User ID");
        luid.setBounds(90, 30, 120, 30);
        add(luid);
        tuid = new JTextField();
        tuid.setBounds(240, 30, 120, 30);
        add(tuid);

        // Username
        luser = new JLabel("Username");
        luser.setBounds(90, 70, 120, 30);
        add(luser);
        tuser = new JTextField();
        tuser.setBounds(240, 70, 120, 30);
        add(tuser);

        // Security Question
        lquestion = new JLabel("Security Question");
        lquestion.setBounds(90, 110, 140, 30);
        add(lquestion);
        cbselect = new JComboBox();
        cbselect.setBounds(240, 110, 180, 30);
        cbselect.addItem("---Select---");
        cbselect.addItem("Your Favourite Color");
        cbselect.addItem("Your Favourite Book");
        cbselect.addItem("Your Favourite Food");
        cbselect.addItem("Your Mother's Name");
        cbselect.addItem("Your Father's Name");
        add(cbselect);

        // Security Answer
        lanswer = new JLabel("Security Answer");
        lanswer.setBounds(90, 150, 120, 30);
        add(lanswer);
        tanswer = new JTextField();
        tanswer.setBounds(240, 150, 120, 30);
        add(tanswer);

        // New Password
        lpass = new JLabel("New Password");
        lpass.setBounds(90, 190, 120, 30);
        add(lpass);
        tpass = new JPasswordField();
        tpass.setBounds(240, 190, 120, 30);
        add(tpass);

        // Show Password
        cbpass = new JCheckBox("Show Password");
        cbpass.setBounds(240, 230, 150, 30);
        add(cbpass);

        // Buttons
        bverify = new JButton("Verify");
        bverify.setBounds(380, 150, 80, 30);
        add(bverify);

        bsubmit = new JButton("Submit");
        bsubmit.setBounds(120, 280, 120, 30);
        add(bsubmit);

        bclear = new JButton("Clear");
        bclear.setBounds(250, 280, 120, 30);
        add(bclear);

        bexit = new JButton("Exit");
        bexit.setBounds(180, 330, 120, 30);
        add(bexit);

        // Listeners
        bsubmit.addActionListener(this);
        bclear.addActionListener(this);
        bexit.addActionListener(this);
        bverify.addActionListener(this);

        cbpass.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED)
                tpass.setEchoChar((char) 0);
            else
                tpass.setEchoChar('*');
        });

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        String id = tuid.getText();
        String username = tuser.getText();
        String question = cbselect.getSelectedItem().toString();
        String answer = tanswer.getText();
        String newpass = new String(tpass.getPassword());

        if (ae.getSource() == bclear) {
            tuid.setText("");
            tuser.setText("");
            cbselect.setSelectedIndex(0);
            tanswer.setText("");
            tpass.setText("");
        }

        if (ae.getSource() == bexit) {
            new LoginForm();
            dispose();
        }
        

        if (ae.getSource() == bverify) {
            if (id.isEmpty() || username.isEmpty() || question.equals("---Select---") || answer.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required for verification!");
                return;
            }

            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                con = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/qems",
                        "root",
                        "VISHESHSQL123@"
                );

                String sql = "SELECT * FROM admin WHERE userid=? AND username=? AND question=? AND answer=?";
                pst = con.prepareStatement(sql);
                pst.setString(1, id);
                pst.setString(2, username);
                pst.setString(3, question);
                pst.setString(4, answer);
                rs = pst.executeQuery();

                if (rs.next()) {
                    JOptionPane.showMessageDialog(this, "Verification Successful! Enter new password.");
                } else {
                    JOptionPane.showMessageDialog(this, "Verification Failed! Check your details.");
                }

                rs.close();
                pst.close();
                con.close();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e);
            }
        }

        if (ae.getSource() == bsubmit) {
            if (id.isEmpty() || username.isEmpty() || newpass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please complete verification and enter new password!");
                return;
            }

            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                con = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/project",
                        "root",
                        "VISHESHSQL123@"
                );

                String updateSql = "UPDATE admin SET password=? WHERE userid=? AND username=?";
                pst = con.prepareStatement(updateSql);
                pst.setString(1, newpass);
                pst.setString(2, id);
                pst.setString(3, username);

                int x = pst.executeUpdate();
                if (x > 0) {
                    JOptionPane.showMessageDialog(this, "Password reset successfully!");
                     new LoginForm();  // open welcome page
                     dispose();                  // close login form
                 

                } else {
                    JOptionPane.showMessageDialog(this, "Password reset failed! Verify details again.");
                }

                pst.close();
                con.close();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e);
            }
        }
    }

    public static void main(String[] args) {
        new ForgetPass();
    }
}
