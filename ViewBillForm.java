import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

class ViewBillForm extends JFrame implements ActionListener {

    JTable table;
    JScrollPane scrollPane;
    DefaultTableModel model;
    JButton bPrint, bBack;

    Connection con;
    Statement st;
    ResultSet rs;

    ViewBillForm() {
        setTitle("View Bills");
        setSize(700, 450);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Table columns
        String[] columns = {"Bill ID", "Customer ID", "Date", "Total Amount"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Panel for buttons
        JPanel panel = new JPanel();
        panel.setBackground(new Color(194, 217, 220));
        bPrint = new JButton("Print");
        bBack = new JButton("Back");
        panel.add(bPrint);
        panel.add(bBack);
        add(panel, BorderLayout.SOUTH);

        bPrint.addActionListener(this);
        bBack.addActionListener(this);

        // Load data from database
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
            rs = st.executeQuery("SELECT * FROM sales");

            model.setRowCount(0); // Clear existing rows

            while (rs.next()) {
                int billId = rs.getInt("bill_id");
                int custId = rs.getInt("cust_id");
                Date date = rs.getDate("date");
                float totalAmount = rs.getFloat("total_amount");

                Object[] row = {billId, custId, date, totalAmount};
                model.addRow(row);
            }

            rs.close();
            st.close();
            con.close();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error loading data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == bPrint) {
            try {
                boolean complete = table.print();
                if (complete) {
                    JOptionPane.showMessageDialog(null, "Printing Complete", "Print", JOptionPane.INFORMATION_MESSAGE);
                    new WelcomePage();
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "Printing Cancelled", "Print", JOptionPane.WARNING_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error Printing: " + e.getMessage());
                e.printStackTrace();
            }
        }

        if (ae.getSource() == bBack) {
            new WelcomePage();
            dispose();
        }
    }

    public static void main(String[] args) {
        new ViewBillForm();
    }
}
