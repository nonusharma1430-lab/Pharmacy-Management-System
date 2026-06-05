import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;

class EditCustomerForm extends JFrame implements ActionListener {

    JLabel lCustId, lCustName, lContact;
    JTextField tCustId, tCustName, tContact; // use tContact consistently
    JButton bLoad, bUpdate, bBack;

    Connection con;
    PreparedStatement pst;
    ResultSet rs;

    EditCustomerForm() {
        setTitle("Edit Customer");
        setSize(400, 300);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(194, 217, 220));

        // Labels
        lCustId = new JLabel("Customer ID:");
        lCustId.setBounds(50, 30, 120, 25);
        add(lCustId);

        lCustName = new JLabel("Customer Name:");
        lCustName.setBounds(50, 70, 120, 25);
        add(lCustName);

        lContact = new JLabel("Contact:");
        lContact.setBounds(50, 110, 120, 25);
        add(lContact);

        // Text fields
        tCustId = new JTextField();
        tCustId.setBounds(180, 30, 150, 25);
        add(tCustId);

        tCustName = new JTextField();
        tCustName.setBounds(180, 70, 150, 25);
        add(tCustName);

        tContact = new JTextField();
        tContact.setBounds(180, 110, 150, 25);
        add(tContact);

        // Buttons
        bLoad = new JButton("Load");
        bLoad.setBounds(30, 180, 100, 30);
        add(bLoad);

        bUpdate = new JButton("Update");
        bUpdate.setBounds(140, 180, 100, 30);
        add(bUpdate);

        bBack = new JButton("Back");
        bBack.setBounds(250, 180, 100, 30);
        add(bBack);

        // Action listeners
        bLoad.addActionListener(this);
        bUpdate.addActionListener(this);
        bBack.addActionListener(this);

        setVisible(true);
    }

    public void actionPerformed(ActionEvent ae) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/project",
                    "root",
                    "VISHESHSQL123@"
            );

            if (ae.getSource() == bLoad) {
                String custId = tCustId.getText().trim(); // customer ID as String
                if (custId.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please enter a Customer ID");
                    return;
                }

                String sql = "SELECT * FROM customer WHERE cust_id=?";
                pst = con.prepareStatement(sql);
                pst.setString(1, custId); // use setString for String ID
                rs = pst.executeQuery();

                if (rs.next()) {
                    tCustName.setText(rs.getString("cust_name"));
                    tContact.setText(rs.getString("contect")); // use correct database column name
                } else {
                    JOptionPane.showMessageDialog(null, "Customer not found!");
                    tCustName.setText("");
                    tContact.setText("");
                }

                rs.close();
                pst.close();
            }

            if (ae.getSource() == bUpdate) {
                String custId = tCustId.getText().trim(); // String ID
                String name = tCustName.getText().trim();
                String contact = tContact.getText().trim(); // consistent variable

                if (custId.isEmpty() || name.isEmpty() || contact.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "All fields must be filled");
                    return;
                }

                String sql = "UPDATE customer SET cust_name=?, contect=? WHERE cust_id=?";
                pst = con.prepareStatement(sql);
                pst.setString(1, name);
                pst.setString(2, contact);
                pst.setString(3, custId); // use String here too

                int x = pst.executeUpdate();
                if (x > 0) {
                    JOptionPane.showMessageDialog(null, "Customer updated successfully!");
                    new WelcomePage();
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "Update failed!");
                }

                pst.close();
            }

            if (ae.getSource() == bBack) {
                new WelcomePage();
                dispose();
            }

            con.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new EditCustomerForm();
    }
}
