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

        }
    }

    internal fun newState(name: String, event: (() -> Event)? = null): State {
        return State(name, event).apply {
            addTransition(start)
            start.addTransition(this)
        }
    }

    @Suppress("unused")
    internal fun handle(targetState: State) {
        if (currentState.hasTransition(targetState))
            transitionTo(currentState.targetState(targetState))
    }

    @Suppress("unused")
    internal fun handle(targetState: String) {
        if (currentState.hasTransition(targetState))
            transitionTo(currentState.targetState(targetState))
    }

    private fun transitionTo(target: State) {
        currentState = target
        eventChannel.call(target.event.invoke())
    }

    internal fun getState(state: Enum<*>): State = getState(state.name)
    internal fun getState(state: String): State {
        return getStates().firstOrNull() { it.name == state }
            .apply { assertNotNull(this, "$state state is not exist!") }!!
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
        if (hasTransition(targetState)) {
            throw AssertionError("state transition already exist!")
        }
        if (transitions.indexOfFirst { it.target == targetState } == 0) {
            throw AssertionError("cannot add 'start' transition cause is always exists")
        }
        transitions.add(Transition(this, targetState))
    }

    fun hasTransition(targetState: State): Boolean {
        return transitions.any { it.target == targetState }
    }

    fun hasTransition(targetState: String): Boolean {
        return transitions.any { it.target.name == targetState }
    }

    private fun assertTransitionIsNull(transition: Transition?): Transition {
        assertNotNull(transition, "transition not exist!")
        return transition
    }

    internal fun targetState(targetState: State): State {
        val transition = transitions.firstOrNull { it.target == targetState }
        return assertTransitionIsNull(transition).target
    }

    internal fun targetState(targetState: String): State {
        val transition = transitions.firstOrNull { it.target.name == targetState }
        return assertTransitionIsNull(transition).target
    }

    fun getAllTargets(): List<State> {
        return transitions.map { it.target }
    }

}
