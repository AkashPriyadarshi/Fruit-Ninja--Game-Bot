import java.io.BufferedWriter;
import java.io.FileWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

public class calibrate {
	public static void main(String[] args) {
		int [] computationalArr = new int[]{1,2,3,4,5,6,7,8,9,0};
		int k=10000000, j=10;
		while(k>0) {
			j=10;
			while(j>0) {
			for(int index=0;index<computationalArr.length;index++)computationalArr[index]+=1;
			j--;
			}
			k--;
		}
		try {
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("Calibrate.txt"));		
			bufferedWriter.write(""+(getCpuTime()/10000000));
			bufferedWriter.flush();
			bufferedWriter.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
		//System.out.println(getCpuTime()/10000000);
	}	
	public static long getCpuTime( ) {
		ThreadMXBean bean = ManagementFactory.getThreadMXBean( );
		return bean.isCurrentThreadCpuTimeSupported( ) ?
				bean.getCurrentThreadCpuTime( ) : 0L;
	}	

}
