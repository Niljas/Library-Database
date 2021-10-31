
import java.awt.*;
import java.awt.event.*;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.*;
import javax.swing.border.*;
import java.sql.Connection;
import java.util.Date;

public class CheckOut extends JFrame {

    JFrame J_MainFrame;
    JPanel J_Control;
    static Connection conn;

    CheckOut() {
        prepareGUI();
    }

    void prepareGUI() {
        J_MainFrame = new JFrame("Library Management System");
        J_MainFrame.setSize(500, 300);
        J_MainFrame.setLocation(20, 50);
        J_MainFrame.setMinimumSize(J_MainFrame.getSize());
        J_MainFrame.setLocationRelativeTo(null);
        J_Control = new JPanel();
        J_Control.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(J_Control);
        GridBagLayout Grid_J_Control = new GridBagLayout();
        Grid_J_Control.columnWidths = new int[]{0, 0};
        Grid_J_Control.rowHeights = new int[]{0, 0, 0, 0, 0, 0};
        Grid_J_Control.columnWeights = new double[]{Double.MIN_VALUE, Double.MIN_VALUE};
        Grid_J_Control.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
        J_Control.setLayout(Grid_J_Control);

        //Isbn Text Box
        JTextField Isbntextbox = new JTextField();
        Isbntextbox.setFont(new Font("Times New Roman", Font.PLAIN, 14));
        Isbntextbox.setForeground(Color.black);
        GridBagConstraints Grid_Isbntextbox = new GridBagConstraints();
        Grid_Isbntextbox.fill = GridBagConstraints.HORIZONTAL;
        Grid_Isbntextbox.insets = new Insets(0, 0, 5, 0);
        Grid_Isbntextbox.gridx = 1;
        Grid_Isbntextbox.gridy = 2;
        J_Control.add(Isbntextbox, Grid_Isbntextbox);
        Isbntextbox.setColumns(15);

        //Card No.Text Box
        JTextField Cardtextbox = new JTextField();
        Cardtextbox.setFont(new Font("Times New Roman", Font.PLAIN, 14));
        Cardtextbox.setForeground(Color.black);
        GridBagConstraints Grid_Cardtextbox = new GridBagConstraints();
        Grid_Cardtextbox.fill = GridBagConstraints.HORIZONTAL;
        Grid_Cardtextbox.insets = new Insets(0, 0, 5, 0);
        Grid_Cardtextbox.gridx = 1;
        Grid_Cardtextbox.gridy = 3;
        J_Control.add(Cardtextbox, Grid_Cardtextbox);
        Cardtextbox.setColumns(15);

        //Borrower Card No.
        JLabel Borrower_card_number = new JLabel("Card No. :", JLabel.LEFT);
        Borrower_card_number.setFont(new Font("Times New Roman", Font.BOLD, 14));
        GridBagConstraints Grid_Borrower_card_number = new GridBagConstraints();
        Grid_Borrower_card_number.insets = new Insets(0, 0, 5, 0);
        Grid_Borrower_card_number.gridx = 0;
        Grid_Borrower_card_number.gridy = 3;
        J_Control.add(Borrower_card_number, Grid_Borrower_card_number);

        //Book ISBN
        JLabel Book_ISBN_Disply = new JLabel("ISBN :", JLabel.LEFT);
        Book_ISBN_Disply.setFont(new Font("Times New Roman", Font.BOLD, 14));
        GridBagConstraints Grid_Book_ISBN_Disply = new GridBagConstraints();
        Grid_Book_ISBN_Disply.insets = new Insets(0, 0, 5, 0);
        Grid_Book_ISBN_Disply.gridx = 0;
        Grid_Book_ISBN_Disply.gridy = 2;
        J_Control.add(Book_ISBN_Disply, Grid_Book_ISBN_Disply);

        JLabel Empty = new JLabel("  ", JLabel.CENTER);
        GridBagConstraints Grid_Empty = new GridBagConstraints();
        Grid_Empty.insets = new Insets(0, 0, 5, 0);
        Grid_Empty.gridx = 0;
        Grid_Empty.gridy = 1;
        Grid_Empty.gridwidth = 2;
        J_Control.add(Empty, Grid_Empty);

        JButton Display_CheckOut = new JButton("Check Out");
        Display_CheckOut.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (Isbntextbox.getText().equals("") || Cardtextbox.getText().equals("")) {
                    JOptionPane.showMessageDialog(null, "Please enter an Isbn and Card ID");
                } else {
                	
                	// Boolean used to check if checkout was successful
                	boolean isCheckOutSuccessful = false;
                	
                    try {
                        String isbn = Isbntextbox.getText();
                        String cardId = Cardtextbox.getText();
                        String nextLoanId = "";
                        String date = new SimpleDateFormat("YYYY-MM-dd").format(Calendar.getInstance().getTime());
                        Calendar c = Calendar.getInstance();
                        c.setTime(new Date());
                        c.add(Calendar.DATE, 14);
                        String dueDate = new SimpleDateFormat("YYYY-MM-dd").format(c.getTime());
                        
                        // Prepare connection and statement
                        Connection myConn = DriverManager.getConnection(GlobalConnectionInformation.GlobalDatabaseConn, 
                        		GlobalConnectionInformation.GlobalDatabaseUser,
                        		GlobalConnectionInformation.GlobalDatabasePass);
                        Statement myStmt = myConn.createStatement();
                        
                        // Setup Query Strings
                        String isIsbnValid = "select count(*) from book where Isbn='" + isbn + "';";
                        String isCardIdValid = "select count(*) from borrower where Card_id='" + cardId + "'";
                        String strTotalLoans = "select count(*) from book_loans;";
                        String strLoanCount = "select count(*) from book_loans as bl where bl.Card_id = '" + cardId + "' and bl.Date_in IS NULL;";
                        String strUnpaidFines = "select count(*) from borrower as b, book_loans as bl, "
                        					  + "fines as f where b.Card_id='" + cardId + "' and bl.Card_id='" + cardId + "' and bl.Loan_id=f."
                        					  + "Loan_id and f.Paid=0;";
                        String strIsBookOut = "select count(*) from book_loans as bl, book as b where b.Isbn = '" + isbn + "' and bl.Isbn = "
                        					  + "'" + isbn + "' and bl.Date_in IS NULL";
                        
                        // Verify if Isbn is valid
                        ResultSet rsValidIsbn = myStmt.executeQuery(isIsbnValid);
                        rsValidIsbn.next();
                        if (rsValidIsbn.getInt(1) == 0) {
                        	JOptionPane.showMessageDialog(null, "The Isbn given does not match a book in the library. "
                        										+ "Please enter a valid Isbn");
                        	return;
                        }
                        
                        // Verify Card_id is valid
                        ResultSet rsValidCardId = myStmt.executeQuery(isCardIdValid);
                        rsValidCardId.next();
                        if (rsValidCardId.getInt(1) == 0) {
                        	JOptionPane.showMessageDialog(null, "The Card ID given does not exist. Please enter a valid "
                        										+ "Card ID.");
                        	return;
                        }
                        
                        // Get number of current book_loans
                        ResultSet rsLoanCount = myStmt.executeQuery(strLoanCount);
                        rsLoanCount.next();
                        int numPersonalLoans = rsLoanCount.getInt(1);
                        if (numPersonalLoans >= 3) {
                        	JOptionPane.showMessageDialog(null, "You already have 3 checked out books. Please return "
                        									  + "one before checking out more.");
                        	return;
                        }
                        
                        // See if user has unpaid fines
                        ResultSet rsUnpaidFines = myStmt.executeQuery(strUnpaidFines);
                        rsUnpaidFines.next();
                        if (rsUnpaidFines.getInt(1) > 0) {
                        	JOptionPane.showMessageDialog(null, "Please pay your outstanding fines before checking"
                        									  + " out any more books.");
                        	return;
                        }
                        
                        // Check if book is already checked out
                        ResultSet rsIsBookOut = myStmt.executeQuery(strIsBookOut);
                        rsIsBookOut.next();
                        if (rsIsBookOut.getInt(1) == 1) { 
                        	JOptionPane.showMessageDialog(null, "Sorry, that book is already checked out. Try"
                        									  + " a different one!");
                        	return;
                        }
                        
                        // Get total number of loans
                        ResultSet rsTotalNumLoans = myStmt.executeQuery(strTotalLoans);
                        rsTotalNumLoans.next();
                        int numLoans = rsTotalNumLoans.getInt(1) + 1;
                        
                        // ----- All checks have been passed, try to let user check out this book ----- //
                        
                        // Create Next Loan Id
                        String leadingZeroes = "";
                        for (int i = (int) Math.log10(numLoans); i < 5; i++) {
                            leadingZeroes += "0";
                        }
                        nextLoanId = "LO" + leadingZeroes + numLoans;
                        
                        // Create new query for checkout
                        String strCheckOut = "insert into book_loans values ('" + nextLoanId + "', '" + isbn + "', '" + cardId + "', '" + 
          					  				  date + "', '" + dueDate + "', NULL);";
                        
                        // Check out book
                        try {
                            myStmt.executeUpdate(strCheckOut);
                            isCheckOutSuccessful = true;
                        } catch (SQLException e2) {
                        	JOptionPane.showMessageDialog(null, e2.getMessage());
                        }

                    } catch (SQLException ex) {
                    	JOptionPane.showMessageDialog(null, ex.getMessage());
                    } finally {
                        // Check if the book was checked out
                    	if (isCheckOutSuccessful)
                    		JOptionPane.showMessageDialog(null, "Checkout Successful! Thank you.");
                    }
                  

                }
            }
        });

        //Close Button
        JButton close = new JButton("Back");
        close.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                J_MainFrame.setVisible(false);
                //new MainPage();
            }
        });

        GridBagConstraints Grid_CheckoutDiplay = new GridBagConstraints();
        Grid_CheckoutDiplay.fill = GridBagConstraints.HORIZONTAL;
        Grid_CheckoutDiplay.insets = new Insets(0, 0, 5, 0);
        Grid_CheckoutDiplay.gridx = 0;
        Grid_CheckoutDiplay.gridy = 4;
        Grid_CheckoutDiplay.gridwidth = 2;
        J_Control.add(Display_CheckOut, Grid_CheckoutDiplay);

        JLabel Room = new JLabel("  ", JLabel.CENTER);
        GridBagConstraints Grid_Room = new GridBagConstraints();
        Grid_Room.insets = new Insets(0, 0, 5, 0);
        Grid_Room.gridx = 0;
        Grid_Room.gridy = 5;
        Grid_Room.gridwidth = 2;
        J_Control.add(Room, Grid_Room);

        GridBagConstraints gbc_btnClose = new GridBagConstraints();
        gbc_btnClose.fill = GridBagConstraints.HORIZONTAL;
        gbc_btnClose.insets = new Insets(0, 0, 5, 0);
        gbc_btnClose.gridx = 0;
        gbc_btnClose.gridy = 6;
        gbc_btnClose.anchor = GridBagConstraints.PAGE_END;
        gbc_btnClose.gridwidth = 2;
        J_Control.add(close, gbc_btnClose);

        J_MainFrame.add(J_Control);
        J_MainFrame.setVisible(true);
    }
}
