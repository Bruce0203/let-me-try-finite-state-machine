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
        return StateAdapter(machine.newState(name, event))
    }

    @Suppress("unused")
    fun getState(state: String) = StateAdapter(machine.getState(state))
    @Suppress("unused")
    fun getState(state: Enum<*>) = StateAdapter(machine.getState(state))

    @Suppress("unused")
    fun handle(targetState: StateAdapter) {
        machine.handle(targetState.theState)
    }

    @Suppress("unused")
    fun handle(targetState: String) {
        machine.handle(targetState)
    }

    fun addTransition(source :String, target: String) {
        getState(source).addTransition(getState(target))
    }

}

class StateAdapter internal constructor(internal val theState: State) {

    @Suppress("unused")
    fun addTransition(vararg states: StateAdapter) {
        for (state in states) theState.addTransition(state.theState)
    }

}