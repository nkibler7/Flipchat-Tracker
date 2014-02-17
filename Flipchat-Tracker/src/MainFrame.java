import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import java.awt.Font;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.border.TitledBorder;
import javax.swing.UIManager;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.BevelBorder;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EtchedBorder;

/***********************************************************************
 ** Flipchat Tracker - The easiest way to track your Runescape flips! **
 ***********************************************************************
 *
 * This code is committed for use by the FlipchatRS community (flipchatrs.com).
 * The use of this code for other purposes or by other communities, which includes,
 * but is not limited to, full application use, partial code use, full code use,
 * and code modification, is strictly prohibited. Doing so will cause this code
 * to become closed-source and support will end on the open source version.
 * Please keep it open source!
 * 
 * 
 * -- GENERAL LOGIC --
 * The basic format of how the program works is pretty simple: you can only
 * have up to 6 items trading at one time (per G.E. restriction in game). 
 * So to make it easy and prevent the unnecessary creation/deletion of Swing
 * components on the fly, the 6 item panes are created once and displayed/hidden
 * to simulate dynamic item pane creation. In order to do this, the panes'
 * information must be movable (to shift items 'down the line' when an item
 * pane is hidden), which presents an extra challenge when it comes to managing
 * the timers.
 * 
 * -- TIMER LOGIC --
 * Each timer is bound to a specific JLabel object. Because of this, there's
 * technically two ways to correctly change the timer: change the counter it's
 * using or change the label it's updating. I chose the former for no particular
 * reason.
 * 
 * -- BUY RESTRICTION LOGIC --
 * Buy restrictions are dynamic JPanels that are absolutely positioned in a
 * scrollable JPanel. "Why?" you might ask. The short answer is because I said
 * so. Be my guest to use any other Swing layout format, but good luck. Personally,
 * I can't be bothered with figuring out why GroupLayouts are such a good concept,
 * but are so terribly difficult to correctly implement, but I digress. These
 * panels too have a timer, but since these panels aren't static and are physically
 * moving on an update, it's common sense for the timer to be bound to the JLabel
 * for life. Basic ideology of removal is to remove from the list, remove all
 * buy restriction panels from the main JPanel, update indices of following panels, 
 * and redraw the main JPanel. It was faster for me to write it this way than to
 * calculate the new JPanel pixel offsets. If it comes to a point where this is
 * hindering the application, again, be my guest to rewrite it.
 * 
 * -- PROFIT LOGIC --
 * This is very similar to the explanation above for the buy restriction panel.
 * Panels are dynamic, except there is no need for removal. Profit is calculated when
 * item is marked as sold, at which point the profit is added to the total profit
 * and the hourly profit is updated.
 * 
 * @author i96
 */
@SuppressWarnings({ "serial" })
public class MainFrame extends JFrame {
	
	//TODO: Update before pushing changes to new release number
	private String version = "1.02";

	/*
	 * Some JPanels, JLabels, etc. are global for easy interaction between features.
	 */
	private JPanel contentPane;
	private static JPanel buy_restr_panel, profit_panel;
	private static JLabel lblNoItemsIn, total_profit_label, profit_hour_label, total_time_label, adjust_dialog_idx_label;
	private static NewItemFrame iFrame;
	private static AdjustPriceMarginsDialog aDialog;
	private final static ButtonGroup tradingItemsButtonGroup = new ButtonGroup();
	private static int numItems = 0;
	private static ArrayList<JPanel> itemPanelArray;
	private static ArrayList<JRadioButton> itemSelectArray;
	private static ArrayList<JLabel> itemStatusArray, itemDescArray, itemPriceArray, itemMarginArray, itemTimeArray, itemRulesArray;
	private static ArrayList<RuleTimer> timerArray;
	private static int[][] buySellArray = new int[6][2], marginArray = new int[6][2];
	private static ArrayList<BuyRestriction> buyRestrictions;
	private static ArrayList<String> boughtItems;
	private int profitItems = 0;
	private static int totalSeconds = 1, totalProfit = 0;
	private static Timer totalTimer;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					// Sometimes the look and feel gets defaulted to something other than the system's default
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					
					// Create our JFrames and make the main JFrame visible
					MainFrame frame = new MainFrame();
					frame.setVisible(true);
					iFrame = new NewItemFrame();
					aDialog = new AdjustPriceMarginsDialog();

