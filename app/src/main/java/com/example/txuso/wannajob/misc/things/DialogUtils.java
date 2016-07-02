package com.example.txuso.wannajob.misc.things;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.v4.app.FragmentActivity;


/**
 * Created by Guillermo V2M on 08/03/2016.
 */
public class DialogUtils {
    private static ProgressDialog loading_progress = null;
    private static AlertDialog alertDialog = null;

    public static void showProgressDialog(Context context) {
        if (loading_progress == null && context != null) {
            loading_progress = new ProgressDialog(context);
            loading_progress.setCancelable(false);
            loading_progress.show();
        }
    }

    public static void showProgressDialog(Context context, int stringID) {
        if (loading_progress == null && context != null) {
            loading_progress = new ProgressDialog(context);
            loading_progress.setMessage(context.getString(stringID));
            loading_progress.setCancelable(false);
            loading_progress.show();
        }
    }

    public static void updateProgressDialog(Context mContext, final String message) {
        Activity activity = null;
        if (mContext instanceof FragmentActivity)
            activity = ((FragmentActivity) mContext);
        else if (mContext instanceof Activity)
            activity = ((Activity) mContext);

        if (activity != null) {
            activity.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (loading_progress != null) {
                        loading_progress.setMessage(message);
                        loading_progress.setCancelable(false);
                    }
                }
            });
        }
    }

    public static void dismissProgressDialog() {
        if(loading_progress != null && loading_progress.isShowing()) {
            try {
                loading_progress.dismiss();
            } catch (Exception ignored) {
            }
        }
        loading_progress = null;
    }

    public static void showAlertDialog(Context context, String string) {
        showAlertDialog(context, string, null);
    }

    public static void showAlertDialog(Context context, int stringId) {
        showAlertDialog(context, context.getString(stringId), null);
    }

    public static void showAlertDialog(Context context, int stringId, DialogInterface.OnClickListener listener) {
        if (context != null)
            showAlertDialog(context, context.getString(stringId), listener);
    }

    public static void showAlertDialog(Context context, String string, DialogInterface.OnClickListener listener) {
        if (context == null)
            return;
        if (context instanceof FragmentActivity && ((FragmentActivity) context).isFinishing())
            return;
        if (context instanceof Activity && ((Activity) context).isFinishing())
            return;

        AlertDialog.Builder builder = buildAlertDialog(context);
        builder.setCancelable(listener == null);
        builder.setMessage(string);
        builder.setPositiveButton(android.R.string.ok, listener);
        alertDialog = builder.create();
        alertDialog.show();
    }

    public static void showAlertDialogTwoButtonsTwoListeners(Context context, int stringId, DialogInterface.OnClickListener listener, DialogInterface.OnClickListener listenerCancel) {
        if (context instanceof FragmentActivity && ((FragmentActivity) context).isFinishing())
            return;
        if (context instanceof Activity && ((Activity) context).isFinishing())
            return;

        AlertDialog.Builder builder = buildAlertDialog(context);
        builder.setMessage(context.getString(stringId));
        builder.setPositiveButton(android.R.string.ok, listener);
        builder.setNegativeButton(android.R.string.cancel, listenerCancel);
        alertDialog = builder.create();
        alertDialog.show();
    }

    public static void showAlertDialogTwoButtons(Context context, int messageStringId, DialogInterface.OnClickListener positiveButttonListener) {
        if (context instanceof FragmentActivity && ((FragmentActivity) context).isFinishing())
            return;
        if (context instanceof Activity && ((Activity) context).isFinishing())
            return;

        AlertDialog.Builder builder = buildAlertDialog(context);
        builder.setMessage(context.getString(messageStringId));
        builder.setPositiveButton(android.R.string.ok, positiveButttonListener);
        builder.setNegativeButton(android.R.string.cancel, null);
        alertDialog = builder.create();
        alertDialog.show();
    }

    public static void dismissAlertDialog() {
        if(alertDialog != null) {
            alertDialog.dismiss();
        }
    }

    public static AlertDialog.Builder buildAlertDialog(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return new AlertDialog.Builder(context, android.R.style.Theme_Material_Light_Dialog_Alert);
        } else {
            return new AlertDialog.Builder(context);
        }
    }
}
