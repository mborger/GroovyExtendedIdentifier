import org.codehaus.groovy.antlr.AntlrParserPlugin
import org.codehaus.groovy.control.CompilationFailedException
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.syntax.Reduction

class CustomParser extends AntlrParserPlugin {

    private static String METHOD = "assay"

    @Override
    Reduction parseCST(SourceUnit sourceUnit, Reader reader) throws CompilationFailedException {
        def text = modifySource(reader.text)
        def modifiedReader = new StringReader(text)
        super.parseCST(sourceUnit, modifiedReader)
    }

    private static String modifySource(String source) {
        def lines = source.tokenize('\n')*.trim()
        lines.collect {
            if (it.contains(METHOD)) {
                return globMethodArgument(it)
            }
            return it
        }
                .join '\n'
    }

    private static String globMethodArgument(String line) {
        StringBuilder sb = new StringBuilder(line)
        def argumentPosition = sb.indexOf(METHOD) + METHOD.length() + 1
        def base64Argument = '__' + sb.substring(argumentPosition).bytes.encodeHex()
        sb.delete(argumentPosition, sb.length())
        sb.insert(argumentPosition, base64Argument)
        sb.toString()
    }

}