					// Create our timer that keeps track of how long we have been flipping
					totalTimer = new Timer(1000, new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent arg0) {
							// Calculate our hours, minutes, and seconds
							int currHours = totalSeconds / 3600;
							int currMin = (totalSeconds - (currHours * 3600)) / 60;
							int currSecs = totalSeconds - (currHours * 3600) - (currMin * 60);
							
							// Format our time string to display minutes and seconds with 2 digits always (e.g. 0:00:01)
							total_time_label.setText(Math.abs(currHours) + ":" + String.format("%02d", Math.abs(currMin)) + ":" + String.format("%02d", Math.abs(currSecs)));
							
							// Calculate and display our profit per hour
							int prof_hour = (int) (totalProfit * (3600.0 / (double)totalSeconds));
							profit_hour_label.setText("$" + prof_hour + "K");
							
							// Increment our second counter
							totalSeconds++;
						}
					});
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MainFrame() {
		setResizable(false);
		setTitle("Flipchat Tracker - Created by i96 - v" + version);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 685, 490);
		
		// Set location of window to be in center of user's screen
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		JMenu mnAddItem = new JMenu("Add item from...");
		mnAddItem.setSelectedIcon(null);
		mnFile.add(mnAddItem);
		
		/*
		 * The menu item below is fully commented for explanation of the process.
		 * Other menu items are not commented to avoid repetition.
		 */
		JMenuItem mntmArmadylLine = new JMenuItem("Armadyl Line");
		mntmArmadylLine.addActionListener(new ActionListener() {
			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			public void actionPerformed(ActionEvent e) {
				// Form new ArrayList from the array of Strings, which are the item descriptions
				ArrayList<String> items = new ArrayList<String>(Arrays.asList(new String[]{"AH - Armadyl helmet", "ACP - Armadyl chestplate", "ACS - Armadyl chainskirt", "AG - Armadyl gloves", "AB - Armadyl boots", "ACB - Armadyl crossbow", "BUCK - Armadyl buckler"}));
				// Check to make sure the item isn't on a buy restriction; if so, remove it from the list
				for (BuyRestriction br: buyRestrictions) {
					if (items.contains(br.getDesc())) {
						items.remove(br.getDesc());
					}
				}
				// Set the values of the JFrame accordingly and display the window
				iFrame.itemComboBox.setModel(new DefaultComboBoxModel(items.toArray()));
				iFrame.rdbtnBuying.setSelected(true);
				iFrame.marginSellTextField.setText("");
				iFrame.marginBuyTextField.setText("");
				iFrame.setVisible(true);
			}
		});
		mnAddItem.add(mntmArmadylLine);
		
		JMenuItem mntmBandosLine = new JMenuItem("Bandos Line");
		mntmBandosLine.addActionListener(new ActionListener() {
			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			public void actionPerformed(ActionEvent e) {
				ArrayList<String> items = new ArrayList<String>((Collection<String>) new HashSet<String>(Arrays.asList(new String[]{"BH - Bandos helmet", "BCP - Bandos chestplate", "TASS - Bandos tassets", "BG - Bandos gloves", "BB - Bandos boots", "BWS - Bandos warshield"})));
				for (BuyRestriction br: buyRestrictions) {
					if (items.contains(br.getDesc())) {
						items.remove(br.getDesc());
					}
				}
				iFrame.itemComboBox.setModel(new DefaultComboBoxModel(items.toArray()));
				iFrame.rdbtnBuying.setSelected(true);
				iFrame.marginSellTextField.setText("");
				iFrame.marginBuyTextField.setText("");
				iFrame.setVisible(true);
			}
		});
		mnAddItem.add(mntmBandosLine);
		
		JMenuItem mntmShieldLine = new JMenuItem("Shield Line");
		mntmShieldLine.addActionListener(new ActionListener() {
			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			public void actionPerformed(ActionEvent e) {
				ArrayList<String> items = new ArrayList<String>((Collection<String>) new HashSet<String>(Arrays.asList(new String[]{"SPEC - Spectral spirit shield", "VENG - Vengeful kiteshield", "MERC - Merciless kiteshield", "MAL - Malevolent kiteshield"})));
				for (BuyRestriction br: buyRestrictions) {
					if (items.contains(br.getDesc())) {
						items.remove(br.getDesc());
					}
				}
				iFrame.itemComboBox.setModel(new DefaultComboBoxModel(items.toArray()));
				iFrame.rdbtnBuying.setSelected(true);
				iFrame.marginSellTextField.setText("");
				iFrame.marginBuyTextField.setText("");
				iFrame.setVisible(true);
			}
		});
		mnAddItem.add(mntmShieldLine);
		
		JMenuItem mntmSubjugationLine = new JMenuItem("Subjugation Line");
		mntmSubjugationLine.addActionListener(new ActionListener() {
			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			public void actionPerformed(ActionEvent e) {
				ArrayList<String> items = new ArrayList<String>((Collection<String>) new HashSet<String>(Arrays.asList(new String[]{"HOOD - Hood of subjugation", "GARB - Garb of subjugation", "GOWN - Gown of subjugation", "SG - Gloves of subjugation", "SB - Boots of subjugation", "WARD - Ward of subjugation"})));
				for (BuyRestriction br: buyRestrictions) {
					if (items.contains(br.getDesc())) {
						items.remove(br.getDesc());
					}
				}
				iFrame.itemComboBox.setModel(new DefaultComboBoxModel(items.toArray()));
				iFrame.rdbtnBuying.setSelected(true);
				iFrame.marginSellTextField.setText("");
				iFrame.marginBuyTextField.setText("");
				iFrame.setVisible(true);
			}
		});
		mnAddItem.add(mntmSubjugationLine);
		
		JSeparator separator = new JSeparator();
		mnAddItem.add(separator);
		
		JMenuItem mntmMiscLine = new JMenuItem("Misc. Line");
		mntmMiscLine.addActionListener(new ActionListener() {
			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			public void actionPerformed(ActionEvent e) {
				ArrayList<String> items = new ArrayList<String>((Collection<String>) new HashSet<String>(Arrays.asList(new String[]{"ROBIN - Robin hood hat", "RANGER - Ranger boots", "HISS - Saradomin's hiss", "MURM - Saradomin's murmur", "WHISP - Saradomin's whisper"})));
				for (BuyRestriction br: buyRestrictions) {
					if (items.contains(br.getDesc())) {
						items.remove(br.getDesc());
					}
				}
				iFrame.itemComboBox.setModel(new DefaultComboBoxModel(items.toArray()));
				iFrame.rdbtnBuying.setSelected(true);
				iFrame.marginSellTextField.setText("");
				iFrame.marginBuyTextField.setText("");
				iFrame.setVisible(true);
			}
		});
		mnAddItem.add(mntmMiscLine);
		
		JMenu mnEdit = new JMenu("Edit");
		menuBar.add(mnEdit);
		
		JMenuItem mntmBoughtsold = new JMenuItem("Bought/Sold");
		mntmBoughtsold.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Determine the index number of the button that was selected
				int idx = 0;
				for (JRadioButton btn: itemSelectArray) {
					if (btn.isSelected())
						break;
					idx++;
				}
				// If idx == 6, we know there was no selection. If idx > 6, well, that's just weird.
				if (idx < 6) {
					// Check to see if we are buying or selling this item
					if (itemStatusArray.get(idx).getText().equals("BUYING")) {
						// Reset some things and mark it as selling
						itemStatusArray.get(idx).setText("SELLING");
						itemStatusArray.get(idx).setForeground(new Color(72, 61, 139));
						itemPriceArray.get(idx).setText("$" + marginArray[idx][1] + "K");
						itemRulesArray.get(idx).setText("0");
						timerArray.get(idx).resetTimer();
						
						// Calculate x and y positions for the buy restriction
						int arrSize = buyRestrictions.size();
						int row = arrSize / 3;
						int col = arrSize % 3;
						int x = (col * 202) + 10;
						int y = (row * 32) + 11;
						
						// Make and display the buy restriction
						JPanel panel = new JPanel();
						panel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
						panel.setBounds(x, y, 192, 28);
						buy_restr_panel.add(panel);
						panel.setLayout(null);
						
						String desc = itemDescArray.get(idx).getText();
						JLabel lblDesc = new JLabel(desc);
						lblDesc.setToolTipText(desc);
						lblDesc.setBounds(10, 0, 130, 28);
						panel.add(lblDesc);
						
						// Set timer for our buy restriction to 4 hours
						JLabel lblTime = new JLabel("4:00:00");
						lblTime.setForeground(new Color(205, 133, 63));
						lblTime.setFont(new Font("Tahoma", Font.BOLD, 11));
						lblTime.setHorizontalAlignment(SwingConstants.TRAILING);
						lblTime.setBounds(144, 0, 41, 28);
						panel.add(lblTime);
						
						// Update the buy restriction panel
						buy_restr_panel.revalidate();
						buy_restr_panel.repaint();
						
						BuyRestriction br = new BuyRestriction(desc, panel, lblTime, arrSize);
						buyRestrictions.add(br);
						
						// Add it to the list of items that we have bought so far
						boughtItems.add(desc);
					}
					else {
						String desc = itemDescArray.get(idx).getText();
						
						// Since we're selling this, do this profit stuff only if we bought it
						if (boughtItems.contains(desc)) {
							boughtItems.remove(desc);
							int profit = buySellArray[idx][1] - buySellArray[idx][0];
							totalProfit += profit;
							total_profit_label.setText("$" + totalProfit + "K");
							
							// Calculate x and y positions for the profit summary
							int row = profitItems / 3;
							int col = profitItems++ % 3;
							int x = (col * 204) + 10;
							int y = (row * 46) + 11;
							
							// Make and display the profit summary
							JPanel panel = new JPanel();
							panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
							panel.setBounds(x, y, 194, 35);
							profit_panel.add(panel);
							panel.setLayout(null);
							
							JLabel desc_label = new JLabel(desc);
							desc_label.setToolTipText(desc);
							desc_label.setFont(new Font("Tahoma", Font.PLAIN, 11));
							desc_label.setBounds(10, 0, 113, 35);
							panel.add(desc_label);
							
							JLabel profit_label = new JLabel("$" + profit + "K");
							profit_label.setForeground(new Color(0, 128, 0));
							profit_label.setFont(new Font("Tahoma", Font.BOLD, 11));
							profit_label.setBounds(133, 0, 51, 35);
							panel.add(profit_label);
							
							// Update our profit panel
							profit_panel.revalidate();
							profit_panel.repaint();
						}
						
						// Shift the following items down 
						for (int i = idx; i < (numItems - 1); i++) {
							itemStatusArray.get(i).setText(itemStatusArray.get(i+1).getText());
							itemStatusArray.get(i).setForeground(itemStatusArray.get(i+1).getForeground());
							itemDescArray.get(i).setText(itemDescArray.get(i+1).getText());
							itemDescArray.get(i).setToolTipText(itemDescArray.get(i+1).getToolTipText());
							itemPriceArray.get(i).setText(itemPriceArray.get(i+1).getText());
							itemMarginArray.get(i).setText(itemMarginArray.get(i+1).getText());
							itemRulesArray.get(i).setText(itemRulesArray.get(i+1).getText());
							itemTimeArray.get(i).setText(itemTimeArray.get(i+1).getText());
							itemTimeArray.get(i).setForeground(itemTimeArray.get(i+1).getForeground());
							timerArray.get(i).changeCount(timerArray.get(i+1).getCount());
							buySellArray[i][0] = buySellArray[i+1][0];
							buySellArray[i][1] = buySellArray[i+1][1];
							marginArray[i][0] = marginArray[i+1][0];
							marginArray[i][1] = marginArray[i+1][1];
						}
						// Hide the last panel
						itemPanelArray.get(--numItems).setVisible(false);
						// Stop the last panel's timer (maybe helps performance?)
						timerArray.get(numItems).stop();
					}
					// Clear the selection
					tradingItemsButtonGroup.clearSelection();
				}
			}
		});
		mnEdit.add(mntmBoughtsold);
		
		JMenuItem mntmApplyRule = new JMenuItem("Apply rule");
		mntmApplyRule.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int idx = 0;
				for (JRadioButton btn: itemSelectArray) {
					if (btn.isSelected())
						break;
					idx++;
				}
				if (idx < 6) {
					String desc = itemDescArray.get(idx).getText();
					int ruleAmt = (desc.startsWith("VENG") || desc.startsWith("MAL") || desc.startsWith("MERC")) ? 250: 25;
					if (itemStatusArray.get(idx).getText().equals("BUYING")) {
						buySellArray[idx][0] += ruleAmt;
						itemPriceArray.get(idx).setText("$" + buySellArray[idx][0] + "K");
						itemRulesArray.get(idx).setText("" + (Integer.parseInt(itemRulesArray.get(idx).getText()) + 1));
						timerArray.get(idx).resetTimer();
					}
					else {
						buySellArray[idx][1] -= ruleAmt;
						itemPriceArray.get(idx).setText("$" + buySellArray[idx][1] + "K");
						itemRulesArray.get(idx).setText("" + (Integer.parseInt(itemRulesArray.get(idx).getText()) + 1));
						timerArray.get(idx).resetTimer();
					}
					tradingItemsButtonGroup.clearSelection();
				}
			}
		});
		mnEdit.add(mntmApplyRule);
		
		JMenuItem mntmAdjustPricemargins = new JMenuItem("Adjust price/margins");
		mntmAdjustPricemargins.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int idx = 0;
				for (JRadioButton btn: itemSelectArray) {
					if (btn.isSelected())
						break;
					idx++;
				}
				if (idx < 6) {
					String desc = itemDescArray.get(idx).getText();
					adjust_dialog_idx_label.setText("" + idx);
					aDialog.desc_label.setText(desc);
					if (itemStatusArray.get(idx).getText().equals("BUYING")) {
						aDialog.price_field.setText("" + buySellArray[idx][0]);
					}
					else {
						aDialog.price_field.setText("" + buySellArray[idx][1]);
					}
					aDialog.buy_field.setText("" + marginArray[idx][0]);
					aDialog.sell_field.setText("" + marginArray[idx][1]);
					aDialog.setModal(true);
					aDialog.setVisible(true);
				}
			}
		});
		mnEdit.add(mntmAdjustPricemargins);
		
		JSeparator separator_1 = new JSeparator();
		mnEdit.add(separator_1);
		
		JMenuItem mntmDeleteItem = new JMenuItem("Delete item");
		mntmDeleteItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int idx = 0;
				for (JRadioButton btn: itemSelectArray) {
					if (btn.isSelected())
						break;
					idx++;
				}
				if (idx < 6) {
					for (int i = idx; i < (numItems - 1); i++) {
						itemStatusArray.get(i).setText(itemStatusArray.get(i+1).getText());
						itemStatusArray.get(i).setForeground(itemStatusArray.get(i+1).getForeground());
						itemDescArray.get(i).setText(itemDescArray.get(i+1).getText());
						itemDescArray.get(i).setToolTipText(itemDescArray.get(i+1).getToolTipText());
						itemPriceArray.get(i).setText(itemPriceArray.get(i+1).getText());
						itemMarginArray.get(i).setText(itemMarginArray.get(i+1).getText());
						itemRulesArray.get(i).setText(itemRulesArray.get(i+1).getText());
						itemTimeArray.get(i).setText(itemTimeArray.get(i+1).getText());
						itemTimeArray.get(i).setForeground(itemTimeArray.get(i+1).getForeground());
						timerArray.get(i).changeCount(timerArray.get(i+1).getCount());
						buySellArray[i][0] = buySellArray[i+1][0];
						buySellArray[i][1] = buySellArray[i+1][1];
						marginArray[i][0] = marginArray[i+1][0];
						marginArray[i][1] = marginArray[i+1][1];
					}
					itemPanelArray.get(--numItems).setVisible(false);
					timerArray.get(numItems).stop();
					tradingItemsButtonGroup.clearSelection();
				}
			}
		});
		mnEdit.add(mntmDeleteItem);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setFocusable(false);
		contentPane.add(tabbedPane, BorderLayout.CENTER);
		
		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new EmptyBorder(5, 5, 5, 5));
		tabbedPane.addTab("Trades", null, panel_2, null);
		panel_2.setLayout(new BorderLayout(0, 0));
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setResizeWeight(0.78);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane.setDividerSize(0);
		splitPane.setBorder(null);
		panel_2.add(splitPane, BorderLayout.CENTER);
		
		JPanel panel = new JPanel();
		panel.setToolTipText("");
		panel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Items I'm Trading", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		splitPane.setLeftComponent(panel);
		panel.setLayout(null);
		
		JPanel item_panel_1 = new JPanel();
		item_panel_1.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		item_panel_1.setBounds(10, 21, 101, 241);
		panel.add(item_panel_1);
		item_panel_1.setLayout(null);
		
		JRadioButton item_select_1 = new JRadioButton("");
		tradingItemsButtonGroup.add(item_select_1);
		item_select_1.setForeground(new Color(128, 0, 128));
		item_select_1.setFont(new Font("Tahoma", Font.PLAIN, 16));
		item_select_1.setHorizontalAlignment(SwingConstants.CENTER);
		item_select_1.setBounds(6, 7, 89, 16);
		item_panel_1.add(item_select_1);
		
		JLabel item_desc_1 = new JLabel("ACB - Armadyl crossbow");
		item_desc_1.setForeground(new Color(205, 92, 92));
		item_desc_1.setFont(new Font("Tahoma", Font.BOLD, 12));
		item_desc_1.setBounds(6, 55, 89, 23);
		item_panel_1.add(item_desc_1);
		
		JLabel lblPrice = new JLabel("Price");
		lblPrice.setForeground(Color.DARK_GRAY);
		lblPrice.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblPrice.setHorizontalAlignment(SwingConstants.CENTER);
		lblPrice.setBounds(6, 77, 89, 14);
		item_panel_1.add(lblPrice);
		
		JLabel item_price_1 = new JLabel("$2374K");
		item_price_1.setForeground(new Color(0, 128, 0));
		item_price_1.setHorizontalAlignment(SwingConstants.CENTER);
		item_price_1.setFont(new Font("Tahoma", Font.BOLD, 12));
		item_price_1.setBounds(6, 93, 89, 14);
		item_panel_1.add(item_price_1);
		
		JLabel lblMargin_1 = new JLabel("Margin");
		lblMargin_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblMargin_1.setForeground(Color.DARK_GRAY);
		lblMargin_1.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblMargin_1.setBounds(6, 118, 89, 14);
		item_panel_1.add(lblMargin_1);
		
		JLabel item_margin_1 = new JLabel("23742 - 91023");
		item_margin_1.setForeground(new Color(30, 144, 255));
		item_margin_1.setHorizontalAlignment(SwingConstants.CENTER);
		item_margin_1.setFont(new Font("Tahoma", Font.PLAIN, 12));
		item_margin_1.setBounds(6, 135, 89, 14);
		item_panel_1.add(item_margin_1);
		
		JLabel lblTimeToRule = new JLabel("Time to Rule");
		lblTimeToRule.setHorizontalAlignment(SwingConstants.CENTER);
		lblTimeToRule.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblTimeToRule.setForeground(Color.DARK_GRAY);
		lblTimeToRule.setBounds(6, 160, 89, 14);
		item_panel_1.add(lblTimeToRule);
		
		JLabel item_time_1 = new JLabel("25:00");
		item_time_1.setForeground(new Color(0, 128, 128));
		item_time_1.setHorizontalAlignment(SwingConstants.CENTER);
		item_time_1.setFont(new Font("Tahoma", Font.BOLD, 12));
		item_time_1.setBounds(6, 174, 89, 14);
		item_panel_1.add(item_time_1);
		
		JLabel item_status_1 = new JLabel("BUYING");
		item_status_1.setForeground(new Color(128, 0, 128));
		item_status_1.setHorizontalAlignment(SwingConstants.CENTER);
		item_status_1.setFont(new Font("Tahoma", Font.BOLD, 14));
		item_status_1.setBounds(6, 30, 89, 23);
		item_panel_1.add(item_status_1);
		
		JPanel item_panel_2 = new JPanel();
		item_panel_2.setLayout(null);
		item_panel_2.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		item_panel_2.setBounds(115, 21, 101, 241);
		panel.add(item_panel_2);
		
		JRadioButton item_select_2 = new JRadioButton("");
		tradingItemsButtonGroup.add(item_select_2);
		item_select_2.setHorizontalAlignment(SwingConstants.CENTER);
		item_select_2.setForeground(new Color(128, 0, 128));
		item_select_2.setFont(new Font("Tahoma", Font.PLAIN, 16));
		item_select_2.setBounds(6, 7, 89, 16);
		item_panel_2.add(item_select_2);
		
		JLabel item_desc_2 = new JLabel("ACB - Armadyl crossbow");
		item_desc_2.setForeground(new Color(205, 92, 92));
		item_desc_2.setFont(new Font("Tahoma", Font.BOLD, 12));
		item_desc_2.setBounds(6, 55, 89, 23);
		item_panel_2.add(item_desc_2);
		
		JLabel label_1 = new JLabel("Price");
		label_1.setHorizontalAlignment(SwingConstants.CENTER);
		label_1.setForeground(Color.DARK_GRAY);
		label_1.setFont(new Font("Tahoma", Font.BOLD, 11));
		label_1.setBounds(6, 77, 89, 14);
		item_panel_2.add(label_1);
		
		JLabel item_price_2 = new JLabel("$2374K");
		item_price_2.setHorizontalAlignment(SwingConstants.CENTER);
		item_price_2.setForeground(new Color(0, 128, 0));
		item_price_2.setFont(new Font("Tahoma", Font.BOLD, 12));
		item_price_2.setBounds(6, 93, 89, 14);
		item_panel_2.add(item_price_2);
		
		JLabel label_3 = new JLabel("Margin");
		label_3.setHorizontalAlignment(SwingConstants.CENTER);
		label_3.setForeground(Color.DARK_GRAY);
		label_3.setFont(new Font("Tahoma", Font.BOLD, 11));
		label_3.setBounds(6, 118, 89, 14);
		item_panel_2.add(label_3);
		
		JLabel item_margin_2 = new JLabel("23742 - 91023");
		item_margin_2.setHorizontalAlignment(SwingConstants.CENTER);
		item_margin_2.setForeground(new Color(30, 144, 255));
		item_margin_2.setFont(new Font("Tahoma", Font.PLAIN, 12));
		item_margin_2.setBounds(6, 135, 89, 14);
		item_panel_2.add(item_margin_2);
		
		JLabel label_5 = new JLabel("Time to Rule");
		label_5.setHorizontalAlignment(SwingConstants.CENTER);
		label_5.setForeground(Color.DARK_GRAY);
		label_5.setFont(new Font("Tahoma", Font.BOLD, 11));
		label_5.setBounds(6, 160, 89, 14);
		item_panel_2.add(label_5);
		
		JLabel item_time_2 = new JLabel("25:00");
		item_time_2.setHorizontalAlignment(SwingConstants.CENTER);
		item_time_2.setForeground(new Color(0, 128, 128));
		item_time_2.setFont(new Font("Tahoma", Font.BOLD, 12));
		item_time_2.setBounds(6, 174, 89, 14);
		item_panel_2.add(item_time_2);
		
		JLabel item_status_2 = new JLabel("BUYING");
		item_status_2.setHorizontalAlignment(SwingConstants.CENTER);
		item_status_2.setForeground(new Color(128, 0, 128));
		item_status_2.setFont(new Font("Tahoma", Font.BOLD, 14));
		item_status_2.setBounds(6, 30, 89, 23);
		item_panel_2.add(item_status_2);
		
		JPanel item_panel_3 = new JPanel();
		item_panel_3.setLayout(null);
		item_panel_3.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		item_panel_3.setBounds(220, 21, 101, 241);
		panel.add(item_panel_3);
		
		JRadioButton item_select_3 = new JRadioButton("");
		tradingItemsButtonGroup.add(item_select_3);
		item_select_3.setHorizontalAlignment(SwingConstants.CENTER);
		item_select_3.setForeground(new Color(128, 0, 128));
		item_select_3.setFont(new Font("Tahoma", Font.PLAIN, 16));
		item_select_3.setBounds(6, 7, 89, 16);
		item_panel_3.add(item_select_3);
		
		JLabel item_desc_3 = new JLabel("ACB - Armadyl crossbow");
		item_desc_3.setForeground(new Color(205, 92, 92));
		item_desc_3.setFont(new Font("Tahoma", Font.BOLD, 12));
		item_desc_3.setBounds(6, 55, 89, 23);
		item_panel_3.add(item_desc_3);
		
		JLabel label_2 = new JLabel("Price");
		label_2.setHorizontalAlignment(SwingConstants.CENTER);
		label_2.setForeground(Color.DARK_GRAY);
		label_2.setFont(new Font("Tahoma", Font.BOLD, 11));
		label_2.setBounds(6, 77, 89, 14);
		item_panel_3.add(label_2);
		
		JLabel item_price_3 = new JLabel("$2374K");
		item_price_3.setHorizontalAlignment(SwingConstants.CENTER);
		item_price_3.setForeground(new Color(0, 128, 0));
		item_price_3.setFont(new Font("Tahoma", Font.BOLD, 12));
		item_price_3.setBounds(6, 93, 89, 14);
		item_panel_3.add(item_price_3);
		
		JLabel label_6 = new JLabel("Margin");
		label_6.setHorizontalAlignment(SwingConstants.CENTER);
		label_6.setForeground(Color.DARK_GRAY);
		label_6.setFont(new Font("Tahoma", Font.BOLD, 11));
		label_6.setBounds(6, 118, 89, 14);
		item_panel_3.add(label_6);
		
		JLabel item_margin_3 = new JLabel("23742 - 91023");
		item_margin_3.setHorizontalAlignment(SwingConstants.CENTER);
		item_margin_3.setForeground(new Color(30, 144, 255));
		item_margin_3.setFont(new Font("Tahoma", Font.PLAIN, 12));
		item_margin_3.setBounds(6, 135, 89, 14);
		item_panel_3.add(item_margin_3);
		
		JLabel label_8 = new JLabel("Time to Rule");
		label_8.setHorizontalAlignment(SwingConstants.CENTER);
		label_8.setForeground(Color.DARK_GRAY);
		label_8.setFont(new Font("Tahoma", Font.BOLD, 11));
		label_8.setBounds(6, 160, 89, 14);
		item_panel_3.add(label_8);
		
		JLabel item_time_3 = new JLabel("25:00");
		item_time_3.setHorizontalAlignment(SwingConstants.CENTER);
		item_time_3.setForeground(new Color(0, 128, 128));
		item_time_3.setFont(new Font("Tahoma", Font.BOLD, 12));
		item_time_3.setBounds(6, 174, 89, 14);
		item_panel_3.add(item_time_3);
		
		JLabel item_status_3 = new JLabel("BUYING");
		item_status_3.setHorizontalAlignment(SwingConstants.CENTER);
		item_status_3.setForeground(new Color(128, 0, 128));
		item_status_3.setFont(new Font("Tahoma", Font.BOLD, 14));
		item_status_3.setBounds(6, 30, 89, 23);
		item_panel_3.add(item_status_3);
		
		JPanel item_panel_4 = new JPanel();
		item_panel_4.setLayout(null);
		item_panel_4.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		item_panel_4.setBounds(325, 21, 101, 241);
		panel.add(item_panel_4);
		
		JRadioButton item_select_4 = new JRadioButton("");
		tradingItemsButtonGroup.add(item_select_4);
		item_select_4.setHorizontalAlignment(SwingConstants.CENTER);
		item_select_4.setForeground(new Color(128, 0, 128));
		item_select_4.setFont(new Font("Tahoma", Font.PLAIN, 16));
		item_select_4.setBounds(6, 7, 89, 16);
		item_panel_4.add(item_select_4);
		
		JLabel item_desc_4 = new JLabel("ACB - Armadyl crossbow");
		item_desc_4.setForeground(new Color(205, 92, 92));
		item_desc_4.setFont(new Font("Tahoma", Font.BOLD, 12));
		item_desc_4.setBounds(6, 55, 89, 23);
		item_panel_4.add(item_desc_4);
		
		JLabel label_4 = new JLabel("Price");
		label_4.setHorizontalAlignment(SwingConstants.CENTER);
		label_4.setForeground(Color.DARK_GRAY);
		label_4.setFont(new Font("Tahoma", Font.BOLD, 11));
		label_4.setBounds(6, 77, 89, 14);
		item_panel_4.add(label_4);
		
		JLabel item_price_4 = new JLabel("$2374K");
		item_price_4.setHorizontalAlignment(SwingConstants.CENTER);
		item_price_4.setForeground(new Color(0, 128, 0));
		item_price_4.setFont(new Font("Tahoma", Font.BOLD, 12));
		item_price_4.setBounds(6, 93, 89, 14);
		item_panel_4.add(item_price_4);
		
		JLabel label_9 = new JLabel("Margin");
		label_9.setHorizontalAlignment(SwingConstants.CENTER);
		label_9.setForeground(Color.DARK_GRAY);
		label_9.setFont(new Font("Tahoma", Font.BOLD, 11));
		label_9.setBounds(6, 118, 89, 14);
		item_panel_4.add(label_9);
		
		JLabel item_margin_4 = new JLabel("23742 - 91023");
		item_margin_4.setHorizontalAlignment(SwingConstants.CENTER);
		item_margin_4.setForeground(new Color(30, 144, 255));
		item_margin_4.setFont(new Font("Tahoma", Font.PLAIN, 12));
		item_margin_4.setBounds(6, 135, 89, 14);
		item_panel_4.add(item_margin_4);
		
		JLabel label_11 = new JLabel("Time to Rule");
		label_11.setHorizontalAlignment(SwingConstants.CENTER);
		label_11.setForeground(Color.DARK_GRAY);
		label_11.setFont(new Font("Tahoma", Font.BOLD, 11));
		label_11.setBounds(6, 160, 89, 14);
		item_panel_4.add(label_11);
		
		JLabel item_time_4 = new JLabel("25:00");
		item_time_4.setHorizontalAlignment(SwingConstants.CENTER);
		item_time_4.setForeground(new Color(0, 128, 128));
		item_time_4.setFont(new Font("Tahoma", Font.BOLD, 12));
		item_time_4.setBounds(6, 174, 89, 14);
		item_panel_4.add(item_time_4);
		
		JLabel item_status_4 = new JLabel("BUYING");
		item_status_4.setHorizontalAlignment(SwingConstants.CENTER);
		item_status_4.setForeground(new Color(128, 0, 128));
		item_status_4.setFont(new Font("Tahoma", Font.BOLD, 14));
		item_status_4.setBounds(6, 30, 89, 23);
		item_panel_4.add(item_status_4);
		
		JPanel item_panel_5 = new JPanel();
		item_panel_5.setLayout(null);
		item_panel_5.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		item_panel_5.setBounds(430, 21, 101, 241);
		panel.add(item_panel_5);
		
		JRadioButton item_select_5 = new JRadioButton("");
		tradingItemsButtonGroup.add(item_select_5);
		item_select_5.setHorizontalAlignment(SwingConstants.CENTER);
		item_select_5.setForeground(new Color(128, 0, 128));
		item_select_5.setFont(new Font("Tahoma", Font.PLAIN, 16));
		item_select_5.setBounds(6, 7, 89, 16);
		item_panel_5.add(item_select_5);
		
		JLabel item_desc_5 = new JLabel("ACB - Armadyl crossbow");
		item_desc_5.setForeground(new Color(205, 92, 92));
		item_desc_5.setFont(new Font("Tahoma", Font.BOLD, 12));
		item_desc_5.setBounds(6, 55, 89, 23);
		item_panel_5.add(item_desc_5);
		
		JLabel label_7 = new JLabel("Price");
		label_7.setHorizontalAlignment(SwingConstants.CENTER);
		label_7.setForeground(Color.DARK_GRAY);
		label_7.setFont(new Font("Tahoma", Font.BOLD, 11));
		label_7.setBounds(6, 77, 89, 14);
		item_panel_5.add(label_7);
		
		JLabel item_price_5 = new JLabel("$2374K");
		item_price_5.setHorizontalAlignment(SwingConstants.CENTER);
		item_price_5.setForeground(new Color(0, 128, 0));
		item_price_5.setFont(new Font("Tahoma", Font.BOLD, 12));
		item_price_5.setBounds(6, 93, 89, 14);
		item_panel_5.add(item_price_5);
		
		JLabel label_12 = new JLabel("Margin");
		label_12.setHorizontalAlignment(SwingConstants.CENTER);
		label_12.setForeground(Color.DARK_GRAY);
		label_12.setFont(new Font("Tahoma", Font.BOLD, 11));
		label_12.setBounds(6, 118, 89, 14);
		item_panel_5.add(label_12);
		
		JLabel item_margin_5 = new JLabel("23742 - 91023");
		item_margin_5.setHorizontalAlignment(SwingConstants.CENTER);
		item_margin_5.setForeground(new Color(30, 144, 255));
		item_margin_5.setFont(new Font("Tahoma", Font.PLAIN, 12));
		item_margin_5.setBounds(6, 135, 89, 14);
		item_panel_5.add(item_margin_5);
		
		JLabel label_14 = new JLabel("Time to Rule");
		label_14.setHorizontalAlignment(SwingConstants.CENTER);
		label_14.setForeground(Color.DARK_GRAY);
		label_14.setFont(new Font("Tahoma", Font.BOLD, 11));
		label_14.setBounds(6, 160, 89, 14);
		item_panel_5.add(label_14);
		
		JLabel item_time_5 = new JLabel("25:00");
		item_time_5.setHorizontalAlignment(SwingConstants.CENTER);
		item_time_5.setForeground(new Color(0, 128, 128));
		item_time_5.setFont(new Font("Tahoma", Font.BOLD, 12));
		item_time_5.setBounds(6, 174, 89, 14);
		item_panel_5.add(item_time_5);
		
		JLabel item_status_5 = new JLabel("BUYING");
		item_status_5.setHorizontalAlignment(SwingConstants.CENTER);
		item_status_5.setForeground(new Color(128, 0, 128));
		item_status_5.setFont(new Font("Tahoma", Font.BOLD, 14));
		item_status_5.setBounds(6, 30, 89, 23);
		item_panel_5.add(item_status_5);
		
		JPanel item_panel_6 = new JPanel();
		item_panel_6.setLayout(null);
		item_panel_6.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		item_panel_6.setBounds(535, 21, 101, 241);
		panel.add(item_panel_6);
		
		JRadioButton item_select_6 = new JRadioButton("");
		tradingItemsButtonGroup.add(item_select_6);
		item_select_6.setHorizontalAlignment(SwingConstants.CENTER);
		item_select_6.setForeground(new Color(128, 0, 128));
		item_select_6.setFont(new Font("Tahoma", Font.PLAIN, 16));
		item_select_6.setBounds(6, 7, 89, 16);
		item_panel_6.add(item_select_6);
		
		JLabel item_desc_6 = new JLabel("ACB - Armadyl crossbow");
		item_desc_6.setForeground(new Color(205, 92, 92));
		item_desc_6.setFont(new Font("Tahoma", Font.BOLD, 12));
		item_desc_6.setBounds(6, 55, 89, 23);
		item_panel_6.add(item_desc_6);
		
		JLabel label_18 = new JLabel("Price");
		label_18.setHorizontalAlignment(SwingConstants.CENTER);
		label_18.setForeground(Color.DARK_GRAY);
		label_18.setFont(new Font("Tahoma", Font.BOLD, 11));
		label_18.setBounds(6, 77, 89, 14);
		item_panel_6.add(label_18);
		
		JLabel item_price_6 = new JLabel("$2374K");
		item_price_6.setHorizontalAlignment(SwingConstants.CENTER);
		item_price_6.setForeground(new Color(0, 128, 0));
		item_price_6.setFont(new Font("Tahoma", Font.BOLD, 12));
		item_price_6.setBounds(6, 93, 89, 14);
		item_panel_6.add(item_price_6);
		
		JLabel label_20 = new JLabel("Margin");
		label_20.setHorizontalAlignment(SwingConstants.CENTER);
		label_20.setForeground(Color.DARK_GRAY);
		label_20.setFont(new Font("Tahoma", Font.BOLD, 11));
		label_20.setBounds(6, 118, 89, 14);
		item_panel_6.add(label_20);
		
		JLabel item_margin_6 = new JLabel("23742 - 91023");
		item_margin_6.setHorizontalAlignment(SwingConstants.CENTER);
		item_margin_6.setForeground(new Color(30, 144, 255));
		item_margin_6.setFont(new Font("Tahoma", Font.PLAIN, 12));
		item_margin_6.setBounds(6, 135, 89, 14);
		item_panel_6.add(item_margin_6);
		
		JLabel label_22 = new JLabel("Time to Rule");
		label_22.setHorizontalAlignment(SwingConstants.CENTER);
		label_22.setForeground(Color.DARK_GRAY);
		label_22.setFont(new Font("Tahoma", Font.BOLD, 11));
		label_22.setBounds(6, 160, 89, 14);
		item_panel_6.add(label_22);
		
		JLabel item_time_6 = new JLabel("25:00");
		item_time_6.setHorizontalAlignment(SwingConstants.CENTER);
		item_time_6.setForeground(new Color(0, 128, 128));
		item_time_6.setFont(new Font("Tahoma", Font.BOLD, 12));
		item_time_6.setBounds(6, 174, 89, 14);
		item_panel_6.add(item_time_6);
		
		JLabel item_status_6 = new JLabel("BUYING");
		item_status_6.setHorizontalAlignment(SwingConstants.CENTER);
		item_status_6.setForeground(new Color(128, 0, 128));
		item_status_6.setFont(new Font("Tahoma", Font.BOLD, 14));
		item_status_6.setBounds(6, 30, 89, 23);
		item_panel_6.add(item_status_6);
		
		
		JLabel lblNumRules = new JLabel("Num Rules");
		lblNumRules.setHorizontalAlignment(SwingConstants.CENTER);
		lblNumRules.setForeground(Color.DARK_GRAY);
		lblNumRules.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblNumRules.setBounds(6, 199, 89, 14);
		item_panel_1.add(lblNumRules);
		
		JLabel item_rules_1 = new JLabel("0");
		item_rules_1.setHorizontalAlignment(SwingConstants.CENTER);
		item_rules_1.setForeground(new Color(119, 136, 153));
		item_rules_1.setFont(new Font("Tahoma", Font.BOLD, 12));
		item_rules_1.setBounds(6, 213, 89, 14);
		item_panel_1.add(item_rules_1);

		
		JLabel label = new JLabel("Num Rules");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setForeground(Color.DARK_GRAY);
		label.setFont(new Font("Tahoma", Font.BOLD, 11));
		label.setBounds(6, 199, 89, 14);
		item_panel_2.add(label);
		
		JLabel item_rules_2 = new JLabel("0");
		item_rules_2.setHorizontalAlignment(SwingConstants.CENTER);
		item_rules_2.setForeground(new Color(119, 136, 153));
		item_rules_2.setFont(new Font("Tahoma", Font.BOLD, 12));
		item_rules_2.setBounds(6, 213, 89, 14);
		item_panel_2.add(item_rules_2);

		
		JLabel label_13 = new JLabel("Num Rules");
		label_13.setHorizontalAlignment(SwingConstants.CENTER);
		label_13.setForeground(Color.DARK_GRAY);
		label_13.setFont(new Font("Tahoma", Font.BOLD, 11));
		label_13.setBounds(6, 199, 89, 14);
		item_panel_3.add(label_13);
		
		JLabel item_rules_3 = new JLabel("0");
		item_rules_3.setHorizontalAlignment(SwingConstants.CENTER);
		item_rules_3.setForeground(new Color(119, 136, 153));
		item_rules_3.setFont(new Font("Tahoma", Font.BOLD, 12));
		item_rules_3.setBounds(6, 213, 89, 14);
		item_panel_3.add(item_rules_3);

		
		JLabel label_16 = new JLabel("Num Rules");
		label_16.setHorizontalAlignment(SwingConstants.CENTER);
		label_16.setForeground(Color.DARK_GRAY);
		label_16.setFont(new Font("Tahoma", Font.BOLD, 11));
		label_16.setBounds(6, 199, 89, 14);
		item_panel_4.add(label_16);
		
		JLabel item_rules_4 = new JLabel("0");
		item_rules_4.setHorizontalAlignment(SwingConstants.CENTER);
		item_rules_4.setForeground(new Color(119, 136, 153));
		item_rules_4.setFont(new Font("Tahoma", Font.BOLD, 12));
		item_rules_4.setBounds(6, 213, 89, 14);
		item_panel_4.add(item_rules_4);

		
		JLabel label_19 = new JLabel("Num Rules");
		label_19.setHorizontalAlignment(SwingConstants.CENTER);
		label_19.setForeground(Color.DARK_GRAY);
		label_19.setFont(new Font("Tahoma", Font.BOLD, 11));
		label_19.setBounds(6, 199, 89, 14);
		item_panel_5.add(label_19);
		
		JLabel item_rules_5 = new JLabel("0");
		item_rules_5.setHorizontalAlignment(SwingConstants.CENTER);
		item_rules_5.setForeground(new Color(119, 136, 153));
		item_rules_5.setFont(new Font("Tahoma", Font.BOLD, 12));
		item_rules_5.setBounds(6, 213, 89, 14);
		item_panel_5.add(item_rules_5);

		
		JLabel label_23 = new JLabel("Num Rules");
		label_23.setHorizontalAlignment(SwingConstants.CENTER);
		label_23.setForeground(Color.DARK_GRAY);
		label_23.setFont(new Font("Tahoma", Font.BOLD, 11));
		label_23.setBounds(6, 199, 89, 14);
		item_panel_6.add(label_23);
		
		JLabel item_rules_6 = new JLabel("0");
		item_rules_6.setHorizontalAlignment(SwingConstants.CENTER);
		item_rules_6.setForeground(new Color(119, 136, 153));
		item_rules_6.setFont(new Font("Tahoma", Font.BOLD, 12));
		item_rules_6.setBounds(6, 213, 89, 14);
		item_panel_6.add(item_rules_6);
		
		// $hide>>$
		lblNoItemsIn = new JLabel("No items in trading! Add a new item to get started.");
		lblNoItemsIn.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblNoItemsIn.setBounds(166, 29, 308, 17);
		panel.add(lblNoItemsIn);
		// $hide<<$
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(null, "Items On Buy Restriction", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		splitPane.setRightComponent(panel_1);
		panel_1.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setBorder(null);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		panel_1.add(scrollPane, BorderLayout.CENTER);
		
		buy_restr_panel = new JPanel();
		buy_restr_panel.setPreferredSize(new Dimension(10, 400));
		buy_restr_panel.setBorder(null);
		scrollPane.setViewportView(buy_restr_panel);
		buy_restr_panel.setLayout(null);
		
		
		//Array sets
		itemPanelArray = new ArrayList<JPanel>();
		itemPanelArray.add(item_panel_1);
		itemPanelArray.add(item_panel_2);
		itemPanelArray.add(item_panel_3);
		itemPanelArray.add(item_panel_4);
		itemPanelArray.add(item_panel_5);
		itemPanelArray.add(item_panel_6);
		
		//Hide panels
		for (JPanel p: itemPanelArray) {
			p.setVisible(false);
		}
		
		itemSelectArray = new ArrayList<JRadioButton>();
		itemSelectArray.add(item_select_1);
		itemSelectArray.add(item_select_2);
		itemSelectArray.add(item_select_3);
		itemSelectArray.add(item_select_4);
		itemSelectArray.add(item_select_5);
		itemSelectArray.add(item_select_6);
		
		itemStatusArray = new ArrayList<JLabel>();
		itemStatusArray.add(item_status_1);
		itemStatusArray.add(item_status_2);
		itemStatusArray.add(item_status_3);
		itemStatusArray.add(item_status_4);
		itemStatusArray.add(item_status_5);
		itemStatusArray.add(item_status_6);
		
		itemDescArray = new ArrayList<JLabel>();
		itemDescArray.add(item_desc_1);
		itemDescArray.add(item_desc_2);
		itemDescArray.add(item_desc_3);
		itemDescArray.add(item_desc_4);
		itemDescArray.add(item_desc_5);
		itemDescArray.add(item_desc_6);
		
		itemPriceArray = new ArrayList<JLabel>();
		itemPriceArray.add(item_price_1);
		itemPriceArray.add(item_price_2);
		itemPriceArray.add(item_price_3);
		itemPriceArray.add(item_price_4);
		itemPriceArray.add(item_price_5);
		itemPriceArray.add(item_price_6);
		
		itemMarginArray = new ArrayList<JLabel>();
		itemMarginArray.add(item_margin_1);
		itemMarginArray.add(item_margin_2);
		itemMarginArray.add(item_margin_3);
		itemMarginArray.add(item_margin_4);
		itemMarginArray.add(item_margin_5);
		itemMarginArray.add(item_margin_6);
		
		itemTimeArray = new ArrayList<JLabel>();
		itemTimeArray.add(item_time_1);
		itemTimeArray.add(item_time_2);
		itemTimeArray.add(item_time_3);
		itemTimeArray.add(item_time_4);
		itemTimeArray.add(item_time_5);
		itemTimeArray.add(item_time_6);
		
		itemRulesArray = new ArrayList<JLabel>();
		itemRulesArray.add(item_rules_1);
		itemRulesArray.add(item_rules_2);
		itemRulesArray.add(item_rules_3);
		itemRulesArray.add(item_rules_4);
		itemRulesArray.add(item_rules_5);
		itemRulesArray.add(item_rules_6);
		
		JPanel panel_3 = new JPanel();
		tabbedPane.addTab("Profit / Loss", null, panel_3, null);
		panel_3.setLayout(null);
		
		JPanel panel_4 = new JPanel();
		panel_4.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panel_4.setBounds(10, 11, 634, 46);
		panel_3.add(panel_4);
		panel_4.setLayout(null);
		
		JLabel lblTotalTimeFlipping = new JLabel("Total time flipping:");
		lblTotalTimeFlipping.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblTotalTimeFlipping.setBounds(10, 11, 128, 24);
		panel_4.add(lblTotalTimeFlipping);
		
		total_time_label = new JLabel("0:00:00");
		total_time_label.setForeground(new Color(0, 128, 128));
		total_time_label.setFont(new Font("Tahoma", Font.BOLD, 14));
		total_time_label.setBounds(148, 11, 55, 24);
		panel_4.add(total_time_label);
		
		JLabel lblTotalProfit = new JLabel("Total profit:");
		lblTotalProfit.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblTotalProfit.setBounds(248, 11, 82, 24);
		panel_4.add(lblTotalProfit);
		
		total_profit_label = new JLabel("$0K");
		total_profit_label.setForeground(new Color(0, 128, 0));
		total_profit_label.setFont(new Font("Tahoma", Font.BOLD, 14));
		total_profit_label.setBounds(340, 11, 90, 24);
		panel_4.add(total_profit_label);
		
		JLabel lblProfithr = new JLabel("Profit/hr:");
		lblProfithr.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblProfithr.setBounds(458, 11, 66, 24);
		panel_4.add(lblProfithr);
		
		profit_hour_label = new JLabel("$0K");
		profit_hour_label.setForeground(new Color(128, 0, 128));
		profit_hour_label.setFont(new Font("Tahoma", Font.BOLD, 14));
		profit_hour_label.setBounds(534, 11, 90, 24);
		panel_4.add(profit_hour_label);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		scrollPane_1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane_1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane_1.setBounds(10, 68, 634, 314);
		panel_3.add(scrollPane_1);
		
		profit_panel = new JPanel();
		profit_panel.setPreferredSize(new Dimension(10, 1500));
		profit_panel.setBorder(null);
		scrollPane_1.setViewportView(profit_panel);
		profit_panel.setLayout(null);

		
		timerArray = new ArrayList<RuleTimer>();
		for (final JLabel tLabel: itemTimeArray) {
			timerArray.add(new RuleTimer(1000, tLabel));
		}
		
		buyRestrictions = new ArrayList<BuyRestriction>();
		boughtItems = new ArrayList<String>();
	}
	
	protected static class NewItemFrame extends JFrame {

		protected JPanel contentPane;
		protected final ButtonGroup buyingSellingButtonGroup = new ButtonGroup();
		protected JRadioButton rdbtnBuying, rdbtnSelling;
		protected JTextField marginBuyTextField;
		protected JTextField marginSellTextField;
		@SuppressWarnings("rawtypes")
		protected JComboBox itemComboBox;

		/**
		 * Create the frame.
		 */
		@SuppressWarnings({ "rawtypes" })
		public NewItemFrame() {
			setResizable(false);
			setTitle("Add New Item");
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			setBounds(100, 100, 349, 200);
			contentPane = new JPanel();
			contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
			setContentPane(contentPane);
			
			Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
			this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
			
			JLabel lblItemToAdd = new JLabel("Item to add:");
			
			itemComboBox = new JComboBox();
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
					if (numItems < 6) {
						if (rdbtnBuying.isSelected()) {
							itemStatusArray.get(numItems).setText("BUYING");
							itemStatusArray.get(numItems).setForeground(new Color(128, 0, 128));
							
							itemPriceArray.get(numItems).setText("$" + marginBuyTextField.getText() + "K");
						}
						else {
							itemStatusArray.get(numItems).setText("SELLING");
							itemStatusArray.get(numItems).setForeground(new Color(72, 61, 139));
							
							itemPriceArray.get(numItems).setText("$" + marginSellTextField.getText() + "K");
						}
						itemDescArray.get(numItems).setText(itemComboBox.getSelectedItem().toString());
						itemDescArray.get(numItems).setToolTipText(itemComboBox.getSelectedItem().toString());
						itemMarginArray.get(numItems).setText(marginBuyTextField.getText() + " - " + marginSellTextField.getText());
						itemRulesArray.get(numItems).setText("0");
						itemTimeArray.get(numItems).setText("25:00");
						timerArray.get(numItems).resetTimer();
						itemPanelArray.get(numItems).setVisible(true);
						int marginBuy = Integer.parseInt(marginBuyTextField.getText());
						int marginSell = Integer.parseInt(marginSellTextField.getText());
						buySellArray[numItems][0] = marginBuy;
						buySellArray[numItems][1] = marginSell;
						marginArray[numItems][0] = marginBuy;
						marginArray[numItems][1] = marginSell;
						numItems++;
					}
					
					if (!totalTimer.isRunning()) {
						totalTimer.start();
					}
					
					lblNoItemsIn.setVisible(false);
					iFrame.setVisible(false);
				}
			});
			
			GroupLayout gl_contentPane = new GroupLayout(contentPane);
			gl_contentPane.setHorizontalGroup(
				gl_contentPane.createParallelGroup(Alignment.LEADING)
					.addGroup(gl_contentPane.createSequentialGroup()
						.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
							.addGroup(gl_contentPane.createSequentialGroup()
								.addContainerGap()
								.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
									.addGroup(gl_contentPane.createSequentialGroup()
										.addComponent(lblItemToAdd)
										.addPreferredGap(ComponentPlacement.RELATED)
										.addComponent(itemComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
									.addComponent(lblMargin)
									.addGroup(gl_contentPane.createSequentialGroup()
										.addComponent(rdbtnBuying)
										.addGap(18)
										.addComponent(rdbtnSelling))))
							.addGroup(gl_contentPane.createSequentialGroup()
								.addGap(28)
								.addComponent(lblNewLabel)
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(marginBuyTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(ComponentPlacement.UNRELATED)
								.addComponent(lblTo)
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(marginSellTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
							.addGroup(gl_contentPane.createSequentialGroup()
								.addContainerGap()
								.addComponent(btnAddItem, GroupLayout.DEFAULT_SIZE, 303, Short.MAX_VALUE)))
						.addContainerGap())
			);
			gl_contentPane.setVerticalGroup(
				gl_contentPane.createParallelGroup(Alignment.LEADING)
					.addGroup(gl_contentPane.createSequentialGroup()
						.addContainerGap()
						.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
							.addComponent(lblItemToAdd)
							.addComponent(itemComboBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addGap(7)
						.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
							.addComponent(rdbtnBuying)
							.addComponent(rdbtnSelling))
						.addPreferredGap(ComponentPlacement.UNRELATED)
						.addComponent(lblMargin)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
							.addComponent(lblNewLabel)
							.addComponent(marginBuyTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
							.addComponent(lblTo)
							.addComponent(marginSellTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addGap(18)
						.addComponent(btnAddItem, GroupLayout.DEFAULT_SIZE, 26, Short.MAX_VALUE))
			);
			contentPane.setLayout(gl_contentPane);
		}
	}
	
	protected class RuleTimer extends Timer {	
		public RuleTimer(int delay, final JLabel label) {
			super(delay, new TimerActionListener(label));
		}
		
		public void resetTimer() {
			((TimerActionListener)this.getActionListeners()[0]).count = 1;
			this.restart();
		}
		
		public void changeCount(int newCount) {
			((TimerActionListener)this.getActionListeners()[0]).count = newCount;
		}
		
		public int getCount() {
			return ((TimerActionListener)this.getActionListeners()[0]).count;
		}
	}
	
	protected class TimerActionListener implements ActionListener {
		protected int count = 1;
		protected final static int max = 1500, yellow = 300, orange = 120;
		protected JLabel tLabel;
		
		public TimerActionListener(JLabel label) {
			super();
			tLabel = label;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			int curr = max - count++;
			int currMin = curr / 60;
			int currSecs = curr - (currMin * 60);
			String prefix = (curr < 0) ? "- " : "";
			tLabel.setText(prefix + Math.abs(currMin) + ":" + String.format("%02d", Math.abs(currSecs)));
			if (curr < 0) {
				tLabel.setForeground(new Color(128, 0, 0));
			}
			else if (curr < orange) {
				tLabel.setForeground(new Color(210, 105, 30));
			}
			else if (curr < yellow) {
				tLabel.setForeground(new Color(218, 165, 32));
			}
			else {
				tLabel.setForeground(new Color(0, 128, 128));
			}
		}
	}
	
	protected class BuyRestriction {
		private JPanel panel;
		private String desc;
		private BRTimer timer;
		
		public BuyRestriction(String desc, JPanel panel, JLabel label, int index) {
			this.desc = desc;
			this.panel = panel;
			timer = new BRTimer(1000, new BRTimerActionListener(label, index));
			timer.start();
		}
		
		public void stopTimer() {
			timer.stop();
		}
		
		public void updateIndex(int newIndex) {
			timer.updateIndex(newIndex);
		}
		
		public JPanel getPanel() {
			return panel;
		}
		
		public String getDesc() {
			return desc;
		}
		
		protected class BRTimer extends Timer {
			public BRTimer(int delay, BRTimerActionListener listener) {
				super(delay, listener);
			}
			
			public void updateIndex(int newIndex) {
				((BRTimerActionListener)this.getActionListeners()[0]).index = newIndex;
			}
		}
		
		protected class BRTimerActionListener implements ActionListener {			
			protected int count = 1;
			protected static final int max = 14400;
			protected JLabel jLabel;
			protected int index;
			
			public BRTimerActionListener(JLabel label, int idx) {
				super();
				jLabel = label;
				index = idx;
			}
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int curr = max - count++;
				
				if (curr < 0) {
					buyRestrictions.get(index).stopTimer();
					buyRestrictions.remove(index);
					buy_restr_panel.removeAll();
					for (int i = 0; i < buyRestrictions.size(); i++) {
						BuyRestriction br = buyRestrictions.get(i);
						br.updateIndex(i);
						int row = i / 3;
						int col = i % 3;
						int x = (col * 202) + 10;
						int y = (row * 32) + 11;
						
						int width = br.getPanel().getWidth();
						int height = br.getPanel().getHeight();
						br.getPanel().setBounds(x, y, width, height);
						buy_restr_panel.add(br.getPanel());
					}
					buy_restr_panel.revalidate();
					buy_restr_panel.repaint();
				}
				
				int currHours = curr / 3600;
				int currMin = (curr - (currHours * 3600)) / 60;
				int currSecs = curr - (currHours * 3600) - (currMin * 60);
				jLabel.setText(Math.abs(currHours) + ":" + String.format("%02d", Math.abs(currMin)) + ":" + String.format("%02d", Math.abs(currSecs)));
			}
		}
	}
	
	protected static class AdjustPriceMarginsDialog extends JDialog {
		protected final JPanel contentPanel = new JPanel();
		protected JTextField price_field, buy_field, sell_field;
		protected JLabel desc_label;

		/**
		 * Create the dialog.
		 */
		public AdjustPriceMarginsDialog() {
			setTitle("Adjust Price/Margins");
			setBounds(100, 100, 297, 189);
			getContentPane().setLayout(new BorderLayout());
			contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
			getContentPane().add(contentPanel, BorderLayout.CENTER);
			contentPanel.setLayout(null);
			
			Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
			this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
			
			JLabel lblItemAdjusting = new JLabel("Item adjusting:");
			lblItemAdjusting.setBounds(10, 11, 73, 14);
			contentPanel.add(lblItemAdjusting);
			
			desc_label = new JLabel("SPEC - Spirit spectral shield");
			desc_label.setFont(new Font("Tahoma", Font.BOLD, 11));
			desc_label.setBounds(93, 11, 170, 14);
			contentPanel.add(desc_label);
			
			JLabel lblPrice = new JLabel("Price:");
			lblPrice.setBounds(10, 36, 27, 14);
			contentPanel.add(lblPrice);
			
			adjust_dialog_idx_label = new JLabel("");
			adjust_dialog_idx_label.setVisible(false);
			adjust_dialog_idx_label.setBounds(0, 0, 0, 0);
			contentPanel.add(adjust_dialog_idx_label);
			
			price_field = new JTextField();
			price_field.setBounds(47, 36, 86, 20);
			contentPanel.add(price_field);
			price_field.setColumns(10);
			
			JLabel lblMargin = new JLabel("Margin:");
			lblMargin.setBounds(10, 61, 46, 14);
			contentPanel.add(lblMargin);
			
			JLabel lblBuy = new JLabel("Buy:");
			lblBuy.setBounds(20, 86, 27, 14);
			contentPanel.add(lblBuy);
			
			buy_field = new JTextField();
			buy_field.setBounds(47, 83, 86, 20);
			contentPanel.add(buy_field);
			buy_field.setColumns(10);
			
			JLabel lblSell = new JLabel("Sell:");
			lblSell.setBounds(143, 86, 27, 14);
			contentPanel.add(lblSell);
			
			sell_field = new JTextField();
			sell_field.setBounds(170, 83, 86, 20);
			contentPanel.add(sell_field);
			sell_field.setColumns(10);
			{
				JPanel buttonPane = new JPanel();
				buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
				getContentPane().add(buttonPane, BorderLayout.SOUTH);
				{
					JButton okButton = new JButton("Save");
					okButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent arg0) {
							int idx = Integer.parseInt(adjust_dialog_idx_label.getText());
							int newPrice = Integer.parseInt(price_field.getText());
							
							int buyMargin = Integer.parseInt(buy_field.getText());
							int sellMargin = Integer.parseInt(sell_field.getText());
							marginArray[idx][0] = buyMargin;
							marginArray[idx][1] = sellMargin;
							itemMarginArray.get(idx).setText(buyMargin + " - " + sellMargin);
							
							if (itemStatusArray.get(idx).getText().equals("BUYING")) {
								buySellArray[idx][0] = newPrice;
								buySellArray[idx][1] = sellMargin;
							}
							else {
								buySellArray[idx][1] = newPrice;
							}
							itemPriceArray.get(idx).setText("$" + newPrice + "K");
							timerArray.get(idx).resetTimer();

							tradingItemsButtonGroup.clearSelection();
							aDialog.setVisible(false);
						}
					});
					okButton.setFocusable(false);
					okButton.setActionCommand("OK");
					buttonPane.add(okButton);
					getRootPane().setDefaultButton(okButton);
				}
				{
					JButton cancelButton = new JButton("Cancel");
					cancelButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent arg0) {
							tradingItemsButtonGroup.clearSelection();
							aDialog.setVisible(false);
						}
					});
					cancelButton.setFocusable(false);
					cancelButton.setActionCommand("Cancel");
					buttonPane.add(cancelButton);
				}
			}
		}
	}
}
