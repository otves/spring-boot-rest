package hello.dto;

/**
 * @author Sergey Grishchenko on 07.09.16.
 */
public class Employee {
    private String id = "0";
    private String name = "First";
    private String value = "1";

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

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
