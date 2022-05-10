package messagelogix.com.smartbuttoncommunications.model;

/**
 * Created by Vahid
 * This is a simple item with id and value
 */
public class Item {

    private String _id;

    private String _value;

    public Item(String id, String values) {

        _id = id;
        _value = values;
    }

    public String getId() {

        return this._id;
    }

    public String getValue() {

        return this._value;
    }
}
