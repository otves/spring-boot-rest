package hello.dto;

import java.util.List;

/**
 * Created by otves on 07.09.2016.
 */
public class Person {

    private String id;

    private String content;

    private List<Shift> shifts;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<Shift> getShifts() {
        return shifts;
    }

    public void setShifts(List<Shift> shifts) {
        this.shifts = shifts;
    }
}
