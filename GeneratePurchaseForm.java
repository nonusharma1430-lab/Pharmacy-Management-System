import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.HashMap;

class GeneratePurchaseForm extends JFrame implements ActionListener {

    // --- Configuration ---
    private static class DatabaseConfig {
        static final String URL = "jdbc:mysql://localhost:3306/project";
        static final String USER = "root"; 
        static final String PASSWORD = "VISHESHSQL123@"; 
    }
    
    // Maps medicine name to its ID
    private HashMap<String, Integer> medicineIdMap = new HashMap<>();

    // --- UI Components ---
    // ADDED PURCHASE ID FIELDS HERE
    JLabel lPurchaseId, lSuppId, lDate, lNewMed, lNewQty, lNewCost, lNewCompany, lNewExpDate;
    JTextField tPurchaseId, tSuppId, tDate, tTotalAmount;
    
    JComboBox<String> cbNewMedicine;
    JTextField tNewQuantity, tNewCost, tNewCompany, tNewExpDate; 
    
    JButton bAddNewItem, bCommitPurchase, bBack;
    JTable itemTable;
    DefaultTableModel itemModel;

    GeneratePurchaseForm() {
        setTitle("Generate Purchase Order");
        setSize(1000, 700); 
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(194, 217, 220));
        
        loadMedicineNames();

        // --- Header Section (Purchase ID, Supplier, Date, Total) ---
        
        // ** NEW PURCHASE ID FIELD **
        lPurchaseId = new JLabel("Purchase ID (Manual):"); 
        lPurchaseId.setBounds(50, 20, 180, 25); add(lPurchaseId);
        tPurchaseId = new JTextField(); 
        tPurchaseId.setBounds(230, 20, 100, 25); add(tPurchaseId);

        lSuppId = new JLabel("Supplier ID (e.g., SUP001):"); lSuppId.setBounds(350, 20, 180, 25); add(lSuppId);
        tSuppId = new JTextField(); tSuppId.setBounds(500, 20, 100, 25); add(tSuppId);
        
        lDate = new JLabel("Date (YYYY-MM-DD):"); lDate.setBounds(620, 20, 150, 25); add(lDate);
        tDate = new JTextField(); tDate.setBounds(770, 20, 150, 25); 
        // Set default date to today
        tDate.setText(java.time.LocalDate.now().toString()); 
        add(tDate);
        
        // --- Section for Adding New Items ---
        JPanel pNewItem = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        pNewItem.setBorder(BorderFactory.createTitledBorder("Add Medicine Details (Price is the Cost Price)"));
        // Adjusted vertical position due to new field
        pNewItem.setBounds(50, 60, 900, 80); 
        
        lNewMed = new JLabel("Medicine:"); pNewItem.add(lNewMed);
        cbNewMedicine = new JComboBox<>(); 
        cbNewMedicine.setPreferredSize(new Dimension(150, 25)); pNewItem.add(cbNewMedicine);
        
        lNewQty = new JLabel("Qty:"); pNewItem.add(lNewQty);
        tNewQuantity = new JTextField(5); tNewQuantity.setPreferredSize(new Dimension(60, 25)); pNewItem.add(tNewQuantity);
        
        lNewCost = new JLabel("Price:"); pNewItem.add(lNewCost); 
        tNewCost = new JTextField(5); tNewCost.setPreferredSize(new Dimension(80, 25)); pNewItem.add(tNewCost);
        
        lNewCompany = new JLabel("Company:"); pNewItem.add(lNewCompany);
        tNewCompany = new JTextField(10); tNewCompany.setPreferredSize(new Dimension(120, 25)); pNewItem.add(tNewCompany);
        
        lNewExpDate = new JLabel("Exp. Date:"); pNewItem.add(lNewExpDate);
        tNewExpDate = new JTextField(8); tNewExpDate.setPreferredSize(new Dimension(100, 25)); pNewItem.add(tNewExpDate);
        
        bAddNewItem = new JButton("Add Item"); pNewItem.add(bAddNewItem);
        add(pNewItem);

        // Populate the new medicine dropdown
        for (String medName : medicineIdMap.keySet()) {
            cbNewMedicine.addItem(medName);
        }
        
        // --- Table for Purchase Items ---
        itemModel = new DefaultTableModel(
            new Object[]{"Medicine", "Qty", "Price (₹)", "Company", "Exp. Date", "Amount (₹)"}, 0) {
             @Override
             public boolean isCellEditable(int row, int column) {
                 return false; 
             }
        };
        itemTable = new JTable(itemModel);
        JScrollPane scroll = new JScrollPane(itemTable); 
        scroll.setBounds(50, 150, 900, 350); 
        add(scroll);
        
        // --- Footer Buttons and Total ---
        JLabel lTotal = new JLabel("PURCHASE TOTAL (₹):"); 
        lTotal.setBounds(700, 520, 150, 25); lTotal.setFont(new Font("Arial", Font.BOLD, 14)); add(lTotal);
        
