import statemachine.StateMachine

@Suppress("unused")
class App {
    companion object {
        @JvmStatic
        @Suppress("unused")
        fun main(args: Array<String>) {
            StateMachine.create {
                "wait" transition "play" transition "stop"
            }

        }
    }
}