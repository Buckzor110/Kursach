package com.company;

public class MathFunc {
    float a;
    float b;
    int type;

    public MathFunc(float a, float b, int type)
    {
        this.a = a;
        this.b = b;
        this.type = type;
    }

    float calc(float x)
    {
        if (type == 0)
            return a * x + b;
        else if (type == 1)
            return 1 / (a * x + b);
        else if (type == 2)
            return a / x + b;
        else if (type == 3)
            return (float)Math.pow(x, a) * b;
        else if (type == 4)
            return (float)Math.exp(a * x) * b;
        else if (type == 5)
            return a * (float)Math.log(x) + b;
        return 0;
    }

    @Override
    public String toString()
    {
        if (type == 0)
            return a + " * x + " + b;
        else if (type == 1)
            return "1 / (" + a + " * x + " + b + ")";
        else if (type == 2)
            return a + " / x + " + b;
        else if (type == 3)
            return "x ^ " + a + " * " + b;
        else if (type == 4)
            return "e ^ (x * " + a + ") * " + b;
        else if (type == 5)
            return a + " * log(x) + " + b;
        return "";
    }
}
