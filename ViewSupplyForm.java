import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

class ViewSupplyForm extends JFrame implements ActionListener {

    JTable table;
    JScrollPane scrollPane;
    DefaultTableModel model;
    JButton bLoad, bBack;

    ViewSupplyForm() {
        setTitle("View Suppliers");
        setSize(700, 450);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(194, 217, 220));

        // Table columns
        String[] columns = {"Supplier ID", "Supplier Name", "Contact", "Address"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        scrollPane = new JScrollPane(table);
        scrollPane.setBounds(20, 20, 640, 300);
        add(scrollPane);

        // Buttons
        bLoad = new JButton("Load");
        bLoad.setBounds(180, 350, 120, 30);
        add(bLoad);

        bBack = new JButton("Back");
        bBack.setBounds(360, 350, 120, 30);
        add(bBack);

        bLoad.addActionListener(this);
        bBack.addActionListener(this);

        setVisible(true);
    }

    private void loadData() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/project",
                    "root",
                    "VISHESHSQL123@"
            );

            String sql = "SELECT * FROM supplier"; // table name
            PreparedStatement pst = con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();

            model.setRowCount(0); // clear existing rows

            while (rs.next()) {
                String id = rs.getString("supp_id");
                String name = rs.getString("supp_name");
                String contact = rs.getString("contect"); // match column name
                String address = rs.getString("address");

                model.addRow(new Object[]{id, name, contact, address});
            }

            rs.close();
            pst.close();
            con.close();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error loading data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == bLoad) {
            loadData();
        }

        if (ae.getSource() == bBack) {
            dispose();
            // Optional: open WelcomePage if it exists
            // new WelcomePage();
        }
    }

    public static void main(String[] args) {
        new ViewSupplyForm();
    }
}
