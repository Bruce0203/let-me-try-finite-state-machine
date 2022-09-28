import statemachine.StateMachine

@Suppress("unused")
class App {
    companion object {
        @JvmStatic
        @Suppress("unused")
        fun main(args: Array<String>) {
            StateMachine.newMachine {
                newState("wait")
                newState("play")
                addTransition("wait", "play")
            }

        }
    }
}