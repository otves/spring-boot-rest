package hello.dto;

import java.util.*;

/**
 * Created by otves on 07.09.2016.
 */
public class Person {

    private String id;

    private String name;

    private List<Shift> shifts;

    private final Map<String, Shift> shiftHoursMap = new HashMap<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<Shift> getShifts() {
        return shiftHoursMap.values();
    }

    public void incShift(String shift_date, String planId) {
        Shift shift;
        if(!this.shiftHoursMap.containsKey(shift_date)) {
            shift = new Shift();
            this.shiftHoursMap.put(shift_date, shift);
        } else {
            shift =  this.shiftHoursMap.get(shift_date);
        }
        shift.setStart(shift_date);
        shift.setPlanId(planId);
        shift.setStore("");
        shift.incHour();
    }

}
