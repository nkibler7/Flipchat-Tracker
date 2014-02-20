import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;


/**
 * This class serves as an individual profit summary for one item that was
 * bought and sold. It is added by the ProfitsPanel object, which is notified
 * to add a Profit object for a specified Item when the Item is sold.
 * 
 * @author i96
 *
 */
public class Profit extends JPanel {

	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Creates a Profit object.
	 */
	public Profit(Item item, int profit, int x, int y) {
		super();
		
		this.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		this.setBounds(x, y, 194, 35);
		this.setLayout(null);
		
		String desc = item.getAbbr() + " - " + item.getName();
		JLabel desc_label = new JLabel(desc);
		desc_label.setToolTipText(desc);
		desc_label.setFont(new Font("Tahoma", Font.PLAIN, 11));
		desc_label.setBounds(10, 0, 113, 35);
		this.add(desc_label);
		
		JLabel profit_label = new JLabel("$" + profit + "K");
		profit_label.setForeground(new Color(0, 128, 0));
		profit_label.setFont(new Font("Tahoma", Font.BOLD, 11));
		profit_label.setBounds(133, 0, 51, 35);
		this.add(profit_label);
	}

}
