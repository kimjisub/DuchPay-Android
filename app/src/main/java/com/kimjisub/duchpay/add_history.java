package com.kimjisub.duchpay;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by jisub on 2015-07-16.
 */
public class add_history extends AppCompatActivity {
	
	ArrayList<String> name;
	내역 data;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_history);
		this.setTitle("내역 수정");
		final Intent intent = getIntent();
		
		name = intent.getStringArrayListExtra("name");
		String JSONData = intent.getStringExtra("data");
		
		if (JSONData == null || JSONData.length() == 0) {
			this.setTitle("새로운 내역 추가");
			data = new 내역();
			data.사용인원 = new String[0];
			data.시간 = System.currentTimeMillis();
			인원설정();
		} else {
			data = new 내역(JSONData);
		}
		
		((EditText) findViewById(R.id.제목)).setText(data.제목);
		((EditText) findViewById(R.id.제목)).setSelection(((EditText) findViewById(R.id.제목)).length());
		((TextView) findViewById(R.id.명)).setText(data.사용인원.length + "명");
		((Switch) findViewById(R.id.소비지불)).setChecked(data.소비지불);
		스위치((Switch) findViewById(R.id.소비지불), "소비 ", "지불 ");
		((Switch) findViewById(R.id.개인단체)).setChecked(data.개인단체);
		스위치((Switch) findViewById(R.id.개인단체), "개인 ", "단체 ");
		((EditText) findViewById(R.id.돈)).setText(String.valueOf(data.돈));
		
		findViewById(R.id.인원설정).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				인원설정();
			}
			
		});
		
		findViewById(R.id.소비지불).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				스위치((Switch) findViewById(R.id.소비지불), "소비 ", "지불 ");
			}
		});
		findViewById(R.id.개인단체).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				스위치((Switch) findViewById(R.id.개인단체), "개인 ", "단체 ");
			}
		});
		
		findViewById(R.id.완료).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				data.제목 = ((TextView) findViewById(R.id.제목)).getText().toString();
				data.소비지불 = ((Switch) findViewById(R.id.소비지불)).isChecked();
				data.개인단체 = ((Switch) findViewById(R.id.개인단체)).isChecked();
				data.돈 = Long.parseLong(((EditText) findViewById(R.id.돈)).getText().toString());
				
				intent.putExtra("data", data.toJSONObject().toString());
				
				setResult(RESULT_OK, intent);
				finish();
			}
		});
		
		findViewById(R.id.취소).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setResult(RESULT_CANCELED, intent);
				finish();
			}
		});
	}
	
	void 스위치(Switch 스위치, String 문자열1, String 문자열2) {
		if (!스위치.isChecked())
			스위치.setText(문자열1);
		else
			스위치.setText(문자열2);
	}
	
	void 인원설정() {
		AlertDialog.Builder 창 = new AlertDialog.Builder(add_history.this);
		창.setTitle("사용 인원");
		
		final ListView 리스트뷰 = new ListView(add_history.this);
		리스트뷰.setAdapter(new ArrayAdapter<String>(add_history.this, android.R.layout.simple_list_item_multiple_choice, name));
		리스트뷰.setItemsCanFocus(false);
		리스트뷰.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		
		if (data.사용인원 != null) {
			for (int i = 0; i < name.size(); i++) {
				for (int j = 0; j < data.사용인원.length; j++) {
					if (name.get(i).equals(data.사용인원[j]))
						리스트뷰.setItemChecked(i, true);
				}
			}
		}
		
		창.setView(리스트뷰);
		
		창.setPositiveButton("확인", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				data.사용인원 = new String[리스트뷰.getCheckedItemCount()];
				for (int i = 0; i < 리스트뷰.getCheckedItemCount(); i++)
					data.사용인원[i] = name.get((int) 리스트뷰.getCheckItemIds()[i]);
				((TextView) findViewById(R.id.명)).setText(data.사용인원.length + "명");
			}
		});
		
		창.show();
	}
}
