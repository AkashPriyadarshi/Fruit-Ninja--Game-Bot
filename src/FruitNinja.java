import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


public class FruitNinja {
	char[] gameBoard =  null;
	Node resultNode =  null;
	private static final String inputfileName="input.txt";
	private static final String calibrate="Calibrate.txt";
	private final String outputfileName="output.txt";
	private int gameBoardSize, fruitTypes, actualFruitType;
	private float computationaTime=200, timeLeft=0;

	private static int nodeExpanded =0;
	Comparator<int[]> comAlpha;

	class Node{
		char[] gameBoard =  null;
		int row, column;
		Node(char[] gameBoard, int row, int column) {
			this.gameBoard= gameBoard;
			this.row = row;
			this.column = column;
		}
	}

	public FruitNinja(String fileName) {
		// TODO Auto-generated constructor stub
		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			gameBoardSize= Integer.parseInt(br.readLine()); 
			fruitTypes   = Integer.parseInt(br.readLine());
			timeLeft     = Float.parseFloat(br.readLine());
			gameBoard = new char[gameBoardSize*gameBoardSize];
			int[] tempArr= new int[11];
			String gameBoardRow=null;
			for(int i=0;i<gameBoardSize;i++) {
				gameBoardRow = br.readLine().trim();
				for(int j=0;j<gameBoardSize;j++) {
					gameBoard[i*gameBoardSize+j]=gameBoardRow.charAt(j);
					tempArr[Character.getNumericValue(gameBoard[i*gameBoardSize+j])+1]=1;
				}
			}
			for(int fruit:tempArr)if(fruit==1)actualFruitType++;
			br.close();

			br = new BufferedReader(new FileReader(calibrate));
			computationaTime =Integer.parseInt(br.readLine()); 
			br.close();

		}catch (Exception e) {			
			e.printStackTrace();
		}
		comAlpha= new Comparator<int[]>(){
			@Override
			public int compare(int[] cluster1, int[] cluster2)
			{				
				if(cluster2[1]==cluster1[1])return cluster2[0]-cluster1[0];
				else return cluster2[1]-cluster1[1];
			}        
		};
	}

