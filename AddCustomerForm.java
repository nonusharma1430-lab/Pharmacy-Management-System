import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;

class AddCustomerForm extends JFrame implements ActionListener {

    JLabel lCustId, lCustName, lContact;
    JTextField tCustId, tCustName, tContact;
    JButton bAdd, bBack;

    Connection con;
    PreparedStatement pst;

    AddCustomerForm() {
        setTitle("Add Customer");
        setSize(400, 300);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(194, 217, 220));

        // Labels
        lCustId = new JLabel("Customer ID:");
        lCustId.setBounds(50, 50, 120, 25);
        add(lCustId);

        lCustName = new JLabel("Customer Name:");
        lCustName.setBounds(50, 90, 120, 25);
        add(lCustName);

        lContact = new JLabel("Contact:");
        lContact.setBounds(50, 130, 120, 25);
        add(lContact);

        // Text fields
        tCustId = new JTextField();
        tCustId.setBounds(180, 50, 150, 25);
        add(tCustId);

        tCustName = new JTextField();
        tCustName.setBounds(180, 90, 150, 25);
        add(tCustName);

        tContact = new JTextField();
        tContact.setBounds(180, 130, 150, 25);
        add(tContact);

        // Buttons
        bAdd = new JButton("Add");
        bAdd.setBounds(70, 200, 100, 30);
        add(bAdd);

        bBack = new JButton("Back");
        bBack.setBounds(200, 200, 100, 30);
        add(bBack);

        // Add action listeners
        bAdd.addActionListener(this);
        bBack.addActionListener(this);

        setVisible(true);
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == bBack) {
               new WelcomePage();  // open welcome page
                    dispose();                  // close login form
        }

        if (ae.getSource() == bAdd) {
            try {
                // Get values from text fields
                String id = tCustId.getText();
                String name = tCustName.getText();
                String contact = tContact.getText();

                // Database connection
              Class.forName("com.mysql.cj.jdbc.Driver");
                con = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/project",
                        "root",
                        "VISHESHSQL123@"
                );
                // Insert query
                String sql = "INSERT INTO customer (cust_id, cust_name, contect) VALUES (?, ?, ?)";
                pst = con.prepareStatement(sql);
                pst.setString(1, id);
                pst.setString(2, name);
                pst.setString(3, contact);

                int x = pst.executeUpdate();
                if (x > 0) {
                    JOptionPane.showMessageDialog(null, "Customer added successfully!");
                     new WelcomePage();  // open welcome page
                    dispose();                  // close login form
                
                } else {
                    JOptionPane.showMessageDialog(null, "Failed to add customer!");
                }

                pst.close();
                con.close();

            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(null, "Please enter a valid numeric ID");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new AddCustomerForm();
    }
}
