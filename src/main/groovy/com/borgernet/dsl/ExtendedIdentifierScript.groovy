package com.borgernet.dsl

import static ExtendedIdentifierPattern.IDENTIFIER_MARKER
import static java.lang.System.identityHashCode

/**
 * Base class for custom DSL scripts.
 * Any DSL that you want to take advantage of extended identifiers should extend this class.
 * It's purpose is to catch any unknown variables and offer to decode them if the are extended identifiers.
 * During that process the encoded value is stored so that the {@link com.borgernet.dsl.ExtendedIdentifierBinding Binding}
 * can access that value later because it will attempt variable resolution against the encoded value.
 */
abstract class ExtendedIdentifierScript extends Script {

    def static extendedIdentifiers = Collections.synchronizedMap([:])

    static {
        String.metaClass.setBindingValue = { String value ->
            extendedIdentifiers[identityHashCode(delegate)] = value
        }
        String.metaClass.getBindingValue = { ->
            extendedIdentifiers[identityHashCode(delegate)]
        }
    }

    def propertyMissing(String propertyName) {
        if (isExtendedIdentifier(propertyName))
            return decodeExtendedIdentifier(propertyName)
        throw new MissingPropertyException(propertyName, Void)
    }

    private static isExtendedIdentifier(String propertyName) {
        propertyName.startsWith(IDENTIFIER_MARKER)
    }

    /**
     * Decodes the original identifier string.
     * @param extendedIdentifier The encoded identifier
     * @return The original identifier string
     */
    private static String decodeExtendedIdentifier(String extendedIdentifier) {
        def identifier = new String(extendedIdentifier.substring(IDENTIFIER_MARKER.length()).decodeHex())
        identifier.setBindingValue(extendedIdentifier)
        return identifier
    }

}
