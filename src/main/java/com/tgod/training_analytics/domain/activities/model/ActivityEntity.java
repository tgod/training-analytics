package com.tgod.training_analytics.domain.activities.model;

import jakarta.persistence.*;

@Entity
@Table(
        name = "activities",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_activities_strava_id", columnNames = "strava_id")
        }
)
public class ActivityEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "strava_id", nullable = false)
    private Long stravaId;

    @Column(nullable = false, length = 100)
    private String username;

    @Column(name = "activity_name")
    private String name;

    private double distance;

    @Column(name = "moving_time")
    private int movingTime;

    @Column(name = "elapsed_time")
    private int elapsedTime;

    private String type;

    @Column(name = "sport_type")
    private String sportType;

    @Column(name = "start_date")
    private String startDate;

    @Column(name = "start_date_local")
    private String startDateLocal;

    private String timezone;

    @Column(name = "total_elevation_gain")
    private double totalElevationGain;

    @Column(name = "average_speed")
    private double averageSpeed;

    @Column(name = "max_speed")
    private double maxSpeed;

    @Column(name = "average_heartrate")
    private Double averageHeartrate;

    @Column(name = "max_heartrate")
    private Double maxHeartrate;

    @Column(name = "average_cadence")
    private Double averageCadence;

    @Column(name = "average_watts")
    private Double averageWatts;

    @Column(name = "weighted_average_watts")
    private Double weightedAverageWatts;

    @Column(name = "average_temp")
    private Integer averageTemp;

    private Double kilojoules;

    private boolean trainer;

    private boolean commute;

    @Column(name = "achievement_count")
    private int achievementCount;

    @Column(name = "kudos_count")
    private int kudosCount;

    @Column(name = "suffer_score")
    private Double sufferScore;

    @Column(name = "elev_high")
    private double elevHigh;

    @Column(name = "elev_low")
    private double elevLow;

    protected ActivityEntity() {}

    public ActivityEntity(Activity activity, String username) {
        this.stravaId = activity.id();
        this.username = username;
        updateFrom(activity);
    }

    public void updateFrom(Activity activity) {
        this.name = activity.name();
        this.distance = activity.distance();
        this.movingTime = activity.movingTime();
        this.elapsedTime = activity.elapsedTime();
        this.type = activity.type();
        this.sportType = activity.sportType();
        this.startDate = activity.startDate();
        this.startDateLocal = activity.startDateLocal();
        this.timezone = activity.timezone();
        this.totalElevationGain = activity.totalElevationGain();
        this.averageSpeed = activity.averageSpeed();
        this.maxSpeed = activity.maxSpeed();
        this.averageHeartrate = activity.averageHeartrate();
        this.maxHeartrate = activity.maxHeartrate();
        this.averageCadence = activity.averageCadence();
        this.averageWatts = activity.averageWatts();
        this.weightedAverageWatts = activity.weightedAverageWatts();
        this.averageTemp = activity.averageTemp();
        this.kilojoules = activity.kilojoules();
        this.trainer = activity.trainer();
        this.commute = activity.commute();
        this.achievementCount = activity.achievementCount();
        this.kudosCount = activity.kudosCount();
        this.sufferScore = activity.sufferScore();
        this.elevHigh = activity.elevHigh();
        this.elevLow = activity.elevLow();
    }

    public Long getId() { return id; }
    public Long getStravaId() { return stravaId; }
    public String getUsername() { return username; }
    public String getStartDate() { return startDate; }
}
