package com.usal.jorgeav.sportapp.fields;

import com.usal.jorgeav.sportapp.data.FieldRepository;

/**
 * Created by Jorge Avila on 25/04/2017.
 */

public class FieldsPresenter implements FieldsContract.Presenter {

    FieldsContract.View mFieldsView;
    FieldRepository mFieldsRepository;

    public FieldsPresenter(FieldRepository fieldsRepository, FieldsContract.View fieldsView) {
        this.mFieldsRepository = fieldsRepository;
        this.mFieldsView = fieldsView;
    }

    @Override
    public void loadFields() {
        mFieldsView.showFields(mFieldsRepository.getDataset());
    }
}