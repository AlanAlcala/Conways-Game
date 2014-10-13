package com.alan.alcala.conwaysgame;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class DrawWorld extends View
{
	public DrawWorld(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public int gen; 
    private int pob; 
    private int sizeCell = 48; 
    private int numCol = 10; 
    private int numLin = 10; 
    private int corX, corY;
    private boolean[][] cells = new boolean[numCol][numLin];
    private int[][] neighbors = new int[numCol][numLin]; 
    
    @Override
	protected void onDraw(Canvas canvas)
    { 
		super.onDraw(canvas);
		Paint p = new Paint( );
    	
		//Dibujar el "mundo" (Un cuadrote gris)
		p.setColor(Color.GRAY);
        canvas.drawRect(0, 0, sizeCell*numCol, sizeCell*numLin,p);
        
        //Cuadricular el cuadrote 
        p.setColor(Color.WHITE);
        for (int x=1; x<numCol; x++) 
        {
        	canvas.drawLine(x*sizeCell-1, 0, x*sizeCell-1, sizeCell*numLin-1,p);
        }
        for (int y=1; y<numLin; y++) 
        {
            canvas.drawLine(0, y*sizeCell-1, sizeCell*numCol-1, y*sizeCell-1,p);
        }
        
        //Pintar de verde las celulas vivas
        p.setColor(Color.GREEN);
        for (int y=0; y<numLin; y++)
        	for (int x=0; x<numCol; x++)
        		if (cells[x][y])
        			canvas.drawRect(x*sizeCell, y*sizeCell, (x+1)*sizeCell-1, (y+1)*sizeCell-1,p);
        
        // Escribir generación y población
        p.setColor(Color.BLACK);
        contPoblacion();
		canvas.drawText("Generacion: "+gen+" Poblacion: "+pob, 1, numLin*sizeCell+15,p); 
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) 
    {
		// TODO Auto-generated method stub
		corX = (int) event.getX();
		corY = (int) event.getY();
		if (event.getAction()==MotionEvent.ACTION_DOWN)
		{
			try 
		    {
				//Invertir el valor de la celula
				cells[corX/sizeCell][corY/sizeCell] = !cells[corX/sizeCell][corY/sizeCell]; 
		    } 
		    catch (Exception e) {}
		    invalidate();
		}
		return true;
	}
	
	// Crear la siguiente generacion
	public void next() 
    { 
    	int x; 
    	int y;
    	gen++; 
    	// Borrar todos los vecinos
    	for (x=0; x<numCol; x++)
    		for (y=0; y<numLin; y++)
    			neighbors[x][y] = 0;
    	// Contar vecinos de las celulas que no estan en las orillas
    	for (x=1; x<numCol-1; x++) 
    	{
    		for (y=1; y<numLin-1; y++) 
    		{
    			if (cells[x][y]) 
    			{
    				neighbors[x-1][y-1]++;
    				neighbors[x][y-1]++;
    				neighbors[x+1][y-1]++;
    				neighbors[x-1][y]++;
    				neighbors[x+1][y]++;
    				neighbors[x-1][y+1]++;
    				neighbors[x][y+1]++;
    				neighbors[x+1][y+1]++;
    			}
            }
        }
    	
    	// Contar vecinos de celulas en las orillas (Aqui esta lo bonito)
    	x=1;
    	y=0; 
    	int dx=1;
    	int dy=0;
    	
    	while (true) 
    	{
    		if (cells[x][y]) 
    		{
    			if (x>0) 
    			{
    				if (y>0)
    					neighbors[x-1][y-1]++;
    				if (y<numLin-1)
    					neighbors[x-1][y+1]++;
    				neighbors[x-1][y]++;
    			}
    			if (x<numCol-1) 
    			{
    				if (y<numLin-1)
    					neighbors[x+1][y+1]++;
    				if (y>0)
    					neighbors[x+1][y-1]++;
    				neighbors[x+1][y]++;
    			}
    			if (y>0)
    				neighbors[x][y-1]++;
    			if (y<numLin-1)
    				neighbors[x][y+1]++;
            }
    		
    		// Girar al llegar a la esquina (Sí, más decisiones)
            if (x==numCol-1 && y==0) 
            {
            	dx=0;
            	dy=1;
            }
            else if (x==numCol-1 && y==numLin-1) 
            {
            	dx=-1;
            	dy=0;
            }
            else if (x==0 && y==numLin-1) 
            {
            	dx=0;
            	dy=-1;
            }
            else if (x==0 && y==0) 
            	break;//Por fin acaba 
            
            x=x+dx;
            y=y+dy;
        }
    	
    	/*Evaluar reglas del juego
    		1.- Cualquier célula viva con menos de dos vecinos vivos MUERE, por baja población. 
			2.- Cualquier célula viva con dos o tres vecinos vivos VIVE a la siguiente generación. 
			3.- Cualquier célula viva con más de tres vecinos vivos MUERE, por sobrepoblación. 
			4.- Cualquier célula muerta con exactamente tres vecinos VIVE, por reproducción.
		*/
    	for (x=0; x<numCol; x++) 
    	{
    		for (y=0; y<numLin; y++) 
    		{
    			switch (neighbors[x][y]) 
    			{
    				case 2:
    					break; 
    				case 3:
    					cells[x][y] = true;
    					break; 
    				default:
    					cells[x][y] = false;
    					break; 
    			}
            }
        }
    	contPoblacion();
    }
	
	//Contar celulas vivas
    public void contPoblacion() 
    { 
    	pob=0;
    	for (int x=0; x<numCol; x++)
    		for (int y=0; y<numLin; y++)
    			if (cells[x][y])
    				pob++;
    }
    
    // Borrar mundo
	public void clear() 
    { 
    	synchronized(this)
    	{
    		gen = 0;
        	pob = 0;
        	for (int x=0; x<numCol; x++)
        		for (int y=0; y<numLin; y++)
        			cells[x][y] = false;
        }
    }
}
