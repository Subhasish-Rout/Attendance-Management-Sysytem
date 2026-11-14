package com.example.amsapp;

public class Student {
    private String uid;
    private String name;
    private String email;
    private String role; // "admin", "teacher", "student"
    private String classname;
    private String rollNumber;

    public Student() {}

    public Student(String uid, String name, String email, String role,
                   String classname, String rollNumber) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.role = role;
        this.classname = classname;
        this.rollNumber = rollNumber;
    }

    public String getUid()         { return uid; }
    public String getName()        { return name; }
    public String getEmail()       { return email; }
    public String getRole()        { return role; }
    public String getClassname()   { return classname; }
    public String getRollNumber()  { return rollNumber; }

    public void setUid(String uid)               { this.uid = uid; }
    public void setName(String name)             { this.name = name; }
    public void setEmail(String email)           { this.email = email; }
    public void setRole(String role)             { this.role = role; }
    public void setClassname(String classname)   { this.classname = classname; }
    public void setRollNumber(String rollNumber) { this.rollNumber = rollNumber; }
}