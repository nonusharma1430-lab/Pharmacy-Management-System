import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.ArrayList;

class ReturnBillForm extends JFrame implements ActionListener {

    // --- Configuration (REPLACE THESE WITH YOUR ACTUAL DB DETAILS) ---
    private static class DatabaseConfig {
        static final String URL = "jdbc:mysql://localhost:3306/project";
        static final String USER = "root";
        static final String PASSWORD = "VISHESHSQL123@";
    }

    // --- Data Model for Bill Items ---
    private static class BillItem {
        int medId;
        String medName;
        int quantity;
        float price;

        public BillItem(int medId, String medName, int quantity, float price) {
            this.medId = medId;
            this.medName = medName;
            this.quantity = quantity;
            this.price = price;
        }
    }
    
    // --- UI Components ---
    JLabel lBillId, lReturnTotal;
    JTextField tBillId, tReturnTotal;
    JButton bShow, bReturn, bBack;
    JTable table;
    DefaultTableModel model;

    int currentBillId = -1; 

    ReturnBillForm() {
        setTitle("Partial/Full Bill Return");
        setSize(700, 450); 
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(194, 217, 220));

        // Bill ID Input
        lBillId = new JLabel("Bill ID:"); lBillId.setBounds(50, 30, 100, 25); add(lBillId);
        tBillId = new JTextField(); tBillId.setBounds(150, 30, 150, 25); add(tBillId);

        bShow = new JButton("Load Bill"); 
        bShow.setBounds(310, 30, 120, 25); add(bShow);

