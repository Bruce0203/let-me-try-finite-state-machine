import commandchannel.Event
import commandchannel.EventChannel
import statemachine.StateMachine

enum class State {
    START, WAIT, PlAY, STOP
}

class StartEvent : Event
class WaitEvent : Event
class PlayEvent : Event
class StopEvent : Event

@Suppress("unused")
class App {
    companion object {
        @JvmStatic
        @Suppress("unused")
        fun main(args: Array<String>) {
            val channel = EventChannel().apply {
                val debug = { it: Any -> println("channel published ${it.javaClass.simpleName}") }
                register(WaitEvent::class, debug)
                register(StartEvent::class, debug)
                register(PlayEvent::class, debug)
                register(StopEvent::class, debug)
            }

            val machine = StateMachine.newMachine(channel, State.START.name, { StartEvent() })
            val wait = machine.newState(State.WAIT) { WaitEvent() }
            val play = machine.newState(State.PlAY) { PlayEvent() }
            val stop = machine.newState(State.STOP) { StopEvent() }
            wait.addTransition(play)
            play.addTransition(stop)

            machine.handle(wait)
            machine.handle(play)
            machine.handle(stop)


        }
    }
}