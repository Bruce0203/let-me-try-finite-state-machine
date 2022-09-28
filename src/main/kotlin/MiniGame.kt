import statemachine.createStateMachineController

/***
상태: 준비, 시작, 실행, 종료
처음에, n명 이상 플레이어가 접속했을 때 `준비`로 이동
준비로 이동했을 때 5초 후 `실행`으로 이동
준비인 동안 플레이어가 퇴장해서 n명 이하가 될 때 `시작`으로 이동
`실행` 시 플레이를 출력하고 `종료`로 이동
 */

fun main() {
    val fsm = createStateMachineController {

        "start" to "wait" name "players-are-enough-and-ready-to-play"

        "wait" to "start" name "not-enough-players"

        "wait" to "play" name "game-start"

        "play" to "stop" name "game-ended"

        "play" action {
            println("GAME START!!")
        } name "print-start-state"

        "wait" action {
            println("GAME READY TO START, WAIT FOR 5 SECONDS...")
        } name "print-wait-state"

        "stop" action {
            println("GAME ENDED!!")
        } name "print-stop-state"

        "start" action {
            println("GAME RESET.")
        }

    }

    //wait > start
    fsm handle "wait"
    fsm handle "start"

    //wait > start
    fsm call "players-are-enough-and-ready-to-play"
    fsm call "not-enough-players"

    //wait > play > stop
    fsm call "players-are-enough-and-ready-to-play"
    fsm call "game-start"
    fsm call "game-ended"

    //no changes
    fsm call "not-enough-players"
    fsm call "game-start"

}
