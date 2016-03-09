package com.aml.missaolen;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

public class ScreenType extends DialogFragment {
    Button btn,btnc;
    static String dialogTitle;
    boolean type, savetrack;
    int pointE,pointN;
    String scale;
    
	public interface ScreenTypeListener {
        void onFinishInputDialog(boolean type,boolean savetrack,String startPointE,String startPointN,String scale);
    }

    public ScreenType() {
    }
    
    public void setDialogTitle(String title,boolean type,boolean savetrack, int pointE, int pointN,String scale) {
    	dialogTitle = title;
    	this.type = type;
    	this.savetrack = savetrack;
    	this.pointE = pointE;
    	this.pointN = pointN;
    	this.scale = scale;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
    Bundle savedInstanceState) {
    	final View viewf = inflater.inflate(R.layout.fragment_screentype, container);
    	((RadioGroup) viewf.findViewById(R.id.rdbGp1)).check(type ? R.id.rdb1 : R.id.rdb2);
    	((CheckBox) viewf.findViewById(R.id.chkTrack)).setChecked(savetrack);
    	if (scale != null) {
    		Spinner mSpinner = (Spinner) viewf.findViewById(R.id.scale_spinner);
    		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.option_arrays, android.R.layout.simple_spinner_item);
    		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    		mSpinner.setAdapter(adapter);
    	    int spinnerPosition = adapter.getPosition(scale);
    	    mSpinner.setSelection(spinnerPosition);
    	}
    	final int out[] = MissaOlenSettings.loadEandNsettings(getActivity());
    	if(out != null){
    		((EditText) viewf.findViewById(R.id.startPointE)).setText(Integer.toString(out[0]));
    		((EditText) viewf.findViewById(R.id.startPointN)).setText(Integer.toString(out[1]));
    	}
        btn = (Button) viewf.findViewById(R.id.btnDone);
        btn.setOnClickListener(new View.OnClickListener() 
        {
            public void onClick(View view) {
            	EditText ee = (EditText) viewf.findViewById(R.id.startPointE);
            	EditText en = (EditText) viewf.findViewById(R.id.startPointN);            	
            	int e = ee.getText().toString().length() == 0 ? 0 : Integer.parseInt(ee.getText().toString());
            	int n = en.getText().toString().length() == 0 ? 0 : Integer.parseInt(en.getText().toString());
            	if((e != 0 || n != 0) && (e < MyMapView.AREAE_MIN || e > MyMapView.AREAE_MAX || n < MyMapView.AREAN_MIN || n > MyMapView.AREAN_MAX))
            		return;
            	if(out == null || out[0] != e || out[1] != n)
            		MissaOlenSettings.saveEandNsettings(getActivity(),e, n);
            	ScreenTypeListener activity = (ScreenTypeListener) getActivity();
            	RadioButton rb = (RadioButton) viewf.findViewById(R.id.rdb1);
            	CheckBox cb = (CheckBox) viewf.findViewById(R.id.chkTrack);
            	Spinner spinner = (Spinner) viewf.findViewById(R.id.scale_spinner);
                activity.onFinishInputDialog(rb.isChecked(),cb.isChecked(),ee.getText().toString(),en.getText().toString(),spinner.getSelectedItem().toString());
                dismiss();
            }
        });
        btnc = (Button) viewf.findViewById(R.id.buttonCopy);
        btnc.setOnClickListener(new View.OnClickListener() 
        {
            public void onClick(View view) {
        		((EditText) viewf.findViewById(R.id.startPointE)).setText(String.valueOf(pointE));
        		((EditText) viewf.findViewById(R.id.startPointN)).setText(String.valueOf(pointN));
            }
        });

        getDialog().getWindow().setSoftInputMode(
                LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        getDialog().setTitle(dialogTitle);
        
        return viewf;
    }
}
