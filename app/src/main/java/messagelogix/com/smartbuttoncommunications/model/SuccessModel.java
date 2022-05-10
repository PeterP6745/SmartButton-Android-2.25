package messagelogix.com.smartbuttoncommunications.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Create by Vahid
 * This is a model for success message that comes from server
 */
public class SuccessModel {

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
