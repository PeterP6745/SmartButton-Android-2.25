package messagelogix.com.smartbuttoncommunications.model;

/**
 * Created by Vahid
 * This is a model for a spinner item
 */
public class SpinnerItem {

    private String _id;

    private String _values;

    public SpinnerItem(String id, String value) {

        _id = id;
        _values = value;
    }

    public String getId() {

        return this._id;
    }

    public String getValue() {

        return this._values;
    }
}
