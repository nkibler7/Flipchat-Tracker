/**
 * This class holds all the relevant information that is specific to an item in game.
 * There are purposefully no setters because there is no reason for you to modify
 * the item's contents after creation.
 * 
 * @author i96
 *
 */
public class Item {
	private String name, abbr;
	private ItemLine line;
	private int ruleValue;
	private int limit;
	
	/**
	 * Creates a new Item object with the specified parameters. These parameters are set
	 * only once and can only be retrieved through "getter methods" after this constructor.
	 * @param abbr - the abbreviation of the item
	 * @param name - the name of the item
	 * @param line - the line the item belongs to
	 * @param ruleValue - the amount (in thousands) to adjust the price by when a rule is applied
	 * @param limit - the number of items that can be bought in a 4 hour period
	 */
	public Item(String abbr, String name, ItemLine line, int ruleValue, int limit) {
		if (name != null) {
			this.name = name;
		}
		else {
			this.name = "";
		}
		if (abbr != null) {
			this.abbr = abbr;
		}
		else {
			this.abbr = "";
		}
		this.line = line;
		this.ruleValue = ruleValue;
		this.limit = limit;
	}
	
	/**
	 * Returns the name of this item.
	 * @return name - the name of this item
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns the ItemLine enum that this item is a part of.
	 * @return line - the ItemLine enum that this item is a part of
	 */
	public ItemLine getLine() {
		return line;
	}
	
	/**
	 * Returns the abbreviation of this item.
	 * @return abbr - the abbreviation of this item
	 */
	public String getAbbr() {
		return abbr;
	}
	
	/**
	 * Returns the amount (in thousands) of cash that should be added or
	 * removed from the price when the 25 minute rule is applied.
	 * @return ruleValue - the amount (in thousands) of cash that is applied to this item
	 * when the 25 minute rule is applied
	 */
	public int getRuleValue() {
		return ruleValue;
	}
	
	/**
	 * Returns the number of items that may be bought in a 4 hour period. 0 means there
	 * is no limit.
	 * @return limit - the number of items that can be bought in a 4 hour period
	 */
	public int getLimit() {
		return limit;
	}

}
