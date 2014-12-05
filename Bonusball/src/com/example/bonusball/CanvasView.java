package com.example.bonusball;  
  
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

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
	/**
	 * 判断 小球停止是的偏移量
	 */
	private final static int OFFSET=5;
	
	/**剩下这么多个球是 判断字已经画完*/
	private final static int NAUGHTY_BALL_NUMBER=10;
	
	/**摩擦力*/
	private final static float FRICTION=0.96f;		// 
	
	private ArrayList<ScreenPoint> screenlist=null;
	public int number=0;
	public int totalNum=0;
	//外部监听 绘制完成的接口
	private OnCompleteListener myCompleteListener;
	
    private SurfaceHolder myHolder;  
    private Paint ballPaint; // Paint used to draw the cannonball  
    private int screenWidth; // width of the screen  
    private int screenHeight; // height of the screen  
    private int maxBallRadius=10;  
    private int maxBallSpeed=30;
    private CanvasThread myThread;  
    private List<Ball> ballList;  //所有小球的集合
    private Paint backgroundPaint;  
    private Random mRandom;  
    //控制循环  
    boolean isLoop;  
    
    int		mouseX,		mouseY;			// 当前鼠标坐标
    int		mouseVX,	mouseVY;		// 鼠标速度
    int		prevMouseX,	prevMouseY;		// 上次鼠标坐标
    boolean	isMouseDown=false;				// 鼠标左键是否按下
  
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
        ballList=new CopyOnWriteArrayList<Ball>();  
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
        
        ballList.add(new Ball(ranColor,tmpRadius,pX,pY,speedX,speedY));  
    }
    
    public void fireBall(float startX,float startY,float velocityX,float velocityY)  
    {  
    	//随机产生颜色
        int ranColor = 0xff000000 | mRandom.nextInt(0x00ffffff);
        
        //随机产生半径
        float randomRadius=mRandom.nextInt(maxBallRadius);  
        float tmpRadius=maxBallRadius/5.0>randomRadius?maxBallRadius:randomRadius;  
        
        ballList.add(new Ball(ranColor,tmpRadius,startX,startY,velocityX,velocityY));  
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
        for(Ball b:ballList)  
        {  
            ballPaint.setColor(b.getColor());  
            canvas.drawCircle(b.getX(),b.getY(),b.getRadius(),ballPaint);  
        }  
    }  
    
    
    
    /**
     * 改变所有球的位置
     * 根据每个球当前的速度
     * 如果碰到边界 反弹
     * @param elapsedTimeMS
     */
    private void updatePositions(double elapsedTimeMS) {  
        // TODO Auto-generated method stub  
//        float interval = (float) (elapsedTimeMS / 1000.0);   
        float interval = (float) 0.1;  
        //如果字写完了停止线程
        //允许有两个球。。。迟迟跑不到位
        if(totalNum!=0&&number>totalNum-NAUGHTY_BALL_NUMBER)//停止线程
        {
//        	myThread.stop();
//        	isLoop=false;
        	myCompleteListener.onComplete();
        	number=0;
//        	screenlist.clear();
//        	screenlist=null;
        	random_update_ball_speed();//随机打乱
        	
        }
        
        

		mouseVX    = mouseX - prevMouseX;
		mouseVY    = mouseY - prevMouseY;
		prevMouseX = mouseX;
		prevMouseY = mouseY;
		
		float toDist   = screenWidth * 0.86f;
		float stirDist = screenWidth * 0.125f;
		float blowDist = screenWidth * 0.5f;
        
        
        for(Ball b:ballList)  
        {  
        	

			float x  = b.getX();
			float y  = b.getY();
			float vX = b.getVX();
			float vY = b.getVY();
			
			float dX = x - mouseX;
			float dY = y - mouseY; 
			float d  = (float)Math.sqrt(dX * dX + dY * dY);
			dX = d>0 ? dX / d : 0;
			dY = d>0 ? dY / d : 0;
			
        	/*
        	 * 遍历screenlist
        	 * 如果经过这些点。。停下来
        	 */
        	if(screenlist!=null)
        	{
        		for(ScreenPoint point:screenlist)
            	{
        			//这个点有值该球在动
        			if(!b.isStop()&&point.isFalg())
        			{//&&	b.getY()==point.getY()
        				
        				
        				if(Math.sqrt((b.getX()-point.getX()))<OFFSET&&
        						Math.sqrt((b.getY()-point.getY()))<OFFSET)
	            		{
        					point.setFalg(new Random().nextBoolean());//设置这个点已经有了。。。
	            			b.setStop(true);
	            			number++;
	            			break;
//	            			System.out.println(number+"  "+totalNum);
	            		}
        				
        				float d2=(float) Math.sqrt(vX * vX + vY * vY);
        				
        				if(d2>2*OFFSET)
        				{
        					//移动之后跳过了目标点的判断
//        					Log.i("CanvasView", "会跳过");
        					if(Math.sqrt((x+vX/2-point.getX()))<OFFSET&&
            						Math.sqrt((y+vY/2-point.getY()))<OFFSET)
    	            		{
            					point.setFalg(new Random().nextBoolean());//设置这个点已经有了。。。
    	            			b.setStop(true);
    	            			number++;
    	            			break;
//    	            			System.out.println(number+"  "+totalNum);
    	            		}
        				}
        					
        					
        			}
            		
            	}
        	}
        	
        	
        
        	if(!b.isStop())//当前球处于停止状态
        	{
//        		b.setPosX(b.getX()+b.getVX()*interval);  
//                b.setPosY(b.getY()+b.getVY()*interval);  
//                
////                Log.i("Ball", b.getX()+","+b.getY());
//                
//                if (b.getX() + b.getRadius()> screenWidth )  
//                {  
//                    b.setVX(-1*b.getVX());  
//                    //边界修复  
//                    b.setPosX(screenWidth-b.getRadius());  
//                }  
//                if(b.getX() - b.getRadius() < 0)  
//                {  
//                    b.setVX(-1*b.getVX());  
//                    b.setPosX(b.getRadius());  
//                }  
//                if (b.getY() + b.getRadius()> screenHeight)  
//                {  
//                    b.setVY(-1*b.getVY());  
//                    b.setPosY(screenHeight-b.getRadius());  
//                }  
//                if(b.getY() - b.getRadius() < 0)  
//                {  
//                    b.setVY(-1*b.getVY());  
//                    b.setPosY(b.getRadius());  
//                }
        		
//        		darken();	// 全屏变暗

        		
        			
//        			//鼠标按下监听 点击屏幕。。。
        			if (isMouseDown && d < blowDist)
        			{
        				float blowAcc = (1 - (d / blowDist)) * 14;
        				vX += dX * blowAcc + 0.5f - mRandom.nextFloat()/Integer.MAX_VALUE;
        				vY += dY * blowAcc + 0.5f - mRandom.nextFloat()/Integer.MAX_VALUE;
        			}
        			
        			if (d < toDist)
        			{
        				float toAcc = (1 - (d / toDist)) * screenWidth * 0.0014f;
        				vX -= dX * toAcc;
        				vY -= dY * toAcc;			
        			}
        			
        			if (d < stirDist)
        			{
        				float mAcc = (1 - (d / stirDist)) * screenWidth * 0.00026f;
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
        			
        			// 画小球
//        			setcolor(b.getColor());
//        			setfillstyle(b.getColor());
//        			fillcircle(int(nextX + 0.5), int(nextY + 0.5), int(sc + 0.5));
        		
        	}
        	
              
        }  
  
    }  
    
    public void formChinese(ArrayList<ScreenPoint> list)
    {
    	screenlist=new ArrayList<ScreenPoint>();
    	screenlist=list;
    	number=0;//记录有多少个球停下来了
    	
    	
    	
    	totalNum=ballList.size();//球的总数
    }
    
    /**
     * 改变小球当前的速度方向
     * 根据目标地点
     * 计算出当前的速度方向
     * @param x
     * @param y
     */
    public void update_ball_speed(float x2,float y2)
    {

        for(Ball b:ballList)  
        {  
    		float x1=b.getX();
        	float y1=b.getY();

    		b.setVX((x2-x1)/2);
    		b.setVY((y2-y1)/2);
        }
    }
    
    /**
     * 随机改变
     * @param x
     * @param y
     */
    public void random_update_ball_speed()
    {
    	screenlist=null;//清空字
    	
        for(Ball b:ballList)  
        {  
        	
        	float speedX=mRandom.nextFloat()>0.5? maxBallSpeed:-maxBallSpeed;
            float speedY=mRandom.nextFloat()>0.5? maxBallSpeed:-maxBallSpeed;
            
            b.setStop(false);//停止的球也开始移动
        	
        	b.setVX(speedX);
        	b.setVY(speedY);
//        	
    
        }
    }
    
    /**
     * 清空内存
     */
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
            long previousFrameTime = System.currentTimeMillis();   
            while(isLoop)  
            { 
                  
                try{  
                    canvas = myHolder.lockCanvas(null);//获取画布  
                    synchronized( myHolder )  
                    {  
//                        canvas.drawColor(Color.BLACK);  
//                        long currentTime = System.currentTimeMillis();  
//                        double elapsedTimeMS = currentTime - previousFrameTime;  
                        updatePositions(0); // update game state  
                        if(canvas!=null)
                        	drawGameElements(canvas);  
//                        previousFrameTime = currentTime; // update previous time  
                        //System.out.println("run");  
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
//                
                
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