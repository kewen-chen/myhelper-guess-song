package com.chenkewen.administrator.myhelper.util;


import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.chenkewen.administrator.myhelper.R;
import com.chenkewen.administrator.myhelper.data.Const;
import com.chenkewen.administrator.myhelper.model.IAlertDialogButtonListener;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Administrator on 2016/3/21.
 */
public class Util {

    public static AlertDialog mAlertDialog;

    public static View getView(Context context, int layoutId) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(layoutId, null);
        return layout;
    }

    /**
     * 页面跳转
     * @param context
     * @param desti
     */
    public static void startActivity(Context context, Class desti) {
        Intent intent = new Intent();
        intent.setClass(context, desti);
        context.startActivity(intent);

        //关闭当前的activity
        ((AppCompatActivity)context).finish();
    }

    /**
     * 显示自定义对话框
     *
     * @param context
     * @param message
     * @param listener
     */
    public static void showDialog(final Context context, String message, final IAlertDialogButtonListener listener) {

        View dialogView = null;

        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.Theme_Transparent);

        dialogView = getView(context, R.layout.dialog_view);

        ImageButton btnOkView = (ImageButton) dialogView.findViewById(R.id.btn_dialog_ok);
        ImageButton btnCancelView = (ImageButton) dialogView.findViewById(R.id.btn_dialog_cancel);
        TextView textMessageView = (TextView) dialogView.findViewById(R.id.text_dialog_message);

        textMessageView.setText(message);

        btnOkView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //关闭对话框
                if (mAlertDialog != null) {
                    mAlertDialog.cancel();
                }

                //事件回调
                if (listener != null) {
                    listener.onClick();
                }

                //播放音效
                MyPlayer.playTone(context, MyPlayer.INDEX_SONG_ENTER);
            }
        });

        btnCancelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //关闭对话框
                if (mAlertDialog != null) {
                    mAlertDialog.cancel();
                }

                //播放音效
                MyPlayer.playTone(context, MyPlayer.INDEX_SONG_CANCEL);
            }
        });

        //为dialog设置View
        builder.setView(dialogView);
        mAlertDialog = builder.create();

        //显示对话框
        mAlertDialog.show();

    }

    /**
     * 数据的存储
     *
     * @param context
     * @param stageIndex
     * @param coins
     */
    public static void saveData(Context context, int stageIndex, int coins) {

        FileOutputStream fos = null;

        try {
            fos = context.openFileOutput(Const.FILE_NAME_SAVE_DATA, Context.MODE_PRIVATE);
            DataOutputStream dos = new DataOutputStream(fos);

            dos.writeInt(stageIndex);
            dos.writeInt(coins);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 读取游戏数据
     *
     * @param context
     * @return
     */
    public static int[] loadData(Context context) {
        FileInputStream fis = null;
        int[] datas = {-1, Const.TOTAL_COINS};

        try {
            fis = context.openFileInput(Const.FILE_NAME_SAVE_DATA);

            DataInputStream dis = new DataInputStream(fis);

            datas[Const.INDEX_LOAD_DATA_STAGE] = dis.readInt();
            datas[Const.INDEX_LOAD_DATA_COINS] = dis.readInt();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return datas;
    }


}
