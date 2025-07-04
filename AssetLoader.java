package view;

import java.util.ArrayList;
import java.awt.Point;
import java.io.*;

public class AssetLoader {
	
	
	
	public static ArrayList<Point> loadTrack(int startCol, int startRow){
		
		int[][] DIRECTIONS = {{0, -1}, {-1, 0}, {0, 1}, {1, 0}};
		int rows = 27, cols = 29;
		ArrayList<Point> track = new ArrayList<>();
		int[][] board = new int[rows][cols];
		boolean[][] visited = new boolean[rows][cols];

		try {
			BufferedReader reader = new BufferedReader(new FileReader("board.txt"));
			String line;
            for(int r = 0; r< rows; r++) {
            	line = reader.readLine();
                for (int c = 0; c < cols && c < line.length(); c++) {
                    board[r][c] = line.charAt(c) == '1' ? 1 : 0;
                } 
            }
            int col = startCol;
            int row = startRow;
            track.add(new Point(startCol, startRow));
            visited[row][col] = true;
            while(true){
            	boolean moved = false;
            	for (int[] dir : DIRECTIONS) {
                    int newRow = row + dir[0];
                    int newCol = col + dir[1];

                    if (newRow >= 0 && newRow < rows && newCol >= 0 && newCol < cols
                        && board[newRow][newCol] == 1
                        && !visited[newRow][newCol]) {
                    	row = newRow;
                        col = newCol;
                        visited[row][col] = true;
                        track.add(new Point (col, row));
                        moved = true;
                        break;
                    }
                    

                } 
            	if(!moved || track.size()>1 && (startCol == col && startRow == row))
                	break;
           }
           
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e){
			e.printStackTrace();
		}
		return track;
	}
}	
