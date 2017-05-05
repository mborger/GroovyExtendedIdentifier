package dsl

abstract class DslScript extends Script {

    def propertyMissing(String propertyName) {
        if (propertyName.startsWith("__")) {
            def identifier = new String(propertyName.substring(2).decodeHex())
            identifier.setBindingValue(propertyName)
            return identifier
        }
        propertyName
    }

}
