package hello.dto;

/**
 * Created by otves on 07.09.2016.
 */
public class Shift {
    private String   start;
    private Long   end;
    private Integer hours = 0;
    private String planId;
    //store
    private String store;

    public Long getEnd() {
        return end;
    }

    public void setEnd(Long end) {
        this.end = end;
    }

    public Integer getHours() {
        return hours;
    }

    public void setHours(Integer hours) {
        this.hours = hours;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public void incHour() {
        this.hours++;
    }
}
