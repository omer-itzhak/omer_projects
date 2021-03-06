#define PY_SSIZE_T_CLEAN
#include <Python.h>
#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#define INT_MAX 2147483647

void avg(double **cluster, int n, double *centroid);
void sub_vector(double *old, double *new, double *sub);
double euclidean_norm(double* vector);
int is_numeric(char *str);
static double** k_mean(int k, int max_iter, double epsilon, double **data_points,double **initial_centroids,int total_vec_number,int size_vec);
int invalid_input();
void copy_vector(double *copy_from, double *copy_to);
double** create_mat(int vec_num, int vec_size);


int size_vec,total_vec_number;
double *sub;
double **centroids;


static double** k_mean(int k, int max_iter,double epsilon, double ** data_points, double **initial_centroids,int total_vec_number,int size_vec)
{
    int i,iteration=0,cluster_i = 0, idx, g;
    char more_than_epsilon;
    double min_euclidean_dist,euclidean_dist,norm,dist;
    double *change_vector;
    double **temp_centroids, **new_centroids;
    double ***clusters;
    int *clusters_sizes;
    more_than_epsilon = 1;
    new_centroids = create_mat(k , size_vec);
    if (!new_centroids)
    {
        return NULL;
    }
/*     new_centroids = (double **)calloc(k,sizeof(double*));
    assert(new_centroids && "An Error Has Occurred");
    for (i = 0 ; i < k ; i++)
    {
        new_centroids[i] = (double *)calloc(size_vec, sizeof(double));
        assert(new_centroids[i] && "An Error Has Occurred");
    } */
    sub = (double *)calloc(size_vec, sizeof(double));
    if (!sub)
    {
        return NULL;
    }
    clusters = (double ***)calloc(k,sizeof(double**));
    if (!clusters)
    {
        return NULL;
    }
    clusters_sizes = (int *)malloc(k*sizeof(int));
    if(!clusters_sizes)
    {
        return NULL;
    }
    for (i = 0 ; i < k ; i++)
    {
        clusters[i] = (double **)malloc((total_vec_number - k + 1)*sizeof(double *)); 
        /*largest cluster size can be num of data points - (k-1)*/
        if (!clusters[i])
        {
            return  NULL;
        }
    }
    change_vector = (double *)malloc(k*sizeof(double));
    if(!change_vector)
    {
        return NULL;
    }


    while (more_than_epsilon && iteration < max_iter)
    {
        iteration += 1;

        /* set cluster sizes to 0*/

        for (g = 0; g < k ; g++)
        {
            clusters_sizes[g] = 0;
        }

        /* make clusters */

        for (idx = 0 ; idx < total_vec_number ; idx++)
        {
            min_euclidean_dist = (double)(INT_MAX);
            for (i = 0 ; i < k ; i++)
            {
                sub_vector(data_points[idx], centroids[i], sub);
                euclidean_dist = euclidean_norm(sub);
                if (euclidean_dist < min_euclidean_dist)
                {
                    min_euclidean_dist = euclidean_dist;
                    cluster_i = i;
                }
            }
            clusters[cluster_i][clusters_sizes[cluster_i]] = data_points[idx];
            clusters_sizes[cluster_i] += 1;
        }

        /* make centroids*/
        for (i = 0 ; i < k ; i++)
        {
            avg(clusters[i], clusters_sizes[i], new_centroids[i]);
        }

        /* make change vector*/
        /* makes a sub vector of each two centroids and
        the norm of this sub is the cordinate in change vector*/
        for (i = 0 ; i < k ; i++)
        {
            sub_vector(centroids[i], new_centroids[i], sub);
            norm = euclidean_norm(sub);
            change_vector[i] = norm;
        }
        dist = euclidean_norm(change_vector);
        if (dist < epsilon)
        {
            more_than_epsilon = 0;
        }

        temp_centroids = centroids;
        centroids = new_centroids;
        new_centroids = temp_centroids;
    }
    
    free(change_vector);
/*     for (i = 0; i < total_vec_number ; i++)
    {        
        free(data_points[i]);
    } */
    free(data_points);
    for (i = 0 ; i < k ; i++)
    {
        /* free(centroids[i]); */
/*         free(new_centroids[i]); */
        free(clusters[i]);
    }
    free(centroids);
/*     free(new_centroids); */
    free(clusters);
    free(sub);
    free(clusters_sizes);
    return new_centroids;
}
void avg(double **cluster, int n, double *centroid)
{
    int i, j;
    double sum;
    for (i = 0 ; i < size_vec ; i++)
    {
        sum = 0;
        for (j = 0; j < n; j++) 
        {
            sum += cluster[j][i];
        }
        centroid[i] = sum/n;
    }
}

