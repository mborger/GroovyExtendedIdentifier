package my.dsl

import com.borgernet.dsl.ExtendedIdentifierPattern
import com.borgernet.dsl.ExtendedIdentifierScript

import static ExtendedIdentifierPattern.EOL

abstract class Dsl extends ExtendedIdentifierScript {
    Set<Assay> assays = new HashSet<>()
    Set<CompositeAssay> compositeAssays = new HashSet<>();

    @ExtendedIdentifierPattern
    def assay(String name) {
        def assay = new Assay(name: name)
        assays.add(assay)
        getBinding().setVariable(name, assay)
        assay
    }

    @ExtendedIdentifierPattern(endTokens = [EOL, "with"])
    def compositeAssay(String name) {
        def compositeAssay = new CompositeAssay(name: name)
        compositeAssays.add(compositeAssay)
        getBinding().setVariable(name, compositeAssay)
        compositeAssay
    }

    def manuallySpecifiedPatternMethod(Assay assay) {
        assays.add(assay);
    }

}
