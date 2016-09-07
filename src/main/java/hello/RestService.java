package hello;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/rest")
public class RestService {

    private static final Logger logger = Logger.getLogger(RestService.class.getName());


    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();



    static ConfigJdbc configJdbc() {
        return new ConfigJdbc(
                org.postgresql.Driver.class,
                "jdbc:postgresql://88.201.248.46:5432/personal",
                "vitaly",
                "m127rqu4");
    }

    @RequestMapping("/greeting")
    public Greeting greeting(@RequestParam(value="name", defaultValue="World") String name) {
        return new Greeting(counter.incrementAndGet(),
                            String.format(template, name));
    }

    @RequestMapping("/employees")
    public List<Employee> employees(@RequestParam(value="name", defaultValue="World") String name) {
        List<Employee> res = new ArrayList<>();
        res.add(new Employee());
        return res;
    }

    @RequestMapping("/shifts")
    public List greeting(@RequestParam(value = "store") Long store,
                         @RequestParam(value = "calculation") Long calculation,
                         @RequestParam(value = "date") Date date) {

        return shiftsPlan(store, calculation, date);

    }

    public static List shiftsPlan(Long store, Long calculation, Date date) {
        List<Map> result  = new ArrayList<>();
        HashMap<String, Map> persons  = new HashMap();
        String sqlQuery = " select persons.id as person_id, persons.name, shifts.id as shift_id, shifts_plan.id as shifts_plan_id, shifts_plan.shift_time, shifts_plan.shift_date from shifts \n" +
                "/*join staff_positions ON  shifts.staff_position_id = staff_positions.id*/\n" +
                "join staff_list ON  shifts.staff_person_id = staff_list.id\n" +
                "join persons ON  staff_list.person_id = persons.id AND persons.active = true\n" +
                "join shifts_plan ON shifts.id = shifts_plan.shift_id and shifts.store_id = shifts_plan.store_id\n" +
                "where shifts_plan.shift_date = ? \n" +
                "AND shifts.store_id = ? \n" +
                "AND shifts.calc_id = ?\n" +
                "LIMIT 100; ";
        try (final Connection db = configJdbc().getConnection()) {
            try (final PreparedStatement sql = db.prepareStatement(sqlQuery)) {
                sql.setDate(1,  new java.sql.Date(date.getTime()));
                sql.setLong(2,  store);
                sql.setLong(3, calculation);
                try (ResultSet resultSet = sql.executeQuery()) {
                    while (resultSet.next()) {
                        final String person_id = resultSet.getString("person_id");
                        Map person;
                        Map<String, Object> shifts;
                        if(!persons.containsKey(person_id)) {
                            person = new HashMap();
                            shifts = new HashMap<String, Object>();
                            person.put("name", resultSet.getString("name"));
                            person.put("shifts", shifts);
                            persons.put(person_id,  person);
                        } else {
                            person = persons.get(person_id);
                            shifts = (Map<String, Object>) person.get("shifts");
                        }
                        shifts.put("shift_time",  resultSet.getDate("shift_time"));
                        shifts.put("shift_date",  resultSet.getDate("shift_date"));
                        System.out.println(">> " + person_id);
                    }
                }
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
        return result;
    }
}
