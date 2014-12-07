package com.example.bonusball;  
  
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

import com.example.bonusball.ball.BaseBall;
import com.example.bonusball.ball.BallForCH;
import com.example.bonusball.hzk.ScreenPoint;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
  
public class CanvasView extends SurfaceView implements SurfaceHolder.Callback  
{  

	/**摩擦力*/
	private final static float FRICTION=0.96f;		// 
	
	private final static String TAG = "CanvasView";
	
	private ArrayList<ScreenPoint> screenlist=null;
	//外部监听 绘制完成的接口
	private OnCompleteListener myCompleteListener;
	
    private SurfaceHolder myHolder;  
    private Paint ballPaint; // Paint used to draw the cannonball  
    private int screenWidth; // width of the screen  
    private int screenHeight; // height of the screen  
    private int maxBallRadius=10;  
    private CanvasThread myThread;  
    private List<BallForCH> ballList;//所有小球的集合
    private Paint backgroundPaint;  
    private Random mRandom;  
    
    //控制循环  
    boolean isLoop;  
    
    int	mouseX, mouseY;// 当前鼠标坐标
    int	mouseVX, mouseVY;// 鼠标速度
    int	prevMouseX,	prevMouseY;// 上次鼠标坐标
    boolean	isMouseDown=false;// 鼠标左键是否按下
  
    public CanvasView(Context context,int width,int height) {  
        super(context);   
        // TODO Auto-generated constructor stub  
        this.screenWidth=width;
        this.screenHeight=height;
        
        myHolder=this.getHolder();  
        myHolder.addCallback(this);  
        ballPaint=new Paint();  
  
        backgroundPaint = new Paint();  
        backgroundPaint.setColor(Color.BLACK);  
        isLoop = true;  
        ballList=new CopyOnWriteArrayList<BallForCH>();  
        mRandom=new Random();  
        
    	// 初始化鼠标变量
    	mouseX = prevMouseX = width / 2;
    	mouseY = prevMouseY = height / 2;
        
       
    }  
  
    public void fireBall()
    {
    	//随机产生颜色
        int ranColor = 0xff000000 | mRandom.nextInt(0x00ffffff);
        
        //随机产生半径
        float randomRadius=mRandom.nextInt(maxBallRadius);  
        float tmpRadius=maxBallRadius/5.0>randomRadius?maxBallRadius:randomRadius;  
       
//        tmpRadius=maxBallRadius;
        
        float pX=screenWidth * 0.5f; 
        float pY=screenHeight * 0.5f;
        int i=ballList.size();
        float speedX=(float) Math.cos(i)*mRandom.nextInt(34);
        float speedY=(float) Math.sin(i)*mRandom.nextInt(34);
        
        ballList.add(new BallForCH(ranColor,tmpRadius,pX,pY,speedX,speedY));  
    }
    
    public void fireBall(float startX,float startY,float velocityX,float velocityY)  
    {  
    	//随机产生颜色
        int ranColor = 0xff000000 | mRandom.nextInt(0x00ffffff);
        
        //随机产生半径
        float randomRadius=mRandom.nextInt(maxBallRadius);  
        float tmpRadius=maxBallRadius/5.0>randomRadius?maxBallRadius:randomRadius;  
        
        ballList.add(new BallForCH(ranColor,tmpRadius,startX,startY,velocityX,velocityY));  
//        System.out.println("Fire");  
    }  
  
    @Override  
    public void surfaceChanged(SurfaceHolder holder, int format, int width,  
            int height) {  
        // TODO Auto-generated method stub  
  
  
    }  
    @Override  
    protected void onSizeChanged(int w, int h, int oldw, int oldh)  
    {  
        super.onSizeChanged(w, h, oldw, oldh);  
        screenWidth = w; // store the width  
        screenHeight = h; // store the height  
        maxBallRadius=w/10;  
    }  
  
    @Override  
    public void surfaceCreated(SurfaceHolder holder) {  
        // TODO Auto-generated method stub  
        myThread = new CanvasThread();  
        System.out.println("SurfaceCreated!");  
        myThread.start();   
  
    }  
  
    @Override  
    public void surfaceDestroyed(SurfaceHolder holder) {  
        // TODO Auto-generated method stub  
        // 停止循环  
        isLoop = false;  
    }  
    
