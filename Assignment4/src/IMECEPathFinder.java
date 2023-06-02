import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.awt.*;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IMECEPathFinder{
	  public int[][] grid;
	  public int height, width;
	  public int maxFlyingHeight;
	  public double fuelCostPerUnit, climbingCostPerUnit;
	  public DiGraph graph;
	  public double minHeight,maxHeight;

	  public IMECEPathFinder(String filename, int rows, int cols, int maxFlyingHeight, double fuelCostPerUnit, double climbingCostPerUnit){
		  grid = new int[rows][cols];
		  this.height = rows;
		  this.width = cols;
		  graph = new DiGraph(height*width);
		  this.maxFlyingHeight = maxFlyingHeight;
		  this.fuelCostPerUnit = fuelCostPerUnit;
		  this.climbingCostPerUnit = climbingCostPerUnit;

			// TODO: fill the grid variable using data from filename

		  BufferedReader reader;
		  int row=0;
		  int col=0;
		  try {
			  reader = new BufferedReader(new FileReader(filename));
			  String line = reader.readLine();
			  minHeight=Integer.MAX_VALUE;
			  maxHeight=0;


			  while (line!=null){
				  Pattern pattern = Pattern.compile("\\d+");
				  Matcher matcher = pattern.matcher(line);
				  while (matcher.find()) {
					  int value = Integer.parseInt(matcher.group());
					  grid[row][col]= value;
					  if (grid[row][col]<=minHeight) minHeight=grid[row][col];
					  if (grid[row][col]>=maxHeight) maxHeight=grid[row][col];
					  col++;
				  }
				  try {
					  line = reader.readLine();
				  }
				  catch (Exception e){
					  line=null;
				  }
				  row++;
				  col=0;
			  }
			  reader.close();
		  }
		  catch (IOException e) {
			  e.printStackTrace();
		  }
	  }


	  /**
	   * Draws the grid using the given Graphics object.
	   * Colors should be grayscale values 0-255, scaled based on min/max elevation values in the grid
	   */
	  public void drawGrayscaleMap(Graphics g){

		  // TODO: draw the grid, delete the sample drawing with random color values given below
		  for (int i = 0; i < grid.length; i++)
		  {
			  for (int j = 0; j < grid[0].length; j++) {
				  double diff = ((maxHeight-minHeight)/255);
				  double grayscaleValue =(int) ((grid[i][j]-minHeight)/diff);
				  int value = (int) grayscaleValue;
				  g.setColor(new Color(value,value,value));
				  g.fillRect(j,i,width,height);
			  }
		  }
	  }

	/**
	 * Get the most cost-efficient path from the source Point start to the destination Point end
	 * using Dijkstra's algorithm on pixels.
	 * @return the List of Points on the most cost-efficient path from start to end
	 */
	public List<Point> getMostEfficientPath(Point start, Point end) {

		List<Point> path = new ArrayList<>();

		// TODO: Your code goes here
		// TODO: Implement the Mission 0 algorithm here


		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {

				int curr = i * width + j;
				for (int incX = -1; incX <= 1; incX++) {
					for (int incY = -1; incY <= 1; incY++) {
						if (incX == 0 && incY == 0) continue;

						int nextR = i + incX;
						int nextC = j + incY;

						if (nextR >= 0 && nextR < height && nextC >= 0 && nextC < width) {
							int neighbor = nextR * width + nextC;
							int heightDiff;
							if (grid[nextR][nextC]-grid[i][j]<=0){
								heightDiff=0;
							}
							else{
								heightDiff=grid[nextR][nextC]-grid[i][j];
							}
							double weight = Math.sqrt((incX*incX)+(incY*incY))*fuelCostPerUnit+heightDiff*climbingCostPerUnit;
							graph.addEdge(curr, neighbor,weight);
						}
					}
				}
			}
		}


		List<Integer> path1 = graph.dijkstra(start.y*width+ start.x,end.y*width+ end.x);

		for (int i = 0; i < path1.size(); i++) {
			int y = path1.get(i)/width;
			int x = path1.get(i)%width;
			Point temp = new Point(x,y);
			path.add(temp);
		}



		return path;
	}

	/**
	 * Calculate the most cost-efficient path from source to destination.
	 * @return the total cost of this most cost-efficient path when traveling from source to destination
	 */
	public double getMostEfficientPathCost(List<Point> path){
		double totalCost = 0.0;

		// TODO: Your code goes here, use the output from the getMostEfficientPath() method
		for (int i = 0; i < path.size() - 1; i++) {
			int currentVertex = path.get(i).y*width+path.get(i).x;
			int nextVertex = path.get(i+1).y*width+path.get(i+1).x;

			// Find the edge with the given vertices
			for (Edge edge : graph.adjacencyList.get(currentVertex)) {
				if (edge.end == nextVertex) {
					totalCost += edge.weight;
					break;
				}
			}
		}
		return totalCost;
	}


	/**
	 * Draw the most cost-efficient path on top of the grayscale map from source to destination.
	 */
	public void drawMostEfficientPath(Graphics g, List<Point> path){
		// TODO: Your code goes here, use the output from the getMostEfficientPath() method
		g.setColor(new Color(0, 204, 0));
		for (int i = 0; i < path.size(); i++)
		{
			g.drawLine(path.get(i).x,path.get(i).y,path.get(i).x,path.get(i).y);
		}

	}

	/**
	 * Find an escape path from source towards East such that it has the lowest elevation change.
	 * Choose a forward step out of 3 possible forward locations, using greedy method described in the assignment instructions.
	 * @return the list of Points on the path
	 */
	public List<Point> getLowestElevationEscapePath(Point start){
		List<Point> pathPointsList = new ArrayList<>();

		// TODO: Your code goes here
		// TODO: Implement the Mission 1 greedy approach here

		Point temp;
		int rows = height;
		int cols = width;

		int currentRow = start.y;
		int currentCol = start.x;
		pathPointsList.add(start);

		while (currentCol < cols - 1) {
			// if exist diff's in the directions
			int east,northeast,southeast;

			if (currentCol + 1 < cols) {
				east = Math.abs(grid[currentRow][currentCol + 1] - grid[currentRow][currentCol]);
			}
			else {
				east = Integer.MAX_VALUE;
			}
			if (currentRow - 1 >= 0 && currentCol + 1 < cols) {
				northeast = Math.abs(grid[currentRow - 1][currentCol + 1] - grid[currentRow][currentCol]);
			}
			else {
				northeast = Integer.MAX_VALUE;
			}

			if (currentRow + 1 < rows && currentCol + 1 < cols) {
				southeast = Math.abs(grid[currentRow + 1][currentCol + 1] - grid[currentRow][currentCol]);
			}
			else {
				southeast = Integer.MAX_VALUE;
			}

			int minTemp=Math.min(east, northeast);
			int minDiff = Math.min(minTemp, southeast);

			if (minDiff == east) {
				currentCol++;
			}
			else if (minDiff == northeast) {
				currentRow--;
				currentCol++;
			}
			else if (minDiff == southeast) {
				currentRow++;
				currentCol++;
			}

			temp=new Point(currentCol,currentRow);
			pathPointsList.add(temp);
		}


		return pathPointsList;
	}


	/**
	 * Calculate the escape path from source towards East such that it has the lowest elevation change.
	 * @return the total change in elevation for the entire path
	 */
	public int getLowestElevationEscapePathCost(List<Point> pathPointsList){
		int totalChange = 0;

		// TODO: Your code goes here, use the output from the getLowestElevationEscapePath() method
		for (int i = 0; i < pathPointsList.size()-1; i++) {
			int j=i+1;
			totalChange+=Math.abs(grid[pathPointsList.get(j).y][pathPointsList.get(j).x]-grid[pathPointsList.get(i).y][pathPointsList.get(i).x]);
		}



		return totalChange;
	}


	/**
	 * Draw the escape path from source towards East on top of the grayscale map such that it has the lowest elevation change.
	 */
	public void drawLowestElevationEscapePath(Graphics g, List<Point> pathPointsList){
		// TODO: Your code goes here, use the output from the getLowestElevationEscapePath() method
		g.setColor(new Color(255, 255, 0));
		for (int i = 0; i < pathPointsList.size(); i++)
		{
			g.drawLine(pathPointsList.get(i).x,pathPointsList.get(i).y,pathPointsList.get(i).x,pathPointsList.get(i).y);
		}


	}


}
