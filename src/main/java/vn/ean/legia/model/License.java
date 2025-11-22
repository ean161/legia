package vn.ean.legia.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import vn.ean.legia.util.Lib;

@Entity
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class License {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title, directory, note, icon;

    @ManyToOne
    @JoinColumn(name = "user")
    @JsonIgnore
    private User user;

    @Column(name = "expired_date")
    private Long expiredDate;
    
    @Column(name = "created_date")
    private Long createdDate;

    public License(User user, String title, String directory, String note, String icon, Long expiredDate) {
        this.user = user;
        this.title = title;
        this.directory = directory;
        this.note = note;
        this.icon = icon;
        this.expiredDate = expiredDate;
        createdDate = Lib.currentTimestamp();
    }

    public String getUserName() {
        return user.getUsername();
    }

    public String getExpiredDateDisplay() {
        return Lib.timestampConvert(expiredDate, "HH:mm dd/MM/YYYY");
    }

    public String getCreatedDateDisplay() {
        return Lib.timestampConvert(createdDate, "HH:mm dd/MM/YYYY");
    }

    public boolean getActions() {
        if (Lib.currentTimestamp() < this.expiredDate) {
            return true;
        }

        return false;
    }
}
