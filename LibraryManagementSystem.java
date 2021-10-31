//Library Management System 
//Database Initialization Code + Menu Sample Design Code

import java.io.*;
import java.sql.*;
import java.util.*;


public class LibraryManagementSystem {

	static Connection conn; // conn, stmt, and rs are used in multiple static methods
	static Statement stmt;
	static ResultSet rs;
	static int authorIdNumber = 1; // numeric ending to an Author_id value; 1 is first
	int cardIdNumber = 1001; // numeric ending to a Card_id value; first 1,000 are already retrieved from borrowers.csv
	int loanIdNumber = 1; // numeric ending to a Loan_id value; 1 is first

	public static void executeSQLUpdateCommand(String command) {

		// execute a MySQL update command or print the MySQL error messages
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate(command);
		}
		catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
		}
		finally { // nullify rs and stmt after every MySQL command
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException sqlEx) {};
				rs = null;
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException sqlEx) {};
				stmt = null;
			}
		}

	}

	public static void createLibraryDatabase() {

		// create the library database and define all the tables
		executeSQLUpdateCommand("DROP DATABASE IF EXISTS LIBRARY;");
		executeSQLUpdateCommand("CREATE DATABASE LIBRARY;");
		executeSQLUpdateCommand("USE LIBRARY;");
		executeSQLUpdateCommand("DROP TABLE IF EXISTS BOOK;");
		executeSQLUpdateCommand("CREATE TABLE BOOK ("
				+ "Isbn CHAR(10) CHECK (REGEXP_LIKE(Isbn, '[:digit:]{9}[[:digit:]X]', 'c')) NOT NULL, "
				+ "Title VARCHAR(250) NOT NULL, "
				+ "PRIMARY KEY (Isbn));");
		executeSQLUpdateCommand("DROP TABLE IF EXISTS AUTHORS;");
		executeSQLUpdateCommand("CREATE TABLE AUTHORS ("
				+ "Author_id CHAR(8) CHECK (REGEXP_LIKE(Author_id, 'AU[:digit:]{6}', 'c')) NOT NULL, "
				+ "Name VARCHAR(100), "
				+ "PRIMARY KEY (Author_id));");
		executeSQLUpdateCommand("DROP TABLE IF EXISTS BOOK_AUTHORS;");
		executeSQLUpdateCommand("CREATE TABLE BOOK_AUTHORS ("
				+ "Author_id CHAR(8) CHECK (REGEXP_LIKE(Author_id, 'AU[:digit:]{6}', 'c')) NOT NULL, "
				+ "Isbn CHAR(10) CHECK (REGEXP_LIKE(Isbn, '[:digit:]{9}[[:digit:]X]', 'c')) NOT NULL, "
				+ "PRIMARY KEY (Author_id, Isbn), "
				+ "FOREIGN KEY (Author_id) REFERENCES AUTHORS (Author_id), "
				+ "FOREIGN KEY (Isbn) REFERENCES BOOK (Isbn));");
		executeSQLUpdateCommand("DROP TABLE IF EXISTS BORROWER;");
		executeSQLUpdateCommand("CREATE TABLE BORROWER ("
				+ "Card_id CHAR(8) CHECK (REGEXP_LIKE(Card_id, 'ID[:digit:]{6}', 'c')) NOT NULL, "
				+ "Ssn CHAR(11) CHECK (REGEXP_LIKE(Ssn, '[:digit:]{3}-[:digit:]{2}-[:digit:]{4}')) NOT NULL, "
				+ "Bname VARCHAR(100) NOT NULL, "
				+ "Address VARCHAR(100) NOT NULL, "
				+ "Phone CHAR(14) CHECK (REGEXP_LIKE(Phone, '\\\\([:digit:]{3}\\\\) [:digit:]{3}-[:digit:]{4}')), "
				+ "PRIMARY KEY (Card_id), "
				+ "UNIQUE (Ssn));");
		executeSQLUpdateCommand("DROP TABLE IF EXISTS BOOK_LOANS;");
		executeSQLUpdateCommand("CREATE TABLE BOOK_LOANS ("
				+ "Loan_id CHAR(8) CHECK (REGEXP_LIKE(Loan_id, 'LO[:digit:]{6}', 'c')) NOT NULL, "
				+ "Isbn CHAR(10) CHECK (REGEXP_LIKE(Isbn, '[:digit:]{9}[[:digit:]X]', 'c')) NOT NULL, "
				+ "Card_id CHAR(8) CHECK (REGEXP_LIKE(Card_id, 'ID[:digit:]{6}', 'c')) NOT NULL, "
				+ "Date_out DATE NOT NULL, "
				+ "Due_date DATE NOT NULL, "
				+ "Date_in DATE, "
				+ "PRIMARY KEY (Loan_id), "
				+ "FOREIGN KEY (Isbn) REFERENCES BOOK (Isbn), "
				+ "FOREIGN KEY (Card_id) REFERENCES BORROWER (Card_id));");
		executeSQLUpdateCommand("DROP TABLE IF EXISTS FINES;");
		executeSQLUpdateCommand("CREATE TABLE FINES ("
				+ "Loan_id CHAR(8) CHECK (REGEXP_LIKE(Loan_id, 'LO[:digit:]{6}', 'c')) NOT NULL, "
				+ "Fine_amt DECIMAL(4, 2) NOT NULL, "
				+ "Paid BOOLEAN NOT NULL, "
				+ "PRIMARY KEY (Loan_id), "
				+ "FOREIGN KEY (Loan_id) REFERENCES BOOK_LOANS (Loan_id));");

	}

	public static void populateLibraryDatabasefromBooks(String[] args) throws IOException {

		// if a book author is unknown, use Author_id value 'AU000000'
		executeSQLUpdateCommand("INSERT AUTHORS VALUES ('AU000000', NULL);");

		// the LinkedList stores separated mutable substrings from each line of the books.csv file
		LinkedList<StringBuffer> bookInformation = new LinkedList<>();

		// the HashMap stores Author_id values for every Name key; for the book_authors table
		HashMap<String, String> authorToIdMap = new HashMap<>();

		// args[0] is books.csv
		FileReader input = new FileReader(new File(args[0]));

		// skip past the first line filled with attribute information
		while (input.read() != '\n') {}
		int charRead = input.read();
		while (charRead != -1) {
			// read every character in the respective line of books.csv until newline or end-of-file
			String output = "";
			for (; charRead != '\n' && charRead != -1; charRead = input.read()) {
				output += (char)charRead;
			}

			// bookInformation.get(0) contains the value for Isbn
			// bookInformation.get(1) contains the value for Title
			// bookInformation.get(2) contains the value for the first Author or "" if no author
			// any other elements in bookInformation are for secondary authors
			bookInformation.add(new StringBuffer());
			int bookInformationIndex = 0;

			// no more data is needed at the Cover column, which has data always begin with "http"
			for (int charIndex = 0; output.substring(charIndex, charIndex + 4).compareTo("http") != 0; charIndex++) {
				// data is tab-separated and comma-separated, but check if any data information needs to be merged via space or digit (during Title processing)
				if (output.charAt(charIndex) == '\t' || output.charAt(charIndex) == ',') {
					if (bookInformationIndex > 1 && (output.charAt(charIndex + 1) == ' ' || output.charAt(charIndex + 1) >= '0' && output.charAt(charIndex + 1) <= '9')) {
						continue;
					}
					if (bookInformationIndex++ > 0) { // do not add a new StringBuffer for ISBN-13 data
						bookInformation.add(new StringBuffer());
					}
				} else if (bookInformationIndex == 0) {
					if (output.charAt(charIndex) == '\'') { // whenever an apostrophe is reached, it should be appended twice so MySQL does not interpret it as the end of the string
						bookInformation.get(0).append(output.charAt(charIndex));
					}
					bookInformation.get(0).append(output.charAt(charIndex));
				} else if (bookInformationIndex > 1) {
					if (output.charAt(charIndex) == '\'') {
						bookInformation.get(bookInformationIndex - 1).append(output.charAt(charIndex));
					}
					bookInformation.get(bookInformationIndex - 1).append(output.charAt(charIndex));
				}
			}
			if (bookInformation.size() > 3) { // remove extra "" element in bookInformation if there is an author known
				bookInformation.removeLast();
			}
			for (int i = 2; i < bookInformation.size() - 1; i++) { // if an author's name exists more than once for this book, remove all duplicates
				for (int j = i + 1; j < bookInformation.size(); j++) {
					if (bookInformation.get(i).toString().compareToIgnoreCase(bookInformation.get(j).toString()) == 0) {
						bookInformation.remove(j--);
					}
				}
			}

			// insert book tuple containing Isbn and Title
			executeSQLUpdateCommand("INSERT BOOK VALUES ('"
					+ bookInformation.get(0).toString() + "', '"
					+ bookInformation.get(1).toString() + "');");

			// calculate leading zeroes for Author_id by factoring in digits of current authorIdNumber
			String leadingZeroes = "";
			for (int i = (int)Math.log10(authorIdNumber); i < 5; i++) {
				leadingZeroes += "0";
			}
			bookInformationIndex = 2; // reset to 2 to begin dealing with first author
			do {
				// set Author_id values for new authors as necessary, and then insert authors tuple containing Author_id and Name
				if (!bookInformation.get(bookInformationIndex).toString().isEmpty() && authorToIdMap.get(bookInformation.get(bookInformationIndex).toString().toUpperCase()) == null) {
					// if an author has not been assigned an ID yet, do so and save it as a value in the HashMap; toUpperCase ensures no author gets multiple ID's
					authorToIdMap.put(bookInformation.get(bookInformationIndex).toString().toUpperCase(), "AU" + leadingZeroes + authorIdNumber);
					executeSQLUpdateCommand("INSERT AUTHORS VALUES ('AU"
							+ leadingZeroes + authorIdNumber + "', '"
							+ bookInformation.get(bookInformationIndex).toString() + "');");

					// always check whether a leading zero needs to be erased to make room for the next authorIdNumber
					if (++authorIdNumber == 10 || authorIdNumber == 100 || authorIdNumber == 1000
							|| authorIdNumber == 10000 || authorIdNumber == 100000) {
						leadingZeroes = leadingZeroes.substring(1);
					}
				}

				// obtain the Author_id for the current author (or the ID for no author), and then insert book_authors tuple containing Author_id and Isbn
				String authorIdLiteral = (bookInformation.get(bookInformationIndex).toString().isEmpty()) ? "'AU000000'" : "'" + authorToIdMap.get(bookInformation.get(bookInformationIndex).toString().toUpperCase()) + "'";
				executeSQLUpdateCommand("INSERT BOOK_AUTHORS VALUES ("
						+ authorIdLiteral + ", '"
						+ bookInformation.get(0).toString() + "');");
			} while (++bookInformationIndex < bookInformation.size()); // if multiple authors wrote this book, repeat the loop for the next author

			// clear bookInformation for the next line, and then read the first character of the next line to see if it is end-of-file; if not, process another book
			bookInformation.clear();
			charRead = input.read();
		}
		input.close(); // close the FileReader when all characters in books.csv are processed

	}

	public static void populateLibraryDatabasefromBorrowers(String[] args) throws IOException {

		// the LinkedList stores separated mutable substrings from each line of the borrowers.csv file
		LinkedList<StringBuffer> borrowerInformation = new LinkedList<>();

		// args[0] is borrowers.csv
		FileReader input = new FileReader(new File(args[1]));

		// skip past the first line filled with attribute information
		while (input.read() != '\n') {}
		int charRead = input.read();
		while (charRead != -1) {
			// read every character in the respective line of books.csv until newline or end-of-file
			String output = "";
			for (; charRead != '\n' && charRead != -1; charRead = input.read()) {
				output += (char)charRead;
			}

			// borrowerInformation.get(0) contains the value for Card_id
			// borrowerInformation.get(1) contains the value for Ssn
			// borrowerInformation.get(2) contains the value for Bname
			// borrowerInformation.get(3) contains the value for Address
			// borrowerInformation.get(4) contains the value for Phone
			borrowerInformation.add(new StringBuffer());
			int borrowerInformationIndex = 0;

			// read to the last character of the phone column
			for (int charIndex = 0; charIndex < output.lastIndexOf(',') + 15; charIndex++) {
				// data is comma-separated only, but with data from some columns in need of merging, append the right separators in those cases
				if (output.charAt(charIndex) == ',') {
					if (borrowerInformationIndex == 2) {
						borrowerInformation.get(borrowerInformationIndex++).append(' ');
						continue;
					}
					if (borrowerInformationIndex == 5 || borrowerInformationIndex == 6) {
						borrowerInformation.get(3).append(", ");
						borrowerInformationIndex++;
						continue;
					}
					if (borrowerInformationIndex++ != 3) { // do not add a new StringBuffer for email data
						borrowerInformation.add(new StringBuffer());
					}
				} else if (borrowerInformationIndex < 3) {
					if (output.charAt(charIndex) == '\'') { // whenever an apostrophe is reached, it should be appended twice so MySQL does not interpret it as the end of the string
						borrowerInformation.get(borrowerInformationIndex).append(output.charAt(charIndex));
					}
					borrowerInformation.get(borrowerInformationIndex).append(output.charAt(charIndex));
				} else if (borrowerInformationIndex == 3) {
					if (output.charAt(charIndex) == '\'') {
						borrowerInformation.get(2).append(output.charAt(charIndex));
					}
					borrowerInformation.get(2).append(output.charAt(charIndex));
				} else if (borrowerInformationIndex > 4 && borrowerInformationIndex < 8) {
					if (output.charAt(charIndex) == '\'') {
						borrowerInformation.get(3).append(output.charAt(charIndex));
					}
					borrowerInformation.get(3).append(output.charAt(charIndex));
				} else if (borrowerInformationIndex == 8) {
					if (output.charAt(charIndex) == '\'') {
						borrowerInformation.get(4).append(output.charAt(charIndex));
					}
					borrowerInformation.get(4).append(output.charAt(charIndex));
				}
			}

			// insert borrowers tuple
			executeSQLUpdateCommand("INSERT BORROWER VALUES ('"
					+ borrowerInformation.get(0).toString() + "', '"
					+ borrowerInformation.get(1).toString() + "', '"
					+ borrowerInformation.get(2).toString() + "', '"
					+ borrowerInformation.get(3).toString() + "', '"
					+ borrowerInformation.get(4).toString() + "');");

			// clear borrowerInformation for the next line, and then read the first character of the next line to see if it is end-of-file; if not, process another borrower
			borrowerInformation.clear();
			charRead = input.read();
		}
		input.close(); // close the FileReader when all characters in borrowers.csv are processed

	}

	public static void initializeLibraryDatabase(String[] args) throws IOException {
		createLibraryDatabase();
		populateLibraryDatabasefromBooks(args);
		populateLibraryDatabasefromBorrowers(args);
	}

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		try { // connect to MySQL or terminate
			Class.forName("com.mysql.cj.jdbc.Driver").getDeclaredConstructor().newInstance();
		} catch (Exception ex) {
			System.out.println("Could not make a Connection with this JDBC driver.");
			System.out.println("Exception: " + ex.getMessage());
			System.exit(1);
		}
		try { // connect to MySQL
			conn = DriverManager.getConnection(GlobalConnectionInformation.GlobalDatabaseConn, 
            		GlobalConnectionInformation.GlobalDatabaseUser,
            		GlobalConnectionInformation.GlobalDatabasePass);
		} catch (SQLException ex) {
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());
			System.exit(1);
		}
		//initializeLibraryDatabase(args);
	}

}
