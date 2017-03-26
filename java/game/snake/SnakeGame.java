//package greg;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.JFrame;
//这是自定义蛇及食物的节点的自定义类，不是javaAPI中的Node哦。
class Node
{
	int x,y;
	Color color = Color.BLACK;
	Node(int x,int y,Color color)
	{
		this.x = x;
		this.y = y;
		this.color = color;
	}
	Node(int x,int y)
	{
		this(x,y,Color.BLACK);
	}
}
class SnakeGame extends JFrame
{
	//常量小的们。
	public static final int F_W = 600;//窗体宽。
	public static final int F_H = 600;//窗体高。
	public static final int S_L = 15;//初始长度。
	public static final int S_W = 10;//蛇块宽。
	public static final int S_H = 10;//蛇块高。
	public static final int S_X = F_W/2 - S_W/2;//初始横坐标。
	public static final int S_Y = F_H/3 - S_H/2;//初始纵坐标。
	public static final int S_S = 0;//蛇的块与块之间的间隙。
	public static final int S_SPEED = 90;//蛇的初始速度。单位：（1/10块）/秒。
	public static final int R_P = 1;//运行频率。
	public static final Color C_B = Color.WHITE;//画布背景色。
	public static final Color C_SHEAD = Color.RED;//蛇头颜色。
	public static final Color C_SBODY = Color.BLUE;//蛇身颜色。
	public static final Color C_FOOD = Color.GREEN;//食物颜色。
	public static final int ADD_SCORE = 10;//每次增加的分数。
	public static final int ADD_SPEED = 10;//每次增加的速度。
	public static final int D_UP = 0;
	public static final int D_DOWN = 1;
	public static final int D_LEFT = 2;
	public static final int D_RIGHT = 3;
	//变量成员们。
	private Canvas c;//画布，，= =
	private Graphics g;//图形工具，，仰仗楼上：D
	private Label lbScore;//用来显示分数的标签，到时去北方。。maybe
	private int score;//楼上的内容（儿子，，楼上是母滴？.？）。
	private ArrayList<Node> alSnake;//用来装蛇头和蛇身的容器。
	private int speed;//蛇的运行速度。
	private int direction;//蛇的运行方向。
	private Node food;//食物。
	private int fx;//食物的x坐标。
	private int fy;//食物的y坐标。
	private Node head;//蛇头。
	private boolean gameOver = false;//游戏结束开关。
	private boolean gameStart = false;//游戏开始开关。
	private int colorMode = 0;//颜色模式。#2016/9/19 13:50添加#
	private int colorCount = 0;//颜色计数。#2016/9/19 14:00添加#
	SnakeGame()
	{
		initFrame();
		initSnake();
		initFood();
		drawSnake();
		addEvents();
		runSnake();
		//System.out.println("frame:"+this.getWidth()+",\ncanvas:"+c.getWidth()+",\nlabel:"+lbScore.getHeight());
	}
	//各种各样的函数。
	//初始化窗口。
	private void initFrame()
	{
		setTitle("七彩贪食蛇");
		setBounds(300,50,F_W+16,F_H+62);
		c = new Canvas();
		c.setBackground(C_B);
		add(c);
		lbScore = new Label("分数："+0);
		add(lbScore,BorderLayout.NORTH);
		setVisible(true);
		//代码们累了，睡了0.1秒。
		try{Thread.sleep(100);}catch(Exception e){throw new RuntimeException("虽然不知道怎么回事，貌似出了异常。。");}
		//起来干活，交出g。
		g = c.getGraphics();
	}
	//画一个节点。
	private void drawNode(Node n)
	{
		g.setColor(n.color);
		g.fillRect(n.x,n.y,S_W,S_H);
	}
	//画一条蛇。。
	private void drawSnake()
	{
		for(Node n:alSnake)
			drawNode(n);
	}
	//画出食物。
	private void drawFood()
	{
		food.x = fx;
		food.y = fy;
		trimNodeLocation(food);
		drawNode(food);
	}
	//随机食物坐标。
	private void randFoodLocation()
	{
		fx = new Random().nextInt(F_W-S_W);
		fy = new Random().nextInt(F_H-S_H);
		//trimLocation(fx,fy);
	}
	//调整节点坐标。
	/*#2016/9/18 20:00修改#
		以前为trimLocation(int x,int y),其中传入的x,y本身
		值不会改变，没有效果。
		将参数改为引用类型，可以在函数内部改变外部的值。
	*/
	private void trimNodeLocation(Node n)
	{
		//System.out.println(x+","+y);
		if(n.x%S_W!=0)
			n.x += S_W-n.x%S_W;
		if(n.y%S_H!=0)
			n.y += S_H-n.y%S_H;
		//System.out.println(x+","+y);
	}
	//初始化食物。
	private void initFood()
	{
		randFoodLocation();
		food = new Node(fx,fy,C_FOOD);
		drawFood();
	}
	//初始化蛇和分数。
	private void initSnake()
	{
		score = 0;
		speed = S_SPEED;
		direction = D_UP;
		alSnake = new ArrayList<Node>();
		//trimLocation(tx,ty);//无效。
		
		head = new Node(S_X,S_Y,C_SHEAD);
		trimNodeLocation(head);
		//System.out.println("line146--"+head.x+","+head.y);
		alSnake.add(head);
		Node[] body = new Node[S_L-1];
		for(int i=0;i<S_L-1;i++)
		{
			body[i] = new Node(head.x,head.y+S_H*(i+1)+(i+1)*S_S,C_SBODY);
			alSnake.add(body[i]);
		}
		changeBodyColor(colorMode);
	}
	//重设游戏。
	private void resetGame()
	{
		clear();
		initSnake();
		initFood();
		gameOver = false;
	}
	//改变蛇体的颜色。
	/*#2016/9/19 13:00改版#
	添加了参数mode，现在有不同的变色方案，
	通过键盘上0-9数字键选择。
	*/
	private void changeBodyColor(int mode)
	{
		boolean flag = false;
		int index;
		Color c;
		switch (mode)
		{
			case 0:
				Color[] colors = new Color[7];
				colors[0] = Color.BLUE;
				colors[1] = Color.ORANGE;
				colors[2] = Color.CYAN;
				colors[3] = Color.GREEN;
				colors[4] = Color.PINK;
				colors[5] = Color.MAGENTA;
				colors[6] = Color.GRAY;
				for(Node n:alSnake)
					if(flag){
						index = new Random().nextInt(6);
						n.color = colors[index];
					}else
						flag = true;
				break;
			case 1:
				int r,g,b;
				for(Node n:alSnake)
					if(flag){
						r = new Random().nextInt(255);
						g = new Random().nextInt(255);
						b = new Random().nextInt(255);
						c = new Color(r,g,b);
						n.color = c;
					}else
						flag = true;
				break;
			case 2:
				int len = alSnake.size()-1;
				int pos = 0;
				int cb;
				//index = new Random().nextInt(6);
				//cb = Color.BLUE;//colors[index];
				for(Node n:alSnake)
				{
					if(flag){
						//rgb = c.getRGB();
						cb = 255 - Math.abs(pos++-colorCount)*6;
						c = new Color(0,0,cb);
						n.color = c;
						//System.out.println("line217-->cb="+cb);
					}else
						flag = true;
				}
				colorCount++;
				if(colorCount>len)
					colorCount = 0;
				break;
		}
		
	}
	//蛇蛇跑起来。
	private void runSnake()
	{
		new Thread(){
			public void run()
			{
				while(true)
				{
					try{Thread.sleep(10000/speed);}catch(Exception e){throw new RuntimeException("链子掉了");}
					if(gameStart)
					{
						checkHit();
						if(!gameOver){
							lbScore.setText("分数："+score);
							setThisStep();
							clear();
							drawFood();
							changeBodyColor(colorMode);
							drawSnake();
							//System.out.println(head.x+","+head.y);
						}
					}	
				}
			}
		}.start();
	}
	//检查并设置这次要走的位置。
	private void setThisStep()
	{
		//System.out.println(head.x+","+head.y);
		Node lastNode = null;// = head;
		Node nowNode;
		//head.y -= R_P*S_H +1;
		boolean flag = false;
		
		for(Node n:alSnake)
		{
			/*
			一开始直接写nowNode = n;这样操作的是同一个对象,
			当n属性值改变，nowNode、lastNode属性值会一起改变。
			*/
			nowNode = new Node(n.x,n.y,n.color);//当前改变之前的值。		
			if(flag)
			{
				n.x = lastNode.x;
				n.y = lastNode.y;
			}else{
				switch (direction)
				{
					case D_UP:
						n.y -= R_P*S_H + S_S;
						break;
					case D_DOWN:
						n.y += R_P*S_H + S_S;
						break;
					case D_LEFT:
						n.x -= R_P*S_W + S_S;
						break;
					case D_RIGHT:
						n.x += R_P*S_W + S_S;
						break;	
				}
				
			}
			lastNode = nowNode;//对下次来说，这就是上次未改变之前的值。
			flag = true;
			//System.out.println(n.x+","+n.y);
		}
	}
	//添加身体。
	private void addBody()
	{
		//0,0是随意给的坐标，其它任何坐标都可以，因为在drawSnake()中会将其改变为正确坐标。
		alSnake.add(new Node(0,0,C_SBODY));
	}
	//重置画布，从此一片空白。。世界因此显得宁静。
	private void clear()
	{
		g.setColor(C_B);
		g.fillRect(0,0,F_W,F_H);
	}
	//检查各种碰撞。
	private void checkHit()
	{
		boolean flag = true;
		//蛇头与蛇身的碰撞。
		for(Node n:alSnake)
		{
			if(flag)
			{
				flag = false;
				continue;
			}
			if(hitNode(n,head))
				gameOver = true;
		}
		//蛇头与墙的碰撞。
		if(head.x<0 || head.x>F_W-S_W || head.y<0 || head.y>F_H-S_H)
		{
			gameOver = true;
		}
		//蛇头与食物的碰撞。
		if(hitNode(food,head))
		{
			//加分。
			score += ADD_SCORE;
			//加速。
			/*#2016/9/18 21:00修改#
			之前为speed += ADD_SCORE;
			每次都加速同样的值，后期难度
			过大。如今加速增加量逐渐递减。
			大幅度降低了难度。
			*/
			int a = (110-score)/10;
			if(a<=1)
				a = 1;
			speed += a;
			//System.out.println("line299-->speed:"+speed);
			randFoodLocation();
			addBody();
		}
	}
	//节点碰撞检测。
	private boolean hitNode(Node n1,Node n2)
	{
		Rectangle r1 = new Rectangle(n1.x,n1.y,S_W,S_H);
		Rectangle r2 = new Rectangle(n2.x,n2.y,S_W,S_H);
		return r1.intersects(r2);
	}
	//添加事件。
	private void addEvents()
	{
		//#2016/9/19 17:10修改#将键盘事件内容封装成了GameKeyAdapter类,方便多次调用。
		c.addKeyListener(new GameKeyAdapter());
		this.addKeyListener(new GameKeyAdapter());
	}
	//自定义的游戏键盘事件。#2016/9/19 17:10添加#
	class GameKeyAdapter extends KeyAdapter
	{
		public void keyPressed(KeyEvent e)
		{
			if(e.getKeyCode() == e.VK_UP)
				if(direction!=D_UP && direction!= D_DOWN)
					direction = D_UP;
			if(e.getKeyCode() == e.VK_DOWN)
				if(direction!=D_UP && direction!= D_DOWN)
					direction = D_DOWN;
			if(e.getKeyCode() == e.VK_LEFT)
				if(direction!=D_LEFT && direction!= D_RIGHT)
					direction = D_LEFT;
			if(e.getKeyCode() == e.VK_RIGHT)
				if(direction!=D_LEFT && direction!= D_RIGHT)
					direction = D_RIGHT;

			if(e.getKeyCode() == e.VK_ENTER){
				if(gameOver)
					resetGame();
				if(!gameStart)
					gameStart = true;
			}
			if(e.getKeyCode() == e.VK_0)
				colorMode = 0;
			if(e.getKeyCode() == e.VK_1)
				colorMode = 1;
			//if(e.getKeyCode() == e.VK_2)
				//colorMode = 2;
			//if(e.getKeyCode() == e.VK_3)
				//colorMode = 3;
		}
	}

	//主函数：我不是自愿在最下面的。
	public static void main(String[] args)
	{
		new SnakeGame();
	}
}
