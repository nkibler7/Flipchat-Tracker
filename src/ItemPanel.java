import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

/**
 * This class represents a JPanel object that holds the information about an item
 * that is going through a transaction. There may only be a maximum of 6 in the viewing
 * pane at a time due to the Grand Exchange restrictions.
 * 
 * @author i96
 *
 */
public class ItemPanel extends JPanel {
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;
	
	private int marginBuy, marginSell, priceBuy, priceSell, price, rules;
	private Item item;
	private ItemStatus status;
	private boolean boughtIn;
	
	private JRadioButton select;
	private JLabel statusLabel, priceLabel, marginLabel, timeLabel, rulesLabel;
	
	private RuleTimer timer;
	
	/**
	 * Color of the status label when the item's status is buying.
	 */
	private static final Color buyingStatusColor = new Color(72, 61, 139);
	private static final Color sellingStatusColor = new Color(72, 61, 139);
	
	/**
	 * Creates a new ItemPanel for the specified Item object, initializing
	 * it with the given parameters.
	 * @param item - the Item object to bind to this panel
	 * @param initStatus - the initial ItemStatus to set to this item
	 * @param initMarginBuy - the initial buy margin
	 * @param initMarginSell - the initial sell margin
	 */
	public ItemPanel(Item item, ItemStatus initStatus, int initMarginBuy, int initMarginSell) {
		super();
		
		this.item = item;
		this.status = initStatus;
		marginBuy = initMarginBuy;
		marginSell = initMarginSell;
		priceBuy = marginBuy;
		priceSell = marginSell;
		rules = 0;
		if (initStatus == ItemStatus.BUYING) {
			price = priceBuy;
			boughtIn = true;
		}
		else {
			price = priceSell;
			boughtIn = false;
		}
		
		this.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		this.setBounds(0, 0, 101, 241);
		this.setLayout(null);
		
		select = new JRadioButton();
		select.setHorizontalAlignment(SwingConstants.CENTER);
		select.setBounds(6, 7, 89, 16);
		this.add(select);
		
		statusLabel = new JLabel(status.name());
		statusLabel.setForeground(new Color(128, 0, 128));
		statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
		statusLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
		statusLabel.setBounds(6, 30, 89, 23);
		this.add(statusLabel);
		
		String desc = item.getAbbr() + " - " + item.getName();
		JLabel descLabel = new JLabel(desc);
		descLabel.setToolTipText(desc);
		descLabel.setForeground(new Color(205, 92, 92));
		descLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
		descLabel.setBounds(6, 55, 89, 23);
		this.add(descLabel);
		
		JLabel lblPrice = new JLabel("Price");
		lblPrice.setForeground(Color.DARK_GRAY);
		lblPrice.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblPrice.setHorizontalAlignment(SwingConstants.CENTER);
		lblPrice.setBounds(6, 77, 89, 14);
		this.add(lblPrice);
		
		priceLabel = new JLabel("$" + price + "K");
		priceLabel.setForeground(new Color(0, 128, 0));
		priceLabel.setHorizontalAlignment(SwingConstants.CENTER);
		priceLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
		priceLabel.setBounds(6, 93, 89, 14);
		this.add(priceLabel);
		
		JLabel lblMargin = new JLabel("Margin");
		lblMargin.setHorizontalAlignment(SwingConstants.CENTER);
		lblMargin.setForeground(Color.DARK_GRAY);
		lblMargin.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblMargin.setBounds(6, 118, 89, 14);
		this.add(lblMargin);
		
		marginLabel = new JLabel(marginBuy + " - " + marginSell);
		marginLabel.setForeground(new Color(30, 144, 255));
		marginLabel.setHorizontalAlignment(SwingConstants.CENTER);
		marginLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
		marginLabel.setBounds(6, 135, 89, 14);
		this.add(marginLabel);
		
		JLabel lblTimeToRule = new JLabel("Time to Rule");
		lblTimeToRule.setHorizontalAlignment(SwingConstants.CENTER);
		lblTimeToRule.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblTimeToRule.setForeground(Color.DARK_GRAY);
		lblTimeToRule.setBounds(6, 160, 89, 14);
		this.add(lblTimeToRule);
		
		timeLabel = new JLabel("25:00");
		timeLabel.setForeground(new Color(0, 128, 128));
		timeLabel.setHorizontalAlignment(SwingConstants.CENTER);
		timeLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
		timeLabel.setBounds(6, 174, 89, 14);
		this.add(timeLabel);
		
		JLabel lblNumRules = new JLabel("Num Rules");
		lblNumRules.setHorizontalAlignment(SwingConstants.CENTER);
		lblNumRules.setForeground(Color.DARK_GRAY);
		lblNumRules.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblNumRules.setBounds(6, 199, 89, 14);
		this.add(lblNumRules);
		
		rulesLabel = new JLabel("0");
		rulesLabel.setHorizontalAlignment(SwingConstants.CENTER);
		rulesLabel.setForeground(new Color(119, 136, 153));
		rulesLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
		rulesLabel.setBounds(6, 213, 89, 14);
		this.add(rulesLabel);
		
		timer = new RuleTimer(timeLabel);
		timer.start();
	}
	
