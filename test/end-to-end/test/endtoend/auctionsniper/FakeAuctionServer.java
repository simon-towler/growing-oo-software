package test.endtoend.auctionsniper;

import auctionsniper.ui.Main;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.jivesoftware.smack.*;
import org.jivesoftware.smack.packet.Message;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

// Start of "So far, we have"

/*
 * this fake is a _minimal_ implementation just to support testing.
 */

/**
 * A FakeAuctionServer is a substitute server that allows the test to check how the
 * Auction Sniper interacts with an auction using XMPP messages.
 *
 * It has three responsibilities: it must connect to the XMPP broker and accept a request to join the
 * chat from the Sniper; it must receive chat messages from the Sniper or fail if no
 * message arrives within some timeout; and, it must allow the test to send messages back
 * to the Sniper.
 */
public class FakeAuctionServer {
  public static final String ITEM_ID_AS_LOGIN = "auction-%s";
  public static final String AUCTION_RESOURCE = "Auction";
  public static final String XMPP_HOSTNAME = "localhost";
  private static final String AUCTION_PASSWORD = "auction";

  private final String itemId;
  private final XMPPConnection connection;
  private Chat currentChat;

  public FakeAuctionServer(String itemId) {
    this.itemId = itemId;
    // opens a connection to the locally running instance of Openfire
    this.connection = new XMPPConnection(XMPP_HOSTNAME);
  }

  public void startSellingItem() throws XMPPException {
    connection.connect();
    connection.login(String.format(ITEM_ID_AS_LOGIN, itemId), AUCTION_PASSWORD,
        AUCTION_RESOURCE);
    connection.getChatManager().addChatListener(new ChatManagerListener() {
      public void chatCreated(Chat chat, boolean createdLocally) {
        currentChat = chat;
        chat.addMessageListener((MessageListener) messageListener);
      }
    });
  }

  public String getItemId() {
    return itemId;
  }

  // End of "So far, we have"

  // Start of "A Minimal Fake Implementation"

  private final SingleMessageListener messageListener = new SingleMessageListener();

  public void sendInvalidMessageContaining(String brokenMessage) throws XMPPException {
    currentChat.sendMessage(brokenMessage);
  } 

  public void reportPrice(int price, int increment, String bidder) throws XMPPException {
  currentChat.sendMessage( 
      String.format("SOLVersion: 1.1; Event: PRICE; " 
                    + "CurrentPrice: %d; Increment: %d; Bidder: %s;", 
                    price, increment, bidder)); 
  }

  /*
  1. The test needs to know when a Join message has arrived. We just check whether _any_ message has arrived, since the
  Sniper will only be sending Join messages to start with; we'll fill in more detail as we grow the application. This
  implementation will fail if no message is received within 5 seconds.
   */
  public void hasReceivedJoinRequestFromSniper() throws InterruptedException {
    messageListener.receivesAMessage(); // 1
  }

  public void hasReceivedJoinRequestFrom(String sniperId) throws InterruptedException {
    receivesAMessageMatching(sniperId, equalTo(Main.JOIN_COMMAND_FORMAT));
  }
  
  private void receivesAMessageMatching(String sniperId, Matcher<? super String> messageMatcher) throws InterruptedException {
    messageListener.receivesAMessage(messageMatcher); 
    assertThat(currentChat.getParticipant(), equalTo(sniperId)); 
  } 

  /*
  2. The test needs to be able to simulate the auction announcing when it closes, which is why we held onto the
  currentChat when it opened. As with the Join request, the fake auction just sends an empty message, since this is the
  only event we support so far.
   */
  public void announceClosed() throws XMPPException { 
    currentChat.sendMessage(new Message()); // 2
  }

  /**
   * Closes the connection
   */
  public void stop() { 
    connection.disconnect(); // 3
  } 



  public class SingleMessageListener implements MessageListener { 
    private final ArrayBlockingQueue<Message> messages =
                                new ArrayBlockingQueue<Message>(1); 
    public void processMessage(Chat chat, Message message) { 
      messages.add(message); 
    }

    public void receivesAMessage() throws InterruptedException { 
      assertThat("Message",
              messages.poll(5, TimeUnit.SECONDS),
              // uses Hamcrest matcher syntax
              Matchers.is(notNullValue()));  // 4
    } 

    public void receivesAMessage(Matcher<? super String> messageMatcher) 
      throws InterruptedException 
    { 
      final Message message = messages.poll(5, TimeUnit.SECONDS); 
      assertThat(message, hasProperty("body", messageMatcher));
    } 
  }

}
