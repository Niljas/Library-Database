import java.awt.Dimension;
import java.awt.Rectangle;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

public class QueryDisplay {

	static private JScrollPane refScrollPane;
	static private HomeScreen refHomeScreen;
	JScrollPane listScrollPane;
	private JFrame referenceFrame;
	private DefaultListModel<String> displayModel;
	
	
	public QueryDisplay(LMSScreen screen) {
		referenceFrame = screen.frame;
	}
	
	public QueryDisplay(HomeScreen screen, JScrollPane sp) {
		refHomeScreen = screen;
		referenceFrame = screen.frame;
		refScrollPane = sp;
	}
	
	private void InitDisplay() {
		
		// Get Reference ScrollPane's coordinates
		Rectangle bounds = refScrollPane.getBounds();
		Dimension prefsize = refScrollPane.getPreferredSize();
		
		// Init list
		JList<String> display = new JList<String>(displayModel);
		display.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		display.setLayoutOrientation(JList.VERTICAL);
		display.setSelectedIndex(0);
		//display.setVisibleRowCount(2);
		
		listScrollPane = new JScrollPane(display);
		listScrollPane.setPreferredSize(prefsize);
		listScrollPane.setBounds(bounds);
		
		// Set Currently Displayed Scroll Pane as invisible
		// Set the new scroll pane as the reference, and
		// delete old reference
		referenceFrame.remove(refScrollPane);
		refHomeScreen.listQueryDisplay = listScrollPane;
		
		// Show Query
		referenceFrame.add(listScrollPane);
		referenceFrame.revalidate();
		referenceFrame.repaint();
	}
	
	// Displays the items in ResultSet items to the 
	// display display
	public void DisplayResults(Statement myStmt, ResultSet items, String search) throws SQLException {
		
		// Return if null
		if (items == null) return;
		
		// Initialize list model
		displayModel = new DefaultListModel<String>();
		
		// Get MetaData
		ResultSetMetaData itemsmd = items.getMetaData();
		
		// Obtain maximum lengths of titles and authors for adequate spacing
		int maxTitleLength = itemsmd.getColumnDisplaySize(2);
		int authorLength = 0;
		int maxAuthorLength = 0;
		String authors = "";
		while (items.next()) {
			if (items.getRow() == 1) {
				authors += items.getString(3);
				authorLength = authors.length();
			}
			else {
				String currentRowIsbn = items.getString(1);
				items.previous();
				String previousRowIsbn = items.getString(1);
				items.next();
				if (currentRowIsbn.equals(previousRowIsbn)) {
					authors += ", " + items.getString(3);
					authorLength = authors.length();
				}
				else {
					maxAuthorLength = Math.max(authorLength, maxAuthorLength);
					authors = items.getString(3);
					authorLength = authors.length();
				}
			}
		}
		maxAuthorLength = Math.max(authorLength, maxAuthorLength);

		// Display column names
		String columnLabels = "ISBN                   Book title";
		for (int i = 0; i < maxTitleLength + 5; i++) {
			columnLabels += " ";
		}
		columnLabels += "Book author(s)";
		for (int i = -4; i < maxAuthorLength + 5; i++) {
			columnLabels += " ";
		}
		columnLabels += "Book availability";
		displayModel.addElement(columnLabels);

		// Return to the beginning to start displaying data
		items.beforeFirst();

		// Next Item to add
		String nextItem = "";
		authors = "";
		int rowCount = 0;

		while (items.next()) {
			if (rowCount++ == 0) {
				nextItem += items.getString(1) + "     " + items.getString(2);
				for (int i = items.getString(2).length(); i < maxTitleLength + 5; i++) {
					nextItem += " ";
				}
				authors += items.getString(3);
				authorLength = authors.length();
			}
			else {
				String currentRowIsbn = items.getString(1);
				items.previous();
				String previousRowIsbn = items.getString(1);
				items.next();
				if (currentRowIsbn.equals(previousRowIsbn)) {
					authors += ", " + items.getString(3);
					authorLength = authors.length();
				}
				else {
					nextItem += authors;
					for (int i = authorLength; i < maxAuthorLength + 5; i++) {
						nextItem += " ";
					}
					items = myStmt.executeQuery("SELECT Isbn FROM BOOK_LOANS "
							+ "WHERE Isbn='" + previousRowIsbn + "';");
					nextItem += (items.next()) ? "NO" : "YES";

					// Add new item
					displayModel.addElement(nextItem);

					// Obtain previous query
					items = myStmt.executeQuery("select b.Isbn, b.Title, a.Name from book as b, authors as a, book_authors as ba where "
							+ "ba.Isbn=b.Isbn and ba.Author_id=a.Author_id and (b.Title like '%" + search + "%' or a.Name like "
							+ "'%" + search + "%' or b.Isbn like '%" + search + "%') order by b.Isbn");
					items.absolute(rowCount);
					nextItem = items.getString(1) + "     " + items.getString(2);
					for (int i = items.getString(2).length(); i < maxTitleLength + 5; i++) {
						nextItem += " ";
					}
					authors = items.getString(3);
					authorLength = authors.length();
				}
			}
		}
		nextItem += authors;
		for (int i = authorLength; i < maxAuthorLength + 5; i++) {
			nextItem += " ";
		}
		items.last();
		items = myStmt.executeQuery("SELECT Isbn FROM BOOK_LOANS "
				+ "WHERE Isbn='" + items.getString(1) + "';");
		nextItem += (items.next()) ? "NO" : "YES";

		// Add final item
		displayModel.addElement(nextItem);
		
		// Initialize the display
		InitDisplay();

		// Reset items before returning
		items = myStmt.executeQuery("select b.Isbn, b.Title, a.Name from book as b, authors as a, book_authors as ba where "
				+ "ba.Isbn=b.Isbn and ba.Author_id=a.Author_id and (b.Title like '%" + search + "%' or a.Name like "
				+ "'%" + search + "%' or b.Isbn like '%" + search + "%') order by b.Isbn");
	}
	
	
	
}




