package com.borgernet.dsl;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Indicates that the first String parameter of this method
 * should be treated as an extended String meaning everything
 * after the method name until one of the end tokens is found
 * will be captured as the String argument.
 */
@Target(METHOD)
@Retention(RUNTIME)
public @interface ExtendedStringParameter {

    /**
     * Token that represents matching the end of a line
     */
    String EOL = "$";

    /**
     * The tokens that mark the end of the String parameter
     */
    String[] endTokens() default EOL;
}
