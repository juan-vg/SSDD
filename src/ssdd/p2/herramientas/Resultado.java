package ssdd.p2.herramientas;

import java.util.ArrayList;

public class Resultado {

    /** Listas de numeros primos */
    // resultados separados
    private ArrayList<Integer> primos1;
    private ArrayList<Integer> primos2;

    // resultados juntos
    private ArrayList<Integer> primos;

    /** tiempo de ejecucion */
    private double tiempo;

    /**
     * Constructor
     */
    public Resultado() {
        primos1 = null;
        primos2 = null;
        primos = null;
        tiempo = 0;
    }

    /**
     * @return the tiempo
     */
    public double getTiempo() {
        return tiempo;
    }

    /**
     * @param tiempo the tiempo to set
     */
    public void setTiempo(double tiempo) {
        this.tiempo = tiempo;
    }

    /**
     * @param primos1 the primos1 to set
     */
    public void setResultado1(ArrayList<Integer> primos1) {
        this.primos1 = primos1;
    }

    /**
     * @param primos2 the primos2 to set
     */
    public void setResultado2(ArrayList<Integer> primos2) {
        this.primos2 = primos2;
    }

    /**
     * Devuelve una lista de numeros primos
     * 
     * @return
     */
    public ArrayList<Integer> getResultado() {

        if (primos == null) {
            
            if(primos1 != null && primos2 != null){
                
                int num = primos1.size() + primos2.size();
                primos = new ArrayList<Integer>(num);

                for (Integer primo : primos1) {
                    primos.add(primo);
                }

                for (Integer primo : primos2) {
                    primos.add(primo);
                }
                
                return primos;
                
            } else if (primos1 != null){
                
                return primos1;
                
            } else if (primos2 != null){
                
                return primos2; 
            }
        }

        return primos;
    }

}
