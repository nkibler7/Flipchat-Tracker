import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.Timer;

/**
 * This class is a custom implementation of the Swing Timer class. It
 * handles the timer for the rules inside the ItemPanels. The global
 * variables set are for stylistic purposes, except the max setting
 * is for how many minutes the rules should count-down from (25 minutes,
 * in seconds).
 * 
 * @author i96
 * 
 */
@SuppressWarnings("serial")
public class RuleTimer extends Timer {
	private final static int max = 1500, yellow = 300, orange = 120;
	
	/**
	 * Creates a new RuleTimer object and binds the given label to it.
	 * @param label - the JLabel object to update with the new time every
	 * second
	 */
	public RuleTimer(final JLabel label) {
		super(1000, new TimerActionListener(label));
	}

	/**
	 * Resets the timer by resetting the second count to 1.
	 */
	public void resetTimer() {
		((TimerActionListener) this.getActionListeners()[0]).setCount(1);
		this.restart();
	}

	/**
	 * This class is a custom implementation of the ActionListener class. It allows
	 * us to define our own actionPerformed() method, which is fired after
	 * every second passes.
	 * 
	 * @author i96
	 *
	 */
	private static class TimerActionListener implements ActionListener {
		private int count = 1;
		private JLabel tLabel;

		/**
		 * Creates a new TimerActionListener bound to the given label.
		 * @param label - the JLabel object to update with the new time every
		 * second
		 */
		public TimerActionListener(JLabel label) {
			super();
			tLabel = label;
		}

		/**
		 * Sets the count to the given newCount.
		 * @param newCount - the new second count to set the timer to
		 */
		public void setCount(int newCount) {
			count = newCount;
		}

		/**
		 * Method is fired by the Timer every time the delay (1000 ms) passes.
		 * This is what updates the time in the JLabel.
		 * @param e - an ActionEvent (not applicable in this case)
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			int curr = max - count++;
			int currMin = curr / 60;
			int currSecs = curr - (currMin * 60);
			String prefix = (curr < 0) ? "- " : "";
			tLabel.setText(prefix + Math.abs(currMin) + ":"
					+ String.format("%02d", Math.abs(currSecs)));
			if (curr < 0) {
				tLabel.setForeground(new Color(128, 0, 0));
			} else if (curr < orange) {
				tLabel.setForeground(new Color(210, 105, 30));
			} else if (curr < yellow) {
				tLabel.setForeground(new Color(218, 165, 32));
			} else {
				tLabel.setForeground(new Color(0, 128, 128));
			}
		}
	}
}
