#!/usr/bin/env python3

import numpy as np
from sklearn.linear_model import LinearRegression
import matplotlib.pyplot as plt

def fit_line(x, y):

    model=LinearRegression(fit_intercept=True)
    model.fit(x[:,np.newaxis], y)

    slope = model.coef_
    slope = float(slope[0])

    return slope, model.intercept_
    
def main():
    x=np.linspace(0, 10, 20)
    y=x*2 + 1 + 1*np.random.randn(20)
    
    slope, ic = fit_line(x,y) 

    abline_values = [slope * i + ic for i in x]
    
    print("Slope: ", slope)
    print("Intercept: ", ic)

    plt.plot(x,y)
    plt.plot(x, abline_values, 'b')




    plt.show()

if __name__ == "__main__":
    main()
