abstract class DslScript extends Script {

    Set<Assay> assays = new HashSet<>()
    Device device

    def assay(String name) {
        def assay = new Assay(name: name)
        assays.add(assay)
        getBinding().setVariable(name, assay)
    }

    def device(String name) {
        device = new Device(name: name)
    }

    def propertyMissing(String propertyName) {
        propertyName
    }

}
