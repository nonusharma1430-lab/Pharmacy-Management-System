import java.awt.*;
import javax.swing.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;

class ViewMedicineForm extends JFrame {

    JTable table;
    JScrollPane scrollPane;
    DefaultTableModel model;

    Connection con;
    Statement st;
    ResultSet rs;

    ViewMedicineForm() {
        setTitle("View Medicines");
        setSize(800, 400);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Table columns
        String[] columns = {"Med_id", "Medicine Name", "Company", "Price", "Expire Date", "Quantity"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

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
            rs = st.executeQuery("SELECT * FROM medicine");

            model.setRowCount(0); // clear previous data if any

            while (rs.next()) {
                int medId = rs.getInt("Med_id");
                String medName = rs.getString("med_name");
                String company = rs.getString("company");
                float price = rs.getFloat("price");
                String expireDate = rs.getString("expire_date");
                int quantity = rs.getInt("quantity");

                Object[] row = {medId, medName, company, price, expireDate, quantity};
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
        new ViewMedicineForm();
    }
}
