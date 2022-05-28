## ex1_omer-itzhak

import random
import math

import Bio.SubsMat.MatrixInfo
from Bio import pairwise2, AlignIO
from Bio import Align
from Bio.Align import substitution_matrices
import re
from Bio import SeqIO
from Bio.Align.Applications import MafftCommandline
from io import StringIO
from Bio import AlignIO
MAFFT_EXE_PATH = r"C:\MAFFT\mafft-7.490-win64-signed\mafft-win\mafft.bat"

#read fasta- general function
def read_fasta(filename):
    names = []
    seqs =[]
    record_iterator = SeqIO.parse(filename, "fasta")
    record = next(record_iterator)
    while True:
        names.append(record.id)
        seqs.append(str(record.seq))
        try:
            record = next(record_iterator)
        except StopIteration:
            break
    return (names,seqs)




##Question B
class matrix:
    def __init__(self, n_rows, n_cols):
        assert n_rows > 0 and n_cols > 0
        self.row = n_rows+1
        self.col = n_cols+1
        self.mat = [[0 for i in range(self.col)] for j in range(self.row)]

    def __repr__(self):
        print(self.mat)

    def get(self, i, j):
        return self.mat[i+1][j+1]

    def set(self, x, i, j):
        self.mat[i+1][j+1] = x

    def row_delete(self,i):
        del self.mat[i]
        self.row -= 1

    def col_delet(self,j):
        for i in range(self.row):
            del self.mat[i][j]
        self.col -= 1
##Question B




##Question C

# this class is used in order to calculate the weighted average more easily
class tuppels:
    def __init__(self, tup, size=1):
        self.tup = tup
        self.size = size

    def __repr__(self):
        return str(self.tup)

    def __getitem__(self, i):
        if type(self.tup) == str:
            return self
        else:
            return self.tup[i]

##Performs a union of two clusters
    def merge_tup(self, tup2):
        temp = tuppels(self.tup, self.size)
        self.tup = (temp, tup2)
        self.size += tup2.size # increasing the size by the second tup size

def find_min_cell(D): # helper_func C
    min_cell = math.inf
    for i in range(1,D.row-1):
        for j in range(0,i):
            cur_val = D.get(i,j)
            if cur_val < min_cell:
                min_cell = cur_val
                pair_to_unite = [j,i]
    return pair_to_unite

def mat_union(D,tuppels_lst,pair_to_unite): # helper_func C
    current_tupple = tuppels_lst[pair_to_unite[0]]
    for j in range(0, pair_to_unite[0]): #This part fix the matrix row of the first index in pair to unite cuple
        distance = (D.get(pair_to_unite[0], j) * current_tupple[0].size + D.get(pair_to_unite[1], j) *current_tupple[1].size) / current_tupple.size
        D.set(distance, pair_to_unite[0], j)
        D.set(distance,j,pair_to_unite[0]) # updating the mat symmetrically
    for i in range(pair_to_unite[0] + 1, D.row - 1): #This part fix the matrix col of the first index in pair to unite cuple
        distance = (D.get(i, pair_to_unite[0]) * current_tupple[0].size + D.get(i, pair_to_unite[1]) *current_tupple[1].size) / current_tupple.size
        D.set(distance, i, pair_to_unite[0])
        D.set(distance,pair_to_unite[0],i) # updating the mat symmetrically
    # delete used row and col
    D.row_delete(pair_to_unite[1] + 1)
    D.col_delet(pair_to_unite[1] + 1)


def tree_union(tuppels_lst,pair_to_unite): # helper_func C
    a_ind = pair_to_unite[0]
    b_ind = pair_to_unite[1]
    a_tup = tuppels_lst[a_ind]
    b_tup = tuppels_lst[b_ind]
    a_tup.merge_tup(b_tup) # creating new tup - size is updating accordingly
    del tuppels_lst[b_ind] # deleting used tup

##Question C - main function
def upgma(D, seq_names_lst):
    tuppels_lst = [tuppels(seq_names_lst[i]) for i in range(len(seq_names_lst))]
    while len(tuppels_lst) > 1:
        pair_to_unite = find_min_cell(D)
        tree_union(tuppels_lst, pair_to_unite)  # form the tree
        mat_union(D,tuppels_lst,pair_to_unite) # fix the mat
    #the tree is complite
    return tuppels_lst[0]
