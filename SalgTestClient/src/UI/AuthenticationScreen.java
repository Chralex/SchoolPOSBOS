package UI;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import Client.Frontend;

public class AuthenticationScreen {

	JFrame frame;
	JPanel mainPanel;
	Dimension dimensions = Toolkit.getDefaultToolkit().getScreenSize();
	
	public JLabel authStatusLabel;
	
	public AuthenticationScreen() {
		frame = new JFrame("School POS - Login");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(450, 350);
		frame.setLocation(
			new Point(
				(int)(dimensions.getWidth() / 2) - frame.getWidth() / 2,
				(int)(dimensions.getHeight() /2 ) - frame.getHeight() / 2
			)
		);
		
		GridBagLayout layout = new GridBagLayout();		

		mainPanel = new JPanel(layout);
		mainPanel.setBackground(new Color(45, 45, 45));
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.insets = new Insets(0,90,0,90);
		//constraints.anchor = GridBagConstraints.CENTER;
		
		layout.setConstraints(mainPanel, constraints);

		class Listener implements MouseListener {
			
			private AuthenticationScreen mainPanel;
			
			Listener() {
				
			}
			
			Listener(AuthenticationScreen frameClass) {
				this.mainPanel = frameClass;
			}
			
			@Override
			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub
				Component[] components = ((JButton)arg0.getSource()).getParent().getComponents();
				
				String username = "test";
				String password = "test";
				
				for (Component component : components) {
					if (!username.equals("") && !password.equals(""))
						break;
					
					switch (component.getName()) {
						case "UsernameField":
							username = ((JTextField)component).getText();
						break;
						
						case "PasswordField":
							password = ((JTextField)component).getText();
						break;
					}
				}

				this.mainPanel.authStatusLabel.setVisible(true);
				if (Communication.Authentication.LoginAPI.Login(username, password)) {
					new Frontend();
					this.mainPanel.authStatusLabel.setText("Succesfully authenticated - Taking you to the main screen.");
					mainPanel.frame.setVisible(false);
					mainPanel.frame.dispose();
				}
				else
					this.mainPanel.authStatusLabel.setText("Invalid username or password.");
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}	
		}
		
		JButton btn = new JButton();
		btn.setName("LoginButton");
		btn.setText("Login");
		btn.setAlignmentY(GridBagConstraints.PAGE_END);
		btn.setLocation(new Point((350 / 2) - btn.getWidth() / 2, 325));
		btn.addMouseListener(new Listener(this));
	
		class fieldFoucsHandler implements FocusListener 
		{
			@Override
			public void focusGained(FocusEvent e) {
				
				if (e.getSource().getClass() == JTextField.class) {
					JTextField field = (JTextField)e.getSource();
					field.setText("");
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				if (e.getSource().getClass() == JTextField.class && ((JTextField)e.getSource()).getText() == "") {
					JTextField field = (JTextField)e.getSource();
					field.setText(field.getToolTipText());
				}
			}
			
		}
		
		JTextField loginField = new JTextField();
		loginField.setAlignmentY(GridBagConstraints.PAGE_START);
		loginField.setPreferredSize(new Dimension(250, 24));
		loginField.addFocusListener(new fieldFoucsHandler());
		loginField.setName("UsernameField");
		loginField.setText("Username");
		loginField.setToolTipText("Username");
		
		JPasswordField passwordField = new  JPasswordField();
		passwordField.setAlignmentY(GridBagConstraints.PAGE_START);
		passwordField.setPreferredSize(new Dimension(250, 24));
		passwordField.addFocusListener(new fieldFoucsHandler());
		passwordField.setName("PasswordField");
		passwordField.setText("Login");
		passwordField.setToolTipText("Login");
		
		authStatusLabel = new JLabel();
		authStatusLabel.setName("StausLabel");
		authStatusLabel.setVisible(false);
		authStatusLabel.setAlignmentX(GridBagConstraints.CENTER);
		authStatusLabel.setForeground(Color.white);

		constraints.weightx = 1;
		constraints.weighty = 0.25;
		
		frame.add(mainPanel);
		
		constraints.fill = GridBagConstraints.HORIZONTAL;
		mainPanel.add(loginField, constraints);
			
		constraints.gridy = 1;
		mainPanel.add(passwordField, constraints);
		
		constraints.gridy = 3;
		constraints.fill = GridBagConstraints.NONE;
		mainPanel.add(btn, constraints);
		
		constraints.gridy = 2;
		constraints.fill = GridBagConstraints.CENTER;
		mainPanel.add(authStatusLabel, constraints);
		
		
		frame.setVisible(true);
		
		mainPanel.updateUI();
	}
	
}
