import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.HashMap;
import java.util.Vector;

class UpdateBillForm extends JFrame implements ActionListener {

    // --- Configuration (Update these with your actual DB details) ---
    private static class DatabaseConfig {
        static final String URL = "jdbc:mysql://localhost:3306/project";
        static final String USER = "root";
        static final String PASSWORD = "VISHESHSQL123@";
    }
    
    // --- Data Structures ---
    private static class MedicineStock {
        int medId;
        float price;
        int stock;
        
        public MedicineStock(int medId, float price, int stock) {
            this.medId = medId;
            this.price = price;
            this.stock = stock;
        }
    }
    
    private HashMap<String, MedicineStock> stockMap = new HashMap<>();

    // --- UI Components ---
    JLabel lBillId, lCustId, lDate, lTotal;
    JTextField tBillId, tCustId, tDate, tTotal;
    JButton bLoad, bCommitUpdate, bBack;
    JTable itemTable;
    DefaultTableModel itemModel;
    
    // Components for adding a new item
    JComboBox<String> cbNewMedicine;
    JTextField tNewQuantity;
    JButton bAddNewItem;
    
    // --- State Variables ---
    private int loadedBillId = -1;
    private float originalBillTotal = 0.0f;
    // Maps medicine name to its quantity when the bill was first loaded
    private HashMap<String, Integer> originalQuantities = new HashMap<>(); 

    UpdateBillForm() {
        setTitle("Update/Edit Bill");
        setSize(800, 600); 
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(194, 217, 220));
        
        // Load All Medicine Stock Data
        loadAllMedicineData();
        
        // --- Header Section (Bill ID, Customer, Date, Total) ---
        lBillId = new JLabel("Bill ID:"); lBillId.setBounds(50, 20, 100, 25); add(lBillId);
        tBillId = new JTextField(); tBillId.setBounds(150, 20, 150, 25); add(tBillId);
        bLoad = new JButton("Load Bill"); bLoad.setBounds(310, 20, 100, 25); add(bLoad);
        
        lCustId = new JLabel("Customer ID:"); lCustId.setBounds(50, 60, 100, 25); add(lCustId);
        tCustId = new JTextField(); tCustId.setBounds(150, 60, 150, 25); tCustId.setEditable(false); add(tCustId);
        
        lDate = new JLabel("Date:"); lDate.setBounds(350, 60, 50, 25); add(lDate);
        tDate = new JTextField(); tDate.setBounds(400, 60, 150, 25); tDate.setEditable(false); add(tDate);
        
        lTotal = new JLabel("New Bill Total (₹):"); lTotal.setBounds(580, 60, 120, 25); add(lTotal);
        tTotal = new JTextField("0.00"); tTotal.setBounds(700, 60, 80, 25); tTotal.setEditable(false); add(tTotal);
        
