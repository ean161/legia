package vn.ean.legia.model.mcserver_deposit_portal;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import vn.ean.legia.model.User;
import vn.ean.legia.util.Lib;

@Entity
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@Table(name = "mcserver_deposit_portal_server")
public class Server {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title, description;
    
    @Column(length = 1000)
    private String banner;

    @OneToMany(mappedBy = "server")
    @JsonIgnore
    private List<ChildServer> childServer;

    @ManyToOne
    @JoinColumn(name = "user")
    private User user;
    
    @Column(name = "created_date")
    private Long createdDate;

    public String getCreatedDateDisplay() {
        return Lib.timestampConvert(createdDate, "HH:mm dd/MM/YYYY");
    }

    public Server(Long id, User user, String title, String description, String banner) {
        this.id = id;
        this.user = user;
        this.title = title;
        this.description = description;
        this.banner = banner;
        createdDate = Lib.currentTimestamp();
    }

    public Server(User user, String title, String description, String banner) {
        this.user = user;
        this.title = title;
        this.description = description;
        this.banner = banner;
        createdDate = Lib.currentTimestamp();
    }
}