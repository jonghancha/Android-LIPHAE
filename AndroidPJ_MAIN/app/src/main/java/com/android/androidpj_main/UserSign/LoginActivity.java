package com.android.androidpj_main.UserSign;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.androidpj_main.Activity.PreferenceManager;
import com.android.androidpj_main.Main.MainActivity;
import com.android.androidpj_main.NetworkTask.NetworkTask_LogIn;
import com.android.androidpj_main.R;
import com.android.androidpj_main.Share.ShareVar;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.kakao.auth.AuthType;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.ApiErrorCode;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.callback.UnLinkResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.util.OptionalBoolean;
import com.kakao.util.exception.KakaoException;

//-----????????? ????????? ----------------------------
//import android.support.v7.app.AppCompatActivity;
//???????????????.

//--------------------------------------------


public class LoginActivity extends Activity {

                                //????????? - ?????????.

    //????????? ????????? ?????? ??????
    private SessionCallback sessionCallback;

    //?????? ?????????
    private FirebaseAuth auth; //????????? ????????? ?????? ??????
    private GoogleApiClient googleApiClient; //?????? API??????????????? ??????.
    private static final int REQ_SIGN_GOOGLE = 100; //?????? ????????? ?????? ??????.(100????????? ??????????????? ??????)

    //Network
    String macIP = ShareVar.macIP;
    final static String TAG = "LoginActivity";
    String urlAddr3 = null;
    String urlAddr4 = null;

    String sid, spw, sname, sphone;
    EditText Eid, Epw;
    TextView tvFindPw;
    ImageButton tvGoogle, tvKakao;
    TextView tvErrorId, tvErrorPw;
    Button btnLogin, btnSignup;

    //??????????????? field
   // Boolean loginChecked; //???????????? ????????? ????????? ??????.
    Boolean saveLoginData;
    //SharedPreferences pref;
    SharedPreferences appData;
    SharedPreferences.Editor editor; //@@@@@@@@@@
    CheckBox cbAutoLogin; //????????????.
    //?????????????????? ???????????? ??????.
    String id;
    String pwd;

