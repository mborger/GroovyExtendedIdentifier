package com.borgernet.dsl

/**
 * Stores extended identifiers according to their binding value.
 *
 * @see com.borgernet.dsl.ExtendedIdentifierScript
 */
class ExtendedIdentifierBinding extends Binding {

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
