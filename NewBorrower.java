
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;




public class NewBorrower {

	private static int borrowerIdNumber = 0;
	
	// Screen, and size
	private JFrame frame;
	private int width = 450;
	private int height = 300;
	
	// Borrower Name
	JTextField Bname;
	JLabel BnameLabel;
	
	// SSN
	JTextField SSN;
	JLabel SSNLabel;
	
	// Borrower Address
	JTextField BAddr;
	JLabel BAddrLabel;
	
	// Borrower Phone
	JTextField BPhone;
	JLabel BPhoneLabel;
	
	// Submit Button
	JButton SubmitButton;
	
	// Information Text (Errors and Tips)
	JTextPane Info;
	
	public NewBorrower() {
		frame = new JFrame();
    	frame.setSize(width, height);
    	frame.setTitle("Create New Borrower");
    	frame.setLocationRelativeTo(null);
    	
    	InitComponents();
    	ShowNewBorrowerGUI();
    	setCurrentAuthorId();
	}
	
	private void InitComponents() {
		// Buttons
		SubmitButton = new JButton("Submit");
		SubmitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Grab strings from user
				String ssn   = SSN.getText();
				String name  = Bname.getText();
				String addr  = BAddr.getText();
				String phone = BPhone.getText();
				
				if (ssn.length() == 0 || name.length() == 0 || addr.length() == 0) {
					Info.setText("ERROR: Ssn, Name, or Addr field must be non-empty.");
					return;
				}
				
				if (phone.isEmpty()) {
					// Call SubmitNewBorrowerToDB() w/o phone (empty)
					SubmitNewBorrowerToDB(ssn, name, addr, phone);
				}
				else {
					// See if phone number is in desired format
					Pattern p = Pattern.compile("\\([0-9]{3}\\) [0-9]{3}-[0-9]{4}");
					Matcher m = p.matcher(phone);
					if (m.find()) {
						// Good to submit, phone is valid
						SubmitNewBorrowerToDB(ssn, name, addr, phone);
						return;
					} else {
						// Phone number bad, report mistake and return
						Info.setText("ERROR: Phone number must be in format:"
								+ "\"(XXX) XXX-XXXX\" ( note the space after ')' )");
						return;
					}
				}
			} // end actionPerformed
		});
		
		// Labels
		BnameLabel  = new JLabel("Borrower Name");
		SSNLabel    = new JLabel("SSN");
		BAddrLabel  = new JLabel("Address");
		BPhoneLabel = new JLabel("Phone Number");
		
		// Textfields
		Bname = new JTextField(25);
		SSN = new JTextField(10);
		BAddr = new JTextField(25);
		BPhone = new JTextField(10);
		
		// ----- Please note, the below is adapted from auto-generated code
		// ----- from Eclipse's GUI builder
		
		Info = new JTextPane();
		Info.setEditable(false);
		Info.setText("Enter New Borrower Information and Click Submit when done.");
		// Give border
		Info.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED, null, null), 
									 "Status Messages", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		// Get rid of background color
		Info.setOpaque(false);
		// Make it unclickable
		Info.setFocusable(false);
		
		// Create styles for centering text and italicizing text
		StyledDocument doc = Info.getStyledDocument();
		SimpleAttributeSet center = new SimpleAttributeSet();
		center.addAttribute(StyleConstants.CharacterConstants.Italic, Boolean.TRUE);
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
		doc.setParagraphAttributes(0, doc.getLength(), center, false);
		
		GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addGroup(groupLayout.createSequentialGroup()
									.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
										.addComponent(BnameLabel, GroupLayout.DEFAULT_SIZE, 133, Short.MAX_VALUE)
										.addComponent(BPhoneLabel)
										.addComponent(BAddrLabel, GroupLayout.DEFAULT_SIZE, 133, Short.MAX_VALUE))
									.addPreferredGap(ComponentPlacement.RELATED))
								.addGroup(groupLayout.createSequentialGroup()
									.addComponent(SSNLabel)
									.addGap(118)))
							.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addComponent(SSN, GroupLayout.DEFAULT_SIZE, 239, Short.MAX_VALUE)
								.addComponent(BPhone, GroupLayout.DEFAULT_SIZE, 239, Short.MAX_VALUE)
								.addComponent(BAddr, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 239, Short.MAX_VALUE)
								.addComponent(Bname, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 239, Short.MAX_VALUE))
							.addGap(48))
						.addGroup(Alignment.LEADING, groupLayout.createSequentialGroup()
							.addComponent(SubmitButton)
							.addContainerGap(357, Short.MAX_VALUE))
						.addGroup(groupLayout.createSequentialGroup()
							.addGap(56)
							.addComponent(Info)
							.addGap(60))))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(BnameLabel)
						.addComponent(Bname, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(BAddr, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(BAddrLabel))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(BPhone, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(BPhoneLabel))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(SSNLabel)
						.addComponent(SSN, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(18)
					.addComponent(SubmitButton)
					.addGap(18)
					.addComponent(Info, GroupLayout.DEFAULT_SIZE, 43, Short.MAX_VALUE)
					.addGap(40))
		);
		frame.getContentPane().setLayout(groupLayout);
	}
	
	private void ShowNewBorrowerGUI() {
		// Show GUI
		frame.setVisible(true);
		
	}
	
	
	private boolean SubmitNewBorrowerToDB(String ssn, String name, String addr, String phone) {
		
		// Check for if query failed
		boolean WasQuerySuccessful = true;
		
		// Prepare strings for sql query
		String quote = "'";
		name  = quote + name + quote;
		addr  = quote + addr + quote;
		// Check if phone is "" - should be null
		if (phone.isEmpty()) {
			phone = "NULL";
		} else {
			phone = quote + phone + quote;
		}
		
		// Check if ssn has '-' or not
		if (!ssn.contains("-")) {
			ssn = ssn.substring(0, 3) + "-" + ssn.substring(3, 5) + "-" + ssn.substring(5);
		}
		
		// Create Next Author Id
		String leadingZeroes = "";
		for (int i = (int)Math.log10(borrowerIdNumber); i < 5; i++) {
			leadingZeroes += "0";
		}
		String nextBorrowerId = "ID" + leadingZeroes + borrowerIdNumber;
		
		// Create New SQL Query to submit Borrower to db and report back any errors
		try {
			// Get Connection to Database
			Connection myConn = DriverManager.getConnection(GlobalConnectionInformation.GlobalDatabaseConn, 
            		GlobalConnectionInformation.GlobalDatabaseUser,
            		GlobalConnectionInformation.GlobalDatabasePass);	
			
			// Create statement and query
			Statement myStmt = myConn.createStatement();
			
			String query = "INSERT INTO BORROWER VALUES ('" + nextBorrowerId + "', '" + ssn + "'," + name + "," + addr + "," + phone + ");";
			
			// Try to execute query
			try {
				myStmt.executeUpdate(query);
			} catch (Exception e) {
				Info.setText(e.getMessage());
				WasQuerySuccessful = false;
			}
			
			// Report to user if query was successful
			if (WasQuerySuccessful) {
				Info.setText("Borrower " + name + " accepted. Your card id is: " + nextBorrowerId);
				
				// Increment Author id if query was successful
				borrowerIdNumber += 1;
			}
			
			// Close connection
			myConn.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return true;
	}
	
	
	

	void setCurrentAuthorId() {
			// Locals
			Connection myConn = null;
			Statement myStmt = null;
			String query = "SELECT count(*) from borrower;";
			ResultSet rs = null;
			
			try {
				myConn = DriverManager.getConnection(GlobalConnectionInformation.GlobalDatabaseConn, 
	            		GlobalConnectionInformation.GlobalDatabaseUser,
	            		GlobalConnectionInformation.GlobalDatabasePass);
				myStmt = myConn.createStatement();
				
				// Execute, move to first row and get result of count
				rs = myStmt.executeQuery(query);
				rs.next();
				
				// Save next author id
				borrowerIdNumber = rs.getInt(1) + 1;
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally { // nullify rs and stmt after every MySQL command
				if (rs != null) {
					try {
						rs.close();
					} catch (SQLException sqlEx) {};
					rs = null;
				}
				if (myStmt != null) {
					try {
						myStmt.close();
					} catch (SQLException sqlEx) {};
					myStmt = null;
				}
			}
	}
	
	
	
}



















