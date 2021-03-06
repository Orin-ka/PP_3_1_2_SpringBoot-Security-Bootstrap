package com.orinka.springboot.entity;

import org.springframework.security.core.GrantedAuthority;
import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "roles")

public class Role implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private EnumRole name;

    //@ManyToMany(mappedBy = "roles")
    @ManyToMany
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
   // @Transient
    Set<User> users;

    public Role() {}
    public Role(EnumRole role) {
        this.name = role;
    }
    public Role(String str) {
        this(EnumRole.valueOf(str));
    }



    public Long getId() {
        return id;
    }

    public EnumRole getName() {
        return name;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(EnumRole name) {
        this.name = name;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }



    @Override
    public String getAuthority() {
        return name.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return  (this.name == (role.name));//enum можно сравнивать по ==
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }


}

