import java.io.BufferedReader;
import java.io.FileReader;

public class BC_Assembler extends CPU {


    // 팀별로 함수 사용


    /*
    * 코드 입력(어셈블러) 팀
    * 1) Assembly.txt 파일을 읽어오기
    * 2) 읽어온 어셈블리어를 16진수로 변환 (First Pass, Second Pass)
    * 3) memory[4096]에 16진수 형태로 저장
    * 4) memory[4096] 콘솔 출력
    *
    *
    * */

    private static void runAssembler(String file) {
        int cnt=0;
        int lc;
        String[] temp1 = new String[4096];
        String[][] temp2 = new String[4096][4];
        // 라벨필드 | 명령어 | 주소 | I

        try {
            // 버퍼리더에 파일 등록
            BufferedReader br = new BufferedReader(new FileReader(file));
            // 파일을 temp1에 저장, 달성 후 cnt = 명령어+1
            while(true) {
                String line = br.readLine();
                if(line==null) break;
                temp1[cnt++] = line;
            }
            br.close();


            //ㅡㅡㅡㅡㅡㅡㅡㅡㅡTEST용 출력ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ
            System.out.println("<temp1>");
            for(int i=0;i<9;i++) {
                System.out.println(temp1[i]);
            }
            System.out.println();
            //ㅡㅡㅡㅡㅡㅡㅡㅡㅡTEST용 출력ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ


            // temp1을 temp2로 이전
            for(int i=0;i<temp1.length;i++) {
                if(temp1[i] != null) {
                    String[] temp3 = temp1[i].split(" ");
                    for(int j=0;j<temp3.length;j++) {
                        temp2[i][j]=temp3[j];
                    }
                }
            }

            // 라벨 주소 위치 맞혀주기
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


            //ㅡㅡㅡㅡㅡㅡㅡㅡㅡTEST용 출력ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ
            System.out.println("<temp2>");
            for(int i=0;i<9;i++) {
                for(int j=0;j<4;j++) {
                    System.out.print(temp2[i][j]+" ");
                }
                System.out.println();
            }
            System.out.println();
            //ㅡㅡㅡㅡㅡㅡㅡㅡㅡTEST용 출력ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ


            // ORG 값대로 location counter 초기화
            lc=Integer.parseInt(temp2[0][2]);


            //ㅡㅡㅡㅡㅡㅡㅡㅡㅡTEST용 출력ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ
            System.out.println("<location counter>");
            System.out.println(lc+"\n");
            //ㅡㅡㅡㅡㅡㅡㅡㅡㅡTEST용 출력ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ


            for(int i=1;i<memory.length;i++);
            // memory[i] = (short)temp1[i];

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }



        tempMemory();
    }
    static void tempMemory(){
        memory[0] = 0x2004;
        memory[1] = (short) 0x1005;
        memory[2] = 0x3006;
        memory[3] = 0x7001;
        memory[4] = (short) 0x0053;
        memory[5] = (short) 0xFFE9;
        memory[6] = 0x0000;
    }

    static void printInstruction(){
        int instructionSize = 7;        // 이 개수는 Second Pass에서 LC나 다른 변수에서 갖고 와야 함
        int org = 0;                    // 이 개수는 어셈블러에서 첫 번째 org 옆 숫자 값이어야 함

        System.out.println("---저장된 기계어입니다---");
        for(int i=org; i<instructionSize; i++){
            System.out.printf("M[%03X] = %04X\n", i, memory[i]);
        }
        System.out.println("---기계어의 끝입니다---");
    }



    //////////////////////////////////////////////////////////////////////////////////////




    /*
     * Fetch & Decode 팀
     * 1) memory[4096]에서 16진수 명령어 하나씩 읽어오기
     * 2) 시간 순서마다 PC, AR, IR 등에 적절한 값 입력하기
     * 3) 간접 주소 방식인 경우 실제 주소가 입력되기까지 수행하기
     * 참고: MRI는 T4부터, RRI, IOI는 T3에 실행됨에 유의
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
        System.out.printf("--- Memory[%03x]: %04x 실행 ---\n", reg_AR, reg_IR);
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

        if(opc == -1 || opc == 7){      // 1111 1111(=-1)이거나 0000 0111(=7)
            switch (reg_IR){
                case 0x7001: decodedInstruction = "HLT"; break;
                default:  decodedInstruction = "ERR";
            }
        } else {

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

    }


    //////////////////////////////////////////////////////////////////////////////////////



    /*
     * Instruction Set 팀
     * 1) 25개의 명령어 코드 작성
     * 2) 마이크로 연산을 한 줄로 생각하고 입력
     * 3) 함수에 인자는 없어도 됨(실제 레지스터를 참조)
     *
     *
     * */

    static void LDA(){
        // T4
        reg_DR = memory[reg_AR];
        reg_SC++;
        // T5
        reg_AC = reg_DR;
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

    static void STA(){
        // T4
        memory[reg_AR] = reg_AC;
    }


    static void HLT(){
        ff_S = false;
    }




    //////////////////////////////////////////////////////////////////////////////////////





    /*
     * Execution 팀
     * 1) Fetch & Decode 팀의 출력값에 따라 적절한 명령어 실행하도록 코딩
     * 2) 실행 처음에 현재 메모리와 레지스터에 저장된 값 표시, 실행 끝나고 변화된 값 표시
     *
     *
     * */
    static void execute(){

        System.out.println("해석 결과: "+(ff_I?"(간접) ":"")+decodedInstruction);
        switch (decodedInstruction){
            case "LDA": LDA(); break;
            case "ADD": ADD(); break;
            case "STA": STA(); break;
            case "HLT": HLT(); break;
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
        printInstruction();

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



//        short a = (short)0xffff;
//        short b = (short)0x8fef;
//        int c = a + b;
//        int d = (short) c;
//        int e = (c << 15);
//        int f = (c << 15) >>> 31;
//        System.out.println(Integer.toBinaryString(a));
//        System.out.println(Integer.toBinaryString(b));
//        System.out.println(Integer.toBinaryString(c));
//        System.out.println(Integer.toBinaryString(d));
//        System.out.println(Integer.toBinaryString(e));
//        System.out.println(Integer.toBinaryString(f));
//
//        int g = 0b1111111100000000;
//        int h = g >>> 12;
//        System.out.println(Integer.toBinaryString(h));
//        System.out.println(h);


    }



}
