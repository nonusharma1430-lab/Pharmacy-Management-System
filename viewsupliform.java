import java.awt.*;
import javax.swing.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;

class ViewCustomerForm extends JFrame {

    JTable table;
    JScrollPane scrollPane;
    DefaultTableModel model;

    Connection con;
    Statement st;
    ResultSet rs;

    ViewCustomerForm() {
        setTitle("View Customers");
        setSize(600, 400);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Table columns
        String[] columns = {"Customer ID", "Customer Name"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Load data
        loadData();

        setVisible(true);
    }

    private void loadData() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/project",
                    "root",
                    "VISHESHSQL123@"
            );

            st = con.createStatement();
            rs = st.executeQuery("SELECT * FROM customer");

            model.setRowCount(0);

            while (rs.next()) {
                String id = rs.getString("cust_id");
                String name = rs.getString("cust_name");

                Object[] row = {id, name};
                model.addRow(row);
            }

           
            new WelcomePage();
             dispose();

            rs.close();
            st.close();
            con.close();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error loading data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new ViewCustomerForm();
    }
}
