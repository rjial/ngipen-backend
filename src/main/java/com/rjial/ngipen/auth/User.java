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
@NoArgsConstructor
@Table(name = "user")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user")
    @JsonIgnore
    private Long id;
    @NonNull
    @Column(name = "email_user")
    private String email;
    @NonNull
    @Column(name = "nama_user")
    private String name;
    @NonNull
    @Column(name = "password_user")
    @JsonIgnore
    private String password;
    @NonNull
    @Column(name = "nohp_user")
    private String hp;
    @NonNull
    @Column(name = "alamat")
    private String address;
    @NonNull
    @Enumerated(EnumType.STRING)
    private Level level;
    @NonNull
    @Column(name = "uuid")
    private UUID uuid = UUID.randomUUID();

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
