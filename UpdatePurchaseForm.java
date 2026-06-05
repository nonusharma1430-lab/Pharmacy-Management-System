import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.sql.*;

// Placeholder class to ensure the code compiles
class WelcomePage extends JFrame {}

class UpdatePurchaseForm extends JFrame implements ActionListener {

    // --- Configuration (Update these with your actual DB details) ---
    private static class DatabaseConfig {
        static final String URL = "jdbc:mysql://localhost:3306/project";
        static final String USER = "root";
        static final String PASSWORD = "VISHESHSQL123@";
    }

    // --- Purchase Header Components ---
    JLabel lPurchaseId, lSuppId, lDate, lAmount;
    JTextField tPurchaseId, tSuppId, tDate, tAmount;
    JButton bLoad, bUpdateHeader, bUpdateItem, bBack;
    
    // --- Purchase Items Components (Table) ---
    JLabel lItemsTitle;
    JTable itemsTable;
    DefaultTableModel itemsTableModel;

    // --- Selected Item Update Fields ---
    JLabel lSelectedItem, lNewQuantity, lNewCost;
    JTextField tSelectedItem, tNewQuantity, tNewCost;
    
    // --- State Variable ---
    private int currentlyLoadedPurchaseId = -1;

    UpdatePurchaseForm() {
        setTitle("Update Purchase Bill (Header & Items)");
        setSize(850, 750); 
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(194, 217, 220));

        // ------------------ 1. PURCHASE HEADER SECTION ------------------
        int yOffset = 40;
        
        lPurchaseId = new JLabel("Purchase ID:");
        lPurchaseId.setBounds(50, yOffset, 100, 25);
        add(lPurchaseId);

        tPurchaseId = new JTextField();
        tPurchaseId.setBounds(180, yOffset, 150, 25);
        add(tPurchaseId);

        lSuppId = new JLabel("Supplier ID:");
        lSuppId.setBounds(50, yOffset + 40, 100, 25);
        add(lSuppId);

        tSuppId = new JTextField();
        tSuppId.setBounds(180, yOffset + 40, 150, 25);
        add(tSuppId);

        lDate = new JLabel("Date (YYYY-MM-DD):");
        lDate.setBounds(50, yOffset + 80, 150, 25);
        add(lDate);

        tDate = new JTextField();
        tDate.setBounds(180, yOffset + 80, 150, 25);
        add(tDate);

        lAmount = new JLabel("Total Amount:");
        lAmount.setBounds(50, yOffset + 120, 100, 25);
        add(lAmount);

        tAmount = new JTextField();
        tAmount.setBounds(180, yOffset + 120, 150, 25);
        tAmount.setEditable(false); // Amount should be calculated, not directly editable
        add(tAmount);

        // --- Header Buttons ---
        bLoad = new JButton("Load Details");
        bLoad.setBounds(30, yOffset + 180, 120, 30);
        add(bLoad);

        bUpdateHeader = new JButton("Update Header");
        bUpdateHeader.setBounds(160, yOffset + 180, 140, 30);
        add(bUpdateHeader);
        
        bBack = new JButton("Back");
        bBack.setBounds(320, yOffset + 180, 90, 30);
        add(bBack);


        // ------------------ 2. PURCHASE ITEMS SECTION (TABLE) ------------------
        int tableYOffset = 260; 

        lItemsTitle = new JLabel("Purchase Items:");
        lItemsTitle.setFont(new Font("Arial", Font.BOLD, 14));
        lItemsTitle.setBounds(50, tableYOffset, 400, 25);
        add(lItemsTitle);

        // Define column headers and types
        itemsTableModel = new DefaultTableModel(
            new Object[]{"Select", "Item ID", "Med ID", "Quantity", "Cost Price (₹)"}, 0) {
            
            // Override getColumnClass to make the first column a Boolean (checkbox)
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return Boolean.class;
                return super.getColumnClass(columnIndex);
            }
            
