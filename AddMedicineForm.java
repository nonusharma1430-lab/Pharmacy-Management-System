import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;
import java.text.SimpleDateFormat;

class AddMedicineForm extends JFrame implements ActionListener {

    JLabel lMedId, lMedName, lCompany, lPrice, lExpireDate, lQuantity;
    JTextField tMedId, tMedName, tCompany, tPrice, tExpireDate, tQuantity;
    JButton bAdd, bBack;

    Connection con;
    PreparedStatement pst;

    AddMedicineForm() {
        setTitle("Add Medicine");
        setSize(500, 500);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(194, 217, 220));

        // Labels
        lMedId = new JLabel("Medicine ID:");
        lMedId.setBounds(50, 50, 120, 30);
        add(lMedId);

        lMedName = new JLabel("Medicine Name:");
        lMedName.setBounds(50, 100, 120, 30);
        add(lMedName);

        lCompany = new JLabel("Company:");
        lCompany.setBounds(50, 150, 120, 30);
        add(lCompany);

        lPrice = new JLabel("Price:");
        lPrice.setBounds(50, 200, 120, 30);
        add(lPrice);

        lExpireDate = new JLabel("Expire Date (yyyy-MM-dd):");
        lExpireDate.setBounds(50, 250, 200, 30);
        add(lExpireDate);

        lQuantity = new JLabel("Quantity:");
        lQuantity.setBounds(50, 300, 120, 30);
        add(lQuantity);

        // Text Fields
        tMedId = new JTextField();
        tMedId.setBounds(250, 50, 150, 30);
        add(tMedId);

        tMedName = new JTextField();
        tMedName.setBounds(250, 100, 150, 30);
        add(tMedName);

        tCompany = new JTextField();
        tCompany.setBounds(250, 150, 150, 30);
        add(tCompany);

        tPrice = new JTextField();
        tPrice.setBounds(250, 200, 150, 30);
        add(tPrice);

        tExpireDate = new JTextField();
        tExpireDate.setBounds(250, 250, 150, 30);
        add(tExpireDate);

        tQuantity = new JTextField();
        tQuantity.setBounds(250, 300, 150, 30);
        add(tQuantity);

        // Buttons
        bAdd = new JButton("Add Medicine");
        bAdd.setBounds(80, 370, 140, 30);
        add(bAdd);

        bBack = new JButton("Back");
        bBack.setBounds(250, 370, 120, 30);
        add(bBack);

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
                int medId = Integer.parseInt(tMedId.getText());
                String medName = tMedName.getText();
                String company = tCompany.getText();
                float price = Float.parseFloat(tPrice.getText());
                int quantity = Integer.parseInt(tQuantity.getText());
                String expireDate = tExpireDate.getText();

                // Validate date format
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                sdf.setLenient(false); // strict date check
                sdf.parse(expireDate);

                // Database connection
               Class.forName("com.mysql.cj.jdbc.Driver");
                con = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/project",
                        "root",
                        "VISHESHSQL123@"
                );

                String sql = "INSERT INTO medicine (Med_id, med_name, company, price, expire_date, quantity) VALUES (?, ?, ?, ?, ?, ?)";
                pst = con.prepareStatement(sql);
                pst.setInt(1, medId);
                pst.setString(2, medName);
                pst.setString(3, company);
                pst.setFloat(4, price);
                pst.setString(5, expireDate);
                pst.setInt(6, quantity);

                int x = pst.executeUpdate();
                if (x > 0) {
                    JOptionPane.showMessageDialog(null, "Medicine added successfully!");
                     new WelcomePage();  // open welcome page
                    dispose();                  // close login form
                
                } else {
                    JOptionPane.showMessageDialog(null, "Failed to add medicine.");
                }

                pst.close();
                con.close();

            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(null, "Please enter valid numbers for ID, Price, and Quantity");
            } catch (java.text.ParseException pe) {
                JOptionPane.showMessageDialog(null, "Invalid date format! Use yyyy-MM-dd");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new AddMedicineForm();
    }
}
