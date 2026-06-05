import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;

class AddSupplyForm extends JFrame implements ActionListener {

    JLabel lSuppId, lSuppName, lContact, lAddress;
    JTextField tSuppId, tSuppName, tContact, tAddress;
    JButton bAdd, bBack;

    Connection con;
    PreparedStatement pst;

    AddSupplyForm() {

        setTitle("Add Supply");
        setSize(450, 350);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(194, 217, 220));

        lSuppId = new JLabel("Supply ID:");
        lSuppId.setBounds(50, 30, 150, 30);
        add(lSuppId);

        lSuppName = new JLabel("Supply Name:");
        lSuppName.setBounds(50, 70, 150, 30);
        add(lSuppName);

        lContact = new JLabel("Contact:");
        lContact.setBounds(50, 110, 150, 30);
        add(lContact);

        lAddress = new JLabel("Address:");
        lAddress.setBounds(50, 150, 150, 30);
        add(lAddress);

        tSuppId = new JTextField();
        tSuppId.setBounds(200, 30, 150, 30);
        add(tSuppId);

        tSuppName = new JTextField();
        tSuppName.setBounds(200, 70, 150, 30);
        add(tSuppName);

        tContact = new JTextField();
        tContact.setBounds(200, 110, 150, 30);
        add(tContact);

        tAddress = new JTextField();
        tAddress.setBounds(200, 150, 150, 30);
        add(tAddress);

        bAdd = new JButton("Add");
        bAdd.setBounds(70, 220, 120, 30);
        add(bAdd);

        bBack = new JButton("Back");
        bBack.setBounds(220, 220, 120, 30);
        add(bBack);

        bAdd.addActionListener(this);
        bBack.addActionListener(this);

        setVisible(true);
    }

    public void actionPerformed(ActionEvent ae) {

        if (ae.getSource() == bBack) {
            new WelcomePage();
            dispose();
        }

        if (ae.getSource() == bAdd) {
            try {

                // Check empty fields
                if (tSuppId.getText().isEmpty() || tSuppName.getText().isEmpty() ||
                    tContact.getText().isEmpty() || tAddress.getText().isEmpty()) {

                    JOptionPane.showMessageDialog(null, "Please fill all fields!");
                    return;
                }

                // Now Supply ID is VARCHAR (String)
                String id = tSuppId.getText();
                String name = tSuppName.getText();
                String contact = tContact.getText();
                String address = tAddress.getText();

                Class.forName("com.mysql.cj.jdbc.Driver");
                con = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/project",
                        "root",
                        "VISHESHSQL123@"
                );

                // Updated SQL (contact spelled correctly)
                String sql = "INSERT INTO supplier (supp_id, supp_name, contect, address) VALUES (?, ?, ?, ?)";

                pst = con.prepareStatement(sql);
                pst.setString(1, id);
                pst.setString(2, name);
                pst.setString(3, contact);
                pst.setString(4, address);

                int x = pst.executeUpdate();

                if (x > 0) {
                    JOptionPane.showMessageDialog(null, "Supply Added Successfully!");
                    new WelcomePage();
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "Failed to Add Supply!");
                }

                pst.close();
                con.close();

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new AddSupplyForm();
    }
}
