package commandchannel

import java.util.function.Consumer
import kotlin.reflect.KClass

/**
 * The action boilerplate
 */
interface Event

typealias Registry = ArrayList<Consumer<Event>>
/**
 * This is Actions Channel for Event Publish–subscribe pattern
 */
@Suppress("unused")
class EventChannel {

    private val registry: HashMap<Class<Event>, Registry> = HashMap()

    @Suppress("unused")
    fun call(event: Event) {
        getRegistry(event.javaClass).forEach { listener -> listener.accept(event) }
    }

    @Suppress("unused")
    fun <E : Event> register(event: KClass<E>, listener: Consumer<E>) {
        register(event.java, listener)
    }

    @Suppress("unused", "unchecked_cast")
    fun <E : Event> register(event: Class<E>, listener: Consumer<E>) {
        getRegistry(event as Class<Event>).add(listener as Consumer<Event>)
    }

    private fun getRegistry(event: Class<Event>): Registry {
        val registry = registry.getOrElse(event) { Registry() }
        this.registry[event] = registry
        return registry
    }

}