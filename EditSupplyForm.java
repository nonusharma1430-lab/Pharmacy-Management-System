import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;

class EditSupplyForm extends JFrame implements ActionListener {

    JLabel lSuppId, lSuppName, lContact, lAddress;
    JTextField tSuppId, tSuppName, tContact, tAddress;
    JButton bLoad, bUpdate, bBack;

    Connection con;
    PreparedStatement pst;
    ResultSet rs;

    EditSupplyForm() {
        setTitle("Edit Supply");
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

        bLoad = new JButton("Load");
        bLoad.setBounds(50, 220, 100, 30);
        add(bLoad);

        bUpdate = new JButton("Update");
        bUpdate.setBounds(160, 220, 100, 30);
        add(bUpdate);

        bBack = new JButton("Back");
        bBack.setBounds(270, 220, 100, 30);
        add(bBack);

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

            if (ae.getSource() == bBack) {
                new WelcomePage();
                dispose();
            }

            if (ae.getSource() == bLoad) {
                String idText = tSuppId.getText().trim();
                String nameText = tSuppName.getText().trim();

                if (idText.isEmpty() && nameText.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Enter ID or Name to load");
                    return;
                }

                String sql;
                if (!idText.isEmpty()) {
                    sql = "SELECT * FROM supplier WHERE supp_id=?";
                    pst = con.prepareStatement(sql);
                    pst.setString(1, idText); // ID as string
                } else {
                    sql = "SELECT * FROM supplier WHERE supp_name=?";
                    pst = con.prepareStatement(sql);
                    pst.setString(1, nameText);
                }

                rs = pst.executeQuery();
                if (rs.next()) {
                    tSuppId.setText(rs.getString("supp_id"));
                    tSuppName.setText(rs.getString("supp_name"));
                    tContact.setText(rs.getString("contect"));
                    tAddress.setText(rs.getString("address"));
                } else {
                    JOptionPane.showMessageDialog(null, "Supply not found");
                    tSuppId.setText("");
                    tSuppName.setText("");
                    tContact.setText("");
                    tAddress.setText("");
                }

                rs.close();
                pst.close();
            }

            if (ae.getSource() == bUpdate) {
                String id = tSuppId.getText().trim(); // string
                String name = tSuppName.getText().trim();
                String contact = tContact.getText().trim();
                String address = tAddress.getText().trim();

                if (id.isEmpty() || name.isEmpty() || contact.isEmpty() || address.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "All fields must be filled");
                    return;
                }

                String sql = "UPDATE supplier SET supp_name=?, contect=?, address=? WHERE supp_id=?";
                pst = con.prepareStatement(sql);
                pst.setString(1, name);
                pst.setString(2, contact);
                pst.setString(3, address);
                pst.setString(4, id); // ID as string

                int x = pst.executeUpdate();
                if (x > 0) {
                    JOptionPane.showMessageDialog(null, "Supply updated successfully!");
                    new WelcomePage();
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "Update failed!");
                }

                pst.close();
            }

            con.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new EditSupplyForm();
    }
}
