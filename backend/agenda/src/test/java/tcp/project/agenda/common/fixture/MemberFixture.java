package tcp.project.agenda.common.fixture;

import tcp.project.agenda.member.domain.Member;

public class MemberFixture {

    public static final String EXECUTIVE_NAME = "대표 이름";
    public static final String EXECUTIVE_UNIV_ID = "17000000";
    public static final String EXECUTIVE_PASSWORD = "970101";

    public static Member getExecutiveMember() {
        return new Member(EXECUTIVE_NAME, EXECUTIVE_UNIV_ID, EXECUTIVE_PASSWORD);
    }

    public static final String REGULAR_NAME = "정회원 이름";
    public static final String REGULAR_UNIV_ID = "18000000";
    public static final String REGULAR_PASSWORD = "980101";

    public static Member getRegularMember() {
        return new Member(REGULAR_NAME, REGULAR_UNIV_ID, REGULAR_PASSWORD);
    }

    public static final String GENERAL_NAME = "회원 이름";
    public static final String GENERAL_UNIV_ID = "19000000";
    public static final String GENERAL_PASSWORD = "990101";

    public static Member getGeneralMember() {
        return new Member(GENERAL_NAME, GENERAL_UNIV_ID, GENERAL_PASSWORD);
    }

    public static final String EXECUTIVE_GENERAL_NAME = "휴학 대표 이름";
    public static final String EXECUTIVE_GENERAL_UNIV_ID = "16000000";
    public static final String EXECUTIVE_GENERAL_PASSWORD = "960101";

    public static Member getExecutiveGeneralMember() {
        return new Member(EXECUTIVE_GENERAL_NAME, EXECUTIVE_GENERAL_UNIV_ID, EXECUTIVE_GENERAL_PASSWORD);
    }
}
