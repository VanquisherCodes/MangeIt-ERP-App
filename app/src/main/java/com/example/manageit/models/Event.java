package com.example.manageit.models;

import com.google.gson.annotations.SerializedName;

/**
 * Event entity for group schedules and calendars.
 */
public class Event {

    @SerializedName("event_id")
    private String eventId;
    @SerializedName("group_id")
    private String groupId;
    @SerializedName(value = "event_name", alternate = {"title"})
    private String eventName;
    @SerializedName(value = "event_description", alternate = {"description"})
    private String eventDescription;
    @SerializedName(value = "event_date", alternate = {"event_datetime", "date_time", "datetime"})
    private String eventDateTime;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName(value = "created_by", alternate = {"created_by_membership_id"})
    private String createdByMembershipId;

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public String getEventDateTime() {
        return eventDateTime;
    }

    public void setEventDateTime(String eventDateTime) {
        this.eventDateTime = eventDateTime;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedByMembershipId() {
        return createdByMembershipId;
    }

    public void setCreatedByMembershipId(String createdByMembershipId) {
        this.createdByMembershipId = createdByMembershipId;
    }

    // Backward-compatible aliases.
    public String getId() {
        return getEventId();
    }

    public void setId(String id) {
        setEventId(id);
    }

    public String getTitle() {
        return getEventName();
    }

    public void setTitle(String title) {
        setEventName(title);
    }

    public String getDescription() {
        return getEventDescription();
    }

    public void setDescription(String description) {
        setEventDescription(description);
    }

    public String getDateTime() {
        return getEventDateTime();
    }

    public void setDateTime(String dateTime) {
        setEventDateTime(dateTime);
    }
}
