package statemachine

fun createStateMachineController(startName: String = "start", init: StateMachineDSL.() -> Unit): ControllerDSL {
    val controller = Controller(startName)
    init.invoke(StateMachineDSL(controller.machine))
    return ControllerDSL(controller)
}

class ControllerDSL(private val controller: Controller) {

    private val states = controller.machine.getStates()
    private val transitions = controller.machine.getTransitions()

    infix fun handle(name: String): ControllerDSL {
        controller.handle(states.firstOrNull { it.name == name }
            ?: throw AssertionError("state $name is not exists!"))
        return this
    }

    infix fun call(name: String): ControllerDSL {
        controller.call(transitions.firstOrNull { it.event.name == name }
            ?: throw AssertionError("transition event $name not exists!"))
        return this
    }

}

class StateMachineDSL(private val machine: StateMachine) {

    private fun newState(name: String): State {
        return State(name).apply {
            addTransition(machine.start)
            machine.start.addTransition(this)
        }
    }

    private fun getOrNewState(state: String): State {
        return machine.getStates().firstOrNull { it.name == state }?: newState(state)
    }

    infix fun String.to(target: String): TransitionDSL {
        return getOrNewState(this).addTransition(getOrNewState(target)).run(::TransitionDSL)
    }

    infix fun String.action(code: () -> Unit): CommandDSL {
        val state = getOrNewState(this)
        val command = Command("${state.name} action #${state.actions.size}", code)
        state.actions.add(command)
        return CommandDSL(command)
    }

}

class CommandDSL(private val command: Command) {
    infix fun name(name: String): CommandDSL {
        command.name = name
        return this
    }
}

class TransitionDSL(private val transition: Transition) {
    infix fun name(name: String): TransitionDSL {
        transition.event.name = name
        return this
    }
}