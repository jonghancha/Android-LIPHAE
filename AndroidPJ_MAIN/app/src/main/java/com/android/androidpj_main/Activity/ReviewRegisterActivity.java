package com.android.androidpj_main.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.androidpj_main.Adapter.PrdReviewAdapter;
import com.android.androidpj_main.Bean.Review;
import com.android.androidpj_main.Bean.User;
import com.android.androidpj_main.Main.PreferenceManager;
import com.android.androidpj_main.NetworkTask.CUDNetworkTask;
import com.android.androidpj_main.NetworkTask.ReviewNetworkTask;
import com.android.androidpj_main.R;
import com.android.androidpj_main.Share.ShareVar;

import java.util.ArrayList;

// 21.01.25 지은 완료
public class ReviewRegisterActivity extends AppCompatActivity {

    final static String TAG = "ReviewRegisterActivity";

    WebView register_prdImg;
    TextView register_prdName, register_prdPrice;
    String prdImg, prdName, prdPrice, ordNo;

    TextView tv_star;
    Spinner review_star_spinner;

    Button btn_register_review;
    EditText et_review;


    String urlAddr_review = null;

    String userEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_register);
        setTitle("리뷰 작성 창");

        register_prdImg = findViewById(R.id.register_prdImg);   // 내가 구매한 상품 사진
        register_prdName = findViewById(R.id.register_prdName); // 내가 구매한 상품 이름
        register_prdPrice = findViewById(R.id.register_prdPrice);   // 내가 구매한 상품 가격

        Intent intent = getIntent();
        prdImg = intent.getStringExtra("prdFilename");
        prdName = intent.getStringExtra("prdName");
        prdPrice = intent.getStringExtra("prdPrice");
        ordNo = intent.getStringExtra("ordNo");


        register_prdName.setText(prdName);
        register_prdPrice.setText(prdPrice + " 원");
        Log.v(TAG, prdName);

        tv_star = findViewById(R.id.tv_star);
        review_star_spinner = findViewById(R.id.review_star_spinner);

        review_star_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                tv_star.setText((CharSequence) parent.getItemAtPosition(position));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        //
        btn_register_review = findViewById(R.id.btn_register_review);
        btn_register_review.setOnClickListener(registerOnClickListener);

        et_review = findViewById(R.id.et_review);

        // 저장된 아이디(이메일) 값
        userEmail = PreferenceManager.getString(ReviewRegisterActivity.this, "email");
    }


    View.OnClickListener registerOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_register_review:
                    String review_star = tv_star.getText().toString();
                    String review_content = et_review.getText().toString();


                    urlAddr_review = "http://" + ShareVar.macIP + ":8080/JSP/ReviewRegister.jsp?ordReview=" + review_content;
                    urlAddr_review = urlAddr_review +"&ordStar="+ review_star +"&userEmail=" + userEmail+"&ordNo="+ordNo;

                    connectRegisterReview();
                    Toast.makeText(ReviewRegisterActivity.this, prdName+" 상품에 대한 리뷰가 작성되었습니다.", Toast.LENGTH_SHORT).show();

                    break;
            }
        }



        // 리뷰 입력 받은값을 업데이트 시킴
        private void connectRegisterReview(){
            try {
                CUDNetworkTask reviewNetworkTask = new CUDNetworkTask(ReviewRegisterActivity.this, urlAddr_review);
                reviewNetworkTask.execute().get();

            }catch (Exception e){
                e.printStackTrace();
            }
        }

    };


    //editText 외의 화면 클릭시 키보드 숨기기
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


}