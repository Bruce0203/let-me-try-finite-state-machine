package statemachine

class Event(var name: String)

class Command(var name: String, var code: () -> Unit)

class Controller(startName: String) {

    private var currentState = State(startName)
    internal val machine = StateMachine(currentState)

    internal fun handle(target: State) {
        if (currentState.hasTransition(target)) {
            transitionTo(target)
        }
    }

    internal fun call(transition: Transition) {
        if (currentState.transitions.contains(transition)) {
            transitionTo(transition.target)
        }
    }

    private fun transitionTo(target: State): State {
        currentState = target
        target.executeActions()
        return target
    }


}

class StateMachine constructor(val start: State) {

    fun getStates(): HashSet<State> {
        return HashSet<State>().apply { collectStates(start, this) }
    }

    private fun collectStates(state: State, result: HashSet<State> = HashSet()) {
        if (result.add(state)) {
            for (next in state.getAllTargets()) {
                collectStates(next, result)
            }
        }
    }

    fun getTransitions(): HashSet<Transition> {
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

data class Transition(val source: State, val event: Event, val target: State)

class State(val name: String) {
    val actions = ArrayList<Command>()

    val transitions by lazy { HashSet<Transition>() }

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

    fun addTransition(targetState: State): Transition {
        val transition = Transition(this,
            Event("${this.name}_to_${targetState.name}"), targetState)
        transitions.add(transition)
        return transition
    }

    fun hasTransition(targetState: State): Boolean {
        return transitions.any { it.target == targetState }
    }

    fun getAllTargets(): List<State> {
        return transitions.map { it.target }
    }

}