        // --- Table Setup for Selectable Items ---
        model = new DefaultTableModel(new Object[]{"Select", "Medicine", "Qty", "Price", "Amount", "Med ID"}, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return Boolean.class; 
                if (columnIndex == 2) return Integer.class;
                if (columnIndex == 3 || columnIndex == 4) return Float.class;
                return String.class;
            }
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0;
            }
        };
        table = new JTable(model);
        
        // Hide the Med ID column (index 5)
        table.getColumnModel().getColumn(5).setMinWidth(0);
        table.getColumnModel().getColumn(5).setMaxWidth(0);
        table.getColumnModel().getColumn(5).setWidth(0);

        JScrollPane scroll = new JScrollPane(table); 
        scroll.setBounds(50, 70, 600, 250); 
        add(scroll);
        
        // --- Total Amount for Return ---
        lReturnTotal = new JLabel("Total Return Amount (₹):"); 
        lReturnTotal.setBounds(50, 330, 200, 25); 
        add(lReturnTotal);
        
        tReturnTotal = new JTextField("0.00");
        tReturnTotal.setBounds(230, 330, 150, 25);
        tReturnTotal.setEditable(false);
        add(tReturnTotal);
        
        // --- Buttons ---
        bReturn = new JButton("Confirm Selected Return"); 
        bReturn.setBounds(50, 370, 250, 30); add(bReturn);

        bBack = new JButton("Back");
        bBack.setBounds(320, 370, 120, 30); add(bBack);

        bShow.addActionListener(this);
        bReturn.addActionListener(this);
        bBack.addActionListener(this);
        
        table.getModel().addTableModelListener(e -> updateTotalReturnAmount());

        setVisible(true);
    }
    
    // Calculates the sum of selected item amounts
    private void updateTotalReturnAmount() {
        float total = 0.0f;
        for (int i = 0; i < model.getRowCount(); i++) {
            boolean isSelected = (Boolean) model.getValueAt(i, 0); 
            if (isSelected) {
                total += (Float) model.getValueAt(i, 4);
            }
        }
        tReturnTotal.setText(String.format("%.2f", total));
    }


    // --- Action Handler ---
    public void actionPerformed(ActionEvent ae) {
        
        if (ae.getSource() == bBack) {
            // Goes back to WelcomePage on explicit Back button click
            new WelcomePage(); 
            dispose();
            return;
        }

        if (ae.getSource() == bShow) {
            handleLoadBill();
            return;
        }

        if (ae.getSource() == bReturn) {
            handleConfirmReturn();
            return;
        }
    }

    // --- Helper Methods ---

    private void handleLoadBill() {
        String billIdText = tBillId.getText().trim();
        if (billIdText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter Bill ID");
            return;
        }

        int billId;
        try {
            billId = Integer.parseInt(billIdText);
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Bill ID must be a number", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        model.setRowCount(0); 
        tReturnTotal.setText("0.00");
        currentBillId = -1;

        String fetchSql = "SELECT b.medicine, b.quantity, b.price, (b.quantity * b.price) AS amount, m.Med_id " +
                          "FROM bill b " +
                          "JOIN medicine m ON b.medicine = m.med_name " +
                          "WHERE b.bill_id = ?";
                          
        try (Connection con = DriverManager.getConnection(DatabaseConfig.URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
             PreparedStatement pst = con.prepareStatement(fetchSql)) {
            
            pst.setInt(1, billId);
            try (ResultSet rs = pst.executeQuery()) {
                
                boolean found = false;
                
                while (rs.next()) {
                    found = true;
                    String name = rs.getString("medicine");
                    int qty = rs.getInt("quantity");
                    float price = rs.getFloat("price");
                    float amount = rs.getFloat("amount");
                    int medId = rs.getInt("Med_id");
                    
                    model.addRow(new Object[]{false, name, qty, price, amount, medId});
                }

                if (found) {
                    currentBillId = billId;
                    JOptionPane.showMessageDialog(this, "Bill ID " + billId + " loaded. Select items to return.");
                } else {
                    JOptionPane.showMessageDialog(this, "Bill not found or no items associated.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void handleConfirmReturn() {
        if (currentBillId == -1) {
            JOptionPane.showMessageDialog(this, "Please load a bill first!", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        ArrayList<BillItem> selectedItems = new ArrayList<>();
        float totalReturnAmount = 0.0f;
        for (int i = 0; i < model.getRowCount(); i++) {
            boolean isSelected = (Boolean) model.getValueAt(i, 0);
            if (isSelected) {
                int medId = (Integer) model.getValueAt(i, 5); 
                String medName = (String) model.getValueAt(i, 1);
                int qty = (Integer) model.getValueAt(i, 2);
                float price = (Float) model.getValueAt(i, 3);
                totalReturnAmount += (Float) model.getValueAt(i, 4);
                
                selectedItems.add(new BillItem(medId, medName, qty, price));
            }
        }

        if (selectedItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No items selected for return.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String totalFormatted = String.format("%.2f", totalReturnAmount);
        
        int confirm = JOptionPane.showConfirmDialog(this, 
                "Confirm return of " + selectedItems.size() + " item(s) for a total refund of ₹" + totalFormatted + "?", 
                "Confirm Partial Return", 
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        // --- Start Transaction ---
        Connection con = null;
        try {
            con = DriverManager.getConnection(DatabaseConfig.URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
            con.setAutoCommit(false); 

            // 1. Update Stock 
            String sqlStock = "UPDATE medicine SET quantity = quantity + ? WHERE Med_id = ?";
            try (PreparedStatement pstStock = con.prepareStatement(sqlStock)) {
                for (BillItem item : selectedItems) {
                    pstStock.setInt(1, item.quantity);
                    pstStock.setInt(2, item.medId);
                    pstStock.addBatch();
                }
                pstStock.executeBatch();
            }

            // 2. Delete/Update records in the 'bill' table
            String sqlDeleteBillItem = "DELETE FROM bill WHERE bill_id = ? AND medicine = ? AND quantity = ? AND price = ?";
            try (PreparedStatement pstDeleteBill = con.prepareStatement(sqlDeleteBillItem)) {
                for (BillItem item : selectedItems) {
                    pstDeleteBill.setInt(1, currentBillId);
                    pstDeleteBill.setString(2, item.medName);
                    pstDeleteBill.setInt(3, item.quantity);
                    pstDeleteBill.setFloat(4, item.price);
                    pstDeleteBill.addBatch();
                }
                pstDeleteBill.executeBatch();
            }
            
            // 3. Update the main 'sale' total
            String sqlUpdateSale = "UPDATE sale SET total_amount = total_amount - ? WHERE bill_id = ?";
            try (PreparedStatement pstUpdateSale = con.prepareStatement(sqlUpdateSale)) {
                pstUpdateSale.setFloat(1, totalReturnAmount);
                pstUpdateSale.setInt(2, currentBillId);
                pstUpdateSale.executeUpdate();
            }
            
            // Check if the sale is now empty (no line items left in the table model)
            boolean billIsEmpty = model.getRowCount() == selectedItems.size();
            
            if (billIsEmpty) {
                // If the bill is empty after the return, delete the sale header record
                String sqlDeleteSaleHeader = "DELETE FROM sale WHERE bill_id = ?";
                try (PreparedStatement pstDeleteSaleHeader = con.prepareStatement(sqlDeleteSaleHeader)) {
                    pstDeleteSaleHeader.setInt(1, currentBillId);
                    pstDeleteSaleHeader.executeUpdate();
                }
            }


            con.commit(); // *** Commit Transaction ***
            JOptionPane.showMessageDialog(this, 
                "Successfully returned item(s). Refund amount: ₹" + totalFormatted + 
                "\nReturning to Welcome Page.", 
                "Success", JOptionPane.INFORMATION_MESSAGE);

            // *** THE REQUESTED CHANGE IS HERE ***
            new WelcomePage();
            dispose(); // Close the current form

        } catch (SQLException sqle) {
            // *** Rollback Transaction on error ***
            try {
                if (con != null) {
                    con.rollback();
                }
            } catch (SQLException rollbackEx) {
                System.err.println("Rollback failed: " + rollbackEx.getMessage());
            }
            JOptionPane.showMessageDialog(this, "Return transaction failed. Error: " + sqle.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
            sqle.printStackTrace();
        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException closeEx) {
                System.err.println("Connection closing failed: " + closeEx.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "MySQL JDBC Driver not found!", "Driver Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Assuming WelcomePage is needed for the entry point if no other class calls it
        // SwingUtilities.invokeLater(() -> new WelcomePage()); 
        // For testing the return form directly:
        SwingUtilities.invokeLater(() -> new ReturnBillForm());
    }
}

