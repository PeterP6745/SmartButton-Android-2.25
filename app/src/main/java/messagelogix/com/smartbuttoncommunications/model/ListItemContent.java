package messagelogix.com.smartbuttoncommunications.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Create by Vahid
 * This is the model class for our list
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class  ListItemContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<ConversationItem> ITEMS = new ArrayList<ConversationItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, ConversationItem> ITEM_MAP = new HashMap<String, ConversationItem>();
//    private static final int COUNT = 25;

    static {
        // Add some sample items.
//        for (int i = 1; i <= COUNT; i++) {
//            addItem(createDummyItem(i));
//        }
    }

    public static void addItem(ConversationItem item) {

        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static ConversationItem createDummyItem(int position) {

        return new ConversationItem(String.valueOf(position), "Item " + position, makeDetails(position));
    }

    private static String makeDetails(int position) {

        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class ConversationItem {

        public final String id;

        public final String timestamp;

        public final String message_id;

        public final String fname;

        public final String message;

      //  public final String group_total;
        public final String newMessage;

        public final String receiver_id;

        public final String receiverName;

        public final String lastUserInChat;

        public ConversationItem(String id, String content, String details) {

            this.id = id;
            this.timestamp = "";
            this.message_id = "";
            this.fname = "";
            this.message = "";
         //   this.group_total = "";
            this.newMessage = "";
            this.receiver_id = "";
            this.receiverName = "";
            this.lastUserInChat = "";
        }

        public ConversationItem(String id, String timestamp, String message_id, String fname, String message, String newMessage, String receiver_id, String receiverName, String lastUserInChat) {

            this.id = id;
            this.timestamp = timestamp;
            this.message_id = message_id;
            this.fname = fname;
            this.message = message;
        //    this.group_total = group_total;
            this.newMessage = newMessage;
            this.receiver_id = receiver_id;
            this.receiverName = receiverName;
            this.lastUserInChat = lastUserInChat;
        }
//        @Override
//        public String toString() {
//
//            return content;
//        }

       // public String getTitle(){return this.message;}
    }


}
