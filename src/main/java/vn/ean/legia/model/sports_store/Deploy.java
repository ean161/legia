package vn.ean.legia.model.sports_store;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.ean.legia.model.User;
import vn.ean.legia.util.Lib;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "sports_store_deploy")
public class Deploy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_size")
    private double fileSize;

    @ManyToOne
    @JoinColumn(name = "user")
    private User user;

    private String status;

    @Column(name = "created_date")
    private Long createdDate;

    public String getUserName() {
        return this.user.getUsername();
    }

    public String getCreatedDateDisplay() {
        return Lib.timestampConvert(createdDate, "HH:mm dd/MM/YYYY");
    }
}
