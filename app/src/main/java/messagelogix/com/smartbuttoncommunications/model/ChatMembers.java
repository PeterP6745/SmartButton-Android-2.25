package messagelogix.com.smartbuttoncommunications.model;

/**
 * Created by Richard on 11/18/2016.
 */
public class ChatMembers {

    private String fname;
    private String email;


    public String getFname(){
        return fname;
    }

    public  void setFname(String name){

        this.fname = name;
    }

    public String getEmail(){
        return email;
    }

    public void setEmail(String email){
        this.email = email;
    }
}
