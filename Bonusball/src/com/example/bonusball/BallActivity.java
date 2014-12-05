package com.example.bonusball;  
  
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bonusball.CanvasView.OnCompleteListener;
import com.example.bonusball.hzk.HZKUtils;
import com.example.bonusball.hzk.ScreenCal;
import com.example.bonusball.hzk.ScreenPoint;
public class BallActivity extends Activity {  
  
	private GestureDetector gd;  //手势监听
	
    private CanvasView myCanvas;  
    //在屏幕上画一个16*16的点阵 记录所有的坐标
    private ArrayList<ScreenPoint> screenlist;
  
    EditText inputEt=null;
    
    private String str="郑翔";//显示的字
    private int ballNum=100;//球的数量
    private int index=0;
    
    private Handler handler;
    
    @Override  
    public void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);    
        
        // 获取屏幕宽高
     	Display display = getWindowManager().getDefaultDisplay();
     	
     	screenlist=ScreenCal.screenCal(display.getWidth(), display.getHeight());
     	
        myCanvas=new CanvasView(this,display.getWidth(),display.getHeight());
        
        setContentView(myCanvas); 
        
        
        handler=new Handler(){

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
//				if(msg.what==1)
//				{
//					myCanvas.random_update_ball_speed();
					

				/**
				 * 让球随机移动2秒之后再写下一个字
				 */
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				startGame(str, msg.what);
//				}
			}
        	
        };
        
        
        //初始化输入框
        inputEt=new EditText(this);
        inputEt.setHint("请输入");
        
        gd=new GestureDetector(this,new OnDoubleClick());  
        
        /*
         * 初始化个球
         */
        for(int i=0;i<ballNum;i++)
        	myCanvas.fireBall(); 
        
        
        //字在绘制完时会调用
        myCanvas.setOnCompleteListener(new OnCompleteListener() {
			
			@Override
			public void onComplete() {
				// TODO Auto-generated method stub
				/**
				 * 画完了字
				 * 开始画下一个
				 * index画了第几个
				 */
				if(str.length()>1&&index!=(str.length()-1))//不止一个字
				{
					index++;
					
					
					handler.sendEmptyMessage(index);
				}
				else
				{
					index=0;//记录g归0
				}
//				System.out.println("画完了 第"+index+"个 总共 "+str.length());
				
				
			}
		});
    }  
    
    

    /**
     * 开始显示字
     * @param string 需要显示的字
     */
	protected void startGame(String str,int position) {
		// TODO Auto-generated method stub
//		System.out.println("正在画第"+index+"个 总共 "+str.length());
		//根据汉字得到对应的点阵
    	int[][] data=HZKUtils.readChinese(this,str.charAt(position));
    	
    	//结合data修改 screenlist中的点的flag
    	for(int i=0,length=data.length;i<length;i++)
        {
        	for (int j = 0; j < length; j++) {
        		//设置默认值
        		screenlist.get(i*length+j).setFalg(false);
        		//该点为1 点的状态设为true
        		//表示需要将点移动到对应的地方
        		if(data[i][j]==1)
        		{
        			screenlist.get(i*length+j).setFalg(true);
//        			System.out.print("■");
        		}
        		else
    			{
        			screenlist.get(i*length+j).setFalg(false);
//        			System.out.print(" ");
    			}
			}
//        	System.out.print("\n");
        }
    	myCanvas.formChinese(screenlist);
	    	
	    
		
		
	}
    
    @Override  
    public boolean onTouchEvent(MotionEvent event)  
    {  
    	
    	switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			myCanvas.isMouseDown=true;
			Log.i("BallActivity", "MotionEvent.ACTION_DOWN");
			break;
		case MotionEvent.ACTION_MOVE:
			
			myCanvas.isMouseDown=false;
			
			float x=event.getX();
	    	float y=event.getY();
	    	
			myCanvas.mouseX=(int)x;
	    	myCanvas.mouseY=(int)y;
	    	break;
		case MotionEvent.ACTION_UP:
			myCanvas.isMouseDown=false;
			Log.i("BallActivity", "MotionEvent.ACTION_UP");
			break;
		default:
			break;
		}
    	
        return gd.onTouchEvent(event);   //处理双击 长安
    }  
    
  
  
    class OnDoubleClick extends GestureDetector.SimpleOnGestureListener{  
    	
    
		@Override
		public boolean onSingleTapUp(MotionEvent event) {
			// TODO Auto-generated method stub
			
	    	
			//myCanvas.random_update_ball_speed();
//			myCanvas.isMouseDown=true;
//			Log.i("BallActivity", "onSingleTapUp");
			return false;
		}

		@Override  
        public boolean onDoubleTap(MotionEvent e) {  //双击
            //TODO  
			myCanvas.random_update_ball_speed();
			Log.i("BallActivity", "onDoubleTap");
            return false;  
        }

		@Override
		public void onLongPress(MotionEvent event) {//长安
			// TODO Auto-generated method stub
			super.onLongPress(event);
//			startGame(str);
			
	    	if(str.length()>0)
			{
				startGame(str,0);//开始写字
				index=0;
//				handler.sendEmptyMessage(0);
			}
//			Toast.makeText(BallActivity.this, "长安", Toast.LENGTH_SHORT).show();
		}  
		
		
//        @Override  
//        public boolean onDoubleTapEvent(MotionEvent e) {  
//            return super.onDoubleTapEvent(e);  
//        }  
    }  
    



	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		myCanvas.clear();
		Log.i("BallActivity", "onDestroy");
	}  
    
    
    
    @Override  
    public boolean onCreateOptionsMenu(Menu menu) {  
        getMenuInflater().inflate(R.menu.main, menu);  
        
        return true;  
    }
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		showInputDialog();//弹出输入提示
		return super.onOptionsItemSelected(item);
	}  
    
	public void showInputDialog() {
		new AlertDialog.Builder(this)
				.setTitle("提示")
				.setMessage("请输入想要显示的字，，，不要太多")
				.setView(inputEt)
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface di, int which) {
						String tem=inputEt.getText().toString();
						
						//没有输入汉字时  提示一下
						if(tem.length()<1)
						{
							Toast.makeText(BallActivity.this, "没有输入任何汉字！", Toast.LENGTH_SHORT).show();
						}
						else
						{
							str=tem;//修改本地存储的值
							startGame(str,0);
							index=0;
//							handler.sendEmptyMessage(0);
						}
						
					}

				}).show();
	}
	
}  