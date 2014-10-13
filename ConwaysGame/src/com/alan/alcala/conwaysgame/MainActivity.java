package com.alan.alcala.conwaysgame;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import android.view.View.OnClickListener;

public class MainActivity extends Activity implements OnClickListener, Runnable
{
	private DrawWorld objWorld; 
	
	private boolean working; // variable por la gestion del hilo
    private Thread hilo;
    private int time; // tiempo para crear siguiente generacion
    private Button simulate, next, stop, clear; // controles
   
    
    @Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		objWorld = (DrawWorld)findViewById(R.id.DrawClass);
		//hilo = new Thread(this);
		
        time = 200;
        working = false;
		
        simulate = (Button)findViewById(R.id.ButtonSimulate);
		simulate.setOnClickListener(this);
		next = (Button)findViewById(R.id.ButtonNext);
		next.setOnClickListener(this);
		stop = (Button)findViewById(R.id.ButtonStop);
		stop.setOnClickListener(this);
		clear = (Button)findViewById(R.id.ButtonClear);
		clear.setOnClickListener(this); 
		
	}
   
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public void run() 
	{
		// TODO Auto-generated method stub
		while (!working) 
  	  	{ 
			// mientras existe el hilo
			objWorld.next(); // calcular siguiente generacion
			objWorld.postInvalidate(); // dibujar generacion
            try 
            {
            	hilo.sleep(time); // descanzar hilo (tiempo en ms)
            } 
            catch (InterruptedException e) {};
  	  	}
	}

	@Override
	public void onClick(View v) 
	{
		if(v == findViewById(R.id.ButtonSimulate))
		{
			hilo = new Thread(this);
			working = false;
			hilo.start(); // llama a funcion run()
	    }
		else if(v == findViewById(R.id.ButtonNext))
		{
			objWorld.next();
        	objWorld.invalidate();
		}
		else if(v == findViewById(R.id.ButtonStop))
		{
			hilo = null;
        	working = true;
		}
		else if(v == findViewById(R.id.ButtonClear))
		{
			objWorld.clear();
			objWorld.invalidate();
		}
	}
}
