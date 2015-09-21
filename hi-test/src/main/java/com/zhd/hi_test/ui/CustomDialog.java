package com.zhd.hi_test.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.zhd.hi_test.R;
import com.zhd.hi_test.callback.IDialogCallback;


/**
 * Created by juiz on 2015/9/7.
 * 使用回调函数来获取dialog上面的信息
 */
public class CustomDialog extends DialogFragment {

    private IDialogCallback mCallback;
    private View mView;

    //回调函数
    public void setmCallback(IDialogCallback mCallback) {
        this.mCallback = mCallback;
    }

    //创建返回一个自定义弹出框
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //使用dialogbuild来创建
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //设置layoutInflater布局填充器来填充布局
        //LayoutInflater inflater=LayoutInflater.from(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        //将AlertDialog用布局填充器进行设置和填充(这里获取view对象，用来获取上面的信息)
        mView = inflater.inflate(R.layout.dialog_sign, null);
        builder.setView(mView)
                //设置确定按钮
                .setPositiveButton("创建", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mCallback.getInfo(mView);
                                CustomDialog.this.dismiss();
                            }
                        }
                )
                .setNegativeButton("取消", null);
        return builder.create();
    }
}
