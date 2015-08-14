package com.it.mobilesafe.activity;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.ArcProgress;
import com.it.mobilesafe.R;
import com.it.mobilesafe.bean.AntiVirusInfo;
import com.it.mobilesafe.db.AntiVirusDao;
import com.it.mobilesafe.utils.MD5Utils;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

public class AntiVirusActivity extends Activity implements OnClickListener {
	// private static final String TAG = "AntiVirusActivity";
	private PackageManager mPm;

	private List<AntiVirusInfo> mDatas;

	private TextView mtvPackageName;
	// private TextView mtvProgress;
	private ArcProgress mArcProgress;

	private ListView mListView;
	private VirusAdapter mAdapter;

	private LinearLayout mLlResultContainer;
	private RelativeLayout mRlProgressContainer;
	private LinearLayout mLlAnimatorContainer;
	private TextView mResult;
	private Button mBtnScan;

	private ImageView mIvLeft;
	private ImageView mIvRight;

	private ScanTask mTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_antivirus);

		mPm = getPackageManager();

		mtvPackageName = (TextView) findViewById(R.id.aa_tv_packageName);
		// mtvProgress = (TextView) findViewById(R.id.aa_tv_progress);
		mArcProgress = (ArcProgress) findViewById(R.id.arc_progress);
		mLlResultContainer = (LinearLayout) findViewById(R.id.aa_ll_result_container);
		mRlProgressContainer = (RelativeLayout) findViewById(R.id.aa_rl_progress_container);

		mResult = (TextView) findViewById(R.id.aa_tv_result);
		mBtnScan = (Button) findViewById(R.id.aa_btn_scan);
		mBtnScan.setOnClickListener(this);

		mListView = (ListView) findViewById(R.id.aa_listview);
		mIvLeft = (ImageView) findViewById(R.id.aa_iv_left);
		mIvRight = (ImageView) findViewById(R.id.aa_iv_right);

		mLlAnimatorContainer = (LinearLayout) findViewById(R.id.aa_animator_container);
		
		mAdapter = new VirusAdapter();
		mListView.setAdapter(mAdapter);

		startScan();

	}

	@Override
	protected void onPause() {
		super.onPause();

		if (mTask != null) {
			mTask.stop();
			mTask = null;
		}
	}

