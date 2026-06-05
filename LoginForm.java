import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.awt.event.*;

class LoginForm extends JFrame implements ActionListener {

    JLabel luid, luser, lpass;
    JTextField tuid, tuser;
    JPasswordField tpass;
    JCheckBox cbshow;
    JButton blogin, bexit, bnewreg, bforgetPass, bforgetID, bchange;

    Connection con;
    PreparedStatement pst;
    ResultSet rs;

    LoginForm() {
        setTitle("Login Form");
        setSize(500, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);
        getContentPane().setBackground(new Color(194, 217, 220));

        // USER ID
        luid = new JLabel("User ID");
        luid.setBounds(80, 40, 120, 30);
        add(luid);
        tuid = new JTextField();
        tuid.setBounds(220, 40, 180, 30);
        add(tuid);

        // USER NAME
        luser = new JLabel("User Name");
        luser.setBounds(80, 90, 120, 30);
        add(luser);
        tuser = new JTextField();
        tuser.setBounds(220, 90, 180, 30);
        add(tuser);

        // PASSWORD
        lpass = new JLabel("Password");
        lpass.setBounds(80, 140, 120, 30);
        add(lpass);
        tpass = new JPasswordField();
        tpass.setBounds(220, 140, 180, 30);
        tpass.setEchoChar('*');
        add(tpass);

        // SHOW PASSWORD
        cbshow = new JCheckBox("Show password");
        cbshow.setBounds(220, 180, 180, 30);
        add(cbshow);

        // Buttons row 1
        blogin = new JButton("Login");
        blogin.setBounds(80, 230, 100, 30);
        add(blogin);

        bnewreg = new JButton("New User");
        bnewreg.setBounds(200, 230, 100, 30);
        add(bnewreg);

        bexit = new JButton("Exit");
        bexit.setBounds(320, 230, 100, 30);
        add(bexit);

        // Buttons row 2
        bchange = new JButton("Change Pass");
        bchange.setBounds(80, 280, 120, 30);
        add(bchange);

        bforgetPass = new JButton("Forget Password");
        bforgetPass.setBounds(220, 280, 150, 30);
        add(bforgetPass);

        bforgetID = new JButton("Forget User ID");
        bforgetID.setBounds(150, 330, 150, 30);
        add(bforgetID);

        // LISTENERS
        blogin.addActionListener(this);
        bexit.addActionListener(this);
        bnewreg.addActionListener(this);
        bchange.addActionListener(this);
        bforgetPass.addActionListener(this);
        bforgetID.addActionListener(this);

        cbshow.addItemListener(e -> {
            char defaultEcho = '*';
            tpass.setEchoChar(e.getStateChange() == ItemEvent.SELECTED ? (char) 0 : defaultEcho);
        });

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == bexit) {
            System.exit(0);
        } else if (ae.getSource() == bchange) {
            new ChangePass().setVisible(true);
            dispose();
        } else if (ae.getSource() == bforgetPass) {
            new ForgetPass().setVisible(true);
            dispose();
        } else if (ae.getSource() == bforgetID) {
            new ForgetUserID().setVisible(true);
            dispose();
        } else if (ae.getSource() == bnewreg) {
            new NewUser().setVisible(true);
            dispose();
        } else if (ae.getSource() == blogin) {
            String userID = tuid.getText().trim();
            String username = tuser.getText().trim();
            String password = new String(tpass.getPassword()).trim();

            if (userID.isEmpty() || username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required!");
                return;
            }

            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                con = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/project",
                        "root",
                        "VISHESHSQL123@");

                // Correct SQL using VARCHAR columns
                String sql = "SELECT password FROM admin WHERE userid=? AND username=?";
                pst = con.prepareStatement(sql);
                pst.setString(1, userID);
                pst.setString(2, username);
                rs = pst.executeQuery();

                if (rs.next()) {
                    String dbpass = rs.getString("password");
                    if (password.equals(dbpass)) {
                        JOptionPane.showMessageDialog(this, "Login Successful");
                        new WelcomePage();
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(this, "Incorrect Password");
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid User ID or Username");
                }

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e);
            } finally {
                try {
                    if (rs != null)
                        rs.close();
                } catch (Exception e) {
                }
                try {
                    if (pst != null)
                        pst.close();
                } catch (Exception e) {
                }
                try {
                    if (con != null)
                        con.close();
                } catch (Exception e) {
                }
            }
        }
    }

    public static void main(String[] args) {
        new LoginForm();
    }
}
