package test.endtoend.auctionsniper;

import org.junit.After;
import org.junit.Test;

public class AuctionSniperEndToEndTest {
    /* End-to-end test entry point. */

    /* the very first thing we do is start an XMPP server as a fake Auction Server */
    private final FakeAuctionServer auction =
            new FakeAuctionServer("item-54321");
    private final ApplicationRunner application = new ApplicationRunner();

    @Test
    public void sniperJoinsAuctionUntilAuctionCloses() throws Exception {
        auction.startSellingItem(); // Step 1
        application.startBiddingIn(auction); // Step 2
        auction.hasReceivedJoinRequestFrom("sniper@localhost/Auction"); // Step 3

        auction.announceClosed(); // Step 4
        application.showsSniperHasLostAuction(); // Step 5
    }

    @After
    public void stopAuction() {
        auction.stop();
    }

    @After
    public void stopApplication() {
        application.stop();
    }
}