    //????????? ????????? ??????.
    private String emailValidation = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ///////////////////////////////////////////////////////////////////////////
        //
        // 21.01.11 ??????
        //
        ///////////////////////////////////////////////////////////////////////////
        Button gomain = findViewById(R.id.gomain);
        gomain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);

                // PreferenceManager ????????? ????????????
                PreferenceManager.setString(LoginActivity.this, "email", "qkrtpa12@naver.com");
                finish();
            }
        });
        ///----------------------------------------------------------------------------------------------------


        //[???????????????] ????????? ????????????  ---------------------------------------------------------------------
        appData = getSharedPreferences("appData",MODE_PRIVATE);
                //--------
                  load(); //@@@@@@@@@
                //--------
        //------------------------------------------------------------------------------------------d

        tvFindPw = findViewById(R.id.tv_findPw);
        btnLogin = findViewById(R.id.btn_Login);
        btnSignup = findViewById(R.id.btn_Signup);
        //????????????
        cbAutoLogin = findViewById(R.id.cb_autoLogin);

        //EditText??????
        Eid = findViewById(R.id.et_id);
        Epw = findViewById(R.id.et_pw);

        //EditText??? ????????? ????????? ??????.
        Eid.setFilters(new InputFilter[]{new InputFilter.LengthFilter(25)});
        Epw.setFilters(new InputFilter[]{new InputFilter.LengthFilter(15)});

        //[???????????????] ????????? ????????? ????????? ???????????? ????????? ????????? -----------------------------------------------
        if(saveLoginData){

            Eid.setText(id);
            Epw.setText(pwd);
            cbAutoLogin.setChecked(saveLoginData);
        }
        //------------------------------------------------------------------------------------------

       // cbAutoLogin.setOnCheckedChangeListener(mClickListener);
        tvFindPw.setOnClickListener(onClickListener);
        btnLogin.setOnClickListener(onClickListener);
        btnSignup.setOnClickListener(onClickListener);


        //????????? ????????? ?????? ?????? ------------------------------------------------------------------------

        tvErrorId = findViewById(R.id.tv_errorId);
        Eid = findViewById(R.id.et_id);
        Eid.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {



                String sid = Eid.getText().toString().trim();

                if(sid.length() == 0){   //null?????????, ????????? ??????????????????

                    tvErrorId.setText("");

                }else {


                    if (sid.matches(emailValidation) && s.length() > 0) {

                        tvErrorId.setText("????????? ????????? ???????????????.");//??????.

                        Eid.setBackgroundResource(R.drawable.et_black); //??????.


                        String strColor = "#0000FF";

                        tvErrorId.setTextColor(Color.parseColor(strColor));

                        //     inputEmail.setBackgroundResource(R.drawable.edt_bg_selelctor);

                        //     btnReset.setBackgroundColor(Color.parseColor("#007ed6"));

                    } else {

                        tvErrorId.setText("????????? ????????? ????????? ????????????."); //??????.

                        String strColor2 = "#FF0000"; //??????.

                        tvErrorId.setTextColor(Color.parseColor(strColor2)); //????????????.

                        Eid.setBackgroundResource(R.drawable.et_red); //??????


                        //      inputEmail.setBackgroundResource(R.drawable.edt_bg_wrong_validate);

                        //     btnReset.setBackgroundColor(Color.parseColor("#c0c0c0"));
                    }

                }
            }
        });



    //[??????????????????]-----------------------------------------------------------------------------------

    //onCreate ??? SessionCallback??? ???????????????, ?????? ????????? ????????? ??????.

        sessionCallback = new SessionCallback(); //SessionCallback ?????????.
        Session.getCurrentSession().addCallback(sessionCallback); //?????? ????????? ?????? ??????.

   //     Session.getCurrentSession().checkAndImplicitOpen();  //??????????????? (??? ?????? ??? ????????? ????????? ????????? ???????????? ????????? ??????)@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ ????????? ???????????????.
        //checkAndImplicitOpen()????????? ?????? ?????? ????????? ?????????????????? ????????? ????????? ?????? ???????????? ???????????? ?????????.
        //???, ????????? ???????????? ????????? ?????????, ???????????? ?????? ????????? ???????????? ??????????????????.
        //checkAndImplicitOpen() ????????? ?????? ????????? ??? ?????? ?????? ????????? Activity??? onCreate ?????? ????????? ??????????????? ???????????????.
        // ???????????? LoginActivity??? ?????? ?????? ???????????? ????????? LoginActivity??? onCreate ?????? checkAndImplicitOpen() ????????? ?????????,
        // ?????? ?????? ???????????? ????????? Activity??? ?????? ?????? Activity(?????? ?????? ?????? ?????? Activity ???)??????, ????????? Activity??? ?????? ????????????
        // Activity??? onCreate ?????? checkAndImplicitOpen()??? ??????????????????.


        //????????? ????????? ????????? ??????
        tvKakao = findViewById(R.id.tv_kakao);
        tvKakao.setOnClickListener(kakaoClickListener);

    //--------------------------------------------
    //    getAppKeyHash(); //?????? ???.
    //--------------------------------------------

    //[???????????????]-------------------------------------------------------------------------------------

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        Log.v(TAG,"googleSignInOptions = " + googleSignInOptions);

        googleApiClient = new GoogleApiClient.Builder(this)
