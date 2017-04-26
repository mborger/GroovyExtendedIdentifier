abstract class DslScript extends Script {

    Set<Assay> assays = new HashSet<>()

    def assay(String name) {
        def assay = new Assay(name: name)
        assays.add(assay)
        getBinding().setVariable(name, assay)
    }

    def device(String name) {
        new Device(name: name)
    }

}
