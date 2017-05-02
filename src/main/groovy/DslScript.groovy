abstract class DslScript extends Script {

    Set<Assay> assays = new HashSet<>()
    Device device
    def identifierProperties = Collections.synchronizedMap([:])

    DslScript() {
        String.metaClass.setBindingValue = { String value ->
            identifierProperties[System.identityHashCode(delegate)] = value
        }
        String.metaClass.getBindingValue = {->
            identifierProperties[System.identityHashCode(delegate)]
        }
    }

    def assay(String name) {
        def assay = new Assay(name: name)
        assays.add(assay)
        getBinding().setVariable(name, assay)
    }

    def device(String name) {
        device = new Device(name: name)
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
