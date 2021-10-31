
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.sql.Connection;

public class Fines extends JFrame {

    JFrame J_MainFrame;
    JPanel J_Control;
    static Connection conn;

    Fines() {
        prepareGUI();
    }

    void prepareGUI() {
        J_MainFrame = new JFrame("Library Management System");
        J_MainFrame.setSize(500, 300);
        J_MainFrame.setLocation(20, 50);
        J_MainFrame.setLocationRelativeTo(null);

        J_Control = new JPanel();
        J_Control.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(J_Control);
        GridBagLayout gbl_J_Control = new GridBagLayout();
        gbl_J_Control.columnWidths = new int[]{0, 0, 0};
        gbl_J_Control.rowHeights = new int[]{0, 0, 0, 0, 0, 0};
        gbl_J_Control.columnWeights = new double[]{Double.MIN_VALUE, Double.MIN_VALUE, Double.MIN_VALUE};
        gbl_J_Control.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
        J_Control.setLayout(gbl_J_Control);

        //update Fines table
        try {
            conn = DriverManager.getConnection(GlobalConnectionInformation.GlobalDatabaseConn, 
            		GlobalConnectionInformation.GlobalDatabaseUser,
            		GlobalConnectionInformation.GlobalDatabasePass);
            Statement stmt1 = conn.createStatement();
            String date = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
         
            ResultSet rs1 = stmt1.executeQuery("Select * from library.book_loans where Due_date<'" + date + "';");
            while (rs1.next()) {
                String loan_id = rs1.getString(1);
                String dueDate = rs1.getString(5);

                DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                java.util.Date due = format.parse(dueDate);
                java.util.Date today = format.parse(date);

                double x = (today.getTime() - due.getTime());

                x = x / (1000 * 60 * 60 * 24);
                double fine = 0.25 * x;

                Statement stmt2 = conn.createStatement();
                ResultSet rs2 = stmt2.executeQuery("Select * from library.fines where Loan_id='" + loan_id + "' and Paid=0;");

                if (rs2.next()) {
                    Statement stmt3 = conn.createStatement();
                    stmt3.executeUpdate("Update library.fines set Fine_amt=" + fine + " where Loan_id='" + loan_id + "';");
                } else {
                    Statement stmt5 = conn.createStatement();
                    ResultSet rs5 = stmt5.executeQuery("Select * from library.fines where Loan_id='" + loan_id + "' and Paid=1;");

                    if (!rs5.next()) {
                        Statement stmt4 = conn.createStatement();
                        stmt4.executeUpdate("INSERT INTO library.fines (Loan_id,Fine_amt, Paid) VALUES ('" + loan_id + "', '" + fine + "',0);");
                    }
                }
            }
            conn.close();
        } catch (SQLException e1) {
            // TODO Auto-generated catch block
            System.out.println("Sql Error : " + e1.getMessage());
        } catch (ParseException e1) {
            // TODO Auto-generated catch block
            System.out.println("Parse Exception : " + e1.getMessage());
        }

        //Header
        JLabel header = new JLabel("Fines", JLabel.CENTER);
        header.setFont(new Font("Aerial", Font.BOLD, 20));
        GridBagConstraints Grid_header = new GridBagConstraints();
        Grid_header.insets = new Insets(0, 0, 5, 0);
        Grid_header.gridx = 0;
        Grid_header.gridy = 1;
        Grid_header.gridwidth = 3;
        J_Control.add(header, Grid_header);
        //given space
        JLabel Given_Space = new JLabel("  ", JLabel.CENTER);
        GridBagConstraints Grid_Given_Space = new GridBagConstraints();
        Grid_Given_Space.insets = new Insets(0, 0, 5, 0);
        Grid_Given_Space.gridx = 0;
        Grid_Given_Space.gridy = 2;
        Grid_Given_Space.gridwidth = 3;
        J_Control.add(Given_Space, Grid_Given_Space);

        //Borrower Card No.
        JLabel Borrower_Card_Number = new JLabel("Card No.*:", JLabel.LEFT);
        Borrower_Card_Number.setFont(new Font("Aerial", Font.BOLD, 14));
        GridBagConstraints Grid_Borrower_Card_Number = new GridBagConstraints();
        Grid_Borrower_Card_Number.insets = new Insets(0, 0, 5, 0);
        Grid_Borrower_Card_Number.gridx = 0;
        Grid_Borrower_Card_Number.gridy = 3;
        J_Control.add(Borrower_Card_Number, Grid_Borrower_Card_Number);

        //Card No.Text Box
        JTextField Card_number_text = new JTextField();
        Card_number_text.setFont(new Font("Ariel", Font.PLAIN, 14));
        Card_number_text.setForeground(Color.black);
        GridBagConstraints Grid_Card_number_text = new GridBagConstraints();
        Grid_Card_number_text.fill = GridBagConstraints.HORIZONTAL;
        Grid_Card_number_text.insets = new Insets(0, 0, 5, 0);
        Grid_Card_number_text.gridx = 1;
        Grid_Card_number_text.gridy = 3;
        Grid_Card_number_text.gridwidth = 2;
        J_Control.add(Card_number_text, Grid_Card_number_text);

        Card_number_text.setColumns(15);

        //All Fines Due
        JButton finesDue = new JButton("All Fines Due");
        finesDue.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                		if (Card_number_text.getText().isEmpty()) {
                        	new FinesDue("");
                		} else {
                        	new FinesDue(Card_number_text.getText());
                		}
                } catch (SQLException e1) {
                	JOptionPane.showMessageDialog(null, "Unknown Error. Please call the help desk.");
                }
            }
        });
        GridBagConstraints Grid_finesDue = new GridBagConstraints();
        Grid_finesDue.insets = new Insets(0, 0, 5, 0);
        Grid_finesDue.gridx = 0;
        Grid_finesDue.gridy = 4;
        J_Control.add(finesDue, Grid_finesDue);

        //Total Amount Due
        JButton totalDue = new JButton("Total Amount Due");
        totalDue.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                try {
                    conn = DriverManager.getConnection(GlobalConnectionInformation.GlobalDatabaseConn, 
                    		GlobalConnectionInformation.GlobalDatabaseUser,
                    		GlobalConnectionInformation.GlobalDatabasePass);
                    Statement stmt1 = conn.createStatement();
                    String card_id = Card_number_text.getText();
                    ResultSet rs1 = stmt1.executeQuery("Select b.Card_id, SUM(f.Fine_amt) from library.fines as f left outer join library.book_loans as b on b.Loan_id=f.Loan_id where f.Paid=0 group by b.Card_id having b.Card_id='" + card_id + "';");
                    if (rs1.next()) {
                        BigDecimal fine = rs1.getBigDecimal(2);
                        JOptionPane.showMessageDialog(null, "Total Fine Amount Due : " + fine.toString());
                    } else {
                    	if (card_id.isEmpty()) {
                    		JOptionPane.showMessageDialog(null, "Please enter a Card ID to view Total Amount of Fines.");
                    	} else {
                            JOptionPane.showMessageDialog(null, "No Fines Due");
                    	}
                    }
                } catch (SQLException e1) {
                    // TODO Auto-generated catch block
                	JOptionPane.showMessageDialog(null, "Unknown Error. Please call the help desk.");
                    System.out.println("SQL Exception : " + e1.getMessage());
                }

            }
        });
        GridBagConstraints Grid_totalDue = new GridBagConstraints();
        Grid_totalDue.insets = new Insets(0, 0, 5, 0);
        Grid_totalDue.gridx = 2;
        Grid_totalDue.gridy = 4;
        J_Control.add(totalDue, Grid_totalDue);

        JLabel Given_Space2 = new JLabel("  ", JLabel.CENTER);
        GridBagConstraints Grid_Given_Space2 = new GridBagConstraints();
        Grid_Given_Space2.insets = new Insets(0, 0, 5, 0);
        Grid_Given_Space2.gridx = 0;
        Grid_Given_Space2.gridy = 5;
        Grid_Given_Space2.gridwidth = 2;
        J_Control.add(Given_Space2, Grid_Given_Space2);

        //Close Button
        JButton close = new JButton("Back");
        close.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                J_MainFrame.setVisible(false);
                //new LMS();
            }
        });
        GridBagConstraints Grid_Close_Button = new GridBagConstraints();
        Grid_Close_Button.fill = GridBagConstraints.HORIZONTAL;
        Grid_Close_Button.insets = new Insets(0, 0, 5, 0);
        Grid_Close_Button.gridx = 0;
        Grid_Close_Button.gridy = 6;
        Grid_Close_Button.anchor = GridBagConstraints.PAGE_END;
        Grid_Close_Button.gridwidth = 3;
        J_Control.add(close, Grid_Close_Button);

        J_MainFrame.add(J_Control);
        J_MainFrame.setVisible(true);
    }
}