        // --- Table for Existing Items (Editable Quantity) ---
        itemModel = new DefaultTableModel(new Object[]{"Medicine", "Price (₹)", "Qty", "Amount (₹)"}, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 1 || columnIndex == 3) return Float.class;
                if (columnIndex == 2) return Integer.class;
                return String.class;
            }
            @Override
            public boolean isCellEditable(int row, int column) {
                // Only allow quantity (Column 2) to be edited
                return column == 2;
            }
        };
        itemTable = new JTable(itemModel);
        JScrollPane scroll = new JScrollPane(itemTable); 
        scroll.setBounds(50, 100, 700, 250); 
        add(scroll);
        
        // Listener to update the total when quantity is edited
        itemTable.getModel().addTableModelListener(e -> updateBillTotalFromTable());
        
        // --- Section for Adding New Items ---
        JPanel pNewItem = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        pNewItem.setBorder(BorderFactory.createTitledBorder("Add New Medicine"));
        pNewItem.setBounds(50, 370, 700, 80);
        
        JLabel lNewMed = new JLabel("Medicine:"); pNewItem.add(lNewMed);
        cbNewMedicine = new JComboBox<>(); 
        cbNewMedicine.setPreferredSize(new Dimension(200, 25)); pNewItem.add(cbNewMedicine);
        
        JLabel lNewQty = new JLabel("Quantity:"); pNewItem.add(lNewQty);
        tNewQuantity = new JTextField(5); tNewQuantity.setPreferredSize(new Dimension(80, 25)); pNewItem.add(tNewQuantity);
        
        bAddNewItem = new JButton("Add to Bill"); pNewItem.add(bAddNewItem);
        add(pNewItem);

        // Populate the new medicine dropdown
        for (String medName : stockMap.keySet()) {
            cbNewMedicine.addItem(medName);
        }
        
        // --- Footer Buttons ---
        bCommitUpdate = new JButton("Commit All Changes"); 
        bCommitUpdate.setBounds(200, 480, 180, 30); add(bCommitUpdate);
        
        bBack = new JButton("Back"); bBack.setBounds(400, 480, 100, 30); add(bBack);

        // Listeners
        bLoad.addActionListener(this);
        bCommitUpdate.addActionListener(this);
        bBack.addActionListener(this);
        bAddNewItem.addActionListener(this);
        
        setVisible(true);
    }

    // --- Database Helper: Load All Medicine Data (for price lookup and stock check) ---
    private void loadAllMedicineData() {
        String sql = "SELECT Med_id, med_name, price, quantity FROM medicine";
        try (Connection con = DriverManager.getConnection(DatabaseConfig.URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            
            while (rs.next()) {
                String name = rs.getString("med_name");
                stockMap.put(name, new MedicineStock(
                    rs.getInt("Med_id"),
                    rs.getFloat("price"),
                    rs.getInt("quantity")
                ));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading stock data: " + e.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // --- UI Helper: Recalculate and update the bill total ---
    private void updateBillTotalFromTable() {
        float total = 0.0f;
        for (int i = 0; i < itemModel.getRowCount(); i++) {
            try {
                // Read Quantity (Column 2) and Price (Column 1)
                int qty = Integer.parseInt(itemModel.getValueAt(i, 2).toString());
                float price = Float.parseFloat(itemModel.getValueAt(i, 1).toString());
                float itemAmount = qty * price;
                
                // Update Amount Column (Column 3)
                itemModel.setValueAt(itemAmount, i, 3);
                total += itemAmount;
            } catch (NumberFormatException ex) {
                // If invalid input, set total to 0 and show error
                tTotal.setText("0.00");
                JOptionPane.showMessageDialog(this, "Invalid quantity entered. Please use whole numbers.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return; 
            }
        }
        tTotal.setText(String.format("%.2f", total));
    }

    // --- Action Handler ---
    public void actionPerformed(ActionEvent ae) {
        Object source = ae.getSource();

        if (source == bBack) {
            new WelcomePage();
            dispose();
        } else if (source == bLoad) {
            handleLoadBill();
        } else if (source == bCommitUpdate) {
            handleCommitUpdate();
        } else if (source == bAddNewItem) {
            handleAddNewItem();
        }
    }
    
    // --- Business Logic: Load Bill ---
    
    private void handleLoadBill() {
        String billIdText = tBillId.getText().trim();
        if (billIdText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter Bill ID", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            loadedBillId = Integer.parseInt(billIdText);
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Bill ID must be a number", "Input Error", JOptionPane.ERROR_MESSAGE);
            loadedBillId = -1;
            return;
        }

        // Reset UI/State
        tCustId.setText(""); tDate.setText(""); tTotal.setText("0.00");
        itemModel.setRowCount(0); // Clear the table
        originalQuantities.clear();
        originalBillTotal = 0.0f;
        
        // SQL queries
        String sqlSale = "SELECT cust_id, date, total_amount FROM sale WHERE bill_id=?";
        String sqlBill = "SELECT medicine, quantity, price FROM bill WHERE bill_id=?";
        
        try (Connection con = DriverManager.getConnection(DatabaseConfig.URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
             PreparedStatement pstSale = con.prepareStatement(sqlSale);
             PreparedStatement pstBill = con.prepareStatement(sqlBill)) {
            
            // 1. Load Header
            pstSale.setInt(1, loadedBillId);
            try (ResultSet rsSale = pstSale.executeQuery()) {
                if (rsSale.next()) {
                    tCustId.setText(rsSale.getString("cust_id"));
                    tDate.setText(rsSale.getString("date"));
                    originalBillTotal = rsSale.getFloat("total_amount");
                    tTotal.setText(String.format("%.2f", originalBillTotal));
                } else {
                    JOptionPane.showMessageDialog(this, "Bill not found!", "Error", JOptionPane.ERROR_MESSAGE);
                    loadedBillId = -1;
                    return;
                }
            }
            
            // 2. Load Items (These are the medicines currently on the bill)
            pstBill.setInt(1, loadedBillId);
            try (ResultSet rsBill = pstBill.executeQuery()) {
                while (rsBill.next()) {
                    String name = rsBill.getString("medicine");
                    int qty = rsBill.getInt("quantity");
                    float price = rsBill.getFloat("price");
                    float amount = qty * price;
                    
                    // Add item to table
                    itemModel.addRow(new Object[]{name, price, qty, amount}); 
                    
                    // Store original quantity for tracking changes
                    originalQuantities.put(name, qty);
                }
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading bill: " + e.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
            loadedBillId = -1;
        }
    }
    
    // --- Business Logic: Add New Item to Table ---
    
    private void handleAddNewItem() {
        if (loadedBillId == -1) {
            JOptionPane.showMessageDialog(this, "Load a bill first.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String medName = (String) cbNewMedicine.getSelectedItem();
        String qtyText = tNewQuantity.getText().trim();
        
        if (medName == null || qtyText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a medicine and enter a quantity.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int newQty;
        try {
            newQty = Integer.parseInt(qtyText);
            if (newQty <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Quantity must be a positive integer.", "Validation", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        MedicineStock stock = stockMap.get(medName);
        if (stock == null) {
            JOptionPane.showMessageDialog(this, "Stock data missing for " + medName, "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Check stock availability based on current stock (not considering the bill's existing quantity yet, as this is just adding to the UI)
        if (newQty > stock.stock) {
             JOptionPane.showMessageDialog(this, "Insufficient stock. Only " + stock.stock + " available.", "Stock Error", JOptionPane.ERROR_MESSAGE);
             return;
        }
        
        // Find if the item already exists in the table
        int existingRow = -1;
        for (int i = 0; i < itemModel.getRowCount(); i++) {
            if (itemModel.getValueAt(i, 0).equals(medName)) {
                existingRow = i;
                break;
            }
        }
        
        if (existingRow != -1) {
            // Item exists: Add the new quantity to the existing quantity in the table
            int currentQty = (Integer) itemModel.getValueAt(existingRow, 2);
            itemModel.setValueAt(currentQty + newQty, existingRow, 2);
        } else {
            // Item is new: Add a new row
            float price = stock.price;
            float amount = newQty * price;
            itemModel.addRow(new Object[]{medName, price, newQty, amount});
            // Mark new item's original quantity as 0
            originalQuantities.put(medName, 0); 
        }
        
        tNewQuantity.setText("");
        updateBillTotalFromTable();
    }
    
    // --- Business Logic: Commit All Changes Transactionally ---
    
    private void handleCommitUpdate() {
        if (loadedBillId == -1) {
            JOptionPane.showMessageDialog(this, "Please load a bill before attempting to update.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
                "Confirm update to Bill ID " + loadedBillId + "? This will adjust stock and total.", 
                "Confirm Update", 
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        // --- Start Transaction ---
        Connection con = null;
        try {
            con = DriverManager.getConnection(DatabaseConfig.URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
            con.setAutoCommit(false); 

            float newTotal = Float.parseFloat(tTotal.getText());

            // Prepare statements for batched execution
            String sqlUpdateStock = "UPDATE medicine SET quantity = quantity - ? WHERE Med_id = ?";
            PreparedStatement pstStock = con.prepareStatement(sqlUpdateStock);
            
            String sqlInsertBill = "INSERT INTO bill (bill_id, medicine, quantity, price) VALUES (?, ?, ?, ?) ON DUPLICATE KEY UPDATE quantity=VALUES(quantity)";
            PreparedStatement pstInsertBill = con.prepareStatement(sqlInsertBill);
            
            String sqlDeleteBill = "DELETE FROM bill WHERE bill_id=? AND medicine=?";
            PreparedStatement pstDeleteBill = con.prepareStatement(sqlDeleteBill);
            
            
            // Map to track all items currently in the table
            HashMap<String, Integer> currentItems = new HashMap<>(); 
            
            for (int i = 0; i < itemModel.getRowCount(); i++) {
                String medName = (String) itemModel.getValueAt(i, 0);
                int newQty = Integer.parseInt(itemModel.getValueAt(i, 2).toString());
                float price = Float.parseFloat(itemModel.getValueAt(i, 1).toString());
                
                currentItems.put(medName, newQty);
                
                MedicineStock stockInfo = stockMap.get(medName);
                if (stockInfo == null) throw new SQLException("Stock information missing for item: " + medName);

                int originalQty = originalQuantities.getOrDefault(medName, 0);
                int qtyDifference = newQty - originalQty; // +ve if increased (stock decreases), -ve if decreased (stock increases)
                
                // A. Check Stock (only needed for increases)
                if (qtyDifference > 0 && stockInfo.stock < qtyDifference) {
                    throw new SQLException("Insufficient stock for " + medName + ". Available: " + stockInfo.stock + ", Needed: " + qtyDifference);
                }
                
                // B. Update Stock (Subtracts quantityDifference from stock)
                pstStock.setInt(1, qtyDifference);
                pstStock.setInt(2, stockInfo.medId);
                pstStock.addBatch();

                // C. Insert or Update Bill Line Item (Use INSERT... ON DUPLICATE KEY UPDATE)
                pstInsertBill.setInt(1, loadedBillId);
                pstInsertBill.setString(2, medName);
                pstInsertBill.setInt(3, newQty);
                pstInsertBill.setFloat(4, price);
                pstInsertBill.addBatch();
            }
            
            // D. Delete Items (Check if any original items were removed from the table)
            for (String originalMed : originalQuantities.keySet()) {
                if (!currentItems.containsKey(originalMed) && originalQuantities.get(originalMed) > 0) {
                    // This item was in the original bill but is NOT in the current table (was deleted)
                    
                    // Restore stock for deleted item (qty difference is 0 - originalQty)
                    MedicineStock stockInfo = stockMap.get(originalMed);
                    if (stockInfo == null) throw new SQLException("Stock information missing for deleted item: " + originalMed);

                    pstStock.setInt(1, -originalQuantities.get(originalMed)); // Restore stock: -(-originalQty) = +originalQty
                    pstStock.setInt(2, stockInfo.medId);
                    pstStock.addBatch();
                    
                    // Delete item from bill table
                    pstDeleteBill.setInt(1, loadedBillId);
                    pstDeleteBill.setString(2, originalMed);
                    pstDeleteBill.addBatch();
                }
            }
            
            // Execute all batched statements
            pstStock.executeBatch();
            pstInsertBill.executeBatch();
            pstDeleteBill.executeBatch();
            
            // 2. Update Sale Header (Total Amount)
            String sqlUpdateSaleTotal = "UPDATE sale SET total_amount=? WHERE bill_id=?";
            PreparedStatement pstSale = con.prepareStatement(sqlUpdateSaleTotal);
            pstSale.setFloat(1, newTotal);
            pstSale.setInt(2, loadedBillId);
            pstSale.executeUpdate();

            // 3. Commit
            con.commit(); 
            
            // Close prepared statements
            pstStock.close();
            pstInsertBill.close();
            pstDeleteBill.close();
            pstSale.close();

            JOptionPane.showMessageDialog(this, "Bill successfully updated! New Total: ₹" + String.format("%.2f", newTotal), "Success", JOptionPane.INFORMATION_MESSAGE);
            
            // Navigate back to Welcome Page
            new WelcomePage();
            dispose(); 

        } catch (SQLException sqle) {
            try {
                if (con != null) con.rollback();
            } catch (SQLException rollbackEx) {
                System.err.println("Rollback failed: " + rollbackEx.getMessage());
            }
            JOptionPane.showMessageDialog(this, "Update failed. Stock and bill records were NOT updated. Error: " + sqle.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
            sqle.printStackTrace();
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Please ensure all quantity fields contain valid numbers.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
             JOptionPane.showMessageDialog(this, "An unexpected error occurred: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
             e.printStackTrace();
        } finally {
            try {
                if (con != null) con.close();
            } catch (SQLException closeEx) {
                System.err.println("Connection closing failed: " + closeEx.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        try {
            // Ensure the JDBC driver is loaded
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "MySQL JDBC Driver not found! Check your classpath.", "Driver Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        SwingUtilities.invokeLater(() -> new UpdateBillForm());
    }
}