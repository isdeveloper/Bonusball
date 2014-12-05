package com.example.bonusball.hzk;
import java.util.ArrayList;


public class ScreenCal {
	
	
	
	/**
	 * 根据屏幕大小
	 * 在屏幕中间放一个16*16的点阵
	 * 即返回这64个点的左边
	 * @param width
	 * @param height
	 * @return 返回一个列表
	 */
	public static ArrayList<ScreenPoint> screenCal(int width,int height)
	{
		ArrayList<ScreenPoint> list=new ArrayList<ScreenPoint>();
		
		int l=width/17;//向下取整 每个点之间的宽度
		
		int temp_x=(width-15*l)/2;//第一个点离左边的距离
		
		int temp_y=(height-15*l)/2;//第一个点离顶部的距离
		
		//将这一群点按顺序放到list中
		for(int i=0;i<256;i++)
		{
			int x=temp_x+l*(i%16);
			int y=temp_y+l*(i/16);
					
			ScreenPoint point=new ScreenPoint(x,y,false);
			list.add(point);
		}
		
		return list;
	}
	
	
}
