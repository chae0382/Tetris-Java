import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Random;
import java.util.Timer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import java.io.File;


public class Tetris extends JFrame{
	//1. GUI 테트리스 화면 구성
	//2. GUI 메뉴 구현
	//3. Thread
	//4. 이벤트 처리
	//5. 파일 입출력
	//6. 순서도 작성
	
	//버튼객체의 좌표
	public JButton jb[][];
	//각 버튼에 색이 있으면 1, 화이트이면 0
	public Boolean state[][];
	
	//랜덤으로 색깔을 입히기 위한 배열
	Color c1 = new Color(255,255,000);//노란색
	Color c2 = new Color(153,153,255);//연한 보라색
	Color c3 = new Color(153, 255,153);//연한 연두색
	Color c4 = new Color(255,153,255);//연한 핑크색
	Color c5 = new Color(153,255,255);//연한 하늘색
	Color c6 = new Color(255,153,153);//연한 빨간색
	Color c7 = new Color(153,204,255);//연한 파란색
	public Color blockColor[]={c1, c2, c3, c4, c5, c6, c7};
	
	//내려가는 블록의 cell들의 위치를 저장
	public int x[] = new int[4];
	public int y[] = new int[4];
	
	//내려가는 도중의 cell 위치의 변화값 저장
	public int plusX = 0;
	public int plusY = 0;
	
	//처음위치가 0, 시계방향으로 rotation은 0->1->2->3 으로 변함
	public int rotation = 0;
	
	//block의  모양, rotation, 그때의 cell 위치를 저장하는 배열
	public int block[][][]= {
            {
                    {0, 4}, {1, 4}, {2, 4}, {3, 4},
                    {1, 3}, {1, 4}, {1, 5}, {1, 6},
                    {0, 4}, {1, 4}, {2, 4}, {3, 4},
                    {1, 3}, {1, 4}, {1, 5}, {1, 6}
            }, // I
            {
                    {0, 4}, {0, 5}, {1, 4}, {1, 5},
                    {0, 4}, {0, 5}, {1, 4}, {1, 5},
                    {0, 4}, {0, 5}, {1, 4}, {1, 5},
                    {0, 4}, {0, 5}, {1, 4}, {1, 5}
            }, // O
            {
	                {0, 4}, {1, 4}, {2, 4}, {2, 3},
	                {1, 3}, {2, 3}, {2, 4}, {2, 5},
	                {0, 3}, {0, 4}, {1, 3}, {2, 3},
	                {1, 3}, {1, 4}, {1, 5}, {2, 5}
            }, // J
            {
	                {0, 4}, {0, 5}, {1, 3}, {1, 4},
	                {0, 4}, {1, 4}, {1, 5}, {2, 5},
	                {2, 3}, {2, 4}, {1, 4}, {1, 5},
	                {0, 4}, {1, 4}, {1, 5}, {2, 5}
            }, // S
            {
	                {0, 3}, {0, 4}, {1, 4}, {1, 5},
	                {2, 4}, {1, 4}, {1, 5}, {0, 5},
	                {1, 3}, {1, 4}, {2, 4}, {2, 5},
	                {2, 4}, {1, 4}, {1, 5}, {0, 5}
            }, // Z
            {
	                {0, 3}, {0, 4}, {0, 5}, {1, 4},
	                {1, 4}, {1, 5}, {0, 5}, {2, 5},
	                {2, 3}, {2, 4}, {2, 5}, {1, 4},
	                {0, 3}, {1, 3}, {2, 3}, {1, 4}
            }, // T
            {
	                {0, 4}, {1, 4}, {2, 4}, {2, 5},
	                {1, 3}, {2, 3}, {1, 4}, {1, 5},
	                {0, 4}, {1, 5}, {2, 5}, {0, 5},
	                {2, 3}, {2, 4}, {2, 5}, {1, 5}
            } // L
   };
	
	//게임의 종료 유무를 저장하는 Boolean 타입 변수
	public Boolean stop;
	
	//블록이 하나하나 내려올때마다 블록의 색깔과 유형이 랜덤으로 결정됨
	public int bColor;
	public int bType;
	public boolean b;
	public int score;
	public String strScore ;
	JMenu score2;
	JMenuBar menu;
	
