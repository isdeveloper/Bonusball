package com.example.bonusball;  

import android.util.Log;
  
public class Ball {  
  
    private float posX;  
    private float posY;  
    private float targetX=0;  
    private float targetY=0;  
    private float velocityX;  
    private float velocityY;  
    private float radius;  //°ë¾¶
    private int color;  
    private boolean isStop=false;
    
    private float tempV = (float) 0.5;
    
    public Ball(int rgb,float r,float pX,float pY,float vX,float vY)  
    {  
        this.color=rgb;  
        this.radius=r;  
        this.posX=pX;  
        this.posY=pY;  
        this.velocityX=vX;  
        this.velocityY=vY;  
    }  
    
    public float getRadius()  
    {  
        return radius;  
    }  
    public int getColor()  
    {  
        return color;  
    }  
    public float getX()  
    {  
        return posX;  
    }  
    public float getY()  
    {  
        return posY;  
    }  
    public float getVX()  
    {  
        return velocityX;  
    }  
    public float getVY()  
    {  
        return velocityY;  
    }  
      
    public void setColor(int color)  
    {  
        this.color=color;  
    } 
    
    public void setPosX(float newX)  
    {  
        this.posX=newX;  
    }  
    public void setPosY(float newY)  
    {  
        this.posY=newY;  
    }  
    
    public void setVX(float newVX)  
    {  
        this.velocityX=newVX;  
    }  
    public void setVY(float newVY)  
    {  
        this.velocityY=newVY;  
    }

	public boolean isStop() {
		return isStop;
	}

	public void setStop(boolean isStop) {
		this.isStop = isStop;
	}

	public float getTargetX() {
		return targetX;
	}

	public void setTargetX(float targetX) {
		this.targetX = targetX;
	}

	public float getTargetY() {
		return targetY;
	}

	public void setTargetY(float targetY) {
		this.targetY = targetY;
	}
	
	
    
}  