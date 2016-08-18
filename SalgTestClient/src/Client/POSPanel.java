package Client;
import javax.swing.*;
import java.awt.*;

public class POSPanel 
{
	GridBagLayout gridBagLayout;
	JPanel jPanel;
	GridBagConstraints gridBagCons;
	
	POSPanel()
	{	
		gridBagLayout = new GridBagLayout();
		
		gridBagCons = new GridBagConstraints();
		gridBagCons.insets = new Insets(0, 0, 0, 0); // Padding
		gridBagCons.fill = GridBagConstraints.BOTH;
		gridBagCons.anchor = GridBagConstraints.NORTHWEST;
		gridBagCons.weightx = 1;
        gridBagCons.weighty = 1;
        
		jPanel = new JPanel(gridBagLayout);
		

	}

}
