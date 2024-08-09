package com.nexters.goalpanzi.fixture;

import com.nexters.goalpanzi.domain.mission.DayOfWeek;
import com.nexters.goalpanzi.domain.mission.InvitationCode;
import com.nexters.goalpanzi.domain.mission.Mission;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static com.nexters.goalpanzi.domain.mission.DayOfWeek.*;
import static com.nexters.goalpanzi.fixture.MemberFixture.MEMBER_ID;

public class MissionFixture {
    public static final String DESCRIPTION = "운동하기";
    public static final Integer BOARD_COUNT = 30;

    public static final List<DayOfWeek> WEEK = List.of(MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY);
    public static final MultipartFile IMAGE_FILE;
    public static final String UPLOADED_IMAGE_URL = "uploadedImageUrl";

    public static Mission create() {
        return new Mission(
                MEMBER_ID,
                DESCRIPTION,
                InvitationCode.generate(),
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(10),
                "10:00",
                "12:00",
                List.of(MONDAY),
                10
        );
    }

    static {
        try {
            File tempFile = File.createTempFile("미션 인증 이미지", ".jpg");
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                fos.write("이미지 파일 내용".getBytes());
            }
            IMAGE_FILE = new MockMultipartFile("imageFile", tempFile.getName(), "image/jpeg", new FileInputStream(tempFile));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
