package com.usal.jorgeav.sportapp.fields.detail;

import android.support.annotation.NonNull;

import com.usal.jorgeav.sportapp.Utiles;
import com.usal.jorgeav.sportapp.data.Field;

/**
 * Created by Jorge Avila on 26/04/2017.
 */

public class DetailFieldPresenter implements DetailFieldContract.Presenter {
    private Field mField;
    private DetailFieldContract.View mView;

    public DetailFieldPresenter(@NonNull Field field, @NonNull DetailFieldContract.View view) {
        this.mField = field;
        this.mView = view;
    }

    @Override
    public void openField() {
        mView.showFieldId(mField.getmId());
        mView.showFieldName(mField.getmName());
        mView.showFieldAddress(mField.getmAddress());
        mView.showFieldRating(mField.getmRating());
        mView.showFieldSport(mField.getmSport());
        mView.showFieldOpeningTime(Utiles.millisToTimeString(mField.getmOpeningTime()));
        mView.showFieldClosingTime(Utiles.millisToTimeString(mField.getmClosingTime()));
    }
}
