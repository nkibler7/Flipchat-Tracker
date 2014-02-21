import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.border.TitledBorder;
import javax.swing.UIManager;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.ButtonGroup;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

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
 * This is the main class, or the "controller", if you will. It handles the
 * instantiation and display of all other subframes, subpanels, etc. 
 * 
 * @author i96
 */
public class MainFrame extends JFrame {
	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	//TODO: Update before pushing changes to new release number
	private String version = "1.1.0";

	private JPanel contentPane;
	private JLabel lblNoItemsIn;
	private NewItemFrame iFrame;
	private AdjustPriceMarginsDialog aDialog;
	private ButtonGroup tradingItemsButtonGroup = new ButtonGroup();
	private ArrayList<ItemPanel> itemPanels;
	private ItemCollectionManager itemCollection;
	private BuyRestrictionsPanel buyRestrictionsPanel;
	private ProfitsPanel profitsPanel;
	private JPanel itemsPanel;

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
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the MainFrame.
	 */
	public MainFrame() {
		itemCollection = new ItemCollectionManager();
		iFrame = new NewItemFrame(this);
		aDialog = new AdjustPriceMarginsDialog(this);
		
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
		
		JMenuItem mntmArmadylLine = new JMenuItem("Armadyl Line");
		mntmArmadylLine.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				iFrame.makeVisible(itemCollection.getItemListByLine(ItemLine.ARMADYL));
			}
		});
		mnAddItem.add(mntmArmadylLine);
		
		JMenuItem mntmBandosLine = new JMenuItem("Bandos Line");
		mntmBandosLine.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				iFrame.makeVisible(itemCollection.getItemListByLine(ItemLine.BANDOS));
			}
		});
		mnAddItem.add(mntmBandosLine);
		
		JMenuItem mntmShieldLine = new JMenuItem("Shield Line");
		mntmShieldLine.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				iFrame.makeVisible(itemCollection.getItemListByLine(ItemLine.SHIELD));
			}
		});
		mnAddItem.add(mntmShieldLine);
		
		JMenuItem mntmSubjugationLine = new JMenuItem("Subjugation Line");
		mntmSubjugationLine.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				iFrame.makeVisible(itemCollection.getItemListByLine(ItemLine.SUBJUGATION));
			}
		});
		mnAddItem.add(mntmSubjugationLine);
		
		JSeparator separator = new JSeparator();
		mnAddItem.add(separator);
		
		JMenuItem mntmMiscLine = new JMenuItem("Misc. Line");
		mntmMiscLine.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				iFrame.makeVisible(itemCollection.getItemListByLine(ItemLine.MISCELLANEOUS));
			}
		});
		mnAddItem.add(mntmMiscLine);
		
		JMenu mnEdit = new JMenu("Edit");
		menuBar.add(mnEdit);
		
		JMenuItem mntmBoughtsold = new JMenuItem("Bought/Sold");
		mntmBoughtsold.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ItemPanel ip = findSelectedItemPanel();
				Item item = ip.getItem();
				if (ip != null) {
					ItemStatus status = ip.getStatus();
					switch(status) {
						case BUYING:
							ip.markAsSelling(itemCollection);
							buyRestrictionsPanel.addItemToBuyRestrictionList(item);
							break;
						case SELLING:
							ip.markAsSold();
							if (ip.wasBought()) {
								profitsPanel.addProfit(ip.getItem(), ip.getProfit());
							}
							removeItemPanel(ip);
							break;
						default:
							break;
					}
				}
				clearSelection();
			}
		});
		mnEdit.add(mntmBoughtsold);
		
		// Let's apply a 25 minute rule to the item
		JMenuItem mntmApplyRule = new JMenuItem("Apply rule");
		mntmApplyRule.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ItemPanel ip = findSelectedItemPanel();
				if (ip != null) {
					ip.applyRule();
				}
				clearSelection();
			}
		});
		mnEdit.add(mntmApplyRule);
		
		// Prepare the adjust price/margins window for the selected item
		JMenuItem mntmAdjustPricemargins = new JMenuItem("Adjust price/margins");
		mntmAdjustPricemargins.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ItemPanel ip = findSelectedItemPanel();
				if (ip != null) {
					aDialog.makeVisible(ip);
				}
			}
		});
		mnEdit.add(mntmAdjustPricemargins);
		
		JSeparator separator_1 = new JSeparator();
		mnEdit.add(separator_1);
		
		// Let's remove the item, but keep the buy restriction
		JMenuItem mntmDeleteItem = new JMenuItem("Delete item");
		mntmDeleteItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ItemPanel ip = findSelectedItemPanel();
				if (ip != null) {
					removeItemPanel(ip);
				}
			}
		});
		mnEdit.add(mntmDeleteItem);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(10, 10, 660, 420);
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
		
		itemsPanel = new JPanel();
		itemsPanel.setToolTipText("");
		itemsPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Items I'm Trading", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		splitPane.setLeftComponent(itemsPanel);
		itemsPanel.setLayout(null);
		
		lblNoItemsIn = new JLabel("No items in trading! Add a new item to get started.");
		lblNoItemsIn.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblNoItemsIn.setBounds(166, 29, 308, 17);
		itemsPanel.add(lblNoItemsIn);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(null, "Items On Buy Restriction", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		splitPane.setRightComponent(panel_1);
		panel_1.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setBorder(null);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		panel_1.add(scrollPane, BorderLayout.CENTER);
		
		buyRestrictionsPanel = new BuyRestrictionsPanel();
		scrollPane.setViewportView(buyRestrictionsPanel);
		
		profitsPanel = new ProfitsPanel();
		tabbedPane.addTab("Profit / Loss", null, profitsPanel, null);
		getContentPane().setLayout(null);
		
		itemPanels = new ArrayList<ItemPanel>();
	}
	
	/**
	 * Adds the specified ItemPanel to the list and display this panel.
	 * @param ip - the ItemPanel that should be added to the list and displayed
	 */
	public void addItemPanel(ItemPanel ip) {
		if (itemPanels.size() < 6) {
			if (lblNoItemsIn.isVisible()) {
				lblNoItemsIn.setVisible(false);
			}
			
			tradingItemsButtonGroup.add(ip.getSelectRadioButton());
			itemPanels.add(ip);
			this.redrawItemsPanel();
			
			if (!profitsPanel.isTimerRunning()) {
				profitsPanel.startTimer();
			}
		}
		iFrame.setVisible(false);
	}
	
	/**
	 * Clears the selection from the select button group.
	 */
	public void clearSelection() {
		tradingItemsButtonGroup.clearSelection();
	}
	
	/**
	 * Removes an ItemPanel from the display.
	 * @param ip - ItemPanel object to remove
	 */
	private void removeItemPanel(ItemPanel ip) {
		itemPanels.remove(ip);
		tradingItemsButtonGroup.remove(ip.getSelectRadioButton());
		this.redrawItemsPanel();
	}
	
	/**
	 * Finds which ItemPanel object is selected.
	 * @return the ItemPanel object that is selected, or null if none
	 */
	private ItemPanel findSelectedItemPanel() {
		for (ItemPanel ip: itemPanels) {
			if (ip.isSelected()) {
				return ip;
			}
		}
		return null;
	}
	
	/**
	 * Redraws the items panel by removing all panels, adding them back from
	 * the saved list, and repainting itself.
	 */
	private void redrawItemsPanel() {
		itemsPanel.removeAll();
		int count = 0;
		for (ItemPanel ip: itemPanels) {
			int x = (count++ * 105) + 10;
			int y = 21;
			ip.setLocation(x, y);
			itemsPanel.add(ip);
		}
		itemsPanel.revalidate();
		itemsPanel.repaint();
	}
}
