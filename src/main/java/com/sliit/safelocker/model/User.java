package com.sliit.safelocker.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    @Column(unique = true)
    private String username;
    @JsonIgnore
    private String password;
    private boolean isActive;
    private String flag;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id",referencedColumnName = "id")
    private Role role;
    private int otp;
    private Timestamp otpTime;


//    @CreationTimestamp
//    private LocalDateTime createDateTime;
//    @UpdateTimestamp
//    private LocalDateTime updateDateTime;
}
