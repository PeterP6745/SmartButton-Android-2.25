package messagelogix.com.smartbuttoncommunications.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Richard on 2/26/2016.
 */
public class ContactDefault {

    @SerializedName("data")
    @Expose
    private List<Contact> data = new ArrayList<Contact>();

    @SerializedName("success")
    @Expose
    private Boolean success;

    /**
     * @return The data
     */
    public List<Contact> getData() {

        return data;
    }

    /**
     * @param data The data
     */
    public void setData(List<Contact> data) {

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