//                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();

        auth = FirebaseAuth.getInstance(); //?????????????????? ?????? ?????? ?????????.
        Log.v(TAG,"auth = " + auth);

        tvGoogle = findViewById(R.id.tv_google);
        tvGoogle.setOnClickListener(googleClickListener);

    }//@@@@@@@@@@@@@@@@@@@@@ OnCreate ???. ???????????? ?????????. @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@



    //[???????????????]?????? ????????? 2??? -------------------------------------------------------------------------

    private void save(){

        // SharedPreferences ??????????????? ?????? ????????? Editor ??????
        SharedPreferences.Editor editor = appData.edit();

        // ???????????????.put??????( ???????????? ??????, ???????????? ??? )
        // ???????????? ????????? ?????? ???????????? ????????????
        editor.putBoolean("SAVE_LOGIN_DATA", cbAutoLogin.isChecked());
        editor.putString("ID", Eid.getText().toString().trim());
        editor.putString("PWD", Epw.getText().toString().trim());

        // apply, commit ??? ????????? ????????? ????????? ???????????? ??????
        editor.apply();

    }

    // ???????????? ???????????? ??????
    private void load() {
        // SharedPreferences ??????.get??????( ????????? ??????, ????????? )
        // ????????? ????????? ???????????? ?????? ??? ?????????
        saveLoginData = appData.getBoolean("SAVE_LOGIN_DATA", false);
         id = appData.getString("ID", "");
         pwd = appData.getString("PWD", "");
    }


    //????????? ???????????? ??????????????? ?????????????????? ???????????????(???????????? ??????????????? ??????????????? ???????????? ???.)-----------------------
    private boolean loginValidation(String id, String password){

        SharedPreferences loginInfo = getSharedPreferences("setting",0);
        SharedPreferences.Editor editor = loginInfo.edit();

            //Preference?????? ????????? ????????? ???????????? true.
            if(loginInfo.getString("id", "").equals(sid) && loginInfo.getString("pw", "").equals(spw)){ //????????? ??????.
                return true;
            //????????? ????????? ????????? false.
            }else if(loginInfo.getString("id","").equals(null)){
                return false;
            //????????? ????????? ???????????? ?????????.
            }else{
                return false;
            }
    }



    //=== ?????? ?????? ??? ?????? ????????? ?????? =================================================================================


    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            tvErrorId = findViewById(R.id.tv_errorId);
            tvErrorPw = findViewById(R.id.tv_errorPw);



            switch(v.getId()) {

                //???????????? ?????? ?????????.
                case R.id.tv_findPw:
                    Intent intentPw = new Intent(LoginActivity.this, FindPwActivity.class);
                    startActivity(intentPw);
                    break;

                //???????????? ?????? ???.
                case R.id.btn_Signup:
                    Intent intentSignUp = new Intent(LoginActivity.this, SignupActivity.class);
                    startActivity(intentSignUp);
                    break;

                // ????????? ?????? ?????? ???
                case R.id.btn_Login:
                    sid = Eid.getText().toString().trim();
                    spw = Epw.getText().toString().trim();

                    if (sid.length() == 0) {
                        tvErrorId.setText("???????????? ??????????????????.");
                        String strColor = "#FF0000";
                        tvErrorId.setTextColor(Color.parseColor(strColor));
                        //????????????
                        Eid.requestFocus();
                        Eid.setCursorVisible(true);


                       /* new AlertDialog.Builder(LoginActivity.this)
                                .setTitle("???????????? ??????????????????!!")
                                .setMessage("")
                                .setPositiveButton("??????", null)
                                .show();*/

                    } else if (spw.length() == 0) {

                        tvErrorPw.setText("??????????????? ??????????????????.");
                        String strColor = "#FF0000";
                        tvErrorPw.setTextColor(Color.parseColor(strColor));
                        //????????????.
                        Epw.requestFocus();
                        Epw.setCursorVisible(true);

                        /*new AlertDialog.Builder(LoginActivity.this)
                                .setTitle("??????????????? ??????????????????!!")
                                .setMessage("")
                                .setPositiveButton("??????", null)
                                .show();*/

                    } else{

                        //----------
                            save();
                        //----------
                        Intent intent = getIntent();

                        //???????????? ????????? ????????? count?????? ???????????????.
                        urlAddr3 = "http://" + macIP + ":8080/JSP/loginCheck.jsp?"; //@@@@@@@@@@@@@@@@@@@@@@@@ JSP???
                        urlAddr3 = urlAddr3 + "id=" + sid + "&pw=" + spw;

                        //?????? ???????????? ?????? ???????????????.
        /*              urlAddr4 = "http://" + macIP + ":8080/test/loginGetData.jsp?";
                        urlAddr4 = urlAddr4 + "id=" + sid + "&pw=" + spw;*/
                        //??? ?????? ?????? PreferenceManager.setString????????? ????????? ???.

                        int count = connectLoginCheckData();
                        Log.v(TAG, "count =" + count);

                        switch (count) {
                            case 0: //???????????? ???????????? ???????????? ?????? ??????.

                                tvErrorPw.setText("???????????? ??????????????? ????????? ?????? ??????????????????.");
                                String strColor = "#FF0000";
                                tvErrorPw.setTextColor(Color.parseColor(strColor));

                               /* new AlertDialog.Builder(LoginActivity.this)
                                        .setTitle("[ID??? Password ?????????!!]")
                                        .setMessage("- ID??? Password??? ????????? ?????? ??????????????????. -")
                                        .setPositiveButton("??????", null)
                                        .show();*/
                                break;

                            case 1: //???????????? ???????????? ??????.

                                //SharedPreference??? ??????.(???????????? ???????????????)


                                sid = Eid.getText().toString().trim();
                                spw = Epw.getText().toString().trim();
                                // PreferenceManager ????????? ????????????
                                PreferenceManager.setString(LoginActivity.this, "email", sid);


                                SharedPreferences loginInfo = getSharedPreferences("setting",0);
                                SharedPreferences.Editor editor = loginInfo.edit();

                                editor.putString("id", sid); //id?????? ???????????? ??????.
                                editor.putString("pw", spw); //pw?????? ???????????? ??????.
                                editor.commit();                                  //???????????????(??????!!!)

                                Intent intentLogIn = new Intent(LoginActivity.this, MainActivity.class);

                                // ????????? ??? ????????? ????????? ???????????? ????????? ????????? String ?????? ??????
                                String checkId = loginInfo.getString("id","");
                                String checkPw = loginInfo.getString("pw","");
                                Log.v(TAG, "String checkId.end =" + checkId);
                                Log.v(TAG, "String checkPw.end =" + checkPw);

                                startActivity(intentLogIn);
                                finish();
                                break;

                        }
                    }
                    break;

            }



        }
    };

    private int connectLoginCheckData(){
        int result = 0;

        try{

            /////////////////////////////////////////////////////////////////////////////////////
            // Description:
            //  - NetworkTask??? ???????????? ???????????? ?????? ?????? CUDNetworkTask ??????
            //  - NetworkTask??? ????????? ?????? : where
            //
            ///////////////////////////////////////////////////////////////////////////////////////

            //NetworkTask ?????????.
            NetworkTask_LogIn networkTask_LogIn3 = new NetworkTask_LogIn(LoginActivity.this, urlAddr3, "loginCheck");
            ///////////////////////////////////////////////////////////////////////////////////////

            ///////////////////////////////////////////////////////////////////////////////////////
            // Description:
            //  - ?????? ?????? ?????? ?????? ?????? Object??? return
            //
            ///////////////////////////////////////////////////////////////////////////////////////
            Object obj = networkTask_LogIn3.execute().get();
            result = (int) obj;
            Log.v(TAG,"connectLoginCheckData :"+result);
            ///////////////////////////////////////////////////////////////////////////////////////

        }catch (Exception e){
            e.printStackTrace();
        }
        return result;

    }

    //[KaKao ???????????????]================================================================================================================

    View.OnClickListener kakaoClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            Session.getCurrentSession().open(AuthType.KAKAO_LOGIN_ALL, LoginActivity.this);

        }
    };


    @Override
    //????????? ????????? ?????????????????? ???????????? ???????????? ????????????, ????????? ?????? ??????.
    //?????? ????????? ????????? ??????????????? ?????? ?????? ??????????????????.
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        //@@@@@@@@@@@@@@@@@ ????????? ????????? ???????????? ?????? ????????? ?????? ??????@@@@@@@@@@@@@@@@@@@@@@@@@@@
        if(Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }

        //@@@@@@@@@@@@@@@@@ ??????  ????????? ???????????? ?????? ????????? ?????? ?????? @@@@@@@@@@@@@@@@@@@@@@@@@@@
        if (requestCode == REQ_SIGN_GOOGLE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess() == true) { //??????????????? ???????????????.
                GoogleSignInAccount account = result.getSignInAccount(); // account?????? ???????????? ?????? ??????????????? ????????? ??? ???????????????.(?????????, ???????????????url,?????????, ?????? ???)
                resultLogin(account); //????????? ????????? ?????? ??????????????? ?????????.
            }
        }
    }


    @Override
    protected void onDestroy() {  //onCreat?????? ?????? ????????? ????????? ????????????, ???????????? ????????? ???????????????. ?????????, ????????? ?????? ?????? API??? ?????? ?????????,??????????????? ???????????? ??????????????? ????????? ??????.
        //Activity Destroy ??? ????????? ????????? ?????? ??????
        //??? ????????? ????????? ??? ????????? ???????????? ?????? ??? ????????? ????????? ???????????? ??????.
        super.onDestroy();
        Session.getCurrentSession().removeCallback(sessionCallback);
    }

    //????????? ????????? ??????
    private class SessionCallback implements ISessionCallback {
        @Override

        //onSessionOpened????????? ???????????? ????????????, ???????????? ???????????? ?????????
        public void onSessionOpened() { //????????? ????????? ??????????????? ?????? ??????
            UserManagement.getInstance().me(new MeV2ResponseCallback() { //?????? ????????? ????????????.(me??? ??????????????? ???????????? ?????????)

                //?????? 3?????? ????????? ?????? ????????? ?????????.

                //1.???????????? ??????????????? ???????????? ?????????.
                @Override
                public void onFailure(ErrorResult errorResult) { //?????? ????????? ???????????? ??? ????????? ??????
                    int result = errorResult.getErrorCode(); //?????? ????????? ????????????.

                    if (result == ApiErrorCode.CLIENT_ERROR_CODE) { //??????????????? ????????? ??????: ???????????? ??????
                        Toast.makeText(getApplicationContext(), "???????????? ????????? ??????????????????. ?????? ????????? ?????????.", Toast.LENGTH_SHORT).show();
                        finish();
                    } else { //??????????????? ????????? ?????? ??????: ?????? ??????
                        Toast.makeText(getApplicationContext(), "????????? ?????? ????????? ??????????????????: " + errorResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                //2. ????????? ?????? ????????? ??????????????? ????????? ????????? ??? ???????????? ?????????.(??? ????????? ?????? ???????????? ???????????????)
                @Override
                public void onSessionClosed(ErrorResult errorResult) { //????????? ????????? ?????? ??????
                    Toast.makeText(getApplicationContext(), "????????? ???????????????. ?????? ????????? ?????????: " + errorResult.getErrorMessage(), Toast.LENGTH_SHORT).show();
                }

                //3.???????????? ??????????????? ?????? ?????????.
                @Override
                public void onSuccess(MeV2Response result) { //?????? ????????? ??????????????? ????????? ??????
                    //MeV2Response??? ????????? ????????? ???????????? ?????? ????????? ?????????. ????????? ??????????????? ????????? ????????? ???????????? ????????? ??? ?????????.

                    String needsScopeAutority = ""; //?????????, ??????, ?????????, ?????? ?????? ???????????? ?????? ?????????

                    if (result.getKakaoAccount().needsScopeAccountEmail()) { //????????? ????????? ???????????? ??? ???????????? ???????????? ?????? ??????
                        needsScopeAutority = needsScopeAutority + "?????????";
                    }
                    if (result.getKakaoAccount().needsScopeGender()) { //?????? ????????? ???????????? ??? ???????????? ???????????? ?????? ??????
                        needsScopeAutority = needsScopeAutority + ", ??????";
                    }
                    if (result.getKakaoAccount().needsScopeAgeRange()) { //????????? ????????? ???????????? ??? ???????????? ???????????? ?????? ??????
                        needsScopeAutority = needsScopeAutority + ", ?????????";
                    }
                    if (result.getKakaoAccount().needsScopeBirthday()) { //?????? ????????? ???????????? ??? ???????????? ???????????? ?????? ??????
                        needsScopeAutority = needsScopeAutority + ", ??????";
                    }

                    if (needsScopeAutority.length() != 0) { //????????? ????????? ?????? ??????
                        //????????? ????????? ?????????????????? Toast ????????? ??????
                        if (needsScopeAutority.charAt(0) == ',') {
                            needsScopeAutority = needsScopeAutority.substring(2);
                        }
                        Toast.makeText(getApplicationContext(), needsScopeAutority + "??? ?????? ????????? ???????????? ???????????????. ???????????? ????????? ??????????????????.", Toast.LENGTH_SHORT).show();

                        //???????????? ??????
                        //??????????????? ?????? ????????? ????????? MainActivity??? ???????????? ?????? ??????
                        UserManagement.getInstance().requestUnlink(new UnLinkResponseCallback() {
                            @Override
                            public void onFailure(ErrorResult errorResult) {
                                int result = errorResult.getErrorCode();

                                if (result == ApiErrorCode.CLIENT_ERROR_CODE) {
                                    Toast.makeText(getApplicationContext(), "???????????? ????????? ??????????????????. ?????? ????????? ?????????.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "????????? ??????????????????. ?????? ????????? ?????????.", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onSessionClosed(ErrorResult errorResult) {
                                Toast.makeText(getApplicationContext(), "????????? ????????? ???????????????. ?????? ???????????? ?????????.", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onNotSignedUp() {
                                Toast.makeText(getApplicationContext(), "???????????? ?????? ???????????????. ?????? ???????????? ?????????.", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onSuccess(Long result) {
                            }
                        });

                    } else { //?????? ????????? ??????????????? ??????????????????
                        //MainActivity??? ??????????????? ?????? ????????? ?????? ?????????
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        //intent.putExtra("name", result.getNickname()); //?????? ??????(String)
                        //intent.putExtra("profile", result.getProfileImagePath()); //?????? ????????? ?????? ??????(String)

                        if (result.getKakaoAccount().hasEmail() == OptionalBoolean.TRUE) {
                            intent.putExtra("email", result.getKakaoAccount().getEmail()); //???????????? ????????? -> ????????? ??? ?????????(String)

                            SharedPreferences loginInfo = getSharedPreferences("setting", 0);
                            SharedPreferences.Editor editor = loginInfo.edit();

                            String email = result.getKakaoAccount().getEmail();
                            editor.putString("id", email); //SharedPreference??? emil?????? ???????????? ??????.
                            editor.commit();

                            String checkId = loginInfo.getString("id", ""); //????????? ??????????????? ??????.
                            Log.v(TAG, "Kakao email ID :" + checkId);

                        } else
                            intent.putExtra("email", "none"); //???????????? ????????? -> ????????? ????????? none ????????????.
                        /*if (result.getKakaoAccount().hasAgeRange() == OptionalBoolean.TRUE)
                            intent.putExtra("ageRange", result.getKakaoAccount().getAgeRange().getValue()); //????????? ?????? ????????? -> ????????? ????????? String?????? ???????????? ?????????
                        else
                            intent.putExtra("ageRange", "none");
                        if (result.getKakaoAccount().hasGender() == OptionalBoolean.TRUE)
                            intent.putExtra("gender", result.getKakaoAccount().getGender().getValue()); //?????? ????????? ????????? -> ?????? ????????? String?????? ???????????? ?????????
                        else
                            intent.putExtra("gender", "none");
                        if (result.getKakaoAccount().hasBirthday() == OptionalBoolean.TRUE)
                            intent.putExtra("birthday", result.getKakaoAccount().getBirthday()); //?????? ????????? ????????? -> ?????? ????????? String?????? ???????????? ?????????
                        else
                            intent.putExtra("birthday", "none");*/

                        startActivity(intent);
                        finish();
                    }
                }
            });
        }

        @Override
        public void onSessionOpenFailed(KakaoException e) { //????????? ????????? ?????? ?????? ????????? ????????? ?????? -> Toast ???????????? ??????.
            //?????????, ???????????? ?????? ???????????? ??????????????? onSessionOpenFailed??? ?????????. ?????? ????????? ????????? ???????????? ??????(????????????????????? ???????????? ??????) onSessionOpened??? ?????????.
            Toast.makeText(getApplicationContext(), "????????? ?????? ????????? ??????????????????. ????????? ????????? ??????????????????: " + e.toString(), Toast.LENGTH_SHORT).show();
        }
    }



 /*       //-------- ?????? ??? ?????? ?????? -----------------------------------------------------------------
        private void getAppKeyHash() {
            try {
                PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
                for (Signature signature : info.signatures) {
                    MessageDigest md;
                    md = MessageDigest.getInstance("SHA");
                    md.update(signature.toByteArray());
                    String something = new String(Base64.encode(md.digest(), 0));

                    Log.e(TAG,"Hash key : " + something);
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                Log.e(TAG,"name not found" + e.toString());
            }
        }*/

    //[?????????????????????] ============================================================================================================================


    View.OnClickListener googleClickListener = new View.OnClickListener() { //?????? ????????? ????????? ???????????????.
        @Override
        public void onClick(View v) {
            Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
            startActivityForResult(intent, REQ_SIGN_GOOGLE);
        }
    };


/*    @Override @@@@@@@@@@@@@ ????????? ??????????????? ?????? @@@@@@@@@@@@@@@@@@@@@@@2
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) { //?????? ????????? ????????? ??????????????? ?????? ?????? ??????????????????.
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_SIGN_GOOGLE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess() == true) { //??????????????? ???????????????.
                GoogleSignInAccount account = result.getSignInAccount(); // account?????? ???????????? ?????? ??????????????? ????????? ??? ???????????????.(?????????, ???????????????url,?????????, ?????? ???)
                resultLogin(account); //????????? ????????? ?????? ??????????????? ?????????.
            }
        }
    }*/


    private void resultLogin(final GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>(){
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){ //???????????? ???????????????.
                            Toast.makeText(LoginActivity.this, "????????? ??????", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.putExtra("nickName", account.getDisplayName());
                            intent.putExtra("photoUrl", String.valueOf(account.getPhotoUrl())); //String.valueOf() ?????? ???????????? Stirng ????????? ??????.
                            intent.putExtra("email", account.getEmail());

                            startActivity(intent);
                            finish();

                        }else{ //???????????? ???????????????.
                            Toast.makeText(LoginActivity.this, "????????? ??????", Toast.LENGTH_SHORT).show();

                        }

                    }
                });
    }

    /*@Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }*/

    //================================================================================================================================================

    //editText ?????? ?????? ????????? ????????? ?????????
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View focusView = getCurrentFocus();
        if (focusView != null) {
            Rect rect = new Rect();
            focusView.getGlobalVisibleRect(rect);
            int x = (int) ev.getX(), y = (int) ev.getY();
            if (!rect.contains(x, y)) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (imm != null)
                    imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
                focusView.clearFocus();
            }
        }
        return super.dispatchTouchEvent(ev);
    }

}//-------------------