import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;

/**
 * This class controls the entirety of the "Profit/Loss" tab in the main program.
 * It is designed to keep track of all the individual profits, as well as the 
 * profit summaries at the top of the window.
 * 
 * @author i96
 * 
 */
public class ProfitsPanel extends JPanel {
	private JPanel profitPanel;
	private JLabel totalTimeLabel, totalProfitLabel, profitHourLabel;
	private ArrayList<Profit> list;
	private Timer totalTimer;
	private int totalSeconds, totalProfit;

	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new ProfitsPanel object and initializes the timer that counts
	 * the total time the program has been running. This timer does not start
	 * until the method startTimer(), which is when the first item is added.
	 */
	public ProfitsPanel() {
		this.setLayout(null);

		JPanel topPanel = new JPanel();
		topPanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null,
				null, null));
		topPanel.setBounds(10, 11, 634, 46);
		this.add(topPanel);
		topPanel.setLayout(null);

		JLabel lblTotalTimeFlipping = new JLabel("Total time flipping:");
		lblTotalTimeFlipping.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblTotalTimeFlipping.setBounds(10, 11, 128, 24);
		topPanel.add(lblTotalTimeFlipping);

		totalTimeLabel = new JLabel("0:00:00");
		totalTimeLabel.setForeground(new Color(0, 128, 128));
		totalTimeLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
		totalTimeLabel.setBounds(148, 11, 55, 24);
		topPanel.add(totalTimeLabel);

		JLabel lblTotalProfit = new JLabel("Total profit:");
		lblTotalProfit.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblTotalProfit.setBounds(248, 11, 82, 24);
		topPanel.add(lblTotalProfit);

		totalProfitLabel = new JLabel("$0K");
		totalProfitLabel.setForeground(new Color(0, 128, 0));
		totalProfitLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
		totalProfitLabel.setBounds(340, 11, 90, 24);
		topPanel.add(totalProfitLabel);

		JLabel lblProfithr = new JLabel("Profit/hr:");
		lblProfithr.setFont(new Font("Tahoma", Font.BOLD, 14));
		lblProfithr.setBounds(458, 11, 66, 24);
		topPanel.add(lblProfithr);

		profitHourLabel = new JLabel("$0K");
		profitHourLabel.setForeground(new Color(128, 0, 128));
		profitHourLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
		profitHourLabel.setBounds(534, 11, 90, 24);
		topPanel.add(profitHourLabel);

		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null,
				null, null));
		scrollPane_1
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane_1
				.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane_1.setBounds(10, 68, 634, 314);
		this.add(scrollPane_1);
		
		profitPanel = new JPanel();
		profitPanel.setBorder(null);
		scrollPane_1.setViewportView(profitPanel);
		profitPanel.setLayout(null);
		
		list = new ArrayList<Profit>();
		
		totalSeconds = 0;
		totalTimer = new Timer(1000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int currHours = totalSeconds / 3600;
				int currMin = (totalSeconds - (currHours * 3600)) / 60;
				int currSecs = totalSeconds - (currHours * 3600) - (currMin * 60);
				
				totalTimeLabel.setText(Math.abs(currHours) + ":" + String.format("%02d", Math.abs(currMin)) + ":" + String.format("%02d", Math.abs(currSecs)));
				
				int prof_hour = (int) (totalProfit * (3600.0 / (double)totalSeconds));
				profitHourLabel.setText("$" + prof_hour + "K");
				
				totalSeconds++;
			}
		});
	}
	
	/**
	 * Adds a profit summary for the specified item with the specified profit amount
	 * to the ProfitsPanel.
	 * @param item - the Item that this profit was made on
	 * @param profitAmt - the cash amount (in thousands) that was profited (can be negative)
	 */
	public void addProfit(Item item, int profitAmt) {
		totalProfit += profitAmt;
		totalProfitLabel.setText("$" + totalProfit + "K");
		
		int size = list.size();
		int row = size / 3;
		int col = size % 3;
		int x = (col * 204) + 10;
		int y = (row * 46) + 11;
		
		int bottom = y + 57;
		if (profitPanel.getHeight() < bottom) {
			profitPanel.setPreferredSize(new Dimension(10, bottom));
		}
		
		Profit profit = new Profit(item, profitAmt, x, y);
		list.add(profit);
		profitPanel.add(profit);
		
		profitPanel.revalidate();
		profitPanel.repaint();
	}
	
	/**
	 * Starts the Timer that determines how long the program has been running.
	 */
	public void startTimer() {
		totalTimer.start();
	}
	
	/**
	 * Returns whether or not the totalTimer object is running.
	 * @return true if the Timer is running, false otherwise
	 */
	public boolean isTimerRunning() {
		return totalTimer.isRunning();
	}

}
