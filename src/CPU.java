public class CPU {
    
    // static: 아무리 생성해도 하나밖에 만들어지지 않음
    protected static short[] memory = new short[4096];

    // 주소 레지스터는 12비트만 사용
    protected static short reg_AR = 0;
    protected static short reg_PC = 0;

    // AC와 DR은 연산의 핵심
    protected static short reg_DR = 0;
    protected static short reg_AC = 0;

    // IR = I + OpCode + AR
    protected static short reg_IR = 0;

    // 임시 레지스터
    protected static short reg_TR = 0;

    // 입출력은 8비트
    protected static byte reg_INPR = 0;
    protected static byte reg_OUTR = 0;

    // 시퀀스 카운터(이 값에 따라 타이밍 값 제공)
    protected static byte reg_SC = 0;

    // 1 = true, 0 = false
    protected static boolean ff_I = false;

    // S FF: HLT 명령어에서 S<-0이면 컴퓨터 종료이므로 기본값 1
    protected static boolean ff_S = true;

    // AC Carry FF
    protected static boolean ff_E = false;

    // Interrupt FF
    protected static boolean ff_R = false;

    // Interrupt Enable FF
    // 교수님 피드백대로 IEN = 1로 시작
    protected static boolean ff_IEN = true;

    // Flag Input/Output FF
    protected static boolean ff_FGI = false;
    protected static boolean ff_FGO = false;


    public static void reboot(){
        System.out.println("--- 컴퓨터를 재부팅합니다. ---");
        memory = new short[4096];
        reg_AR = 0;
        reg_PC = 0;
        reg_DR = 0;
        reg_AC = 0;
        reg_IR = 0;
        reg_TR = 0;
        reg_INPR = 0;
        reg_OUTR = 0;
        reg_SC = 0;
        ff_I = false;
        ff_S = true;
        ff_E = false;
        ff_R = false;
        ff_IEN = true;
        ff_FGI = false;
        ff_FGO = false;
    }
    
    
}
