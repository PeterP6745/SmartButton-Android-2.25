package messagelogix.com.smartbuttoncommunications.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vahid
 * This is a model for push messages
 */
public class PushModel {

    @Expose
    private List<PushItem> data = new ArrayList<PushItem>();

    @Expose
    private Boolean success;

    /**
     * @return The data
     */
    public List<PushItem> getData() {

        return data;
    }

    /**
     * @return The success
     */
    public Boolean getSuccess() {

        return success;
    }

    public class PushItem {

        @Expose
        private String id;

        @SerializedName("acct_id")
        @Expose
        private String acctId;

        @SerializedName("device_id")
        @Expose
        private String deviceId;

        @SerializedName("device_type")
        @Expose
        private String deviceType;

        @Expose
        private String message;

        @SerializedName("schedule_datetime")
        @Expose
        private String scheduleDatetime;

        @SerializedName("first_viewed")
        @Expose
        private String firstViewed;

        @Expose
        private String subject;

        @SerializedName("campaign_id")
        @Expose
        private String campaignId;

        @SerializedName("message_viewed")
        @Expose
        private String messageViewed;

        /**
         * @return The id
         */
        public String getId() {

            return id;
        }

        /**
         * @return The acctId
         */
        public String getAcctId() {

            return acctId;
        }

        /**
         * @return The deviceId
         */
        public String getDeviceId() {

            return deviceId;
        }

        /**
         * @return The deviceType
         */
        public String getDeviceType() {

            return deviceType;
        }

        /**
         * @return The message
         */
        public String getMessage() {

            return message;
        }

        /**
         * @return The scheduleDatetime
         */
        public String getScheduleDatetime() {
            return scheduleDatetime;
        }

        public String getFirstViewed() {
            return firstViewed;
        }

        /**
         * @return The subject
         */
        public String getSubject() {

            return subject;
        }

        public String wasItemSeen() {
            return messageViewed;
        }

        /**
         * @return The campaignId
         */
        public String getCampaignId() {

            return campaignId;
        }

        public void setMessage(String message) {

            this.message = message;
        }

        public void setScheduleDatetime(String scheduleDatetime) {

            this.scheduleDatetime = scheduleDatetime;
        }

        public void setFirstViewed(String firstViewed) {
            this.firstViewed = firstViewed;
        }

        public void setSubject(String subject) {

            this.subject = subject;
        }

        public void setCampaignId(String campaignId) {

            this.campaignId = campaignId;
        }
    }
}
