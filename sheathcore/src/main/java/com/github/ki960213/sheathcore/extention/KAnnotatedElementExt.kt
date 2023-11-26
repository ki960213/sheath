package com.github.ki960213.sheathcore.extention

import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.full.hasAnnotation

internal inline fun <reified T : Annotation> KAnnotatedElement.hasAnnotationOrHasAttachedAnnotation(): Boolean =
    this.hasAnnotation<T>() || this.annotations.any { it.annotationClass.hasAnnotation<T>() }

internal inline fun <reified T : Annotation> KAnnotatedElement.findAttachedAnnotation(): Annotation? =
    this.annotations.find { it.annotationClass.hasAnnotation<T>() }
