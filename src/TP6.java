
public class TP6 {
    // Tiempo simulacion
    static long T;                                //Instante de tiempo actual
    static final long TF = 2592000;                //Tiempo de simulacion de un mes

    // Control:
    static int NC = 5;                    //Cantidad de Cabinas
    static int NQ = NC * 6;                    //Numero de vehiculos para hacer quiebre
    static int TipoHora=2;					//Seleccionamos la banda horaria
    
    // Datos:
    static long IA;                                //Intervalo entre arribos
    static long TA;                                //Tiempo de Atencion
    static long DQ;                                //Duracion de Quiebre

    // Estado:
    static int[] NV = new int[NC];                //Numero de vehiculos en cada cabina

    // Constantes:
    static final long COC = 360000;                //Costo Operativo de Cabina mensual
    static final long HV = 5000000;                //High Value
    static final int TAQ = 1;                        //Tiempo de atencion en quiebre

    //  TEF:
    static long TPLL;                            //Tiempo de Llegada
    static long[] TPS = new long[NC];            //Proximo tiempo de salida para cada cabina

    // Resultados:
    static long[] STO = new long[NC];            //Suma Tiempo de Ocio por cabina
    static long[] ITO = new long[NC];            //Inicio Tiempo de Ocio
    static float[] PTO = new float[NC];            //Porcentaje tiempo ocioso
    static long STA;                            //Suma tiempo de atencion
    static long STLL;                            //Suma tiempo de Llegada
    static long STS;                            //Suma tiempo de salidas
    static int CLL;                                //Vehiculos que llegaron en la simulacion
    static double PTE;                            //Promedio Tiempo de Espera
    static long PMQ;                            //Perdida en el quiebre
    static long COP;                            //Costo operativo mensual para NC cabinas

    // Auxiliares:
    static int NT;                                //Vehiculos actuales en el peaje
    static long TFQ;                            //Tiempo final de quiebre
    static int VQ;                                //Cantidad de vehiculos en quiebre


    public static void main(String[] args) {
        if (args.length == 3) {
            NC = Integer.parseInt(args[0]);
            NQ = Integer.parseInt(args[1]);
            TipoHora = Integer.parseInt(args[2]);
        }
        new TP6().simulacion();
    }

    public void simulacion() {
        CI();
        do {
            do {
                int x = MenorTPS();
                long menorTPS = TPS[x];
                if (TPLL <= menorTPS) {
                    //Rama de Llegada
                    T = TPLL;
                    CLL = CLL + 1;
                    STLL = STLL + T;
                    NT = NT + 1;
                    IA = CalculoIA();
                    TPLL = T + IA;
                    int y = MenorFila();
                    NV[y] = NV[y] + 1;

                    if (NV[y] == 1) {
                        //Es el unico en la fila
                        if (T > TFQ) {
                            //No esta en quiebre
                            TA = CalculoTA();
                            TPS[y] = T + TA;
                            STO[y] = STO[y] + (T - ITO[y]);
                            STA = STA + TA;


                        } else {
                            //Esta en quiebre
                            TPS[y] = T + TAQ;
                            STA = STA + TAQ;
                        }
                    } else {
                        //No es el unico en la fila
                        if (NT > NQ && T > TFQ) {
                            //Empezamos quiebre
                            DQ = CalculoDQ();
                            TFQ = T + DQ;
                            for (int i = 0; i < NC; i++) {
                                TPS[i] = T + TAQ;
                                STA = STA + TAQ;
                            }

                        } else {
                            //es un auto mas que se suma a la fila
                        }

                    }
                } else {
                    //Rama de salida
                    T = menorTPS;
                    NV[x] = NV[x] - 1;
                    if (NV[x] >= 1) {
                        //Hay otro en la fila
                        if (T < TFQ) {
                            //Estoy en quiebre
                            int p = PrecioPorCategoria();
                            PMQ = PMQ + p;
                            TPS[x] = T + TAQ;
                            STA = STA + TAQ;
                        } else {
                            //No estoy en quiebre
                            TA = CalculoTA();
                            TPS[x] = T + TA;
                            STA = STA + TA;

                        }
                    } else {
                        //No hay otro vehiculo en esta cabina
                        ITO[x] = T;
                        TPS[x] = HV;
                    }
                    NT = NT - 1;
                    STS = STS + T;

                }
            } while (T < TF);

            TPLL = HV;

        } while (NT > 0);

        System.out.println("====================================================");
        System.out.println("Cantidad de Cabinas: " + NC);
        System.out.println("Vehiculos en el Peaje para realizar Quiebre: " + NQ);
        if(TipoHora==1) {
        	System.out.println("Franja Horaria 2 a 7 hs");	
        }
        else if(TipoHora==2) {
        	System.out.println("Franja Horaria 7 a 21 hs");
        }
        else {
        	System.out.println("Franja Horaria 21 a 2 hs");
        }
        System.out.println("====================================================");
        int j=0;
        for (int i = 0; i < NC; i++) {
            PTO[i] = ((float) STO[i] / T) * 100;
            j=i+1;
            System.out.println("Porcentaje tiempo ocioso en cabina " + j + ": " + PTO[i] + "%");
        }


        COP = COC * NC;
        PTE = (double) (STS - STLL - STA) / (double) (CLL);

        System.out.println("Cantidad de vehiculos: " + CLL);
        System.out.println("Promedio tiempo de espera: " + PTE);
        System.out.println("Perdida Mensual por quiebre: $" + PMQ);
        System.out.println("Costo operativo por cabina: $" + COC);
        System.out.println("Costo operativo del peaje: $" + COP);


    }

