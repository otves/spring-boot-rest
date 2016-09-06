package hello;

/**
 * Created by otves on 06.09.2016.
 */
public class Data {

    private String Id = "e10";
    private String ResourceId = "r1";
    private String Title = "Deal negotiation";
    private String StartDate = "2017-06-04";
    private String EndDate = "2017-06-08";

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getResourceId() {
        return ResourceId;
    }

    public void setResourceId(String resourceId) {
        ResourceId = resourceId;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getStartDate() {
        return StartDate;
    }

    public void setStartDate(String startDate) {
        StartDate = startDate;
    }

    public String getEndDate() {
        return EndDate;
    }

    public void setEndDate(String endDate) {
        EndDate = endDate;
    }
}
