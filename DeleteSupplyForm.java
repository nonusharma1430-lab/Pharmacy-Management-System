import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;

class DeleteSupplyForm extends JFrame implements ActionListener {

    JLabel lSuppId, lSuppName;
    JTextField tSuppId, tSuppName;
    JButton bDelete, bBack;

    Connection con;
    PreparedStatement pst;

    DeleteSupplyForm() {
        setTitle("Delete Supply");
        setSize(450, 300);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(194, 217, 220));

        lSuppId = new JLabel("Supply ID:");
        lSuppId.setBounds(50, 50, 150, 30);
        add(lSuppId);

        lSuppName = new JLabel("Supply Name:");
        lSuppName.setBounds(50, 100, 150, 30);
        add(lSuppName);

        tSuppId = new JTextField();
        tSuppId.setBounds(200, 50, 150, 30);
        add(tSuppId);

        tSuppName = new JTextField();
        tSuppName.setBounds(200, 100, 150, 30);
        add(tSuppName);

        bDelete = new JButton("Delete");
        bDelete.setBounds(80, 180, 120, 30);
        add(bDelete);

        bBack = new JButton("Back");
        bBack.setBounds(240, 180, 120, 30);
        add(bBack);

        bDelete.addActionListener(this);
        bBack.addActionListener(this);

        setVisible(true);
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == bBack) {
            new WelcomePage();
            dispose();
        }

        if (ae.getSource() == bDelete) {
            try {
                String idText = tSuppId.getText().trim();
                String nameText = tSuppName.getText().trim();

                if (idText.isEmpty() && nameText.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Enter Supply ID or Name to delete");
                    return;
                }

                Class.forName("com.mysql.cj.jdbc.Driver");
                con = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/project",
                        "root",
                        "VISHESHSQL123@"
                );

                String sql;
                if (!idText.isEmpty() && !nameText.isEmpty()) {
                    sql = "DELETE FROM supplier WHERE supp_id=? AND supp_name=?";
                    pst = con.prepareStatement(sql);
                    pst.setString(1, idText);  // all string
                    pst.setString(2, nameText);
                } else if (!idText.isEmpty()) {
                    sql = "DELETE FROM supplier WHERE supp_id=?";
                    pst = con.prepareStatement(sql);
                    pst.setString(1, idText);  // all string
                } else {
                    sql = "DELETE FROM supplier WHERE supp_name=?";
                    pst = con.prepareStatement(sql);
                    pst.setString(1, nameText);
                }

                int x = pst.executeUpdate();
                if (x > 0) {
                    JOptionPane.showMessageDialog(null, "Supply deleted successfully!");
                    new WelcomePage();  // open welcome page
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "No matching supply found.");
                }

                pst.close();
                con.close();

            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new DeleteSupplyForm();
    }
}
