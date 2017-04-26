import org.codehaus.groovy.control.CompilerConfiguration
import spock.lang.Specification

class DslScriptTest extends Specification {
    def "Script Dsl"() {
        setup:
        def conf = new CompilerConfiguration()
        conf.scriptBaseClass = DslScript.name
        conf.pluginFactory = new CustomPluginFactory()
        def shell = new GroovyShell(this.class.classLoader, new Binding(), conf)

        when:
        DslScript result = shell.parse(new InputStreamReader(this.class.classLoader.getResourceAsStream("Script.dsl"))) as DslScript
        result.run()

        then:
        result.assays.size() == 1
    }
}
