package com.borgernet.dsl

import static ExtendedIdentifierPattern.IDENTIFIER_MARKER
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
        if (propertyName.startsWith(IDENTIFIER_MARKER)) {
            def identifier = new String(propertyName.substring(IDENTIFIER_MARKER.length()).decodeHex())
            identifier.setBindingValue(propertyName)
            return identifier
        }
        propertyName
    }

}