    public void CI() {
        T = 1;
        TPLL = 0;
        CLL = 0;
        NT = 0;
        IA = 0;
        TA = 0;
        TFQ = -1;
        STA = 0;
        STLL = 0;
        DQ = 0;
        STS = 0;
        PMQ = 0;
        COP = 0;
        PTE = 0;
        for (int i = 0; i < NC; i++) {
            TPS[i] = HV;
            NV[i] = 0;
            STO[i] = 0;
            ITO[i] = 0;
            PTO[i] = 0;
        }

    }

    public int MenorTPS() {
        int fila = 0;
        long tiempoSalida = TPS[0];
        for (int i = 0; i < NC; i++) {
            if (TPS[i] < tiempoSalida) {
                tiempoSalida = TPS[i];
                fila = i;
            }
        }
        return fila;
    }

    public int CalculoIA() {
        double ia;     
            //entre las 2 y las 7am
        if(TipoHora==1){
            ia = CalculoIA1();
        }
        else if (TipoHora==3){
            //entre las 7am y las 21hs
            ia = CalculoIA2();
        } 
        else {
            //entre las 21hs y las 2am
            ia = CalculoIA3();
        }

        if (ia < 0) {
            ia = ia * (-1);
        }


        return (int) ia;


    }

    public double CalculoIA1() {
        double r = Math.random();
        return -18.9358 * Math.log10(1 - r);
    }

    public double CalculoIA2() {
        double r = Math.random();
        return -6.65336 * Math.log10(1 - r);
    }

    public double CalculoIA3() {
        double r = Math.random();
        return -15.3846 * Math.log10(1 - r);
    }

    public int CalculoTA() {
        double r = Math.random();
        double ta = 14 / (Math.pow((1 - r), 0.9225));
        if(ta>120)
        {
        	ta=120;
        }
        return (int) ta;
    }

    public int CalculoDQ() {
        double r = Math.random();
        double dq = 180 / (Math.pow((1 - r), 0.4764));
        return (int) dq;
    }

    public int PrecioPorCategoria() {
        int p;
        double r = Math.random();
        if (r <= 0.0017) {
            p = 390;
        } else if (r > 0.0017 && r <= 0.0495) {
            p = 255;
        } else if (r > 0.0495 && r <= 0.1453) {
            p = 155;
        } else if (r > 0.1453 && r <= 0.41) {
            p = 310;
        } else {
            p = 85;
        }
        return p;
    }

    public int MenorFila() {
        int fila = 0;
        int CantidadFila = NV[0];
        for (int i = 0; i < NC; i++) {
            if (NV[i] < CantidadFila) {
                CantidadFila = NV[i];
                fila = i;
            }
        }
        return fila;
    }

}