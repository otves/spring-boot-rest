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

import hello.dto.Person;
import hello.dto.Shift;
import hello.dto.Store;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/rest")
public class RestService {

    private static final Logger logger = Logger.getLogger(RestService.class.getName());


    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class,
                new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd"), true, 10));
    }


    static ConfigJdbc configJdbc() {
        return new ConfigJdbc(
                org.postgresql.Driver.class,
                "jdbc:postgresql://88.201.248.46:5432/personal",
                "vitaly",
                "m127rqu4");
    }

    @RequestMapping("/stores")
    public List<Store> stores() throws SQLException {
        List<Store> stores = new ArrayList<>();
        try (final Connection db = configJdbc().getConnection()) {
            try (final PreparedStatement sql = db.prepareStatement("select * from stores")) {
                try (ResultSet resultSet = sql.executeQuery()) {
                    while (resultSet.next()) {
                        stores.add(new Store(resultSet.getString("id"), resultSet.getString("descr")));
                    }
                }
            }
        }
        return stores;
    }


    @RequestMapping("/persons")
    public List<Employee> persons(@RequestParam(value = "name", defaultValue = "World") String name) {
        List<Employee> res = new ArrayList<>();
        res.add(new Employee());
        return res;
    }

    @RequestMapping("/shifts")
    public List greeting(@RequestParam(value = "store", required = false) Long store,
                         @RequestParam(value = "calculation", required = false) Long calculation,
                         @RequestParam(value = "date", defaultValue = "2016-09-01") Date date) {

        return shiftsPlan(store, calculation, date);

    }

    public static List shiftsPlan(Long store, Long calculation, Date date) {
        List<Person> result = new ArrayList<>();
        HashMap<String, Person> persons = new HashMap();
        String sqlQuery = " select persons.id as person_id, persons.name, shifts.id as shift_id, shifts_plan.id as shifts_plan_id, shifts_plan.shift_time, shifts_plan.shift_date from shifts \n" +
                "/*join staff_positions ON  shifts.staff_position_id = staff_positions.id*/\n" +
                "join staff_list ON  shifts.staff_person_id = staff_list.id\n" +
                "join persons ON  staff_list.person_id = persons.id AND persons.active = true\n" +
                "join shifts_plan ON shifts.id = shifts_plan.shift_id and shifts.store_id = shifts_plan.store_id\n" +
                "where shifts_plan.shift_date <= :date \n";
        if(store != null) {
            sqlQuery += " AND shifts.store_id = :store \n";
        }
        if(calculation != null) {
            sqlQuery += "AND shifts.calc_id = :calc\n";
        }
        sqlQuery +=     "LIMIT 100;";
        logger.info(sqlQuery);
        try (final Connection con = configJdbc().getConnection()) {
            try (final NamedParameterStatement sql = new NamedParameterStatement(con, sqlQuery)) {
                sql.setDate("date", new java.sql.Date(date.getTime()));
                if(store != null) {
                    sql.setLong("store", store);
                }
                if(calculation != null) {
                    sql.setLong("calc", calculation);
                }
                try (ResultSet resultSet = sql.executeQuery()) {
                    while (resultSet.next()) {
                        final String person_id = resultSet.getString("person_id");
                        Person person;
                        List<Shift> shifts;
                        if (!persons.containsKey(person_id)) {
                            person = new Person();
                            shifts = new ArrayList<>();
                            person.setId(person_id);
                            person.setContent(resultSet.getString("name"));
                            person.setShifts(shifts);
                            persons.put(person_id, person);
                            result.add(person);
                        } else {
                            person = persons.get(person_id);
                            shifts = person.getShifts();
                        }
                        Shift shift = new Shift();
                        shift.setStart(resultSet.getDate("shift_time").getTime());
                        shift.setEnd(resultSet.getDate("shift_time").getTime());
                        shift.setPlanId(resultSet.getString("shifts_plan_id"));
                        shift.setStore("");
                        shift.setHours("1");
                        shifts.add(shift);
                        //shifts.put("shift_date", resultSet.getDate("shift_date"));
                    }
                }
            }
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
        logger.info("===================================================");
        logger.info(result.toString());
        return result;
    }
}
