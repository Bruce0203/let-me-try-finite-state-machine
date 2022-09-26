package statemachine

import commandchannel.Event

class StateMachineAdapter internal constructor(private val machine: StateMachine) {

    @Suppress("unused")
    val startState get() = StateAdapter(machine.start)
    @Suppress("unused")
    val channel get() = machine.eventChannel

    @Suppress("unused")
    fun newState(name: Enum<*>, event: (() -> Event)? = null) = newState(name.name, event)
    @Suppress("unused")
    fun newState(name: String, event: (() -> Event)? = null): StateAdapter {
        return StateAdapter(machine.newState(name, event)).apply {
            machine.start.addTransition(theState)
        }
    }

    @Suppress("unused")
    fun handle(targetState: StateAdapter) {
        machine.handle(targetState.theState)
    }

}

class StateAdapter internal constructor(internal val theState: State) {

    @Suppress("unused")
    fun addTransition(vararg states: StateAdapter) {
        for (state in states) theState.addTransition(state.theState)
    }

}