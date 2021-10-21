public class BC_Assembler extends CPU {


    // 팀별로 함수 사용


    /*
    * 코드 입력 팀
    * 1) Assembly.txt 파일을 읽어오기
    * 2) 읽어온 어셈블리어를 16진수로 변환 (First Pass, Second Pass)
    * 3) memory[4096]에 16진수 형태로 저장
    * 4) memory[4096] 콘솔 출력
    *
    *
    * */

    /*
     * Fetch & Decode 팀
     * 1) memory[4096]에서 16진수 명령어 하나씩 읽어오기
     * 2) 시간 순서마다 PC, AR, IR 등에 적절한 값 입력하기
     * 3) 간접 주소 방식인 경우 실제 주소가 입력되기까지 수행하기
     * 참고: MRI는 T4부터, RRI, IOI는 T3에 실행됨에 유의
     *
     *
     * */

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



    public static void main(String[] args) {

        // main 함수의 시작은 코드 입력팀에서 작성하는 함수로 시작.

        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();














    }
}