	public void write() {
		Charset cs = null;
		Path p = null;
		try {
			cs = Charset.defaultCharset();
			p = new File("C:\\homework\\Tetris.txt").toPath();
			if(!Files.notExists(p)) {
				Files.delete(p);
			}
			Files.createFile(p);
			
			for(int i = 0; i<20;i++) {
				for(int j = 0; j<10;j++) {
					if(jb[i][j].getBackground()==Color.WHITE) {
						String s = Integer.toString(7);
						byte data[] = s.getBytes();
						Files.write(p,data,StandardOpenOption.APPEND);
					}
					else {
						for(int k = 0; k<7;k++) {
							if(jb[i][j].getBackground()==blockColor[k]) {
								String s = Integer.toString(k);
								byte data[] = s.getBytes();
								Files.write(p,data,StandardOpenOption.APPEND);
								break;
							}
						}
					}
				}
			}
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public void read() {
		Charset cs = null;
		Path p = null;
		try {
			cs = Charset.defaultCharset();
			p = new File("C:\\homework\\Tetris.txt").toPath();
			List<String>lines = Files.readAllLines(p,cs);
			int i = 0; int j = 0;
				for(String line: lines) {
					int n = Integer.parseInt(line);
					if(n==7) {
						jb[i][j].setBackground(Color.WHITE);
					}
					else {
						jb[i][j].setBackground(blockColor[n]);
					}
					j++;
					if(j==10) {
						i++;
						j=0;
					}
				}
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	//맨 아래 한줄이 채워졌을때 아래줄을 삭제하는 함수
	public void eraseLine() {
		synchronized(this){
			for(int i = 19; i >=0;i--) {
				for(int j=0;j<10;j++) {
					if(i==19) {
						jb[i][j].setBackground(Color.WHITE);
					}
					else {
						Color before =jb[i][j].getBackground();
						jb[i+1][j].setBackground(before);
						state[i+1][j] = state[i][j];
					}
				}
			}
		}
	}
	//사용자가 누르는 키보드에 반응
	//'a'는 블록이 반시계방향으로 회전
	//'b'는 블록이 시계방향으로 회전
	//'왼쪽''오른쪽''아래' 화살표에 따라 블록이 이동함
	class key extends KeyAdapter{

		@Override
		public void keyPressed(KeyEvent e) {
			// TODO Auto-generated method stub
			int key = e.getKeyCode();
			Boolean move = true;
			if(key == 68) {
				move = true;
				int change = 4*((rotation+1)%4);
				for(int i = 0; i < 4; i++) {
					if(plusY+block[bType][change+i][0]<0||plusY+block[bType][change+i][0]>=20||
							plusX+block[bType][change+i][1]<0||plusX+block[bType][change+i][1]>9) {
						move = false;
					}
					else if(state[plusY+block[bType][change+i][0]][plusX+block[bType][change+i][1]]==true) {
						move = false;
					}
				}
				if(move==true) {
					rotation = (rotation+1)%4;
					for(int i = 0; i <4; i++) {
						jb[y[i]][x[i]].setBackground(Color.WHITE);
						y[i]=plusY+block[bType][change+i][0];
						x[i]=plusX+block[bType][change+i][1];
					}
					for(int i = 0; i < 4; i++) {
						jb[y[i]][x[i]].setBackground(blockColor[bColor]);
					}
				}
			}
			else if(key == 65) {
				move = true;
				int change = 4*((4+rotation-1)%4);
				for(int i = 0; i < 4; i++) {
					if(plusY+block[bType][change+i][0]<0||plusY+block[bType][change+i][0]>=20||plusX+block[bType][change+i][1]<0||plusX+block[bType][change+i][1]>9) {
						move = false;
					}
					else if(state[plusY+block[bType][change+i][0]][plusX+block[bType][change+i][1]]==true) {
						move = false;
					}
				}
				if(move==true) {
					rotation = (4+rotation-1)%4;
					for(int i = 0; i <4; i++) {
						jb[y[i]][x[i]].setBackground(Color.WHITE);
						y[i]=plusY+block[bType][change+i][0];
						x[i]=plusX+block[bType][change+i][1];
					}
					for(int i = 0; i < 4; i++) {
						jb[y[i]][x[i]].setBackground(blockColor[bColor]);
					}
				}
			}
			else if(key == e.VK_RIGHT) {
				move = true;
				for(int i = 0; i < 4; i++) {
					if(x[i]+1>=10) {
						move = false;
					}
					else if(state[y[i]][x[i]+1]==true) {
						move = false;
					}
				}
				if(move==true) {
					for(int i = 0; i <4; i++) {
						jb[y[i]][x[i]].setBackground(Color.WHITE);
						x[i]++;
					}
					plusX++;
					for(int i = 0; i < 4; i++) {
						jb[y[i]][x[i]].setBackground(blockColor[bColor]);
					}
				}
			}
			else if(key == e.VK_LEFT) {
				move = true;
				for(int i = 0; i < 4; i++) {
					if(x[i]-1<0) {
						move = false;
					}
					else if(state[y[i]][x[i]-1]==true) {
						move = false;
					}
				}
				if(move==true) {
					for(int i = 0; i <4; i++) {
						jb[y[i]][x[i]].setBackground(Color.WHITE);
						x[i]--;
					}
					plusX--;
					for(int i = 0; i < 4; i++) {
						jb[y[i]][x[i]].setBackground(blockColor[bColor]);
					}
				}
			}
			else if(key == e.VK_DOWN) {
				move = true;
				for(int i = 0; i < 4; i++) {
					if(y[i]+1>=20) {
						move = false;
					}
					else if(state[y[i]+1][x[i]]==true) {
						move = false;
					}
				}
				if(move==true) {
					for(int i = 0; i <4; i++) {
						jb[y[i]][x[i]].setBackground(Color.WHITE);
						y[i]++;
					}
					plusY++;
					for(int i = 0; i < 4; i++) {
						jb[y[i]][x[i]].setBackground(blockColor[bColor]);
					}
				}
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void keyTyped(KeyEvent e) {
			// TODO Auto-generated method stub
			
		}
	};
	
	
	Tetris(){
		
		score = 0;
		strScore = Integer.toString(score);
		stop = false;
		
		//제목
		setTitle("안녕, 테트리스!");
		//x를 누르면 프로그램 종료
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		
		menu = new JMenuBar();
		JMenu score1 = new JMenu("점수");
		menu.add(score1);
		score2 = new JMenu(strScore);
		menu.add(score2);
		setJMenuBar(menu);
		
		//가로 10, 세로20 개의 버튼으로 구성된 레이아웃
		//색깔은 화이트로 초기화한다
		GridLayout gd = new GridLayout(20,10);
		setLayout(gd);
		jb = new JButton[20][10];
		state= new Boolean[20][10];

		for(int i = 0; i < 20; i++) {
			for(int j = 0; j < 10; j++) {
				jb[i][j] = new JButton();
				add(jb[i][j]);
				jb[i][j].setBackground(Color.WHITE);
				state[i][j]=false;
			}
		}
		
		addKeyListener(new key());
		setFocusable(true);
		setSize(500,1000);
		
		//블록과 색깔을 랜덤하게 하기 위함
		Random random = new Random();
		random.setSeed(System.currentTimeMillis());
		
		//메인 스레드
		class TetrisThread extends Thread{
			public void run() {
				try {
					Boolean b = true;
					
					//블록들이 가득찰때까지 실행
					while(stop==false) {
					
						plusX = 0;
						plusY = 0;
						
						//블록과 색깔을 랜덤하게 지정하여 초기 블록의 cell위치 지정
						bType = random.nextInt(7);
						bColor = random.nextInt(7);
						for(int i = 0; i <4; i++) {
							y[i] = block[bType][i][0];
							x[i] = block[bType][i][1];
							Color old = blockColor[bColor];
							if(state[y[i]][x[i]]==true) {
								stop = true;
								old = jb[y[i]][x[i]].getBackground();
							}
							jb[y[i]][x[i]].setBackground(old);
						}
						b = true;
						
						//블록이 착지할때까지 실행
						while(b==true&&stop==false) {
							
							Thread.sleep(1000);
							for(int i=0;i<4;i++) {
								if(y[i]==19) {
									b = false;
									break;
								}
							}
							if(b==true) {
								for(int i = 0; i < 4;i++) {
									int p = y[i]+1;
									int q = x[i];
									if(state[p][q]==true) {
										b = false;
										break;
									}
								}
							}
								if(b==true) {
									for(int k = 0;k<4;k++) {	
										jb[y[k]][x[k]].setBackground(Color.WHITE);
										y[k]++;
									}	
									plusY++;
									for(int k = 0;k<4;k++) {	
										jb[y[k]][x[k]].setBackground(blockColor[bColor]);
									}
								}
							}
							for(int i = 0; i <4;i++) {
								state[y[i]][x[i]]=true;
							}
							
							Boolean erase = true;
							for(int i =0; i<10;i++) {
								if(state[19][i]==false) {
									erase = false;
								}
							}
							if(erase==true) {
							    eraseLine();
							    score+=10;
								strScore = Integer.toString(score);
								score2 = new JMenu(strScore);
								menu.add(score2);
								setJMenuBar(menu);
							}
							write();
					}
						}catch(InterruptedException e) {
							e.printStackTrace();
						}
				interrupt();
			}
		}
		TetrisThread th1 = new TetrisThread();
		th1.start();
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		new Tetris();
	
	}
}
