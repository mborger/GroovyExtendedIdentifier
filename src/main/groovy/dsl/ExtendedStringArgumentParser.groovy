package dsl

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner
import io.github.lukehutch.fastclasspathscanner.matchprocessor.MethodAnnotationMatchProcessor
import org.codehaus.groovy.antlr.AntlrParserPlugin
import org.codehaus.groovy.control.CompilationFailedException
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.syntax.Reduction

import java.lang.reflect.Method

class ExtendedStringArgumentParser extends AntlrParserPlugin {

    private Set<String> identifierMethods = new HashSet<>()

    ExtendedStringArgumentParser(String packageName) {
        def scanner = new FastClasspathScanner(packageName)
        scanner.matchClassesWithMethodAnnotation(ExtendedStringArgument,
                new MethodAnnotationMatchProcessor() {
                    @Override
                    void processMatch(Class<?> matchingClass, Method matchingMethod) {
                        if (!String.isAssignableFrom(matchingMethod.parameterTypes[0])) {
                            throw new IllegalStateException("The first argument must be a String")
                        }
                        identifierMethods.add(matchingMethod.getName())
                    }
                }
        )
        scanner.scan()
    }

    @Override
    Reduction parseCST(SourceUnit sourceUnit, Reader reader) throws CompilationFailedException {
        def text = modifySource(reader.text)
        def modifiedReader = new StringReader(text)
        super.parseCST(sourceUnit, modifiedReader)
    }

    private String modifySource(String source) {
        def lines = source.tokenize('\n')*.trim()
        lines.collect { line ->
            // see if any identifierMethods are in this line
            def identifier = identifierMethods.find { line.contains(it) }
            if (identifier != null) {
                return globMethodArgument(line, identifier)
            }
            return line
        }
                .join '\n'
    }

    private static String globMethodArgument(String line, String method) {
        StringBuilder sb = new StringBuilder(line)
        def argumentPosition = sb.indexOf(method) + method.length() + 1
        def base64Argument = '__' + sb.substring(argumentPosition).bytes.encodeHex()
        sb.delete(argumentPosition, sb.length())
        sb.insert(argumentPosition, base64Argument)
        sb.toString()
    }

}
