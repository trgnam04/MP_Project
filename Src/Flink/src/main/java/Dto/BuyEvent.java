package Dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.Serializable;
import java.time.Instant;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class BuyEvent implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String event = "buy";
    private int userId;
    private int itemId;
    private double price;
    private int quantity;
    private Instant timestamp;

    @JsonCreator
    public BuyEvent(
            @JsonProperty("userId") int userId,
            @JsonProperty("itemId") int itemId,
            @JsonProperty("price") double price,
            @JsonProperty("quantity") int quantity,
            @JsonProperty("timestamp") Instant timestamp) {
        this.userId = userId;
        this.itemId = itemId;
        this.price = price;
        this.quantity = quantity;
        this.timestamp = timestamp;
    }

    // Getters & Setters
    public String getEvent() { return event; }
    public int getUserId() { return userId; }
    public int getItemId() { return itemId; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public Instant getTimestamp() { return timestamp; }

    public void setUserId(int userId) { this.userId = userId; }
    public void setItemId(int itemId) { this.itemId = itemId; }
    public void setPrice(double price) { this.price = price; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
}
