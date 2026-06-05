import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

class ViewSalesForm extends JFrame implements ActionListener {

    // --- Configuration (REPLACE THESE WITH YOUR ACTUAL DB DETAILS) ---
    private static class DatabaseConfig {
        static final String URL = "jdbc:mysql://localhost:3306/project";
        static final String USER = "root";
        static final String PASSWORD = "VISHESHSQL123@";
    }

    // --- UI Components ---
    JLabel lBillId, lCustId, lDate, lTotalAmount;
    JTextField tBillId, tCustId, tDate, tTotalAmount;
    JButton bLoad, bBack;
    
    JTable itemTable;
    DefaultTableModel itemModel;

    ViewSalesForm() {
        setTitle("View Sales Details");
        setSize(650, 500); // Increased size to accommodate the table
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(194, 217, 220));

        // --- Header Fields ---
        lBillId = new JLabel("Bill ID:"); lBillId.setBounds(50, 30, 100, 25); add(lBillId);
        tBillId = new JTextField(); tBillId.setBounds(180, 30, 150, 25); add(tBillId);
        
        lCustId = new JLabel("Customer ID:"); lCustId.setBounds(50, 70, 100, 25); add(lCustId);
        tCustId = new JTextField(); tCustId.setBounds(180, 70, 150, 25); tCustId.setEditable(false); add(tCustId);

        lDate = new JLabel("Date:"); lDate.setBounds(350, 70, 50, 25); add(lDate);
        tDate = new JTextField(); tDate.setBounds(400, 70, 150, 25); tDate.setEditable(false); add(tDate);

        lTotalAmount = new JLabel("Total Amount (₹):"); lTotalAmount.setBounds(50, 110, 150, 25); add(lTotalAmount);
        tTotalAmount = new JTextField(); tTotalAmount.setBounds(180, 110, 150, 25); tTotalAmount.setEditable(false); add(tTotalAmount);

        // --- Table Setup for Line Items ---
        itemModel = new DefaultTableModel(new Object[]{"Medicine", "Quantity", "Price (₹)", "Amount (₹)"}, 0);
        itemTable = new JTable(itemModel);
        JScrollPane scroll = new JScrollPane(itemTable); 
        scroll.setBounds(50, 150, 550, 250); // Position the table below headers
        add(scroll);
        
        // --- Buttons ---
        bLoad = new JButton("Load Details"); bLoad.setBounds(150, 420, 120, 30); add(bLoad);
        bBack = new JButton("Back"); bBack.setBounds(350, 420, 100, 30); add(bBack);

        // Action listeners
        bLoad.addActionListener(this);
        bBack.addActionListener(this);

        setVisible(true);
    }
    
    // --- Action Handler ---
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == bBack) {
            new WelcomePage(); // Navigate to WelcomePage
            dispose();
        } else if (ae.getSource() == bLoad) {
            handleLoadDetails();
        }
    }

    // --- Business Logic: Load Sale and Line Item Details ---
    private void handleLoadDetails() {
        String idText = tBillId.getText().trim();
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a Bill ID", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int billId;
        try {
            billId = Integer.parseInt(idText);
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Bill ID must be a valid number", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Clear previous data
        tCustId.setText("");
        tDate.setText("");
        tTotalAmount.setText("");
        itemModel.setRowCount(0);

        // SQL queries
        String sqlSale = "SELECT cust_id, date, total_amount FROM sale WHERE bill_id=?";
        String sqlBillItems = "SELECT medicine, quantity, price, (quantity * price) AS amount FROM bill WHERE bill_id=?";

        try (Connection con = DriverManager.getConnection(DatabaseConfig.URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD)) {
            
            boolean found = false;

            // 1. Load Sale Header (from 'sale' table)
            try (PreparedStatement pstSale = con.prepareStatement(sqlSale)) {
                pstSale.setInt(1, billId);
                try (ResultSet rsSale = pstSale.executeQuery()) {
                    if (rsSale.next()) {
                        tCustId.setText(rsSale.getString("cust_id"));
                        tDate.setText(rsSale.getString("date"));
                        tTotalAmount.setText(String.format("%.2f", rsSale.getFloat("total_amount")));
                        found = true;
                    }
                }
            }

            if (!found) {
                JOptionPane.showMessageDialog(this, "Sale not found for Bill ID " + billId, "Not Found", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 2. Load Line Items (from 'bill' table)
            try (PreparedStatement pstBill = con.prepareStatement(sqlBillItems)) {
                pstBill.setInt(1, billId);
                try (ResultSet rsBill = pstBill.executeQuery()) {
                    while (rsBill.next()) {
                        String name = rsBill.getString("medicine");
                        int qty = rsBill.getInt("quantity");
                        float price = rsBill.getFloat("price");
                        float amount = rsBill.getFloat("amount");
                        
                        // Add row to table model
                        itemModel.addRow(new Object[]{name, qty, String.format("%.2f", price), String.format("%.2f", amount)});
                    }
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "MySQL JDBC Driver not found!", "Driver Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        SwingUtilities.invokeLater(() -> new ViewSalesForm());
    }
}

