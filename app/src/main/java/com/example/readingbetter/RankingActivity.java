package com.example.readingbetter;

import android.app.*;
import android.content.*;
import android.os.*;
import android.widget.*;

// 랭킹 화면의 기본이 되는 탭 화면
public class RankingActivity extends TabActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ranking_page); // Layout ranking_page의 자바 파일

        TabHost tabHost = this.getTabHost(); // 뼈대로 쓰일 TabHost 생성

        // TabHost에 화면 전체 추가, RankingTotal 호출
        Intent goTotal = new Intent(this, RankingTotalActivity.class);
        TabHost.TabSpec tabSpecTotal = tabHost.newTabSpec("Total").setIndicator("전체");
        tabSpecTotal.setContent(goTotal);
        tabHost.addTab(tabSpecTotal);

        // TabHost에 화면 학년 추가, RankingGrade 호출
        Intent goGrade = new Intent(this, RankingSchoolActivity.class);
        TabHost.TabSpec tabSpecGrade = tabHost.newTabSpec("Grade").setIndicator("학교");
        tabSpecGrade.setContent(goGrade);
        tabHost.addTab(tabSpecGrade);

        tabHost.setCurrentTab(0);
    }
}