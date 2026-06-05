import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;

class ReturnPurchaseForm extends JFrame implements ActionListener {

    JLabel lPurchaseId;
    JTextField tPurchaseId;
    JButton bReturn, bBack;

    Connection con;
    PreparedStatement pst;

    ReturnPurchaseForm() {
        setTitle("Return Purchase");
        setSize(350, 200);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(194, 217, 220));

        lPurchaseId = new JLabel("Purchase ID:");
        lPurchaseId.setBounds(50, 40, 100, 25);
        add(lPurchaseId);

        tPurchaseId = new JTextField();
        tPurchaseId.setBounds(150, 40, 120, 25);
        add(tPurchaseId);

        bReturn = new JButton("Return");
        bReturn.setBounds(50, 100, 100, 30);
        add(bReturn);

        bBack = new JButton("Back");
        bBack.setBounds(180, 100, 100, 30);
        add(bBack);

        bReturn.addActionListener(this);
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
            if (ae.getSource() == bReturn) {
                int id = Integer.parseInt(tPurchaseId.getText().trim());
                String sql = "DELETE FROM purchase WHERE purchase_id=?";
                pst = con.prepareStatement(sql);
                pst.setInt(1, id);

                int x = pst.executeUpdate();
                if (x > 0) {
                    JOptionPane.showMessageDialog(null, "Purchase returned successfully!");
                    new WelcomePage();
                    dispose();
                }
                else JOptionPane.showMessageDialog(null, "Purchase not found!");
                pst.close();
                tPurchaseId.setText("");
            }

            if (ae.getSource() == bBack) {
                new WelcomePage();
                dispose();
            }

            con.close();

        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(null, "Enter a valid numeric Purchase ID");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new ReturnPurchaseForm();
    }
}
