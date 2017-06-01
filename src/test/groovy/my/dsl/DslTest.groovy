package my.dsl

import com.borgernet.dsl.DslBinding
import com.borgernet.dsl.ExtendedIdentifierParser
import org.codehaus.groovy.control.CompilerConfiguration
import spock.lang.Specification

class DslTest extends Specification {
    def "Script Dsl"() {
        setup:
        def conf = new CompilerConfiguration()
        conf.scriptBaseClass = Dsl.name
        conf.pluginFactory = {
                def parser = new ExtendedIdentifierParser()
                parser.scanPackage("my.dsl")
                parser.addPattern("manuallySpecifiedPatternMethod")
                parser
        }
        def binding = new DslBinding()
        def shell = new GroovyShell(this.class.classLoader, binding, conf)

        when:
        Dsl result = shell.parse(new InputStreamReader(this.class.classLoader.getResourceAsStream("Script.dsl"))) as Dsl
        result.run()

        then:
        result.assays.size() == 1
        result.assays.find { it.name == "RS123A->C" }
        result.compositeAssays.size() == 2
        result.compositeAssays.find { it.name == "RS456-789" }
    }
}
