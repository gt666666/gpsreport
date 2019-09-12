package com.ynzhongxi.gpsreport.pojo;

import lombok.Data;
@Data
public class Course {
 
    private String courseName;
 
    private String courseScore;
 
    public String getCourseName() {
        return courseName;
    }
 
    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }
 
    public String getCourseScore() {
        return courseScore;
    }
 
    public void setCourseScore(String courseScore) {
        this.courseScore = courseScore;
    }
}
