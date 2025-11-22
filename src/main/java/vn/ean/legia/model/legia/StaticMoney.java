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
@Table(name = "legia_static_money")
public class StaticMoney {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String note;
    private int amount;

    @ManyToOne
    @JoinColumn(name = "staff")
    @JsonIgnore
    private Staff staff;

    @Column(name = "created_date")
    private Long createdDate;

    public StaticMoney(Staff staff) {
        this.staff = staff;
        this.createdDate = Lib.currentTimestamp();
    }

    public StaticMoney(Staff staff, String note, int amount) {
        this.staff = staff;
        this.note = note;
        this.amount = amount;
        this.createdDate = Lib.currentTimestamp();
    }

    public String getStaffName() {
        return staff.getName();
    }

    public String getCreatedDateDisplay() {
        return Lib.timestampConvert(createdDate, "HH:mm dd/MM/YYYY");
    }

    public String getAmountDisplay() {
        return String.format("<span class='bg-%s-200 px-2 py-1'>%s%,d</span>", amount < 0 ? "red" : "green", amount > 0 ? "+" : "", amount);
    }
}
