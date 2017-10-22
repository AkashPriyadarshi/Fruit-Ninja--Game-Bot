import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Random;

public class CreateInput {

	private static String inputfileName="input";
	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		Random rand = new Random();
		for(int i = 102; i<200; i++)
		writeFile(inputfileName+i+".txt",createMatrix(15, rand));
		
	}
	
	private static int[][] createMatrix(int height, Random rand) {
		int[][] matrix = new int[height][height];
		for(int i=0;i<height;i++)
			for(int j=0;j<height;j++) {
				matrix[i][j]= 1+ rand.nextInt(9);
			}
		return matrix;
	}
	
	private static void writeFile(String fileName, int[][] matrix) throws Exception{
		BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
		StringBuilder strB = new StringBuilder();
		bw.write("15");bw.newLine();
		bw.write("9");bw.newLine();
		bw.write("300");bw.newLine();
		for(int i=0;i<matrix.length;i++) {
			for(int j=0;j<matrix.length;j++) {
				strB.append(""+matrix[i][j]);
			}
			bw.write(strB.toString());
			bw.newLine();
			strB.setLength(0);
		}
		bw.flush();
		bw.close();				
	}

}
