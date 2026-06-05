import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

class ViewAllCustomers extends JFrame implements ActionListener {

    JTable table;
    DefaultTableModel model;
    JButton bLoad, bBack;

    Connection con;
    PreparedStatement pst;
    ResultSet rs;

    ViewAllCustomers() {

        setTitle("View All Customers");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        getContentPane().setBackground(new Color(194, 217, 220));

        // Table model
        model = new DefaultTableModel();
        model.addColumn("Customer ID");
        model.addColumn("Customer Name");

        table = new JTable(model);
        JScrollPane sp = new JScrollPane(table);
        sp.setBounds(30, 30, 520, 250);
        add(sp);

        // Buttons
        bLoad = new JButton("Load All");
        bLoad.setBounds(120, 300, 120, 35);
        add(bLoad);

        bBack = new JButton("Back");
        bBack.setBounds(330, 300, 120, 35);
        add(bBack);

        bLoad.addActionListener(this);
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

                // Clear old data from table
                model.setRowCount(0);

                String sql = "SELECT cust_id, cust_name FROM customer";
                pst = con.prepareStatement(sql);
                rs = pst.executeQuery();

                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getString("cust_id"),
                        rs.getString("cust_name")
                    });
                }

                rs.close();
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
        new ViewAllCustomers();
    }
}
