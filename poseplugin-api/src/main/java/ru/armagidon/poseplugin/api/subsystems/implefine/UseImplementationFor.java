package ru.armagidon.poseplugin.api.subsystems.implefine;

//TODO implement Implefine
//Each class should have instance annotated with @Implementation

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface UseImplementationFor
{
    Class<?> parent();
}
