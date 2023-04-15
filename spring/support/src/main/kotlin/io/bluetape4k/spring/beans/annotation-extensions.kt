package io.bluetape4k.spring.beans

import org.springframework.beans.PropertyAccessorFactory
import org.springframework.util.ReflectionUtils
import org.springframework.util.StringValueResolver

fun Annotation.copyPropertiesToBean(bean: Any, vararg excludedProperties: String) {
    copyPropertiesToBean(bean, null, *excludedProperties)
}

fun Annotation.copyPropertiesToBean(
    bean: Any, valueResolver: StringValueResolver?,
    vararg excludedProperties: String,
) {
    val excluded = excludedProperties.toSet()
    val annotationProperties = this.annotationClass.java.declaredMethods
    val bw = PropertyAccessorFactory.forBeanPropertyAccess(bean)

    annotationProperties.forEach { annotationProperty ->
        val propertyName = annotationProperty.name
        if (!excluded.contains(propertyName) && bw.isWritableProperty(propertyName)) {
            var value = ReflectionUtils.invokeMethod(annotationProperty, this)
            if (valueResolver != null && value is String) {
                value = valueResolver.resolveStringValue(value)
            }
            bw.setPropertyValue(propertyName, value)
        }
    }
}
