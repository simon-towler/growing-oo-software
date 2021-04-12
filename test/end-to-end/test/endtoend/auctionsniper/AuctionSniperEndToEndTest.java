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
        // 1. When an auction is selling an item,
        auction.startSellingItem(); // Step 1
        // 2. And an Auction Sniper has started to bid in that auction,
        application.startBiddingIn(auction); // Step 2
        // 3. Than the auction will receive a Join request from the Auction Sniper.
        auction.hasReceivedJoinRequestFrom("sniper@localhost/Auction"); // Step 3
        // 4. When an auction announces that it is Closed,
        auction.announceClosed(); // Step 4
        // 5. Then the Auction Sniper will show that it lost the auction.
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