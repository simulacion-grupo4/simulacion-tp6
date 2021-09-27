
public class TP6 
{
	static final int NC = 4;					//Cantidad de Cabinas
	static final int NQ = 20;					//N�mero de veh�culos para hacer quiebre
	static final long COC = 360000;				//Costo Operativo de Cabina mensual
	static final long HV = 5000000;				//High Value
	static final long TF = 2592000;				//Tiempo de simulacion de un mes
	static final int TAQ=2;						//Tiempo de atencion en quiebre
	static long TPLL;							//Tiempo de Llegada
	static long[] TPS= new long [NC];				//Proximo tiempo de salida para cada cabina
	static int[] NV= new int [NC];				//Numero de vehiculos en cada cabina
	static int CLL;								//Vehiculos que llegaron en la simulaci�n
	static int NT;								//Veh�culo en el peaje
	static long T;								//Instante de tiempo
	static long IA;								//Intervalo entre arribos
	static long TA;								//Tiempo de Atencion	
	static long TFQ; 							//Tiempo final de quiebre
	static long[] STO= new long [NC];				//Suma Tiempo de Ocio por cabina
	static long[] ITO= new long [NC];				//Inicio Tiempo de Ocio
	static long STA;								//Suma tiempo de atenci�n
	static long STLL;							//Suma tiempo de Llegada
	static long DQ;								//Duracion de Quiebre
	static long PMQ;								//Perdida en el quiebre
	static int VQ;								//Cantidad de vehiculos en quiebre
	static long STS;								//Suma tiempo de salidas
	static float[] PTO= new float [NC];				//Porcentaje tiempo ocioso
	static long COP;								//Costo operativo mensual para NC cabinas
	static float PTE;								//Promedio Tiempo de Espera
	
	
	public static  void main(String args[]) 
	{
		int i=0;
		int y=0;
		int x=0;
		int p=0;
		long TPSm=0;					//Valor del TPS menor
		cabina CabMenor=new cabina();
		TP6 tp6=new TP6(); 
		tp6.CI();
		do
		{
			do
			{			
				CabMenor=tp6.MenorTPS();
				x=CabMenor.getCabina();
				TPSm=CabMenor.getTPS();
				if(TPLL<= TPSm)
				{				
					//Rama de Llegada
					T=TPLL;
					CLL=CLL+1;
					NT=NT+1;
					STLL=STLL+T;
					IA=tp6.CalculoIA(T);
					TPLL=T+IA;
					y=tp6.MenorFila();
					NV[y]=NV[y]+1;
					
					if(NV[y]==1)
					{					
						//Es el �nico en la fila
						if(T>TFQ)
						{
							//No esta en quiebre
							TA=tp6.CalculoTA();
							TPS[y]=T+TA;
							STO[y]=STO[y]+(T-ITO[y]);
							STA=STA+TA;
													
						}
						else
						{
							//Est� en quiebre
							TPS[y]=T+TAQ;
							STA=STA+TAQ;
						}
					}
					else
					{
						//No es el unico en la fila
						if(NT>NQ && T>TFQ)
						{
							//Empezamos quiebre
							DQ=tp6.CalculoDQ();
							TFQ=T+DQ;
							for(i=0;i<NC;i++)
							{
								TPS[i]=T+TAQ;
								STA=STA+TAQ;
							}
							
						}
						else
						{
							//es un auto mas que se suma a la fila
							//STLL=STLL+T;
						}
						
					}									
				}
				else
				{
					//Rama de salida
					T=TPSm;
					NV[x]=NV[x]-1;
					if(NV[x]>=1)
					{
						//Hay otro en la fila
						if(T<TFQ)
						{
							//Estoy en quiebre
							p=tp6.PrecioPorCategoria();
							PMQ=PMQ+p;
							VQ=VQ+1;
							TPS[x]=T+TAQ;
							STA=STA+TAQ;
						}
						else
						{
							//No estoy en quiebre
							TA=tp6.CalculoTA();
							TPS[x]=T+TA;
							STA=STA+TA;							
						}
					}
					else 
					{
						//No hay otro vehiculo en esta cabina
						ITO[x]=T;
						TPS[x]=HV;
					}
					NT=NT-1;
					STS=STS+T;
					
				}					
			}
			while(T<TF);
			TPLL=HV;
		}
		while(NT > 0);
		
		System.out.println("Cantidad de Cabinas: "+NC);
		System.out.println("Vehiculos en el Peaje para realizar Quiebre: "+NQ);
	
		for(i=0;i<NC;i++)
		{
			PTO[i]=(float)((float)STO[i]/T)*100;
			System.out.println("Porcentaje tiempo ocioso en cabina "+ i +": "+ PTO[i]+"%");

		}
		
		
		COP=COC*NC;
		PTE=(float)(STS-STLL-STA)/(float)(CLL-VQ);
		
		System.out.println("Cantidad de Cabinas: "+NC);
		System.out.println("Vehiculos en el Peaje para realizar Quiebre: "+NQ);
		System.out.println("Promedio tiempo de espera: "+PTE);
		System.out.println("Perdida Mensual por quiebre: "+PMQ);
		System.out.println("Costo operativo por cabina: "+COC);
		System.out.println("Costo operativo del peaje: "+COP);
		
		
	}
	
