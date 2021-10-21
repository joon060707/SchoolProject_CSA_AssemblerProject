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

    /*
     * Fetch & Decode 팀
     * 1) memory[4096]에서 16진수 명령어 하나씩 읽어오기
     * 2) 시간 순서마다 PC, AR, IR 등에 적절한 값 입력하기
     * 3) 간접 주소 방식인 경우 실제 주소가 입력되기까지 수행하기
     * 참고: MRI는 T4부터, RRI, IOI는 T3에 실행됨에 유의
     *
     *
     * */
    static void tempMemory(){
        memory[0] = 0x2004;
        memory[1] = 0x1005;
        memory[2] = 0x3006;
        memory[3] = 0x7001;
        memory[4] = 0x0053;
        memory[5] = (short) 0xFFE9;
        memory[6] = 0x0000;
    }

    static void fetch(){
        reg_AR = reg_PC;
    }
    static void decode(){
        reg_IR = memory[reg_AR];
        reg_PC++;
        ff_I = reg_IR<0;
        reg_AR = (short) (reg_IR & 0x0fff);
    }

    /*
     * Instruction Set 팀
     * 1) 25개의 명령어 코드 작성
     * 2) 마이크로 연산을 한 줄로 생각하고 입력
     * 3) 함수에 인자는 없어도 됨(실제 레지스터를 참조)
     *
     *
     * */

    /*
     * Execution 팀
     * 1) Fetch & Decode 팀의 출력값에 따라 적절한 명령어 실행하도록 코딩
     * 2) 실행 처음에 현재 메모리와 레지스터에 저장된 값 표시, 실행 끝나고 변화된 값 표시
     *
     *
     * */
    static void execute(){
        System.out.println("IR\t\tAR\tPC\tDR\t\tAC\t\tTR");
        System.out.println(String.format("%04x\t%03x\t%03x\t%04x\t%04x\t%04x", reg_IR, reg_AR, reg_PC, reg_DR, reg_AC, reg_TR));
    }



    public static void main(String[] args) {

        // main 함수의 시작은 코드 입력팀에서 작성하는 함수로 시작.
        runAssembler("src/Assembly.txt");

        while (true){

             fetch();
             decode();
             execute();

             if(reg_IR == 0x7001) {
                System.out.println("HALT COMPUTER");
                break;
             }
        }
    }
}