        tTotalAmount = new JTextField("0.00"); 
        tTotalAmount.setBounds(850, 520, 100, 25); tTotalAmount.setEditable(false); add(tTotalAmount);

        bCommitPurchase = new JButton("Generate & Commit"); 
        bCommitPurchase.setBounds(350, 570, 180, 30); add(bCommitPurchase);
        
        bBack = new JButton("Back"); bBack.setBounds(550, 570, 100, 30); add(bBack);

        // Listeners
        bAddNewItem.addActionListener(this);
        bCommitPurchase.addActionListener(this);
        bBack.addActionListener(this);
        
        setVisible(true);
    }
    
    // --- Database Helper: Load Medicine Names ---
    private void loadMedicineNames() {
        String sql = "SELECT Med_id, med_name FROM medicine";
        try (Connection con = DriverManager.getConnection(DatabaseConfig.URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            
            while (rs.next()) {
                medicineIdMap.put(rs.getString("med_name"), rs.getInt("Med_id"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading medicine list: " + e.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // --- Database Helper: Validate Supplier ID ---
    private boolean isSupplierValid(String suppId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM supplier WHERE supp_id = ?";
        try (Connection con = DriverManager.getConnection(DatabaseConfig.URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
             PreparedStatement pst = con.prepareStatement(sql)) {
            
            pst.setString(1, suppId);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false; 
    }
    
    // --- UI Helper: Recalculate and update the purchase total ---
    private void updatePurchaseTotal() {
        float total = 0.0f;
        for (int i = 0; i < itemModel.getRowCount(); i++) {
            try {
                // Total amount is in column 5 (index 5)
                float itemAmount = Float.parseFloat(itemModel.getValueAt(i, 5).toString()); 
                total += itemAmount;
            } catch (NumberFormatException ex) {
                tTotalAmount.setText("Error");
                return;
            }
        }
        tTotalAmount.setText(String.format("%.2f", total));
    }

    // --- Action Handler ---
    public void actionPerformed(ActionEvent ae) {
        Object source = ae.getSource();

        if (source == bBack) {
            // Placeholder for back action (e.g., return to main menu)
            // new WelcomePage();
            dispose();
        } else if (source == bAddNewItem) {
            handleAddNewItem();
        } else if (source == bCommitPurchase) {
            handleCommitPurchase();
        }
    }
    
    // --- Business Logic: Add New Item to Table ---
    private void handleAddNewItem() {
        String medName = (String) cbNewMedicine.getSelectedItem();
        String qtyText = tNewQuantity.getText().trim();
        String costText = tNewCost.getText().trim();
        String company = tNewCompany.getText().trim();
        String expDate = tNewExpDate.getText().trim();
        
        if (medName == null || qtyText.isEmpty() || costText.isEmpty() || company.isEmpty() || expDate.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All item fields (Med name, Qty, Price, Company, Exp. Date) must be filled.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int qty;
        float cost;
        try {
            qty = Integer.parseInt(qtyText);
            cost = Float.parseFloat(costText);
            if (qty <= 0 || cost <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Quantity and Price must be positive numbers.", "Validation", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Basic Date Format Check
        if (!expDate.matches("\\d{4}-\\d{2}-\\d{2}")) {
            JOptionPane.showMessageDialog(this, "Expiry Date must be in YYYY-MM-DD format.", "Validation", JOptionPane.ERROR_MESSAGE);
            return;
        }

        float amount = qty * cost;
        
        // Add row to table (medName, qty, cost, company, expDate, amount)
        itemModel.addRow(new Object[]{medName, qty, String.format("%.2f", cost), company, expDate, String.format("%.2f", amount)});
        
        // Clear input fields
        tNewQuantity.setText("");
        tNewCost.setText("");
        tNewCompany.setText("");
        tNewExpDate.setText("");
        updatePurchaseTotal();
    }
    
    // --- Business Logic: Commit Full Purchase Transaction ---
    private void handleCommitPurchase() {
        String purchaseIdText = tPurchaseId.getText().trim(); // NEW: Get manual ID
        String suppIdText = tSuppId.getText().trim(); 
        String date = tDate.getText().trim();
        float totalAmount;
        
        try {
            totalAmount = Float.parseFloat(tTotalAmount.getText());
        } catch (NumberFormatException e) {
              JOptionPane.showMessageDialog(this, "Invalid total amount. Cannot commit purchase.", "Error", JOptionPane.ERROR_MESSAGE);
              return;
        }

        if (purchaseIdText.isEmpty() || suppIdText.isEmpty() || itemModel.getRowCount() == 0 || totalAmount <= 0) {
            JOptionPane.showMessageDialog(this, "Please enter **Purchase ID**, **Supplier ID**, and add at least **one item**.", "Validation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int purchaseId;
        try {
            purchaseId = Integer.parseInt(purchaseIdText); // NEW: Convert ID to integer
            if (purchaseId <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Purchase ID must be a positive integer.", "Validation", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // --- Validation: Check if Supplier ID exists ---
        try {
            if (!isSupplierValid(suppIdText)) {
                JOptionPane.showMessageDialog(this, "Supplier ID '" + suppIdText + "' does not exist in the database.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "DB Error during Supplier ID validation: " + e.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
              "Commit Purchase ID " + purchaseId + " for ₹" + String.format("%.2f", totalAmount) + "? This will update stock and medicine prices.", 
              "Confirm Purchase", 
              JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        Connection con = null;
        try {
            con = DriverManager.getConnection(DatabaseConfig.URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
            con.setAutoCommit(false); // Start Transaction
            
            // 1. Insert into Purchase Header (purchase table)
            // MODIFIED SQL: We SUPPLY the purchase_id because AUTO_INCREMENT is off.
            String sqlInsertPurchase = "INSERT INTO purchase (purchase_id, supp_id, date, amount) VALUES (?, ?, ?, ?)";
            
            // IMPORTANT: Removed Statement.RETURN_GENERATED_KEYS because we supply the ID
            PreparedStatement pstPurchase = con.prepareStatement(sqlInsertPurchase); 
            
            pstPurchase.setInt(1, purchaseId);    // NEW: Set the manual Purchase ID
            pstPurchase.setString(2, suppIdText); // supp_id is VARCHAR
            pstPurchase.setString(3, date);
            pstPurchase.setFloat(4, totalAmount);
            pstPurchase.executeUpdate();
            
            // Since we manually set purchaseId, the code block to retrieve generated keys is removed.

            // 2. Insert into Purchase Items (purchase_items table - assumed to exist)
            String sqlInsertItems = "INSERT INTO purchase_items (purchase_id, med_id, quantity, cost_price) VALUES (?, ?, ?, ?)";
            PreparedStatement pstItems = con.prepareStatement(sqlInsertItems);

            // 3. Update Medicine Stock (medicine table)
            String sqlUpdateStock = "UPDATE medicine SET " + 
                                    "quantity = quantity + ?, " +
                                    "price = ?, " + // Note: You are setting 'price' to the cost price here.
                                    "company = ?, " + 
                                    "expire_date = ? " + 
                                    "WHERE Med_id = ?";
            PreparedStatement pstStock = con.prepareStatement(sqlUpdateStock);
            
            
            for (int i = 0; i < itemModel.getRowCount(); i++) {
                String medName = (String) itemModel.getValueAt(i, 0);
                int qty = Integer.parseInt(itemModel.getValueAt(i, 1).toString()); 
                float costPrice = Float.parseFloat(itemModel.getValueAt(i, 2).toString()); 
                String company = (String) itemModel.getValueAt(i, 3);
                String expDate = (String) itemModel.getValueAt(i, 4);
                
                Integer medId = medicineIdMap.get(medName);

                if (medId == null) throw new SQLException("Medicine ID not found for: " + medName); 

                // Batch for Item Insertion
                pstItems.setInt(1, purchaseId); // Use manual ID
                pstItems.setInt(2, medId);
                pstItems.setInt(3, qty);
                pstItems.setFloat(4, costPrice); 
                pstItems.addBatch();

                // Batch for Stock Update
                pstStock.setInt(1, qty);
                pstStock.setFloat(2, costPrice); 
                pstStock.setString(3, company);
                pstStock.setString(4, expDate);
                pstStock.setInt(5, medId);
                pstStock.addBatch();
            }
            
            // Execute all batched statements
            pstItems.executeBatch();
            pstStock.executeBatch();

            // 4. Commit Transaction
            con.commit(); 
            
            // Close prepared statements
            pstPurchase.close();
            pstItems.close();
            pstStock.close();

            JOptionPane.showMessageDialog(this, 
                "Purchase ID " + purchaseId + " recorded successfully. Stock updated.", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
            
            // new WelcomePage();
            dispose(); 

        } catch (SQLIntegrityConstraintViolationException constraintEx) {
            // Catch specific error for duplicate PK (e.g., if purchaseId is reused)
            try { if (con != null) con.rollback(); } catch (SQLException rollbackEx) { }
            JOptionPane.showMessageDialog(this, "Purchase ID " + purchaseIdText + " already exists. Please enter a unique ID.", "ID Conflict Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException sqle) {
            try {
                if (con != null) con.rollback(); // Rollback on error
            } catch (SQLException rollbackEx) {
                System.err.println("Rollback failed: " + rollbackEx.getMessage());
            }
            JOptionPane.showMessageDialog(this, "Purchase failed. Records and stock were NOT updated. Error: " + sqle.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
            sqle.printStackTrace();
        } catch (Exception e) {
             JOptionPane.showMessageDialog(this, "An unexpected error occurred: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
             e.printStackTrace();
        } finally {
            try {
                if (con != null) {
                    con.setAutoCommit(true); // Restore default
                    con.close();
                }
            } catch (SQLException closeEx) {
                System.err.println("Connection closing failed: " + closeEx.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "MySQL JDBC Driver not found! Ensure you have the Connector/J JAR file in your classpath.", "Driver Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        SwingUtilities.invokeLater(() -> new GeneratePurchaseForm());
    }
}