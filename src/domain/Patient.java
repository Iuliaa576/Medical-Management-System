package domain;

import java.io.Serializable;

public class Patient implements Identifiable<String>, Serializable {
    private String id;
    private String name;
    private String email;
    private String phoneNumber;
    private int age;

    public Patient(String id, String name, String email, String phoneNumber) {
        this(id, name, email, phoneNumber, 30);
    }

    public Patient(String id, String name, String email, String phoneNumber, int age) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.age = age;
    }

    //Getters
    @Override
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public int getAge() {return age;}

    public String getHealthRiskStatus() {
        if (age < 60) return "low risk";
        return "high risk";
    }

    //Setters
    @Override
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "Patient{" +
                "id=" + id +
                ", name=" + name +
                ", email=" + email +
                ", phoneNumber=" + phoneNumber +
                ", age=" + age +
                ", healthRiskStatus=" + getHealthRiskStatus() +
                "}";
    }
}