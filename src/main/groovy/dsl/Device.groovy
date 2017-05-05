package dsl

class Device {
    String name
    private int voltage
    private Assay assay

    def voltage(int voltage) {
        this.voltage = voltage
    }

    def assay(Assay assay) {
        this.assay = assay
    }
}
