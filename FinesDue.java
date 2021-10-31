
import java.awt.*;
import java.awt.event.*;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.*;
import javax.swing.border.*;
import net.proteanit.sql.DbUtils;
import java.sql.Connection;

public class FinesDue extends JFrame {

    JFrame J_MainFrame;
    JPanel J_Control;
    static Connection conn = null;

    String loan_id = "";

    JTable table;

    FinesDue(String card_str) throws SQLException {
        prepareGUI(card_str);
    }

    void prepareGUI(String card_str) throws SQLException {

        String card = card_str;

        J_MainFrame = new JFrame("Library Management System");
        J_MainFrame.setSize(500, 500);
        J_MainFrame.setLocation(20, 50);
        J_MainFrame.setLocationRelativeTo(null);
        J_Control = new JPanel();
        J_Control.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(J_Control);
        GridBagLayout gbl_J_Control = new GridBagLayout();
        gbl_J_Control.columnWidths = new int[]{0, 0};
        gbl_J_Control.rowHeights = new int[]{0, 0, 0};
        gbl_J_Control.columnWeights = new double[]{1.0, Double.MIN_VALUE};
        gbl_J_Control.rowWeights = new double[]{0.0, 0.0, 1.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
        J_Control.setLayout(gbl_J_Control);

        //details
        JLabel details = new JLabel("Click on the payment row and then click Pay", JLabel.LEFT);
        details.setFont(new Font("Aerial", Font.PLAIN, 16));
        GridBagConstraints Grid_details = new GridBagConstraints();
        Grid_details.insets = new Insets(0, 0, 5, 0);
        Grid_details.gridx = 0;
        Grid_details.gridy = 0;
        Grid_details.gridwidth = 2;
        J_Control.add(details, Grid_details);

        // Given space
        JLabel Given_Space = new JLabel(" ", JLabel.CENTER);
        GridBagConstraints Grid_Given_Space = new GridBagConstraints();
        Grid_Given_Space.insets = new Insets(0, 0, 5, 0);
        Grid_Given_Space.gridx = 0;
        Grid_Given_Space.gridy = 1;
        Grid_Given_Space.gridwidth = 2;
        J_Control.add(Given_Space, Grid_Given_Space);

        //close button
        JButton close = new JButton("Close");
        close.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                J_MainFrame.setVisible(false);
                J_MainFrame.dispose();
            }
        });
        // Layer pane
        JLayeredPane layeredPane = new JLayeredPane();
        GridBagConstraints Grid_layeredPane = new GridBagConstraints();
        Grid_layeredPane.insets = new Insets(0, 0, 5, 0);
        Grid_layeredPane.fill = GridBagConstraints.BOTH;
        Grid_layeredPane.gridx = 0;
        Grid_layeredPane.gridy = 2;
        J_Control.add(layeredPane, Grid_layeredPane);
        
        // J table
        table = new JTable();
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(6, 6, 600, 400);
        layeredPane.add(scrollPane); // this might cause an error again so comment out if it does
        
        
        scrollPane.setViewportView(table);

        table.setColumnSelectionAllowed(false);
        table.setRowSelectionAllowed(true);

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evnt) {
                if (evnt.getClickCount() == 1 || evnt.getClickCount() == 2) {
                    loan_id = (String) table.getModel().getValueAt(table.rowAtPoint(evnt.getPoint()), 0);
                    setBackground(Color.blue);
                }
            }
        });

        //Pay
        JButton pay = new JButton("Pay Fine");
        pay.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	
            	// If a fine wasn't selected (loan_id == ""), then return
            	if (loan_id == "") {
            		JOptionPane.showMessageDialog(null, "Please select a fine to pay first.");
            		return;
            	}
            	
            	// Check if we want to pay all fines for some user (i.e., card_str == "" and Pay fines is called)
            	/*
            	 * Note that if user is looking at aggregate of fines for all users, then loan_id is really Card_id
            	 */
            	if (card_str == "") {
            		String Card_id = loan_id;
            		
                	try {
                        conn = DriverManager.getConnection(GlobalConnectionInformation.GlobalDatabaseConn, 
                        		GlobalConnectionInformation.GlobalDatabaseUser,
                        		GlobalConnectionInformation.GlobalDatabasePass);
                        Statement stmt = conn.createStatement();
                        ResultSet rs = stmt.executeQuery("Select * from library.book_loans where Card_id='" + Card_id + "' and Date_in is null;");
                        
                        if (!rs.next()) {
                            conn = DriverManager.getConnection(GlobalConnectionInformation.GlobalDatabaseConn, 
                            		GlobalConnectionInformation.GlobalDatabaseUser,
                            		GlobalConnectionInformation.GlobalDatabasePass);
                            Statement stmt2 = conn.createStatement();
                            stmt2.executeUpdate("update fines, book_loans set Paid=1 where book_loans.Loan_id=fines.Loan_id and book_loans.Card_id='" + Card_id + "';");
                            JOptionPane.showMessageDialog(null, "Fines for User '" + loan_id + "' have been paid ");
                            J_MainFrame.setVisible(false);
                        } else {
                            JOptionPane.showMessageDialog(null, "Some books not returned for user '" + loan_id + "'. Please "
                            								+ "return all books to pay fines.");
                        }
                    } catch (SQLException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
            	} else {
            		// Else, pay individual user fine
                	try {
                        conn = DriverManager.getConnection(GlobalConnectionInformation.GlobalDatabaseConn, 
                        		GlobalConnectionInformation.GlobalDatabaseUser,
                        		GlobalConnectionInformation.GlobalDatabasePass);
                        Statement stmt = conn.createStatement();
                        ResultSet rs = stmt.executeQuery("Select * from library.book_loans where Loan_id='" + loan_id + "' and Date_in is null;");
                        
                        if (!rs.next()) {
                            conn = DriverManager.getConnection(GlobalConnectionInformation.GlobalDatabaseConn, 
                            		GlobalConnectionInformation.GlobalDatabaseUser,
                            		GlobalConnectionInformation.GlobalDatabasePass);
                            Statement stmt2 = conn.createStatement();
                            //stmt2.executeUpdate("Update fines, book_loans set fines.Paid=1 where book_loans.Loan_id='" + loan_id + "' and book_loans.Loan_id=fines.Loan_id;");
                            stmt2.executeUpdate("update fines set fines.Paid=1 where Loan_id='" + loan_id + "';");
                            JOptionPane.showMessageDialog(null, "Fines for Loan Id '" + loan_id + "' has been paid ");
                            J_MainFrame.setVisible(false);
                        } else {
                            JOptionPane.showMessageDialog(null, "Book for loan '" + loan_id + "' not yet returned. Please "
                            								+ "return the book to pay fines.");
                        }
                    } catch (SQLException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
            	}
            }
        });
        GridBagConstraints Grid_pay = new GridBagConstraints();
        Grid_pay.fill = GridBagConstraints.HORIZONTAL;
        Grid_pay.insets = new Insets(0, 0, 5, 0);
        Grid_pay.gridx = 0;
        Grid_pay.gridy = 3;
        Grid_pay.gridwidth = 2;
        J_Control.add(pay, Grid_pay);

        // Given space 2
        JLabel Given_Space_2 = new JLabel("  ", JLabel.CENTER);
        GridBagConstraints Grid_Given_Space_2 = new GridBagConstraints();
        Grid_Given_Space_2.insets = new Insets(0, 0, 5, 0);
        Grid_Given_Space_2.gridx = 0;
        Grid_Given_Space_2.gridy = 4;
        Grid_Given_Space_2.gridwidth = 2;
        J_Control.add(Given_Space_2, Grid_Given_Space_2);
        
        //close
        GridBagConstraints Grid_Close_Button = new GridBagConstraints();
        Grid_Close_Button.fill = GridBagConstraints.HORIZONTAL;
        Grid_Close_Button.insets = new Insets(0, 0, 5, 0);
        Grid_Close_Button.gridx = 0;
        Grid_Close_Button.gridy = 5;
        Grid_Close_Button.anchor = GridBagConstraints.PAGE_END;
        J_Control.add(close, Grid_Close_Button);
        try {
            //jdbc connection to database
            conn = DriverManager.getConnection(GlobalConnectionInformation.GlobalDatabaseConn, 
            		GlobalConnectionInformation.GlobalDatabaseUser,
            		GlobalConnectionInformation.GlobalDatabasePass);
            Statement stm = conn.createStatement();
            ResultSet rs = null;
            
            
            if (card_str == "") {
            	rs = stm.executeQuery("select bl.Card_id, sum(f.Fine_amt) as 'Total Fines' from book_loans as bl, fines as f where bl.Loan_id=f.Loan_id and f.paid=0 group by bl.Card_id;");
                table.setModel(DbUtils.resultSetToTableModel(rs));
                table.setEnabled(true);
            	table.getColumnModel().getColumn(0).setPreferredWidth(200);
                table.getColumnModel().getColumn(1).setPreferredWidth(150);
            } else {
            	rs = stm.executeQuery("Select f.Loan_id, b.Isbn, f.Fine_amt from library.fines as f left outer join library.book_loans as b on b.Loan_id=f.Loan_id where b.Card_id='" + card + "' and Paid=0;");
                table.setModel(DbUtils.resultSetToTableModel(rs));
                table.setEnabled(true);
            	table.getColumnModel().getColumn(0).setPreferredWidth(100);
                table.getColumnModel().getColumn(1).setPreferredWidth(100);
                table.getColumnModel().getColumn(2).setPreferredWidth(100);
                table.getColumnModel().getColumn(2).setPreferredWidth(150);
            }

            table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        J_MainFrame.add(J_Control);
        J_MainFrame.setVisible(true);

    }
}
