package vn.ean.legia.model.legia;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import vn.ean.legia.service.legia.SalaryService;
import vn.ean.legia.util.Lib;

@Entity
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@Table(name = "legia_staff")
public class Staff {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int salary = 0;

    @OneToMany(mappedBy = "staff", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<TimeSheet> timeSheets;

    @OneToMany(mappedBy = "staff", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<StaticMoney> staticMoneys;

    @Column(name = "created_date")
    private Long createdDate;

    @Transient
    private Map<String, Object> overview = new HashMap<>();

    public Staff(Long id) {
        this.id = id;
    }

    public Staff(Long id, String name, int salary) {
        this.id = id;
        this.name = name;
        this.salary = salary;
        createdDate = Lib.currentTimestamp();
    }

    public Staff(String name, int salary) {
        this.name = name;
        this.salary = salary;
        createdDate = Lib.currentTimestamp();
    }

    public Map<String, Object> getNameDisplay() {
        Map<String, Object> result = new HashMap<>();

        result.put("id", id);
        result.put("name", name);
        result.put("salary", salary);

        return result;
    }

    public Map<String, Object> getActions() {
        Map<String, Object> result = new HashMap<>();

        if (timeSheets != null && !timeSheets.isEmpty()) {
            TimeSheet last = timeSheets.get(timeSheets.size() - 1);
            if (last.getEnd() == 0) {
                result.put("type", "end");
                result.put("workTime", Lib.timeSince(Lib.currentTimestamp() - last.getStart(), 2));
            }
        }

        return result;
    }

    public String getSalaryDisplay() {
        return String.format("%,d", salary);
    }

    public Map<String, Map<String, String>> getDates() {
        SalaryService salaryService = new SalaryService();

        Map<String, Map<String, String>> dates = new LinkedHashMap<>();

        Map<String, Map<String, Long>> months = Lib.getMonthsMapFromPast(1);
        months.forEach((month, value) -> {
            Map<String, String> detail = new HashMap<>();
            Long start = value.get("start"), end = value.get("end");

            if (timeSheets == null) {
                dates.put(month, detail);
                return;
            }

            List<TimeSheet> indayShifts = timeSheets.stream().filter(ts -> ts.getStart() >= start && ts.getEnd() <= end && (ts.getWorktime() > 0 || ts.getOvertime() > 0) && ts.getEnd() != 0).toList();
            if (indayShifts.isEmpty()) {
                dates.put(month, detail);
                return;
            }

            long normal = 0, ot = 0;
            for (TimeSheet shift : indayShifts) {
                normal += shift.getWorktime();
                ot += shift.getOvertime();
            }

            double worktimeHr = Math.floor((normal / 3600.0) * 10) / 10.0;
            double overtimeHr = Math.floor((ot / 3600.0) * 10) / 10.0;

            detail.put("normal", Lib.timeSince(normal, 2));
            detail.put("normalNum", String.valueOf(worktimeHr));
            detail.put("normalSal", String.valueOf(salaryService.calcWorktimeSal(salary, worktimeHr)));

            detail.put("ot", Lib.timeSince(ot, 2));
            detail.put("otNum", String.valueOf(overtimeHr));
            detail.put("otSal", String.valueOf(salaryService.calcOvertimeSal(overtimeHr)));

            dates.put(month, detail);
        });

        return dates;
    }
}