    /**
     * 在画板上
     * 改变所有小球的状态
     * @param canvas
     */
    public void drawGameElements(Canvas canvas)  
    {  
  
        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), backgroundPaint);  
        for(BaseBall b:ballList)  
        {  
            ballPaint.setColor(b.getColor());  
            canvas.drawCircle(b.getX(),b.getY(),b.getRadius(),ballPaint);  
        }  
    }  
    
    
    
    /**
     * 改变所有球的位置
     * 根据每个球当前的速度
     * 如果碰到边界 反弹
     */
    private void updatePositions() {  
        // TODO Auto-generated method stub  
     
        //如果字写完了停止线程
        //允许有两个球。。。迟迟跑不到位        

		mouseVX    = mouseX - prevMouseX;
		mouseVY    = mouseY - prevMouseY;
		prevMouseX = mouseX;
		prevMouseY = mouseY;
		
		float toDist   = screenHeight * 0.86f;
		float stirDist = screenHeight * 0.125f;
		float blowDist = screenHeight * 0.5f;
        
        for(BallForCH b:ballList)  
        {  
			float x  = b.getX();
			float y  = b.getY();
			float vX = b.getVX();
			float vY = b.getVY();
			
			float dX = x - mouseX;
			float dY = y - mouseY; 
			if(b.isReadyToForm())//当前球处于构建汉字状态
        	{
				dX = x - b.getTargetX();
				dY = y - b.getTargetY();
				
        	} 
			
			float d  = (float)Math.sqrt(dX * dX + dY * dY);
			dX = d>0 ? dX / d : 0;
			dY = d>0 ? dY / d : 0;
			
        	
   			//鼠标按下监听 点击屏幕。。。
			if (isMouseDown && d < blowDist)
			{
				float blowAcc = (1 - (d / blowDist)) * 14;
				vX += dX * blowAcc + 0.5f - mRandom.nextFloat()/Integer.MAX_VALUE;
				vY += dY * blowAcc + 0.5f - mRandom.nextFloat()/Integer.MAX_VALUE;
			}
			//修改速度
			//离触摸点越远速度越小
			if (d < toDist)
			{
				float toAcc = (1 - (d / toDist)) * screenHeight * 0.0014f;
				vX -= dX * toAcc;
				vY -= dY * toAcc;			
			}
			
			if (d < stirDist)
			{
				float mAcc = (1 - (d / stirDist)) * screenHeight * 0.00026f;
				vX += mouseVX * mAcc;
				vY += mouseVY * mAcc;			
			}
			
			vX *= FRICTION;
			vY *= FRICTION;
			
			float avgVX = (float)Math.abs(vX);
			float avgVY = (float)Math.abs(vY);
			float avgV  = (avgVX + avgVY) * 0.5f;
			
			if (avgVX < 0.1) vX *= mRandom.nextFloat()/Integer.MAX_VALUE * 3;//float(mRandom.nextInt()) / Integer.MAX_VALUE * 3;
			if (avgVY < 0.1) vY *= mRandom.nextFloat()/Integer.MAX_VALUE * 3;
			
			float sc = avgV * 0.45f;
			sc = Math.max(Math.min(sc, 3.5f), 0.4f);
			
			float nextX = x + vX;
			float nextY = y + vY;
			
			if		(nextX > screenWidth)	{ nextX = screenWidth;	vX *= -1; }
			else if (nextX < 0)		{ nextX = 0;		vX *= -1; }
			if		(nextY > screenHeight){ nextY = screenHeight;	vY *= -1; }
			else if (nextY < 0)		{ nextY = 0;		vY *= -1; }
			
			b.setVX(vX);
			b.setVY(vY);
			b.setPosX(nextX);
			b.setPosY(nextY);
        	
        }
    }  

    public void formChinese(ArrayList<ScreenPoint> list)
    {
    	screenlist=new ArrayList<ScreenPoint>();
    	//取出需要填充的坐标点
    	for(ScreenPoint point : list) {
    		if(point.isFalg()) {
    			screenlist.add(point);
    		}
    	}
    	
    	int index = 0;
    	int size = screenlist.size();
    
    	//修改每个小球的target坐标
    	//坐标值取自汉字坐标值列表
		for(BallForCH b : ballList) {
			ScreenPoint point = screenlist.get(index);
    		
    		b.setTargetX(point.getX());
    		b.setTargetY(point.getY());
    		b.setReadyToForm(true);
//    		Log.i(TAG, " ready to stop " + b.getTargetX() + "," + b.getTargetY());
    		
    		index ++;
    		index = index % size;
		}
		
		
		//开启一个定时任务 几秒后 结束画字 
		stopFormChineseTimerTask(5000);
		
    }
    
    private void stopFormChineseTimerTask(long when) {
    	new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				formChineseComplete();
			}
		}, when);
    }
    
    private void formChineseComplete(){
    	if(myCompleteListener != null) {
			myCompleteListener.onComplete();
		}
    	for(BallForCH b:ballList)  
        {  
            b.setReadyToForm(false);//
        }
    }
//    
//    /**
//     * 随机改变 球的坐标
//     * 起到打乱球的作用
//     * 并将球设为free状态
//     */
//    public void random_update_ball_speed()
//    {
//    	screenlist=null;//清空字
//    	
//        for(BallForCH b:ballList)  
//        {  
//        	float speedX=mRandom.nextFloat()>0.5? maxBallSpeed:-maxBallSpeed;
//            float speedY=mRandom.nextFloat()>0.5? maxBallSpeed:-maxBallSpeed;
//            
//            b.setReadyToForm(false);//
//        	
//        	b.setVX(speedX);
//        	b.setVY(speedY);
//        }
//    }
    

    public synchronized void clear()
    {
		isLoop=false;
    	ballList.clear();
    }
  
    private class CanvasThread extends Thread  
    {  
        @Override  
        public void run()  
        {  
            Canvas canvas=null;  
            while(isLoop)  
            { 
                try{  
                    canvas = myHolder.lockCanvas(null);//获取画布  
                    synchronized( myHolder )  
                    {  
                        updatePositions(); 
                        if(canvas!=null)
                        	drawGameElements(canvas);  
                        try {
            				Thread.sleep(5);
            			} catch (Exception e) {
            				// TODO: handle exception
            			}
                    }  
                }  
                finally  
                {  
                    if (canvas != null)   
                        myHolder.unlockCanvasAndPost(canvas);//解锁画布，提交画好的图像  
                } // end finally  
            }  
        }  
    }  
    
    /**
	 * 轨迹球画完成事件
	 */
	public interface OnCompleteListener {
		/**
		 * 画完了
		 */
		public void onComplete();
	}
	
	/**
	 * 外部监听使用
	 * @param myCompleteListener
	 */
	public void setOnCompleteListener(OnCompleteListener myCompleteListener) {
		this.myCompleteListener = myCompleteListener;
	}
    
    
}  