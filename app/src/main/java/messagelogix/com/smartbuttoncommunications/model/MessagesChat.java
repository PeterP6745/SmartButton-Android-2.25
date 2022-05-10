package messagelogix.com.smartbuttoncommunications.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vahid
 * This is a model for chat message
 */
public class MessagesChat {

    @SerializedName("data")
    @Expose
    private List<MessageChat> data = new ArrayList<MessageChat>();

    @SerializedName("success")
    @Expose
    private Boolean success;

    /**
     * @return The data
     */
    public List<MessageChat> getData() {

        return data;
    }

    /**
     * @param data The data
     */
    public void setData(List<MessageChat> data) {

        this.data = data;
    }

    /**
     * @return The success
     */
    public Boolean getSuccess() {

        return success;
    }

    /**
     * @param success The success
     */
    public void setSuccess(Boolean success) {

        this.success = success;
    }
}
