package messagelogix.com.smartbuttoncommunications.model;

public class DecModel {
    public String name;
    public String title;
    public String dep;

    public String email;
    public String phone;


    public DecModel(String name, String title, String dep) {
        this.name = name;
        this.title = title;
        this.dep = dep;

    }


    public DecModel(String name, String title, String dep, String email, String phone) {
        this.name = name;
        this.title = title;
        this.dep = dep;

        this.email = email;
        this.phone = phone;

    }
}



