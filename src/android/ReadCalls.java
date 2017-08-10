package br.com.daniel.readcalls;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.Manifest;
import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog;
import android.content.pm.PackageManager;

public class ReadCalls extends CordovaPlugin {

    public static final int LOG_CALL_REQ_CODE = 300;
    public static final int ALL_LOG_CALL_REQ_CODE = 400;
    public static final String PERMISSION_DENIED_ERROR = "Permissão de acesso ao telefone negada";
    public static final String READ_CALL_LOG = Manifest.permission.READ_CALL_LOG;

    private CallbackContext callbackContext;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        this.callbackContext = callbackContext;

        if (action.equals("getLastCall")) {
            if (cordova.hasPermission(READ_CALL_LOG))
                getCallDetails();
            else
                getReadLogPermission(LOG_CALL_REQ_CODE);
            return true;

        } else {
            if (cordova.hasPermission(READ_CALL_LOG))
                getAllCallDetails();
            else
                getReadLogPermission(ALL_LOG_CALL_REQ_CODE);
            return true;
        }
    }

    protected void getReadLogPermission(int requestCode) {
        cordova.requestPermission(this, requestCode, READ_CALL_LOG);
    }

    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults)
            throws JSONException {
        for (int r : grantResults) {
            if (r == PackageManager.PERMISSION_DENIED) {
                this.callbackContext.error(PERMISSION_DENIED_ERROR);
                return;
            }
        }
        switch (requestCode) {
        case LOG_CALL_REQ_CODE:
            getCallDetails();
            break;
        case ALL_LOG_CALL_REQ_CODE:
            getAllCallDetails();
            break;
        }
    }

    private void getCallDetails() throws JSONException {
        Cursor managedCursor = cordova.getActivity().getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null,
                null, null);
        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);

        managedCursor.moveToLast();
        String phNumber = managedCursor.getString(number);
        String callType = managedCursor.getString(type);
        String callDuration = managedCursor.getString(duration);
        String dir = null;
        int dircode = Integer.parseInt(callType);
        switch (dircode) {
        case CallLog.Calls.OUTGOING_TYPE:
            dir = "OUTGOING";
            break;
        case CallLog.Calls.INCOMING_TYPE:
            dir = "INCOMING";
            break;
        case CallLog.Calls.MISSED_TYPE:
            dir = "MISSED";
            break;
        }
        JSONObject retorno = new JSONObject();
        retorno.put("phoneNumber", phNumber);
        retorno.put("duration", callDuration);
        retorno.put("type", dir);

        callbackContext.success(retorno);
    }

    private void getAllCallDetails() throws JSONException {
        Cursor managedCursor = cordova.getActivity().getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null,
                null, null);
        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);

        JSONArray callsArray = new JSONArray();
        while (managedCursor.moveToNext()) {
            String phNumber = managedCursor.getString(number);
            String callType = managedCursor.getString(type);
            String callDuration = managedCursor.getString(duration);
            String dir = null;
            int dircode = Integer.parseInt(callType);
            switch (dircode) {
            case CallLog.Calls.OUTGOING_TYPE:
                dir = "OUTGOING";
                break;
            case CallLog.Calls.INCOMING_TYPE:
                dir = "INCOMING";
                break;
            case CallLog.Calls.MISSED_TYPE:
                dir = "MISSED";
                break;
            }
            JSONObject retorno = new JSONObject();
            retorno.put("phoneNumber", phNumber);
            retorno.put("duration", callDuration);
            retorno.put("type", dir);

            callsArray.put(retorno);
        }
        callbackContext.success(callsArray);
    }

}
