package my.dsl

import com.borgernet.dsl.DslScript
import com.borgernet.dsl.ExtendedStringParameter

import static com.borgernet.dsl.ExtendedStringParameter.EOL

abstract class Dsl extends DslScript {
    Set<Assay> assays = new HashSet<>()
    Set<CompositeAssay> compositeAssays = new HashSet<>();

    @ExtendedStringParameter
    def assay(String name) {
        def assay = new Assay(name: name)
        assays.add(assay)
        getBinding().setVariable(name, assay)
        assay
    }

    @ExtendedStringParameter(endTokens = [EOL, "with"])
    def compositeAssay(String name) {
        def compositeAssay = new CompositeAssay(name: name)
        compositeAssays.add(compositeAssay)
        getBinding().setVariable(name, compositeAssay)
        compositeAssay
    }

}
