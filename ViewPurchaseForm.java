import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.util.Vector; 

// Placeholder class to allow the code to compile successfully
class WelcomePage extends JFrame {}

class ViewPurchaseForm extends JFrame implements ActionListener {

    // --- Configuration (Update these with your actual DB details) ---
    private static class DatabaseConfig {
        static final String URL = "jdbc:mysql://localhost:3306/project";
        static final String USER = "root";
        static final String PASSWORD = "VISHESHSQL123@";
    }

    // --- UI Components ---
    JLabel lTitle;
    JTable purchaseTable;
    DefaultTableModel tableModel;
    JButton bLoad, bBack;
    
    // --- State Variable ---
    private static final String TABLE_NAME = "purchase";

    ViewPurchaseForm() {
        setTitle("View All Purchase Bills");
        setSize(800, 500);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(194, 217, 220));

        lTitle = new JLabel("All Purchases Bill Records");
        lTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lTitle.setBounds(50, 20, 300, 30);
        add(lTitle);
        
        // --- Table Setup ---
        tableModel = new DefaultTableModel(new Object[]{"Purchase ID", "Supplier ID", "Date", "Total Amount (₹)"}, 0) {
             @Override
             public boolean isCellEditable(int row, int column) {
                 return false; // All cells are read-only
             }
        };
        purchaseTable = new JTable(tableModel);
        JScrollPane scroll = new JScrollPane(purchaseTable); 
        scroll.setBounds(50, 60, 700, 350); 
        add(scroll);
        
        // --- Buttons ---
        bLoad = new JButton("Refresh/Load All"); bLoad.setBounds(250, 420, 150, 30); add(bLoad);
        bBack = new JButton("Back"); bBack.setBounds(420, 420, 100, 30); add(bBack);

        // Action listeners
        bLoad.addActionListener(this);
        bBack.addActionListener(this);

        setVisible(true);
        
        // Load data automatically upon opening
        loadAllPurchases();
    }
    
    // --- Action Handler ---
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == bBack) {
            new WelcomePage(); // Navigate to WelcomePage
            dispose();
        } else if (ae.getSource() == bLoad) {
            loadAllPurchases();
        }
    }

    // --- Business Logic: Load All Purchases ---
    private void loadAllPurchases() {
        // Clear previous data
        tableModel.setRowCount(0);

        String sql = "SELECT purchase_id, supp_id, date, amount FROM " + TABLE_NAME + " ORDER BY date DESC";

        try (Connection con = DriverManager.getConnection(DatabaseConfig.URL, DatabaseConfig.USER, DatabaseConfig.PASSWORD);
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            int rowCount = 0;
            while (rs.next()) {
                
                // *** CORRECTION 1: Use index (1) for Purchase ID to ensure reliability ***
                // purchase_id is the first column in the SELECT statement.
                int id = rs.getInt(1); 
                
                // *** CORRECTION 2: Use getString() for supp_id (as confirmed VARCHAR) ***
                String suppId = rs.getString(2); // supp_id is the second column
                
                String date = rs.getString(3); // date is the third column
                float amount = rs.getFloat(4); // amount is the fourth column
                
                // Add row to table model
                tableModel.addRow(new Object[]{id, suppId, date, String.format("%.2f", amount)});
                rowCount++;
            }
            
            if (rowCount == 0) {
                 JOptionPane.showMessageDialog(this, "No purchase records found in the database.", "Information", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error loading purchases: " + e.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "MySQL JDBC Driver not found! Check your classpath.", "Driver Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        SwingUtilities.invokeLater(() -> new ViewPurchaseForm());
    }
}