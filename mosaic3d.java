/**
 * A 3D-ish mosaic using a dozen pictures.
 * Press right arrow key to change the composition.
 * Yuan Gao, 2012
 */

import processing.core.*; 
import processing.xml.*; 

// import java.io.File;
// import javax.imageio.ImageIO;

public class mosaic3d extends PApplet
{
  private float xMouse, yMouse;
  private float xOffset, yOffset;
  private float rxOffset, ryOffset;
  private Grid grid;

  public void setup()
  {
    size(800, 600, P3D);
    background(0);
    imageMode(CENTER);
    
    xOffset = width / 2;
    yOffset = height / 2;
    grid = new Grid(50, 50);

    frameRate(30);
  }

  public void draw()
  {
    background(0);

    xMouse = mouseX - xOffset;
    yMouse = mouseY - yOffset;
    
    rxOffset = lerp(rxOffset, xMouse / 30, 0.1f);
    ryOffset = lerp(ryOffset, yMouse / 30, 0.1f);
    
    translate(xOffset, yOffset, -2000);
    rotateY(radians(rxOffset));
    rotateX(radians(ryOffset));
    
    grid.update();
    grid.display();
  }

  public void keyPressed()
  {
    if(keyCode == RIGHT)
      grid.nextMosaic = (grid.nextMosaic + 1) % grid.numMosaics;
  }

  private class Cell
  {
    private PImage bitmap;
    private float xOffset, yOffset;
    private float zOffset, zOffsetTarget;
    private float r, g, b;

    public Cell(PImage bitmap, float xOffset, float yOffset)
    {
      this.bitmap = bitmap;
      this.xOffset = xOffset;
      this.yOffset = yOffset;
      zOffset = 0;
    }

    public void display()
    {
      pushMatrix();
      translate(xOffset, yOffset, zOffset);
      tint(r, g, b);
      image(bitmap, 0, 0);
      popMatrix();
    }

    public void update(float zOffsetTarget, int cellColor)
    {
      this.zOffsetTarget = zOffsetTarget;
      zOffset = lerp(zOffset, zOffsetTarget, 0.1f);
      r = lerp(r, red(cellColor), 0.1f);
      g = lerp(g, green(cellColor), 0.1f);
      b = lerp(b, blue(cellColor), 0.1f);
    }
  }

  private class Grid
  {
    private Cell[][] cell;
    private int row, col;
    private float cellSize;

    private PImage[] mosaics;
    
    public int numMosaics;
    public int nextMosaic;

    public Grid(int row, int col)
    {
      this.row = row;
      this.col = col;
      cell = new Cell[row][col];

      // File file = new File("./data");
      // String[] filenames = file.list(new FilenameFilter() {
      //   public boolean accept(File dir, String name)
      //   {
      //     return name.endsWith(".jpg");
      //   }
      // });
      // mosaics = new PImage[filenames.length];
      // for (int i = 0; i < mosaics.length; ++i) {
      //   System.out.println(filenames[i]);
      //   try {
      //     mosaics[i] = new PImage(ImageIO.read(new File("./data/" + filenames[i])));
      //   } catch (java.io.IOException e) {
      //     e.printStackTrace();
      //   }
      // }
      numMosaics = 12;
      mosaics = new PImage[numMosaics];
      for (int i = 0; i < numMosaics; i++)
        mosaics[i] = loadImage(i + ".jpg");

      cellSize = mosaics[0].width;

      for (int j = 0; j < row; ++j) {
        for (int i = 0; i < col; ++i) {
          int randomPortrait = floor(random(8));
          float xOffset = -col / 2 * cellSize + i * cellSize;
          float yOffset = -row / 2 * cellSize + j * cellSize;
          cell[j][i] = new Cell(mosaics[randomPortrait], xOffset, yOffset);
        }
      }
      nextMosaic = 0;
    }

    public void display()
    {
      for (int j = 0; j < row; ++j)
        for (int i = 0; i < col; ++i)
          cell[j][i].display();
    }

    public void update()
    {
      for (int j = 0; j < row; ++j) {
        for (int i = 0; i < col; ++i) {
          float zOffset = 
            brightness(mosaics[nextMosaic].pixels[i + j * mosaics[nextMosaic].width]) * 2;
          cell[j][i].update(zOffset, mosaics[nextMosaic].pixels[i + j * mosaics[nextMosaic].width]);
        }
      }
    }
  }

  static public void main(String args[])
  {
    PApplet.main(new String[] { "--bgcolor=#FFFFFF", "mosaic3d" });
  }
}