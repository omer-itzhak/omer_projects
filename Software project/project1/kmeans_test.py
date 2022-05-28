from tempfile import NamedTemporaryFile
from typing import List, Optional
import unittest
from dataclasses import dataclass
import subprocess

from kmeans import sub_vector, euclidean_norm
from kmeans import read_file as load_vecs_from_file

def dist(a, b):
    new_vec = [a[i] - b[i] for i in range(len(a))]
    res = 0
    for i in range(len(new_vec)):
        res += new_vec[i]*new_vec[i]
    return res  
    

@dataclass
class KMeansTestCase:
    input_file: str
    k: int
    max_iter: Optional[int]
    expected_output: str

class KMeansTests(unittest.TestCase):

    test_cases = [
        KMeansTestCase(
            "resources/input_1.txt",
            3,
            600,
            expected_output="resources/output_1.txt"
        ),
        KMeansTestCase(
            "resources/input_2.txt",
            7,
            None,
            expected_output="resources/output_2.txt"
        ),
        KMeansTestCase(
            "resources/input_3.txt",
            15,
            300,
            expected_output="resources/output_3.txt"
        ),
    ]

    def kmeans_test(self, exec: str):
        for test_case in self.test_cases:
            with NamedTemporaryFile("w+") as f:
                res = subprocess.run([
                    *exec.split(' '),
                    *f"{test_case.k}{(' ' + str(test_case.max_iter)) if test_case.max_iter else ''} {test_case.input_file} {f.name}".split(' ')
                ], stdout=subprocess.PIPE, stderr=subprocess.PIPE)
                self.assertFalse(
                    res.stderr
                )
                exp_vecs = load_vecs_from_file(test_case.expected_output)
                actual_vecs = load_vecs_from_file(f.name)
                self.assertEqual(
                    len(exp_vecs),
                    len(actual_vecs),
                    "Output file and expected output file have a different number of vectors."
                )
                for i in range(len(exp_vecs)):
                    self.assertAlmostEqual(
                        dist(actual_vecs[i], exp_vecs[i]) ** 0.5,
                        0,
                        delta=0.001,
                        msg=f"Expected:\n{exp_vecs}\n\nActual:\n{actual_vecs}"
                    )
    
    def test_kmeans_python(self):
        self.kmeans_test("python3 kmeans.py")
    
    def test_kmeans_errors(self):
        self.compile_c_prog()
        with NamedTemporaryFile("w+") as f:
            INVALID_INPUT_ERR = b"Invalid Input!\n"
            test_cases = {
                f"test resources/input_1.txt {f.name}": INVALID_INPUT_ERR,
                f"3 file.txt {f.name} 344": INVALID_INPUT_ERR,
                f"5000 resources/input_1.txt {f.name}": INVALID_INPUT_ERR,
                f"5 resources/input_1.txt {f.name} 100 a": INVALID_INPUT_ERR,
                f"5.0 233 resources/input_1.txt {f.name}": INVALID_INPUT_ERR,
            }
            for args, exp_msg in test_cases.items():
                res = subprocess.run([
                    "python3",
                    "kmeans.py",
                    *args.split(' ')
                ], stdout=subprocess.PIPE, stderr=subprocess.PIPE)
                self.assertEqual(
                    res.stdout,
                    exp_msg
                )
                res = subprocess.run([
                    "./kmeans",
                    *args.split(' ')
                ], stdout=subprocess.PIPE, stderr=subprocess.PIPE)
                self.assertEqual(
                    res.stdout,
                    exp_msg
                )

    def test_kmeans_c(self):
        self.compile_c_prog()
        self.kmeans_test("./kmeans")

    def compile_c_prog(self):
        res = subprocess.run([
            "gcc",
            "kmeans.c",
            "-o",
            "kmeans",
            "-ansi",
            "-Wall",
            "-Wextra",
            "-Werror",
            "-pedantic-errors",
            "-lm"
        ], stdout=subprocess.PIPE, stderr=subprocess.PIPE)
        self.assertFalse(
            res.stderr
        )