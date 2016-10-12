package com.example.readingbetter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.readingbetter.connect.GetConnect;
import com.example.readingbetter.connect.PutConnect;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ShopActivity extends Activity {
    private int arrayLength;
    private Long[] arrayNo;
    private Long[] arrayPoint;
    private Long point, no;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop_page);

        GridView gridView = (GridView) findViewById(R.id.gridView);
        TextView candy = (TextView) findViewById(R.id.candy);
        Button btnPrev = (Button) findViewById(R.id.btnPrev);
        MyGridAdapter gridAdater = new MyGridAdapter(this);
        gridView.setAdapter(gridAdater);

        no = LoginActivity.setting.getLong("no", 0);
        String url = "http://ec2-52-34-170-68.us-west-2.compute.amazonaws.com/GetShop.php?no=" + no;
        GetConnect getTask = new GetConnect(url);
        getTask.start();

        try {
            getTask.join();
            System.out.println("waiting... for result");
        } catch (InterruptedException e) {
        }
        String result = getTask.getResult();

        // JSON 파싱
        try {
            JSONObject root = new JSONObject(result);
            JSONArray ja = root.getJSONArray("result");
            arrayLength = ja.length();
            arrayNo = new Long[arrayLength];
            arrayPoint = new Long[arrayLength];

            // 작성자, 리뷰내용, 작성일 배열에 대입
            for (int i = 0; i < arrayLength; i++) {
                JSONObject jo = ja.getJSONObject(i);
                Long userNo = jo.getLong("no");
                Long userPoint = jo.getLong("point");

                arrayNo[i] = userNo;
                arrayPoint[i] = userPoint;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        point = arrayPoint[0];
        candy.setText("내 캔디  :  " + point + "개");

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public class MyGridAdapter extends BaseAdapter {
        Context context;
        Integer[] giftIcon = {R.drawable.gift01, R.drawable.gift02, R.drawable.gift03, R.drawable.gift04, R.drawable.gift05,
                R.drawable.gift06, R.drawable.gift07, R.drawable.gift08};
        String[] giftTitle = {"서울우유", "불닭볶음면", "신라면", "육개장", "참치마요 삼각김밥", "페로로로쉐", "박카스", "비타500"};
        Integer[] giftPrice = {5, 10, 10, 10, 7, 10, 8, 8};

        public MyGridAdapter(Context c) {
            context = c;
        }

        @Override
        public int getCount() {
            return giftIcon.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ImageView imageView = new ImageView(context);
            imageView.setLayoutParams(new GridView.LayoutParams(300, ViewGroup.LayoutParams.WRAP_CONTENT));
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setImageResource(giftIcon[position]);
            imageView.setPadding(5, 5, 5, 5);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View dialogView = (View) View.inflate(ShopActivity.this, R.layout.shop_dialog, null);
                    TextView gift = (TextView) dialogView.findViewById(R.id.gift);
                    TextView minusCandy = (TextView) dialogView.findViewById(R.id.minusCandy);
                    AlertDialog.Builder dlg = new AlertDialog.Builder(ShopActivity.this);
                    dlg.setTitle("구매확인");
                    gift.setText(giftTitle[position]);
                    minusCandy.setText(giftPrice[position] + "캔디");
                    dlg.setView(dialogView);
                    dlg.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getApplicationContext(), "구매를 취소하셨습니다", Toast.LENGTH_SHORT).show();
                        }
                    });

                    dlg.setPositiveButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            PutConnect putTask = new PutConnect();
                            if (point > giftPrice[position]) {
                                point = point - giftPrice[position];
                                String url = "http://ec2-52-34-170-68.us-west-2.compute.amazonaws.com/PutShop.php?no=" + no + "&point=" + point;
                                putTask.execute(url);

                                Toast.makeText(getApplicationContext(), "구매에 성공했습니다", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "캔디가 부족합니다", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    dlg.show();
                }
            });
            return imageView;
        }
    }
}