	public void CI()
	{
		int i;
		T=1;
		TPLL=0;
		CLL=0;
		NT=0;
		IA=0;
		TA=0;
		TFQ=-1;	
		STA=0;
		STLL=0;	
		DQ=0;
		VQ=0;
		STS=0;
		PMQ=0;
		COP=0;
		PTE=0;
		for(i =0;i<NC;i++)
		{
			TPS[i]=HV;
			NV[i]=0;
			STO[i]=0;
			ITO[i]=0;
			PTO[i]=0;
		}
		
	}
	public cabina MenorTPS()
	{
		int i=0;		
		cabina cab=new cabina();
		cab.setCabina(0);
		cab.setTPS(HV);
		for(i=0;i<NC;i++)
		{
			if(TPS[i]<cab.getTPS())
			{
				cab.setCabina(i);
				cab.setTPS(TPS[i]);
			}
		}
		return cab;
	}
	public int CalculoIA(long t)
	{
		double r=0;
		double ia=0;
		long resto=0;
		resto=t%86400;	//resto segundos de un dia
		r=((double)(Math.random()*1)+0);
		if(resto<=7200 || resto >75600)	//entre las 21hs y las 2am
		{
			r=18.9358*r;
			ia=-18.9358*Math.log10(r);			
		}
		else if(resto >7200 && resto <= 25200)	//entre las 2 y las 7am
		{
			r=15.3846*r;
			ia=-15.3846*Math.log10(r);			
		}
		else			//entre las 7am y las 21hs
		{			
			r=6.65336*r;
			ia=-6.65336*Math.log10(r);				
			
		}
		
		if(ia<0)
		{
			ia=ia*(-1);
		}
			
		return (int)ia;
		

	}
	public int CalculoTA()
	{
		double r=0;
		double ta=0;
		r=Math.random();
		ta=4.10174/(Math.pow(r,0.4799));
		return (int)ta;
	}	
	public int CalculoDQ()
	{
		double r=0;
		double dq=0;
		r=Math.random();
		dq=42.7995/(Math.pow(r,0.3227));
		return (int)dq;
	}		
	public int PrecioPorCategoria()
	{
		int r=0;
		int p=0;
		r=(int)Math.random()*100;
		if(r==1)
		{
			p=390;
		}
		else if (r<6)
		{
			p=255;
		}
		else if(r<15)
		{
			p=155;
		}
		else if(r<41)
		{
			p=310;
		}
		else
		{
			p=85;
		}
		return p;
	}			
	public int MenorFila()
	{
		int i=0;
		int fila=0;
		int CantidadFila=NV[0];
		for(i=0;i<NC;i++)
		{
			if(NV[i]<CantidadFila)
			{
				CantidadFila=NV[i];
				fila=i;
			}
		}
		return fila;
	}	

}


