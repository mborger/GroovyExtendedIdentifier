package com.borgernet.dsl

class DslBinding extends Binding {

    @Override
    void setVariable(String name, Object value) {
        String bindingValue = name.getBindingValue()
        if (bindingValue != null) {
            super.setVariable(bindingValue, value)
        } else {
            super.setVariable(name, value)
        }
    }

}
