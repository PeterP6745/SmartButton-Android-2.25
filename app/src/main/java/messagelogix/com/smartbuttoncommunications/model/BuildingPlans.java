package messagelogix.com.smartbuttoncommunications.model;
//This is building plan model

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class BuildingPlans {

    @Expose
    private List<HelpItem> data = new ArrayList<HelpItem>();

    @Expose
    private Boolean success;

    /**
     * @return The data
     */
    public List<HelpItem> getData() {

        return data;
    }

    /**
     * @return The success
     */
    public Boolean getSuccess() {

        return success;
    }

    public class HelpItem {

        @SerializedName("icon")
        @Expose
        private String icon;

        @SerializedName("title")
        @Expose
        private String title;

        @SerializedName("value")
        @Expose
        private String value;

        @SerializedName("type")
        @Expose
        private String type;

        @SerializedName("subtype")
        @Expose
        private String subtype;

        @SerializedName("subheader")
        @Expose
        private String subheader;

        @SerializedName("color")
        @Expose
        private String color;

        @SerializedName("type_id")
        @Expose
        private String typeId;

        @SerializedName("sub_type_id")
        @Expose
        private String subTypeId;

        @SerializedName("subicon")
        @Expose
        private String subicon;

        @SerializedName("sort_type")
        @Expose
        private String sortType;

        @SerializedName("header_name")
        @Expose
        private String headerName;

        @SerializedName("sch_type_id")
        @Expose
        private String schoolTypeId;

        /**
         * @return The icon
         */
        public String getIcon() {

            return icon;
        }

        /**
         * @param icon The icon
         */
        public void setIcon(String icon) {

            this.icon = icon;
        }

        /**
         * @return The title
         */
        public String getTitle() {

            return title;
        }

        /**
         * @param title The title
         */
        public void setTitle(String title) {

            this.title = title;
        }

        /**
         * @return The value
         */
        public String getValue() {

            return value;
        }

        /**
         * @param value The value
         */
        public void setValue(String value) {

            this.value = value;
        }

        /**
         * @return The type
         */
        public String getType() {

            return type;
        }

        /**
         * @param type The type
         */
        public void setType(String type) {

            this.type = type;
        }

        /**
         * @return The subtype
         */
        public String getSubtype() {

            return subtype;
        }

        /**
         * @param subtype The subtype
         */
        public void setSubtype(String subtype) {

            this.subtype = subtype;
        }

        /**
         * @return The subheader
         */
        public String getSubheader() {

            return subheader;
        }

        /**
         * @param subheader The subheader
         */
        public void setSubheader(String subheader) {

            this.subheader = subheader;
        }

        /**
         * @return The color
         */
        public String getColor() {

            return color;
        }

        /**
         * @param color The color
         */
        public void setColor(String color) {

            this.color = color;
        }

        /**
         * @return The typeId
         */
        public String getTypeId() {

            return typeId;
        }

        /**
         * @param typeId The type_id
         */
        public void setTypeId(String typeId) {

            this.typeId = typeId;
        }

        /**
         * @return The subTypeId
         */
        public String getSubTypeId() {

            return subTypeId;
        }

        /**
         * @param subTypeId The sub_type_id
         */
        public void setSubTypeId(String subTypeId) {

            this.subTypeId = subTypeId;
        }

        /**
         * @return The subicon
         */
        public String getSubicon() {

            return subicon;
        }

        /**
         * @param subicon The subicon
         */
        public void setSubicon(String subicon) {

            this.subicon = subicon;
        }

        /**
         * @return The sortType
         */
        public String getSortType() {

            return sortType;
        }

        /**
         * @param sortType The sort_type
         */
        public void setSortType(String sortType) {

            this.sortType = sortType;
        }

        /**
         * @return The headerName
         */
        public String getHeaderName() {

            return headerName;
        }

        /**
         * @param headerName The header_name
         */
        public void setHeaderName(String headerName) {

            this.headerName = headerName;
        }

        public String getSchoolTypeId() {

            return schoolTypeId;
        }

        public void setSchoolTypeId(String schoolTypeId) {

            this.schoolTypeId = schoolTypeId;
        }
    }
}
