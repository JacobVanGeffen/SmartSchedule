package com.pennapps.smartschedule.storage;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.EditText;

public class DialogUtil {

    //TODO: totally not done
    public static EditText showGoogleAccountDialog(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        EditText text = new EditText(context);
        
        builder.setTitle("Google Account");
        builder.setView(text);
        
        return text;
    }
    
}
