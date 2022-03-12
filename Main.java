import java.util.Objects;
import java.util.Scanner;
import java.io.*;

public class Main {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);

        int i = 1;
        boolean stop = false;
        while (!stop) {

            if (i == 1) {
                System.out.print("""
                        -------------------------------------------------------------
                        Info:
                        -All measurements should be entered in inches.
                        -"side AB total length" is the length between the two
                        ball feet, including their width.
                        -ROC means "radius of curvature"
                        -FL means "focal length" 
                        """);
            } else {
                System.out.println("\n-------------------------------------------------------------");
            }
            System.out.print("""
                    \nChoose an option:
                    (1) Load default dimensions
                    (2) Enter dimensions of new spherometer
                    Enter here:\s""");

            int choice = 0;
            try {
                choice = scan.nextInt();
                if (choice == 1 || choice == 2){ // Handles the possibility that an exception is not thrown, but the integer entered is not an acceptable one
                    ;
                } else {
                    System.out.println("Invalid input, expecting 1 or 2");
                }
            } catch (Exception e) {
                System.out.println("Invalid input, expecting an integer");
            }
            scan.nextLine(); // Consumes the ">Enter" from when the user enters their input

            if (choice == 1) {
                double indicatorPrecision = 0;
                double sideABTotalLength = 0;
                double sideBCTotalLength = 0;
                double sideCATotalLength = 0;
                double ballDiameter = 0;
                double measuredValue = 0;

                try {
                    System.out.print("Enter the measured value: ");
                    measuredValue = scan.nextDouble();

                    scan.nextLine(); // Consumes the ">Enter" from when the user enters their input

                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    BufferedReader br = new BufferedReader(
                            new FileReader("C:\\Users\\Ben\\Documents\\Spherometer Calculator\\default.txt"));

                    indicatorPrecision = Double.parseDouble(br.readLine());
                    sideABTotalLength = Double.parseDouble(br.readLine());
                    sideBCTotalLength = Double.parseDouble(br.readLine());
                    sideCATotalLength = Double.parseDouble(br.readLine());
                    ballDiameter = Double.parseDouble(br.readLine());
                    br.close();
                } catch (Exception ignored) {
                    ;
                }

                printEtc(indicatorPrecision, sideABTotalLength, sideBCTotalLength, sideCATotalLength, ballDiameter, measuredValue);
            }

            if (choice == 2) {
                System.out.print("\nEnter the precision of the spherometer indicator: ");
                double indicatorPrecision = scan.nextDouble();
                System.out.print("Enter the total AB side length (including ball feet): ");
                double sideABTotalLength = scan.nextDouble();
                System.out.print("Enter the total BC side length (including ball feet): ");
                double sideBCTotalLength = scan.nextDouble();
                System.out.print("Enter the total CA side length (including ball feet): ");
                double sideCATotalLength = scan.nextDouble();
                System.out.print("Enter the diameter of the ball feet: ");
                double ballDiameter = scan.nextDouble();
                System.out.print("Would you like to set these dimensions as default? (y/n): ");
                String setDefault = scan.next();
                System.out.print("\nEnter the measured value: ");
                double measuredValue = scan.nextDouble();

                printEtc(indicatorPrecision, sideABTotalLength, sideBCTotalLength, sideCATotalLength, ballDiameter, measuredValue);

                scan.nextLine(); // Consumes the ">Enter" from when the user enters their input

                if (Objects.equals(setDefault, "y")) {
                    try {
                        BufferedWriter bw = new BufferedWriter(
                                new FileWriter("C:\\Users\\Ben\\Documents\\Spherometer Calculator\\default.txt"));
                        bw.write(String.valueOf(indicatorPrecision) + "\n");
                        bw.write(String.valueOf(sideABTotalLength) + "\n");
                        bw.write(String.valueOf(sideBCTotalLength) + "\n");
                        bw.write(String.valueOf(sideCATotalLength) + "\n");
                        bw.write(String.valueOf(ballDiameter) + "\n");
                        bw.close();
                    } catch (IOException ex) {
                        return;
                    }
                }
            }

            System.out.print("Run it again? (y/n): ");
            String runAgain = scan.nextLine();

            if (runAgain.equals("n")) {
                stop = true;
            }

            i += 1;
        }
    }

    public static void printEtc(double indicatorPrecision, double sideABTotalLength, double sideBCTotalLength, double sideCATotalLength, double ballDiameter, double measuredValue){
        int sigFigs = calculateSigFigs(indicatorPrecision);
        double numROC = roundToSigFigs(calculateROC(sideABTotalLength,sideBCTotalLength,sideCATotalLength, measuredValue, ballDiameter),sigFigs);

        System.out.println( "The ROC is: " + numROC + "\nThe FL is: " + numROC/2);
    }

    public static double calculateROC(double sideABTotalLength, double sideBCTotalLength, double sideCATotalLength, double measuredValue, double ballDiameter){
        double numA = sideABTotalLength - ballDiameter; // Calculates the point to point length of side AB of the triangle
        double numB = sideBCTotalLength - ballDiameter; // Calculates the point to point length of side BC of the triangle
        double numC = sideCATotalLength - ballDiameter; // Calculates the point to point length of side CA of the triangle

        double numK = ((numA + numB + numC) / 2);
        double numR = ((numA * numB * numC) / (4*(Math.sqrt((numK*((numK-numA)*(numK-numB)*(numK-numC))))))); // Calculates the radius of the circumcircle of the triangle
        double numROC = ((((numR*numR)-(measuredValue*measuredValue))/(2*measuredValue))-(ballDiameter/2)); // Calculates the radius of curvature of the mirror, while accounting for the error caused by ball feet
        return numROC;
    }

    private static int calculateSigFigs(double indicatorPrecision) { // Actually just finds the number of decimal places
        String text = Double.toString(Math.abs(indicatorPrecision));
        int integerPlaces = text.indexOf('.');
        int decimalPlaces = text.length() - integerPlaces - 1;
        return decimalPlaces;
    }

    private static double roundToSigFigs(double calculateROC, int sigFigs) {
        if(calculateROC == 0) {
            return 0;
        }
        final double d = Math.ceil(Math.log10(calculateROC < 0 ? -calculateROC: calculateROC));
        final int power = sigFigs - (int) d;

        final double magnitude = Math.pow(10, power);
        final long shifted = Math.round(calculateROC*magnitude);
        return shifted/magnitude;
    }
}
