package io.bluetape4k.spring.beans

import io.bluetape4k.logging.KotlinLogging
import io.bluetape4k.support.assertNotBlank
import io.bluetape4k.utils.KotlinDelegates
import org.springframework.beans.BeanInstantiationException
import org.springframework.beans.BeanUtils
import org.springframework.core.KotlinDetector
import org.springframework.core.MethodParameter
import java.beans.PropertyDescriptor
import java.lang.reflect.Constructor
import java.lang.reflect.Method


private val log = KotlinLogging.logger {}

/**
 * 지정한 수형의 새로운 인스턴스를 생성합니다.
 * @receiver Class<T> 생성할 인스턴스의 수형
 * @return T 생성된 인스턴스
 */
fun <T> Class<T>.instantiateClass(): T = BeanUtils.instantiateClass(this)

/**
 * [Constructor]와 인자를 이용하여 인스턴스를 생성합니다.
 *
 * @receiver Constructor<T>
 * @param args Array<out Any?>
 * @return T
 */
fun <T: Any> Constructor<T>.instantiateClass(vararg args: Any?): T {
    return try {
        when {
            KotlinDetector.isKotlinType(this.declaringClass) ->
                KotlinDelegates.instantiateClass(this, *args)!!

            else                                             ->
                BeanUtils.instantiateClass(this, *args)
        }
    } catch (e: Exception) {
        throw BeanInstantiationException(this, "Fail to instantiate", e)
    }
}

/**
 * Receiver 수형으로 인스턴스를 생성하고, [assignableTo] 수형으로 casting해서 반환합니다.
 * @receiver Class<*>
 * @param assignableTo Class<T>
 * @return T
 */
fun <T> Class<*>.instantiateClass(assignableTo: Class<T>): T =
    BeanUtils.instantiateClass(this, assignableTo)


/**
 * 현 수형의 [methodName]의 메소드 정보를 찾습니다.
 *
 * @param methodName method name to find
 * @param paramTypes types of method parameter
 * @return 메소드 정보, 찾지 못하면 null 반환
 */
fun Class<*>.findMethod(methodName: String, vararg paramTypes: Class<*>): Method? {
    methodName.assertNotBlank("methodName")
    return BeanUtils.findMethod(this, methodName, *paramTypes)
}

/**
 * 현 수형의 [methodName]의 메소드 정보를 찾습니다.
 *
 * @param methodName method name to find
 * @param paramTypes types of method parameter
 * @return 메소드 정보, 찾지 못하면 null 반환
 */
fun Class<*>.findDeclaredMethod(methodName: String, vararg paramTypes: Class<*>): Method? {
    methodName.assertNotBlank("methodName")
    return BeanUtils.findDeclaredMethod(this, methodName, *paramTypes)
}

/**
 * 현 수형의 [methodName]의 메소드 정보를 찾습니다.
 *
 * @param methodName method name to find
 * @param paramTypes types of method parameter
 * @return 메소드 정보, 찾지 못하면 null 반환
 */
fun Class<*>.findMethodWithMinimalParameters(methodName: String): Method? {
    methodName.assertNotBlank("methodName")
    return BeanUtils.findMethodWithMinimalParameters(this, methodName)
}

/**
 * 현 수형의 [methodName]의 메소드 정보를 찾습니다.
 *
 * @param methodName method name to find
 * @param paramTypes types of method parameter
 * @return 메소드 정보, 찾지 못하면 null 반환
 */
fun Class<*>.findDeclaredMethodWithMinimalParameters(methodName: String): Method? {
    methodName.assertNotBlank("methodName")
    return BeanUtils.findDeclaredMethodWithMinimalParameters(this, methodName)
}

/**
 * 현 수형의 [methodName]의 메소드 정보를 찾습니다.
 *
 * @param methodName method name to find
 * @param paramTypes types of method parameter
 * @return 메소드 정보, 찾지 못하면 null 반환
 */
