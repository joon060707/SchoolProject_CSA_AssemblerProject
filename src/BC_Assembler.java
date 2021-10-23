import java.io.BufferedReader;
import java.io.FileReader;

public class BC_Assembler extends CPU {


    // 팀별로 함수 사용


    /*
    * 코드 입력(어셈블러) 팀
    *
    * + 주소 기호 테이블
    * + 코드에 주석 추가
    * + 어셈블리 코드 주석 제거
    * + 첫 줄 이외의 ORG 처리
    *
    * */

    private static void runAssembler(String file) {
        int cnt=0;
        int org;
        int lc;
        String[] temp1 = new String[4096];
        String[][] temp2 = new String[4096][4];
        // 라벨필드 | 명령어 | 주소 | I

        try {
            // 1-1. 버퍼리더에 파일 등록, 파일을 temp1에 저장, 달성 후 cnt = 명령어+1
            BufferedReader br = new BufferedReader(new FileReader(file));
            while(true) {
                String line = br.readLine();
                if(line==null) break;
                temp1[cnt++] = line;
            }
            br.close();


            // 2-1. temp1을 4열짜리 temp2로 이전
            for(int i=0;i<temp1.length;i++) {
                if(temp1[i] != null) {
                    String[] temp3 = temp1[i].split(" ");
                    for(int j=0;j<temp3.length;j++) {
                        temp2[i][j]=temp3[j];
                    }
                }
            }
            // 2-2. 라벨 주소 배열 라인 맞춰주기
            for(int i=0;i<temp2.length;i++) {
                if(temp1[i] != null) {
                    if(!(temp2[i][0].contains(","))) {
                        temp2[i][3]=temp2[i][2];
                        temp2[i][2]=temp2[i][1];
                        temp2[i][1]=temp2[i][0];
                        temp2[i][0]=null;
                    }
                }
            }
            // 2-3. 라벨 주소 실제 주소 대입하기
            for(int i=0;i<temp2.length;i++) {
                for(int j=0;j<temp2.length;j++) {
                    if(temp2[i][0]!=null && temp2[j][2]!=null) {
                        if(temp2[i][0].contains(temp2[j][2]))
                            temp2[j][2]=Integer.toString(i+Integer.parseInt(temp2[0][2])-1);
                    }
                }
            }
            // 2-4. 명령어 숫자로 변환하기
            for(int i=0;i<temp2.length;i++) {
                if(temp2[i][1]!=null) {
                    if(temp2[i][1].equals("AND")) temp2[i][1]="0";
                    if(temp2[i][1].equals("ADD")) temp2[i][1]="4096";
                    if(temp2[i][1].equals("LDA")) temp2[i][1]="8192";
                    if(temp2[i][1].equals("STA")) temp2[i][1]="12288";
                    if(temp2[i][1].equals("BUN")) temp2[i][1]="16384";
                    if(temp2[i][1].equals("BSA")) temp2[i][1]="20480";
                    if(temp2[i][1].equals("ISZ")) temp2[i][1]="24576";
                    if(temp2[i][1].equals("CLA")) temp2[i][1]="30720";
                    if(temp2[i][1].equals("CLE")) temp2[i][1]="29696";
                    if(temp2[i][1].equals("CMA")) temp2[i][1]="29184";
                    if(temp2[i][1].equals("CME")) temp2[i][1]="28928";
                    if(temp2[i][1].equals("CIR")) temp2[i][1]="28800";
                    if(temp2[i][1].equals("CIL")) temp2[i][1]="28736";
                    if(temp2[i][1].equals("INC")) temp2[i][1]="28704";
                    if(temp2[i][1].equals("SPA")) temp2[i][1]="28688";
                    if(temp2[i][1].equals("SNA")) temp2[i][1]="28680";
                    if(temp2[i][1].equals("SZA")) temp2[i][1]="28676";
                    if(temp2[i][1].equals("SZE")) temp2[i][1]="28674";
                    if(temp2[i][1].equals("HLT")) temp2[i][1]="28673";
                    if(temp2[i][1].equals("INP")) temp2[i][1]="63488";
                    if(temp2[i][1].equals("OUT")) temp2[i][1]="62464";
                    if(temp2[i][1].equals("SKI")) temp2[i][1]="61952";
                    if(temp2[i][1].equals("SKO")) temp2[i][1]="61696";
                    if(temp2[i][1].equals("ION")) temp2[i][1]="61568";
                    if(temp2[i][1].equals("IOF")) temp2[i][1]="61504";
                    // 슈도 명령어
                    if(temp2[i][1].equals("DEC")) temp2[i][1]="0";
                    if(temp2[i][1].equals("HEX")) temp2[i][1]="0";
                }
            }
            // 2-5. 주소 null 0으로 변환
            for(int i=0;i<temp2.length;i++) {
                if(temp2[i][2]==null) {
                    temp2[i][2]="0";
                }
            }
            // 2-6. I 변환
            for(int i=0;i<temp2.length;i++) {
                if(temp2[i][3]==null) {
                    temp2[i][3]="0";
                } else if(temp2[i][3].equals("I"))
                    temp2[i][3]="28672";
            }


            // 3-1. ORG 값대로 location counter 초기화
            org=Integer.parseInt(temp2[0][2]);
            lc=org;
            // 3-2. temp2의 값 memory로 이전
            for(int i = 0 ; !(temp2[i+1][1].equals("END")) ;i++) {
                memory[i+org] = (short)(Integer.parseInt(temp2[i+1][1]) + Integer.parseInt(temp2[i+1][2]) + Integer.parseInt(temp2[i+1][3]));
                lc++;
            }
            // 3-3. memory 상태 출력
            System.out.println("---저장된 기계어입니다---");
            for(int i=org; i<lc; i++){
                System.out.printf("M[%03X] = %04X\n", i, memory[i]);
            }
            System.out.println("---기계어의 끝입니다---");

            reg_PC = (short) org;


        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    //////////////////////////////////////////////////////////////////////////////////////




    /*
     * Fetch & Decode 팀
     *
     * 실행 과정에서 오류 판단
     *
     *
     * */


    static void fetch(){
        // T0
        reg_AR = reg_PC;
        reg_SC++;
        // T1
        reg_IR = memory[reg_AR];
        reg_PC++;
        reg_SC++;
        System.out.printf("--- Memory[%03X]: %04X 실행 ---\n", reg_AR, reg_IR);
    }

    // decode 함수에서 해석된 임시 디코드 문자열
    static String decodedInstruction;

    static void decode(){
        // T2
        ff_I = reg_IR<0;
        reg_AR = (short) (reg_IR & 0x0fff);
        reg_SC++;

        // T3
        // Iooo aaaa aaaa aaaa -> IIII Iooo
        byte opc = (byte)(reg_IR >>> 12);
//        System.out.println(opc);

        if(opc == 7){      // 레지스터 참조 명령
            switch (reg_IR){
                case 0x7800: decodedInstruction = "CLA"; break;
                case 0x7400: decodedInstruction = "CLE"; break;
                case 0x7200: decodedInstruction = "CMA"; break;
                case 0x7100: decodedInstruction = "CME"; break;
                case 0x7080: decodedInstruction = "CIR"; break;
                case 0x7040: decodedInstruction = "CIL"; break;
                case 0x7020: decodedInstruction = "INC"; break;
                case 0x7010: decodedInstruction = "SPA"; break;
                case 0x7008: decodedInstruction = "SNA"; break;
                case 0x7004: decodedInstruction = "SZA"; break;
                case 0x7002: decodedInstruction = "SZE"; break;
                case 0x7001: decodedInstruction = "HLT"; break;
                default:  decodedInstruction = "ERR";
            }
        }
        else if(opc == -1){ //입출력 명령
            switch (reg_IR){
                case (short)0xF800: decodedInstruction = "INP"; break;
                case (short)0xF400: decodedInstruction = "OUT"; break;
                case (short)0xF200: decodedInstruction = "SKI"; break;
                case (short)0xF100: decodedInstruction = "SKO"; break;
                case (short)0xF080: decodedInstruction = "ION"; break;
                case (short)0xF040: decodedInstruction = "IOF"; break;
                default: decodedInstruction = "ERR";
            }
        }else {	//메모리 참조 명령

            if(ff_I) reg_AR = (short) (memory[reg_AR] & 0x0fff);
            reg_SC++;

            switch (opc){
                case 0, -8: decodedInstruction = "AND"; break;
                case 1, -7: decodedInstruction = "ADD"; break;
                case 2, -6: decodedInstruction = "LDA"; break;
                case 3, -5: decodedInstruction = "STA"; break;
                case 4, -4: decodedInstruction = "BUN"; break;
                case 5, -3: decodedInstruction = "BSA"; break;
                case 6, -2: decodedInstruction = "ISZ"; break;
                default:  decodedInstruction = "ERR";
            }
        }


        System.out.printf("해석 결과: %s %s %s\n", decodedInstruction, (reg_IR & 0x00007000) >> 12 == 7 ? "" : String.format("%03X", reg_IR & 0x00000fff), ff_I?"I":"");

    }


    //////////////////////////////////////////////////////////////////////////////////////



    /*
     * Instruction Set 팀
     *
     * + 실행 과정에서 문제 없는지 판단
     *
     *
     * */

    //메모리 참조 명령
    static void AND() {
        // T4
        reg_DR = memory[reg_AR];
        reg_SC++;
        // T5
        reg_AC = (short) (reg_AC&reg_DR);
    }


    static void ADD(){
        // T4
        reg_DR = memory[reg_AR];
        reg_SC++;
        // T5
        // 올바르게 계산되기 위해 사용되지 않는 부분을 클리어
        int temp = (reg_AC & 0x0000ffff) + (reg_DR & 0x0000ffff);
//        System.out.println(Integer.toBinaryString(temp));
        ff_E = temp >>> 16 == 1;  // 둘 다 음수인 경우 E에 저장됨. (오버플로우)
        reg_AC += reg_DR;
    }


    static void LDA(){
        // T4
        reg_DR = memory[reg_AR];
        reg_SC++;
        // T5
        reg_AC = reg_DR;
    }

    static void STA(){
        // T4
        memory[reg_AR] = reg_AC;
    }

    static void BUN() {
        // T4
        reg_PC = reg_AR;
    }


    static void BSA() {
        // T4
        memory[reg_AR++] = reg_PC;
        reg_SC++;
        // T5
        reg_PC = reg_AR;
    }

    static void ISZ() {
        //T4
        reg_DR = memory[reg_AR];
        reg_SC++;
        //T5
        reg_DR++;
        reg_SC++;
        //T6
        memory[reg_AR] = reg_DR;
        if(reg_DR == 0) {
            reg_PC++;
        }
    }






    //레지스터 참조 명령
    static void CLA() {
        reg_AC = 0;
    }

    static void CLE() {
        ff_E = false;
    }

    static void CMA() {
        reg_AC = (short) ~reg_AC;
    }

    static void CME() {
        ff_E = !ff_E;
    }

    static void CIR() {
        short temp = (short)(reg_AC & 0x0001);
        //AC >> 1
        reg_AC = (short)(reg_AC >> 1);
        //AC(15) << E
        if(ff_E) {
            reg_AC = (short)(reg_AC | 0x8000);
        }else {
            reg_AC = (short)(reg_AC & 0x7FFF);
        }
        //E << AC(0)
        ff_E = temp == 1;


    }

    static void CIL() {
        short temp = (short)(reg_AC & 0x8000);
        //AC << 1
        reg_AC = (short)(reg_AC << 1);
        //AC(0) < E
        if(ff_E) {
            reg_AC = (short)(reg_AC | 0x0001);
        }else {
            reg_AC = (short)(reg_AC & 0xFFFE);
        }
        //E < AC(15)
        ff_E = temp == (short) 0x8000;
    }

    static void INC() {
        reg_AC++;
    }

    static void SPA() {
        if (reg_AC > 0 )
            reg_PC++;
    }

    static void SNA() {
        if (reg_AC < 0 )
            reg_PC++;
    }

    static void SZA() {
        if (reg_AC == 0)
            reg_PC++;
    }

    static void SZE() {
        if (!ff_E)
            reg_PC++;
    }
    static void HLT(){
        ff_S = false;
    }

    // 입출력 명령
    static void INP() {
        reg_AC = reg_INPR;
        ff_FGI = false;
    }

    static void OUT() {
        reg_OUTR = (byte) reg_AC;
        ff_FGO = false;
    }

    static void SKI() {
        if (ff_FGI)
            reg_PC++;
    }

    static void SKO() {
        if (ff_FGO)
            reg_PC++;
    }

    static void ION() {
        ff_IEN = true;
    }

    static void IOF() {
        ff_IEN = false;
    }


    //////////////////////////////////////////////////////////////////////////////////////





    /*
     * Execution 팀
     *
     * + 실행시 참조된 메모리들도 출력되도록 수정
     *
     * */
    static void execute(){


        switch (decodedInstruction){
            case "AND": AND(); break;
            case "ADD": ADD(); break;
            case "LDA": LDA(); break;
            case "STA": STA(); break;
            case "BUN": BUN(); break;
            case "BSA": BSA(); break;
            case "ISZ": ISZ(); break;

            case "CLA": CLA(); break;
            case "CLE": CLE(); break;
            case "CMA": CMA(); break;
            case "CME": CME(); break;
            case "CIR": CIR(); break;
            case "CIL": CIL(); break;
            case "INC": INC(); break;
            case "SPA": SPA(); break;
            case "SNA": SNA(); break;
            case "SZA": SZA(); break;
            case "SZE": SZE(); break;
            case "HLT": HLT(); break;

            case "INP": INP(); break;
            case "OUT": OUT(); break;
            case "SKI": SKI(); break;
            case "SKO": SKO(); break;
            case "ION": ION(); break;
            case "IOF": IOF(); break;
            default: break;
        }
        // 모든 명령이 끝나면 공통으로 SC=0이 됨.
        reg_SC = 0;
        System.out.print("IR\t\tAR\tPC\tDR\t\tAC\t\tTR\t\t");
        System.out.println("I\tS\tE");
        System.out.print(String.format("%04X\t%03X\t%03X\t%04X\t%04X\t%04X\t", reg_IR, reg_AR, reg_PC, reg_DR, reg_AC, reg_TR));
        System.out.println(String.format("%X\t%X\t%X", ff_I?1:0, ff_S?1:0, ff_E?1:0));
    }



    //////////////////////////////////////////////////////////////////////////////////////



    public static void main(String[] args) {

        // 메인 함수는 어셈블러 실행 - {fetch - decode - execute}로만 구성. 나머지 작업은 다른 곳에서.
        runAssembler("src/Assembly.txt");

        System.out.println("--- 명령어 실행 시작 ---");
        System.out.print("IR\t\tAR\tPC\tDR\t\tAC\t\tTR\t\t");
        System.out.println("I\tS\tE");
        System.out.print(String.format("%04X\t%03X\t%03X\t%04X\t%04X\t%04X\t", reg_IR, reg_AR, reg_PC, reg_DR, reg_AC, reg_TR));
        System.out.println(String.format("%X\t%X\t%X", ff_I?1:0, ff_S?1:0, ff_E?1:0));


        while (ff_S){       // start-stop flip-flop이 1일 때만 작동.
             fetch();
             decode();
             execute();
        }
        System.out.println("--- 명령어 실행 끝 ---");
        System.out.println("--- 컴퓨터를 종료합니다. ---");


    }



}
