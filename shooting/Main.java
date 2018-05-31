package shooting;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;
import java.util.Vector;

public class Main extends KSFrame implements KeyListener {
	public static Main frame;

	public boolean key_esc;
	public boolean key_up;
	public boolean key_down;
	public boolean key_left;
	public boolean key_right;
	public boolean key_space;

	public boolean run = true;

	Graphics g;

	public static void main(String[] args) {
		frame = new Main();
		frame.setVisible(true);
	}

	public Main() {
		super(600,600);
		setTitle("シューティング");
		addKeyListener(this);
		setResizable(false);
		fps = 10;
	}

	enum STATUS{
		TITLE,
		GAME,
		GAMEOVER,
	}

	STATUS status = STATUS.TITLE;
	@Override
	public void run(){
		initFPS();
		g = getImageGraphics();

		int x = 300;
		int y = 500;
		int bullet_interval = 10; // playerの弾の間隔
		int bullet_interval_i = 0;
		int enemy_bullet_interval = 50; // enemyの弾の間隔
		int enemy_interval = 60; // enemyの出現間隔
		int enemy_interval_i = 0;
		int point = 0;
		Random rand = new Random(1654564456);
		Vector<Bullet> player_bullet = new Vector<>();
		Vector<Bullet> enemy_bullet = new Vector<>();
		Vector<Enemy> enemy = new Vector<>();

		while(run){
			updateBeforeFPS();
			g.setColor(Color.WHITE); // 背景色指定
			g.fillRect(0, 0, 600, 600); // 背景めいっぱい
			g.setColor(Color.BLACK); // 文字色

			switch (status) {
			case GAME:
				g.setColor(Color.BLUE); // playerの母体
				g.fillRect(x, y, 10, 10);
				g.fillRect(x-10, y+10, 30, 10);
				g.setColor(Color.BLACK); // playerの弾の色
				if(key_left && x >= 10){ // 母体の左移動の調整
					x -= 8;
				}
				if(key_right && x <= 580){ // 母体の右移動の調整
					x += 8;
				}
				if(key_up && y >= 50){ // 母体の上移動の調整
					y -= 8;
				}
				if(key_down && y <= 580){ // 母体の下移動の調整
					y += 8;
				}
				if(key_space){ // playserの弾撃
					bullet_interval_i++;
					if(bullet_interval <= bullet_interval_i){
						player_bullet.addElement(new Bullet(x, y, 0, -10));
						bullet_interval_i = 0;
					}
				}
				if(point/10>50){ // 敵を倒す毎に敵の攻撃間隔が短くなる
					enemy_bullet_interval = 50;
				}
				if(point/10>80){ // 敵を倒す毎に敵の攻撃間隔が短くなる
					enemy_bullet_interval = 40;
				}
				if(point/10>100){ // 敵を倒す毎に敵の攻撃間隔が短くなる
					enemy_bullet_interval = 30;
				}
				if(point/10>150){ // 敵を倒す毎に敵の出現間隔が短くなる
					enemy_interval = 50;
				}
				if(point/10>200){ // 敵を倒す毎に敵の出現間隔が短くなる
					enemy_interval = 40;
				}
				if(point/10>250){ // 敵を倒す毎に敵の出現間隔が短くなる
					enemy_interval = 30;
				}
				enemy_interval_i++;
				if(enemy_interval <= enemy_interval_i){
					enemy.addElement(new Enemy(rand.nextInt(590), 0, 0, 4));
					enemy_interval_i = 0;
				}
				for(int i = 0; i < player_bullet.size(); i++){ // player
					Bullet bullet = player_bullet.get(i);
					g.fillRect(bullet.x, bullet.y, 4, 4); // 弾の大きさ
					for(int l = 0; l < enemy.size(); l++){ // enemy撃破の処理
						Enemy enemy1 = enemy.get(l);
						if(bullet.x >= enemy1.x-10 && bullet.x <= enemy1.x+20 && bullet.y >= enemy1.y-10 && bullet.y <= enemy1.y+10){
							enemy.remove(l);
							player_bullet.remove(i); // enemyを撃破した弾の消去
							point += 10; // 得点追加
						}
					}
					bullet.x += bullet.speedX;
					bullet.y += bullet.speedY;
					if(bullet.y <= 0){
						player_bullet.remove(i);
					}
				}
				for(int i = 0; i < enemy_bullet.size(); i++){ // enemy
					Bullet bullet = enemy_bullet.get(i);
					g.fillRect(bullet.x, bullet.y, 4, 4); // 弾の大きさ
					bullet.x += bullet.speedX;
					bullet.y += bullet.speedY;
					if(bullet.y <= 0){
						enemy_bullet.remove(i);
					}
					if(x+20 >= bullet.x && x-10 <= bullet.x+4 && y >= bullet.y-10 && y <= bullet.y-2){ // playerが弾にあたって負ける範囲
						status = STATUS.GAMEOVER;
					}
				}
				for(int i = 0; i < enemy.size(); i++){
					Enemy bullet = enemy.get(i);
					g.setColor(Color.RED); // enemyの母体
					g.fillRect(bullet.x-10, bullet.y-10, 30, 10);
					g.fillRect(bullet.x, bullet.y, 10, 10);
					g.setColor(Color.BLACK); // enemyの弾の色
					bullet.x += bullet.speedX;
					bullet.y += bullet.speedY;
					bullet.bullet_interval++;
					if(bullet.y >= 600){
						enemy.remove(i);
					}
					if(bullet.bullet_interval >= enemy_bullet_interval){
						enemy_bullet.addElement(new Bullet(bullet.x, bullet.y, 0, 10));
						bullet.bullet_interval = 0;
					}
					if(x+20 >= bullet.x-10 && x-10 <= bullet.x+20 && y >= bullet.y-10 && y-20 <= bullet.y+10){ // playerが母体にあたって負ける範囲
						status = STATUS.GAMEOVER;
					}
				}
				setFont(14);
				g.drawString(point+"point", 10, 560);
				break;
			case GAMEOVER:
				setFont(30);
				g.drawString("ゲームオーバー！ポイント："+point, 100, 300);
				setFont(20);
				g.drawString("ESCでスタート画面へ", 200, 330);
				if(key_esc){
					status = STATUS.TITLE;
					x = 300;
					y = 500;
					bullet_interval = 10; // playerの弾の間隔
					bullet_interval_i = 0;
					enemy_bullet_interval = 50; // enemyの弾の間隔
					enemy_interval = 60; // enemyの出現間隔
					enemy_interval_i = 0;
					point = 0;
					player_bullet = new Vector<>();
					enemy_bullet = new Vector<>();
					enemy = new Vector<>();
				}
				break;
			case TITLE:
				setFont(40);
				g.drawString("シューティング", 180, 300);
				setFont(20);
				g.drawString("スペースキーでスタート", 200, 340);
				if(key_space){
					status = STATUS.GAME;
				}
				break;
			default:
				break;
			}

			setFont(14);
			g.drawString(nowfps+"FPS", 10, 580);

			buffPaint();

			updateAfterFPS();
		}
	}

