package hcmute.edu.vn.nitrisensebackend.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity @Table(name = "favorite_foods")
@IdClass(FavoriteFoodId.class)
public class FavoriteFood {

    @Id @Column(name = "user_id") private Long userId;
    @Id @Column(name = "food_id") private Long foodId;
    @Column(name = "created_at", insertable = false, updatable = false) private LocalDateTime createdAt;

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getFoodId() {
        return foodId;
    }

    public void setFoodId(Long foodId) {
        this.foodId = foodId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}