/*	public static void main(String[] args) {
		int fileIndex = Integer.parseInt(args[0]);		
		Date date = new Date();
		System.out.println("processing input file: input"+fileIndex+".txt");
		System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(date));
		homework hw = new homework("input"+fileIndex+".txt");	
		int depth = hw.getDepth(hw.actualFruitType, 
				hw.getCLusters(hw.gameBoard,new ArrayList<int[]>()).size(), 
				hw.computationaTime, 
				hw.gameBoardSize*hw.gameBoardSize,
				hw.timeLeft);
		hw.statGame(depth,"output/output_"+fileIndex+"_"+depth+".txt");
		String logLine=hw.getAccuracy(hw.actualFruitType,
				hw.getCLusters(hw.gameBoard,new ArrayList<int[]>()).size(), 
				hw.computationaTime, 
				hw.gameBoardSize*hw.gameBoardSize,
				depth); 
		logLine = (fileIndex +" "+ logLine);
		runTimeLogger(date, depth,hw.gameBoardSize, logLine);	
		System.out.println(logLine);
		System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()));
	}

	private static void runTimeLogger(Date date, int depth,int gameBoardSize, String logLine){
		try {
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("Outputlog.txt",true));
			//String logLine =noOfCluster+" "+ depth+"  "+(new Date().getTime()-date.getTime());
			//String logLine =nodeExpanded+","+gameBoardSize+","+(new Date().getTime()-date.getTime());
			bufferedWriter.write(logLine);
			bufferedWriter.newLine();
			bufferedWriter.flush();
			bufferedWriter.close();		
		}catch (Exception e) {
			e.printStackTrace();
		}
	}*/

	public static void main(String[] args) {
		//System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()));
		FruitNinja fruitNinja = new FruitNinja(inputfileName);
		int depth = fruitNinja.getDepth(fruitNinja.actualFruitType, 
				fruitNinja.getCLusters(fruitNinja.gameBoard,new ArrayList<int[]>()).size(), 
				fruitNinja.computationaTime, 
				fruitNinja.gameBoardSize*fruitNinja.gameBoardSize,
				fruitNinja.timeLeft);
		fruitNinja.statGame(depth,fruitNinja.outputfileName);
		/*hw.getAccuracy(hw.actualFruitType,
				hw.getCLusters(hw.gameBoard,new ArrayList<int[]>()).size(), 
				hw.computationaTime, 
				hw.gameBoardSize*hw.gameBoardSize,
				depth); */
		//System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()));
	}

	private void statGame( int depth, String outputfileName) {
		try {
			int outcome = alphaPuning(gameBoard,Integer.MIN_VALUE,Integer.MAX_VALUE,0,depth,true);
			writeFile(resultNode.gameBoard,resultNode.row,resultNode.column,outputfileName);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	private int alphaPuning(char[] currentGameBoard,int alpha, int beta, int runningScore, int depth,boolean firstCall){
		if(depth==0)return runningScore;	
		List<int[]> cluster = new ArrayList<int[]>();
		getCLusters(currentGameBoard,cluster);
		if(cluster.size()==0)return runningScore;	
		Collections.sort(cluster, comAlpha);
		char[] currentMove = null;
		for(int i =0, currentAlpha=Integer.MIN_VALUE, move[]=null; i <cluster.size();i++) {
			move =  cluster.get(i);
			currentMove=currentGameBoard.clone();
			claimFruits(currentMove,move[0]/gameBoardSize,move[0]%gameBoardSize,currentMove[move[0]]);
			currentAlpha=betaPuning(applyGravity(currentMove),alpha,beta,runningScore+(move[1]*move[1]),depth-1);
			if(currentAlpha>alpha) {
				alpha=currentAlpha;
				resultNode=firstCall?new Node(currentMove,move[0]/gameBoardSize,move[0]%gameBoardSize):resultNode;
			}
			if(alpha>= beta)return beta;
		}
		return alpha;
	}

	private int betaPuning(char[] currentGameBoard,int alpha, int beta, int runningScore, int depth) {
		if(depth==0)return runningScore;	
		List<int[]> cluster = new ArrayList<int[]>();
		getCLusters(currentGameBoard,cluster);
		if(cluster.size()==0)return runningScore;		
		Collections.sort(cluster, comAlpha);
		char[] currentMove = null;

		for(int i =0, move[]=null; i <cluster.size();i++) {
			move =  cluster.get(i);
			currentMove=currentGameBoard.clone();
			claimFruits(currentMove,move[0]/gameBoardSize,move[0]%gameBoardSize,currentMove[move[0]]);
			beta = Math.min(beta, alphaPuning(applyGravity(currentMove),alpha,beta,runningScore-(move[1]*move[1]),depth-1,false));
			if(alpha>=beta)return alpha;
		}
		return beta;
	}




	private char[] applyGravity(char[] gameBoard) {
		for(int j=0;j<gameBoardSize;j++) {
			for(int i=gameBoardSize-1, index=0;0<=i;i--) {
				if(gameBoard[i*gameBoardSize + j]=='*')index++;
				else { 
					gameBoard[(i+index)*gameBoardSize + j]= gameBoard[i*gameBoardSize + j];
					if(index!=0)gameBoard[i*gameBoardSize + j]='*';
				}
			}
		}
		return gameBoard;		
	}

	private void claimFruits(char[] tempGameBoard, int i, int j,char value) {
		int index=i*gameBoardSize+j;
		tempGameBoard[index]='*';		
		if(i>0 && tempGameBoard[index-gameBoardSize] == value)claimFruits(tempGameBoard,i-1,j,value);
		if(j>0 && tempGameBoard[index-1] == value)claimFruits(tempGameBoard,i,j-1,value);
		if((i<gameBoardSize-1) && tempGameBoard[index+gameBoardSize] == value)claimFruits(tempGameBoard,i+1,j,value);
		if((j<gameBoardSize-1)  && tempGameBoard[index+1] == value)claimFruits(tempGameBoard,i,j+1,value);		
		return ;

	}

	private int getClusterSize(char[] tempGameBoard, int i, int j, char value,boolean[] visited) {	
		int sum=1, index=i*gameBoardSize+j;
		visited[index]=true;
		if(i>0 && tempGameBoard[index-gameBoardSize] == value &&  visited[ index-gameBoardSize] == false)
			sum+=getClusterSize(tempGameBoard,i-1,j,value,visited);
		if(j>0 && tempGameBoard[index-1] == value &&  visited[index-1] == false)
			sum+=getClusterSize(tempGameBoard,i,j-1,value,visited);
		if((i<gameBoardSize-1) && tempGameBoard[index+gameBoardSize] == value &&  visited[index+gameBoardSize] == false)
			sum+=getClusterSize(tempGameBoard,i+1,j,value,visited);
		if((j<gameBoardSize-1)  && tempGameBoard[index+1] == value &&  visited[index+1] == false)
			sum+=getClusterSize(tempGameBoard,i,j+1,value,visited);		
		return sum;
	}

	private List<int[]> getCLusters(char[] currGameBoard,List<int[]> clusterList) {
		boolean[] visited = new boolean[gameBoardSize*gameBoardSize];		
		for(int i=0,clusterSize=0,index=0;i<gameBoardSize;i++) {
			for(int j=0;j<gameBoardSize;j++,index++) {				
				if(visited[index] ==false && currGameBoard[index]!='*') {
					clusterSize=getClusterSize(currGameBoard,i,j,currGameBoard[index],visited);
					clusterList.add(new int[] {index,clusterSize});
				}
			}
		}
		return clusterList;
	}

	private void writeFile(char[] gameBoardOutput, int row , int column, String outputfileName) throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(outputfileName));

		bw.write((char)('A'+column)+""+(row+1));
		bw.newLine();

		StringBuilder strB = new StringBuilder();
		for(int i=0;i<gameBoardSize;i++) {
			for(int j=0;j<gameBoardSize;j++)
				strB.append(gameBoardOutput[i*gameBoardSize+j]);
			bw.write(strB.toString());
			bw.newLine();	
			strB.setLength(0);
		}
		bw.flush();
		bw.close();
	}

	private long getCpuTime( ) {
		ThreadMXBean bean = ManagementFactory.getThreadMXBean( );
		return bean.isCurrentThreadCpuTimeSupported( ) ?
				bean.getCurrentThreadCpuTime( ) : 0L;
	}	

	private String getAccuracy(int fruitTypes, int clusters, float computationalTime, int noOfOperations, int depth) {
		double timeCalcuated = getTime(fruitTypes, clusters, computationalTime, noOfOperations, depth);
		System.out.println("ESTIMATED TIME:"+timeCalcuated+"  Actual TIME:"+(getCpuTime()/1000000));
		return ("Depth :"+depth+" Accuracy in milisec:   "+(int)(timeCalcuated-(getCpuTime()/1000000)));
	}

	private double getTime(int fruitTypes, int clusters, float computationalTime, int noOfOperations, int depth) {
		double timeCalcuated = Math.pow((fruitTypes*1.4+(double)clusters/(fruitTypes*fruitTypes)), depth)*computationalTime*noOfOperations;
		return timeCalcuated/1000000;		
	}

	private int getDepth(int fruitTypes, int clusters, float computationalTime, int noOfOperations,float avaialableTime){
		float maxTimeThisMove=(avaialableTime*1000)/2;
		if(maxTimeThisMove > (getTime(fruitTypes, clusters, computationalTime, noOfOperations, 4)+500))return 4;
		else if(maxTimeThisMove > (getTime(fruitTypes, clusters, computationalTime, noOfOperations, 3)+500))return 3;
		else return 2;			
	}
}
