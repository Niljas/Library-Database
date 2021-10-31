import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.swing.JOptionPane;

public class AssessFines {

	
	public AssessFines() {
		assessFines();
	}
	
	private void assessFines() {
		
		// Get a connection to database
		Connection myConn;
		Statement myStmt;
		try {
			myConn = DriverManager.getConnection(GlobalConnectionInformation.GlobalDatabaseConn, 
					GlobalConnectionInformation.GlobalDatabaseUser,
					GlobalConnectionInformation.GlobalDatabasePass);
			
			// Create a statement
			myStmt = myConn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			
			// Get all book loans
			String allBookLoans = "select * from book_loans;";
			ResultSet rsAllBookLoans = myStmt.executeQuery(allBookLoans);
			
			// Iterate over book loans and see if fines need to be assessed
			while (rsAllBookLoans.next()) {
				// Get Necessary Columns
				String Loan_id = rsAllBookLoans.getString("Loan_id");
				String dueDate = rsAllBookLoans.getString("Due_date");
				String dateIn  = rsAllBookLoans.getString("Date_in");
				
				
				boolean isReturned;
				// Check if book is returned
				if (dateIn == null) {
					isReturned = false;
				} else {
					isReturned = true;
				}
				
				if (isReturned) {
					
					// Book is turned in
					// Convert dueDate to a Date object and get a SimpleDateFormat of it
					DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu-MM-dd");
					LocalDate Due_date = LocalDate.parse(dueDate, dtf);
					LocalDate Date_in  = LocalDate.parse(dateIn, dtf);
					long DifferenceInDays = Duration.between(Due_date.atStartOfDay(), Date_in.atStartOfDay()).toDays();

					if (DifferenceInDays > 0) {
						// Then report fine
						double FineAmt = (double) DifferenceInDays * 0.25;
						
						int currentRow = rsAllBookLoans.getRow();
						
						// See if fine already exists
						ResultSet rsFineExists = myStmt.executeQuery("select * from fines where Loan_id='" + Loan_id + "';");
						if (rsFineExists.next()) {
							// See if fine is paid
							int isPaid = rsFineExists.getInt("Paid");
							
							if (isPaid == 0) {
								// Update fine amount
								myStmt.executeUpdate("update fines set Fine_amt=" + FineAmt + " where Loan_id='" + Loan_id + "';");
							}
						} else {
							myStmt.executeUpdate("insert fines values ('" + Loan_id + "', " + FineAmt + ", FALSE);");
						}
						rsAllBookLoans = myStmt.executeQuery(allBookLoans);
						rsAllBookLoans.absolute(currentRow);
					}
					
				} else {
					// Book is not turned in, update fine amount if late
					
					// Book is turned in
					// Convert dueDate to a Date object and get a SimpleDateFormat of it
					DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu-MM-dd");
					LocalDate Due_date = LocalDate.parse(dueDate, dtf);
					long DifferenceInDays = Duration.between(Due_date.atStartOfDay(), LocalDate.now().atStartOfDay()).toDays();

					if (DifferenceInDays > 0) {
						// Then report fine
						double FineAmt = (double) DifferenceInDays * 0.25;

						int currentRow = rsAllBookLoans.getRow();

						// See if fine already exists
						ResultSet rsFineExists = myStmt.executeQuery("select * from fines where Loan_id='" + Loan_id + "';");
						if (rsFineExists.next()) {
							// See if fine is paid
							int isPaid = rsFineExists.getInt("Paid");
							
							if (isPaid == 0) {
								// Update fine amount
								myStmt.executeUpdate("update fines set Fine_amt=" + FineAmt + " where Loan_id='" + Loan_id + "';");
							}
						} else {
							myStmt.executeUpdate("insert fines values ('" + Loan_id + "', " + FineAmt + ", FALSE);");
						}
						rsAllBookLoans = myStmt.executeQuery(allBookLoans);
						rsAllBookLoans.absolute(currentRow);
					}
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Report fines assessed
		JOptionPane.showMessageDialog(null, "Fines Assessed.");
	}
	
}
