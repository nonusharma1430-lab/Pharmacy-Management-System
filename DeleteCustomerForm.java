import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;

class DeleteCustomerForm extends JFrame implements ActionListener {

    JLabel lCustId, lCustName;
    JTextField tCustId, tCustName;
    JButton bLoad, bDelete, bBack;

    Connection con;
    PreparedStatement pst;
    ResultSet rs;

    DeleteCustomerForm() {
        setTitle("Delete Customer");
        setSize(400, 250);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(194, 217, 220));

        // Labels
        lCustId = new JLabel("Customer ID:");
        lCustId.setBounds(50, 40, 100, 25);
        add(lCustId);

        lCustName = new JLabel("Customer Name:");
        lCustName.setBounds(50, 80, 120, 25);
        add(lCustName);

        // Text fields
        tCustId = new JTextField();
        tCustId.setBounds(180, 40, 150, 25);
        add(tCustId);

        tCustName = new JTextField();
        tCustName.setBounds(180, 80, 150, 25);
        tCustName.setEditable(false); // Read-only
        add(tCustName);

        // Buttons
        bLoad = new JButton("Load");
        bLoad.setBounds(30, 150, 90, 30);
        add(bLoad);

        bDelete = new JButton("Delete");
        bDelete.setBounds(140, 150, 90, 30);
        add(bDelete);

        bBack = new JButton("Back");
        bBack.setBounds(250, 150, 90, 30);
        add(bBack);

        // Action listeners
        bLoad.addActionListener(this);
        bDelete.addActionListener(this);
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
                String custId = tCustId.getText().trim(); // Customer ID as String
                if (custId.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please enter a Customer ID");
                    return;
                }

                String sql = "SELECT cust_name FROM customer WHERE cust_id=?";
                pst = con.prepareStatement(sql);
                pst.setString(1, custId); // use String
                rs = pst.executeQuery();

                if (rs.next()) {
                    tCustName.setText(rs.getString("cust_name"));
                } else {
                    tCustName.setText("");
                    JOptionPane.showMessageDialog(null, "Customer not found!");
                }

                rs.close();
                pst.close();
            }

            if (ae.getSource() == bDelete) {
                String custId = tCustId.getText().trim();
                if (custId.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please enter a Customer ID");
                    return;
                }

                String sql = "DELETE FROM customer WHERE cust_id=?";
                pst = con.prepareStatement(sql);
                pst.setString(1, custId); // use String

                int x = pst.executeUpdate();
                if (x > 0) {
                    JOptionPane.showMessageDialog(null, "Customer deleted successfully!");
                    tCustId.setText("");
                    tCustName.setText("");
                    new WelcomePage();
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "Customer not found!");
                }

                pst.close();
            }

            if (ae.getSource() == bBack) {
                dispose();
            }

            con.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new DeleteCustomerForm();
    }
}
