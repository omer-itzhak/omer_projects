# Bioinfotools exercise 4
# Put your code instead of the 'pass' statements

import random
import numpy as np
import scipy as sp
from numpy import linalg as LA
import matplotlib.pyplot as plt
import pandas as pd
import copy

# define different "global" variables needed in the task
X = np.loadtxt('ge.txt')
y = np.loadtxt('caco.txt')
age = np.loadtxt('age.txt')
bmi = np.loadtxt('bmi.txt')
gender = np.loadtxt('gender.txt')
gene_names = np.loadtxt('gene_names.txt',dtype= str)

def knn(X,y, u, K):
    '''
    :param X: X is the original matrix extracted from ge.txt
    :param y: y is the original vector extracted from caco.txt
    :param u: a subject we want to predict its label
    :param K: the number of neighbors to consider
    :return: the label prediction of u according to its k neighbors
    '''
    n = len(X)
    norm_vector = []
    for i, vector in enumerate(X):
        norm = LA.norm(vector - u)
        norm_vector.append(norm)
    np.sort(norm_vector)
    # argsort is sorting the norm_vector to show the original incises of the element before sorting
    index_arr = np.argsort(norm_vector)
    label_sum = 0
    for i in range(K):
        label_sum += y[index_arr[i]]
    u_hat = round(label_sum/K)
    return u_hat

def L_fold_knn(L, K, X, y):
    '''
    :param L: the number of groups to divide X into
    :param K: the number of neighbors to consider
    :param X: X is the original matrix extracted from ge.txt
    :param y: y is the original vector extracted from caco.txt
    :return: returns the accuracy of Knn algorithm
    '''
    cnt_correct = 0
    index_arr = np.arange(len(X))
    np.random.shuffle(index_arr)
    # dividing into L groups by index
    L_groups = np.array_split(index_arr,L)
    for group in L_groups:
        # keeping the indices that are not a part of the current group to set as training
        index_to_keep = [i for i in range(len(X)) if i not in group]
        # removing the group from X and y
        temp_X = np.array(X[index_to_keep])
        temp_y = np.array(y[index_to_keep])
        for index in group:
            lable = y[index]
            u = X[index]
            pred = knn(temp_X,temp_y,u,K)
            if lable == pred:
                cnt_correct += 1
    return cnt_correct/len(X)


def lin_reg(X, y):
    '''
    :param X: X is the original matrix extracted from ge.txt
    :param y: y is the original vector extracted from caco.txt
    :return: the function returns a tuple of R_square and the coefficients
    '''
    ones = np.ones(len(y))
    X_one = np.c_[ones, X]
    coefficients = LA.lstsq(X_one, y,rcond=None)[0]
    avg_y = np.average(y)
    dot_product = X_one.dot(coefficients)
    SSR = sum((dot_product - avg_y)**2)
    SSE = sum((y - dot_product)**2)
    R_square = SSR/(SSR + SSE)
    return R_square, coefficients

def my_procedue(X):
    '''
    :param X: X is the original matrix extracted from ge.txt
    :return: res - the percentage of times the max R_square value got from a gene different from 'RPS4Y1'(out of 100 iterations)
    the function also prints "not statistically significant" if res >= 0.05, and "statistically significant" otherwise
    '''
    mistakes = 0
    bmi_age_gender_mat = calc_bmi_age_gender_mat()
    temp_X = copy.deepcopy(X)
    for i in range(100):
        index_to_keep = np.random.choice(len(temp_X),len(temp_X)//2)
        rand_X = np.array(temp_X[index_to_keep])
        temp_mat = copy.deepcopy(bmi_age_gender_mat)
        rand_mat = np.array(temp_mat[index_to_keep])
        R_square_max = 0
        gene_max = ''
        for j in range(len(rand_X[0])):
            trans_rand_X = rand_X.transpose()
            y = trans_rand_X[j]
            R_square, coeff = lin_reg(rand_mat,y)
            if R_square > R_square_max:
                R_square_max = R_square
                gene_max = gene_names[j]
        if gene_max != 'RPS4Y1':
            mistakes += 1
    res = mistakes/100
    print(res)
    if res >= 0.05:
        print("not statistically significant")
        return res
    else:
        print("statistically significant")
        return res

#helper function - generates the matrix for the linear regression
def calc_bmi_age_gender_mat():
    '''
    :return: the function returns the matrix containing bmi, age and gender vectors as columns.
    it's required for calculating R_square value as asked.
    '''
    age_col = np.array(age)
    bmi_col = np.array(bmi)
    gender_col = np.array(gender)
    ones = np.ones(len(age_col))
    temp = np.column_stack((age_col,bmi_col))
    mat = np.column_stack((temp,gender_col))
    mat_one = np.c_[ones,mat]
    return mat_one

if __name__ == '__main__':
    pass