	/**
	 * Returns the buy value for the margin.
	 * @return marginBuy - cash value (in thousands) of buy margin
	 */
	public int getMarginBuy() {
		return marginBuy;
	}
	
	/**
	 * Sets the buy value for the margin.
	 * @param newMarginBuy - new cash value (in thousands) of buy margin
	 */
	public void setMarginBuy(int newMarginBuy) {
		if (newMarginBuy > 0 && status == ItemStatus.BUYING) {
			marginBuy = newMarginBuy;
			marginLabel.setText(marginBuy + " - " + marginSell);
		}
	}
	
	/**
	 * Returns the sell value for the margin.
	 * @return marginSell - cash value (in thousands) of sell margin
	 */
	public int getMarginSell() {
		return marginSell;
	}
	
	/**
	 * Sets the sell value for the margin.
	 * @param newMarginSell - new cash value (in thousands) of sell margin
	 */
	public void setMarginSell(int newMarginSell) {
		if (newMarginSell > 0) {
			marginSell = newMarginSell;
			marginLabel.setText(marginBuy + " - " + marginSell);
		}
	}
	
	/**
	 * Returns the value of either the current buying price or the price paid, depending
	 * on the status of the item's transaction.
	 * @return priceBuy - cash value (in thousands) of buy price
	 */
	public int getPriceBuy() {
		return priceBuy;
	}
	
	/**
	 * Sets the value of the current buying price.
	 * @param newPriceBuy - new cash value (in thousands) of buy price
	 */
	public void setPriceBuy(int newPriceBuy) {
		if (newPriceBuy > 0) {
			priceBuy = newPriceBuy;
			if (status == ItemStatus.BUYING) {
				priceLabel.setText("$" + priceBuy + "K");
			}
		}
	}
	
	/**
	 * Returns the value of either the current selling price or the price the item was
	 * sold for, depending on the status of the item's transaction.
	 * @return priceSell - cash value (in thousands) of sell price
	 */
	public int getPriceSell() {
		return priceSell;
	}
	
	/**
	 * Sets the value of the current selling price.
	 * @param newPriceSell - new cash value (in thousands) of sell price
	 */
	public void setPriceSell(int newPriceSell) {
		if (newPriceSell > 0) {
			priceSell = newPriceSell;
			if (status == ItemStatus.SELLING) {
				priceLabel.setText("$" + priceSell + "K");
			}
		}
	}
	
	/**
	 * Sets the price of the Item to the provided price.
	 * @param newPrice - the new price to place on the Item
	 */
	public void setPrice(int newPrice) {
		switch(status) {
			case BUYING:
				priceBuy = newPrice;
				price = newPrice;
				priceLabel.setText("$" + price + "K");
				break;
			case SELLING:
				priceSell = newPrice;
				price = newPrice;
				priceLabel.setText("$" + price + "K");
				break;
			default:
				break;
		}
	}
	
	/**
	 * Returns the ItemStatus enum value that represents this item's current status.
	 * @return status - an ItemStatus enum that represents this item's status
	 */
	public ItemStatus getStatus() {
		return status;
	}
	
