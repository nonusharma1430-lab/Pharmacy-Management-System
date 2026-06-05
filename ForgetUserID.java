import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.sql.*;

class ForgetUserID extends JFrame implements ActionListener {

    JLabel luser, lquestion, lanswer;
    JTextField tuser, tanswer;
    JComboBox cbselect;
    JButton bretrieve, bclear, bexit;

    Connection con;
    PreparedStatement pst;
    ResultSet rs;

    ForgetUserID() {
        setTitle("Forget User ID");
        setSize(500, 350);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(194, 217, 220));

        // Username
        luser = new JLabel("Username");
        luser.setBounds(90, 30, 120, 30);
        add(luser);
        tuser = new JTextField();
        tuser.setBounds(240, 30, 120, 30);
        add(tuser);

        // Security Question
        lquestion = new JLabel("Security Question");
        lquestion.setBounds(90, 70, 140, 30);
        add(lquestion);
        cbselect = new JComboBox();
        cbselect.setBounds(240, 70, 180, 30);
        cbselect.addItem("---Select---");
        cbselect.addItem("Your Favourite Color");
        cbselect.addItem("Your Favourite Book");
        cbselect.addItem("Your Favourite Food");
        cbselect.addItem("Your Mother's Name");
        cbselect.addItem("Your Father's Name");
        add(cbselect);

        // Security Answer
        lanswer = new JLabel("Security Answer");
        lanswer.setBounds(90, 110, 120, 30);
        add(lanswer);
        tanswer = new JTextField();
        tanswer.setBounds(240, 110, 120, 30);
        add(tanswer);

        // Buttons
        bretrieve = new JButton("Retrieve User ID");
        bretrieve.setBounds(90, 160, 150, 30);
        add(bretrieve);

        bclear = new JButton("Clear");
        bclear.setBounds(250, 160, 120, 30);
        add(bclear);

        bexit = new JButton("Exit");
        bexit.setBounds(180, 210, 120, 30);
        add(bexit);

        // Listeners
        bretrieve.addActionListener(this);
        bclear.addActionListener(this);
        bexit.addActionListener(this);

        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        String username = tuser.getText();
        String question = cbselect.getSelectedItem().toString();
        String answer = tanswer.getText();

        if (ae.getSource() == bclear) {
            tuser.setText("");
            cbselect.setSelectedIndex(0);
            tanswer.setText("");
        }

        if (ae.getSource() == bexit) {
            new LoginForm(); // go back to login
            dispose();
        }

        if (ae.getSource() == bretrieve) {
            if (username.isEmpty() || question.equals("---Select---") || answer.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required for retrieval!");
                return;
            }

            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                con = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/qems",
                        "root",
                        "VISHESHSQL123@"
                );

                String sql = "SELECT userid FROM admin WHERE username=? AND question=? AND answer=?";
                pst = con.prepareStatement(sql);
                pst.setString(1, username);
                pst.setString(2, question);
                pst.setString(3, answer);
                rs = pst.executeQuery();

                if (rs.next()) {
                    String userid = rs.getString("userid");
                    JOptionPane.showMessageDialog(this, "Your User ID is: " + userid);
                    new LoginForm();
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "No matching User ID found! Check your details.");
                }

                rs.close();
                pst.close();
                con.close();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, e);
            }
        }
    }

    public static void main(String[] args) {
        new ForgetUserID();
    }
}
