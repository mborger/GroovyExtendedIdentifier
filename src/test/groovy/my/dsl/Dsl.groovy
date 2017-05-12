package my.dsl

import com.borgernet.dsl.DslScript
import com.borgernet.dsl.ExtendedStringParameter

abstract class Dsl extends DslScript {
    Set<Assay> assays = new HashSet<>()

    def identifierProperties = Collections.synchronizedMap([:])

    Dsl() {
        String.metaClass.setBindingValue = { String value ->
            identifierProperties[System.identityHashCode(delegate)] = value
        }
        String.metaClass.getBindingValue = {->
            identifierProperties[System.identityHashCode(delegate)]
        }
    }

    @ExtendedStringParameter
    def assay(String name) {
        def assay = new Assay(name: name)
        assays.add(assay)
        getBinding().setVariable(name, assay)
        assay
    }

    def compositeAssay(String name) {
        def compositeAssay = new CompositeAssay(name: name)
        getBinding().setVariable(name, compositeAssay)
        compositeAssay
    }

}
