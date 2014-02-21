import java.util.ArrayList;

/**
 * This class holds the collections of Item objects that are valid items to flip. The
 * constructor takes care of the creation of all these items. 
 * 
 * @author i96
 *
 */
public class ItemCollectionManager {
	private ArrayList<Item> armadylLine, bandosLine, subjugationLine, shieldLine, miscellaneousLine;
	private ArrayList<Item> restrictedList;
	
	/**
	 * Creates a new ItemCollection that holds and handles all of the lists of items.
	 * Use an instance of this object to add/remove items to/from the restricted list.
	 */
	public ItemCollectionManager() {
		armadylLine = new ArrayList<Item>();
		armadylLine.add(new Item("AH", "Armadyl helmet", ItemLine.ARMADYL, 25, 1));
		armadylLine.add(new Item("ACP", "Armadyl chestplate", ItemLine.ARMADYL, 25, 1));
		armadylLine.add(new Item("ACS", "Armadyl chainskirt", ItemLine.ARMADYL, 25, 1));
		armadylLine.add(new Item("AG", "Armadyl gloves", ItemLine.ARMADYL, 25, 1));
		armadylLine.add(new Item("AB", "Armadyl boots", ItemLine.ARMADYL, 25, 1));
		armadylLine.add(new Item("ACB", "Armadyl crossbow", ItemLine.ARMADYL, 25, 1));
		armadylLine.add(new Item("BUCK", "Armadyl buckler", ItemLine.ARMADYL, 25, 1));
		
		bandosLine = new ArrayList<Item>();
		bandosLine.add(new Item("BH", "Bandos helmet", ItemLine.BANDOS, 25, 1));
		bandosLine.add(new Item("BCP", "Bandos chestplate", ItemLine.BANDOS, 25, 1));
		bandosLine.add(new Item("TASS", "Bandos tassets", ItemLine.BANDOS, 25, 1));
		bandosLine.add(new Item("BG", "Bandos gloves", ItemLine.BANDOS, 25, 1));
		bandosLine.add(new Item("BB", "Bandos boots", ItemLine.BANDOS, 25, 1));
		bandosLine.add(new Item("BWS", "Bandos warshield", ItemLine.BANDOS, 25, 1));
		
		subjugationLine = new ArrayList<Item>();
		subjugationLine.add(new Item("HOOD", "Hood of subjugation", ItemLine.SUBJUGATION, 25, 1));
		subjugationLine.add(new Item("GARB", "Garb of subjugation", ItemLine.SUBJUGATION, 25, 1));
		subjugationLine.add(new Item("GOWN", "Gown of subjugation", ItemLine.SUBJUGATION, 25, 1));
		subjugationLine.add(new Item("SG", "Gloves of subjugation", ItemLine.SUBJUGATION, 25, 1));
		subjugationLine.add(new Item("SB", "Boots of subjugation", ItemLine.SUBJUGATION, 25, 1));
		subjugationLine.add(new Item("WARD", "Ward of subjugation", ItemLine.SUBJUGATION, 25, 1));
		
		shieldLine = new ArrayList<Item>();
		shieldLine.add(new Item("SPEC", "Spectral spirit shield", ItemLine.SHIELD, 25, 1));
		shieldLine.add(new Item("MAL", "Malevolent kiteshield", ItemLine.SHIELD, 250, 1));
		shieldLine.add(new Item("VENG", "Vengeful kiteshield", ItemLine.SHIELD, 250, 1));
		shieldLine.add(new Item("MERC", "Merciless kiteshield", ItemLine.SHIELD, 250, 1));
		
		miscellaneousLine = new ArrayList<Item>();
		miscellaneousLine.add(new Item("HISS", "Saradomin's hiss", ItemLine.MISCELLANEOUS, 25, 1));
		miscellaneousLine.add(new Item("ROBIN", "Robin hood hat", ItemLine.MISCELLANEOUS, 25, 2));
		miscellaneousLine.add(new Item("WHISP", "Saradomin's whisper", ItemLine.MISCELLANEOUS, 25, 1));
		miscellaneousLine.add(new Item("MURM", "Saradomin's murmur", ItemLine.MISCELLANEOUS, 25, 1));
		miscellaneousLine.add(new Item("RANGER", "Ranger boots", ItemLine.MISCELLANEOUS, 25, 2));
		
		restrictedList = new ArrayList<Item>();
	}
	
	/**
	 * Returns a list of Item objects in the specified line.
	 * @param line - the ItemLine enum of the list that should be returned
	 * @return an ArrayList of Item objects that represents the collection of "buyable" items
	 */
	public ArrayList<Item> getItemListByLine(ItemLine line) {
		switch(line) {
			case ARMADYL:
				return armadylLine;
			case BANDOS:
				return bandosLine;
			case SUBJUGATION:
				return subjugationLine;
			case SHIELD:
				return shieldLine;
			case MISCELLANEOUS:
				return miscellaneousLine;
		}
		return null;
	}
	
	/**
	 * Adds the specified Item to the restricted buy list.
	 * @param item - the Item to be added to the restricted buy list
	 */
	public void addItemToRestrictedList(Item item) {
		ItemLine line = item.getLine();
		ArrayList<Item> list = getItemListByLine(line);
		list.remove(item);
		restrictedList.add(item);
	}
	
	/**
	 * Removes the specified item from the restricted buy list.
	 * @param item - the Item to be removed from the restricted buy list
	 */
	public void removeItemFromRestrictedList(Item item) {
		ItemLine line = item.getLine();
		ArrayList<Item> list = getItemListByLine(line);
		restrictedList.remove(item);
		list.add(item);
	}
}
