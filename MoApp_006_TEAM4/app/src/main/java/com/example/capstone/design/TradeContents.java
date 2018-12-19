package com.example.capstone.design;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.WeakHashMap;

public class TradeContents extends AppCompatActivity { //trade내용 올라온 아이템의 내용에서 match버튼을 클릭할시 나오는 dialog match 할 것인지 아닌지 나뉨

    List<CommentItem> lstComments;
    String DATE;
    String CONTENT;
    String TITLE;
    String URL;
    String UID;
    String Obj_info;
    String Table_name;
    String comment_userid;
    private static String comment_user_name;
    String comment_user_image;
    List<String> names;

    FirebaseAuth firebaseAuth;
    FirebaseDatabase database;
    CommunityWrite communityWrite = new CommunityWrite();

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();
    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference memRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trade_contents);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        CONTENT = intent.getStringExtra("CONTENT");
        DATE = intent.getStringExtra("DATE");
        TITLE = intent.getStringExtra("TITLE");
        UID = intent.getStringExtra("UID");
        URL = intent.getStringExtra("URL");
        Obj_info = intent.getStringExtra("Board_info");
        Table_name = intent.getStringExtra("Table_name");


        final ImageView item_Profile_Image = (ImageView) findViewById(R.id.Profile_image);
        final TextView item_profile_Name = (TextView) findViewById(R.id.Name);
        TextView item_Title = (TextView) findViewById(R.id.Title);
        TextView item_Content = (TextView) findViewById(R.id.Content);
        TextView item_Date = (TextView) findViewById(R.id.Date);
        final ImageView item_URL = (ImageView) findViewById(R.id.item_Image);


        item_Title.setText(TITLE);
        item_Content.setText(CONTENT);
        item_Date.setText(DATE);
        Picasso.with(TradeContents.this).load(URL).into(item_URL);

        // 작성자 정보 출력
        memRef = database.getInstance().getReference(); //멤버 테이블 안의 key인(UID)를 식별하겠다
        memRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.child("Member/" + UID).getChildren()) {
                    String value = ds.getKey();
                    if (value.equals("name")) {
                        String value1 = ds.getValue().toString();
                        item_profile_Name.setText(ds.getValue().toString());
                    }
                    if (value.equals("imageUri")) {
                        Picasso.with(TradeContents.this).load(Uri.parse(ds.getValue().toString())).into(item_Profile_Image);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //댓글추가
        ImageView ivAdd = (ImageView) findViewById(R.id.sendCom);
        ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText Content_selected = (EditText) findViewById(R.id.writeCom);
                final String Content = Content_selected.getText().toString();

                Calendar cal = Calendar.getInstance();
                Date date = cal.getTime();
                String today = (new java.text.SimpleDateFormat("yyyy-MM-dd").format(date));
                comment_userid = user.getUid();

                memRef = database.getInstance().getReference(); //멤버 테이블 안의 key인(UID)를 식별하겠다
                memRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot ds : dataSnapshot.child("Member/" + comment_userid).getChildren()) {
                            String value = ds.getKey();
                            if (value.equals("name")) {
                                comment_user_name = ds.getValue().toString();
                            }
                            if (value.equals("imageuri")) {
                                comment_user_image = ds.getValue().toString();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                CommunityWrite communityWrite = new CommunityWrite(comment_userid, Content, today);

                DatabaseReference databaseReference = firebaseDatabase.getReference(Table_name).child(Obj_info).child("comment");
                databaseReference.push().setValue(communityWrite);

                List<String> comments = Arrays.asList(Content);
                List<String> names = Arrays.asList("Anonymous");
                lstComments = new ArrayList<>();
                for (int i = 0, j = 0; i < comments.size() && j < names.size(); i++, j++) {
                    lstComments.add(new CommentItem(comment_user_image, comments.get(i), names.get(j), today));
                }
                Toast.makeText(TradeContents.this, "작성 완료", Toast.LENGTH_LONG).show();

                ListView listView = (ListView) findViewById(R.id.comment_trade);
                CommentAdapter commentAdapter = new CommentAdapter(TradeContents.this, R.layout.comment_item, lstComments);
                listView.setAdapter(commentAdapter);
            }
        });


    }

    public void showMsg() {

        //다이얼로그 객체 생성
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //속성 지정
        builder.setTitle("Real-time Trading System");
        builder.setMessage("Match 하시겠습니까?");
        //아이콘
        builder.setIcon(android.R.drawable.ic_dialog_alert);


        //예 버튼 눌렀을 때
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //텍스트 뷰 객체를 넣어줌..
                //Snackbar.make(textView ,"예버튼이 눌렸습니다",Snackbar.LENGTH_SHORT).show();
            }
        });


        //예 버튼 눌렀을 때
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //텍스트 뷰 객체를 넣어줌..
                //Snackbar.make(textView ,"아니오 버튼이 눌렸습니다",Snackbar.LENGTH_SHORT).show();
            }
        });
        //만들어주기
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}