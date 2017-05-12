package my.dsl

class CompositeAssay {
    String name
    private Assay assay

    def assay(Assay assay) {
        this.assay = assay
    }
}
