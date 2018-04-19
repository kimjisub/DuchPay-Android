package com.kimjisub.duchpay;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class MainActivity extends AppCompatActivity {
	
	ArrayList<내역> data = new ArrayList<>();
	ArrayList<String> name = new ArrayList<>();
	String unit = "원";
	
	LayoutInflater inflater;
	LinearLayout LL_List;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		init();
		
		findViewById(R.id.개인별정산).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
				dialog.setTitle("개인별 정산");
				
				final ListView 리스트뷰 = new ListView(MainActivity.this);
				리스트뷰.setAdapter(new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_multiple_choice, name));
				리스트뷰.setItemsCanFocus(false);
				리스트뷰.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
				
				리스트뷰.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						String 선택된이름 = name.get(position);
						AlertDialog.Builder 창 = new AlertDialog.Builder(MainActivity.this);
						창.setTitle(선택된이름);
						
						LinearLayout 목록 = new LinearLayout(MainActivity.this);
						목록.setOrientation(LinearLayout.VERTICAL);
						
						for (int i = 0; i < data.size(); i++) {
							내역 정보 = data.get(i);
							for (int j = 0; j < 정보.사용인원.length; j++) {
								for (int a = 0; a < name.size(); a++) {
									if (정보.사용인원[j].equals(선택된이름)) {
										TextView 텍스트뷰 = new TextView(MainActivity.this);
										String 표시할정보 = "초기화되지 않음";
										
										if (!정보.개인단체 && !정보.소비지불)
											표시할정보 = " - " + 정보.돈;
										else if (!정보.개인단체 && 정보.소비지불)
											표시할정보 = " + " + 정보.돈;
										else if (정보.개인단체 && !정보.소비지불)
											표시할정보 = " - " + 정보.돈 / 정보.사용인원.length;
										else if (정보.개인단체 && 정보.소비지불)
											표시할정보 = " + " + 정보.돈 / 정보.사용인원.length;
										
										표시할정보 += unit;
										
										텍스트뷰.setText(표시할정보 + "  (" + 정보.제목 + ")");
										목록.addView(텍스트뷰);
										break;
									}
								}
							}
						}
						
						TextView 텍스트뷰 = new TextView(MainActivity.this);
						텍스트뷰.setText("────────────────\n" + 남은돈_목록_가져오기()[position] + unit);
						목록.addView(텍스트뷰);
						
						창.setView(목록);
						창.setPositiveButton("확인", null);
						창.show();
					}
				});
				
				dialog.setView(리스트뷰);
				dialog.setPositiveButton("확인", null);
				dialog.show();
			}
		});
		
		update(false);
	}
	
	void init() {
		inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LL_List = ((LinearLayout) findViewById(R.id.내역리스트));
		
		load();
	}
	
	
	String load() {
		String loadString = setting.dataSave.load(this);
		
		unit = setting.unit.load(this);
		
		name = new ArrayList<>();
		data = new ArrayList<>();
		try {
			JSONObject JsonObject = new JSONObject(loadString);
			JSONArray people = JsonObject.getJSONArray("people");
			JSONArray list = JsonObject.getJSONArray("list");
			
			for (int i = 0; i < people.length(); i++)
				name.add(people.getString(i));
			
			for (int i = 0; i < list.length(); i++)
				data.add(new 내역(list.getJSONObject(i)));
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return loadString;
	}
	
	String save() {
		
		setting.unit.save(this, unit);
		
		try {
			JSONObject JsonObject = new JSONObject();
			JSONArray people = new JSONArray();
			JSONArray list = new JSONArray();
			
			for (int i = 0; i < name.size(); i++)
				people.put(name.get(i));
			
			for (int i = 0; i < data.size(); i++)
				list.put(data.get(i).toJSONObject());
			
			JsonObject.put("people", people);
			JsonObject.put("list", list);
			
			setting.dataSave.save(this, JsonObject.toString());
			return JsonObject.toString();
		} catch (JSONException e) {
			e.printStackTrace();
			return "JSONException";
		}
	}
	
	void update(boolean save) {
		
		if (save)
			save();
		if (!save)
			load();
		
		LL_List.removeAllViews();
		
		for (int i = 0; i < data.size(); i++) {
			final 내역 tmp = data.get(i);
			LinearLayout LL_item = (LinearLayout) inflater.inflate(R.layout.activity_main_item, null);
			
			((TextView) LL_item.findViewById(R.id.제목)).setText((tmp.소비지불 ? "+ " : "- ") + tmp.제목);
			((TextView) LL_item.findViewById(R.id.사용인원)).setText(tmp.사용인원.length + "명");
			((TextView) LL_item.findViewById(R.id.시간)).setText(new SimpleDateFormat("MM월 dd일 a hh시 mm분").format(new Date(tmp.시간)));
			((TextView) LL_item.findViewById(R.id.돈)).setText(tmp.돈 + unit);
			
			final int finalI = i;
			LL_item.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent 인텐트 = new Intent(MainActivity.this, add_history.class);
					인텐트.putExtra("data", tmp.toJSONObject().toString());
					인텐트.putExtra("name", name);
					
					
					startActivityForResult(인텐트, 0x10 + finalI);
				}
			});
			LL_item.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					final CharSequence[] 추가기능 = {"삭제"};
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
					
					alertDialogBuilder.setTitle("추가기능");
					alertDialogBuilder.setItems(추가기능, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							switch (id) {
								case 0:
									data.remove(finalI);
									update(true);
									break;
							}
						}
					});
					AlertDialog alertDialog = alertDialogBuilder.create();
					alertDialog.show();
					return false;
				}
			});
			
			LL_List.addView(LL_item);
		}
		LinearLayout LL_add = (LinearLayout) inflater.inflate(R.layout.activity_main_add_item, null);
		
		LL_add.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent 인텐트 = new Intent(MainActivity.this, add_history.class);
				인텐트.putExtra("name", name);
				startActivityForResult(인텐트, 0x01);
			}
		});
		LL_List.addView(LL_add);
		
		long[] 돈 = 남은돈_목록_가져오기();
		String 결과 = "결산";
		long 총예산 = 0;
		for (int i = 0; i < name.size(); i++) {
			결과 += "\n" + name.get(i) + " : " + 돈[i] + unit;
			총예산 += 돈[i];
		}
		((TextView) findViewById(R.id.정산)).setText(결과 + "\n\n총 예산 : " + 총예산 + unit);
		
	}
	
	long[] 남은돈_목록_가져오기() {
		long[] 돈 = new long[name.size()];
		
		
		for (int i1 = 0; i1 < data.size(); i1++) {
			내역 정보 = data.get(i1);
			for (int i2 = 0; i2 < 정보.사용인원.length; i2++) {
				for (int i3 = 0; i3 < name.size(); i3++) {
					if (정보.사용인원[i2].equals(name.get(i3))) {
						if (!정보.개인단체 && !정보.소비지불)
							돈[i3] -= 정보.돈;
						else if (!정보.개인단체 && 정보.소비지불)
							돈[i3] += 정보.돈;
						else if (정보.개인단체 && !정보.소비지불)
							돈[i3] -= 정보.돈 / 정보.사용인원.length;
						else if (정보.개인단체 && 정보.소비지불)
							돈[i3] += 정보.돈 / 정보.사용인원.length;
						
						break;
					}
				}
			}
		}
		return 돈;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (resultCode == RESULT_OK) {
			if (requestCode == 1)
				this.data.add(new 내역(data.getStringExtra("data")));
			else
				this.data.set(requestCode - 0x10, new 내역(data.getStringExtra("data")));
			update(true);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}
	
	class 에디터엑션리스너 implements TextView.OnEditorActionListener {
		int i = 0;
		ArrayList<EditText> 이름목록;
		LinearLayout 리니어레이아웃;
		
		public 에디터엑션리스너(int i, ArrayList<EditText> 이름목록, LinearLayout 리니어레이아웃) {
			this.i = i;
			this.이름목록 = 이름목록;
			this.리니어레이아웃 = 리니어레이아웃;
		}
		
		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			if (actionId == EditorInfo.IME_ACTION_DONE) {
				if (i >= 이름목록.size()) {
					EditText 에딧텍스트 = new EditText(MainActivity.this);
					에딧텍스트.setImeOptions(EditorInfo.IME_ACTION_DONE);
					에딧텍스트.setSingleLine();
					에딧텍스트.setHint("인원을 추가하세요");
					에딧텍스트.setOnEditorActionListener(new 에디터엑션리스너(i + 1, 이름목록, 리니어레이아웃));
					
					이름목록.add(에딧텍스트);
					리니어레이아웃.addView(에딧텍스트);
				}
				이름목록.get(i).requestFocus();
				이름목록.get(i).selectAll();
			}
			return false;
		}
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		int id = item.getItemId();
		
		
		if (id == R.id.인원추가) {
			final ArrayList<EditText> 이름목록 = new ArrayList();
			
			ScrollView scrollView;
			LinearLayout linearLayout;
			
			AlertDialog.Builder 창 = new AlertDialog.Builder(MainActivity.this);
			창.setTitle("인원 설정");
			
			scrollView = new ScrollView(MainActivity.this);
			linearLayout = new LinearLayout(MainActivity.this);
			linearLayout.setOrientation(LinearLayout.VERTICAL);
			
			
			for (int i = 0; i < name.size(); i++) {
				EditText editText = new EditText(MainActivity.this);
				editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
				editText.setSingleLine();
				editText.setText(name.get(i));
				editText.setHint("인원을 추가하세요");
				editText.setOnEditorActionListener(new 에디터엑션리스너(i + 1, 이름목록, linearLayout));
				
				이름목록.add(editText);
				linearLayout.addView(editText);
			}
			EditText 에딧텍스트 = new EditText(MainActivity.this);
			에딧텍스트.setImeOptions(EditorInfo.IME_ACTION_DONE);
			에딧텍스트.setSingleLine();
			에딧텍스트.setHint("인원을 추가하세요");
			에딧텍스트.setOnEditorActionListener(new 에디터엑션리스너(name.size() + 1, 이름목록, linearLayout));
			
			이름목록.add(에딧텍스트);
			linearLayout.addView(에딧텍스트);
			
			scrollView.addView(linearLayout);
			창.setView(scrollView);
			
			창.setPositiveButton("확인", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					name = new ArrayList<>();
					
					for (int i = 0; i < 이름목록.size(); i++)
						if (이름목록.get(i).getText().toString().length() != 0)
							name.add(이름목록.get(i).getText().toString());
					update(true);
				}
			});
			
			창.show();
			이름목록.get(0).setSelection(이름목록.get(0).length());
		} else if (id == R.id.데이터보기) {
			AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
			dialog.setTitle("JSONData");
			
			final EditText editText = new EditText(MainActivity.this);
			
			editText.setText(load());
			dialog.setView(editText);
			
			
			dialog.setPositiveButton("취소", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					update(true);
				}
			});
			dialog.setPositiveButton("저장하기", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					setting.dataSave.save(MainActivity.this, editText.getText().toString());
					update(false);
				}
			});
			
			dialog.show();
		} else if (id == R.id.초기화) {
			data = new ArrayList<>();
			name = new ArrayList<>();
			update(true);
			return true;
		} else if (id == R.id.화폐단위설정) {
			AlertDialog.Builder 창 = new AlertDialog.Builder(MainActivity.this);
			창.setTitle("화폐 단위 설정");
			
			final EditText 에딧텍스트 = new EditText(MainActivity.this);
			에딧텍스트.setText(unit);
			
			창.setView(에딧텍스트);
			
			창.setPositiveButton("확인", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					unit = 에딧텍스트.getText().toString();
					update(true);
				}
			});
			
			창.show();
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && event.isTracking()) {
			finish();
			overridePendingTransition(R.anim.activity_end_show, R.anim.activity_end_close);
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}
}