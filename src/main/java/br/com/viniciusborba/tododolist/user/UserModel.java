package br.com.viniciusborba.tododolist.user;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity(name= "tb_users")
public class UserModel {
  
  @Id
  @GeneratedValue(generator = "UUID")
  private UUID id;

  @Column(unique = true)
  private String username;
  
  private String name;
  
  @Column(unique = true)
  private String email;
  
  private String password;

  @CreationTimestamp
  private LocalDateTime createdAt;
}
