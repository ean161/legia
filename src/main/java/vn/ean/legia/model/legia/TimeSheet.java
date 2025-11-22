package vn.ean.legia.model.legia;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import vn.ean.legia.util.Lib;

@Entity
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@Table(name = "legia_time_sheet")
public class TimeSheet {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long start, end = 0l, worktime = 0l, overtime = 0l;

    @ManyToOne
    @JoinColumn(name = "staff")
    @JsonIgnore
    private Staff staff;

    private boolean isStartEdited, isEndEdited;

    @Column(name = "created_date")
    private Long createdDate;

    public TimeSheet(Staff staff) {
        this.staff = staff;
        this.isStartEdited = false;
        this.isEndEdited = false;
        this.createdDate = Lib.currentTimestamp();
        this.start = this.createdDate;
    }

    public String getStaffName() {
        return staff.getName();
    }

    public String getStartDisplay() {
        return String.format("%s%s",
                Lib.timestampConvert(start, "HH:mm dd/MM/YYYY"),
                (isStartEdited ? "&ensp;<i class='uk-text-danger'>(đã sửa)</i>" : ""));
    }

    public String getEndDisplay() {
        if (end == 0) {
            return "-";
        }

        return String.format("%s%s",
                Lib.timestampConvert(end, "HH:mm dd/MM/YYYY"),
                (isEndEdited ? "&ensp;<i class='uk-text-danger'>(đã sửa)</i>" : ""));
    }

    public String getWorktimeDisplay() {
        if (worktime == 0) {
            return "-";
        }

        return Lib.timeSince(worktime, 2);
    }

    public String getOvertimeDisplay() {
        if (overtime == 0) {
            return "-";
        }

        return Lib.timeSince(overtime, 2);
    }

    public boolean getAllowEdit() {
        String date = Lib.timestampConvert(Lib.currentTimestamp(), "dd/MM/YYYY");
        String startDate = Lib.timestampConvert(start, "dd/MM/YYYY");

        return date.equals(startDate);
    }
}
