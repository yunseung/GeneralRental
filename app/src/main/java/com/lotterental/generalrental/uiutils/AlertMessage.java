package com.lotterental.generalrental.uiutils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lotterental.common.util.DisplayUtil;
import com.lotterental.generalrental.R;


/**
 * Created by ComAg on 16. 3. 21..
 */
public class AlertMessage extends PopupWindow implements View.OnClickListener, View.OnTouchListener {

    public static String TAG = "AlertMessage";
    // 타이틀 레이아웃
    LinearLayout layAlertTop;
    // 타이틀 텍스트 뷰
    TextView tvAlertTitle;
    // 내용 텍스트 뷰
    TextView tvAlertMessage;
    // 취소 버튼
    Button btnCancel;
    // 다른 기능의 버튼(가운데에 들어간다)
    Button btnOther;
    // 확인 버튼
    Button btnConfirm;
    // 타이틀
    String alertTitleText;
    // 메시지
    String alertMessageText;
    // 취소
    String alertCancelText;
    // 기타
    String alertOtherText;
    // 확인
    String alertConfirmText;
    Context context;
    private AlertFragmentListener alertFragmentListener;

    public AlertMessage(Context context) {

        this.context = context;

        initView();
    }

    public void setOnAlertFragmentListener(AlertFragmentListener alertFragmentListener) {

        this.alertFragmentListener = alertFragmentListener;
    }

    public void initView() {

        View view = LayoutInflater.from(context).inflate(R.layout.lay_alert_message, null);
        layAlertTop = (LinearLayout) view.findViewById(R.id.lay_alert_top);
        tvAlertTitle = (TextView) view.findViewById(R.id.tv_alert_title);
        tvAlertMessage = (TextView) view.findViewById(R.id.tv_alert_message);

        btnCancel = (Button) view.findViewById(R.id.btn_alert_cancel);
        btnOther = (Button) view.findViewById(R.id.btn_alert_other);
        btnConfirm = (Button) view.findViewById(R.id.btn_alert_confirm);

        btnCancel.setOnClickListener(this);
        btnOther.setOnClickListener(this);
        btnConfirm.setOnClickListener(this);

        view.setOnTouchListener(this);
        setContentView(view);

        // 현재 디스플레이 해상도를 구한다
        int[] displaySize = DisplayUtil.getDisplaySize(context);
        setWidth(displaySize[0]);
        setHeight(displaySize[1]);
    }

    /**
     * Override Func
     */
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//
//        return inflater.inflate(R.layout.lay_alert_message, container, false);
//    }
//
//    @Override
//    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        layAlertTop = (LinearLayout) getView().findViewById(R.id.lay_alert_top);
//        tvAlertTitle = (TextView) getView().findViewById(R.id.tv_alert_title);
//        tvAlertMessage = (TextView) getView().findViewById(R.id.tv_alert_message);
//
//        btnCancel = (Button) getView().findViewById(R.id.btn_alert_cancel);
//        btnOther = (Button) getView().findViewById(R.id.btn_alert_other);
//        btnConfirm = (Button) getView().findViewById(R.id.btn_alert_confirm);
//
//        btnCancel.setOnClickListener(this);
//        btnOther.setOnClickListener(this);
//        btnConfirm.setOnClickListener(this);
//
//        getView().setOnTouchListener(this);
//
//        defaultAlertInfo();
//    }
    private void defaultAlertInfo() {

        if (alertTitleText != null) {
            tvAlertTitle.setText(alertTitleText);
        } else {
            tvAlertTitle.setVisibility(View.GONE);
        }

        if (alertMessageText != null)
            tvAlertMessage.setText(alertMessageText);

        if (alertCancelText != null) {
            btnCancel.setText(alertCancelText);
            btnCancel.setVisibility(View.VISIBLE);
        } else {
            btnCancel.setVisibility(View.GONE);
        }

        if (alertOtherText != null) {
            btnOther.setVisibility(View.VISIBLE);
            btnOther.setText(alertOtherText);
        } else {
            btnOther.setVisibility(View.GONE);
        }


        if (alertConfirmText != null) {
            btnConfirm.setVisibility(View.VISIBLE);
            btnConfirm.setText(alertConfirmText);
        } else {
            btnConfirm.setVisibility(View.GONE);
        }

    }

    /**
     * 알럿 타이틀을 입력
     *
     * @param title
     */
    public void setAlertTitle(String title) {

        alertTitleText = title;
    }

    /**
     * Override Func
     */
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//
//        return inflater.inflate(R.layout.lay_alert_message, container, false);
//    }
//
//    @Override
//    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        layAlertTop = (LinearLayout) getView().findViewById(R.id.lay_alert_top);
//        tvAlertTitle = (TextView) getView().findViewById(R.id.tv_alert_title);
//        tvAlertMessage = (TextView) getView().findViewById(R.id.tv_alert_message);
//
//        btnCancel = (Button) getView().findViewById(R.id.btn_alert_cancel);
//        btnOther = (Button) getView().findViewById(R.id.btn_alert_other);
//        btnConfirm = (Button) getView().findViewById(R.id.btn_alert_confirm);
//
//        btnCancel.setOnClickListener(this);
//        btnOther.setOnClickListener(this);
//        btnConfirm.setOnClickListener(this);
//
//        getView().setOnTouchListener(this);
//
//        defaultAlertInfo();
//    }

