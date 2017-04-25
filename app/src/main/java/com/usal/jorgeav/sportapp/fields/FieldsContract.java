package com.usal.jorgeav.sportapp.fields;

import com.usal.jorgeav.sportapp.data.Field;

import java.util.List;

/**
 * Created by Jorge Avila on 25/04/2017.
 */

public class FieldsContract {

    public interface Presenter {
        void loadFields();
    }

    public interface View {
        void showFields(List<Field> fields);
    }
}
