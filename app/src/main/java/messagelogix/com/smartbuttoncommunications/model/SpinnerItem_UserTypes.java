package messagelogix.com.smartbuttoncommunications.model;

/**
 * Created by Richard on 8/23/2018.
 */
public class SpinnerItem_UserTypes {

    private String usrTypeName;
    private String usrTypeId;

    public SpinnerItem_UserTypes(String name, String id){

        usrTypeName = name;
        usrTypeId = id;

    }

    public String getUsrTypeId() {

        return usrTypeId;
    }

    public String getUsrTypeName() {

        return usrTypeName;
    }

    @Override
    public String toString() {

        return this.getUsrTypeName();
    }
}
