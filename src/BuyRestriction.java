import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;

/**
 * This class is a buy restriction against one item and one item only. Its job is to
 * keep track of the JLabel that should be displayed inside the BuyRestrictionsPanel
 * object, and to keep its own count-down time updated. When its count-down time is
 * up, it notifies any observers, which is only the BuyRestrictionsPanel object.
 * 
 * @author i96
 * 
 */
public class BuyRestriction extends Observable {
	private static final int max = 14400;
	
	private JPanel panel;
	private JLabel timeLabel;
	private Item item;
	private Timer timer;

	/**
	 * Creates a new BuyRestriction based on the given Item.
	 * 
	 * @param item - the Item to place a buy restriction on
	 */
	public BuyRestriction(Item item) {
		this.item = item;
		
		panel = new JPanel();
		panel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panel.setBounds(0, 0, 192, 28);
		panel.setLayout(null);
		
		String desc = item.getAbbr() + " - " + item.getName();
		JLabel lblDesc = new JLabel(desc);
		lblDesc.setToolTipText(desc);
		lblDesc.setBounds(10, 0, 130, 28);
		panel.add(lblDesc);
		
		timeLabel = new JLabel("4:00:00");
		timeLabel.setForeground(new Color(205, 133, 63));
		timeLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
		timeLabel.setHorizontalAlignment(SwingConstants.TRAILING);
		timeLabel.setBounds(144, 0, 41, 28);
		panel.add(timeLabel);
		
		timer = new Timer(1000, new BRTimerActionListener(this));
		timer.start();
	}
	
	/**
	 * Returns the Item this buy restriction is placed on.
	 * @return item - the Item object that this restriction is placed on
	 */
	public Item getItem() {
		return item;
	}
	
	/**
	 * Returns the JPanel that contains information about this BuyRestriction,
	 * namely the Item's abbreviation, name, and the count-down until the
	 * restriction is lifted.
	 * @return panel - the JPanel object that represents this BuyRestriction
	 */
	public JPanel getPanel() {
		return panel;
	}

	/**
	 * Stops the count-down timer on this buy restriction. Theoretically,
	 * this should help the GC remove the buy restriction when it's time.
	 */
	public void stopTimer() {
		timer.stop();
	}
	
	/**
	 * Update the label to display the current count-down.
	 * @param curr - the current time left (in seconds) before this restriction is lifted
	 */
	public void updateTime(int curr) {
		int currHours = curr / 3600;
		int currMin = (curr - (currHours * 3600)) / 60;
		int currSecs = curr - (currHours * 3600) - (currMin * 60);
		timeLabel.setText(Math.abs(currHours) + ":"
				+ String.format("%02d", Math.abs(currMin)) + ":"
				+ String.format("%02d", Math.abs(currSecs)));
	}

	/**
	 * This class is a custom implementation of an ActionListener that contains
	 * a actionPerformed() method that is called after each second passes from the
	 * Timer object. It keeps track of how much time has passed since its creation
	 * and will update the time label accordingly. It will also notify the
	 * BuyRestrictionsPanel object when it is time to remove this restriction.
	 * 
	 * @author i96
	 *
	 */
	private class BRTimerActionListener implements ActionListener {
		private int count = 1;
		private BuyRestriction br;

		/**
		 * Creates a new BRTimerActionListener that is binded to the
		 * specified BuyRestriction.
		 * @param br - the BuyRestriction this action listener is for
		 */
		public BRTimerActionListener(BuyRestriction br) {
			super();
			this.br = br;
		}

		/**
		 * This method is called from the timer after each delay value (1000 ms in this
		 * case) passes.
		 * 
		 * @param arg0 - ActionEvent argument (not applicable in this case)
		 */
		@Override
		public void actionPerformed(ActionEvent arg0) {
			int curr = max - count++;
			if (curr < 0) {
				br.stopTimer();
				br.setChanged();
				br.notifyObservers();
			}
			br.updateTime(curr);
		}
	}
}
