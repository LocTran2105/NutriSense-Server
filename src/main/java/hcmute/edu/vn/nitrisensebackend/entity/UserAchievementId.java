package hcmute.edu.vn.nitrisensebackend.entity;

import java.io.Serializable;
import java.util.Objects;

public class UserAchievementId implements Serializable {

    private Long userId;
    private Long achievementId;

    public UserAchievementId() {}

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UserAchievementId that = (UserAchievementId) o;
        return Objects.equals(userId, that.userId) && Objects.equals(achievementId, that.achievementId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, achievementId);
    }
}