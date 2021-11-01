import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class BC_Assembler extends CPU {


    // 팀별로 함수 사용

    /*
    * Contributor
    * [A] 정재준
    * [B] 정성현
    * [C] 정태성
    * [D] 정희석
    * [E] 전석균
    * [F] 이하은
    * [G] 남지성
    *
    * */


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

        // [B]
        int org=0;
        int lc=0;
        // 라벨 명령어 주소 I 코멘트
        String[] temp1 = new String[5000];
        // 라벨 | 명령어 | 주소 | I | 코멘트
        String[][] temp2 = new String[5000][5];
        // 기호 | 주소
        String[][] symbolAddress = new String[100][2];



        // [C]
        // MRI 테이블
        String[][] table_MRI = { { "AND", "0000" }, { "ADD", "1000" }, { "LDA", "2000" },
                { "STA", "3000" }, { "BUN", "4000" }, { "BSA", "5000" }, { "ISZ", "6000" } };
        // non MRI 테이블
        String[][] table_non_MRI = { {"CLA", "7800"}, {"CLE", "7400"}, {"CMA", "7200"},
                {"CME", "7100"}, {"CIR", "7080"}, {"CIL", "7040"},
                {"INC", "7020"}, {"SPA", "7010"}, {"SNA", "7008"},
                {"SZA", "7004"}, {"SZE", "7002"}, {"HLT", "7001"},
                {"INP", "F800"}, {"OUT", "F400"}, {"SKI", "F200"},
                {"SKO", "F100"}, {"ION", "F080"}, {"IOF", "F040"} };


        // [A][B]
        // 1. 버퍼리더에 파일 등록, 파일을 temp1에 저장
        int cnt=0;
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            while(true) {
                String line = br.readLine();
                if(line==null ) break;          // 파일의 끝
                if(line.isEmpty()) continue;   // 빈 줄이면 무시하고 다음 줄 읽기
                if(line.trim().charAt(0)=='/') continue; // 주석으로 시작하면 무시하고 다음 줄 읽기
                temp1[cnt++] = line;
            }
            br.close();
        } catch (Exception e) {}



        // [B]
        // 2-1. temp1에서 라벨 필드 구분하여 temp2로 이전
        for(int i=0;i<temp1.length;i++) {
            if(temp1[i] != null) {
                String[] temp = temp1[i].split(",");
                if(temp.length==2) {
                    temp[1] = temp[1].trim(); //temp[1]의 가장 앞부분에 공백이 있으면 제거
                    temp2[i][0] = temp[0];
                    temp1[i] = temp[1];
                } else
                    temp1[i] = temp[0];
            }
        }

        // [B]
        // 2-2. temp1에서 코멘트 필드 구분하여 temp2로 이전
        for(int i=0;i<temp1.length;i++) {
            if(temp1[i] != null) {
                String[] temp = temp1[i].split("/");
                if(temp.length==2) {
                    temp2[i][4] = temp[1];
                    temp1[i] = temp[0];
                } else
                    temp1[i] = temp[0];
            }
        }

        // [B]
        // 2-3. temp1에서 명령어 필드 구분하여 temp2로 이전
        for(int i=0;i<temp1.length;i++) {
            if(temp1[i] != null) {
                String[] temp = temp1[i].split(" ");
                for(int j = 0; j<temp.length; j++) {
                    temp2[i][j+1]=temp[j];
                }
            }
        }

        // [D]
        //명령어 비교할 때 null이 있으면 오류가 발생하므로 문자열 "null"을 넣음
        for(int i = 0; i < temp1.length; i++) {

            if(temp1[i] != null) {
                if (temp1[i].equals("")) temp2[i][1] = "null"; //빈 줄 혹은 주석만 있는 경우
                if(temp1[i].trim().equals("")) temp2[i][1] = "null"; //빈 줄은 아니지만 공백만 있는 경우
            }
        }



        // [B]
        // 예) temp2 출력
        System.out.println("<  temp2 상태  >");
        for(int i=0;i<cnt;i++) {
            for(int j=0;j<5;j++) {
                if(!temp2[i][1].equals("null")) System.out.print("|"+temp2[i][j]+"\t"); //빈 줄은 temp2의 배열을 출력하지 않고 빈 줄만 출력
                else if(temp2[i][4] != null) System.out.print("|"+temp2[i][j]+"\t");
            } System.out.println();
        } System.out.println();



        // 3. First Pass(기호 주소 테이블 등록)
