import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;

// Placeholder class to ensure the code compiles
class WelcomePage extends JFrame {}

class DeletePurchaseForm extends JFrame implements ActionListener {

    // --- Configuration (REPLACE THESE WITH YOUR ACTUAL DB DETAILS) ---
    private static class DatabaseConfig {
        static final String URL = "jdbc:mysql://localhost:3306/project";
        static final String USER = "root";
        static final String PASSWORD = "VISHESHSQL123@";
    }

    // --- UI Components ---
    JLabel lPurchaseId, lSuppId, lAmount;
    JTextField tPurchaseId, tSuppId, tAmount;
    // Added bLoad
    JButton bLoad, bDelete, bBack;
    
    DeletePurchaseForm() {
        setTitle("Delete Purchase Record");
        setSize(450, 350); // Increased height for better button spacing
        setLayout(null);
        setLocationRelativeTo(null); // Center the frame
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(194, 217, 220));
        
        int yOffset = 40;

        // --- Labels ---
        lPurchaseId = new JLabel("Purchase ID:");
        lPurchaseId.setBounds(50, yOffset, 150, 30);
        add(lPurchaseId);

        lSuppId = new JLabel("Supplier ID:");
        lSuppId.setBounds(50, yOffset + 50, 150, 30);
        add(lSuppId);
        
        lAmount = new JLabel("Total Amount:");
        lAmount.setBounds(50, yOffset + 100, 150, 30);
        add(lAmount);


        // --- Text fields ---
        tPurchaseId = new JTextField();
        tPurchaseId.setBounds(200, yOffset, 150, 30);
        add(tPurchaseId);

        tSuppId = new JTextField();
        tSuppId.setBounds(200, yOffset + 50, 150, 30);
        tSuppId.setEditable(false); // Loaded only
        add(tSuppId);
        
        tAmount = new JTextField();
        tAmount.setBounds(200, yOffset + 100, 150, 30);
        tAmount.setEditable(false); // Loaded only
        add(tAmount);

        // --- Buttons ---
        // Load button placed next to Purchase ID input
        bLoad = new JButton("Load");
        bLoad.setBounds(360, yOffset, 70, 30);
        add(bLoad);
        
        // Delete button (Main action)
        bDelete = new JButton("Delete Purchase");
        bDelete.setBounds(50, yOffset + 180, 150, 30);
        add(bDelete);

        // Back button
        bBack = new JButton("Back");
        bBack.setBounds(250, yOffset + 180, 150, 30);
        add(bBack);

        // Actions
        bLoad.addActionListener(this);
        bDelete.addActionListener(this);
        bBack.addActionListener(this);

        setVisible(true);
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == bBack) {
            new WelcomePage();
            dispose();
            return;
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this, "Database Driver not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (ae.getSource() == bLoad) {
            handleLoadPurchase();
        } 
        
        else if (ae.getSource() == bDelete) {
            handleDeletePurchase();
        }
    }

    private void handleLoadPurchase() {
        String idText = tPurchaseId.getText().trim();
        // Clear dependent fields
        tSuppId.setText("");
        tAmount.setText("");

        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter Purchase ID to load details.", "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try (Connection con = DriverManager.getConnection(
                DatabaseConfig.URL,
                DatabaseConfig.USER,
                DatabaseConfig.PASSWORD
            )) {
            
            int purchaseId = Integer.parseInt(idText);
            
            // Query to fetch Supplier ID and Amount
            String sql = "SELECT supp_id, amount FROM purchase WHERE purchase_id=?";
            
            try (PreparedStatement pst = con.prepareStatement(sql)) {
                pst.setInt(1, purchaseId);
                
                try (ResultSet rs = pst.executeQuery()) {
                    if (rs.next()) {
                        tSuppId.setText(rs.getString("supp_id"));
                        tAmount.setText(String.format("%.2f", rs.getFloat("amount")));
                        JOptionPane.showMessageDialog(this, "Purchase details loaded. Review before deleting.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "Purchase ID " + purchaseId + " not found.", "Not Found", JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
            
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Purchase ID must be a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database Error during Load: " + e.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void handleDeletePurchase() {
        String idText = tPurchaseId.getText().trim();
        
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter Purchase ID to delete.", "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Check if details were loaded or manually verified
        if (tSuppId.getText().trim().isEmpty() || tAmount.getText().trim().isEmpty()) {
             int confirmLoad = JOptionPane.showConfirmDialog(this, 
                        "Details not loaded or fields are empty. Proceed with deletion of ID " + idText + " anyway?", 
                        "Warning: Unverified Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
             
             if (confirmLoad != JOptionPane.YES_OPTION) {
                 return;
             }
        }

        try (Connection con = DriverManager.getConnection(
                DatabaseConfig.URL,
                DatabaseConfig.USER,
                DatabaseConfig.PASSWORD
            )) {
            
            int purchaseId = Integer.parseInt(idText);
            
            // NOTE: We assume the database has CASCADE DELETE set up to handle 'purchase_items'.
            // For a robust system, inventory reversal logic (stock reduction) must be performed here.
            String sql = "DELETE FROM purchase WHERE purchase_id=?";
            
            try (PreparedStatement pst = con.prepareStatement(sql)) {
                pst.setInt(1, purchaseId);

                int confirm = JOptionPane.showConfirmDialog(this, 
                        "Are you sure you want to delete Purchase ID " + purchaseId + "?", 
                        "Confirm Delete", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    int x = pst.executeUpdate();
                    if (x > 0) {
                        JOptionPane.showMessageDialog(this, "Purchase deleted successfully!");
                        tPurchaseId.setText("");
                        tSuppId.setText("");
                        tAmount.setText("");
                        new WelcomePage();
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(this, "No matching purchase found (ID: " + purchaseId + ").", "Not Found", JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
            
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Purchase ID must be a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database Error during Delete: " + e.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DeletePurchaseForm());
    }
}