/*	@Override
	protected void onStop() {
		super.onStop();
		if (mTask != null) {
			mTask.stop();
			mTask = null;
		}
	}*/

	private void startScan() {

		if (mTask != null) {
			mTask.stop();
			mTask = null;
		}

		mTask = new ScanTask();
		mTask.execute();
	}


	private final class ScanTask extends AsyncTask<Void, AntiVirusInfo, Void> {
		public boolean isFinish;

		private int progress;
		private int max;
		private int mVirus = 0;//记录总共几个病毒

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			mLlResultContainer.setVisibility(View.GONE);
			mRlProgressContainer.setVisibility(View.VISIBLE);
			mLlAnimatorContainer.setVisibility(View.GONE);
			mBtnScan.setEnabled(false);
		}

		public void stop() {
			isFinish = true;
		}

		@Override
		protected Void doInBackground(Void... params) {

			List<PackageInfo> list = mPm
					.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
			max = list.size();
			mDatas = new ArrayList<AntiVirusInfo>();
			progress =0;
			for (PackageInfo pack : list) {
				progress++;
//				System.out.println("progress++ :"+progress);

				// data/app/xxx.apk
				// system/app/xxx.apk
				String sourceDir = pack.applicationInfo.sourceDir;

				// Log.d(TAG, "sourceDir :"+sourceDir);
				
				FileInputStream fis;
				try {

					fis = new FileInputStream(sourceDir);
					// 特征值
					String md5 = MD5Utils.encode(fis);
					String name = pack.applicationInfo.loadLabel(mPm)
							.toString();
					boolean isVirus = AntiVirusDao.isVirus(
							AntiVirusActivity.this, md5);
					Drawable icon = pack.applicationInfo.loadIcon(mPm);

					String packageName = pack.packageName;

					AntiVirusInfo info = new AntiVirusInfo();
					info.packageName = packageName;
					info.icon = icon;
					info.name = name;
					info.isVirus = isVirus;

					if (info.isVirus) {
						mDatas.add(0, info);

						mVirus++;
					} else {
						mDatas.add(info);
					}
					
					publishProgress(info);

					if (isFinish) {
						break;
					}

					SystemClock.sleep(100);

					/*
					 * Log.d(TAG, "md5 :"+md5); Log.d(TAG, "name :"+name);
					 * Log.d(TAG, "-----------");
					 */

				} catch (Exception e) {
					e.printStackTrace();
				}

			}

			return null;
		}

		@Override
		protected void onProgressUpdate(AntiVirusInfo... values) {

			if (isFinish) {
				return;
			}

			AntiVirusInfo info = values[0];

			mtvPackageName.setText(info.packageName);

			int currentProgress = (int) (progress * 100f / max + 0.5f);

			// mtvProgress.setText("进度"+currentProgress);
			mArcProgress.setProgress(currentProgress);

			// 第一次就设置，而后就开始 ,什么时候使用adapter，那就什么时候使用
			if (progress == 1) {
				
				System.out.println("progress1 :"+progress);
				
				mAdapter = new VirusAdapter();
				mListView.setAdapter(mAdapter);
			} else {
				 if(mAdapter!=null) {
					 
					 System.out.println("progress :"+progress);
					 mAdapter.notifyDataSetChanged();
				 }
			}
//			mAdapter.notifyDataSetChanged();
			mListView.smoothScrollToPosition(progress);

		}

		@Override
		protected void onPostExecute(Void result) {
			if (isFinish) {
				return;
			}
			// 滚动到顶部
			mListView.smoothScrollToPosition(0);

			if (mVirus > 0) {
				mResult.setText("手机不安全");
				mResult.setTextColor(Color.RED);
			} else {
				mResult.setText("手机很安全");
			}

			// 结束时,做打开的动画
			mRlProgressContainer.setDrawingCacheEnabled(true);
			mRlProgressContainer
					.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
			Bitmap bitmap = mRlProgressContainer.getDrawingCache();

			mIvLeft.setImageBitmap(getLeft(bitmap));
			mIvRight.setImageBitmap(getRight(bitmap));

			mRlProgressContainer.setVisibility(View.GONE);
			mLlResultContainer.setVisibility(View.VISIBLE);
			mLlAnimatorContainer.setVisibility(View.VISIBLE);
			mLlAnimatorContainer.bringToFront();
			// 显示动画
			showOpenAnimor();
		}
	}

	private void showOpenAnimor() {
		AnimatorSet set = new AnimatorSet();

		mLlAnimatorContainer.measure(0, 0);

		set.playTogether(
				ObjectAnimator.ofFloat(mIvLeft, "translationX", 0,
						-mIvLeft.getMeasuredWidth()),
				ObjectAnimator.ofFloat(mIvRight, "translationX", 0,
						mIvRight.getMeasuredWidth()),
				ObjectAnimator.ofFloat(mIvLeft, "alpha", 1, 0),
				ObjectAnimator.ofFloat(mIvRight, "alpha", 1, 0),
				ObjectAnimator.ofFloat(mLlResultContainer, "alpha", 0, 1));

		set.setDuration(3000);

		set.addListener(new AnimatorListener() {

			@Override
			public void onAnimationStart(Animator arg0) {

			}

			@Override
			public void onAnimationRepeat(Animator arg0) {

			}

			@Override
			public void onAnimationEnd(Animator arg0) {
				mBtnScan.setEnabled(true);
			}

			@Override
			public void onAnimationCancel(Animator arg0) {

			}
		});

		set.start();

	}

	private Bitmap getRight(Bitmap srcBitmap) {

		// 准备画纸
		int width = (int) (srcBitmap.getWidth() / 2f + 0.5f);
		int height = srcBitmap.getHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height,
				srcBitmap.getConfig());

		// 准备画布
		Canvas canvas = new Canvas(bitmap);
		// 准备画笔
		Paint paint = new Paint();

		// 准备规则
		Matrix matrix = new Matrix();
		matrix.setTranslate(-width, 0);

		// 绘制 原图,规则(大小),画笔
		canvas.drawBitmap(srcBitmap, matrix, paint);

		return bitmap;
	}

	private Bitmap getLeft(Bitmap srcBitmap) {

		// 准备画纸
		int width = (int) (srcBitmap.getWidth() / 2f + 0.5f);
		int height = srcBitmap.getHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height,
				srcBitmap.getConfig());

		// 准备画布
		Canvas canvas = new Canvas(bitmap);
		// 准备画笔
		Paint paint = new Paint();

		// 准备规则
		Matrix matrix = new Matrix();

		// 绘制 原图,规则(大小),画笔
		canvas.drawBitmap(srcBitmap, matrix, paint);

		return bitmap;
	}

	private class VirusAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			if (mDatas != null) {
				return mDatas.size();
			}
			return 0;
		}

		@Override
		public Object getItem(int position) {
			if (mDatas != null) {
				return mDatas.get(position);
			}
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = View.inflate(getApplicationContext(),
						R.layout.item_virus_info, null);

				holder = new ViewHolder();
				convertView.setTag(holder);

				holder.ivIcon = (ImageView) convertView
						.findViewById(R.id.item_virus_iv_icon);
				holder.tvName = (TextView) convertView
						.findViewById(R.id.item_virus_tv_name);
				holder.isVirus = (TextView) convertView
						.findViewById(R.id.item_virus_tv_isVirus);
				holder.ivClean = (ImageView) convertView
						.findViewById(R.id.item_virus_iv_clear);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			AntiVirusInfo info = mDatas.get(position);
			holder.ivIcon.setImageDrawable(info.icon);
			holder.tvName.setText(info.name);
			holder.isVirus.setText(info.isVirus ? "病毒" : "安全");
			if (info.isVirus) {
				holder.isVirus.setTextColor(Color.RED);
			}
			holder.ivClean.setVisibility(info.isVirus ? View.VISIBLE
					: View.GONE);

			return convertView;
		}
	}
	private class ViewHolder {
		ImageView ivIcon;
		ImageView ivClean;
		TextView tvName;
		TextView isVirus;
	}

	@Override
	public void onClick(View v) {

		if (v == mBtnScan) {
			// startScan();
			mBtnScan.setEnabled(false);
			AnimatorSet set = new AnimatorSet();
			set.playTogether(
					ObjectAnimator.ofFloat(mIvLeft, "translationX",
							-mIvLeft.getWidth(), 0),
					ObjectAnimator.ofFloat(mIvRight, "translationX",
							mIvRight.getWidth(), 0),
					ObjectAnimator.ofFloat(mIvLeft, "alpha", 0, 1),
					ObjectAnimator.ofFloat(mIvRight, "alpha", 0, 1),
					ObjectAnimator.ofFloat(mLlResultContainer, "alpha", 1, 0));

			set.setDuration(3000);
			set.addListener(new AnimatorListener() {

				@Override
				public void onAnimationStart(Animator arg0) {

				}

				@Override
				public void onAnimationRepeat(Animator arg0) {
				}

				@Override
				public void onAnimationEnd(Animator arg0) {
					startScan();
				}

				@Override
				public void onAnimationCancel(Animator arg0) {

				}
			});
			set.start();

		}

	}

}
