package messagelogix.com.smartbuttoncommunications.model;

/**
 * Created by Richard on 8/23/2018.
 */
public class SpinnerItem_Building {

    private String building_name;

    private String building_id;

    public SpinnerItem_Building(String name, String id) {

        building_name = name;
        building_id = id;
    }

    public String getBuilding_name() {

        return building_name;
    }

    public String getBuilding_id() {

        return building_id;
    }

    @Override
    public String toString(){
        return this.getBuilding_name();
    }
}
