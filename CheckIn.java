
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class CheckIn extends JFrame {

    JFrame J_MainFrame;
    JPanel J_Control;

    CheckIn() {
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
        GridBagLayout Grid_Control = new GridBagLayout();
        Grid_Control.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0};
        Grid_Control.columnWidths = new int[]{0, 0};
        Grid_Control.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
        Grid_Control.columnWeights = new double[]{Double.MIN_VALUE, Double.MIN_VALUE};
        J_Control.setLayout(Grid_Control);

        JLabel Empty = new JLabel("  ", JLabel.CENTER);
        GridBagConstraints Grid_Empty = new GridBagConstraints();
        Grid_Empty.insets = new Insets(0, 0, 5, 0);
        Grid_Empty.gridwidth = 2;
        Grid_Empty.gridx = 0;
        Grid_Empty.gridy = 1;
        J_Control.add(Empty, Grid_Empty);

        //Isbn Text Box
        JTextField Isbn_Text_Box = new JTextField();
        Isbn_Text_Box.setFont(new Font("Ariel", Font.PLAIN, 14));
        Isbn_Text_Box.setForeground(Color.black);
        GridBagConstraints Grid_Isbn_Text_Box = new GridBagConstraints();
        Grid_Isbn_Text_Box.fill = GridBagConstraints.HORIZONTAL;
        Grid_Isbn_Text_Box.insets = new Insets(0, 0, 5, 0);
        Grid_Isbn_Text_Box.gridx = 1;
        Grid_Isbn_Text_Box.gridy = 3;
        J_Control.add(Isbn_Text_Box, Grid_Isbn_Text_Box);
        Isbn_Text_Box.setColumns(15);

        //Book ISBN
        JLabel Book_Isbn = new JLabel("ISBN Number:", JLabel.LEFT);
        Book_Isbn.setFont(new Font("Ariel", Font.BOLD, 14));
        GridBagConstraints Grid_Book_Isbn = new GridBagConstraints();
        Grid_Book_Isbn.insets = new Insets(0, 0, 5, 0);
        Grid_Book_Isbn.gridx = 0;
        Grid_Book_Isbn.gridy = 3;
        J_Control.add(Book_Isbn, Grid_Book_Isbn);

        //Borrower
        JLabel Borrower = new JLabel("Borrower's Name :", JLabel.RIGHT);
        Borrower.setFont(new Font("Ariel", Font.BOLD, 14));
        GridBagConstraints Grid_Borrower = new GridBagConstraints();
        Grid_Borrower.insets = new Insets(0, 0, 5, 0);
        Grid_Borrower.gridx = 0;
        Grid_Borrower.gridy = 2;
        J_Control.add(Borrower, Grid_Borrower);

        //Borrower Text Box
        JTextField Borrower_Text_Box = new JTextField();
        Borrower_Text_Box.setFont(new Font("Ariel", Font.PLAIN, 14));
        Borrower_Text_Box.setForeground(Color.black);
        GridBagConstraints Grid_Borrower_Text_Box = new GridBagConstraints();
        Grid_Borrower_Text_Box.fill = GridBagConstraints.HORIZONTAL;
        Grid_Borrower_Text_Box.insets = new Insets(0, 0, 5, 0);
        Grid_Borrower_Text_Box.gridx = 1;
        Grid_Borrower_Text_Box.gridy = 2;
        J_Control.add(Borrower_Text_Box, Grid_Borrower_Text_Box);
        Isbn_Text_Box.setColumns(15);

        //Card No.Text Box
        JTextField Card_Text_Box = new JTextField();
        Card_Text_Box.setFont(new Font("Ariel", Font.PLAIN, 14));
        Card_Text_Box.setForeground(Color.black);
        GridBagConstraints Grid_Card_Text_Box = new GridBagConstraints();
        Grid_Card_Text_Box.fill = GridBagConstraints.HORIZONTAL;
        Grid_Card_Text_Box.insets = new Insets(0, 0, 5, 0);
        Grid_Card_Text_Box.gridx = 1;
        Grid_Card_Text_Box.gridy = 4;
        J_Control.add(Card_Text_Box, Grid_Card_Text_Box);
        Card_Text_Box.setColumns(15);

        //Borrower Card No.
        JLabel Borrower_Card_number = new JLabel(" Enter Card No. :", JLabel.RIGHT);
        Borrower_Card_number.setFont(new Font("Ariel", Font.BOLD, 14));
        GridBagConstraints Grid_Borrower_Card_number = new GridBagConstraints();
        Grid_Borrower_Card_number.insets = new Insets(0, 0, 5, 0);
        Grid_Borrower_Card_number.gridx = 0;
        Grid_Borrower_Card_number.gridy = 4;
        J_Control.add(Borrower_Card_number, Grid_Borrower_Card_number);

        JButton checkOut = new JButton("Check In");
        checkOut.addActionListener((ActionEvent e) -> {
            if (Isbn_Text_Box.getText().equals("") && Card_Text_Box.getText().equals("") && Borrower_Text_Box.getText().equals("")) {
                JOptionPane.showMessageDialog(null, "Fill in the box");
            } else {
                try {

                    new CheckInList(Isbn_Text_Box.getText(), Card_Text_Box.getText(), Borrower_Text_Box.getText());
                } catch (SQLException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        });
        GridBagConstraints Grid_CheckOut = new GridBagConstraints();
        Grid_CheckOut.fill = GridBagConstraints.HORIZONTAL;
        Grid_CheckOut.insets = new Insets(0, 0, 5, 0);
        Grid_CheckOut.gridx = 0;
        Grid_CheckOut.gridy = 5;
        Grid_CheckOut.gridwidth = 2;
        J_Control.add(checkOut, Grid_CheckOut);

        JLabel Extra_Space = new JLabel("  ", JLabel.CENTER);
        GridBagConstraints Grid_Extra_Space = new GridBagConstraints();
        Grid_Extra_Space.insets = new Insets(0, 0, 5, 0);
        Grid_Extra_Space.gridx = 0;
        Grid_Extra_Space.gridy = 6;
        Grid_Extra_Space.gridwidth = 2;
        J_Control.add(Extra_Space, Grid_Extra_Space);

        //Back/Close Button
        JButton Back = new JButton("Back");
        Back.addActionListener((ActionEvent e) -> {
            J_MainFrame.setVisible(false);
            //new MainPage(); // Main Page
        });
        GridBagConstraints Grid_Back = new GridBagConstraints();
        Grid_Back.fill = GridBagConstraints.HORIZONTAL;
        Grid_Back.insets = new Insets(0, 0, 5, 0);
        Grid_Back.gridx = 0;
        Grid_Back.gridy = 7;
        Grid_Back.anchor = GridBagConstraints.PAGE_END;
        Grid_Back.gridwidth = 2;
        J_Control.add(Back, Grid_Back);

        J_MainFrame.add(J_Control);
        J_MainFrame.setVisible(true);
    }
}
