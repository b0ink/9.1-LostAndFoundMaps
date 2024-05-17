package com.example.lostandfound;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Entity(tableName = "lost_items")
public class LostItem {

    public enum REPORT_TYPE {
        REPORT_TYPE_LOST,
        REPORT_TYPE_FOUND
    }

    @PrimaryKey(autoGenerate = true)
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private REPORT_TYPE reportType;
    private String itemName;
    private String description;

    @Embedded
    private LocationInfo location;

    private String dateReported;
    private String postersName;
    private String mobile;

    public LostItem(REPORT_TYPE reportType, String itemName, String description, LocationInfo location, String dateReported, String postersName, String mobile) {
        this.reportType = reportType;
        this.itemName = itemName;
        this.description = description;
        this.location = location;
        this.dateReported = dateReported;
        this.postersName = postersName;
        this.mobile = mobile;
    }

    public REPORT_TYPE getReportType() {
        return reportType;
    }

    public String getItemName() {
        return itemName;
    }

    public String getDescription() {
        return description;
    }

    public LocationInfo getLocation() {
        return location;
    }

    public void setLocation(LocationInfo location) {
        this.location = location;
    }

    public String getDateReported() {
        return dateReported;
    }

    public void setDateReported(String dateReported) {
        this.dateReported = dateReported;
    }

    public String getPostersName() {
        return postersName;
    }

    public String getMobile() {
        return mobile;
    }


    public String formatTimeAgo() {

        LocalDate date = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            date = LocalDate.parse(this.dateReported, formatter);
        }
        LocalDate currentDate = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            currentDate = LocalDate.now();
            long years = ChronoUnit.YEARS.between(date, currentDate);
            long months = ChronoUnit.MONTHS.between(date, currentDate);
            long weeks = ChronoUnit.WEEKS.between(date, currentDate);
            long days = ChronoUnit.DAYS.between(date, currentDate);
            long hours = ChronoUnit.HOURS.between(date.atStartOfDay(), LocalDateTime.now());
            long minutes = ChronoUnit.MINUTES.between(date.atStartOfDay(), LocalDateTime.now());
            long seconds = ChronoUnit.SECONDS.between(date.atStartOfDay(), LocalDateTime.now());

            if (years > 0) {
                return years + " year" + (years > 1 ? "s" : "") + " ago";
            } else if (months > 0) {
                return months + " month" + (months > 1 ? "s" : "") + " ago";
            } else if (weeks > 0) {
                return weeks + " week" + (weeks > 1 ? "s" : "") + " ago";
            } else if (days > 0) {
                return days + " day" + (days > 1 ? "s" : "") + " ago";
            } else if (hours > 0) {
                return hours + " hour" + (hours > 1 ? "s" : "") + " ago";
            } else if (minutes > 0) {
                return minutes + " minute" + (minutes > 1 ? "s" : "") + " ago";
            } else if (seconds > 0) {
                return seconds + " second" + (seconds > 1 ? "s" : "") + " ago";
            } else {
                return "Just now";
            }
        }
        return "Not too long";
    }
}