##Question C

##Question D
def score_the_matrix(mat,seq_lst):  # helper_func D
    max_S = 0
    blosum_62 = Bio.SubsMat.MatrixInfo.blosum62
    for i in range(len(seq_lst)):
        for j in range(len(seq_lst)):
            if i == j: # the diagonal of the mat is 0
                continue
            score = pairwise2.align.globalds(seq_lst[i],seq_lst[j],blosum_62,-5,-5,score_only=True)
            max_S = max(max_S,score)
            mat.set(score,i,j)
    return max_S

def make_score_mat_dist_mat(mat,seq_lst,max_S): # helper_func D
    dist_mat = matrix(len(seq_lst),len(seq_lst))
    for i in range(len(seq_lst)):
        for j in range(len(seq_lst)):
            if i == j: # the diagonal of the mat is 0
                continue
            temp = mat.get(i,j)
            dist_mat.set(max_S - temp + 1,i,j)
    return dist_mat


##Question D - main function
def globalpw_dist(seq_lst):
    mat = matrix(len(seq_lst),len(seq_lst))
    max_S = score_the_matrix(mat,seq_lst)
    dist_mat = make_score_mat_dist_mat(mat,seq_lst,max_S)
    return dist_mat
##Question D




##Question E##
def generate_kmers(seq_lst, k): # helper_func E
    res = []
    for seq in seq_lst:
        d = {}
        for i in range(len(seq) - k + 1):
            kmer = seq[i:i+k]
            if kmer not in d:
                d[kmer] = 1
            else:
                d[kmer] += 1
        res.append(d)
    return res

def build_the_matrix(distance_mat,all_possible_kmer): # helper_func E
    n = len(distance_mat.mat) - 1
    for i in range(n):
        for j in range(i + 1,n):
            curr = distance(all_possible_kmer[i],all_possible_kmer[j])
            distance_mat.set(curr,i,j)
            distance_mat.set(curr,j,i)

def distance(kmers_1,kmers_2): # helper_func E
    res = 0
    kmers_1_set = set(kmers_1.keys())
    kmers_2_set = set(kmers_2.keys())
    kmers_1_set.update(kmers_2_set)
    for kmer in kmers_1_set:
        if kmer not in kmers_1:
            curr1 = 0
        else:
            curr1 = kmers_1[kmer]
        if kmer not in kmers_2:
            curr2 = 0
        else:
            curr2 = kmers_2[kmer]
        temp = math.pow(curr1 - curr2,2)
        res += temp
    return math.sqrt(res)

##Question E - main function##
def kmer_dist(seq_lst, k=3):
    distance_mat = matrix(len(seq_lst),len(seq_lst))
    all_possible_kmer = generate_kmers(seq_lst, k)
    build_the_matrix(distance_mat,all_possible_kmer)
    print("the mat using kmer is:",distance_mat.mat)
    return distance_mat
##Question E##


##Question G - main function
def align_sequences(filename, aln_filename):
    mafft_cline = MafftCommandline(MAFFT_EXE_PATH, input=filename)
    stdout, stderr = mafft_cline()
    align = AlignIO.read(StringIO(stdout), "fasta")
    AlignIO.write(align, aln_filename, "fasta")
    return stdout



##Question H##

def read_msa(msa_aln_path): # helper_func E
    fasta_parse = SeqIO.parse(msa_aln_path,'fasta')
    names_lst = []
    seq_lst = []
    for seq_type in fasta_parse:
        names_lst.append((str(seq_type.id)))
        seq_lst.append((str)(seq_type.seq))
    return (names_lst,seq_lst)

def generate_random_seqs(seq_lst_msa): # helper_func E
    index_arr = []
    res = []
    temp_seqs = ["" for i in range(len(seq_lst_msa))]
    for i in range(len(seq_lst_msa[0])):
        x = random.randint(0,1)
        if x == 0: #if x = 0 we are taking the i' index for the new randomaized seqs
            for j in range(len(seq_lst_msa)):
                temp = temp_seqs[j]
                temp += seq_lst_msa[j][i]
                temp_seqs[j] = temp
    for i in range(len(temp_seqs)):
        temp = temp_seqs[i].replace("-","") #remove all gaps from the new seqs
        temp_seqs[i] = temp
    return temp_seqs

