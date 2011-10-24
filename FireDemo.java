/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author tim
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

public class FireDemo extends JPanel{
    final int WIDTH = 300;
    final int HEIGHT = 300;
    BufferedImage buf;
    Graphics2D g2d, bufGraphics;    
    long animStartTime;
    int scanLineShift = 1;

    public FireDemo(){
        setPreferredSize(new Dimension(WIDTH,HEIGHT));
        buf = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
        bufGraphics = (Graphics2D)buf.getGraphics();
        setupFire();
        Timer timer = new Timer(50, new Animate());
        timer.setInitialDelay(1000);
        timer.setCoalesce(true);
        animStartTime = 1000 + System.nanoTime()/1000000;
        timer.start();
    }

    int[][] fire;
    int[] buffer;
    int[] palette;
    
    public void setupFire(){
        int color;
        fire = new int[WIDTH][HEIGHT];
        float time = System.currentTimeMillis(), oldTime;
        for (int x = 0; x < WIDTH; x++)
            for (int y = 0; y < HEIGHT; y++)
                fire[x][y] = 0;
        
        palette = new int[256];
        for (int x = 0; x < 256; x++){
            color = Color.HSBtoRGB((x)/(3.0f*255.0f), 1.0f, (float)Math.min(1.0, x/255.0 *2));
            //color = Color.HSBtoRGB(0.5f + 0.5f*(float)Math.sin(x), 1.0f, (float)Math.min(1.0, x/255.0 *2));
            palette[x] = color;
        }
        
    }
    
    public void updateFire(){
        for(int x = 0; x < WIDTH; x++){
            fire [x][HEIGHT - 1] = (int)(Math.random() * 256);           
        }

        for (int y = 0; y < HEIGHT-1; y++){
            for (int x = 0; x < WIDTH; x++){     
                fire[x][y] =
                        (int)(((fire[(x - 1 + WIDTH) % WIDTH][(y + 1)]
                        + fire[x % WIDTH][(y + 1) % HEIGHT]
                        + fire[(x + 1 + WIDTH) % WIDTH][(y + 1)]
                        + fire[x % WIDTH][(y + 2) % HEIGHT]
                                )) /4.006);
            }
        }
        buffer = new int[HEIGHT * WIDTH];
        for (int i = 0; i < buffer.length; i++){
            buffer[i] = palette[fire[i % WIDTH][i/HEIGHT]];
        }
        buf.setRGB(0,0,WIDTH,HEIGHT,buffer,0,WIDTH);
        
    }  
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        g2d = (Graphics2D)g.create();       
        bufGraphics.setColor(Color.RED);
        g2d.drawImage(buf,0,0,null);     
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable(){
            
            public void run(){
                JFrame frame = new JFrame();
                FireDemo panel = new FireDemo();
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.getContentPane().add(panel, BorderLayout.CENTER);
                frame.pack();
                frame.setVisible(true);
            }
        });
    }
    
    class Animate implements ActionListener{
        public void actionPerformed(ActionEvent e){
            updateFire();
            repaint();
        }
    }
}

