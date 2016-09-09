package hello.dto;

/**
 * @author Sergey Grishchenko on 09.09.16.
 */
public class Calculation {

    private String id;

    private String interval;

    private String orgId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }

    public String getOrgId() {
        return orgId;
    }

    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    public Calculation(String id, String interval, String org) {
        this.id = id;
        this.interval = interval;
        this.orgId = org;
    }
}