	public void setFont(int size) {
		g.setFont(new Font("ＭＳ　ゴシック", Font.PLAIN, size));
	}

	@Override
	public void keyPressed(KeyEvent e){
		if(e.getKeyCode()==KeyEvent.VK_ESCAPE) key_esc = true;
		if(e.getKeyCode()==KeyEvent.VK_UP) key_up = true;
		if(e.getKeyCode()==KeyEvent.VK_DOWN) key_down = true;
		if(e.getKeyCode()==KeyEvent.VK_LEFT) key_left = true;
		if(e.getKeyCode()==KeyEvent.VK_RIGHT) key_right = true;
		if(e.getKeyCode()==KeyEvent.VK_SPACE) key_space = true;
	}

	@Override
	public void keyReleased(KeyEvent e){
		if(e.getKeyCode()==KeyEvent.VK_ESCAPE) key_esc = false;
		if(e.getKeyCode()==KeyEvent.VK_UP) key_up = false;
		if(e.getKeyCode()==KeyEvent.VK_DOWN) key_down = false;
		if(e.getKeyCode()==KeyEvent.VK_LEFT) key_left = false;
		if(e.getKeyCode()==KeyEvent.VK_RIGHT) key_right = false;
		if(e.getKeyCode()==KeyEvent.VK_SPACE) key_space = false;
	}

	@Override
	public void keyTyped(KeyEvent e){

	}

}

class Bullet{
	public int x;
	public int y;
	public int speedX;
	public int speedY;

	public Bullet(int x, int y, int speedX, int speedY){
		this.x = x;
		this.y = y;
		this.speedX = speedX;
		this.speedY = speedY;
	}
}

class Enemy{
	public int x;
	public int y;
	public int speedX;
	public int speedY;
	public int bullet_interval = 0;

	public Enemy(int x, int y, int speedX, int speedY){
		this.x = x;
		this.y = y;
		this.speedX = speedX;
		this.speedY = speedY;
	}
}
