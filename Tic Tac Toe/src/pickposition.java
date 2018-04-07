package test1;

public class pickposition {
	public static void main(String[] args) {
		int j=0;
		Seed test=Seed.CROSS;
		int [] pickposition={0,0};
		Seed [][] stock={{Seed.CROSS, Seed.CROSS, Seed.CROSS},{Seed.CROSS, Seed.CROSS, Seed.NOUGHT},{Seed.NOUGHT, Seed.NOUGHT,Seed.NOUGHT},{Seed.NOUGHT, Seed.EMPTY, Seed.EMPTY}};
		loop: {
		if(test==Seed.CROSS){
			 while(j<3){
				 if(stock[0][j]==Seed.CROSS){
					 pickposition[0]=3;
					 pickposition[1]=j;
					 stock[0][j]=Seed.EMPTY;
					break loop; 
				 }
				 else if(stock[1][j]==Seed.CROSS){
					 pickposition[0]=4;
					 pickposition[1]=j;
					 stock[1][j]=Seed.EMPTY;
					break loop; 
				 }
				 else j++;
		 }
	}
		else if(test==Seed.NOUGHT){
			while(j<3){
				 if(stock[2][j]==Seed.NOUGHT){
					 pickposition[0]=5;
					 pickposition[1]=j;
					 stock[2][j]=Seed.EMPTY;
					break loop; 
				 }
				 else if(stock[3][j]==Seed.NOUGHT){
					 pickposition[0]=6;
					 pickposition[1]=j;
					 stock[3][j]=Seed.EMPTY;
					break loop; 
				 }
				 else j++;
		 }
		}
		}
		System.out.println(pickposition);
	}
}
