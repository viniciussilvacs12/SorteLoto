package br.com.smartloto.domain;

import jakarta.persistence.*;

@Entity
@Table(name="app_users", uniqueConstraints=@UniqueConstraint(columnNames="email"))
public class AppUser {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private String name;

    @Column(nullable=false)
    private String email;

    @Column(nullable=false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private UserRole role = UserRole.USER;

    protected AppUser(){}

    public AppUser(String name, String email, String passwordHash, UserRole role){
        this.name=name;
        this.email=email;
        this.passwordHash=passwordHash;
        this.role=role == null ? UserRole.USER : role;
    }

    @PrePersist
    void ensureRole(){
        if(role == null) role = UserRole.USER;
    }

    public void synchronizeAdminCredentials(String name, String passwordHash){
        this.name = name;
        this.passwordHash = passwordHash;
        this.role = UserRole.ADMIN;
    }

    public Long getId(){return id;}
    public String getName(){return name;}
    public String getEmail(){return email;}
    public String getPasswordHash(){return passwordHash;}
    public UserRole getRole(){return role == null ? UserRole.USER : role;}
}
