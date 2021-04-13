package auctionsniper.ui;

import javax.swing.*;

public class Main {
    public static final String JOIN_COMMAND_FORMAT = "Join Command Format"; // TODO
    public static final String SNIPER_STATUS_NAME = "sniper status"; // TODO
    public static final String STATUS_JOINING = "Joining"; // TODO
    public static final String STATUS_LOST = "Lost"; // TODO
    private MainWindow ui;

    public Main() throws Exception {
        startUserInterface();
    }

    public static void main(String... args) throws Exception {
        Main main = new Main();
    }

    private void startUserInterface() throws Exception {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                ui = new MainWindow();
            }
        });
    }
}
