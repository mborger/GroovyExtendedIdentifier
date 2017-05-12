package com.borgernet.dsl

import static java.lang.System.identityHashCode

abstract class DslScript extends Script {

    def static identifierProperties = Collections.synchronizedMap([:])

    static {
        String.metaClass.setBindingValue = { String value ->
            identifierProperties[identityHashCode(delegate)] = value
        }
        String.metaClass.getBindingValue = { ->
            identifierProperties[identityHashCode(delegate)]
        }
    }

    def propertyMissing(String propertyName) {
        if (propertyName.startsWith("__")) {
            def identifier = new String(propertyName.substring(2).decodeHex())
            identifier.setBindingValue(propertyName)
            return identifier
        }
        propertyName
    }

}
