package statemachine

import commandchannel.Event
import commandchannel.EventChannel
import kotlin.test.assertNotNull

class StateMachine private constructor(
    internal val start: State,
    private var currentState: State = start,
    internal val eventChannel: EventChannel,
) {

    companion object {
        @Suppress("unused")
        @JvmStatic
        fun newMachine(
            eventChannel: EventChannel = EventChannel(),
            startName: String = "start",
            event: (() -> Event)? = null,
            init: StateMachineAdapter.() -> Unit = {}
        ) = StateMachine(State(startName, event), eventChannel = eventChannel).run {
            val machineAdapter = StateMachineAdapter(this)
            init.invoke(machineAdapter)
            machineAdapter
        }
    }

    internal fun newState(name: String, event: (() -> Event)? = null): State {
        return State(name, event).apply { addTransition(start) }
    }

    @Suppress("unused")
    internal fun handle(targetState: State) {
        if (currentState.hasTransition(targetState))
            transitionTo(currentState.targetState(targetState))
    }

    private fun transitionTo(target: State) {
        currentState = target
        eventChannel.call(target.event.invoke())
    }

    @Suppress("unused")
    private fun getStates(): ArrayList<State> {
        return ArrayList<State>().apply { collectStates(start, this) }
    }

    private fun collectStates(state: State, result: ArrayList<State> = ArrayList()) {
        if (result.contains(state)) return
        result.add(state)
        for (next in state.getAllTargets()) {
            collectStates(next, result)
        }
    }

}

internal data class Transition(val source: State, val target: State)

open class StateTransitionEvent(private val state: State) : Event {
    @Suppress("unused")
    val stateName get() = state.name
}

class State internal constructor(val name: String, event: (() -> Event)? = null) {

    private val transitions by lazy { ArrayList<Transition>() }
    internal var event: () -> Event
    init {
        if (event === null) this.event = { StateTransitionEvent(this) }
        else this.event = event
    }

    internal fun addTransition(targetState: State) {
        if (hasTransition(targetState))
            throw AssertionError("state transition already exist!")
        transitions.add(Transition(this, targetState))
    }

    fun hasTransition(targetState: State): Boolean {
        return transitions.any { it.target == targetState }
    }

    internal fun targetState(targetState: State): State {
        val transition = transitions.firstOrNull { it.target == targetState }
        assertNotNull(transition, "transition not exist!")
        return transition.target
    }

    fun getAllTargets(): List<State> {
        return transitions.map { it.target }
    }

}
