package dsl

import org.codehaus.groovy.control.ParserPlugin
import org.codehaus.groovy.control.ParserPluginFactory

class CustomPluginFactory extends ParserPluginFactory {
    
    @Override
    ParserPlugin createParserPlugin() {
        new ExtendedStringArgumentParser("dsl")
    }
}
