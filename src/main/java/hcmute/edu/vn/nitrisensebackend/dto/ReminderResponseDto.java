package hcmute.edu.vn.nitrisensebackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReminderResponseDto {
    private String notificationText;
    private String detailText;
}