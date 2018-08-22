package com.usal.jorgeav.sportapp;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.usal.jorgeav.sportapp.mainactivities.ActivityContracts;

/**
 * Representa los Fragmentos de la aplicacion. Ha sido creada para implementar el codigo
 * comun al resto de Fragmentos, para que estos obtengan esa funcionalidad mediante herencia.
 */
public abstract class BaseFragment extends Fragment {
    /**
     * Nombre de la clase
     */
    @SuppressWarnings("unused")
    private static final String TAG = BaseFragment.class.getSimpleName();

    /**
     * Referencia a la interfaz {@link ActivityContracts.FragmentManagement} posiblemente
     * implementada por Actividad contenedora del Fragmento
     */
    protected ActivityContracts.FragmentManagement mFragmentManagementListener;
    /**
     * Referencia a la interfaz {@link ActivityContracts.NavigationDrawerManagement} posiblemente
     * implementada por Actividad contenedora del Fragmento
     */
    protected ActivityContracts.NavigationDrawerManagement mNavigationDrawerManagementListener;

    /**
     * En este metodo del ciclo de vida se asocian las varibales a la Actividad, en el caso de que
     * haya implementado las interfaces de {@link ActivityContracts}
     *
     * @param context {@link Context} de Actividad
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ActivityContracts.FragmentManagement)
            mFragmentManagementListener = (ActivityContracts.FragmentManagement) context;
        if (context instanceof ActivityContracts.NavigationDrawerManagement)
            mNavigationDrawerManagementListener = (ActivityContracts.NavigationDrawerManagement) context;
    }

    /**
     * Proceso inverso a {@link #onAttach(Context)}: anula las variables de las interfaces
     */
    @Override
    public void onDetach() {
        super.onDetach();
        hideSoftKeyboard();
        mFragmentManagementListener = null;
        mNavigationDrawerManagementListener = null;
    }

    /**
     *
     * @return {@link Context} de la Actividad contedora
     */
    public Context getActivityContext() {
        return getActivity();
    }

    /**
     *
     * @return {@link BaseFragment} que referencia a este Fragmento
     */
    public BaseFragment getThis() { return this;}

    /**
     * Utiliza {@link #mFragmentManagementListener} para mostrar el Fragmento
     */
    public void showContent() {
        if (mFragmentManagementListener != null)
            mFragmentManagementListener.showContent();
    }

    /**
     * Utiliza {@link #mFragmentManagementListener} para ocultar el Fragmento
     */
    public void hideContent() {
        if (mFragmentManagementListener != null)
            mFragmentManagementListener.hideContent();
    }

    /**
     * Obtiene una referencia a {@link FragmentManager} y la usa para limpiar
     * la pila de Fragmentos
     */
    public void resetBackStack() {
        getActivity().getSupportFragmentManager().popBackStack(
                getActivity().getSupportFragmentManager().getBackStackEntryAt(0).getId(),
                FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    /**
     * Obtiene una referencia a la {@link View} que tiene el foco de la interfaz y, si esta
     * mostrando el teclado flotante en la pantalla, lo esconde
     *
     * {@link
     * <a href= "https://stackoverflow.com/a/17789187/4235666">
     *     (StackOverflow) Close/hide the Android Soft Keyboard
     * </a>}
     */
    public void hideSoftKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null)
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
