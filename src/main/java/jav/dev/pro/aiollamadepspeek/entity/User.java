package jav.dev.pro.aiollamadepspeek.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false,name = "chat_id")
    private Long chatId;
    @Column(name = "fist_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "username",unique = true)
    private String username;
    @Column(name = "phone_number", unique = true, nullable = false)
    private String phoneNumber;
    @Column(name = "created_At", nullable = false)
    private LocalDateTime createdAt=LocalDateTime.now();
    @Column(name = "in_registration")
    private Boolean isStart= false;
    @Column(name = "is_deleted")
    private boolean isDeleted = false;
}
