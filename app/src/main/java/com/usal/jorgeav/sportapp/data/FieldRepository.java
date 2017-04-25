package com.usal.jorgeav.sportapp.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jorge Avila on 25/04/2017.
 */

public class FieldRepository {
    private ArrayList<Field> mDataset;

    public FieldRepository() {
    }

    public List<Field> getDataset() {
        if (this.mDataset == null) {
            mDataset = new ArrayList<>();
            loadFields();
        }
        return mDataset;
    }

    public void loadFields(/*query arguments*/) {
        ArrayList<Field> fieldsLoaded = new ArrayList<>();
        fieldsLoaded.add(new Field("Name", "Address Street, nº 10, 37007", 3.5f, "Sport", "op:en", "cl:se"));
        fieldsLoaded.add(new Field("Name", "Address Street, nº 10, 37007", 3.5f, "Sport", "op:en", "cl:se"));
        fieldsLoaded.add(new Field("Name", "Address Street, nº 10, 37007", 3.5f, "Sport", "op:en", "cl:se"));
        fieldsLoaded.add(new Field("Name", "Address Street, nº 10, 37007", 3.5f, "Sport", "op:en", "cl:se"));
        fieldsLoaded.add(new Field("Name", "Address Street, nº 10, 37007", 3.5f, "Sport", "op:en", "cl:se"));
        fieldsLoaded.add(new Field("Name", "Address Street, nº 10, 37007", 3.5f, "Sport", "op:en", "cl:se"));
        fieldsLoaded.add(new Field("Name", "Address Street, nº 10, 37007", 3.5f, "Sport", "op:en", "cl:se"));
        fieldsLoaded.add(new Field("Name", "Address Street, nº 10, 37007", 3.5f, "Sport", "op:en", "cl:se"));
        fieldsLoaded.add(new Field("Name", "Address Street, nº 10, 37007", 3.5f, "Sport", "op:en", "cl:se"));
        fieldsLoaded.add(new Field("Name", "Address Street, nº 10, 37007", 3.5f, "Sport", "op:en", "cl:se"));
        fieldsLoaded.add(new Field("Name", "Address Street, nº 10, 37007", 3.5f, "Sport", "op:en", "cl:se"));
        fieldsLoaded.add(new Field("Name", "Address Street, nº 10, 37007", 3.5f, "Sport", "op:en", "cl:se"));
        fieldsLoaded.add(new Field("Name", "Address Street, nº 10, 37007", 3.5f, "Sport", "op:en", "cl:se"));

        mDataset.addAll(fieldsLoaded);
    }
}
