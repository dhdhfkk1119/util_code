package com.nationwide.nationwide_server.core.session;


import com.nationwide.nationwide_server.member.Gender;
import com.nationwide.nationwide_server.member.Member;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SessionUser {
    private Long id;
    private String username;
    private String nickName;
    private Gender gender;
    private String profileImage;

    public static SessionUser fromUser(Member member) {
        return new SessionUser(
                member.getId(),
                member.getUsername(),
                member.getNickName(),
                member.getGender(),
                member.getProfileImage()
        );
    }
}
