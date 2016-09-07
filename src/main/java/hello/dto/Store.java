package hello.dto;

/**
 * Created by otves on 07.09.2016.
 */
public class Store {
    private String id;
    private String descr;

    public Store(String id, String descr) {
        this.setId(id);
        this.setDescr(descr);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }
}
