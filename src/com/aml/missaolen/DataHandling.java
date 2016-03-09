package com.aml.missaolen;

import java.util.ArrayList;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Spinner;

public class DataHandling extends DialogFragment {
	
	public static final int SHOWDATE = 1;
	public static final int DELETE = 2;
	public static final int COPYDB = 3;
	Button btn;
	Button btndelete;
    Button btnclose;
    Button btncopy;
    static String dialogTitle;
    String selecteddate;
    
	public interface DataHandlingListener {
        void onFinishDataDialog(int type, String date, boolean in);
    }

    public DataHandling() {
    }
    
    public void setDialogTitle(String title, String selecteddate) {
    	dialogTitle = title;
    	this.selecteddate = selecteddate;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
    Bundle savedInstanceState) {
    	final View viewf = inflater.inflate(R.layout.fragment_data, container);

    	Spinner columnpinner = (Spinner) viewf.findViewById(R.id.columnDates);
	    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
	    		android.R.layout.simple_spinner_item, readDatesFromDb());
	    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    columnpinner.setAdapter(dataAdapter);
	    ArrayAdapter myAdap = (ArrayAdapter) columnpinner.getAdapter();
	    int pos = myAdap.getPosition(selecteddate);
	    columnpinner.setSelection(pos);
        
        btn = (Button) viewf.findViewById(R.id.btnDone);
        btn.setOnClickListener(new View.OnClickListener() 
        {
            public void onClick(View view) {
            	DataHandlingListener activity = (DataHandlingListener) getActivity();
                activity.onFinishDataDialog(SHOWDATE,(String)((Spinner)viewf.findViewById(R.id.columnDates)).getSelectedItem(),true);
                dismiss();
            }
        });
        btndelete = (Button) viewf.findViewById(R.id.btnDelete);
        btndelete.setOnClickListener(new View.OnClickListener() 
        {
            public void onClick(View view) {
            	DataHandlingListener activity = (DataHandlingListener) getActivity();
            	String date = (String)((Spinner)viewf.findViewById(R.id.columnDates)).getSelectedItem();
            	if(date.length() != 10)
            		return;
                activity.onFinishDataDialog(DELETE,date,true);
                dismiss();
            }
        });
        btnclose = (Button) viewf.findViewById(R.id.btnCancel);
        btnclose.setOnClickListener(new View.OnClickListener() 
        {
            public void onClick(View view) {
            	DataHandlingListener activity = (DataHandlingListener) getActivity();
                activity.onFinishDataDialog(0,null,true);
                dismiss();
            }
        });
        btncopy = (Button) viewf.findViewById(R.id.btnCopy);
        btncopy.setOnClickListener(new View.OnClickListener() 
        {
            public void onClick(View view) {
            	DataHandlingListener activity = (DataHandlingListener) getActivity();
            	RadioButton rb = (RadioButton) viewf.findViewById(R.id.rbin);
                activity.onFinishDataDialog(COPYDB,null,rb.isChecked());
                dismiss();
            }
        });
        getDialog().getWindow().setSoftInputMode(
                LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        getDialog().setTitle(dialogTitle);
        
        return viewf;
    }
    
    private ArrayList <String> readDatesFromDb(){
    	ArrayList <String>v = new ArrayList<String>();
    	v.add("");
    	v.add(getResources().getString(R.string.selectall));
    	DBAdapter db = new DBAdapter(getActivity());
	    db.readableopen();
	    Cursor c = db.getLocations(null,null,"date desc");
	    if(c != null ) {
	        if(c.moveToFirst()) {
	        	String newdate = "";
	            do {
				    String tmp = c.getString(0).substring(0, 10);
				    if(!tmp.equals(newdate))
				    	v.add(tmp);
				    newdate = tmp;
	            } while (c.moveToNext());
	        }
	    }
	    c.close();
	    db.close();
	    return v;
    }
}
