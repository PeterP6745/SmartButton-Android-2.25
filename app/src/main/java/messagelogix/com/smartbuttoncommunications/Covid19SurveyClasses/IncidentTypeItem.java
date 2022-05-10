package messagelogix.com.smartbuttoncommunications.Covid19SurveyClasses;

public class IncidentTypeItem {
    private String id;

    private String incidentMessage;

    private String specialCaseValue;

    private String scvParam;

    public IncidentTypeItem (String id, String incidentMessage, String specialCaseValue, String scvParam){
        this.id = id;
        this.incidentMessage = incidentMessage;
        this.specialCaseValue = specialCaseValue;
        this.scvParam = scvParam;
    }

    public String getId(){
        return this.id;
    }

    public String getIncidentMessage(){
        return this.incidentMessage;
    }

    public String getSpecialCaseValue(){
        return this.specialCaseValue;
    }

    public String getSCVParam() { return this.scvParam; }

}
