package com.rjial.ngipen.auth;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.rjial.ngipen.event.Event;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Data
@Getter
@Setter
@Entity
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user")
    @JsonIgnore
    private Long id;
    @Column(name = "email_user", nullable = false, unique = true)
    private String email;
    @Column(name = "nama_user", nullable = false)
    private String name;
    @Column(name = "password_user", nullable = false)
    @JsonIgnore
    private String password;
    @Column(name = "nohp_user")
    private String hp;
    @Column(name = "alamat")
    private String address;
    @Enumerated(EnumType.STRING)
    private Level level;
    @Column(name = "uuid", nullable = false, unique = true)
    private UUID uuid;

    @OneToMany(mappedBy = "pemegangEvent")
    @JsonBackReference
    @JsonIgnore
    private List<Event> events;

    public String firstName() {
        return name.split(" ")[0];
    }

    public String lastName() {
        return name.replace(firstName() + " ", "");
    }

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(level.toString()));
    }

    @JsonIgnore
    @Override
    public String getUsername() {
        return email;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return true;
    }
}
