# opensourcedea-lib

Open Source Data Envelopment Analysis LIB

- 2018-12-28-log: Initialized as Maven project

## How To Use It In Maven Projects

>Step 1. Add the JitPack repository to your build file

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

>Step 2. Add the dependency

```xml
<dependency>
    <groupId>com.github.dmego</groupId>
    <artifactId>opensourcedea-lib</artifactId>
    <version>Tag</version>
</dependency>
```

## Code Example

```java
package org.yourpackage;

import java.util.ArrayList;

import org.opensourcedea.dea.*;

@SuppressWarnings("unused")
public class CallTheDEASolver {

  static String[] testDMUNames = new String[7];
  static String[] testVariableNames = new String [3];
  static VariableOrientation[] testVariableOrientations = new VariableOrientation[3];
  static VariableType[] testVariableTypes = new VariableType[3];
  static double[] [] testDataMatrix = new double[7] [3];

  public static void main(String[] args) {

      createData();

     //Create a DEAProblem and specify number of DMUs (7) and number of variables (3).
     DEAProblem tester = new DEAProblem(7, 3);

     //Set the DEA Problem Model Type (CCR Input Oriented).
     tester.setModelType(ModelType.CCR_I);

     //Set the DEA Problem DMU Names where testDMUName is a double[].
     tester.setDMUNames(testDMUNames);

     //Set the DEA Problem Variable Names where testVariableName is a String[].
     tester.setVariableNames(testVariableNames);

     //Set the DEA Problem Variable Orientation where testVariableOrientation is a VariableOrientation[].
     tester.setVariableOrientations(testVariableOrientations);

     //Set the DEA Problem Variable Types where testVariableTypes is a VariableType[].
     tester.setVariableTypes(testVariableTypes);

     /* Set the DEA Problem Data Matrix where testDataMatrix is a double[] [].
      * Each row of the Matrix corresponds to the DMU in the DMUNames array.
      * Each Column of the Matrix corresponds to the Variable in the Variables Arrays.*/
     tester.setDataMatrix(testDataMatrix);

       try {
          //Solve the DEA Problem
          tester.solve();

          //Get the solution Objectives
          double[] objectives = tester.getObjectives();

          /* Get the solution Reference Set.*/
          ArrayList<NonZeroLambda>[] referenceSets = new ArrayList[7];
          referenceSets = tester.getReferenceSet();

          /* Get the solution Slacks.
           * The first array corresponds to the DMUs.
           * The second nested array corresponds to the Slack values.*/
          double[] [] slacks = tester.getSlacks();

          /* Get the solution Projections.
           * The first array corresponds to the DMUs.
           * The second nested array corresponds to the Projection values.*/
          double[] [] projections = tester.getProjections();

          /* Get the solution Weights.
           * The first array corresponds to the DMUs.
           * The second nested array corresponds to the Weight values.*/
          double[] [] weights = tester.getWeight();

          /* Get the DMU ranks.
           * The boolean confirms that the Highest DMU score is ranked first.
           * The STANDARD ranking type confirms that the ranking is standard.
           * This means that if they are two DMUs with an efficiency score of 1 both will be ranked first.
           * However, the following DMU will only be ranked 3rd as they are two DMUs which score better than it.
           * Conversely, a DENSE RankingType will have given the following (3rd) DMU the ranking of second.
           * The precision is the int value (between 0 and 16) used to round the score values before ranking
           * the objectives.*/
          int[] ranks = tester.getRanks(true, RankingType.STANDARD, 5);
       }
       catch (Exception e) {
          e.printStackTrace();
       }
  }

  private static void createData() {

     //Set up the DMU Names
     testDMUNames[0] = "DMU A";
     testDMUNames[1] = "DMU B";
     testDMUNames[2] = "DMU C";
     testDMUNames[3] = "DMU D";
     testDMUNames[4] = "DMU E";
     testDMUNames[5] = "DMU F";
     testDMUNames[6] = "DMU G";

     //Set up the Variable Names
     testVariableNames[0] = "Wood";
     testVariableNames[1] = "Twigs";
     testVariableNames[2] = "Fire";

     //Set up the Data Matrix
     testDataMatrix [0] [0] = 4;
     testDataMatrix [0] [1] = 3;
     testDataMatrix [0] [2] = 1;

     testDataMatrix [1] [0] = 7;
     testDataMatrix [1] [1] = 3;
     testDataMatrix [1] [2] = 1;

     testDataMatrix [2] [0] = 8;
     testDataMatrix [2] [1] = 1;
     testDataMatrix [2] [2] = 1;

     testDataMatrix [3] [0] = 4;
     testDataMatrix [3] [1] = 2;
     testDataMatrix [3] [2] = 1;

     testDataMatrix [4] [0] = 2;
     testDataMatrix [4] [1] = 4;
     testDataMatrix [4] [2] = 1;

     testDataMatrix [5] [0] = 10;
     testDataMatrix [5] [1] = 1;
     testDataMatrix [5] [2] = 1;

     testDataMatrix [6] [0] = 3;
     testDataMatrix [6] [1] = 7;
     testDataMatrix [6] [2] = 1;

     //Set up the variable types
     testVariableOrientations [0] = VariableOrientation.INPUT;
     testVariableOrientations [1] = VariableOrientation.INPUT;
     testVariableOrientations [2] = VariableOrientation.OUTPUT;

     testVariableTypes[0] = VariableType.STANDARD;
     testVariableTypes[1] = VariableType.STANDARD;
     testVariableTypes[2] = VariableType.STANDARD;
  }
}
```