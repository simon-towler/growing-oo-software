package test.endtoend.auctionsniper;

import auctionsniper.ui.Main;
import auctionsniper.ui.MainWindow;
import com.objogate.wl.swing.AWTEventQueueProber;
import com.objogate.wl.swing.driver.JFrameDriver;
import com.objogate.wl.swing.driver.JLabelDriver;
import com.objogate.wl.swing.gesture.GesturePerformer;

/**
 * The AuctionSniperDriver is simply an extension of a WindowLicker JFrameDriver
 * specialized for our tests.
 *
 * On construction, it attempts to find a visible top-level window for the Auction
 * Sniper within the given timeout. The method showsSniperStatus() looks for the relevant
 * label in the user interface and confirms that it shows the given status. If the driver
 * cannot find a feature it expects, it will throw an exception and fail the test.
 */
public class AuctionSniperDriver extends JFrameDriver {
    public AuctionSniperDriver(int timeoutMillis) {
        super(new GesturePerformer(),
            JFrameDriver.topLevelFrame(
                    named(MainWindow.MAIN_WINDOW_NAME),
                    showingOnScreen()),
                    new AWTEventQueueProber(timeoutMillis, 100));
    }

    public void showsSniperStatus(String statusText) {
        new JLabelDriver(this, named(Main.SNIPER_STATUS_NAME)).hasText(org.hamcrest.core.IsEqual.equalTo(statusText));
    }
}
