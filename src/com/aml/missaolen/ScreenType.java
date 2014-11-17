package com.aml.missaolen;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class ScreenType extends DialogFragment {
    Button btn,btnc;
    static String dialogTitle;
    boolean type, savetrack;
    int pointE,pointN;
    
	public interface ScreenTypeListener {
        void onFinishInputDialog(boolean type,boolean savetrack,String startPointE,String startPointN);
    }

    public ScreenType() {
    }
    
    public void setDialogTitle(String title,boolean type,boolean savetrack, int pointE, int pointN) {
    	dialogTitle = title;
    	this.type = type;
    	this.savetrack = savetrack;
    	this.pointE = pointE;
    	this.pointN = pointN;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
    Bundle savedInstanceState) {
    	final View viewf = inflater.inflate(R.layout.fragment_screentype, container);
    	((RadioGroup) viewf.findViewById(R.id.rdbGp1)).check(type ? R.id.rdb1 : R.id.rdb2);
    	((CheckBox) viewf.findViewById(R.id.chkTrack)).setChecked(savetrack);
    	String out[] = MissaOlenSettings.loadEandNsettings(getActivity());
    	if(out != null && out.length == 2){
    		((EditText) viewf.findViewById(R.id.startPointE)).setText(out[0]);
    		((EditText) viewf.findViewById(R.id.startPointN)).setText(out[1]);
    	}
        btn = (Button) viewf.findViewById(R.id.btnDone);
        btn.setOnClickListener(new View.OnClickListener() 
        {
            public void onClick(View view) {
            	ScreenTypeListener activity = (ScreenTypeListener) getActivity();
            	
            	RadioButton rb = (RadioButton) viewf.findViewById(R.id.rdb1);
            	CheckBox cb = (CheckBox) viewf.findViewById(R.id.chkTrack);
            	EditText ee = (EditText) viewf.findViewById(R.id.startPointE);
            	EditText en = (EditText) viewf.findViewById(R.id.startPointN);
            	MissaOlenSettings.saveEandNsettings(getActivity(),ee.getText().toString(), en.getText().toString());
                activity.onFinishInputDialog(rb.isChecked(),cb.isChecked(),ee.getText().toString(),en.getText().toString());
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
