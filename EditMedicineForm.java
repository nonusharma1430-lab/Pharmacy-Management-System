import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;

class EditMedicineForm extends JFrame implements ActionListener {

    JLabel lMedId, lMedName, lCompany, lPrice, lQuantity, lExpireDate;
    JTextField tMedId, tMedName, tCompany, tPrice, tQuantity, tExpireDate; // using JTextField for date
    JButton bLoad, bUpdate, bBack;

    Connection con;
    PreparedStatement pst;
    ResultSet rs;

    EditMedicineForm() {
        setTitle("Edit Medicine");
        setSize(500, 450);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(194, 217, 220));

        // Labels
        lMedId = new JLabel("Medicine ID:");
        lMedId.setBounds(50, 30, 150, 30);
        add(lMedId);

        lMedName = new JLabel("Medicine Name:");
        lMedName.setBounds(50, 70, 150, 30);
        add(lMedName);

        lCompany = new JLabel("Company:");
        lCompany.setBounds(50, 110, 150, 30);
        add(lCompany);

        lPrice = new JLabel("Price:");
        lPrice.setBounds(50, 150, 150, 30);
        add(lPrice);

        lExpireDate = new JLabel("Expire Date (YYYY-MM-DD):");
        lExpireDate.setBounds(50, 190, 200, 30);
        add(lExpireDate);

        lQuantity = new JLabel("Quantity:");
        lQuantity.setBounds(50, 230, 150, 30);
        add(lQuantity);

        // Text fields
        tMedId = new JTextField();
        tMedId.setBounds(250, 30, 150, 30);
        add(tMedId);

        tMedName = new JTextField();
        tMedName.setBounds(250, 70, 150, 30);
        add(tMedName);

        tCompany = new JTextField();
        tCompany.setBounds(250, 110, 150, 30);
        add(tCompany);

        tPrice = new JTextField();
        tPrice.setBounds(250, 150, 150, 30);
        add(tPrice);

        tExpireDate = new JTextField();
        tExpireDate.setBounds(250, 190, 150, 30);
        add(tExpireDate);

        tQuantity = new JTextField();
        tQuantity.setBounds(250, 230, 150, 30);
        add(tQuantity);

        // Buttons
        bLoad = new JButton("Load");
        bLoad.setBounds(50, 300, 120, 30);
        add(bLoad);

        bUpdate = new JButton("Update");
        bUpdate.setBounds(180, 300, 120, 30);
        add(bUpdate);

        bBack = new JButton("Back");
        bBack.setBounds(310, 300, 120, 30);
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
                String medIdText = tMedId.getText().trim();
                String medNameText = tMedName.getText().trim();

                String sql = "";
                if (!medIdText.isEmpty()) {
                    sql = "SELECT * FROM medicine WHERE Med_id = ?";
                    pst = con.prepareStatement(sql);
                    pst.setInt(1, Integer.parseInt(medIdText));
                } else if (!medNameText.isEmpty()) {
                    sql = "SELECT * FROM medicine WHERE med_name = ?";
                    pst = con.prepareStatement(sql);
                    pst.setString(1, medNameText);
                } else {
                    JOptionPane.showMessageDialog(null, "Enter Med ID or Name to load");
                    return;
                }

                rs = pst.executeQuery();
                if (rs.next()) {
                    tMedId.setText(String.valueOf(rs.getInt("Med_id")));
                    tMedName.setText(rs.getString("med_name"));
                    tCompany.setText(rs.getString("company"));
                    tPrice.setText(String.valueOf(rs.getFloat("price")));
                    tExpireDate.setText(rs.getString("expire_date"));
                    tQuantity.setText(String.valueOf(rs.getInt("quantity")));
                } else {
                    JOptionPane.showMessageDialog(null, "Medicine not found");
                }

                rs.close();
                pst.close();
            }

            if (ae.getSource() == bUpdate) {
                int medId = Integer.parseInt(tMedId.getText());
                String medName = tMedName.getText();
                String company = tCompany.getText();
                float price = Float.parseFloat(tPrice.getText());
                String expireDate = tExpireDate.getText();
                int quantity = Integer.parseInt(tQuantity.getText());

                String sql = "UPDATE medicine SET med_name=?, company=?, price=?, expire_date=?, quantity=? WHERE Med_id=?";
                pst = con.prepareStatement(sql);
                pst.setString(1, medName);
                pst.setString(2, company);
                pst.setFloat(3, price);
                pst.setString(4, expireDate);
                pst.setInt(5, quantity);
                pst.setInt(6, medId);

                int x = pst.executeUpdate();
                if (x > 0) {
                    JOptionPane.showMessageDialog(null, "Medicine updated successfully!");
                     new WelcomePage();  // open welcome page
                        dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "Update failed!");
                }

                pst.close();
            }

            con.close();

        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(null, "Please enter valid numeric values for ID, Price, Quantity");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new EditMedicineForm();
    }
}
