import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import tester.*;
import javalib.impworld.*;
import java.awt.Color;
import javalib.worldimages.*;

// represents a cell on the grid
class Cell {
  int cellWidth = 20;
  int cellHeight = 20;
  boolean mine;
  boolean flag;
  boolean shown; 
  ArrayList<Cell> neighbors;

  // constructor for testing
  Cell(boolean mine, boolean flag, boolean shown) {
    this.mine = mine;
    this.flag = flag;
    this.shown = shown;
    this.neighbors = new ArrayList<Cell>();
  }

  // other constructor
  Cell(boolean mine) {
    this(mine, false, false);
  }
  
  /*
   * TEMPLATE:
   * FIELDS:
   * ... this.cellWidth ...                                             --- int
   * ... this.cellHeight ...                                            --- int
   * ... this.mine ...                                                  --- boolean
   * ... this.flag ...                                                  --- boolean
   * ... this.shown ...                                                 --- boolean
   * ... this.neighbors ...                                             --- ArrayList<Cell>
   * 
   * METHODS:
   * ... this.countMines() ...                                          --- int
   * ... this.drawCell() ....                                           --- WorldImage
   */

  // In Cell
  // counts the number of mines around this Cell
  int countMines() {
    int count = 0;
    for (Cell c : this.neighbors) {
      if (c.mine) {
        count += 1;
      }
    }
    return count;
  }

  // In Cell
  // draws a single Cell
  public WorldImage drawCell() {

    // cell after it has been selected
    RectangleImage filledCell = new RectangleImage(cellWidth, cellHeight, OutlineMode.SOLID,
        Color.DARK_GRAY);
    // empty cell on the blank grid
    RectangleImage emptyCell = new RectangleImage(cellWidth, cellHeight, OutlineMode.SOLID,
        new Color(51, 153, 255));
    // black border surrounding every cell
    RectangleImage border = new RectangleImage(cellWidth, cellHeight, OutlineMode.OUTLINE,
        Color.BLACK);
    // image of the flag
    EquilateralTriangleImage flag = new EquilateralTriangleImage(cellWidth * .60, OutlineMode.SOLID,
        Color.GREEN);
    // image of the mine
    CircleImage mine = new CircleImage(cellWidth * 2 / 5, OutlineMode.SOLID, Color.RED);
    int numMines = this.countMines();
    WorldImage cell;

    if (!this.shown) {
      if (this.flag) {
        cell = new OverlayImage(flag, emptyCell);
      }
      else {
        cell = emptyCell;
      }
    }
    else {
      if (this.mine) {
        cell = new OverlayImage(mine, filledCell);
      }
      else {
        if (numMines == 0) {
          cell = filledCell;
        }
        else if (numMines == 1) {
          cell = new OverlayImage(
              new TextImage(String.valueOf(numMines), cellHeight * .75, Color.BLUE), filledCell);
        }
        else if (numMines == 2) {
          cell = new OverlayImage(
              new TextImage(String.valueOf(numMines), cellHeight * .75, Color.GREEN), filledCell);
        }
        else {
          cell = new OverlayImage(
              new TextImage(String.valueOf(numMines), cellHeight * .75, Color.RED), filledCell);
        }
      }
    }
    return new OverlayImage(border, cell);
  }
}

// represents the world state for the Minesweeper game
class Minesweeper extends World {
  int cellWidth = 20;
  int cellHeight = 20;
  int height;
  int width;
  int rows;
  int cols;
  int countMines;
  Random rand;
  ArrayList<ArrayList<Cell>> grid = new ArrayList<>();
  
  // constructor for testing
  Minesweeper(int rows, int cols, int numMines) {
    this(rows, cols, numMines, new Random());
  }

  // other constructor
  Minesweeper(int rows, int cols, int countMines, Random rand) {
    this.rows = rows;
    this.cols = cols;
    this.height = cellHeight * rows;
    this.width = cellWidth * cols;
    this.countMines = countMines;
    this.rand = rand;
    this.makeGrid();
    this.initMines(countMines);
  }
  
