import java.io.IOException;


public class Batch {
	
	private static final String userHome = System.getProperty("user.home");
	private static final String fs = System.getProperty("file.separator");
	  
	public static void main(String[] args) throws IOException{
	  
		Runtime.getRuntime().exec(userHome + fs + ".jdroidemul" + fs + 
				"tools"+ fs  + "emulator @Slim-Emulator");
	  
	}
}