fun Array<out Method>.findMethodWithMinimalParameters(methodName: String): Method? {
    methodName.assertNotBlank("methodName")
    return BeanUtils.findMethodWithMinimalParameters(this, methodName)
}

/**
 * 지정한 수형에서 [signature]에 해당하는 [Method]를 찾습니다.
 * [signature] 는 `methodName[([arg_list])]` 형식의 메소드의 signature를 표현한 것입니다.
 *
 * @receiver Class<*>
 * @param signature 메소드의 signature
 * @return Method? 찾은 메소드 인스턴스
 */
fun Class<*>.resolveSignature(signature: String): Method? =
    BeanUtils.resolveSignature(signature, this)

/**
 * 주어진 수형의 JavaBeans [PropertyDescriptor]를 얻습니다.
 *
 * @receiver Class<*>
 * @return Array<PropertyDescriptor>
 */
fun Class<*>.getPropertyDescriptors(): Array<PropertyDescriptor> =
    BeanUtils.getPropertyDescriptors(this)


fun Class<*>.getPropertyDescriptor(propertyName: String): PropertyDescriptor? {
    propertyName.assertNotBlank("requireName")
    return BeanUtils.getPropertyDescriptor(this, propertyName)
}

/**
 * 현 메소드를 표현하는 JavaBeans [PropertyDescriptor]를 찾습니다.
 * @receiver Method
 * @return PropertyDescriptor?
 */
fun Method.findPropertyDescriptor(): PropertyDescriptor? =
    BeanUtils.findPropertyForMethod(this)

/**
 * 현 메소드를 표현하는 JavaBeans [PropertyDescriptor]를 찾습니다.
 * @receiver Method
 * @return PropertyDescriptor?
 */
fun Method.findPropertyDescriptor(clazz: Class<*>): PropertyDescriptor? =
    BeanUtils.findPropertyForMethod(this, clazz)

/**
 * Obtain a new MethodParameter object for the write method of the specified property.
 *
 * @receiver the PropertyDescriptor for the property
 * @return a corresponding MethodParameter object
 */
fun PropertyDescriptor.getWriteMethodParamter(): MethodParameter =
    BeanUtils.getWriteMethodParameter(this)

/**
 *
 * @receiver Class<*>
 * @return Boolean
 */
/**
 * Check if the given type represents a "simple" property: a simple value type or an array of simple value types.
 *
 * [isSimpleValueType(Class)] for the definition of **simple value type**
 *
 * Used to determine properties to check for a "simple" dependency-check.
 *
 * @receiver the type to check
 * @return whether the given type represents a "simple" property
 * @see [isSimpleValueType]
 */

fun Class<*>.isSimpleProperty(): Boolean = BeanUtils.isSimpleProperty(this)

/**
 * Check if the given type represents a "simple" value type: a primitive or primitive wrapper,
 * an enum, a String or other CharSequence, a Number, a Date, a Temporal, a URI, a URL, a Locale, or a Class.
 *
 * [Void] and [void] are not considered simple value types.
 *
 * @receiver the type to check
 * @return whether the given type represents a "simple" value type
 * @see  [isSimpleProperty]
 */
fun Class<*>.isSimpleValueType(): Boolean = BeanUtils.isSimpleValueType(this)

/**
 * 지정한 Bean의 속성을 [target]의 속성에 설정합니다.
 *
 * @receiver Source bean
 * @param target Target bean
 * @param ignoreProperties 복사에서 제외할 속성명
 */
fun Any.copyProperties(target: Any, vararg ignoreProperties: String) {
    BeanUtils.copyProperties(this, target, *ignoreProperties)
}

/**
 * 지정한 Bean의 속성을 [target]의 속성에 설정합니다.
 *
 * @receiver Source bean
 * @param target Target bean
 * @param editable the class (or interface) to restrict property setting to
 */
fun Any.copyProperties(target: Any, editable: Class<*>) {
    BeanUtils.copyProperties(this, target, editable)
}
