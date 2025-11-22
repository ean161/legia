package vn.ean.legia.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import vn.ean.legia.util.Lib;

@Entity
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username, password, role = "member", photo_url = "/image/user.png", token;
    private double balance = 0;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    @JsonIgnore
    private List<License> licenses;

    @Column(name = "created_date")
    private Long createdDate;

    public User(String username, String password, String token) {
        this.username = username;
        this.password = password;
        this.token = token;
        createdDate = Lib.currentTimestamp();
    }

    public String getBalanceDisplay() {
        return String.format("%,.0f", balance);
    }

    public List<License> getValidLicenses() {
        Long now = Lib.currentTimestamp();
        List<License> validLicenses = new ArrayList<>();

        if (licenses == null || licenses.isEmpty()) {
            return validLicenses;
        }
        
        for (License item : licenses) {
            if (item.getExpiredDate() > now) {
                validLicenses.add(item);
            }
        }

        return validLicenses;
    }
}
