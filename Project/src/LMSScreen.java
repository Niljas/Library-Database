


import javax.swing.*;
import java.awt.Component;
import java.awt.LayoutManager;


public class LMSScreen {
    
	// ---------- Class Objects and Variables ---------- //
	public JFrame frame;
	public JPanel panel;
	
	// ---------- Class Constructors and Methods ---------- //
    public LMSScreen(int width, int height) {
    	
    	// Prepare JFrame
    	frame = new JFrame();
    	frame.setSize(width, height);
    	frame.setLayout(null);
    	frame.setLocationRelativeTo(null);
    	frame.setVisible(true);
    	
    	// Prepare JPanel
    	panel = new JPanel();
    	panel.setSize(width, height);
    	panel.setLayout(null);
    	panel.setVisible(true);
    }
    
    public LMSScreen(int width, int height, LayoutManager layout) {
    	
    	// Prepare JFrame
    	frame = new JFrame();
    	frame.setSize(width, height);
    	frame.setLayout(layout);
    	frame.setLocationRelativeTo(null);
    	frame.setVisible(true);
    	
    	// Prepare JPanel
    	panel = new JPanel();
    	panel.setSize(width, height);
    	panel.setLayout(layout);
    	panel.setVisible(true);
    }
    
    public void ExitOnClose() {
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    // ---------- Wrapper Methods  ---------- //
    public void add(Component component) { 
    	frame.add(component);
    }

    public void show() { frame.setVisible(true); }
    
    public void hide() { frame.setVisible(false); }
    
    
    
}