  /*
   * TEMPLATE:
   * FIELDS:
   * ... this.cellWidth ...                                             --- int
   * ... this.cellHeight ...                                            --- int
   * ... this.height ...                                                --- int
   * ... this.width ...                                                 --- int
   * ... this.rows ...                                                  --- int
   * ... this.cols ...                                                  --- int
   * ... this.countMines ...                                            --- int
   * ... this.rand ...                                                  --- Random
   * ... this.grid ...                                                --- ArrayList<ArrayList<Cell>>
   * 
   * METHODS:
   * ... this.makeGrid() ...                                            --- void
   * ... this.initMines(int remaining) ....                             --- void
   * ... this.addMines() ....                                           --- void
   * ... this.insideGrid(int x, int y) ...                              --- boolean
   * ... this.getCellInGrid(int x, int y) ...                           --- Cell
   * ... this.makeScene() ...                                           --- WorldScene
   */

  // In Minesweeper
  // makes the blank grid
  void makeGrid() {
    for (int i = 0; i < rows; i++) {
      ArrayList<Cell> temp = new ArrayList<>();

      for (int j = 0; j < cols; j++) {
        temp.add(new Cell(false, false, false));
      }
      this.grid.add(temp);
    }
    addMines();
  }

  // In Minesweeper
  // places the mines in the blank grid
  void initMines(int remaining) {
    int minesLeft = remaining;

    for (ArrayList<Cell> row : this.grid) {
      for (Cell c : row) {
        boolean isMine = rand.nextBoolean();
        if (minesLeft > 0 && isMine && !c.mine) {
          c.mine = true;
          minesLeft -= 1;
        }
      }
    }

    if (minesLeft != 0) {
      this.initMines(minesLeft);
    }
  }

  // In Minesweeper
  // adds the neighboring cells to the given cell
  void addMines() {
    for (int i = 0; i < this.rows; i++) {
      for (int j = 0; j < this.cols; j++) {
        Cell current = this.grid.get(i).get(j);
        for (int dx = -1; dx <= 1; dx++) {
          for (int dy = -1; dy <= 1; dy++) {
            int newX = i + dx;
            int newY = j + dy;

            if (insideGrid(newX, newY) && !(dx == 0 && dy == 0)) {
              current.neighbors.add(getCellInGrid(newX, newY));
            }
          }
        }
      }
    }

  }

  // In Minesweeper
  // checks if the x and y coordinates of the cell are within the grid
  boolean insideGrid(int x, int y) {
    return x >= 0 && x < this.rows && y >= 0 && y < this.cols;
  }

  // In Minesweeper
  // returns the specific cell at the given x and y position
  Cell getCellInGrid(int x, int y) {
    return this.grid.get(x).get(y);
  }

  // In Minesweeper
  // draws the game and grid with the mines and neighboring cells
  public WorldScene makeScene() {
    WorldScene drawn = new WorldScene(width, height);

    for (int i = 0; i < this.rows; i++) {
      for (int j = 0; j < this.cols; j++) {
        int x = (j * cellWidth) + (cellWidth / 2);
        int y = (i * cellWidth) + (cellWidth / 2);
        WorldImage drawnCell = this.getCellInGrid(i, j).drawCell();

        drawn.placeImageXY(drawnCell, x, y);
      }
    }
    return drawn;
  }
}

// examples of Minesweeper
class ExampleMinesweeper {
  Minesweeper testGrid1;
  Minesweeper assignmentGrid;
  WorldImage plainCell;
  WorldScene world = new WorldScene(500, 500);

  Cell ct1;
  Cell cf2;
  Cell cf3;
  Cell ct4;
  Cell cf5;
  Cell ct6;
  Cell cf7;
  Cell cf8;
  Cell cf9;
  Cell cf10;
  Cell cf11;
  Cell cf12;
  Cell cf13;
  Cell cf14;
  Cell cf15;
  Cell cf16;

  Cell cTest1;
  Cell cTest4;
  Cell cTest3;
  Cell cTest6;
  Cell cTest2;
  Cell cTest5;
  ArrayList<ArrayList<Cell>> grid;

