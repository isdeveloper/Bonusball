package com.example.bonusball.hzk;

/**
 * 屏幕上的点
 * 左边
 * 当前状态 默认false
 * @author zxy
 *
 */
public class ScreenPoint {
	
	
	private int x=0;
	private int y=0;
	private boolean falg=false;
	
	public ScreenPoint(int x,int y,boolean flag)
	{
		this.x=x;
		this.y=y;
		this.falg=flag;
	}
	
	public boolean isFalg() {
		return falg;
	}
	public void setFalg(boolean falg) {
		this.falg = falg;
	}
	
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	
}
