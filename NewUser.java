import java.awt.*;
import javax.swing.*;
import java.sql.*;
import java.awt.event.*;

class NewUser extends JFrame implements ActionListener {

    JLabel luid, luser, lpass, lconfpass, lselques, lanswer;
    JTextField tuid, tuser, tanswer;
    JPasswordField tpass, tconfpass;
    JComboBox cbselect;
    JCheckBox cbshow;
    JButton bcreate, bback;

    Connection con;
    PreparedStatement pst;

    NewUser() {

        setTitle("Create User");
        setSize(500,500);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        getContentPane().setBackground(new Color(194, 217, 220));

        // USER ID
        luid = new JLabel("User ID");
        luid.setBounds(50,40,120,30);
        add(luid);

        tuid = new JTextField();
        tuid.setBounds(170,40,120,30);
        add(tuid);

        // USERNAME
        luser = new JLabel("Username");
        luser.setBounds(50,90,120,30);
        add(luser);

        tuser = new JTextField("");
        tuser.setBounds(170,90,120,30);
        add(tuser);

        // PASSWORD
        lpass = new JLabel("Password");
        lpass.setBounds(50,140,120,30);
        add(lpass);

        tpass = new JPasswordField("");
        tpass.setBounds(170,140,120,30);
        add(tpass);

        // CONFIRM PASSWORD
        lconfpass = new JLabel("Confirm Password");
        lconfpass.setBounds(50,190,150,30);
        add(lconfpass);

        tconfpass = new JPasswordField("");
        tconfpass.setBounds(170,190,120,30);
        add(tconfpass);

        // SHOW PASSWORD
        cbshow = new JCheckBox("Show Password");
        cbshow.setBounds(170,230,150,30);
        add(cbshow);

        // SECURITY QUESTION
        lselques = new JLabel("Security Question");
        lselques.setBounds(50,270,150,30);
        add(lselques);

        cbselect = new JComboBox();
        cbselect.setBounds(170,270,180,30);
        add(cbselect);

        cbselect.addItem("---Select---");
        cbselect.addItem("Your Favourite Color");
        cbselect.addItem("Your Favourite Book");
        cbselect.addItem("Your Favourite Food");
        cbselect.addItem("Your Mother's Name");
        cbselect.addItem("Your Father's Name");

        // SECURITY ANSWER
        lanswer = new JLabel("Security Answer");
        lanswer.setBounds(50,320,150,30);
        add(lanswer);

        tanswer = new JTextField("");
        tanswer.setBounds(170,320,120,30);
        add(tanswer);

        // BUTTONS
        bcreate = new JButton("Create");
        bcreate.setBounds(80,380,120,30);
        add(bcreate);

        bback = new JButton("Back");
        bback.setBounds(250,380,120,30);
        add(bback);

        bcreate.addActionListener(this);
        bback.addActionListener(this);

        // SHOW PASSWORD TOGGLE
        cbshow.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                tpass.setEchoChar((char)0);
                tconfpass.setEchoChar((char)0);
            } else {
                tpass.setEchoChar('*');
                tconfpass.setEchoChar('*');
            }
        });

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {

        if (ae.getSource() == bback) {
            new LoginForm();
            dispose();
        }

        if (ae.getSource() == bcreate) {

            String id = tuid.getText();
            String username = tuser.getText();
            String pass = new String(tpass.getPassword());
            String confpass = new String(tconfpass.getPassword());
            String question = cbselect.getSelectedItem().toString();
            String answer = tanswer.getText();

            // VALIDATION
            if (id.isEmpty() || username.isEmpty() || pass.isEmpty() || confpass.isEmpty() ||
                question.equals("---Select---") || answer.isEmpty()) {

                JOptionPane.showMessageDialog(this, "All fields are required!");
                return;
            }

            if (!pass.equals(confpass)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match!");
                return;
            }

            try {

                Class.forName("com.mysql.cj.jdbc.Driver");
                con = DriverManager.getConnection(
                  "jdbc:mysql://localhost:3306/project",
                        "root",
                        "VISHESHSQL123@"
                );

                // ********** CHECK IF USER ID ALREADY EXISTS **********
                String checkSql = "SELECT userid FROM admin WHERE userid=?";
                PreparedStatement checkPst = con.prepareStatement(checkSql);
                checkPst.setString(1, id);

                ResultSet checkRs = checkPst.executeQuery();

                if (checkRs.next()) {
                    JOptionPane.showMessageDialog(this, "User ID already exists! Choose another ID.");
                    checkRs.close();
                    checkPst.close();
                    con.close();
                    return;
                }

                checkRs.close();
                checkPst.close();

                // ********** INSERT NEW USER **********
                String sql = "INSERT INTO admin(userid, username, password, question, answer) VALUES(?,?,?,?,?)";

                pst = con.prepareStatement(sql);
                pst.setString(1, id);
                pst.setString(2, username);
                pst.setString(3, pass);
                pst.setString(4, question);
                pst.setString(5, answer);

                int x = pst.executeUpdate();

                if (x > 0) {
                    JOptionPane.showMessageDialog(this, "User Created Successfully!");
                    new LoginForm();  // open welcome page
                    dispose();                  // close login form
                

                } else {
                    JOptionPane.showMessageDialog(this, "User Not Created!");
                }

                pst.close();
                con.close();

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e);
            }
        }
    }

    public static void main(String[] args) {
        new NewUser();
    }
}
