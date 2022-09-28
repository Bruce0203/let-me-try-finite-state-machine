import statemachine.StateMachine

/***
상태: 준비, 시작, 실행, 종료
처음에, n명 이상 플레이어가 접속했을 때 `준비`로 이동
준비로 이동했을 때 5초 후 `실행`으로 이동
준비인 동안 플레이어가 퇴장해서 n명 이하가 될 때 `시작`으로 이동
`실행` 시 플레이를 출력하고 `종료`로 이동
 */

fun main() {
    StateMachine.newMachine {
        newState("wait")
        newState("play")
        addTransition("wait", "play")
    }
}