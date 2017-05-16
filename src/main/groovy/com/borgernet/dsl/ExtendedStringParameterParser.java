package com.borgernet.dsl;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import org.codehaus.groovy.antlr.AntlrParserPlugin;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.syntax.Reduction;

import java.io.BufferedReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.borgernet.dsl.ExtendedStringParameter.EOL;
import static com.borgernet.dsl.ExtendedStringParameter.IDENTIFIER_MARKER;
import static java.util.stream.Collectors.joining;
import static org.codehaus.groovy.runtime.EncodingGroovyMethods.encodeHex;

public class ExtendedStringParameterParser extends AntlrParserPlugin {

    private static final String identifierPattern = "\\s+(\\S.*\\S)\\s*";

    private Collection<Pattern> methodPatterns = new LinkedList<>();

    public ExtendedStringParameterParser(String packageName) {
        FastClasspathScanner classpathScanner = new FastClasspathScanner(packageName);
        classpathScanner.matchClassesWithMethodAnnotation(ExtendedStringParameter.class,
                ((matchingClass, matchingMethod) -> {
                    ExtendedStringParameter annotation = matchingMethod.getAnnotation(ExtendedStringParameter.class);
                    storeMethodPatterns(matchingMethod.getName(), annotation.endTokens());
                }));
        classpathScanner.scan();
    }

    private void storeMethodPatterns(String methodName, String[] endTokens) {
        String patternStart = methodName + identifierPattern;
        Arrays.stream(endTokens)
                .sorted(this::preferNonEOLTokens)
                .forEach(endToken -> methodPatterns.add(Pattern.compile(patternStart + endToken)));
    }

    /**
     * Always order a non EOL token after anything else.
     */
    private int preferNonEOLTokens(String o1, String o2) {
        if (EOL.equals(o1)) return 1;
        if (EOL.equals(o2)) return -1;
        return 0;
    }

    @Override
    public Reduction parseCST(SourceUnit sourceUnit, Reader reader) throws CompilationFailedException {
        BufferedReader bufferedReader = new BufferedReader(reader);
        String modifiedSource = bufferedReader.lines()
                .map(this::modifyLine)
                .collect(joining("\n"));
        Reader modifiedReader = new StringReader(modifiedSource);
        return super.parseCST(sourceUnit, modifiedReader);
    }

    private String modifyLine(String line) {
        return methodPatterns.stream()
                .map(pattern -> pattern.matcher(line))
                .filter(Matcher::find)
                .findFirst()
                .map(matcher -> {
                    String identifier = matcher.group(1);
                    String hexIdentifier = IDENTIFIER_MARKER + encodeHex(identifier.getBytes());
                    StringBuilder modifiedLine = new StringBuilder(line);
                    int identifierIndex = modifiedLine.indexOf(identifier);
                    modifiedLine.delete(identifierIndex, identifierIndex + identifier.length());
                    modifiedLine.insert(identifierIndex, hexIdentifier);
                    return modifiedLine.toString();
                })
                .orElse(line);
    }
}
