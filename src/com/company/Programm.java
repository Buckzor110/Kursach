package com.company;

import java.io.File;
import java.util.Scanner;

public class Programm {
    float[][] x;
    float[] y;
    float[][] matrixD;
    float yAverage;
    float[] yNormalized;
    float[] rYx;
    int m = 3;
    int n = 22;
    int[] ryxOrder = {0, 1, 2};
    float[][] coofsAB;
    int[] funcType;
    MathFunc[] resultFuncs;


    public void calculate() {
        x = new float[3][22];
        y = new float[22];
        matrixD = new float[4][4];
        yAverage = 0;
        yNormalized = new float[22];
        rYx = new float[3];
        coofsAB = new float[3][2];
        funcType = new int[3];
        resultFuncs = new MathFunc[3];

        getData();
        fillMatrixD();
        fillRyx();
        sortRyx();
        function_build();
        printResultTable();
        for (int i = 0; i < 3; i++) {
            System.out.println(resultFuncs[i]);
        }
    }

    public void getData() {
        try {
            File file = new File("src/com/company/input.txt");
            Scanner inp = new Scanner(file);
            int i = 0;
            while (inp.hasNextLine()) {
                String line = inp.nextLine();

                String[] split_line = line.split(" ");
                this.x[0][i] = Integer.parseInt(split_line[0]);
                this.x[1][i] = Integer.parseInt(split_line[1]);
                this.x[2][i] = Integer.parseInt(split_line[2]);

                this.y[i] = Float.parseFloat(split_line[3]);
                this.yAverage += this.y[i];
                i++;
            }

            inp.close();

            this.yAverage = this.yAverage / this.n;

            for (i = 0; i < 22; i++)
                this.yNormalized[i] = this.y[i] / this.yAverage;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public float ryxK(float[] x, float[] y)//22, 22
    {
        float sum_x = 0, sum_y = 0, sum_x_y = 0, sum_x_sq = 0, sum_y_sq = 0, r;

        for (int i = 0; i < 22; i++) {
            sum_x += x[i];
            sum_y += y[i];
            sum_x_y += x[i] * y[i];
            sum_x_sq += x[i] * x[i];
            sum_y_sq += y[i] * y[i];
        }

        r = (n * sum_x_y - sum_x * sum_y) / (float) Math.sqrt((n * sum_x_sq - sum_x * sum_x) * (n * sum_y_sq - sum_y * sum_y));
        return r;
    }


    // заполнение матрицы matrix_D
    public void fillMatrixD() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j <= i; j++) {
                if (i == j)
                    this.matrixD[i][j] = 1;
                else if (i == 3)
                    this.matrixD[i][j] = ryxK(y, x[j]);
                else
                    this.matrixD[i][j] = ryxK(x[i], x[j]);

                this.matrixD[j][i] = matrixD[i][j];
            }

        }
    }


    // Считает детерминант
    public float det(int a, int b) // строка. столбец
    {
        int ind = 0;
        float d;
        float[] els = new float[9];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if ((i != a) & (j != b)) {
                    els[ind] = this.matrixD[i][j];
                    ind++;
                }
            }
        }

        d = els[0] * els[4] * els[8] + els[1] * els[5] * els[6] + els[3] * els[7] * els[2];
        return d;
    }

    public void fillRyx() {
        //заполнение corellation_coof_ryx
        for (int k = 0; k < this.m; k++) {
            this.rYx[k] = (float) (Math.abs(det(m, k) / Math.sqrt(det(m, m) * det(k, k))));
        }
    }

    public void sortRyx()// сортировка пузырьком; в order_corellation_coof_ryx сохраняется порядок
    {
        float fbuf;
        int ibuf;
        for (int i = 1; i < 3; i++) {
            for (int j = i; j < 3; j++) {
                if (this.rYx[j - 1] < this.rYx[j]) {
                    fbuf = this.rYx[j - 1];
                    this.rYx[j - 1] = this.rYx[j];
                    this.rYx[j] = fbuf;

                    ibuf = this.ryxOrder[j - 1];
                    this.ryxOrder[j - 1] = this.ryxOrder[j];
                    this.ryxOrder[j] = ibuf;
                }
            }
        }
    }


    public void function_build() {
        for (int i = 0; i < 3; i++) {
            MathFunc result = functionSelection(this.x[this.ryxOrder[i]], this.yNormalized);
            this.resultFuncs[i] = result;

            for (int j = 0; j < 22; j++) {
                this.yNormalized[j] = this.yNormalized[j] / result.calculate(x[this.ryxOrder[i]][j]);
            }
        }

        //string function_as_string = "y = " + to_string(y_average);
    }


    public MathFunc functionSelection(float[] x, float[] y) //selects the best function
    {
        float[] A = new float[6];
        float[] B = new float[6];
        float[] a = new float[6];
        float[] b = new float[6];
        float[] deviationSum = new float[6]; // набор из 6 пар a b, описанных ниже некоторые надо преодразовать в соответствии с уравнениями ниже
	    /*
	    все уравнения приводятся к виду линейному виду: Y = A * X + B
	    function type 1.
		    y = a * x + b
		    A = a => a = A
		    B = b => b = B
	    function type 2.
		    y = 1/(a * x + b) => 1/y = a * x + b
		    Y = 1/y
	    function type 3.
		    y = a/x + b
		    X = 1/x
	    function type 4.
		    y = b * x^a => ln y = ln b + a * ln x
		    Y = ln y
		    B = ln b => b = e^B
		    A = a
		    x = ln x
	    function type 5.
		    y = b * e ^( a * x ) => ln y = ln b + a * x
		    Y = ln y
		    B = ln b => b = e^B
	    function type 6.
		    y = a * ln x + b
		    X = ln x
	    */

        // подготовка массивов ввода по условиям выше
        float[] yPowMinOne = new float[22];
        float[] xPowMinOne = new float[22];
        float[] lnX = new float[22];
        float[] lnY = new float[22];


        for (int i = 0; i < 22; i++) {
            yPowMinOne[i] = 1 / y[i];
            xPowMinOne[i] = 1 / x[i];
            lnX[i] = (float) Math.log(x[i]);
            lnY[i] = (float) Math.log(y[i]);
        }

        float[] res; //stores function result

        //type 1
        res = mnkLinear(x, y);
        A[0] = res[0];
        B[0] = res[1];
        //type 2
        res = mnkLinear(x, yPowMinOne);
        A[1] = res[0];
        B[1] = res[1];
        //type 3
        res = mnkLinear(xPowMinOne, y);
        A[2] = res[0];
        B[2] = res[1];
        //type 4
        res = mnkLinear(xPowMinOne, yPowMinOne);
        A[3] = res[0];
        B[3] = (float) Math.exp(res[1]);
        //type 5
        res = mnkLinear(x, lnY);
        A[4] = res[0];
        B[4] = (float) Math.exp(res[1]);
        //type 6
        res = mnkLinear(lnX, y);
        A[5] = res[0];
        B[5] = res[1];

        //обратное преобразование коофицентов
        //type 1
        a[0] = A[0];
        b[0] = B[0];
        //type 2
        a[1] = A[1];
        b[1] = B[1];
        //type 3
        a[2] = A[2];
        b[2] = B[2];
        //type 4
        a[3] = A[3];
        b[3] = (float) Math.exp(B[3]);
        //type 5
        a[4] = A[4];
        b[4] = (float) Math.exp(B[4]);
        //type 6
        a[5] = A[5];
        b[5] = B[5];

        //подсчёт сумм модулей (нужны квадраты) отклонений
        for (int i = 0; i < 22; i++) {
            deviationSum[0] += Math.abs(y[i] - (a[0] * x[i] + b[0]));
            deviationSum[1] += Math.abs(y[i] - 1 / (a[1] * x[i] + b[1]));
            deviationSum[2] += Math.abs(y[i] - (a[2] / x[i] + b[2]));
            deviationSum[3] += Math.abs(y[i] - (Math.pow(x[i], a[3]) * b[3]));
            deviationSum[4] += Math.abs(y[i] - (Math.exp(a[4] * x[i]) * b[4]));
            deviationSum[5] += Math.abs(y[i] - (a[5] * Math.log(x[i]) + b[5]));
        }

        float mn = deviationSum[0];
        int functionType = 7;

        //чем меньше отклонение тем лучше
        for (int i = 0; i < 6; i++) {
            if (deviationSum[i] <= mn) {
                functionType = i;
                mn = deviationSum[i];
            }
        }

        return new MathFunc(a[functionType], b[functionType], functionType);
    }

    public float[] mnkLinear(float[] x, float[] y) // returns a and b
    {
        float sumX = 0, sumY = 0, sumXY = 0, sumXSq = 0;
        //подсчёт коофицентов
        for (int i = 0; i < 8; i++) {
            sumX += x[i];
            sumY += y[i];
            sumXY += x[i] * y[i];
            sumXSq += x[i] * x[i];
        }

        //вычисление и фактический возврат
        float[] res = new float[2];
        res[0] = (n * sumXY - sumX * sumY) / (n * sumXSq - sumX * sumX);
        res[1] = (sumXSq * sumY - sumX * sumXY) / (n * sumXSq - sumX * sumX);

        return res;//[a, b]
    }

    public void printResultTable() {
        for (int i = 0; i < 22; i++) {
            float yRegression = (yAverage * resultFuncs[0].calculate(x[ryxOrder[0]][i]) *
                    resultFuncs[1].calculate(x[ryxOrder[1]][i]) *
                    resultFuncs[2].calculate(x[ryxOrder[2]][i]));

            System.out.println(x[0][i] + " " + x[1][i] + " " + x[2][i] + " " + y[i] + " " + yRegression);
        }
    }
}