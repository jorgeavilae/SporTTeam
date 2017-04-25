package com.usal.jorgeav.sportapp.fields;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.usal.jorgeav.sportapp.R;
import com.usal.jorgeav.sportapp.data.Field;
import com.usal.jorgeav.sportapp.data.FieldRepository;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FieldsActivity extends AppCompatActivity implements FieldsContract.View {
    FieldsContract.Presenter mFieldsPresenter;
    FieldsAdapter mFieldsRecyclerAdapter;

    @BindView(R.id.fields_list)
    RecyclerView fieldsRecyclerList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_field);
        ButterKnife.bind(this);

        mFieldsPresenter = new FieldsPresenter(new FieldRepository(), this);
        mFieldsRecyclerAdapter = new FieldsAdapter(null);

        fieldsRecyclerList.setAdapter(mFieldsRecyclerAdapter);
        fieldsRecyclerList.setHasFixedSize(true);
        fieldsRecyclerList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFieldsPresenter.loadFields();
    }

    @Override
    public void showFields(List<Field> fields) {
        mFieldsRecyclerAdapter.replaceData(fields);
    }
}
