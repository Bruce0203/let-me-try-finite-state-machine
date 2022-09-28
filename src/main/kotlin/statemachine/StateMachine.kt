package statemachine

open class Event(var name: String)

open class Command(var name: String, var code: () -> Unit)

class StateMachine internal constructor(internal val start: State) {

    @Suppress("unused")
    internal fun getStates(): HashSet<State> {
        return HashSet<State>().apply { collectStates(start, this) }
    }

    private fun collectStates(state: State, result: HashSet<State> = HashSet()) {
        if (result.add(state)) {
            for (next in state.getAllTargets()) {
                collectStates(next, result)
            }
        }
    }

    @Suppress("unused")
    internal fun getTransitions(): HashSet<Transition> {
        return HashSet<Transition>().apply { collectTransitions(start, this) }
    }

    private fun collectTransitions(state: State, result: HashSet<Transition> = HashSet()) {
        if (result.addAll(state.transitions)) {
            for (next in state.transitions) {
                collectTransitions(next.target, result)
            }
        }
    }

}

class TransitionEvent(name: String) : Event(name)

data class Transition(val source: State, val event: Event, val target: State)

class State internal constructor(val name: String) {
    internal val actions = ArrayList<Command>()

    internal val transitions by lazy { HashSet<Transition>() }

    fun executeActions() {
        actions.forEach {
            try {
                it.code.invoke()
            } catch (e: Exception) {
                e.printStackTrace()
                throw AssertionError("an error occurred while execution at ${it.name} action")
            }
        }
    }

    internal fun addTransition(targetState: State): Transition {
        val transition = Transition(this,
            TransitionEvent("${this.name}_to_${targetState.name}"), targetState)
        transitions.add(transition)
        return transition
    }

    fun hasTransition(targetState: State): Boolean {
        return transitions.any { it.target == targetState }
    }

    internal fun getAllTargets(): List<State> {
        return transitions.map { it.target }
    }

}
