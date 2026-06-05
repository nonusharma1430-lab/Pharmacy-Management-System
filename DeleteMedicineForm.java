import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;

class DeleteMedicineForm extends JFrame implements ActionListener {

    JLabel lMedId, lMedName;
    JTextField tMedId, tMedName;
    JButton bDelete, bBack, bLoad;

    Connection con;
    PreparedStatement pst;
    ResultSet rs;

    DeleteMedicineForm() {
        setTitle("Delete Medicine");
        setSize(450, 330);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(194, 217, 220));

        // Labels
        lMedId = new JLabel("Medicine ID:");
        lMedId.setBounds(50, 40, 150, 30);
        add(lMedId);

        lMedName = new JLabel("Medicine Name:");
        lMedName.setBounds(50, 90, 150, 30);
        add(lMedName);

        // Text fields
        tMedId = new JTextField();
        tMedId.setBounds(200, 40, 180, 30);
        add(tMedId);

        tMedName = new JTextField();
        tMedName.setBounds(200, 90, 180, 30);
        add(tMedName);

        // LOAD BUTTON (centered)
        bLoad = new JButton("Load");
        bLoad.setBounds(160, 140, 120, 35);
        add(bLoad);

        // DELETE BUTTON
        bDelete = new JButton("Delete");
        bDelete.setBounds(80, 200, 120, 35);
        add(bDelete);

        // BACK BUTTON
        bBack = new JButton("Back");
        bBack.setBounds(240, 200, 120, 35);
        add(bBack);

        // Action Listeners
        bLoad.addActionListener(this);
        bDelete.addActionListener(this);
        bBack.addActionListener(this);

        setVisible(true);
    }

    public void actionPerformed(ActionEvent ae) {

        // BACK
        if (ae.getSource() == bBack) {
            dispose();
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/project",
                    "root",
                    "VISHESHSQL123@"
            );

            // -----------------------------
            // LOAD BUTTON FUNCTIONALITY
            // -----------------------------
            if (ae.getSource() == bLoad) {

                String medIdText = tMedId.getText().trim();
                String medNameText = tMedName.getText().trim();

                if (medIdText.isEmpty() && medNameText.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Enter Medicine ID or Name to load.");
                    return;
                }

                String sql;

                if (!medIdText.isEmpty()) {
                    sql = "SELECT * FROM medicine WHERE med_id = ?";
                    pst = con.prepareStatement(sql);
                    pst.setInt(1, Integer.parseInt(medIdText));
                } else {
                    sql = "SELECT * FROM medicine WHERE med_name = ?";
                    pst = con.prepareStatement(sql);
                    pst.setString(1, medNameText);
                }

                rs = pst.executeQuery();

                if (rs.next()) {
                    tMedId.setText(String.valueOf(rs.getInt("med_id")));
                    tMedName.setText(rs.getString("med_name"));

                    JOptionPane.showMessageDialog(null, "Medicine Loaded!");
                } else {
                    JOptionPane.showMessageDialog(null, "Medicine not found.");
                }

                rs.close();
                pst.close();
            }

            // -----------------------------
            // DELETE BUTTON FUNCTIONALITY
            // -----------------------------
            if (ae.getSource() == bDelete) {

                String medIdText = tMedId.getText().trim();
                String medNameText = tMedName.getText().trim();

                if (medIdText.isEmpty() && medNameText.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please enter Medicine ID or Name to delete.");
                    return;
                }

                String sql;

                if (!medIdText.isEmpty() && !medNameText.isEmpty()) {
                    sql = "DELETE FROM medicine WHERE med_id = ? AND med_name = ?";
                    pst = con.prepareStatement(sql);
                    pst.setInt(1, Integer.parseInt(medIdText));
                    pst.setString(2, medNameText);
                } else if (!medIdText.isEmpty()) {
                    sql = "DELETE FROM medicine WHERE med_id = ?";
                    pst = con.prepareStatement(sql);
                    pst.setInt(1, Integer.parseInt(medIdText));
                } else {
                    sql = "DELETE FROM medicine WHERE med_name = ?";
                    pst = con.prepareStatement(sql);
                    pst.setString(1, medNameText);
                }

                int x = pst.executeUpdate();
                if (x > 0) {
                    JOptionPane.showMessageDialog(null, "Medicine Deleted Successfully!");
                    new WelcomePage();
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "No matching record found.");
                }

                pst.close();
            }

            con.close();

        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(null, "Invalid numeric value.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new DeleteMedicineForm();
    }
}
