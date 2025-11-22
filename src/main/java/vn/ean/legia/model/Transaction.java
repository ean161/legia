package vn.ean.legia.model;

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
@Table(name = "transaction")
public class Transaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "third_id")
    private Long thirdId;

    private String content, status, note, service;
    private double amount;

    @ManyToOne
    @JoinColumn(name = "user")
    @JsonIgnore
    private User user;

    @Column(name = "posted_date")
    private Long postedDate;
    
    @Column(name = "created_date")
    private Long createdDate;

    public Transaction(User user, Long thirdId, String service, String content, String status, String note, double amount, Long postedDate) {
        this.user = user;
        this.thirdId = thirdId;
        this.service = service;
        this.content = content;
        this.status = status;
        this.note = note;
        this.amount = amount;
        this.postedDate = postedDate;
        createdDate = Lib.currentTimestamp();
    }

    public String getServiceDisplay() {
        switch (service) {
            case "deposit":
                return "Nạp tiền";
            case "withdraw":
                return "Rút tiền";
            default:
                return "Không xác định";
        }
    }

    public String getAmountDisplay() {
        return String.format("%s%,.0f", amount >= 0 ? "+" : "", amount);
    }

    public String getCreatedDateDisplay() {
        return Lib.timestampConvert(createdDate, "HH:mm dd/MM/YYYY");
    }
}