    /**
     * 알럿 메시지를 입력
     *
     * @param message
     */
    public void setAlertMessage(String message) {

        alertMessageText = message;
    }

    /** User Func */

    /**
     * 취소 버튼에 텍스트를 입력
     *
     * @param cancel
     */
    public void setAlertButtonTexts(String cancel) {

        alertCancelText = cancel;
    }

    /**
     * 취소 및 확인 버튼에 텍스트를 입력
     *
     * @param cancel  : 취소
     * @param confirm : 확인
     */
    public void setAlertButtonTexts(String cancel, String confirm) {

        alertCancelText = cancel;
        alertConfirmText = confirm;
    }

    // Alert Button

    /**
     * 취소 및 확인 기타 버튼에 텍스트를 입력
     *
     * @param cancel  : 취소
     * @param other   : 기타
     * @param confirm : 확인
     */
    public void setAlertButtonTexts(String cancel, String other, String confirm) {

        alertCancelText = cancel;
        alertConfirmText = confirm;
        alertOtherText = other;
    }

    /**
     * 알럿 타이틀을 보이고 안보이고 여부
     * 기본으로 값은 true
     *
     * @param isShowTitle true : 보임 / false : 안보임
     */
    public void setShowTitle(boolean isShowTitle) {
        if (isShowTitle) {
            layAlertTop.setVisibility(View.VISIBLE);
        } else {
            layAlertTop.setVisibility(View.GONE);
        }
    }

    public void simpleAlert(String messgae) {

        alertMessageText = messgae;
        alertTitleText = "알림";
        alertConfirmText = "확인";

        defaultAlertInfo();

        showAtLocation(getContentView().getRootView(), Gravity.CENTER, 0, 0);
    }

    public void simpleAlert(String title, String btnText, String message, AlertFragmentListener alertFragmentListener) {

        alertMessageText = message;
        alertTitleText = title;
        alertConfirmText = btnText;

        this.alertFragmentListener = alertFragmentListener;

        defaultAlertInfo();

        showAtLocation(getContentView().getRootView(), Gravity.CENTER, 0, 0);
    }

    public void simpleAlertEvent(String message, AlertFragmentListener alertFragmentListener) {
        alertMessageText = message;
        alertTitleText = "알림";
        alertConfirmText = "확인";
        this.alertFragmentListener = alertFragmentListener;

        defaultAlertInfo();

        showAtLocation(getContentView().getRootView(), Gravity.CENTER, 0, 0);
    }

    public void simpleSelectAlertEvent(String message, AlertFragmentListener alertFragmentListener) {

        alertMessageText = message;
        alertTitleText = "알림";
        alertCancelText = "취소";
        alertConfirmText = "확인";

        this.alertFragmentListener = alertFragmentListener;

        defaultAlertInfo();

        showAtLocation(getContentView().getRootView(), Gravity.CENTER, 0, 0);
    }

    public void showFrag(String tag, AlertFragmentListener listener) {

        alertFragmentListener = listener;

        defaultAlertInfo();

        showAtLocation(getContentView().getRootView(), Gravity.CENTER, 0, 0);
    }

