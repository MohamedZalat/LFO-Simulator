package lfo.experiments;

public class Simulator {
 public static void main(String[] args) throws Exception{
	 
	 long ttt = System.currentTimeMillis();
	 
//	GenerateDiscreteTraces.main(args);
	
	 String [] name = new String [1];
	 for(int cycles = 0; cycles<1;cycles++){
		 for(int i = 0;i<1;i++){
			 if(i==0){name[0]="NN";}
			 else if(i==1) {name[0] ="BN";}
			 else if(i==2) {name[0] ="BNk2";}
			 else if(i==3) {name[0] ="NNk2";}
			 else if(i==4) {name[0] ="IOHMM";}
			 else {name[0] ="DBN";}
			 
			 name[0] = "BN";

			 //GenerateDiscreteTracesFromLFOAgent.main(name);
			 AutomaticPerformanceEvaluatorLFO.main(name);

		 }
	 }
	 ttt = (System.currentTimeMillis() - ttt);
	 System.out.println("time:"+ ttt);
 }
}
