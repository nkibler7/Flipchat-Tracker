import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.EmptyBorder;

/**
 * This class is a custom implementation of a JFrame that appears when the user
 * selects an option to add a new Item. When they finish editing the new Item
 * and click the "Add Item" button, it then creates a new ItemPanel object,
 * which it sends back to the parent frame (MainFrame) for further processing
 * and final display.
 * 
 * @author i96
 *
 */
public class NewItemFrame extends JFrame {

	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;
	
	private JPanel contentPane;
	private final ButtonGroup buyingSellingButtonGroup = new ButtonGroup();
	private JRadioButton rdbtnBuying, rdbtnSelling;
	private JTextField marginBuyTextField;
	private JTextField marginSellTextField;

	private JComboBox<String> itemComboBox;
	private ArrayList<Item> currentItems;

	/**
	 * Create the frame.
	 */
	public NewItemFrame(final MainFrame parent) {
		setResizable(false);
		setTitle("Add New Item");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 349, 200);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height
				/ 2 - this.getSize().height / 2);

		JLabel lblItemToAdd = new JLabel("Item to add:");

		itemComboBox = new JComboBox<String>();
		itemComboBox.setFocusable(false);

		rdbtnBuying = new JRadioButton("Buying");
		rdbtnBuying.setFocusable(false);
		rdbtnBuying.setSelected(true);
		buyingSellingButtonGroup.add(rdbtnBuying);

		rdbtnSelling = new JRadioButton("Selling");
		rdbtnSelling.setFocusable(false);
		buyingSellingButtonGroup.add(rdbtnSelling);

		JLabel lblMargin = new JLabel("Margin:");

		marginBuyTextField = new JTextField();
		marginBuyTextField.setColumns(10);

		JLabel lblTo = new JLabel("Sell:");

		marginSellTextField = new JTextField();
		marginSellTextField.setColumns(10);

		JLabel lblNewLabel = new JLabel("Buy:");

		JButton btnAddItem = new JButton("Add Item");
		btnAddItem.setFocusable(false);
		btnAddItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Item item = getSelectedItem();
				ItemStatus status = rdbtnBuying.isSelected() ? ItemStatus.BUYING : ItemStatus.SELLING;
				int marginBuy = Integer.parseInt(marginBuyTextField
						.getText());
				int marginSell = Integer.parseInt(marginSellTextField
						.getText());
				ItemPanel ip = new ItemPanel(item, status, marginBuy, marginSell);
				parent.addItemPanel(ip);
			}
		});

		/**
		 * An ugly auto-generated GroupLayout method.
		 * TODO: Change this to NOT use GroupLayout and considering absolute positioning instead.
		 */
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane
				.setHorizontalGroup(gl_contentPane
						.createParallelGroup(Alignment.LEADING)
						.addGroup(
								gl_contentPane
										.createSequentialGroup()
										.addGroup(
												gl_contentPane
														.createParallelGroup(
																Alignment.LEADING)
														.addGroup(
																gl_contentPane
																		.createSequentialGroup()
																		.addContainerGap()
																		.addGroup(
																				gl_contentPane
																						.createParallelGroup(
																								Alignment.LEADING)
																						.addGroup(
																								gl_contentPane
																										.createSequentialGroup()
																										.addComponent(
																												lblItemToAdd)
																										.addPreferredGap(
																												ComponentPlacement.RELATED)
																										.addComponent(
																												itemComboBox,
																												GroupLayout.PREFERRED_SIZE,
																												GroupLayout.DEFAULT_SIZE,
																												GroupLayout.PREFERRED_SIZE))
																						.addComponent(
																								lblMargin)
																						.addGroup(
																								gl_contentPane
																										.createSequentialGroup()
																										.addComponent(
																												rdbtnBuying)
																										.addGap(18)
																										.addComponent(
																												rdbtnSelling))))
														.addGroup(
																gl_contentPane
																		.createSequentialGroup()
																		.addGap(28)
																		.addComponent(
																				lblNewLabel)
																		.addPreferredGap(
																				ComponentPlacement.RELATED)
																		.addComponent(
																				marginBuyTextField,
																				GroupLayout.PREFERRED_SIZE,
																				GroupLayout.DEFAULT_SIZE,
																				GroupLayout.PREFERRED_SIZE)
																		.addPreferredGap(
																				ComponentPlacement.UNRELATED)
																		.addComponent(
																				lblTo)
																		.addPreferredGap(
																				ComponentPlacement.RELATED)
																		.addComponent(
																				marginSellTextField,
																				GroupLayout.PREFERRED_SIZE,
																				GroupLayout.DEFAULT_SIZE,
																				GroupLayout.PREFERRED_SIZE))
														.addGroup(
																gl_contentPane
																		.createSequentialGroup()
																		.addContainerGap()
																		.addComponent(
																				btnAddItem,
																				GroupLayout.DEFAULT_SIZE,
																				303,
																				Short.MAX_VALUE)))
										.addContainerGap()));
		gl_contentPane
				.setVerticalGroup(gl_contentPane
						.createParallelGroup(Alignment.LEADING)
						.addGroup(
								gl_contentPane
										.createSequentialGroup()
										.addContainerGap()
										.addGroup(
												gl_contentPane
														.createParallelGroup(
																Alignment.BASELINE)
														.addComponent(
																lblItemToAdd)
														.addComponent(
																itemComboBox,
																GroupLayout.PREFERRED_SIZE,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.PREFERRED_SIZE))
										.addGap(7)
										.addGroup(
												gl_contentPane
														.createParallelGroup(
																Alignment.BASELINE)
														.addComponent(
																rdbtnBuying)
														.addComponent(
																rdbtnSelling))
										.addPreferredGap(
												ComponentPlacement.UNRELATED)
										.addComponent(lblMargin)
										.addPreferredGap(
												ComponentPlacement.RELATED)
										.addGroup(
												gl_contentPane
														.createParallelGroup(
																Alignment.BASELINE)
														.addComponent(
																lblNewLabel)
														.addComponent(
																marginBuyTextField,
																GroupLayout.PREFERRED_SIZE,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.PREFERRED_SIZE)
														.addComponent(lblTo)
														.addComponent(
																marginSellTextField,
																GroupLayout.PREFERRED_SIZE,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.PREFERRED_SIZE))
										.addGap(18)
										.addComponent(btnAddItem,
												GroupLayout.DEFAULT_SIZE, 26,
												Short.MAX_VALUE)));
		contentPane.setLayout(gl_contentPane);
	}

	/**
	 * Makes the NewItemFrame window visible, initializing the fields before it does so.
	 * @param items - the list of Item objects to add to the dropdown menu
	 */
	public void makeVisible(ArrayList<Item> items) {
		currentItems = items;
		ArrayList<String> itemDescriptions = new ArrayList<String>();
		for (Item item: items) {
			itemDescriptions.add(item.getAbbr() + " - " + item.getName());
		}
		String[] stringArray = new String[itemDescriptions.size()];
		stringArray = itemDescriptions.toArray(stringArray);
		itemComboBox.setModel(new DefaultComboBoxModel<String>(stringArray));
		rdbtnBuying.setSelected(true);
		marginBuyTextField.setText("");
		marginSellTextField.setText("");
		marginBuyTextField.requestFocus();
		this.setVisible(true);
	}
	
	/**
	 * Returns the Item object associated with the selected String, which is the abbreviation
	 * combined with the item name.
	 * @return the Item object that was selected
	 */
	private Item getSelectedItem() {
		int idx = itemComboBox.getSelectedIndex();
		return currentItems.get(idx);
	}
}