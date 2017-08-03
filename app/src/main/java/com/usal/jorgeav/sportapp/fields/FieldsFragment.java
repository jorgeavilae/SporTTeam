package com.usal.jorgeav.sportapp.fields;


public class FieldsFragment {//extends BaseFragment implements FieldsContract.View, FieldsAdapter.OnFieldItemClickListener {
//    private static final String TAG = FieldsFragment.class.getSimpleName();
//
//    FieldsContract.Presenter mFieldsPresenter;
//    FieldsAdapter mFieldsRecyclerAdapter;
//    ArrayList<Field> mFieldsList;
//    private static boolean sInitialize;
//
//    @BindView(R.id.fields_new_field)
//    Button fieldsNewField;
//    @BindView(R.id.fields_list)
//    RecyclerView fieldsRecyclerList;
//    @BindView(R.id.fields_placeholder)
//    ConstraintLayout fieldsPlaceholder;
//
//    public FieldsFragment() {
//        // Required empty public constructor
//    }
//
//    public static FieldsFragment newInstance(boolean createNewField) {
//        Bundle b = new Bundle();
//        //If is necessary to init NewField programmatically
//        if (createNewField)
//            b.putString(FieldsActivity.INTENT_EXTRA_CREATE_NEW_FIELD, "");
//        FieldsFragment fragment = new FieldsFragment();
//        fragment.setArguments(b);
//        sInitialize = false;
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        mFieldsPresenter = new FieldsPresenter(this);
//        mFieldsRecyclerAdapter = new FieldsAdapter(null, this);
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        View root = inflater.inflate(R.layout.fragment_fields, container, false);
//        ButterKnife.bind(this, root);
//
//        fieldsRecyclerList.setAdapter(mFieldsRecyclerAdapter);
//        fieldsRecyclerList.setHasFixedSize(true);
//        fieldsRecyclerList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
//
//        fieldsNewField.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (mFieldsList != null)
//                    ((FieldsActivity)getActivity()).startMapActivityForResult(mFieldsList, true);
//            }
//        });
//
//        hideContent();
//        return root;
//    }
//
//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        mFragmentManagementListener.setCurrentDisplayedFragment(getString(R.string.fields), this);
//        mActionBarIconManagementListener.setToolbarAsNav();
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//
//        mFieldsPresenter.loadNearbyFields(getLoaderManager(), getArguments());
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        mFieldsRecyclerAdapter.replaceData(null);
//    }
//
//    @Override
//    public void showFields(Cursor cursor) {
//        mFieldsList = UtilesContentProvider.cursorToMultipleField(cursor);
//
//        //If is necessary to init NewField programmatically
//        if (!sInitialize && getArguments() != null && getArguments().containsKey(FieldsActivity.INTENT_EXTRA_CREATE_NEW_FIELD)) {
//            fieldsNewField.callOnClick(); /*https://stackoverflow.com/a/18250395/4235666*/
//            sInitialize = true;
//            return;
//        }
//
//        mFieldsRecyclerAdapter.replaceData(cursor);
//        if (cursor != null && cursor.getCount() > 0) {
//            fieldsRecyclerList.setVisibility(View.VISIBLE);
//            fieldsPlaceholder.setVisibility(View.INVISIBLE);
//        } else {
//            fieldsRecyclerList.setVisibility(View.INVISIBLE);
//            fieldsPlaceholder.setVisibility(View.VISIBLE);
//        }
//
//        mFragmentManagementListener.showContent();
//    }
//
//    @Override
//    public void onFieldClick(String fieldId, String city, LatLng coordinates) {
//        Fragment newFragment = DetailFieldFragment.newInstance(fieldId, false);
//        mFragmentManagementListener.initFragment(newFragment, true);
//    }
}
