package statemachine

import commandchannel.Event

class StateMachineAdapter internal constructor(private val machine: StateMachine) {

    @Suppress("unused")
    val startState get() = StateAdapter(machine.start)

    @Suppress("unused")
    val channel get() = machine.eventChannel

    @Suppress("unused")
    fun newState(name: String, event: (() -> Event)? = null): StateAdapter {
        return StateAdapter(machine.newState(name, event))
    }

    @Suppress("unused")
    fun getOrNewState(state: String) = StateAdapter(machine.getOrNewState(state))

    @Suppress("unused")
    infix fun String.transition(target: String) : String{
        getOrNewState(this).addTransition(getOrNewState(target))
        return target
    }

    @Suppress("unused")
    infix fun String.event(event: () -> Unit) {
        getOrNewState(this).apply {
            machine.eventChannel.register(StateTransitionEvent::class) {
                if (it.state == theState) event()
            }
        }
    }

    @Suppress("unused")
    fun handle(targetState: StateAdapter) {
        machine.handle(targetState.theState)
    }

    @Suppress("unused")
    fun handle(targetState: String) {
        machine.handle(targetState)
    }

}

class StateAdapter internal constructor(internal val theState: State) {

    @Suppress("unused")
    infix fun addTransition(state: StateAdapter) {
        theState.addTransition(state.theState)
    }

    @Suppress("unused")
    fun addTransition(vararg states: StateAdapter) {
        for (state in states) theState.addTransition(state.theState)
    }
}