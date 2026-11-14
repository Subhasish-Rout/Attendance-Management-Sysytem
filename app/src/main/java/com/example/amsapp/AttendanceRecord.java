package com.example.amsapp;

public class AttendanceRecord {
    private String studentUid;
    private String studentName;
    private String rollNumber;
    private String classname;
    private String date;
    private String time;
    private String status; // "Present" or "Absent"
    private String markedBy;

    public AttendanceRecord() {}

    public AttendanceRecord(String studentUid, String studentName,
                            String rollNumber, String classname,
                            String date, String time,
                            String status, String markedBy) {
        this.studentUid  = studentUid;
        this.studentName = studentName;
        this.rollNumber  = rollNumber;
        this.classname   = classname;
        this.date        = date;
        this.time        = time;
        this.status      = status;
        this.markedBy    = markedBy;
    }

    public String getStudentUid()  { return studentUid; }
    public String getStudentName() { return studentName; }
    public String getRollNumber()  { return rollNumber; }
    public String getClassname()   { return classname; }
    public String getDate()        { return date; }
    public String getTime()        { return time; }
    public String getStatus()      { return status; }
    public String getMarkedBy()    { return markedBy; }

    public void setStudentUid(String studentUid)   { this.studentUid = studentUid; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public void setRollNumber(String rollNumber)   { this.rollNumber = rollNumber; }
    public void setClassname(String classname)     { this.classname = classname; }
    public void setDate(String date)               { this.date = date; }
    public void setTime(String time)               { this.time = time; }
    public void setStatus(String status)           { this.status = status; }
    public void setMarkedBy(String markedBy)       { this.markedBy = markedBy; }
}