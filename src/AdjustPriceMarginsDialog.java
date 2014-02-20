import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

/**
 * This class serves as a custom implementation of the JDialog class, which is similar
 * to the JFrame class but allows the ability to be a modal window. This will prevent the
 * user from going back to the main window before dealing with this window. This window,
 * in particular, is used to modify the existing price and margins that were set.
 * 
 * @author i96
 * 
 */
public class AdjustPriceMarginsDialog extends JDialog {
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;
	
	private final JPanel contentPanel = new JPanel();
	private JTextField priceField, buyField, sellField;
	private JLabel descLabel;
	private ItemPanel ip;

	/**
	 * Create the dialog.
	 */
	public AdjustPriceMarginsDialog(final MainFrame parent) {
		setTitle("Adjust Price/Margins");
		setBounds(100, 100, 297, 189);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);

		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height
				/ 2 - this.getSize().height / 2);

		JLabel lblItemAdjusting = new JLabel("Item adjusting:");
		lblItemAdjusting.setBounds(10, 11, 73, 14);
		contentPanel.add(lblItemAdjusting);

		descLabel = new JLabel("SPEC - Spirit spectral shield");
		descLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
		descLabel.setBounds(93, 11, 170, 14);
		contentPanel.add(descLabel);

		JLabel lblPrice = new JLabel("Price:");
		lblPrice.setBounds(10, 36, 27, 14);
		contentPanel.add(lblPrice);

		priceField = new JTextField();
		priceField.setBounds(47, 36, 86, 20);
		contentPanel.add(priceField);
		priceField.setColumns(10);

		JLabel lblMargin = new JLabel("Margin:");
		lblMargin.setBounds(10, 61, 46, 14);
		contentPanel.add(lblMargin);

		JLabel lblBuy = new JLabel("Buy:");
		lblBuy.setBounds(20, 86, 27, 14);
		contentPanel.add(lblBuy);

		buyField = new JTextField();
		buyField.setBounds(47, 83, 86, 20);
		contentPanel.add(buyField);
		buyField.setColumns(10);

		JLabel lblSell = new JLabel("Sell:");
		lblSell.setBounds(143, 86, 27, 14);
		contentPanel.add(lblSell);

		sellField = new JTextField();
		sellField.setBounds(170, 83, 86, 20);
		contentPanel.add(sellField);
		sellField.setColumns(10);

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);

		final AdjustPriceMarginsDialog t = this;
		JButton okButton = new JButton("Save");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int marginBuy = Integer.parseInt(buyField.getText());
				int marginSell = Integer.parseInt(sellField.getText());
				int newPrice = Integer.parseInt(priceField.getText());

				ip.setMarginBuy(marginBuy);
				ip.setMarginSell(marginSell);
				ip.setPrice(newPrice);
				ip.checkStretch();

				ip.getRuleTimer().resetTimer();
				parent.clearSelection();
				t.setVisible(false);
			}
		});
		okButton.setFocusable(false);
		okButton.setActionCommand("OK");
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);
		{
			JButton cancelButton = new JButton("Cancel");
			cancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					parent.clearSelection();
					t.setVisible(false);
				}
			});
			cancelButton.setFocusable(false);
			cancelButton.setActionCommand("Cancel");
			buttonPane.add(cancelButton);
		}
	}

	/**
	 * Makes this AdjustPriceMarginsDialog visible and binds it to the provided
	 * ItemPanel.
	 * 
	 * @param ip - the ItemPanel to edit the price and margins for
	 */
	public void makeVisible(ItemPanel ip) {
		this.ip = ip;
		Item item = ip.getItem();
		ItemStatus status = ip.getStatus();
		String desc = item.getAbbr() + " - " + item.getName();
		
		descLabel.setText(desc);
		if (status == ItemStatus.BUYING) {
			priceField.setText("" + ip.getPriceBuy());
		}
		else {
			priceField.setText("" + ip.getPriceSell());
		}
		buyField.setText("" + ip.getMarginBuy());
		sellField.setText("" + ip.getMarginSell());
		
		this.setModal(true);
		this.setVisible(true);
	}
}