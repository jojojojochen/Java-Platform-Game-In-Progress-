package entity;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import main.GamePanel;
import tilemap.Tile;
import tilemap.TileMap;

public abstract class MapObject {
	//tile stuff
	protected TileMap tileMap;
	protected int tileSize;
	protected double xmap;
	protected double ymap;
	
	//position and vector
	protected double x;
	protected double y;
	protected double dx;
	protected double dy;
	
	//dimensions
	protected int width;
	protected int height;
	
	//collision box
	protected int cwidth;
	protected int cheight;
	
	//collision position
	protected int currRow;
	protected int currCol;
	protected double xdest; //destination of where we are going
	protected double ydest;
	protected double xtemp;
	protected double ytemp;
	protected boolean topLeft;
	protected boolean topRight;
	protected boolean bottomLeft;
	protected boolean bottomRight;
	
	//animation
	protected Animation animation,animation1;
	protected int currentAction;
	protected int previousAction;
	protected boolean facingRight; 
	
	//movement
	protected boolean left;
	protected boolean right;
	protected boolean up;
	protected boolean down;
	protected boolean jumping;
	protected boolean falling;
	
	//movement attributes
	protected double moveSpeed;
	protected double maxSpeed;
	protected double stopSpeed;
	protected double fallSpeed;
	protected double maxFallSpeed;
	protected double jumpStart;
	protected double stopJumpSpeed; //this is if we want the effect of when holding "up" key we can jump higher
	//constructor
	public MapObject(TileMap tm){
		tileMap=tm;
		tileSize=tm.getTileSize();
	}
	public boolean intersects(MapObject o){
		Rectangle r1= getRectangle();
		Rectangle r2=o.getRectangle();
		return r1.intersects(r2); //tell us if these two map object have collided
	}
	public Rectangle getRectangle(){
		return new Rectangle((int)x-cwidth,(int)y-cheight,cwidth*2,cheight*2);
	}
	public void calculateCorners(double x, double y){
		
	    int leftTile = (int)(x - cwidth / 2) / tileSize;
        int rightTile = (int)(x + cwidth / 2 - 1) / tileSize; //-1 to make sure we don't step over 
        int topTile = (int)(y - cheight / 2) / tileSize;
        int bottomTile = (int)(y + cheight / 2 - 1) / tileSize;//-1 we don't wanna go downward to the next tile
        if(topTile < 0 || bottomTile >= tileMap.getNumRows() || leftTile < 0 || rightTile >= tileMap.getNumCols()){
                topLeft = topRight = bottomLeft = bottomRight = false;
                return;
        }
        int tl = tileMap.getType(topTile, leftTile);
        int tr = tileMap.getType(topTile, rightTile);
        int bl = tileMap.getType(bottomTile, leftTile);
        int br = tileMap.getType(bottomTile, rightTile);
        topLeft = tl == Tile.BLOCKED; //if we jump into a wall then topLeft will be set to true
        topRight = tr == Tile.BLOCKED;
        bottomLeft = bl == Tile.BLOCKED;
        bottomRight = br == Tile.BLOCKED;
	}
	
	public void checkTileMapCollision(){
		currCol=(int)x/tileSize;
		currRow=(int)y/tileSize;
		
		xdest=x+dx;
		ydest=y+dy;
		
		xtemp=x;
		ytemp=y;
		
		calculateCorners(x,ydest);
		if(dy<0){ //checking y direction (upward)
			if(topLeft||topRight){
				dy=0;//stop moving up
				ytemp=currRow*tileSize+cheight/2; //set us right below the tile that we just bumped into
			}	
			else{ytemp+=dy;}//if we didn't hit anything, we can keep going
		}
		if(dy>0){
			if(bottomLeft||bottomRight){
				dy=0;
				falling=false;
				ytemp=(currRow+1)*tileSize-cheight/2;
			}
			else{ytemp+=dy;}
		}
		calculateCorners(xdest,y);
		if(dx<0){//going left
			if(topLeft||bottomLeft){
				dx=0;
				xtemp=currCol*tileSize+cwidth/2;
			}
			else{xtemp+=dx;}
		}
		if(dx>0){
			if(topRight||bottomRight){
				dx=0;
				xtemp=(currCol+1)*tileSize-cwidth/2;
			}
			else{xtemp+=dx;}
		}
		if(!falling){
			calculateCorners(x,ydest+1);//check if we fall off a cliff
			if(!bottomLeft&&!bottomRight){
				falling=true;
			}
		}
	}
	public int getx(){return (int)x;}
	public int gety(){return (int)y;}
	public int getxmap(){return (int)xmap;}
	public int getymap(){return (int)ymap;}
	public int getWidth(){return width;}
	public int getHeight(){return height;}
	public int getCWidth(){return cwidth;}
	public int getCHeight(){return cheight;}
	
	public void setPosition(double x,double y){//regular position (local)
		this.x=x;
		this.y=y;
	}
	public void setVector(double dx, double dy){
		this.dx=dx;
		this.dy=dy;
	}
	
	public void setMapPosition(){//map position (global),tell us where to draw our character
		xmap=tileMap.getx();
		ymap=tileMap.gety();
	}
	
	public void setLeft(boolean b){left=b;} //set the action
	public void setRight(boolean b){right=b;}
	public void setUp(boolean b){up=b;}
	public void setDown(boolean b){down=b;}
	public void setJumping(boolean b){jumping=b;}
	
	public boolean notOnScreen(){
		return x+xmap+width<0||x+xmap-width>GamePanel.WIDTH||y+ymap+height<0||y+ymap-height>GamePanel.HEIGHT; //the final position of the player on screen
	}
	public void draw(Graphics2D g){
		if(facingRight){
			g.drawImage(animation.getImage(),(int)(x+xmap-width/2),(int)(y+ymap-height/2),null);
		}
		else{
			g.drawImage(animation.getImage(),(int)(x+xmap-width/2+width),(int)(y+ymap-height/2),-width,height,null);
		}
		
		//draw collision box
		//Rectangle r = getRectangle();
		//r.x += xmap;
		//r.y += ymap;
		//g.draw(r);
	}
	public void draw1(Graphics2D g){
		if(facingRight){
			g.drawImage(animation1.getImage(),(int)(x+xmap-width/2),(int)(y+ymap-height/2),null);
		}
		else{
			g.drawImage(animation1.getImage(),(int)(x+xmap-width/2+width),(int)(y+ymap-height/2),-width,height,null);
		}
	}
}
