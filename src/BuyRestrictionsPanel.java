import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;


/**
 * This class handles the entirety of all buy restrictions and their display. When a request
 * is sent to create a buy restriction, this class creates a new BuyRestriction object,
 * adds it to an array to keep track of it, and draws its JPanel. When the BuyRestriction's
 * time is up, it will notify this class, which will remove it from its list and redraw the
 * other buy restrictions.
 * 
 * @author i96
 *
 */
public class BuyRestrictionsPanel extends JPanel implements Observer {
	private ArrayList<BuyRestriction> list;

	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new BuyRestrictionsPanel that handles the display of all buy restrictions
	 * that are currently on timers.
	 */
	public BuyRestrictionsPanel() {
		super();
		
		this.setBorder(null);
		this.setLayout(null);
		list = new ArrayList<BuyRestriction>();
	}
	
	/**
	 * Redraws the whole BuyRestriction panel by removing all subpanels and adding them again.
	 */
	private void redraw() {
		this.removeAll();
		this.setPreferredSize(new Dimension(10, 10));
		
		int count = 0;
		for (BuyRestriction currBr: list) {
			JPanel brPanel = currBr.getPanel();
			int row = count / 3;
			int col = count++ % 3;
			int x = (col * 202) + 10;
			int y = (row * 32) + 11;
			
			int bottom = y + 43;
			if (this.getHeight() < bottom) {
				this.setPreferredSize(new Dimension(10, bottom));
			}
			
			brPanel.setLocation(x, y);
			this.add(brPanel);
		}
		
		this.revalidate();
		this.repaint();
	}
	
	/**
	 * Add the specified item to the buy restriction list, which will start a count-down
	 * beginning at 4 hours.
	 * 
	 * @param item - the Item to be added to the buy restriction list
	 */
	public void addItemToBuyRestrictionList(Item item) {
		BuyRestriction br = new BuyRestriction(item);
		br.addObserver(this);
		list.add(br);
		this.redraw();
	}

	/**
	 * This update method is called when the BuyRestriction chooses to notify
	 * its observers, which is when its count-down time becomes negative. This
	 * method will then remove that BuyRestriction from the list and redraw.
	 * 
	 * @param br - the BuyRestriction object which calls for the update
	 * @param throwawayArg - normally used to pass an arg from the Observable
	 * up to its Observers, but we don't need this
	 * 
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(Observable br, Object throwawayArg) {
		list.remove(br);
		this.redraw();
	}
	
}
