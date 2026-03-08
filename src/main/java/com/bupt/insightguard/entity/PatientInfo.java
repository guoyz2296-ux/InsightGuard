package com.bupt.insightguard.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "patient_info")
public class PatientInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username; // 登录账号（家属用）

    @Column(nullable = false, length = 100)
    private String password; // 加密后的密码

    @Column(nullable = false, length = 50)
    private String name; // 患者姓名

    private Integer age; // 年龄

    @Column(name = "emergency_contact", length = 20)
    private String emergencyContact; // 家属联系电话

    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime; // 创建时间

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    // --- 无参构造函数 (JPA 必须) ---
    public PatientInfo() {
    }

    // --- 自动设置创建时间的钩子 ---
    @PrePersist
    protected void onCreate() {
        this.createTime = LocalDateTime.now();
    }
}