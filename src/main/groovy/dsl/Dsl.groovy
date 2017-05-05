package dsl

abstract class Dsl extends DslScript {
    Set<Assay> assays = new HashSet<>()
    Device device
    def identifierProperties = Collections.synchronizedMap([:])

    Dsl() {
        String.metaClass.setBindingValue = { String value ->
            identifierProperties[System.identityHashCode(delegate)] = value
        }
        String.metaClass.getBindingValue = {->
            identifierProperties[System.identityHashCode(delegate)]
        }
    }

    @DslIdentifier
    def assay(String name) {
        def assay = new Assay(name: name)
        assays.add(assay)
        getBinding().setVariable(name, assay)
    }

    def device(String name) {
        device = new Device(name: name)
    }

}