def generate_tup_dict(T,d): # helper_func E
    if isinstance(T.tup,str): #no more tuples
        return d
    else:
        d[T.tup] = 0 #add this tupel to dict
        left = T[0]
        right = T[1]
        d1 = generate_tup_dict(left,d)
        d2 = generate_tup_dict(right,d)
        new_dict = {**d1,**d2} # combain the two dicts
        return new_dict

def tup_found(tup,tup_dict_rnd):# helper_func E
    for tup_rnd in tup_dict_rnd: #check for any match in the new dict
        if equal_splits(tup,tup_rnd):
            return True
    return False

def equal_splits(tup1,tup2):# helper_func E

    # make each part of each tupel to be a set
    tup1_set_0 = set_rec(tup1[0],set())
    tup1_set_1 = set_rec(tup1[1],set())
    tup2_set_0 = set_rec(tup2[0],set())
    tup2_set_1 = set_rec(tup2[1],set())

    # make each set to be a tuple type objects
    tup1_set_0_tupped = {tup.tup for tup in tup1_set_0}
    tup1_set_1_tupped = {tup.tup for tup in tup1_set_1}
    tup2_set_0_tupped = {tup.tup for tup in tup2_set_0}
    tup2_set_1_tupped = {tup.tup for tup in tup2_set_1}

    #creating the condisions that mark that the tupels are equal, without consideration of order.
    cond1 = (tup1_set_0_tupped.difference(tup2_set_0_tupped) == set())
    cond2 = (tup1_set_1_tupped.difference(tup2_set_1_tupped) == set())
    cond3 = (tup1_set_0_tupped.difference(tup2_set_1_tupped) == set())
    cond4 = (tup1_set_1_tupped.difference(tup2_set_0_tupped) == set())

    #check is the tuples are equal
    if((cond1 and cond2) or (cond3 and cond4)):
        return True
    else:
        return False

def set_rec(split,set): # helper_func E
    #base case - no more tupels
    if isinstance(split.tup,str):
        set.add(split)
    else:
        set_rec(split[0],set)
        set_rec(split[1],set)
    return set

##Question H - main function
def eval_dist(seq_lst, msa_aln_path, dist_func=globalpw_dist()):
    tup = read_msa(msa_aln_path)
    names_lst = tup[0]
    seq_lst_msa = tup[1]
    dist_mat = dist_func(seq_lst)
    T = upgma(dist_mat,names_lst)
    tup_dict = generate_tup_dict(T,{})
    for i in range(100):
        seq_lst_rnd = generate_random_seqs(seq_lst_msa)
        dist_mat_rnd = dist_func(seq_lst_rnd)
        T_rnd = upgma(dist_mat_rnd,names_lst)
        tup_dict_rnd = generate_tup_dict(T_rnd,{})
        for tup in tup_dict:
            if tup_found(tup,tup_dict_rnd): # without consideration of order!
                tup_dict[tup] += 1
    return tup_dict

if __name__ == '__main__':
    '''
    mat = [[0,0,0,0,0],[0,4,0,7,20],[0,9,7,0,18,17],[0,20,18,17,0]]
    D = matrix(4,4)
    D.mat = mat
    names = ["A","B","C","D"]
    tree_abcd = upgma(D,names)
    MAFFT_EXE_PATH = r"C:\MAFFT\mafft-7.490-win64-signed\mafft-win\mafft.bat" # depends on your operating system
    seqs_path = r"sequences.fasta"
    msa_aln_path = r"sequences.aln.fasta"
    align_sequences(seqs_path, msa_aln_path)
    (x,y) = read_fasta(seqs_path)
    seq_name_lst = x
    seq_lst = y
    score_mat_global =globalpw_dist(seq_lst)
    tree = upgma(score_mat_global,seq_name_lst)
    print("tree using globalpw_dist:")
    print(tree)
    (x,y) = read_fasta(seqs_path)
    seq_name_lst = x
    seq_lst = y
    score_mat_kmer = kmer_dist(seq_lst,3)
    tree_2 = upgma(score_mat_kmer,seq_name_lst)
    print("tree using kmer_dist:",tree_2)
    eval_dist(seq_lst,msa_aln_path)
    '''









