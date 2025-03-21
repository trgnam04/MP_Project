package Dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.time.Instant;

// ClickEvent class
public class ClickEvent implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String event = "click";
    private int userId;
    private int itemId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Instant timestamp;

    @JsonCreator
    public ClickEvent(
            @JsonProperty("userId") int userId,
            @JsonProperty("itemId") int itemId,
            @JsonProperty("timestamp") Instant timestamp) {
        this.userId = userId;
        this.itemId = itemId;
        this.timestamp = timestamp;
    }

    // Getters & Setters
    public String getEvent() { return event; }
    public int getUserId() { return userId; }
    public int getItemId() { return itemId; }
    public Instant getTimestamp() { return timestamp; }

    public void setUserId(int userId) { this.userId = userId; }
    public void setItemId(int itemId) { this.itemId = itemId; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
}
