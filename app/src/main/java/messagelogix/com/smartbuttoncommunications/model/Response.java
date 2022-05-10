package messagelogix.com.smartbuttoncommunications.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Vahid
 * This is the model for a response
 */
public class Response {

    @SerializedName("success")
    @Expose
    private Boolean success;

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