            // Only allow the checkbox column (index 0) to be editable
            @Override
            public boolean isCellEditable(int row, int column) {
                 return column == 0;
            }
        };
        
        itemsTable = new JTable(itemsTableModel);
        // The selection listener is now primarily used to populate the update fields on click
        itemsTable.getSelectionModel().addListSelectionListener(new ItemRowSelectionListener()); 
        
        // Make the checkbox column narrow
        itemsTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        itemsTable.getColumnModel().getColumn(0).setMaxWidth(50);

        JScrollPane itemsScroll = new JScrollPane(itemsTable); 
        itemsScroll.setBounds(50, tableYOffset + 30, 750, 300); 
        add(itemsScroll);

        // ------------------ 3. ITEM UPDATE SECTION ------------------
        int itemUpdateY = tableYOffset + 350;
        
        lSelectedItem = new JLabel("Selected Item ID:");
        lSelectedItem.setBounds(50, itemUpdateY, 120, 25); add(lSelectedItem);
        tSelectedItem = new JTextField();
        tSelectedItem.setBounds(170, itemUpdateY, 80, 25); tSelectedItem.setEditable(false); add(tSelectedItem);

        lNewQuantity = new JLabel("New Qty:");
        lNewQuantity.setBounds(270, itemUpdateY, 80, 25); add(lNewQuantity);
        tNewQuantity = new JTextField();
        tNewQuantity.setBounds(350, itemUpdateY, 80, 25); add(tNewQuantity);

        lNewCost = new JLabel("New Price:");
        lNewCost.setBounds(450, itemUpdateY, 80, 25); add(lNewCost);
        tNewCost = new JTextField();
        tNewCost.setBounds(530, itemUpdateY, 80, 25); add(tNewCost);
        
        bUpdateItem = new JButton("Update Selected Item");
        bUpdateItem.setBounds(630, itemUpdateY, 180, 25);
        add(bUpdateItem);


        // --- Listeners ---
        bLoad.addActionListener(this);
        bUpdateHeader.addActionListener(this);
        bUpdateItem.addActionListener(this);
        bBack.addActionListener(this);

        setVisible(true);
    }

    public void actionPerformed(ActionEvent ae) {
        
        if (ae.getSource() == bBack){
            new WelcomePage();
            dispose();
            return;
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Database Driver not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try (Connection con = DriverManager.getConnection(
                DatabaseConfig.URL,
                DatabaseConfig.USER,
                DatabaseConfig.PASSWORD
            )) {
            
            if (ae.getSource() == bLoad) {
                handleLoadPurchase(con);
            } else if (ae.getSource() == bUpdateHeader) {
                handleUpdatePurchaseHeader(con);
            } else if (ae.getSource() == bUpdateItem) {
                handleUpdatePurchaseItem(con);
            }

        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(null, "Please enter valid numeric values for ID, Quantity, and Price.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database Error: " + e.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "An unexpected error occurred: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    // --- Business Logic for Loading Purchase Data ---
    private void handleLoadPurchase(Connection con) throws SQLException, NumberFormatException {
        String idText = tPurchaseId.getText().trim();
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please enter a Purchase ID to load.", "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = Integer.parseInt(idText);

        // 1. Load Purchase Header
        String sqlHeader = "SELECT supp_id, date, amount FROM purchase WHERE purchase_id=?";
        
        try (PreparedStatement pstHeader = con.prepareStatement(sqlHeader)) {
            pstHeader.setInt(1, id);
            
            try (ResultSet rs = pstHeader.executeQuery()) {
                if (rs.next()) {
                    tSuppId.setText(rs.getString("supp_id")); 
                    tDate.setText(rs.getString("date"));
                    tAmount.setText(String.format("%.2f", rs.getFloat("amount")));
                    this.currentlyLoadedPurchaseId = id;
                    
                    // 2. Load the associated items
                    loadPurchaseItems(con, id);
                } else {
                    JOptionPane.showMessageDialog(null, "Purchase ID " + id + " not found!");
                    clearHeaderFields();
                    itemsTableModel.setRowCount(0); 
                    lItemsTitle.setText("Purchase Items:");
                    this.currentlyLoadedPurchaseId = -1;
                }
            }
        }
    }

    // --- Business Logic for Loading Items ---
    private void loadPurchaseItems(Connection con, int purchaseId) throws SQLException {
        itemsTableModel.setRowCount(0); 
        clearItemFields(); // Clear item selection fields
        lItemsTitle.setText("Loading items for Purchase ID: " + purchaseId + "...");

        String sqlItems = "SELECT item_id, med_id, quantity, cost_price FROM purchase_items WHERE purchase_id = ?";

        try (PreparedStatement pstItems = con.prepareStatement(sqlItems)) {
            pstItems.setInt(1, purchaseId);
            
            try (ResultSet rsItems = pstItems.executeQuery()) {
                int rowCount = 0;
                while (rsItems.next()) {
                    int itemId = rsItems.getInt("item_id");
                    int medId = rsItems.getInt("med_id");
                    int quantity = rsItems.getInt("quantity");
                    float costPrice = rsItems.getFloat("cost_price");
                    
                    // Add the checkbox state (false by default) as the first column
                    itemsTableModel.addRow(new Object[]{
                        Boolean.FALSE, 
                        itemId, 
                        medId, 
                        quantity, 
                        String.format("%.2f", costPrice)
                    });
                    rowCount++;
                }
                
                lItemsTitle.setText("Purchase Items (" + rowCount + " records) for ID: " + purchaseId);
            }
        }
    }

    // --- Business Logic for Updating Purchase Header ---
    private void handleUpdatePurchaseHeader(Connection con) throws SQLException, NumberFormatException {
        if (currentlyLoadedPurchaseId == -1) {
             JOptionPane.showMessageDialog(null, "Please load a Purchase ID first.", "Error", JOptionPane.ERROR_MESSAGE);
             return;
        }
        
        String suppId = tSuppId.getText().trim(); 
        String date = tDate.getText().trim();

        if (suppId.isEmpty() || date.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Supplier ID and Date must be filled to update the header.", "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "UPDATE purchase SET supp_id=?, date=? WHERE purchase_id=?";
        
        try (PreparedStatement pst = con.prepareStatement(sql)) {
            
            pst.setString(1, suppId); 
            pst.setString(2, date);
            pst.setInt(3, currentlyLoadedPurchaseId);

            int x = pst.executeUpdate();
            
            if (x > 0) {
                JOptionPane.showMessageDialog(null, "Purchase Header ID " + currentlyLoadedPurchaseId + " updated successfully!");
            } else {
                JOptionPane.showMessageDialog(null, "Header not found or no changes made!");
            }
        }
    }
    
    // --- Business Logic for Updating Purchase Item (Selected Row) ---
    private void handleUpdatePurchaseItem(Connection con) throws SQLException, NumberFormatException {
        if (currentlyLoadedPurchaseId == -1 || tSelectedItem.getText().isEmpty()) {
             JOptionPane.showMessageDialog(null, "Please load a Purchase and select an Item to update (by clicking a row).", "Error", JOptionPane.ERROR_MESSAGE);
             return;
        }
        
        int itemId = Integer.parseInt(tSelectedItem.getText().trim());
        String qtyText = tNewQuantity.getText().trim();
        String costText = tNewCost.getText().trim();

        if (qtyText.isEmpty() || costText.isEmpty()) {
            JOptionPane.showMessageDialog(null, "New Quantity and New Price must be entered to update the item.", "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int newQty = Integer.parseInt(qtyText);
        float newCost = Float.parseFloat(costText);
        
        if (newQty <= 0 || newCost <= 0) {
            JOptionPane.showMessageDialog(null, "Quantity and Cost Price must be positive.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String sqlUpdateItem = "UPDATE purchase_items SET quantity=?, cost_price=? WHERE item_id=? AND purchase_id=?";
        
        try (PreparedStatement pstItem = con.prepareStatement(sqlUpdateItem)) {
            
            pstItem.setInt(1, newQty);
            pstItem.setFloat(2, newCost);
            pstItem.setInt(3, itemId);
            pstItem.setInt(4, currentlyLoadedPurchaseId);

            int x = pstItem.executeUpdate();
            
            if (x > 0) {
                JOptionPane.showMessageDialog(null, 
                    "Item ID " + itemId + " updated successfully! \n" +
                    "(Stock and Total amount must be manually recalculated/updated.)", 
                    "Item Update Success", 
                    JOptionPane.WARNING_MESSAGE);
                
                loadPurchaseItems(con, currentlyLoadedPurchaseId);
                clearItemFields();

            } else {
                JOptionPane.showMessageDialog(null, "Item not found or no changes made in purchase_items!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // --- Helper Methods ---
    private void clearHeaderFields() {
        tSuppId.setText("");
        tDate.setText("");
        tAmount.setText("");
        clearItemFields();
    }
    
    private void clearItemFields() {
        tSelectedItem.setText("");
        tNewQuantity.setText("");
        tNewCost.setText("");
    }
    
    // --- Inner Class for Handling Table Row Clicks (Selection) ---
    private class ItemRowSelectionListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting() && itemsTable.getSelectedRow() != -1) {
                int selectedRow = itemsTable.getSelectedRow();
                
                // NOTE: Indexes adjusted for the new 'Select' column (column 0)
                
                // Item ID is now in column 1
                Object itemId = itemsTableModel.getValueAt(selectedRow, 1);
                // Quantity is now in column 3
                Object quantity = itemsTableModel.getValueAt(selectedRow, 3);
                // Cost Price is now in column 4
                Object costPrice = itemsTableModel.getValueAt(selectedRow, 4);
                
                tSelectedItem.setText(itemId.toString());
                tNewQuantity.setText(quantity.toString());
                
                // Clear any non-numeric formatting from costPrice
                try {
                    float price = Float.parseFloat(costPrice.toString().replace("₹", "").trim());
                    tNewCost.setText(String.valueOf(price));
                } catch (NumberFormatException ex) {
                    tNewCost.setText(costPrice.toString());
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new UpdatePurchaseForm());
    }
}