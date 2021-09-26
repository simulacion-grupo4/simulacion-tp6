
public class TP6 
{
	static final int NC = 2;					//Cantidad de Cabinas
	static final int NQ = 10;					//Número de vehículos para hacer quiebre
	static final int COC = 360000;				//Costo Operativo de Cabina mensual
	static final int HV = 5000000;				//High Value
	static final int TF = 4000000;				//High Value
	static final int TAQ=2;						//Tiempo de atencion en quiebre
	static int TPLL;							//Tiempo de Llegada
	static int[] TPS= new int [NC];				//Proximo tiempo de salida para cada cabina
	static int[] NV= new int [NC];				//Numero de vehiculos en cada cabina
	static int CLL;								//Vehiculos que llegaron en la simulación
	static int NT;								//Vehículo en el peaje
	static int T;								//Instante de tiempo
	static int IA;								//Intervalo entre arribos
	static int TA;								//Tiempo de Atencion	
	static int TFQ; 							//Tiempo final de quiebre
	static int[] STO= new int [NC];				//Suma Tiempo de Ocio por cabina
	static int[] ITO= new int [NC];				//Inicio Tiempo de Ocio
	static int STA;								//Suma tiempo de atención
	static int STLL;							//Suma tiempo de Llegada
	static int DQ;								//Duracion de Quiebre
	static int PMQ;								//Perdida en el quiebre
	static int VQ;								//Cantidad de vehiculos en quiebre
	static int STS;								//Suma tiempo de salidas
	static float[] PTO= new float [NC];				//Porcentaje tiempo ocioso
	static int COP;								//Costo operativo mensual para NC cabinas
	static int PTE;								//Promedio Tiempo de Espera
	
	
	public static  void main(String args[]) 
	{
		int i=0;
		int y=0;
		int x=0;
		int p=0;
		int TPSm=0;					//Valor del TPS menor
		cabina CabMenor=new cabina();
		TP6 tp6=new TP6(); 
		tp6.CI();			
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
				IA=tp6.CalculoIA();
				TPLL=T+IA;
				y=tp6.MenorFila();
				NV[y]=NV[y]+1;
				
				if(NV[y]==1)
				{					
					//Es el único en la fila
					if(T>TFQ)
					{
						//No esta en quiebre
						TA=tp6.CalculoTA();
						TPS[y]=T+TA;
						STO[y]=STO[y]+(T-ITO[y]);
						STA=STA+TA;
						STLL=STLL+T;						
					}
					else
					{
						//Está en quiebre
						TPS[y]=T+TAQ;
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
						}
						
					}
					else
					{
						//es un auto mas que se suma a la fila
						STLL=STLL+T;
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
					}
					else
					{
						//No estoy en quiebre
						TA=tp6.CalculoTA();
						TPS[x]=T+TA;
						STA=STA+TA;
						STS=STS+T;
					}
				}
				else 
				{
					//No hay otro vehiculo en esta cabina
					ITO[x]=T;
					TPS[x]=HV;
				}
				NT=NT-1;								
			}					
		}
		while(T<TF);
		System.out.println("Cantidad de Cabinas: "+NC);
		System.out.println("Vehiculos en el Peaje para realizar Quiebre: "+NQ);
	
		for(i=0;i<NC;i++)
		{
			PTO[i]=(STO[i]/T)*100;
			System.out.println("Porcentaje tiempo ocioso en cabina "+i+": "+ PTO);

		}
		
		
		COP=COC*NC;
		PTE=(STS-STLL-STA)/(CLL-VQ);
		
		System.out.println("Cantidad de Cabinas: "+NC);
		System.out.println("Vehiculos en el Peaje para realizar Quiebre: "+NQ);
		System.out.println("Promedio tiempo de espera: "+PTE);
		System.out.println("Perdida Mensual por quiebre: "+PMQ);
		System.out.println("Costo operativo por cabina: "+COP);
		
		
	}
	
	public void CI()
	{
		int i;
		T=0;
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
	public int CalculoIA()
	{
		return ((int)(Math.random()*50)+1);
	}
	public int CalculoTA()
	{
		return ((int)(Math.random()*55)+5);
	}	
	public int CalculoDQ()
	{
		return (((int)Math.random()*180)+60);
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


