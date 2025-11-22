package vn.ean.legia.model.legia;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class SalaryInMonth {
    private Staff staff;

    private double worktime, overtime;

    private int worktimeSal, overtimeSal, bonusSal, subSal, total;

    private String workedDates;
}