//        int sac=0; // 기호 주소 카운트
//        for(int i=0;i<temp2.length;i++) {
//            if(temp2[i][0] != null) {
//                symbolAddress[sac][0]=temp2[i][0];
//                symbolAddress[sac][1]=Integer.toString(lc);
//                sac++;
//            } else if(temp2[i][1].equals("ORG")) { // ORG 만날 경우 lc 초기화
//                org = Integer.valueOf(temp2[i][2],16);
//                lc = org;
//                continue;
//            } else if(temp2[i][1].equals("END")) // END 만난 경우 종료
//                break;
//            lc++;
//        }

        // [F]
        int sac=0; // 기호 주소 카운트

        for(int i=0;i<temp2.length;i++) {

            if (temp2[i][1].equals("END")) //END 슈도 명령어일 경우
                break;
            else if (temp2[i][1].equals("ORG")) { // ORG 만날 경우 lc 초기화
                org = Integer.valueOf(temp2[i][2],16);
                lc = org;
                continue;
            }
            else if (temp2[i][0] != null) {
                int a = 0; // 기호표 중복 개수

                for(int j=0;j<sac;j++) {//이미 기호표에 존재하는지 확인
                    if(symbolAddress[j][0].equals(temp2[i][0])) {
                        a++;
                    }
                }
                if (a == 0) {
                    symbolAddress[sac][0]=temp2[i][0];
                    symbolAddress[sac][1]=Integer.toString(lc);
                    sac++;
                }
                else {
                    System.out.println("ERROR: 이중 정의되는 기호가 있습니다."); //이미 기호표에 존재 - 이중 정의된 기호임 오류표시, 시스템 종료
                    System.out.println("==어셈블러 종료==");
                    System.exit(a);
                }
            }
            lc++;
        }




        // [B]
        // 예) 기호주소표 출력
        System.out.println("<  기호주소표 상태  >");
        for(int i=0;i<sac;i++) {
            System.out.print("|");
            for(int j=0;j<2;j++) {
                System.out.print(symbolAddress[i][j]+"\t|");
            } System.out.println();
        } System.out.println();

        // [B][C]
        // 4. Second Pass(어셈블리어-기계어 번역)
        lc=0;
        int line_num = 0;
        for(int i=0;i<temp2.length;i++) {
            line_num++;
            if(temp2[i][1].equals("ORG")) { // 4-1. ORG인 경우
                org = Integer.valueOf(temp2[i][2],16);
                lc = org;
                continue;
            } else if(temp2[i][1].equals("END")) { // 4-2. END인 경우
                break;
            } else if(temp2[i][1].equals("DEC") || temp2[i][1].equals("HEX")) { // 4-3. DEC/HEX인 경우
                if(temp2[i][1].equals("DEC")) ;
                if(temp2[i][1].equals("HEX")) temp2[i][2] = Integer.toString(Integer.valueOf(temp2[i][2], 16));
                memory[lc]=(short)Integer.parseInt(temp2[i][2]);
            } else if ( temp2[i][1].equals("AND") || temp2[i][1].equals("ADD") || temp2[i][1].equals("LDA") ||
                    temp2[i][1].equals("STA") || temp2[i][1].equals("BUN") || temp2[i][1].equals("BSA") || temp2[i][1].equals("ISZ") ) { // 4-4. MRI인 경우
                for ( int j = 0; j < 7; j++ ) {
                    if ( temp2[i][1].equals(table_MRI[j][0]) ) {
                        temp2[i][1] = table_MRI[j][1];
                        break;
                    }
                }
                for(int j=0;j<symbolAddress.length;j++) {
                    if(temp2[i][2].equals(symbolAddress[j][0])) {
                        temp2[i][2] = symbolAddress[j][1];
                        break;
                    }
                }
                if(temp2[i][3]==null)
                    temp2[i][3]="0";
                else if(temp2[i][3].equals("I"))
                    temp2[i][3]="8000";

                memory[lc]=(short)(Integer.valueOf(temp2[i][1], 16) +Integer.parseInt(temp2[i][2])+ Integer.valueOf(temp2[i][3], 16));

            } else if ( temp2[i][1].equals("CLA") || temp2[i][1].equals("CLE") || temp2[i][1].equals("CMA") || temp2[i][1].equals("CME") ||
                    temp2[i][1].equals("CIR") || temp2[i][1].equals("CIL") || temp2[i][1].equals("INC") || temp2[i][1].equals("SPA") ||
                    temp2[i][1].equals("SNA") || temp2[i][1].equals("SZA") || temp2[i][1].equals("SZE") || temp2[i][1].equals("HLT") ||
                    temp2[i][1].equals("INP") || temp2[i][1].equals("OUT") || temp2[i][1].equals("SKI") || temp2[i][1].equals("SKO") ||
                    temp2[i][1].equals("ION") || temp2[i][1].equals("IOF") ) {
                for ( int j = 0; j < table_non_MRI.length; j++) { // 4-5. non_MRI인 경우
                    if ( temp2[i][1].equals(table_non_MRI[j][0]) ) {
                        temp2[i][1] = table_non_MRI[j][1];
                        break;
                    }
                }
                memory[lc]=(short)(int)Integer.valueOf(temp2[i][1], 16);
            } else if(temp2[i][1].equals("null")) lc--; //빈 줄과 주석으로만 되어 있는 줄은 코드 오류 처리를 안하고, lc에도 영향을 안줌.
            else {// 4-6. 코드 오류인 경우
                System.out.printf("잘못된 명령어 입력: %d번째 줄의 입력이 잘못되었습니다.\n", line_num);
                System.exit(0); //코드를 잘못 입력한 경우 프로그램을 강제 종료
            }
            lc++;
        }



        // [A]
        // 5. memory 상태 출력
        System.out.println("---저장된 기계어입니다---");
        for(int i=org; i<lc; i++){
            System.out.printf("M[%03X] = %04X\n", i, memory[i]);
        }
        System.out.println("---기계어의 끝입니다---");
        reg_PC = (short) org;
    }


    //////////////////////////////////////////////////////////////////////////////////////




    /*
     * Fetch & Decode 팀
     *
     * 실행 과정에서 오류 판단
     *
     *
     * */


    // [A]
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

        // [A]
        // T2
        ff_I = reg_IR<0;
        reg_AR = (short) (reg_IR & 0x0fff);
        reg_SC++;

        // T3
        // Iooo aaaa aaaa aaaa -> IIII Iooo
        byte opc = (byte)(reg_IR >>> 12);
