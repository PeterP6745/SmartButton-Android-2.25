package messagelogix.com.smartbuttoncommunications.model;

/**
 * Created by Vahid
 * This is the model class for Locator message
 */
public class LocatorMessage {

    private Data[] data;

    private boolean success;

    public Data[] getData() {

        return data;
    }

    public void setData(Data[] data) {

        this.data = data;
    }

    public boolean getSuccess() {

        return success;
    }

    public void setSuccess(boolean success) {

        this.success = success;
    }

    @Override
    public String toString() {

        return "ClassPojo [data = " + data + ", success = " + success + "]";
    }

    public class Data {

        private String message;

        private String id;

        private String special_case;

        private String special_case_value;

        public String getMessage() {

            return message;
        }

        public String getSpecialCaseValue(){
            return this.special_case;
        }

        public String getSCVParam() { return this.special_case_value; }

        public String getId() {

            return id;
        }

        public void setId(String id) {

            this.id = id;
        }

        public void setMessage(String message) {

            this.message = message;
        }

        public void setSpecialCaseValue(String specialCaseValue){
            this.special_case = specialCaseValue;
        }

        @Override
        public String toString() {

            return "ClassPojo [message = " + message + ", id = " + id + "SpecialCaseValue: "+ special_case +"] ";
        }
    }
}
