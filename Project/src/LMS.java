
/*--------------------------------------------------------------------------------
 * 	Module Name : LMS.java
 * 	Programmer  : Elliott Gorman etc.
 * 	Date        : 7/11/2021	
 * 
 *	Purpose:
 *		This is the main driver class for the Library management system. It 
 *		is the starting point for all major components of the LMS.
 *  
 *  Submodule Calls / Class Usages:
 *  	LMSScreen()
 *  
 * 	Modification History:	
 * 		- Title, Programmer, Date
 * 			Modification 1 Notes
 *
 *-------------------------------------------------------------------------------*/

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;


public class LMS {

	public static void main(String[] args) {
		
		// Create HomeScreen
		HomeScreen window = new HomeScreen();
		window.frame.setVisible(true);

		
		// Attach Borrower action
		window.btnCreateNewBorrower.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Create New Borrower
				new NewBorrower();
			} // end actionPerformed
		}); // End actionListener on new borrower button
		// ----- END Configure GUI for LMS Borrower Button ----- //
		
		// Attach Check-in book action
		window.btnCheckinBook.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Check in book
				new CheckIn();
			} // end actionPerformed
		}); // End actionListener on new borrower button
		// ----- END Configure GUI for LMS Borrower Button ----- //
		
		// Attach Check-out book action
		window.btnCheckoutBook.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Check in book
				new CheckOut();
			} // end actionPerformed
		}); // End actionListener on new borrower button
		// ----- END Configure GUI for LMS Borrower Button ----- //

		// Attach Payment of Fines Action
		window.btnPayFine.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Check in book
				new Fines();
			} // end actionPerformed
		}); // End actionListener on new borrower button
		// ----- END Configure GUI for LMS Borrower Button ----- //
		
		// Attach Assess Fines Action
		window.btnAssessFines.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Check in book
				new AssessFines();
			} // end actionPerformed
		}); // End actionListener on new borrower button
		// ----- END Configure GUI for LMS Borrower Button ----- //
		

		// Create ability to Search for books
		window.btnSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Create Query Display
				QueryDisplay display = new QueryDisplay(window, window.listQueryDisplay);
				
				ResultSet myRs = null;
				Connection myConn = null;
				Statement myStmt = null;
				String query = "";
				String search = "";
				
				try {
					// Get a connection to database
					myConn = DriverManager.getConnection(GlobalConnectionInformation.GlobalDatabaseConn, 
		            		GlobalConnectionInformation.GlobalDatabaseUser,
		            		GlobalConnectionInformation.GlobalDatabasePass);
					
					// Create a statement
					myStmt = myConn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
					
					// Get search box value
					search = window.SearchBox.getText();
					
					if (search.isEmpty()) {
						window.SystemMessages.setText("You must enter some value into the search box before searching.");
						return;
					}
					
					// Create and Execute SQL query
					query = "select b.Isbn, b.Title, a.Name from book as b, authors as a, book_authors as ba where "
							+ "ba.Isbn=b.Isbn and ba.Author_id=a.Author_id and (b.Title like '%" + search + "%' or a.Name like "
							+ "'%" + search + "%' or b.Isbn like '%" + search + "%') order by b.Isbn";
					
					myRs = myStmt.executeQuery(query);
					
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				
				// Display Result Set
				try {
					if (myRs.next()) {
						myRs.previous();
						display.DisplayResults(myStmt, myRs, search);
					}
					
					// Report to user how many results were found
					if (myRs != null) {
						
						/*
						 * The below query will give you the Isbn, Title, and Authors Name for every book
						 */
						// select b.Isbn, b.Title, a.Name from book as b, authors as a, book_authors as ba where ba.Isbn=b.Isbn and ba.Author_id=a.Author_id and b.Title like '%isTitle%' and a.Name like '%isAuthor%' and b.Isbn like '%isIsbn%';
						// Get size of result set
						String sizeQuery = "SELECT COUNT(*) FROM (SELECT DISTINCT b.Isbn FROM BOOK AS b, AUTHORS AS a, BOOK_AUTHORS AS ba WHERE "
								+ "ba.Author_id=a.Author_id AND b.Isbn=ba.Isbn AND (b.Title LIKE '%" + search + "%' OR a.Name LIKE "
								+ "'%" + search + "%' OR b.Isbn LIKE '%" + search + "%') ORDER BY b.Isbn) AS Size_of_Search;";
						ResultSet count = myStmt.executeQuery(sizeQuery);
						count.next();
						int ResultSetSize = count.getInt(1);
						
						if (ResultSetSize == 0) {
							window.SystemMessages.setText("That search returned 0 books.");
							window.Refresh();
						}
						else {
							window.SystemMessages.setText("Found " + ResultSetSize + " books!");
							window.Refresh();
						}
					}
				} catch (SQLException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				
				try {
					myConn.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			} // end actionPerformed
		}); // End actionlistener on new borrower button
		

	} // End main()
	
	
} // End class LMS




