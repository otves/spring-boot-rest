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

import hello.dto.Calculation;
import hello.dto.Employee;
import hello.dto.Person;
import hello.dto.Store;
import hello.jdbc.ConfigJdbc;
import hello.jdbc.NamedParameterStatement;
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

    @RequestMapping("/calculations")
    public List<Calculation> calculations(@RequestParam(value = "store") Long store) throws Exception {

       String query = "select calculations.* from calculations \n" +
                "join shifts ON  calculations.id = shifts.calc_id\n" +
                "join shifts_plan ON shifts.id = shifts_plan.shift_id and shifts.store_id = calculations.store_id and calculations.org_id = shifts.org_id\n " +
                "where calculations.store_id = ? and calculations.actual = true";

        List<Calculation> stores = new ArrayList<>();
        try (final Connection db = configJdbc().getConnection()) {
            try (final PreparedStatement sql = db.prepareStatement(query)) {
                sql.setLong(1, store);
                try (ResultSet resultSet = sql.executeQuery()) {
                    while (resultSet.next()) {
                        String interval = resultSet.getString("start_date") +  " - " + resultSet.getString("end_date");
                        stores.add(new Calculation(resultSet.getString("id"), interval, resultSet.getString("org_id") ));
                    }
                }
            }
        }
        return stores;
    }

    @RequestMapping("/shifts")
    public List greeting(@RequestParam(value = "store", required = true) Long store,
                         @RequestParam(value = "calc", required = true) Long calculation,
                         @RequestParam(value = "from", required = false) Date from,
                         @RequestParam(value = "to", required = false) Date to) {

        return shiftsPlan(store, calculation, from, to);

    }

    public static List shiftsPlan(Long store, Long calculation, Date from, Date to) {
        List<Person> result = new ArrayList<>();
        HashMap<String, Person> persons = new HashMap<>();
        String sqlQuery = " select shifts.calc_id as calc_id, persons.id as person_id, persons.name, shifts.id as shift_id, shifts_plan.id as shifts_plan_id, shifts_plan.shift_time, shifts_plan.shift_date from shifts \n" +
                "/*join staff_positions ON  shifts.staff_position_id = staff_positions.id*/\n" +
                "join staff_list ON  shifts.staff_person_id = staff_list.id\n" +
                "join persons ON  staff_list.person_id = persons.id AND persons.active = true\n" +
                "join shifts_plan ON shifts.id = shifts_plan.shift_id and shifts.store_id = shifts_plan.store_id\n " +
                "where shifts.store_id = :store";
        if (from != null) {
            sqlQuery += " AND shifts_plan.shift_date >= :from \n";
        }
        if (to != null) {
            sqlQuery += " AND shifts_plan.shift_date <= :to \n";
        }
        if (calculation != null) {
            sqlQuery += " AND shifts.calc_id = :calc\n";
        }
        sqlQuery += " LIMIT 10000;";
        logger.info(sqlQuery);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try (final Connection con = configJdbc().getConnection()) {
            try (final NamedParameterStatement sql = new NamedParameterStatement(con, sqlQuery)) {
                sql.setLong("store", store);
                if (from != null) {
                    sql.setDate("from", new java.sql.Date(from.getTime()));
                }
                if (to != null) {
                    sql.setDate("to", new java.sql.Date(to.getTime()));
                }
                if (calculation != null) {
                    sql.setLong("calc", calculation);
                }
                try (ResultSet resultSet = sql.executeQuery()) {
                    while (resultSet.next()) {
                        final String person_id = resultSet.getString("person_id");
                        Person person;

                        if (!persons.containsKey(person_id)) {
                            person = new Person();
                            person.setId(person_id);
                            person.setName(resultSet.getString("name"));
                            logger.info("person:" + person.getName());
                            persons.put(person_id, person);
                            result.add(person);
                        } else {
                            person = persons.get(person_id);
                        }
                        logger.info(resultSet.getString("calc_id") + "::" + resultSet.getString("shifts_plan_id") + " :: shift_date:" + resultSet.getDate("shift_date") + " :: shift_time:" + resultSet.getDate("shift_time"));
                        String shift_date = dateFormat.format(new Date(resultSet.getDate("shift_date").getTime()));
                        person.incShift(shift_date, resultSet.getString("shifts_plan_id"));
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
