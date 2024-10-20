package com.example.solvesphere.DataBaseUnit;

import com.example.solvesphere.UserData.Problem;

import java.security.cert.PolicyNode;
import java.util.List;
import java.util.Map;

public interface ProblemDAO {
    List<Problem> getProblemsByUserInterest(Map<String, Integer> userInterests);
    List<Problem> searchProblems(String keyword);

    List<Problem> fetchAllProblems();

    List<Problem> getProblemsByCountry(String country);
}
