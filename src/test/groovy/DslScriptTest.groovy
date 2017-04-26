import spock.lang.Specification

class DslScriptTest extends Specification {
    def "Script Print Message"() {
        setup:
        def script = new DslScript()

        when:
        def result = script.getTrue()

        then:
        result
    }
}
