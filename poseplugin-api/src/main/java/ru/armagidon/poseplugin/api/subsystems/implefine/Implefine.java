package ru.armagidon.poseplugin.api.subsystems.implefine;


import lombok.SneakyThrows;
import org.reflections.Reflections;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ConfigurationBuilder;
import ru.armagidon.poseplugin.api.utility.LazyObject;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

//Implementation finding engine
public class Implefine {


    private static final Map<Class<?>, Class<?>> cachedClasses = new HashMap<>();

    private static final Reflections reflections = new Reflections(new ConfigurationBuilder()
            .addScanners(new TypeAnnotationsScanner())
            .forPackages("ru.armagidon.poseplugin"));

    static {
        Reflections.log = null;
    }

    public static <T> Class<?> process(Class<T> clazz) throws NoSuchElementException {
        try {
            return reflections.getTypesAnnotatedWith(UseImplementationFor.class).stream().filter(c -> {
                Class<?> parent = c.getAnnotation(UseImplementationFor.class).parent();
                return parent.equals(clazz);
            }).filter(c -> !c.equals(clazz)).findFirst().get();
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public static <T> T get(Class<T> parentClass) {
        Class<?> c = cachedClasses.computeIfAbsent(parentClass, Implefine::process);
        if (c == null)
            throw new RuntimeException("No implementations were found");
        var implementationOptional = Arrays.stream(c.getDeclaredFields())
                //.filter(f -> (f.getModifiers() & Modifier.STATIC) != 0)
                .filter(f -> f.getType().equals(LazyObject.class))
                .peek(f -> f.setAccessible(true))
                .filter(f ->{
                    try {
                        return parentClass.isAssignableFrom(((LazyObject<?>) f.get(null)).getType());
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                })
                //.filter(f -> parentClass.isAssignableFrom(f.getType()))
                .filter(f -> f.isAnnotationPresent(Implementation.class))
                .findFirst();
        if (implementationOptional.isEmpty())
            throw new RuntimeException("Implementation class does not have implementation instance");
        return ((LazyObject<T>) implementationOptional.get().get(null)).get();
    }
}
