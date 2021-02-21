package com.weedow.searchy.mongodb.autoconfigure

import java.lang.reflect.Field

/**
 * The `EnvironmentVariables` allows to set environment variables.
 * <pre>
 * public final EnvironmentVariables environmentVariables = new EnvironmentVariables();
 * environmentVariables.set("name", "value");
 * </pre>
 *
 * You can ensure that some environment variables are not set by calling [clear].
 *
 * **Warning:** This class uses reflection for modifying internals of the environment variables map. It fails if your `SecurityManager` forbids such modifications.
 */
internal class EnvironmentVariables {

    /**
     * Set the value of an environment variable.
     *
     * @param name the environment variable's name.
     * @param value the environment variable's new value. May be `null`.
     * @return the rule itself.
     */
    fun set(name: String, value: String?): EnvironmentVariables {
        set(editableMapOfVariables, name, value)
        set(theCaseInsensitiveEnvironment, name, value)
        return this
    }

    /**
     * Delete multiple environment variables.
     *
     * @param names the environment variables' names.
     * @return the rule itself.
     */
    fun clear(vararg names: String): EnvironmentVariables {
        for (name in names) {
            set(name, null)
        }
        return this
    }

    private fun set(variables: MutableMap<String, String>?, name: String, value: String?) {
        if (variables != null) { // theCaseInsensitiveEnvironment may be null
            if (value == null) {
                variables.remove(name)
            } else {
                variables[name] = value
            }
        }
    }

    companion object {
        private val editableMapOfVariables: MutableMap<String, String>
            get() {
                val classOfMap: Class<*> = System.getenv().javaClass
                return try {
                    getFieldValue(classOfMap, System.getenv(), "m")
                } catch (e: IllegalAccessException) {
                    throw RuntimeException("System Rules cannot access the field 'm' of the map System.getenv().", e)
                } catch (e: NoSuchFieldException) {
                    throw RuntimeException("System Rules expects System.getenv() to have a field 'm' but it has not.", e)
                }
            }

        /*
       * The names of environment variables are case-insensitive in Windows.
       * Therefore it stores the variables in a TreeMap named theCaseInsensitiveEnvironment.
       */
        private val theCaseInsensitiveEnvironment: MutableMap<String, String>?
            get() = try {
                val processEnvironment: Class<*> = Class.forName("java.lang.ProcessEnvironment")
                getFieldValue(processEnvironment, null, "theCaseInsensitiveEnvironment")
            } catch (e: ClassNotFoundException) {
                throw RuntimeException("System Rules expects the existence of the class java.lang.ProcessEnvironment but it does not exist.", e)
            } catch (e: IllegalAccessException) {
                throw RuntimeException(
                    "System Rules cannot access the static field 'theCaseInsensitiveEnvironment' of the class java.lang.ProcessEnvironment.",
                    e
                )
            } catch (e: NoSuchFieldException) {
                // this field is only available for Windows
                null
            }

        @Suppress("UNCHECKED_CAST")
        @Throws(NoSuchFieldException::class, IllegalAccessException::class)
        private fun getFieldValue(klass: Class<*>, obj: Any?, name: String): MutableMap<String, String> {
            val field: Field = klass.getDeclaredField(name)
            field.isAccessible = true
            return field.get(obj) as MutableMap<String, String>
        }
    }
}