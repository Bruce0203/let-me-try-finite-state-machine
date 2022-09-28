package statemachine

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