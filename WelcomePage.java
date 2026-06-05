import javax.swing.*;
import java.awt.*;

// Main Dashboard
public class WelcomePage extends JFrame {

    public WelcomePage() {

        // 🔥 Global Font Increase
        UIManager.put("Label.font", new Font("Arial", Font.PLAIN, 13));
        UIManager.put("Button.font", new Font("Arial", Font.BOLD, 15));
        UIManager.put("TitledBorder.font", new Font("Arial", Font.BOLD, 20));

        setTitle("Pharmacy Management Dashboard");
        setSize(1600, 850);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(245, 245, 245));

        // Title
        JLabel title = new JLabel("Welcome to Pharmacy Dashboard", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setForeground(new Color(0, 102, 204));
        title.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(title, BorderLayout.NORTH);

        // Main Panel with sections
        JPanel mainPanel = new JPanel(new GridLayout(2, 3, 15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(245, 245, 245));

        mainPanel.add(createSectionPanel("Medicines", new String[]{"Add", "Delete", "Edit", "View"}, new Color(204, 229, 255)));
        mainPanel.add(createSectionPanel("Suppliers", new String[]{"Add", "Delete", "Edit", "View"}, new Color(255, 229, 204)));
        mainPanel.add(createSectionPanel("Customers", new String[]{"Add", "Delete", "Edit", "View"}, new Color(204, 255, 204)));
        mainPanel.add(createSectionPanel("Sales", new String[]{"Generate Bill", "Update Bill", "Returns", "View"}, new Color(255, 204, 229)));
        mainPanel.add(createSectionPanel("Purchase", new String[]{"Purchase Bill", "Delete Bill", "Update Purchase", "View Purchase"}, new Color(255, 255, 204)));

        add(mainPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    // Create each section with buttons
    private JPanel createSectionPanel(String section, String[] buttons, Color bgColor) {
        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5)); // one column
        panel.setBorder(BorderFactory.createTitledBorder(section));
        panel.setBackground(bgColor);

        for (String text : buttons) {
            JButton button = new JButton(text);
            button.setBackground(Color.WHITE);
            button.setForeground(new Color(0, 51, 102));
            button.setFocusPainted(false);
            button.addActionListener(e -> openPage(section, text));
            panel.add(button);
        }
        return panel;
    }

    // Link buttons to dedicated forms
    void openPage(String section, String action) {
        switch (section) {
            case "Medicines" -> {
                switch (action) {
                    case "Add" -> new AddMedicineForm();
                    case "Delete" -> new DeleteMedicineForm();
                    case "Edit" -> new EditMedicineForm();
                    case "View" -> new ViewMedicineForm();
                }
            }
            case "Suppliers" -> {
                switch (action) {
                    case "Add" -> new AddSupplyForm();
                    case "Delete" -> new DeleteSupplyForm();
                    case "Edit" -> new EditSupplyForm();
                    case "View" -> new ViewSupplyForm();
                }
            }
            case "Customers" -> {
                switch (action) {
                    case "Add" -> new AddCustomerForm();
                    case "Delete" -> new DeleteCustomerForm();
                    case "Edit" -> new EditCustomerForm();
                    case "View" -> new ViewCustomerForm();
                }
            }
            case "Sales" -> {
                switch (action) {
                    case "Generate Bill" -> new GenerateBillForm();
                    case "Update Bill" -> new UpdateBillForm();
                    case "Returns" -> new ReturnBillForm();
                    case "View" -> new ViewSalesForm();
                }
            }
            case "Purchase" -> {
                switch (action) {
                    case "Purchase Bill" -> new GeneratePurchaseForm();
                    case "Delete Bill" -> new DeletePurchaseForm();
                    case "Update Purchase" -> new UpdatePurchaseForm();
                    case "View Purchase" -> new ViewPurchaseForm();
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(WelcomePage::new);
    }
}

