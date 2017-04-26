import org.codehaus.groovy.control.CompilerConfiguration
import spock.lang.Specification

class DslScriptTest extends Specification {
    def "Script Print Message"() {
        setup:
        def conf = new CompilerConfiguration()
        conf.setScriptBaseClass(DslScript.name)
        def shell = new GroovyShell(this.class.classLoader, new Binding(), conf)

        when:
        DslScript result = shell.parse(new InputStreamReader(this.class.classLoader.getResourceAsStream("Script.dsl")))
        result.run()

        then:
        result.getState()
    }
}
