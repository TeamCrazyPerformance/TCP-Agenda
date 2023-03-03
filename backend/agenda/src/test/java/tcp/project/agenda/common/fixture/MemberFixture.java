package tcp.project.agenda.common.fixture;

import tcp.project.agenda.member.domain.Member;

public class MemberFixture {

    public static final String REPRESENTATIVE_NAME = "대표 이름";
    public static final String REPRESENTATIVE_UNIV_ID = "17000000";
    public static final String REPRESENTATIVE_PASSWORD = "970101";
    public static final String REPRESENTATIVE_GRADE = "감투";

    public static Member getRepresentativeMember() {
        return new Member(REPRESENTATIVE_NAME, REPRESENTATIVE_UNIV_ID, REPRESENTATIVE_PASSWORD, REPRESENTATIVE_GRADE);
    }

    public static final String REGULAR_NAME = "정회원 이름";
    public static final String REGULAR_UNIV_ID = "18000000";
    public static final String REGULAR_PASSWORD = "980101";
    public static final String REGULAR_GRADE = "정회원";

    public static Member getRegularMember() {
        return new Member(REGULAR_NAME, REGULAR_UNIV_ID, REGULAR_PASSWORD, REGULAR_GRADE);
    }

    public static final String GENERAL_NAME = "회원 이름";
    public static final String GENERAL_UNIV_ID = "19000000";
    public static final String GENERAL_PASSWORD = "990101";
    public static final String GENERAL_GRADE = "회원";

    public static Member getGeneralMember() {
        return new Member(GENERAL_NAME, GENERAL_UNIV_ID, GENERAL_PASSWORD, GENERAL_GRADE);
    }

}
