package vn.ean.legia.model.mcserver_deposit_portal;

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

@Entity
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@Table(name = "mcserver_deposit_portal_child_server")
public class ChildServer {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title, ip, port, password, commands;
    
    @Column(name = "card_ratio")
    private double cardRatio;
    
    @Column(name = "banking_ratio")
    private double bankingRatio;

    @ManyToOne
    @JoinColumn(name = "server")
    @JsonIgnore
    private Server server;
    
    @Column(name = "created_date")
    private Long createdDate;
}