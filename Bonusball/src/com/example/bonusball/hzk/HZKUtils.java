package com.example.bonusball.hzk;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;


public class HZKUtils {
	/**点阵中的数据*/
//	public int[] unit=new int[32];
	
	public final static String YES="■";
	public final String NO=" ";
	
	
	
	/**
	 * 读取一个字符的unit
	 * @param ch
	 * @return
	 */
	private static int[] getUnit(Context context,char ch)
	{
		
		int[] unit=new int[32];
		
		byte[] buf=new byte[32];
		InputStream input=null;
		
		try{
			String string=Character.toString(ch);
		    byte[] bt = string.getBytes("GBK"); //获得国标码
		    int a1=negativeToPlus(bt[0]); //转为无符号整数
		    int a2=negativeToPlus(bt[1]);
		    int qh=a1-0xA0; //得到区位码
		    int wh=a2-0xA0;
		    long offset=(94*(qh-1)+(wh-1))*32;   //获得偏移量
		        
		    AssetManager am = null;  
		    am = context.getAssets();  
//		    InputStream is = am.open("HZK16");  
//		    File file=new File("HZK16");
//		    input=new FileInputStream(file);
		    input=am.open("HZK16");
		    input.skip(offset);
		    input.read(buf,0,32);
		    for(int i=0;i<32;i++)
	          unit[i]=negativeToPlus(buf[i]);
		    input.close();
	    }catch(Exception e){
		        
	        System.out.println("文件异常");
	        e.printStackTrace();
	    }
		  
		return unit;
	}
	
	/**
	 * 将一个中文字符转化成
	 * 16*16的点阵
	 * @param ch 一个中文
	 * @return
	 */
	public static int[][] readChinese(Context context,char ch)
	{
		/**点阵中的数据*/
		int[][] data=new int[16][16];
		
		int[] unit=getUnit(context,ch);
		
		
		for(int j=0;j<16;j++)
		{
			int num=0;
			for(int i=0;i<2;i++)
			{
				for(int k=0;k<8;k++)
				{
					if((unit[j*2+i]&(0x80>>k))>=1) //取bit位值
					{
//						System.out.print("■");
						data[j][num]=1;
						num++;
					}
					else
					{   
//						System.out.print(" ");
						data[j][num]=0;
						num++;
					}
				 }
			}
	                       
//	           System.out.println();
           }
		
		return data;
	}
	
	
	/**
	 * 转为无符号整数
	 * @param b
	 * @return
	 */
	private static int negativeToPlus(byte b){
		  return b&0xFF;
	}
}
