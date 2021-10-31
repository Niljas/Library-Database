import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.JTextPane;
import javax.swing.border.TitledBorder;
import javax.swing.border.EtchedBorder;

public class HomeScreen {

	JFrame frame;
	
	// Search bar
	JTextField SearchBox;
	
	// System Messages
	JTextPane SystemMessages;
	
	// List display
	JScrollPane listQueryDisplay;
	
	// Button Controls
	JButton btnSearch;
	JButton btnCreateNewBorrower;
	JButton btnCheckinBook;
	JButton btnCheckoutBook;
	JButton btnPayFine;
	JButton btnAssessFines;

	/**
	 * Create the application.
	 */
	public HomeScreen() {
		initialize();
	}
	
	public void Refresh() {
		frame.revalidate();
		frame.repaint();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 635, 362);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		
		SearchBox = new JTextField();
		SearchBox.setColumns(10);
		
		// Make JList Query Display
		listQueryDisplay = new JScrollPane();
		
		// Make Buttons
		btnSearch = new JButton("Search");
		btnCreateNewBorrower = new JButton("Create New Borrower");
		btnCheckinBook = new JButton("Check-in Book");
		btnCheckoutBook = new JButton("Check-out Book");
		btnPayFine = new JButton("Pay Fine");
		btnAssessFines = new JButton("Assess Fines");
		
		// -------------------- Status Messages ------------------------- //
		// Create Status Messages Text Pane and setup properties
		SystemMessages = new JTextPane();
		SystemMessages.setEditable(false);
		// Give border
		SystemMessages.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED, null, null), 
									 "Status Messages", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		// Get rid of background color
		SystemMessages.setOpaque(false);
		// Make it unclickable
		SystemMessages.setFocusable(false);
		
		// Create styles for centering text and italicizing text
		StyledDocument doc = SystemMessages.getStyledDocument();
		SimpleAttributeSet center = new SimpleAttributeSet();
		center.addAttribute(StyleConstants.CharacterConstants.Italic, Boolean.TRUE);
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
		doc.setParagraphAttributes(0, doc.getLength(), center, false);
		// -------------------- Status Messages ------------------------- //
		
		SystemMessages.setText("Welcome to Team Purple's Library Management System. Search for a book or select one of the controls to get started.");
		
		JTextPane txtpnLmsVUniversity = new JTextPane();
		txtpnLmsVUniversity.setContentType("text/html");
		txtpnLmsVUniversity.setText("<center><i>LMS v1.0 <br>University of Texas at Dallas</i></center>");
		txtpnLmsVUniversity.setOpaque(false);
		txtpnLmsVUniversity.setFocusable(false);
		txtpnLmsVUniversity.setEditable(false);
		
		
		GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addComponent(SystemMessages, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 434, GroupLayout.PREFERRED_SIZE)
						.addComponent(SearchBox, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 434, GroupLayout.PREFERRED_SIZE)
						.addComponent(listQueryDisplay, GroupLayout.DEFAULT_SIZE, 434, Short.MAX_VALUE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
						.addComponent(btnCreateNewBorrower, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
						.addComponent(btnCheckinBook, GroupLayout.DEFAULT_SIZE, 137, Short.MAX_VALUE)
						.addComponent(btnCheckoutBook, GroupLayout.DEFAULT_SIZE, 137, Short.MAX_VALUE)
						.addComponent(btnPayFine, GroupLayout.DEFAULT_SIZE, 137, Short.MAX_VALUE)
						.addComponent(btnAssessFines, GroupLayout.DEFAULT_SIZE, 137, Short.MAX_VALUE)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(btnSearch, GroupLayout.DEFAULT_SIZE, 155, GroupLayout.PREFERRED_SIZE)
							.addGap(2))
						.addComponent(txtpnLmsVUniversity, GroupLayout.PREFERRED_SIZE, 137, GroupLayout.PREFERRED_SIZE))
					.addGap(32))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
						.addComponent(SearchBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnSearch))
					.addGap(18)
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addGroup(groupLayout.createSequentialGroup()
							.addComponent(btnCreateNewBorrower)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnCheckinBook)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnCheckoutBook)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnPayFine)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnAssessFines))
						.addComponent(listQueryDisplay, GroupLayout.PREFERRED_SIZE, 179, GroupLayout.PREFERRED_SIZE))
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, true)
						.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(SystemMessages, GroupLayout.PREFERRED_SIZE, 74, GroupLayout.PREFERRED_SIZE))
						.addGroup(groupLayout.createSequentialGroup()
							.addPreferredGap(ComponentPlacement.RELATED, 22, Short.MAX_VALUE)
							.addComponent(txtpnLmsVUniversity, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)))
					.addContainerGap())
		);
		frame.getContentPane().setLayout(groupLayout);
	}
}