void sub_vector(double *old, double *new, double *sub)
{
    int i;
    for(i = 0 ; i < size_vec ; i++)
    {
        sub[i] = old[i] - new[i];
    }
}

double euclidean_norm(double* vector)
{
    int i;
    double res = 0;
    for (i = 0 ; i < size_vec ; i++){
        res += pow(vector[i],2);
    }
    res = pow(res, 0.5);
    return res;
}
int is_numeric(char *str)
{
    int i = 0;
    while (str[i] != 0){
        if(str[i] < 48 || str[i] > 57){
            return 0;
        }
        i++;
    }
    return 1;
}
int invalid_input()
{
    printf("Invalid Input!\n");
    exit(1);
}


void copy_vector(double *copy_from, double *copy_to)
{
    int i;
    for (i = 0; i< size_vec ; i++)
    {
        copy_to[i] = copy_from[i];
    }
}

double** parse_py_table_to_C(PyObject *lst, int vec_num, int vec_size)
{
    double **data_points = create_mat(vec_num, vec_size);
    if (!data_points)
    {
        return NULL;
    }
    for (int row = 0; row < vec_num ; row++)
    {
        for (int col = 0 ; col < vec_size ; col++)
        {
             data_points[row][col] = PyFloat_AsDouble(PyList_GetItem(lst, row * vec_size + col));
        }
    }
    return data_points;
    
}

double** create_mat(int vec_num, int vec_size){
    int i;
    double *vec;
    double **mat;
    vec = calloc(vec_num*vec_size, sizeof(double));
    if (!vec){
        return NULL;
    }
    mat = calloc(vec_num, sizeof(double*));
    if (!mat){
        return NULL;
    }
    for(i=0; i<vec_num; i++){
        mat[i] = vec + i * vec_size;
    }
    return mat;
}

static PyObject* fit(PyObject *self, PyObject *args)
{
    int k, max_iter, total_vec_number, size_vec, i, j;
    double epsilon;
    double **data_points, **initial_centroids, **centroids_c;
    PyObject **data_points_py, **initial_centroids_py, **centroids_py;
    if (!PyArg_ParseTuple(args, "iidOOii", &k, &max_iter, &epsilon, &data_points_py, &initial_centroids_py, &total_vec_number, &size_vec))
    {
        return NULL;
    }
    data_points = parse_py_table_to_C(data_points_py, total_vec_number, size_vec);
    initial_centroids = parse_py_table_to_C(initial_centroids_py, k, size_vec);
    centroids_c = k_mean(k, max_iter, epsilon, data_points, initial_centroids, total_vec_number, size_vec); // TODO: change
    centroids_py = PyList_New(k);
    /* ask if necessary*/
    if (!centroids_py)
    {
        return NULL;
    }
    for (i = 0; i < k ; i++)
    {
        centroids_py[i] = PyList_New(size_vec);
        if (!centroids_py[i])
        {
            return NULL;
        }
        for (j = 0 ; j < size_vec ; j++)
        {
            PyList_SetItem(PyList_GetItem(centroids_py, i), j, PyFloat_FromDouble(centroids_c[i][j]));
        }
    }
    for (i = 0; i < total_vec_number ; i++)
    {        
        free(data_points[i]);
    }
    free(data_points);
    free(centroids);
    return centroids_py;
}


/* methods from class*/

static PyMethodDef capiMethods[] = {
    {"fit",
    (PyCFunction) fit,
    METH_VARARGS,
    PyDoc_STR("returns calculated centroids for given matrix of data points")},
    {NULL, NULL, 0, NULL}
};

static struct PyModuleDef moduledef = {
    PyModuleDef_HEAD_INIT,
    "mykmeanssp",
    NULL,
    -1,
    capiMethods
};

PyMODINIT_FUNC
PyInit_capi_mykmeanssp(void) {
    PyObject *m;
    m = PyModule_Create(&moduledef);
    if (!m){
        return NULL;
    }
    return m;
}