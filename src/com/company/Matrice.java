package com.company;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author vicky
 */
public class Matrice {

    private  int[][] matrix;
    private  final int number;
    private final int[] R;
    private  int[] D;
    private ComputationalThreads[] Compute;               // this is h(q) arithemetic function
    private Map<Integer, Integer > function;
    private static  Map<Integer, Integer>  SetR = new HashMap<Integer, Integer>() {};
    private static int ReferenceCount=0;


    // Calculate the cardinality of Set R and use it to poplulate the set



    public static int gcd(int a, int b) {
        while (b != 0) {
            int t = a;
            a = b;
            b = t % b;
        }
        return a;
    }

    public static int factors(int k) {
        int factors=0;
        for (int i =3; i<=k; i++){
            if (k%i==0) factors++;
        } return factors;
    }

    public static int ModifiedEulerFunction(int n){                     // returns euler(n)/2
        int count=0;
        for (int i=1; i<=n;i++)
            if (gcd(n, i) == 1) {
                count += 1;
            }
        return count/2;
    }

    public Matrice(int n) {
        this.number=n;
        this.R= new int[ModifiedEulerFunction(n)];
        this.D= new int[factors(n)];
        this.constructR(); this.constructD();
        this.matrix= new int[ModifiedEulerFunction(this.number)][ModifiedEulerFunction(this.number)];


        for (int count =1; count<=this.R.length; count++) {     // relation between the numbers on the matrice and the array R
            SetR.put(count,R[count-1]);
            count++;
        }

        this.Compute= new ComputationalThreads[ModifiedEulerFunction(this.number)];
    }

    public void writeFunction(){                                    // The function is defined over the set D
        this.function= new HashMap<Integer, Integer>() {};
        Scanner scantron = new Scanner(System.in);
        int n = this.number;
        for (int i=3; i<=n; i++) {
            if (n%i==0) {
                System.out.print(i + ": ");
                int number = scantron.nextInt();
                this.function.put(i, number);
            }
        }
    }

    public void constructD(){                                        // the implementation of {d|n, d>=3}
        int count=0;
        for (int i=3; i<=this.number; i++){
            if (this.number%i==0) {
                D[count]=i;
                count++;
            }
        }
    }

    public void constructR() {
        int count =0;
        for (int i=1; i<= (int) this.number/2 ; i++){
            if (gcd(this.number,i)==1) {
                R[count]=i;
                count++;
            } else {
                continue;
            }
        }
    }

    private void compute() {


        for (int count =1; count<= this.R.length; count++) {
            this.Compute[count] = new ComputationalThreads(count);
            this.Compute[count].start();
            count++;
        }
    }

    private void populateMatrice(int[] recievedCalculation, int row) {
        System.out.println("Ducky is here");
        synchronized (this.matrix) {
            for (int i = 0; i < ModifiedEulerFunction(this.number); i++) {
                this.matrix[i][row] = recievedCalculation[i];
            }
        }
    }

    private void printMatrice() {
        synchronized (System.out) {
            for (int i = 0; i < ModifiedEulerFunction(this.number); i++) {
                for (int j = 0; j < ModifiedEulerFunction(this.number); j++) {
                    if (j < ModifiedEulerFunction(this.number) - 1) System.out.print(this.matrix[i][j]);
                    else System.out.println(this.matrix[i][j]);
                }
            }
        }
    }


    public static  void main(String[] args){
        Scanner scan = new Scanner(System.in);

        System.out.println("Select your Number");

        int number = scan.nextInt();
        Matrice matrice = new Matrice(number);
        System.out.println("Write the corresponding values for your function");
        matrice.writeFunction();
        matrice.compute();

    }

    public  class ComputationalThreads extends Thread {

        private int recievedValue;
        private int assignedValue;
        private int[] assignedRow;




        private int subtractionMethod(int j){                           // the j-k h(q)
            int sum=0;
            for (int q : Matrice.this.D) {
                if ((j- assignedValue)%q==0 ) {
                        sum+=Matrice.this.function.get(q);
                }
            } return sum;
        }

        private int additionMethod(int j) {                  // the j+k h(q)
            int sum=0;
            for (int q : Matrice.this.D) {
                if ((j+ assignedValue)%(q)==0) {
                    sum+=Matrice.this.function.get(q);
                }
            } return sum;

        }

        public ComputationalThreads(int givenValue) {
            this.recievedValue= givenValue;
            this.assignedValue= Matrice.SetR.get(givenValue);
            this.assignedRow = new int[Matrice.this.R.length];
        }

        @Override
        public void run() {
            for (int i=1; i<=Matrice.this.R.length; i++) {
                assignedRow[i-1]=  subtractionMethod(Matrice.SetR.get(i))-additionMethod(Matrice.SetR.get(i)) ;
                synchronized (Matrice.this.matrix ) {
                    Matrice.this.populateMatrice(assignedRow, recievedValue-1);
                }
            }
        }
    }
}
