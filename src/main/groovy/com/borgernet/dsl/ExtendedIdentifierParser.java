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

import static com.borgernet.dsl.ExtendedIdentifierPattern.EOL;
import static com.borgernet.dsl.ExtendedIdentifierPattern.IDENTIFIER_MARKER;
import static java.util.stream.Collectors.joining;
import static org.codehaus.groovy.runtime.EncodingGroovyMethods.encodeHex;

/**
 * Rewrites the configured identifiers the source can be parsed as valid Groovy.
 */
public class ExtendedIdentifierParser extends AntlrParserPlugin {

    private static final String identifierPattern = "\\s+(\\S.*\\S)\\s*";

    private Collection<Pattern> identifierPatterns = new LinkedList<>();

    public void addPattern(String methodName) {
        addPattern(methodName, EOL);
    }

    public void addPattern(String methodName, String... endTokens) {
        String patternStart = methodName + identifierPattern;
        Arrays.stream(endTokens).forEach(endToken -> {
            identifierPatterns.add(Pattern.compile(patternStart + endToken));
        });
    }

    public void scanPackage(String packageName) {
        FastClasspathScanner classpathScanner = new FastClasspathScanner(packageName);
        classpathScanner.matchClassesWithMethodAnnotation(ExtendedIdentifierPattern.class,
                ((matchingClass, matchingMethod) -> {
                    ExtendedIdentifierPattern annotation = matchingMethod.getAnnotation(ExtendedIdentifierPattern.class);
                    addPattern(matchingMethod.getName(), annotation.endTokens());
                }));
        classpathScanner.scan();
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
        return identifierPatterns.stream()
                .sorted(ExtendedIdentifierParser::orderEOLPatternsLast)
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

    /**
     * Always order an EOL pattern after anything else.
     */
    private static int orderEOLPatternsLast(Pattern o1, Pattern o2) {
        boolean o1MatchesEOL = o1.pattern().contains(EOL);
        boolean o2MatchesEOL = o2.pattern().contains(EOL);
        if (!o1MatchesEOL && o2MatchesEOL) return -1;
        if (o1MatchesEOL && !o2MatchesEOL) return 1;
        return 0;
    }
}
