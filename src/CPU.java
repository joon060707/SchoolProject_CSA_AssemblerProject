public class CPU {
    
    // static: 아무리 생성해도 하나밖에 만들어지지 않음
    protected static short[] memory = new short[4096];

    // 주소 레지스터는 12비트만 사용
    protected static short Reg_AR = 0;
    protected static short Reg_PC = 0;

    // AC와 DR은 연산의 핵심
    protected static short Reg_DR = 0;
    protected static short Reg_AC = 0;

    // IR = I + OpCode + AR
    protected static short Reg_IR = 0;

    // 임시 레지스터
    protected static short Reg_TR = 0;

    // 입출력은 8비트
    protected static byte Reg_INPR = 0;
    protected static byte Reg_OUTR = 0;

    // 시퀀스 카운터(이 값에 따라 타이밍 값 제공)
    protected static byte Reg_SC = 0;

    // 1 = true, 0 = false
    protected static boolean FF_I = false;

    // S FF: HLT 명령어에서 S<-0이면 컴퓨터 종료이므로 기본값 1
    protected static boolean FF_S = true;

    // AC Carry FF
    protected static boolean FF_E = false;

    // Interrupt FF
    protected static boolean FF_R = false;

    // Interrupt Enable FF
    protected static boolean FF_IEN = false;

    // Flag Input/Output FF
    protected static boolean FF_FGI = false;
    protected static boolean FF_FGO = false;
    
    
}