  // initializing game data
  void init() {
    this.testGrid1 = new Minesweeper(15, 15, 2, new Random(3));
    this.assignmentGrid = new Minesweeper(30, 16, 99);

    // example cell attributes 
    ct1 = new Cell(true);
    cf2 = new Cell(false);
    cf3 = new Cell(false);
    ct4 = new Cell(true);
    cf5 = new Cell(false);
    ct6 = new Cell(true);
    cf7 = new Cell(false);
    cf8 = new Cell(false);
    cf9 = new Cell(false);
    cf10 = new Cell(false);
    cf11 = new Cell(false);
    cf12 = new Cell(false);
    cf13 = new Cell(false);
    cf14 = new Cell(false);
    cf15 = new Cell(false);
    cf16 = new Cell(false);

    // empty cell
    cTest1 = new Cell(false, false, false); 
    // flag on empty cell with mine
    cTest2 = new Cell(true, true, false); 
    // flag on empty cell
    cTest3 = new Cell(false, true, false); 
    // hidden mine under cell
    cTest4 = new Cell(true, false, false); 
    // mine on gray cell
    cTest5 = new Cell(true, false, true); 
    // empty gray cell
    cTest6 = new Cell(false, false, true); 

    // neighbors of cell ct1
    this.ct1.neighbors = new ArrayList<>(Arrays.asList(this.cf2, this.cf5, this.ct6));
    // neighbors of cell cf2
    this.cf2.neighbors = new ArrayList<>(
        Arrays.asList(this.ct1, this.cf3, this.cf5, this.ct6, this.cf7));
    // neighbors of cell cf3
    this.cf3.neighbors = new ArrayList<>(
        Arrays.asList(this.cf2, this.ct4, this.ct6, this.cf7, this.cf8));
    // neighbors of cell ct4
    this.ct4.neighbors = new ArrayList<>(Arrays.asList(this.cf3, this.cf7, this.cf8));
    // neighbors of cell cf5
    this.cf5.neighbors = new ArrayList<>(
        Arrays.asList(this.ct1, this.cf2, this.ct6, this.cf9, this.cf10));
    // neighbors of cell ct6
    this.ct6.neighbors = new ArrayList<>(Arrays.asList(this.ct1, this.cf2, this.cf3, this.cf5,
        this.cf7, this.cf9, this.cf10, this.cf11));
    // neighbors of cell cf7
    this.cf7.neighbors = new ArrayList<>(Arrays.asList(this.cf2, this.cf3, this.ct4, this.ct6,
        this.cf8, this.cf10, this.cf11, this.cf12));
    // neighbors of cell cf8
    this.cf8.neighbors = new ArrayList<>(
        Arrays.asList(this.cf3, this.ct4, this.cf7, this.cf11, this.cf12));
    // neighbors of cell cf9
    this.cf9.neighbors = new ArrayList<>(
        Arrays.asList(this.cf5, this.ct6, this.cf10, this.cf13, this.cf14));
    // neighbors of cell cf10
    this.cf10.neighbors = new ArrayList<>(Arrays.asList(this.cf5, this.ct6, this.cf7, this.cf9,
        this.cf11, this.cf13, this.cf14, this.cf15));
    // neighbors of cell cf11
    this.cf11.neighbors = new ArrayList<>(Arrays.asList(this.ct6, this.cf7, this.cf8, this.cf10,
        this.cf12, this.cf14, this.cf15, this.cf16));
    // neighbors of cell cf12
    this.cf12.neighbors = new ArrayList<>(
        Arrays.asList(this.cf7, this.cf8, this.cf11, this.cf15, this.cf16));
    // neighbors of cell cf13
    this.cf13.neighbors = new ArrayList<>(Arrays.asList(this.cf9, this.cf10, this.cf14));
    // neighbors of cell cf14
    this.cf14.neighbors = new ArrayList<>(
        Arrays.asList(this.cf9, this.cf10, this.cf11, this.cf13, this.cf15));
    // neighbors of cell cf15
    this.cf15.neighbors = new ArrayList<>(
        Arrays.asList(this.cf10, this.cf11, this.cf12, this.cf14, this.cf16));
    // neighbors of cell cf16
    this.cf16.neighbors = new ArrayList<>(Arrays.asList(this.cf11, this.cf12, this.cf15));

    // example gird
    this.grid = new ArrayList<>(
        Arrays.asList(new ArrayList<>(Arrays.asList(this.ct1, this.cf2, this.cf3, this.ct4)),
            new ArrayList<>(Arrays.asList(this.cf5, this.ct6, this.cf7, this.cf8)),
            new ArrayList<>(Arrays.asList(this.cf9, this.cf10, this.cf11, this.cf12)),
            new ArrayList<>(Arrays.asList(this.cf13, this.cf14, this.cf15, this.cf16))));

    // empty cell
    this.plainCell = new OverlayImage(new RectangleImage(20, 20, OutlineMode.OUTLINE, Color.BLACK),
        new RectangleImage(20, 20, OutlineMode.SOLID, Color.WHITE));
  }