    @Override
    public void onClick(View v) {

        if (v.equals(btnCancel)) {

            if (alertFragmentListener != null)
                alertFragmentListener.onAlertEvent(AlertEventType.EventCancel);
        } else if (v.equals(btnOther)) {

            if (alertFragmentListener != null)
                alertFragmentListener.onAlertEvent(AlertEventType.EventOther);
        } else if (v.equals(btnConfirm)) {

            if (alertFragmentListener != null)
                alertFragmentListener.onAlertEvent(AlertEventType.EventConfirm);
        }

        dismiss();
//        getFragmentManager().popBackStack();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        return true;
    }

//    /**
//     * 메시지를 띄우는 펑션
//     *
//     * @param fm
//     * @param message
//     */
//    public void simpleAlert(Fragment fm, String message) {
//
//        alertMessageText = message;
//        alertTitleText = "알림";
//        alertConfirmText = "확인";
//
//        if(fm != null) {
//            FragmentTransaction ft = fm.getFragmentManager().beginTransaction();
//            ft.add(R.id.lay_base_message, this, TAG);
//            ft.addToBackStack(TAG);
//            ft.commitAllowingStateLoss();
//        }
//    }
//
//    public void simpleAlert(Fragment fm, String title, String btnText, String message, AlertFragmentListener alertFragmentListener) {
//
//        alertMessageText = message;
//        alertTitleText = title;
//        alertConfirmText = btnText;
//
//        this.alertFragmentListener = alertFragmentListener;
//
//        if(fm != null) {
//            FragmentTransaction ft = fm.getFragmentManager().beginTransaction();
//            ft.add(R.id.lay_base_message, this, TAG);
//            ft.addToBackStack(TAG);
//            ft.commitAllowingStateLoss();
//        }
//    }
//
//    public void simpleAlertEvent(Fragment fm, String message, AlertFragmentListener alertFragmentListener) {
//
//        alertMessageText = message;
//        alertTitleText = "알림";
//        alertConfirmText = "확인";
//
//        this.alertFragmentListener = alertFragmentListener;
//
//        if(fm != null) {
//            FragmentTransaction ft = fm.getFragmentManager().beginTransaction();
//            ft.add(R.id.lay_base_message, this, TAG);
//            ft.addToBackStack(TAG);
//            ft.commitAllowingStateLoss();
//        }
//    }
//
//    public void simpleSelectAlertEvent(Fragment fm, String message, AlertFragmentListener alertFragmentListener) {
//
//        alertMessageText = message;
//        alertTitleText = "알림";
//        alertCancelText = "취소";
//        alertConfirmText = "확인";
//
//        this.alertFragmentListener = alertFragmentListener;
//
//        if(fm != null) {
//            FragmentTransaction ft = fm.getFragmentManager().beginTransaction();
//            ft.add(R.id.lay_base_message, this, TAG);
//            ft.addToBackStack(TAG);
//            ft.commitAllowingStateLoss();
//        }
//    }
//
//    /**
//     * resource를 통해서 보여준다.
//     * @param fmgr
//     * @param tag
//     * @param listener
//     */
//    public void showFrag(FragmentManager fmgr, String tag, AlertFragmentListener listener) {
//
//        alertFragmentListener = listener;
//
//        if(fmgr != null) {
//            FragmentTransaction ft = fmgr.beginTransaction();
//            ft.add(R.id.lay_base_message, this, TAG);
//            ft.addToBackStack(tag);
//            ft.commitAllowingStateLoss();
//        }
//    }

//    /**
//     * 메시지를 띄우는 펑션
//     *
//     * @param fm
//     * @param message
//     */
//    public void simpleAlert(Fragment fm, String message) {
//
//        alertMessageText = message;
//        alertTitleText = "알림";
//        alertConfirmText = "확인";
//
//        if(fm != null) {
//            FragmentTransaction ft = fm.getFragmentManager().beginTransaction();
//            ft.add(R.id.lay_base_message, this, TAG);
//            ft.addToBackStack(TAG);
//            ft.commitAllowingStateLoss();
//        }
//    }
//
//    public void simpleAlert(Fragment fm, String title, String btnText, String message, AlertFragmentListener alertFragmentListener) {
//
//        alertMessageText = message;
//        alertTitleText = title;
//        alertConfirmText = btnText;
//
//        this.alertFragmentListener = alertFragmentListener;
//
//        if(fm != null) {
//            FragmentTransaction ft = fm.getFragmentManager().beginTransaction();
//            ft.add(R.id.lay_base_message, this, TAG);
//            ft.addToBackStack(TAG);
//            ft.commitAllowingStateLoss();
//        }
//    }
//
//    public void simpleAlertEvent(Fragment fm, String message, AlertFragmentListener alertFragmentListener) {
//
//        alertMessageText = message;
//        alertTitleText = "알림";
//        alertConfirmText = "확인";
//
//        this.alertFragmentListener = alertFragmentListener;
//
//        if(fm != null) {
//            FragmentTransaction ft = fm.getFragmentManager().beginTransaction();
//            ft.add(R.id.lay_base_message, this, TAG);
//            ft.addToBackStack(TAG);
//            ft.commitAllowingStateLoss();
//        }
//    }
//
//    public void simpleSelectAlertEvent(Fragment fm, String message, AlertFragmentListener alertFragmentListener) {
//
//        alertMessageText = message;
//        alertTitleText = "알림";
//        alertCancelText = "취소";
//        alertConfirmText = "확인";
//
//        this.alertFragmentListener = alertFragmentListener;
//
//        if(fm != null) {
//            FragmentTransaction ft = fm.getFragmentManager().beginTransaction();
//            ft.add(R.id.lay_base_message, this, TAG);
//            ft.addToBackStack(TAG);
//            ft.commitAllowingStateLoss();
//        }
//    }
//
//    /**
//     * resource를 통해서 보여준다.
//     * @param fmgr
//     * @param tag
//     * @param listener
//     */
//    public void showFrag(FragmentManager fmgr, String tag, AlertFragmentListener listener) {
//
//        alertFragmentListener = listener;
//
//        if(fmgr != null) {
//            FragmentTransaction ft = fmgr.beginTransaction();
//            ft.add(R.id.lay_base_message, this, TAG);
//            ft.addToBackStack(tag);
//            ft.commitAllowingStateLoss();
//        }
//    }

    public void alertClose() {

        dismiss();
//        getFragmentManager().popBackStack();
    }

    /**
     * Variable
     */
    public static enum AlertEventType {
        EventCancel, EventConfirm, EventOther
    }

    /**
     * Abstract Func
     */
    public interface AlertFragmentListener {
        public void onAlertEvent(AlertEventType alertEventType);
    }
}

