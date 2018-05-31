package shooting;

import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class KSFrame extends JFrame implements Runnable {
	BufferedImage im;
	BufferedImage buff;
	Thread t;
	public int fps = 60;
	public long error = 0;
	public long idealSleep = (1000 << 16) / fps;
	public long oldTime;
	public long newTime = System.currentTimeMillis() << 16;
	public int bufffps = 0;
	public int nowfps = 0;
	public int fpsOldTime = new Date().getSeconds();
	public int fpsNewTime = new Date().getSeconds();
	public KSFrame(int width, int height) {
		super();
		setSize(width,height);
		setLocationRelativeTo(null);
		im=new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		buff=new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				setVisible(false);
				System.exit(1);
			}
		});
	}
	public synchronized void saveImage(File dst) throws IOException {
		ImageIO.write(im, "png", dst);
	}

	@Override
	public void paint(Graphics g) {
		g.drawImage(im, 0, 0, this);
		if (t == null) {
			t = new Thread(this);
			t.start();
		}
	}
	public void initFPS() {
		error = 0;
		idealSleep = (1000 << 16) / fps;
		oldTime = 0;
		newTime = System.currentTimeMillis() << 16;
		bufffps = 0;
		nowfps = 0;
		fpsOldTime = new Date().getSeconds();
		fpsNewTime = new Date().getSeconds();
	}
	public void updateBeforeFPS() {
		oldTime = newTime;

		//fps処理
		if(fpsOldTime==fpsNewTime) {
			fps += 1;
		}else {
			nowfps = fps;
			fps = 0;
			fpsOldTime = fpsNewTime;
		}
		fpsNewTime = new Date().getSeconds();
	}
	public void updateAfterFPS() {
		newTime = System.currentTimeMillis() << 16;
		long sleepTime = idealSleep - (newTime - oldTime) - error; // 休止できる時間
		if(sleepTime < 0x20000) sleepTime = 0x20000; // 最低でも2msは休止
		oldTime = newTime;
		try {
			Thread.sleep(sleepTime >> 16);
		} catch(InterruptedException e) {
			e.printStackTrace();
		}// 休止
		newTime = System.currentTimeMillis() << 16;
		error = newTime - oldTime - sleepTime; // 休止時間の誤差
	}
	public void buffPaint() {
		getGraphics().drawImage(buff, 0, 0, this);
	}
	public void sleep(double time) {
		try {
			getImageGraphics().fillRect(0,0,0,0);
			Thread.sleep((int)(time*1000));
		} catch(InterruptedException e){
			e.printStackTrace();
		}
	}
	public void run() {

	}
	public Graphics getImageGraphics() {
		return buff.getGraphics();
	}
}
