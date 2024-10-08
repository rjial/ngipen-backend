package com.rjial.ngipen.event;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.rjial.ngipen.auth.User;
import com.rjial.ngipen.auth.UserSerializer;
import com.rjial.ngipen.tiket.JenisTiket;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Length;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "event")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_event")
    @JsonIgnore
    private Long id;
    @NonNull
    @Column(name = "nama_event")
    private String name;
    @Column(name = "uid_event")
    private UUID uuid = UUID.randomUUID();
    @NonNull
    @Column(name = "tanggalawal_event")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonProperty("tanggal_awal")
    private LocalDate tanggalAwal;
    @Column(name = "tanggalakhir_event")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonProperty("tanggal_akhir")
    private LocalDate tanggalAkhir;
    @Column(name = "headerimageurl_event")
    @JsonProperty("headerimageurl")
    @JsonSerialize(using = EventHeaderImgUrlSerializer.class)
    private String headerImageUrl;
    @Column(name = "itemimageurl_event")
    @JsonProperty("itemimageurl")
    @JsonSerialize(using = EventHeaderImgUrlSerializer.class)
    private String itemImageUrl;
    @NonNull
    @Column(name = "waktuawal_event")
    @JsonFormat(pattern = "HH:mm")
    @DateTimeFormat(pattern = "HH:mm")
    @JsonDeserialize(using = LocalTimeDeserializer.class)
    @JsonSerialize(using = LocalTimeSerializer.class)
    @JsonProperty("waktu_awal")
    @Schema(type = "string")
    private LocalTime waktuAwal;
    @NonNull
    @Column(name = "waktuakhir_event")
    @JsonFormat(pattern = "HH:mm")
    @DateTimeFormat(pattern = "HH:mm")
    @JsonDeserialize(using = LocalTimeDeserializer.class)
    @JsonSerialize(using = LocalTimeSerializer.class)
    @JsonProperty("waktu_akhir")
    @Schema(type = "string")
    private LocalTime waktuAkhir;
    @NonNull
    @Column(name = "lokasi_event")
    private String lokasi;
    @NonNull
    @Column(name = "deskripsi_event", length = Length.LONG32)
    private String desc;
    @NonNull
    @Column(name = "verify_event")
    private Boolean verifyEvent = false;

    @OneToMany(mappedBy = "event")
//    @JsonIgnore
    @JsonSerialize(using = EventJenisTiketListSerializer.class)
    private List<JenisTiket> jenisTikets;

    @ManyToOne
    @JoinColumn(name = "id_user", referencedColumnName = "id_user")
    @JsonManagedReference
    @JsonSerialize(using = UserSerializer.class)
    @Schema(type = "string", example = "Lorem Ipsum")
    private User pemegangEvent;

}