  // tests the makeGrid method
  void testMakeGrid(Tester t) {
    this.init();
    t.checkExpect(this.testGrid1.grid, this.grid);
  }

  // tests the addMines method
  void testAddMines(Tester t) {
    this.init();
    t.checkExpect(this.testGrid1.grid.get(0).get(0).neighbors, this.ct1.neighbors);
  }

  // tests the insideGrid method
  void testInsideGrid(Tester t) {
    this.init();
    t.checkExpect(this.testGrid1.insideGrid(25, 25), false);
    t.checkExpect(this.testGrid1.insideGrid(2, 3), true);
  }

  // tests the getCellInGrid method
  void testGetCellInGrid(Tester t) {
    this.init();
    t.checkExpect(this.testGrid1.getCellInGrid(1, 1), this.ct6);
    t.checkExpect(this.testGrid1.getCellInGrid(3, 3), this.cf16);
    t.checkExpect(this.testGrid1.getCellInGrid(2, 1), this.cf10);
    t.checkExpect(this.testGrid1.getCellInGrid(1, 3), this.ct6);
  }

  // tests the initMines method
  void testInitMines(Tester t) {
    this.init();
    t.checkExpect(this.testGrid1.grid, this.grid);
  }

  // tests the countMines method
  void testCountMines(Tester t) {
    this.init();
    t.checkExpect(this.cf16.countMines(), 0);
    t.checkExpect(this.cf9.countMines(), 1);
    t.checkExpect(this.cf2.countMines(), 2);
    t.checkExpect(this.cf14.countMines(), 0);
    t.checkExpect(this.ct1.countMines(), 1);
  }

  // tests the drawCell method
  void testDrawCell(Tester t) {
    this.init();
    t.checkExpect(cTest1.drawCell(),
        new OverlayImage(new RectangleImage(20, 20, OutlineMode.OUTLINE, Color.BLACK),
            new RectangleImage(20, 20, OutlineMode.SOLID, new Color(51, 153, 255))));
    t.checkExpect(cTest6.drawCell(),
        new OverlayImage(new RectangleImage(20, 20, OutlineMode.OUTLINE, Color.BLACK),
            new RectangleImage(20, 20, OutlineMode.SOLID, Color.GRAY)));
    this.cf9.shown = true;
    t.checkExpect(cf9.drawCell(),
        new OverlayImage(new RectangleImage(20, 20, OutlineMode.OUTLINE, Color.BLACK),
            new OverlayImage(new TextImage("1", 15, Color.BLUE),
                new RectangleImage(20, 20, OutlineMode.SOLID, Color.GRAY))));
    t.checkExpect(cTest5.drawCell(),
        new OverlayImage(new RectangleImage(20, 20, OutlineMode.OUTLINE, Color.BLACK),
            new OverlayImage(new CircleImage(8, OutlineMode.SOLID, Color.RED),
                new RectangleImage(20, 20, OutlineMode.SOLID, Color.GRAY))));
    t.checkExpect(cTest2.drawCell(),
        new OverlayImage(new RectangleImage(20, 20, OutlineMode.OUTLINE, Color.BLACK),
            new OverlayImage(new EquilateralTriangleImage(15, OutlineMode.SOLID, Color.ORANGE),
                new RectangleImage(20, 20, OutlineMode.SOLID, new Color(51, 153, 255)))));
    t.checkExpect(cTest3.drawCell(),
        new OverlayImage(new RectangleImage(20, 20, OutlineMode.OUTLINE, Color.BLACK),
            new OverlayImage(new EquilateralTriangleImage(15, OutlineMode.SOLID, Color.ORANGE),
                new RectangleImage(20, 20, OutlineMode.SOLID, new Color(51, 153, 255)))));
    this.cf2.shown = true;
    t.checkExpect(cf2.drawCell(),
        new OverlayImage(new RectangleImage(20, 20, OutlineMode.OUTLINE, Color.BLACK),
            new OverlayImage(new TextImage("2", 15, Color.GREEN),
                new RectangleImage(20, 20, OutlineMode.SOLID, Color.GRAY))));
  }
  
  // bigBang to create WorldScene
  void testBigBang(Tester t) {
    this.init();
    int worldWidth = 15 * 20;
    int worldHeight = 15 * 20;
    double tickRate = 1.0 / 60.0;
    this.assignmentGrid.bigBang(worldWidth, worldHeight, tickRate);
  }
}
