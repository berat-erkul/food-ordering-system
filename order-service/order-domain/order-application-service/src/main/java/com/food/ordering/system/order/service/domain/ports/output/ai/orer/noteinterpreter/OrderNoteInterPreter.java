package com.food.ordering.system.order.service.domain.ports.output.ai.orer.noteinterpreter;

import com.food.ordering.system.domain.valueobject.OrderPreferences;

public interface OrderNoteInterPreter {

     OrderPreferences interpret (String orderNotes);
}
