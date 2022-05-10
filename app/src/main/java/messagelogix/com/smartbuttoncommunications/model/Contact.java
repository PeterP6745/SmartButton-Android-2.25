package messagelogix.com.smartbuttoncommunications.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Program on 6/24/2015.
 * this model is for the locator contact manager
 */
public class Contact {

    @SerializedName("contact_name")
    @Expose
    private String _name;

    @SerializedName("contact_cellphone")
    @Expose
    private String _phoneNumber;

    @SerializedName("contact_email")
    @Expose
    private String _email;

    @SerializedName("title")
    @Expose
    private String _title;

    @SerializedName("contact_id")
    @Expose
    private String _contactId;

    private int _id;

    public Contact(int id, String name, String phoneNumber, String email, String title) {

        _id = id;
        _name = name;
        _phoneNumber = phoneNumber;
        _email = email;
        _title = title;
    }

    public Contact(String phoneNumber, String email) {

        _phoneNumber = phoneNumber;
        _email = email;
    }

    public Contact(int id, String name, String phoneNumber, String email) {

        _id = id;
        _name = name;
        _phoneNumber = phoneNumber;
        _email = email;
    }

    public int getId() {

        return this._id;
    }

    public String getName() {

        return this._name;
    }

    public String getPhoneNumber() {

        return this._phoneNumber;
    }

    public String get_contactId() {

        return this._contactId;
    }

    public void set_contactId(String _contactId) {

        this._contactId = _contactId;
    }

    public String getEmail() {

        return this._email;
    }

    public String getTitle() {

        return this._title;
    }

    public void set_name(String _name) {

        this._name = _name;
    }

    public void set_phoneNumber(String _phoneNumber) {

        this._phoneNumber = _phoneNumber;
    }

    public void set_email(String _email) {

        this._email = _email;
    }

    public void set_title(String _title) {

        this._title = _title;
    }

    public void set_id(int _id) {

        this._id = _id;
    }

    @Override
    public String toString() {

        return "_name = " + this._name + " | _phoneNumber = " + this._phoneNumber + " | _email = " + this._email;
    }
}
