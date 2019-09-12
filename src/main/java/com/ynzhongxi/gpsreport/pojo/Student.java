package com.ynzhongxi.gpsreport.pojo;


import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class Student {
 
    private String name;
 
    private String gender;
 
    private String gradeClass;
 
    private List<Course> courses = new ArrayList<>();
 
    public String getName() {
        return name;
    }
 
    public void setName(String name) {
        this.name = name;
    }
 
    public String getGender() {
        return gender;
    }
 
    public void setGender(String gender) {
        this.gender = gender;
    }
 
    public String getGradeClass() {
        return gradeClass;
    }
 
    public void setGradeClass(String gradeClass) {
        this.gradeClass = gradeClass;
    }
 
    public List<Course> getCourses() {
        return courses;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }
}