	/**
	 * Sets the status to the new ItemStatus enum value.
	 * @param newStatus - the new ItemStatus enum that represents this item's status
	 */
	public void setStatus(ItemStatus newStatus) {
		status = newStatus;
		switch(status) {
			case BUYING:
				statusLabel.setText("BUYING");
				statusLabel.setForeground(buyingStatusColor);
				break;
			case SELLING:
				statusLabel.setText("SELLING");
				statusLabel.setForeground(sellingStatusColor);
				break;
			default:
				break;
		}
	}
	
	/**
	 * Helper function that marks the item as selling, changing everything that
	 * needs to be changed when the switch from buying to selling happens.
	 */
	public void markAsSelling(ItemCollectionManager itemCollection) {
		this.setStatus(ItemStatus.SELLING);
		this.setPriceSell(marginSell);
		timer.resetTimer();
		rules = 0;
		rulesLabel.setText("" + rules);
		
		itemCollection.addItemToRestrictedList(item);
	}
	
	/**
	 * Helper function that marks the item as sold, changing everything that
	 * needs to be changed when the switch from selling to sold happens.
	 */
	public void markAsSold() {
		this.setStatus(ItemStatus.SOLD);
	}
	
	/**
	 * Returns a boolean value that tells whether the item was marked as bought in the
	 * program or not, which determines if the profit should be calculated.
	 * @return true if the item was marked as bought in the program, false otherwise
	 */
	public boolean wasBought() {
		return boughtIn;
	}
	
	/**
	 * Returns the Item object that this panel is displaying.
	 * @return item - the Item that this panel is displaying
	 */
	public Item getItem() {
		return item;
	}
	
	/**
	 * Returns the profit (in thousands) that the user has made from buying and
	 * selling the item.
	 * @return the difference between the selling price and buying price if the item
	 * was both bought and sold in the program, 0 otherwise
	 */
	public int getProfit() {
		if (priceSell == 0 || !boughtIn) {
			return 0;
		}
		return (priceSell - priceBuy);
	}
	
	/**
	 * Returns true if the JRadioButton object that belongs to this panel is selected,
	 * false otherwise.
	 * @return true if this panel is selected, false otherwise
	 */
	public boolean isSelected() {
		return select.isSelected();
	}
	
	/**
	 * Returns the RuleTimer object that handles the count-down timer for the next rule application.
	 * @return the RuleTimer object that counts down to the next rule application
	 */
	public RuleTimer getRuleTimer() {
		return timer;
	}
	
	/**
	 * Apply the 25 minute rule to the item. This will lower/raise the price, depending on whether
	 * it is currently buying or selling, by the amount specified for the Item.
	 */
	public void applyRule() {
		switch(status) {
			case BUYING:
				priceBuy += item.getRuleValue();
				priceLabel.setText("$" + priceBuy + "K");
				if (priceBuy == marginBuy) {
					rulesLabel.setText("0");
				}
				else if (priceBuy > marginBuy) {
					rulesLabel.setText("" + (++rules));
				}
				break;
			case SELLING:
				priceSell -= item.getRuleValue();
				priceLabel.setText("$" + priceSell + "K");
				if (priceSell == marginSell) {
					rulesLabel.setText("0");
				}
				else if (priceSell < marginSell) {
					rulesLabel.setText("" + (++rules));
				}
				break;
			default:
				return;
		}
		timer.resetTimer();
	}
	
	/**
	 * Returns the JRadioButton object of the select button for this ItemPanel.
	 * @return the JRadioButton object of this select button
	 */
	public JRadioButton getSelectRadioButton() {
		return select;
	}
	
	/**
	 * Checks if a stretch is being made. If so, sets the text of the rules label
	 * to notify the user that they are performing a stretch of the item.
	 */
	public void checkStretch() {
		switch(status) {
			case BUYING:
				if (priceBuy < marginBuy) {
					rulesLabel.setText("STRETCH");
				}
				break;
			case SELLING:
				if (priceSell > marginSell) {
					rulesLabel.setText("STRETCH");
				}
				break;
			default:
				break;
		}
	}
}
