package Client;

import javax.swing.*;

import Communication.ProductAPI;
import DatabaseModel.Tables.Product;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class Frontend 
{
	JFrame jFrame;
	JPanel mainLayout;
	JLabel priceLabel, moneyReceivedLabel, cashBackLabel, receiptLabel;
	
	//private Product productsArray[];
	private Map<Integer, Product> products = new HashMap<Integer, Product>(); 
	List<Integer> cartList = new ArrayList<Integer>();
	
	int gridX = 1;
	int gridY = 1;
	
	boolean removeItemsButtonIsSelected = false;
	
	//double price = 00.00;
	
	String moneyReceived = "0.0";

//	public static void main(String[] args) 
//	{
//		new Frontend();
//	}
	
	public Frontend() 
	{
		refreshProducts();
		initGUI();
	}
	
	public void refreshProducts()
	{
		try
		{
			ArrayList<Product> productsTmp = ProductAPI.getProducts();

			for (int i = 0; i < productsTmp.size(); i++)
			{
				Product product = productsTmp.get(i);
				this.products.put(product.id, product);
			}	

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void updateCart()
	{
		String result = "<html>";
		Double price = 0.0;
		
		for(Integer i : cartList)
		{
			result += products.get(i).name + ": " + products.get(i).price + "<br>";
			
			price += products.get(i).price;
		}
		result += "</html>";
		
		receiptLabel.setText(result);
		priceLabel.setText("" + price);
	}
	
	private void initGUI()
	{
		mainLayout = new POSPanel().jPanel;
		jFrame = new JFrame("POS Client");
		
		jFrame.setSize(1000, 700);
		jFrame.setVisible(true);
		jFrame.setDefaultCloseOperation(jFrame.EXIT_ON_CLOSE);
		jFrame.add(mainLayout);
		
		productsPanel();
		balancePanel();
		calcuatorPanel();
		removeItemPanel();
		receiptPanel();
		newCustomerPanel();
	}
	
	private void productsPanel()
	{
		POSPanel posPanel = new POSPanel();
		
		JPanel jPanel = posPanel.jPanel;
		GridBagConstraints gridBagCons = posPanel.gridBagCons;
		
		mainLayout.add(jPanel, gridBagCons);
		
//		System.out.println("------Start------------------------------------------------------");
		
		for (Map.Entry<Integer, Product> entry : this.products.entrySet())
		{
			Product product = entry.getValue();
			
			JButton btn = new JButton("<html>" + product.name + "<br>" + product.price + " Kr" + "</html>");
			btn.setName("" + product.id);
			
			btn.addActionListener(new ActionListener()
		    		{
		    			public void actionPerformed(ActionEvent e)
		    			{	
		    				JButton productButton = (JButton) e.getSource();
		    				
		    				if (removeItemsButtonIsSelected)
		    				{
//		    					System.out.println(cartList.indexOf(Integer.parseInt(productButton.getName())));
		    					cartList.remove(cartList.indexOf(Integer.parseInt(productButton.getName())));
		    				}
		    				else
		    				{
		    					cartList.add(Integer.parseInt(productButton.getName()));
		    				}
		    				
		    				updateCart();
		    				cashBack();
	    				}
		    		});
			
		    gridBagCons.gridx = gridX;
			gridBagCons.gridy = gridY;
			
		    jPanel.add(btn, gridBagCons);
		    gridX++;
		    
		    if (gridX == 4)
		    {
		    	gridX = 1;
		    	gridY++;
		    }
			
		    //System.out.println(product.name);
		}
		
//		System.out.println("------End------------------------------------------------------");
//		for (int i = 0; i < buttons.length; ++i)
//		{
//		    JButton btn = new JButton("<html>" + names[i] + "<br>" + prices[i] + " Kr" + "</html>");
//		    btn.setName(Integer.toString(i));
//		    
//		    btn.addActionListener(new ActionListener()
//		    		{
//		    			public void actionPerformed(ActionEvent e)
//		    			{	
//		    				if (!removeItemsButtonIsSelected)
//		    				{
//		    				price = price + prices[Integer.parseInt(((JButton)e.getSource()).getName())];
//		    				priceLabel.setText(Double.toString(price));	
//		    				}
//		    				else
//		    				{
//		    					if (Double.parseDouble(priceLabel.getText()) >= 0.0)
//		    					{
//		    					price = price - prices[Integer.parseInt(((JButton)e.getSource()).getName())];
//			    				priceLabel.setText(Double.toString(price));
//		    					}
//		    				}
//		    				
//		    				cashBack();
//	    				}
//		    		});
//		    
//		    gridBagCons.gridx = gridX;
//			gridBagCons.gridy = gridY;
//			
//		    jPanel.add(btn, gridBagCons);
//		    buttons[i] = btn;
//		    
//		    gridX++;
//		    
//		    if (gridX == 4)
//		    {
//		    	gridX = 1;
//		    	gridY++;
//		    }
//		}	
		jPanel.updateUI();
	}
	
	private void balancePanel()
	{
		POSPanel posPanel = new POSPanel();
		
		JPanel jPanel = posPanel.jPanel;
		GridBagConstraints gridBagCons = posPanel.gridBagCons;
		
		
		priceLabel = new JLabel("0.0", JLabel.CENTER);
		priceLabel.setFont(new Font("Serif", Font.PLAIN, 40));
		priceLabel.setBorder(BorderFactory.createLineBorder(Color.GREEN, 3));
		
		gridBagCons.gridx = 4;
		gridBagCons.gridy = 1;
		gridBagCons.weightx = 3;
	    
		jPanel.add(priceLabel, gridBagCons);
	    
	    
	    moneyReceivedLabel = new JLabel(moneyReceived, JLabel.CENTER);
		moneyReceivedLabel.setFont(new Font("Serif", Font.PLAIN, 40));
		moneyReceivedLabel.setBorder(BorderFactory.createLineBorder(Color.ORANGE, 3));
		
		gridBagCons.gridy = 2;
		
		jPanel.add(moneyReceivedLabel, gridBagCons);
		
		
		cashBackLabel = new JLabel("0.0", JLabel.CENTER);
		cashBackLabel.setFont(new Font("Serif", Font.PLAIN, 40));
		cashBackLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
		
		gridBagCons.gridy = 3;
		
		jPanel.add(cashBackLabel, gridBagCons);
		
		
		gridBagCons.gridx = 4;
		gridBagCons.gridy = 0;
		
		mainLayout.add(jPanel, gridBagCons);
		mainLayout.updateUI();
	}
	
	private void calcuatorPanel()
	{
		POSPanel posPanel = new POSPanel();
		
		JPanel jPanel = posPanel.jPanel;
		GridBagConstraints gridBagCons = posPanel.gridBagCons;
		
		
		//
		//gridBagCons.weightx = 1;
		gridX = 4;
		gridY = 4;
		
		for (int i = 1; i <= 9; i++)
		{
			JButton btn = new JButton(Integer.toString(i));
			
			btn.setName(Integer.toString(i));
			
			btn.addActionListener(new ActionListener()
    		{
    			public void actionPerformed(ActionEvent e)
    			{
    				if (!moneyReceivedLabel.getText().equals("0.0"))
    				{
    				moneyReceived = moneyReceived + btn.getName();
    				}
    				else
    				{
    					moneyReceived = btn.getName();
    				}
    				moneyReceivedLabel.setText(moneyReceived);
    				
    				cashBack();
    				
    			}
    		});
			btn.setFont(new Font ("Serif", Font.PLAIN, 20));
			
			gridBagCons.gridx = gridX;
			gridBagCons.gridy = gridY;
			
			jPanel.add(btn, gridBagCons);
			
			gridX++;
		    
		    if (gridX == 7)
		    {
		    	gridX = 4;
		    	gridY++;
		    }
		}
		
		
		JButton dotButton = new JButton(".");
		
		dotButton.setName(".");
		dotButton.setFont(new Font ("Serif", Font.PLAIN, 20));
		
		dotButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (moneyReceived != null)
				{
				moneyReceived = moneyReceived + dotButton.getName();
				}
				else
				{
					moneyReceived = dotButton.getName();
				}
				moneyReceivedLabel.setText(moneyReceived);
				
			}
		});
		
		gridBagCons.gridx = 4;
		gridBagCons.gridy = 7;
		
		jPanel.add(dotButton, gridBagCons);
		
		JButton zerobutton = new JButton("0");
		
		zerobutton.setName("0");
		zerobutton.setFont(new Font ("Serif", Font.PLAIN, 20));
		
		zerobutton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (moneyReceived != null)
				{
				moneyReceived = moneyReceived + zerobutton.getName();
				}
				else
				{
					moneyReceived = zerobutton.getName();
				}
				moneyReceivedLabel.setText(moneyReceived);
				
				cashBack();
			}
		});
		
		gridBagCons.gridx = 5;
		gridBagCons.gridy = 7;
		
		jPanel.add(zerobutton, gridBagCons);
		
		
		JButton backspaceButton = new JButton("Back");
		
		backspaceButton.setFont(new Font ("Serif", Font.PLAIN, 20));
		
		backspaceButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (!moneyReceived.equals("") && !moneyReceived.equals("0.0"))
				{
					moneyReceived = moneyReceived.substring(0, moneyReceived.length() - 1);
				}
				
				if (moneyReceived.equals(""))
				{
					moneyReceived = "0.0";
				}
				
				moneyReceivedLabel.setText(moneyReceived);
				
				System.out.println(moneyReceived);
				cashBack();
			}
		});
		
		gridBagCons.gridx = 6;
		gridBagCons.gridy = 7;
		
		jPanel.add(backspaceButton, gridBagCons);
		//
		
		
		gridBagCons.gridx = 4;
		gridBagCons.gridy = 4;
		//
		//jPanel.setBackground(Color.RED);
		//
		mainLayout.add(jPanel, gridBagCons);
		mainLayout.updateUI();
	}
	
	private void removeItemPanel() 
	{
		POSPanel posPanel = new POSPanel();
		
		JPanel jPanel = posPanel.jPanel;
		GridBagConstraints gridBagCons = posPanel.gridBagCons;
		
		//
		gridBagCons.gridx = 4;
		gridBagCons.gridy = 8;
		gridBagCons.weightx = 3;
		
		JButton removeItemButton = new JButton("Add Item(s)");
		
		removeItemButton.setBackground(Color.GREEN);
		
		removeItemButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (!removeItemsButtonIsSelected)
				{
					removeItemsButtonIsSelected = true;
					removeItemButton.setBackground(Color.RED);
					removeItemButton.setText("Remove Item(s)");
				}
				else
				{
					removeItemsButtonIsSelected = false;
					removeItemButton.setBackground(Color.GREEN);
					removeItemButton.setText("Add Item(s)");
				}
			}
		});
		removeItemButton.setFont(new Font ("Serif", Font.PLAIN, 20));
		
		jPanel.add(removeItemButton, gridBagCons);
		//
		
		
		//gridBagCons.gridx = 4;
		//gridBagCons.gridy = 8;
		
		mainLayout.add(jPanel, gridBagCons);
		mainLayout.updateUI();
	}
	
	private void receiptPanel()
	{
		POSPanel posPanel = new POSPanel();
		
		JPanel jPanel = posPanel.jPanel;
		GridBagConstraints gridBagCons = posPanel.gridBagCons;
		
		//
		gridBagCons.gridx = 7;
		gridBagCons.gridy = 1;
		
		
		receiptLabel = new JLabel("");
		receiptLabel.setFont(new Font("Serif", Font.PLAIN, 20));
		receiptLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
		
		jPanel.setBackground(Color.WHITE);
		jPanel.add(receiptLabel, gridBagCons);
		
		gridBagCons.weightx = 3;
		gridBagCons.gridy = 0;
		gridBagCons.weighty = 7;
		
		mainLayout.add(jPanel, gridBagCons);
		mainLayout.updateUI();
		//	
	}
	
	private void newCustomerPanel() 
	{
		POSPanel posPanel = new POSPanel();
		
		JPanel jPanel = posPanel.jPanel;
		GridBagConstraints gridBagCons = posPanel.gridBagCons;
		
		//
		gridBagCons.gridx = 7;
		gridBagCons.gridy = 8;
		gridBagCons.weightx = 3;
		
		JButton newCustomerButton = new JButton("New Customer");
		newCustomerButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				resetForNewCustomer();
			}
		});
		newCustomerButton.setFont(new Font ("Serif", Font.PLAIN, 20));
		
		jPanel.add(newCustomerButton, gridBagCons);
		//
		
		mainLayout.add(jPanel, gridBagCons);
		mainLayout.updateUI();
	}
	
	private void cashBack()
	{
		double cashBack;
		
		cashBack = Double.parseDouble(moneyReceived) - Double.parseDouble(priceLabel.getText());
		cashBackLabel.setText(Double.toString(cashBack));
	}
	
	private void resetForNewCustomer()
	{
		try {
			ProductAPI.sendSales(cartList);
		} catch (ClassNotFoundException | IOException e) 
		{
			e.printStackTrace();
		}
		
		cartList = new ArrayList<Integer>();
		updateCart();
		
		moneyReceivedLabel.setText("0.0");
		cashBackLabel.setText("0.0");
		
	}
}
