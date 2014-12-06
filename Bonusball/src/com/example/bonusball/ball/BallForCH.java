package com.example.bonusball.ball;

public class BallForCH extends Ball {

	private boolean readyToForm = false;
	
	public BallForCH(int rgb, float r, float pX, float pY, float vX, float vY) {
		super(rgb, r, pX, pY, vX, vY);
	}

	
	public boolean isReadyToForm() {
		return readyToForm;
	}

	public void setReadyToForm(boolean readyToForm) {
		this.readyToForm = readyToForm;
	}
	
	

}
