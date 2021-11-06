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

        System.out.println("--- 어셈블러 실행 시작 ---");

        // [A][B]
        int[] orgList = new int[10];
        orgList[0] = -1; // Valid ORG flag
        int orgCount = 0;
        // int org=0;      // ORG
        int lc=0;       // LC

        // Assembly Code original
        // 라벨 명령어 주소 I 코멘트
        String[] AC_original = new String[4096];

        // Assembly Code split
        // 라벨 | 명령어 | 주소 | I | 코멘트
        String[][] AC_split = new String[4096][5];

        // Address-Symbol table
        // 기호 | 주소
        String[][] AS_table = new String[2048][2];

        // AssemblyLine 클래스
        // 라벨 , 명령어 , 주소 , I , 코멘트
        class AssemblyLine {
            final String label; String command; String address; String indirect; final String comment;
            AssemblyLine(String[] line) {
                label=line[0];
                command=line[1];
                address=line[2];
                indirect=line[3];
                comment=line[4];
            }

            void printLabel(){
                System.out.printf("%6s\t|%6s\t|%6s\t|%6s\t|%6s\n", "Sym", "Inst", "Addr", "Indir", "Cmt");
            }

            void print(){
                System.out.printf("%6s\t|",label);
                System.out.printf("%6s\t|",command);
                System.out.printf("%6s\t|",address);
                System.out.printf("%6s\t|",indirect);
                System.out.printf("%6s\n",comment);
            }
        }
        // AssemblyLine 객체배열 assLine
        AssemblyLine[] assLine = new AssemblyLine[4096];


        // [C]
        // MRI 테이블
        String[][] table_MRI = {
                { "AND", "0000" }, { "ADD", "1000" }, { "LDA", "2000" },
                { "STA", "3000" }, { "BUN", "4000" }, { "BSA", "5000" }, { "ISZ", "6000" }
        };
        // non MRI 테이블
        String[][] table_non_MRI = {
                {"CLA", "7800"}, {"CLE", "7400"}, {"CMA", "7200"},
                {"CME", "7100"}, {"CIR", "7080"}, {"CIL", "7040"},
                {"INC", "7020"}, {"SPA", "7010"}, {"SNA", "7008"},
                {"SZA", "7004"}, {"SZE", "7002"}, {"HLT", "7001"},
                {"INP", "F800"}, {"OUT", "F400"}, {"SKI", "F200"},
                {"SKO", "F100"}, {"ION", "F080"}, {"IOF", "F040"}
        };


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
                AC_original[cnt++] = line;
            }
            br.close();
        } catch (Exception e) {}



        // [B] [D]
        // 2-1. temp1에서 라벨 필드 구분하여 temp2로 이전
        for(int i=0;i<AC_original.length;i++) {
            if(AC_original[i] != null) {
                String[] commentText = AC_original[i].split("/"); //주석에 ,(콤마)가 들어가는 경우는 따로 분리
                String nonComment = commentText[0].trim();
                String[] symbolSplit =  nonComment.split(",");
                if(symbolSplit.length==2) {
                    symbolSplit[1] = symbolSplit[1].trim(); //temp[1]의 가장 앞부분에 공백이 있으면 제거
                    AC_split[i][0] = symbolSplit[0];
                    AC_original[i] = symbolSplit[1];
                } else {
                    if(nonComment.charAt(nonComment.length() - 1) == ',') {//라벨만 입력된 경우
                        AC_split[i][0] = symbolSplit[0];
                        AC_original[i] = "-";
                        continue;
                    }
                    AC_original[i] = symbolSplit[0];
                }
                if(commentText.length == 2) AC_original[i] += "/" + commentText[1];
            }
        }

        // [B]
        // 2-2. temp1에서 코멘트 필드 구분하여 temp2로 이전
        for(int i=0;i<AC_original.length;i++) {
            if(AC_original[i] != null) {
                String[] commentSplit = AC_original[i].split("/");
                if(commentSplit.length==2) {
                    AC_split[i][4] = commentSplit[1];
                    AC_original[i] = commentSplit[0];
                } else
                    AC_original[i] = commentSplit[0];
            }
        }

        // [B]
        // 2-3. temp1에서 명령어 필드 구분하여 temp2로 이전
        for(int i=0;i<AC_original.length;i++) {
            if(AC_original[i] != null) {
                String[] instSplit = AC_original[i].split(" ");
                for(int j = 0; j<instSplit.length; j++) {
                    AC_split[i][j+1]=instSplit[j];
                }
            }
        }

        // [D]
        //명령어 비교할 때 null이 있으면 오류가 발생하므로 문자열 "null"을 넣음
        for(int i = 0; i < AC_original.length; i++) {

            if(AC_original[i] != null) {
                if (AC_original[i].equals("")) AC_split[i][1] = "null"; //빈 줄 혹은 주석만 있는 경우
                if(AC_original[i].trim().equals("")) AC_split[i][1] = "null"; //빈 줄은 아니지만 공백만 있는 경우
            }
        }



        // [B]
        // 2-4. temp2 -> assLine 전환
        for(int i=0;i<assLine.length;i++) {
            assLine[i] = new AssemblyLine(AC_split[i]);
        }

        // [A] [B]
        // 예) assLine 출력
        System.out.println("<  어셈블리어 분석 결과  >");
        assLine[0].printLabel();
        for(int i=0;i<cnt;i++) assLine[i].print();
         System.out.println();


         // 첫 줄이 ORG가 아닌 경우 ORG 0이 있다고 가정하고 orgCount 1 증가, 첫 org=0 대입.
         if(!assLine[0].command.equals("ORG")){
             orgList[0] = 0;
             orgCount++;
         }



        // [F]
        //3. First Pass(주소-기호 테이블 등록)
        int AS_table_size=0; // 기호 주소 카운트

        // Scan next line of code
        for(int i=0;i<assLine.length;i++) {

            // Label?
            if (assLine[i].label != null) {

                // Store symbol in address-symbol table together with value of LC
                int a = 0; // 기호표 중복 개수

                for(int j=0;j<AS_table_size;j++) {//이미 기호표에 존재하는지 확인
                    if(AS_table[j][0].equals(assLine[i].label)) {
                        a++;
                    }
                }
                if (a == 0) {
                    AS_table[AS_table_size][0]=assLine[i].label;
                    AS_table[AS_table_size][1]=Integer.toString(lc);
                    AS_table_size++;
                }
                else {
                    System.out.println("ERROR: 이중 정의되는 기호가 있습니다."); //이미 기호표에 존재 - 이중 정의된 기호임 오류표시, 시스템 종료
                    System.out.println("==어셈블러 종료==");
                    System.exit(a);
                }
            }
            // ORG?
            else if (assLine[i].command.equals("ORG")) { // ORG 만날 경우 lc 초기화
                orgList[orgCount] = Integer.valueOf(assLine[i].address,16);
                // Set LC
                lc = orgList[orgCount++];
                continue;
            }
            // END?
            else if (assLine[i].command.equals("END")) //END 슈도 명령어일 경우
                break;

            // Increment LC
            lc++;
        }

        // [B]
        // 예) 주소-기호 테이블 출력
        System.out.println("<  주소-기호 테이블 상태  >");
        System.out.printf("|%5s\t|%5s\t|\n", "Sym", "Addr");
        for(int i=0;i<AS_table_size;i++) {
            System.out.print("|");
            System.out.printf("%5s\t|%4X\t|\n", AS_table[i][0], Integer.parseInt(AS_table[i][1]));
        } System.out.println();







        // [B][C][D]
        // 4. Second Pass(어셈블리어-기계어 번역)
        lc=0;
        int line_num = 0;
        orgCount = 0;        // reset for re-count


        // 첫 줄이 ORG가 아닌 경우 ORG 0이 있다고 가정하고 orgCount 1 증가, 첫 org=0 대입.
        if(!assLine[0].command.equals("ORG")){
            orgList[0] = 0;
            orgCount++;
        }

        for(int i=0;i<assLine.length;i++) {
            line_num++;

            // Pseudo-instruction?
            if(multiEquals(assLine[i].command, new String[][]{{"ORG"},{"END"},{"DEC"},{"HEX"},{"-"}})){//"-"는 라벨만 입력된 줄
                // ORG?
                if(assLine[i].command.equals("ORG")) { // 4-1. ORG인 경우
                    orgList[orgCount] = Integer.valueOf(assLine[i].address,16);
                    lc = orgList[orgCount++];
                    continue;
                }
                // END?
                else if(assLine[i].command.equals("END")) { // 4-2. END인 경우
                    break;
                }
                // then this is DEC or HEX
                // Convert operand to binary and store in location given by LC
                else if(assLine[i].command.equals("DEC") || assLine[i].command.equals("HEX")) { // 4-3. DEC/HEX인 경우 (DEC는 그대로)
                    if(assLine[i].command.equals("HEX"))
                        assLine[i].address = Integer.toString(Integer.valueOf(assLine[i].address, 16));
                    memory[lc]=(short)Integer.parseInt(assLine[i].address);
                }
            }
            // then this is normal instruction
            else if (multiEquals(assLine[i].command, table_MRI)) { // 4-4. MRI인 경우

                // Get operation code and set bits 2-4
                for ( int j = 0; j < table_MRI.length; j++ ) {
                    if ( assLine[i].command.equals(table_MRI[j][0]) ) {
                        assLine[i].command = table_MRI[j][1];
                        break;
                    }
                }

                // Search address-symbol table for binary equivalent of symbolic address and set bits 5-16
                for(int j=0;j<AS_table.length;j++) {
                    if(assLine[i].address.equals(AS_table[j][0])) {
                        assLine[i].address = AS_table[j][1];
                        break;
                    }
                }

                // I?
                if(assLine[i].indirect==null)
                    assLine[i].indirect="0";    // set first bit to 0
                else if(assLine[i].indirect.equals("I"))
                    assLine[i].indirect="8000";     // set first bit to 1

                // Assembly all parts of binary instruction and store in location given by LC
                memory[lc]=(short)(Integer.valueOf(assLine[i].command, 16) +Integer.parseInt(assLine[i].address)+ Integer.valueOf(assLine[i].indirect, 16));

                // Valid non-MRI?
            } else if (multiEquals(assLine[i].command, table_non_MRI)) {    // 4-5. non_MRI인 경우

                for ( int j = 0; j < table_non_MRI.length; j++) {
                    if ( assLine[i].command.equals(table_non_MRI[j][0]) ) {
                        assLine[i].command = table_non_MRI[j][1];
                        break;
                    }
                }
                // Store binary equivalent of instruction in location given by LC
                memory[lc]=(short)(int)Integer.valueOf(assLine[i].command, 16);
            } else if(assLine[i].command.equals("null")) lc--; //빈 줄과 주석으로만 되어 있는 줄은 코드 오류 처리를 안하고, lc에도 영향을 안줌.

            // Error in line of code
            else {// 4-6. 코드 오류인 경우
                System.out.printf("잘못된 명령어 입력: %d번째 줄의 입력이 잘못되었습니다.\n", line_num);
                System.exit(0); //코드를 잘못 입력한 경우 프로그램을 강제 종료
            }
            // Increment LC
            lc++;
        }







        // [A]
        // 5. memory 상태 출력
        System.out.println("---저장된 기계어입니다---");

        for(int i=orgList[0]; i<lc; i++){
            if( contains(i, AS_table) || memory[i]!=0)    // 메모리 값이 0이 아니거나 주소 기호 테이블에 포함된 경우에만 출력
                System.out.printf("M[%03X] = %04X\n", i, memory[i]);
        }
        System.out.println("---기계어의 끝입니다---");
        reg_PC = (short) orgList[0];
    }


    //////////////////////////////////////////////////////////////////////////////////////

    // [A]
    // 해당 명령어가 각 테이블에 해당하는지 판단하여 반환하는 함수
    public static boolean multiEquals(String key, String[][] table){

        boolean equals = false;
        for(String[] inst : table) equals |= inst[0].equals(key);
        return equals;
    }

    // [A]
    // 해당 주소가 주소-기호 테이블에 들어 있는지 판단하여 반환하는 함수
    public static boolean contains(int key, String[][] table){
        boolean equals = false;
        for(String[] addr : table) {
            if(addr[1]==null) continue;
            equals |= Integer.parseInt(addr[1]) == key;
        }
        return equals;
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

    // 이 변수들은 출력을 보조하는 변수들이므로 어셈블러나 Basic Computer에는 영향을 주지 않음.
    static List<short[]> changedMemoryList = new ArrayList<>(); //STA 사용시 변화된 메모리 주소 저장하는 리스트

    static StringBuilder string_OUT = new StringBuilder(); //인터럽트 출력 문자열


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
        changedMemoryList.add(changeMemory); //변화된 메모리 주소 저장

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

        // 출력용
        System.out.println("OUT 출력값: "+(char) reg_OUTR);
        string_OUT.append((char) reg_OUTR);
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

        if(reg_IR == 0xFFFFC000){
            isInterrupt = false;
            System.out.println("--- 인터럽트 종료 ---");
        }

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
        for(short[] m : changedMemoryList)
            System.out.printf("\tM[%03X]", m[0]);
        System.out.println();
        System.out.print(String.format("%04X\t%03X\t%03X\t%04X\t%04X\t%04X\t", reg_IR, reg_AR, reg_PC, reg_DR, reg_AC, reg_TR));
        System.out.print(String.format("%X\t%X\t%X", ff_I?1:0, ff_S?1:0, ff_E?1:0));
        for(short[] m : changedMemoryList)
            System.out.printf("\t%04X", m[2]);
        System.out.println();
    }



    //////////////////////////////////////////////////////////////////////////////////////

    static boolean isInterrupt = false;


    // [A]
    public static void runComputer(String filename, short[]... interrupt){

        // 메인 함수는 어셈블러 실행 - {fetch - decode - execute}로만 구성. 나머지 작업은 다른 곳에서.
        runAssembler(filename);

        System.out.println("--- 명령어 실행 시작 ---");
        System.out.print("IR\t\tAR\tPC\tDR\t\tAC\t\tTR\t\t");
        System.out.println("I\tS\tE");
        System.out.print(String.format("%04X\t%03X\t%03X\t%04X\t%04X\t%04X\t", reg_IR, reg_AR, reg_PC, reg_DR, reg_AC, reg_TR));
        System.out.println(String.format("%X\t%X\t%X", ff_I?1:0, ff_S?1:0, ff_E?1:0));

        // 실행되는 명령어 번째(인터럽트 제외)
        int line = 0;


        while (ff_S){       // start-stop flip-flop이 1일 때만 작동.

            // R?
            if(ff_R){
                // interrupt cycle
                //T0
                reg_AR=0;
                reg_TR=reg_PC;
                reg_SC++;
                //T1
                memory[reg_AR] = reg_TR;
                reg_PC = 0;
                reg_SC++;
                //T2
                reg_PC++;
                ff_IEN=false;
                ff_R = false;
                reg_SC = 0;

                // 이 경우 인터럽트 시작
                isInterrupt = true;

            } else{
                // instruction cycle

                fetch();
                decode();
                // IEN, FGI, FGO 체크
                if(ff_IEN){
                    ff_R = ff_FGI || ff_FGO;
                }
                execute();



                // 인터럽트 발생 코드
                // 인터럽트가 아닐때만 line 증가
                if(!isInterrupt) line++;
//                System.out.println(line);

                for(short[] interruptCase : interrupt){
                    short where = interruptCase[0];
                    char ch = (char) interruptCase[1];
                    if(line==where && where>0 && ch>0) {
                            System.out.println("--- 인터럽트 발생 ---");
                            ff_FGI=true;
                            reg_INPR = (byte) ch;
                            break;
                    }
                }
            }
        }
        System.out.println("--- 명령어 실행 끝 ---");

        // [G]
        System.out.println("--- 변경된 메모리 ---");
        for(short[] i : changedMemoryList) {
            System.out.println(String.format("memory[%03X]: %04X -> %04X", i[0], i[1], i[2]));
        }

        // 인터럽트로 들어온 문자열 최종 출력
        if(string_OUT.length()>0) {
            System.out.println("입력받은 문자열: "+string_OUT);
            string_OUT.delete(0, string_OUT.length());
        }

        System.out.println("--- 컴퓨터를 종료합니다. ---");
    }


    // [A]
    public static void main(String[] args) {

        // jar 파일 전용
//        for(String t:args){
//            runComputer(t);
//            reboot();
//        }

        // 테스트 인터럽트 (인터럽트를 주는 위치, 문자)
        short[][] interrupts = {{10, 'h'}, {20, 'e'}, {40, 'l'}, {50, 'l'}, {100, 'o'}, {120, ','}, {140, ' '},
                {160, 'w'}, {200, 'o'}, {240, 'r'}, {300, 'l'}, {360, 'd'}, {390, '!'}};

        runComputer("src/Assembly3.txt", interrupts);
        System.out.println("M[0x10F]="+memory[0x10F]);
        changedMemoryList.clear();
//        reboot();
//        runComputer("src/Assembly.txt");


//        System.out.println("M[0x10F]="+memory[0x10F]);
//        changedMemoryList.clear();
//        reboot();
//
//        runComputer("src/Assembly2.txt");
//        System.out.println("M[0x10F]="+memory[0x10F]);

    }



}
