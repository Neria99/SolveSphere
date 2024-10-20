package com.example.solvesphere.DataBaseUnit;

import com.example.solvesphere.UserData.Problem;

import java.util.List;
import java.util.Map;

public interface ProblemDAO {
    List<Problem> getProblemsByUserInterest(Map<String, Integer> userInterests);
    Problem getProblemById(int problemId);
    List<Problem> searchProblems(String keyword);
}
