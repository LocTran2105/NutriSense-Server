package hcmute.edu.vn.nitrisensebackend.entity;

import java.io.Serializable;
import java.util.Objects;

public class FavoriteFoodId implements Serializable {

    private Long userId;
    private Long foodId;

    public FavoriteFoodId() {}

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        FavoriteFoodId that = (FavoriteFoodId) o;
        return Objects.equals(userId, that.userId) && Objects.equals(foodId, that.foodId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, foodId);
    }
}