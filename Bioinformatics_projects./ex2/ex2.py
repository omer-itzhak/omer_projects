# Bioinfotools exercise 2

import random
import re



#Auxiliary function to read a FASTA file. Returns a string
def read_fasta(filename):
    f = open(filename)
    header = f.readline()
    x = f.read()
    x = x.replace("\n", "")
    return x


# Part A
# Scoring regime
def score(a, b):
    if (a == b):
        return 3
    elif ((a == "-" and b != "-") or (a != "-" and b == "-")):
        return -2
    else:
        return -3

##QA##
def local_pwalignment(S, T, score=score):
    seq1 = ""
    seq2 = ""
    res = 0
    #init pointer helper arr
    arr_pointers = [[0 for i in range(len(T)+1)] for j in range(len(S)+1)] 
    #init main matrix
    mat = [[0 for i in range(len(T)+1)] for j in range(len(S)+1)] 
    max_val = 0
    max_val_index =(-1,-1)
    
    #****generate the score matrix***#
    for i in range(1,len(S)+1):
        for j in range(1,len(T)+1):
            mat[i][j] = max(mat[i-1][j-1] + score(S[i-1],T[j-1]), mat[i][j-1] + score("-",T[j-1]) , mat[i-1][j] + score(S[i-1],"-"), 0)
            
            #deciding where to point backwords
            max_pre_index = calc_max_index(S,T,i,j,mat) 
            
            #update pointer arr
            arr_pointers[i][j] = max_pre_index 
            if mat[i][j] > max_val:
                max_val = mat[i][j]
                max_val_index = (i,j)
    
    #***trace back***#
    i = max_val_index[0]
    j = max_val_index[1]
    while mat[i][j] != 0:
        if S[i-1] == T[j-1]:
            seq1 += S[i-1]
            seq2 += T[j-1]
            i -= 1
            j -= 1
        else:
            max_ind_prev = arr_pointers[i][j]
            if max_ind_prev == (i-1,j-1):
                seq1 += S[i - 1]
                seq2 += T[j - 1]
                i -= 1
                j -= 1
            elif max_ind_prev == (i,j-1):
                seq1 += "-"
                seq2 += T[j - 1]
                j -= 1
            else: # max_ind_prev == (i-1,j)
                seq1 += S[i - 1]
                seq2 += "-"
                i -= 1
    return (float(max_val) , seq1[::-1] ,seq2[::-1])

#****max_index_backpoiter_help_function****#
def calc_max_index(S,T,i,j,mat):
    max = 0
    max_ind = (-1,-1)
    if mat[i-1][j-1] + score(S[i-1],T[j-1]) > max:
        max_ind = (i-1,j-1)
        max = mat[i-1][j-1] + score(S[i-1],T[j-1])
    if mat[i][j-1] + score("-",T[j-1]) > max:
        max_ind = (i,j-1)
        max = mat[i][j-1] + score("-",T[j-1])
    if mat[i-1][j] + score(S[i-1],"-") > max:
        max_ind = (i-1, j)
        max = mat[i-1][j] + score(S[i-1],"-")
    return max_ind


# Part B
def find_strs(S, s, r):
    res = 0
    regex = "(" + s + ")" + "{" + str(r) + ",}"
    for curr in re.finditer(regex,S):
        res += 1
    return res

def find_strs3(S, r):
    res = 0
    for STR in all_perm:
        temp = find_strs(S,STR,r)
        if temp>0:
            res += temp
    return res

def compute_all_3_perm(): #helper function
    nuc = ["A","T","G","C"]
    res = []
    for a in nuc:
        for b in nuc:
            for c in nuc:
                if a==b and b==c:
                    continue
                else:
                    temps_str = a + b + c
                    res.append(temps_str)
    return res




def permutation_test(S, r):
    r_f_3_S = find_strs3(S,r)
    arr_3_length_STR = []
    for i in range(100):
        pi = ''.join(random.sample(S,len(S)))
        f_r_3 = find_strs3(pi,r)
        arr_3_length_STR.append(f_r_3)
    sum = 0
    for f_r_3 in arr_3_length_STR:
        if r_f_3_S < f_r_3:
            sum +=1
    if sum/100 < 0.05:
        return True
    else:
        return False


if __name__ == '__main__':
    all_perm = compute_all_3_perm()
    ## Part a
    foxp1_human_path = 'Foxp1_Homo_sapiens.fasta'
    foxp1_mus_path = 'Foxp1_Mus_musculus.fasta'
    foxp1_bos_path = 'Foxp1_Bos_taurus.fasta'
    foxp1_human = read_fasta(foxp1_human_path)
    foxp1_mus = read_fasta(foxp1_mus_path)
    foxp1_bos = read_fasta(foxp1_bos_path)
    ###
    human_vs_mus = local_pwalignment(foxp1_human,foxp1_mus,score=score)
    human_vs_bos = local_pwalignment(foxp1_human,foxp1_bos,score=score)
    bos_vs_mus = local_pwalignment(foxp1_bos,foxp1_mus,score=score)
    print("bos_vs_mos:", bos_vs_mus)
    print("human_vs_mus:", human_vs_mus)
    print("human_vs_bos:", human_vs_bos)
    ##
    print(local_pwalignment("GGTTGACTA", "TGTTACGG", score=score)[0] == 13)
    ## Part b
    print(find_strs("AAGAGAGTTAGAGTCAGC", "AG", 2) == 2)
    print(find_strs("AAGAGAGTTAGAGTCAGC", "AG", 3) == 1)
    print(find_strs3("AAAGGAGGTGTTCGGTCGTCGTC", 2) == 4)
    print(find_strs3("AAAGGAGGTGTTCGGTCGTCGTC", 3) == 1)
    genome_path = "genome.fasta"
    gene_S = read_fasta(genome_path)
    #print(find_strs3(gene_S,3))
    #print(permutation_test(gene_S,3))


