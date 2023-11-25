package com.ki960213.sheathCore.annotation

@IndexAnnotated
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class Component

@IndexAnnotated
@Target(AnnotationTarget.CLASS)
@Prototype
@Component
annotation class SheathViewModel

@IndexAnnotated
@Target(AnnotationTarget.CLASS)
@Component
annotation class UseCase

@IndexAnnotated
@Target(AnnotationTarget.CLASS)
@Component
annotation class Repository

@IndexAnnotated
@Target(AnnotationTarget.CLASS)
@Component
annotation class DataSource
