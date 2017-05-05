import dsl.CustomPluginFactory
import dsl.Dsl
import dsl.DslBinding
import org.codehaus.groovy.control.CompilerConfiguration
import spock.lang.Specification

class DslTest extends Specification {
    def "Script Dsl"() {
        setup:
        def conf = new CompilerConfiguration()
        conf.scriptBaseClass = Dsl.name
        conf.pluginFactory = new CustomPluginFactory()
        def binding = new DslBinding()
        def shell = new GroovyShell(this.class.classLoader, binding, conf)

        when:
        Dsl result = shell.parse(new InputStreamReader(this.class.classLoader.getResourceAsStream("Script.dsl"))) as Dsl
        result.run()

        then:
        result.assays.size() == 1
        result.assays[0].name == "T143GA->C"
    }
}
