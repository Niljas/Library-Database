
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.*;
import javax.swing.border.*;
import net.proteanit.sql.DbUtils;

public class CheckInList extends JFrame {

    JFrame J_MainFrame;
    JPanel Jpanel_control;
    static Connection conn = null;
    int row = 0;
    
    // The Loan ID that is currently selected on the JTable
    // by the user.
    private String currentLoadId = "";

    JTable table;
// check in list 

    CheckInList(String isbn, String card, String borrower) throws SQLException {
        prepareGUI(isbn, card, borrower);
    }
// Main Frame and GUI

    private void prepareGUI(String isbn, String card, String borrower) throws SQLException {
        J_MainFrame = new JFrame("Library Management System");
        J_MainFrame.setSize(500, 500);
        J_MainFrame.setLocation(20, 50);
		J_MainFrame.setLocationRelativeTo(null);
// Jpanel control
        Jpanel_control = new JPanel();
        Jpanel_control.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(Jpanel_control);
        GridBagLayout Grid_Control = new GridBagLayout();
        Grid_Control.columnWidths = new int[]{0, 0};
        Grid_Control.rowHeights = new int[]{0, 0, 0};
        Grid_Control.columnWeights = new double[]{1.0, Double.MIN_VALUE};
        Grid_Control.rowWeights = new double[]{0.0, 0.0, 1.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
        Jpanel_control.setLayout(Grid_Control);
// close button
        JButton close = new JButton("Close");
        close.addActionListener((ActionEvent e) -> {
            J_MainFrame.setVisible(false);
            J_MainFrame.dispose();
        });
// Layers
        JLayeredPane layeredPane = new JLayeredPane();
        GridBagConstraints Grid_layer = new GridBagConstraints();
        Grid_layer.insets = new Insets(0, 0, 5, 0);
        Grid_layer.fill = GridBagConstraints.BOTH;
        Grid_layer.gridx = 0;
        Grid_layer.gridy = 2;
        Jpanel_control.add(layeredPane, Grid_layer);
//scroll bar 
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(6, 6, 600, 375);
        layeredPane.add(scrollPane);
// table for check in 
        table = new JTable();
        table.setColumnSelectionAllowed(false);
        table.setRowSelectionAllowed(true);
        scrollPane.setViewportView(table);
// adding option for check in details
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evnt) {
                if (evnt.getClickCount() == 1 || evnt.getClickCount() == 2) {
                    currentLoadId = (String) table.getModel().getValueAt(table.rowAtPoint(evnt.getPoint()), 0);
                }
            }
        });

        
        JButton pay = new JButton("Check-in");
        pay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				if (currentLoadId == "") {
					JOptionPane.showMessageDialog(null, "Please select a book loan to Check-in.");
					return;
				}
				
				// TODO Auto-generated method stub
                //time and date data
                String date = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
                try {
                	
                    conn = DriverManager.getConnection(GlobalConnectionInformation.GlobalDatabaseConn, 
                    		GlobalConnectionInformation.GlobalDatabaseUser,
                    		GlobalConnectionInformation.GlobalDatabasePass);
                    
                    Statement Stm1 = conn.createStatement();
                    J_MainFrame.setVisible(false);
                    
                	Stm1.executeUpdate("Update library.book_loans set Date_in='" + date + "' where Loan_id='" + currentLoadId + "';");
                	JOptionPane.showMessageDialog(null, "Successfully Checked-in book for loan " + currentLoadId + ".");
                	
                    
                    //Stm1.executeUpdate("Update library.book_loans set Date_in='" + date + "' where Loan_id='" + currentLoadId + "';");
                    
//                    Statement Stm2 = conn.createStatement();
//                    ResultSet Set = Stm2.executeQuery("Select * from library.book_loans where Loan_id='" + currentLoadId + "' and Due_date<Date_in;");

//                    if (Set.next()) {
//                        JOptionPane.showMessageDialog(null, "Fines for book Overdue:");
//                        J_MainFrame.setVisible(false);
//                        //new Fines();// Fines is called here
//                        String dueDate = Set.getString(5);
//                        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//                        java.util.Date due = format.parse(dueDate);
//                        java.util.Date today = format.parse(date);
//
//                        long x = (today.getTime() - due.getTime());
//                        x = x / (1000 * 60 * 60 * 24);
//                        double fine = 0.25 * x;
//                        JOptionPane.showMessageDialog(null, "Successfully Checked-in book for loan " + currentLoadId + ".");
//                    } else {
//                        JOptionPane.showMessageDialog(null, "Book for loan " + currentLoadId + " already Checked-in.");
//                    }
                    
                } catch (SQLException e1) {
                    System.out.println("Sql Error: " + e1.getMessage());
                } 
			}
        });
        
        // Check-in Button
        GridBagConstraints Grid_pay = new GridBagConstraints();
        Grid_pay.fill = GridBagConstraints.HORIZONTAL;
        Grid_pay.insets = new Insets(0, 0, 5, 0);
        Grid_pay.gridx = 0;
        Grid_pay.gridy = 3;
        Grid_pay.gridwidth = 2;
        Jpanel_control.add(pay, Grid_pay);
        
        //Close Button
        GridBagConstraints Grid_Close = new GridBagConstraints();
        Grid_Close.fill = GridBagConstraints.HORIZONTAL;
        Grid_Close.insets = new Insets(0, 0, 5, 0);
        Grid_Close.gridx = 0;
        Grid_Close.gridy = 2;
        Grid_Close.anchor = GridBagConstraints.PAGE_END;
        Jpanel_control.add(close, Grid_Close);
        try {
            //jdbc connection to database
            conn = DriverManager.getConnection(GlobalConnectionInformation.GlobalDatabaseConn, 
            		GlobalConnectionInformation.GlobalDatabaseUser,
            		GlobalConnectionInformation.GlobalDatabasePass);
            
            Statement Stm = conn.createStatement();
            ResultSet Set = null;

            if (card.equals("") && borrower.equals(""))//For isbn
            {
                Set = Stm.executeQuery("Select Loan_id,Isbn,Card_id from library.book_loans where Isbn='" + isbn + "' and Date_in is null;");
            } else if (isbn.equals("") && borrower.equals(""))//For card_id
            {
                Set = Stm.executeQuery("Select Loan_id,Isbn,Card_id from library.book_loans where Card_id='" + card + "' and Date_in is null;");
            } else if (isbn.equals("") && card.equals(""))//For borrower name
            {
                Set = Stm.executeQuery("Select Loan_id,Isbn,Card_id from library.book_loans where Card_id in (Select Card_id from library.borrower where Bname like '%" + borrower + "%') and Date_in is null;");
            } else if (borrower.equals(""))// For isbn and card_id
            {
                Set = Stm.executeQuery("Select Loan_id,Isbn,Card_id from library.book_loans where Isbn='" + isbn + "' and Card_id='" + card + "' and Date_in is null;");
            } else if (isbn.equals(""))// For card_id and borrower name
            {
                Set = Stm.executeQuery("(Select Loan_id,Isbn,Card_id from library.book_loans where Card_id='" + card + "' and Date_in is null)");
            } else if (card.equals(""))// For isbn and borrower name
            {
                Set = Stm.executeQuery("(Select Loan_id,Isbn,Card_id from library.book_loans where Isbn='" + isbn + "' and Card_id in (Select Card_id from library.borrower where Bname like '%" + borrower + "%') and Date_in is null)");
            } else//For isbn, borrower name, card_id
            {
                Set = Stm.executeQuery("Select Loan_id,Isbn,Card_id from library.book_loans where Isbn='" + isbn + "' and Card_id='" + card + "' and Date_in is null;");
            }
            
//            while (Set.next()) {
//            	System.out.println(Set.getString("Loan_id"));
//            }

            table.setModel(DbUtils.resultSetToTableModel(Set));
            table.setEnabled(true);

            table.getColumnModel().getColumn(1).setPreferredWidth(150);
            table.getColumnModel().getColumn(2).setPreferredWidth(150);
            table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        J_MainFrame.add(Jpanel_control);
        J_MainFrame.setVisible(true);

    }

}