//        System.out.println(opc);

        // [A][G]
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


    //
    // 이 변수들은 출력을 보조하는 변수들이므로 어셈블러나 Basic Computer에는 영향을 주지 않음.
    static List<short[]> changeMemoryList = new ArrayList<>(); //STA 사용시 변화된 메모리 주소 저장하는 리스트
    //static int[] changeMemory = new int[3]; // 주소 : 이전 값 : 이후 값


    //메모리 참조 명령
    // [G]
    static void AND() {
        // T4
        reg_DR = memory[reg_AR];
        reg_SC++;
        // T5
        reg_AC = (short) (reg_AC&reg_DR);
    }


    // [A]
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

    // [A]
    static void LDA(){
        // T4
        reg_DR = memory[reg_AR];
        reg_SC++;
        // T5
        reg_AC = reg_DR;
    }

    // [A][G]
    static void STA(){
        //메모리 변화 출력
        short[] changeMemory = new short[3];
        changeMemory[0] = reg_AR; // 변화할 메모리 주소
        changeMemory[1] = memory[reg_AR]; //변화할 메모리 이전 값
        changeMemory[2] = reg_AC; //변화할 메모리 이후 값
        changeMemoryList.add(changeMemory); //변화된 메모리 주소 저장
        // T4
        memory[reg_AR] = reg_AC;
    }

    // [G]
    static void BUN() {
        // T4
        reg_PC = reg_AR;
    }

    // [G]
    static void BSA() {
        // T4
        memory[reg_AR++] = reg_PC;
        reg_SC++;
        // T5
        reg_PC = reg_AR;
    }

    // [G]
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

    // [C]
    static void CLA() {
        reg_AC = 0;
    }

    // [C]
    static void CLE() {
        ff_E = false;
    }

    // [C]
    static void CMA() {
        reg_AC = (short) ~reg_AC;
    }

    // [C]
    static void CME() {
        ff_E = !ff_E;
    }

    // [G]
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

    // [G]
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

    // [C]
    static void INC() {
        reg_AC++;
    }

    // [C]
    static void SPA() {
        if (reg_AC > 0 )
            reg_PC++;
    }

    // [C]
    static void SNA() {
        if (reg_AC < 0 )
            reg_PC++;
    }

    // [C]
    static void SZA() {
        if (reg_AC == 0)
            reg_PC++;
    }

    // [C]
    static void SZE() {
        if (!ff_E)
            reg_PC++;
    }

    // [A]
    static void HLT(){
        ff_S = false;
    }

    // 입출력 명령

    // [C]
    static void INP() {
        reg_AC = reg_INPR;
        ff_FGI = false;
    }

    // [C]
    static void OUT() {
        reg_OUTR = (byte) reg_AC;
        ff_FGO = false;
    }

    // [C]
    static void SKI() {
        if (ff_FGI)
            reg_PC++;
    }

    // [C]
    static void SKO() {
        if (ff_FGO)
            reg_PC++;
    }

    // [C]
    static void ION() {
        ff_IEN = true;
    }

    // [C]
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


        // [A] [G]
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

        // [A] [G]
        // 모든 명령이 끝나면 공통으로 SC=0이 됨.
        reg_SC = 0;
        System.out.print("IR\t\tAR\tPC\tDR\t\tAC\t\tTR\t\t");
        System.out.print("I\tS\tE");
        for(short[] m : changeMemoryList)
            System.out.printf("\tM[%03X]", m[0]);
        System.out.println();
        System.out.print(String.format("%04X\t%03X\t%03X\t%04X\t%04X\t%04X\t", reg_IR, reg_AR, reg_PC, reg_DR, reg_AC, reg_TR));
        System.out.print(String.format("%X\t%X\t%X", ff_I?1:0, ff_S?1:0, ff_E?1:0));
        for(short[] m : changeMemoryList)
            System.out.printf("\t%04X", m[2]);
        System.out.println();
    }



    //////////////////////////////////////////////////////////////////////////////////////



    // [A]
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

        // [G]
        System.out.println("--- 변경된 메모리 ---");
        for(short[] i : changeMemoryList) {
            System.out.println(String.format("memory[%03X]: %04X -> %04X", i[0], i[1], i[2]));
        }

        System.out.println("--- 컴퓨터를 종료합니다. ---");


    }



}
