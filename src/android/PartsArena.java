package com.commontime.plugin;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class PartsArena extends CordovaPlugin {

    final String CONTENT_PROVIDER_URI = "content://com.infomill.partsarena20.picklist.picklistprovider/picklistitems";
    final String NO_SERACH_BRAND_ERROR = "No search brand";
    final String NO_SEARCH_TERM_ERROR = "No search term";
    final String JSON_EXCEPTION_ERROR = "Error processing parts list";
    final int PARTS_ARENA_REQUEST_CODE = 1;

    private PicklistContentObserver picklistObserver;
    private CallbackContext callbackContext;

    @Override
    public boolean execute(String action, final JSONArray data, final CallbackContext callbackContext) throws JSONException
    {
        this.callbackContext = callbackContext;

        if(action.equals("getPartsList"))
        {
            openPartsArenaApp();
            PluginResult pluginResult = new  PluginResult(PluginResult.Status.NO_RESULT);
            pluginResult.setKeepCallback(true);
            callbackContext.sendPluginResult(pluginResult);
        }
        else if(action.equals("search"))
        {
            String searchBrand = data.getString(0);
            String searchTerm = data.getString(1);
            if(TextUtils.isEmpty(searchBrand))
            {
                callbackContext.error(NO_SERACH_BRAND_ERROR);
                return true;
            }
            if(TextUtils.isEmpty(searchTerm))
            {
                callbackContext.error(NO_SEARCH_TERM_ERROR);
                return true;
            }
            search(searchBrand, searchTerm);
            callbackContext.success();
        }
        return true;
    }

    private void openPartsArenaApp()
    {
        PackageManager pm = cordova.getActivity().getPackageManager();
        Intent appStartIntent = pm.getLaunchIntentForPackage("com.infomill.partsarena20");
        appStartIntent.putExtra("finishOnSend", true);
        appStartIntent.putExtra("callingActivity",cordova.getActivity().getApplicationContext().getPackageName());
        appStartIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        cordova.startActivityForResult(this, appStartIntent, PARTS_ARENA_REQUEST_CODE);
    }

    private void search(String brand, String term)
    {
        PackageManager pm = cordova.getActivity().getPackageManager();
        Intent appStartIntent = pm.getLaunchIntentForPackage("com.infomill.partsarena20");
        appStartIntent.putExtra("searchBrand", brand);
        appStartIntent.putExtra("searchTerm", term);
        cordova.getActivity().startActivity(appStartIntent);
    }

    private List<PicklistItem> getPartsList()
    {
        String[] projection = {"_id", "partno", "partdescription", "partqty", "title" };
        Cursor c = cordova.getActivity().getContentResolver().query(Uri.parse(CONTENT_PROVIDER_URI), projection, null, null, null);
        List<PicklistItem> picklistItems = new ArrayList<PicklistItem>();
        if(c.moveToFirst())
        {
            do
            {
                PicklistItem item = new PicklistItem(
                        c.getInt(c.getColumnIndex("_id")),
                        c.getString(c.getColumnIndex("partno")),
                        c.getString(c.getColumnIndex("partdescription")),
                        c.getString(c.getColumnIndex("partqty")),
                        c.getString(c.getColumnIndex("title"))
                );
                picklistItems.add(item);
            } while (c.moveToNext());
        }
        c.close();
        return picklistItems;
    }

    @Override
    public void onPause(boolean multitasking)
    {
        super.onPause(multitasking);
        unregisterContentObserver();
    }

    @Override
    public void onResume(boolean multitasking)
    {
        super.onResume(multitasking);
        registerContentObserver();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        super.onActivityResult(requestCode, resultCode, intent);
        if(requestCode == PARTS_ARENA_REQUEST_CODE)
        {
            try
            {
                returnPickListJsonArray();
            }
            catch (JSONException e)
            {
                e.printStackTrace();
                callbackContext.error(JSON_EXCEPTION_ERROR);
            }
        }
    }

    private void returnPickListJsonArray() throws JSONException
    {
        List<PicklistItem> picklistItems = getPartsList();
        JSONArray joa = new JSONArray();
        for(PicklistItem item : picklistItems)
        {
            joa.put(item.toJsonObject());
        }
        callbackContext.success(joa);
    }

    private void registerContentObserver()
    {
        picklistObserver = new PicklistContentObserver(new Handler());
        cordova.getActivity().getContentResolver().registerContentObserver(Uri.parse(CONTENT_PROVIDER_URI), true, picklistObserver);
    }

    private void unregisterContentObserver()
    {
        cordova.getActivity().getContentResolver().delete(Uri.parse(CONTENT_PROVIDER_URI), null, null);
    }
}