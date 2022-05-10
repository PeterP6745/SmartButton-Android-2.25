package messagelogix.com.smartbuttoncommunications.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Vahid
 * This is the model class for chat message
 */
public class MessageChat {

    public MessageChat(String content, boolean isMine) {

        this.message = content;
        this.isMine = isMine;
    }

    boolean isMine;

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("timestamp")
    @Expose
    private String timestamp;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("attachment")
    @Expose
    private Object attachment;

    @SerializedName("to_id")
    @Expose
    private String toId;

    @SerializedName("from_id")
    @Expose
    private String fromId;

    @SerializedName("fname")
    @Expose
    private String fname;

    /**
     * @return The id
     */
    public String getId() {

        return id;
    }

    /**
     * @param id The id
     */
    public void setId(String id) {

        this.id = id;
    }

    /**
     * @return The timestamp
     */
    public String getTimestamp() {

        return timestamp;
    }

    /**
     * @param timestamp The timestamp
     */
    public void setTimestamp(String timestamp) {

        this.timestamp = timestamp;
    }

    /**
     * @return The message
     */
    public String getMessage() {

        return message;
    }

    /**
     * @param message The message
     */
    public void setMessage(String message) {

        this.message = message;
    }

    /**
     * @return The attachment
     */
    public Object getAttachment() {

        return attachment;
    }

    /**
     * @param attachment The attachment
     */
    public void setAttachment(Object attachment) {

        this.attachment = attachment;
    }

    /**
     * @return The toId
     */
    public String getToId() {

        return toId;
    }

    /**
     * @param toId The to_id
     */
    public void setToId(String toId) {

        this.toId = toId;
    }

    /**
     * @return The fromId
     */
    public String getFromId() {

        return fromId;
    }

    /**
     * @param fromId The from_id
     */
    public void setFromId(String fromId) {

        this.fromId = fromId;
    }

    /**
     * @return The fname
     */
    public String getFname() {

        return fname;
    }

    /**
     * @param fname The fname
     */
    public void setFname(String fname) {

        this.fname = fname;
    }
}
