package driver;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

/**
 * @author Nick Schulz
 * @author Pawel Suder
 */
public final class SCIP {

    private static final String OOP = "00P";

    private static final String LASER_ON = "BM";

    private static final String LASER_OFF = "QT";

    private static final String RESET = "RS";

    private static final String MOTOR_SPEED = "CR";

    private static final String HIGH_SENSITIVE = "HS";

    private static final String VERSION_INFO = "VV";

    private static final String SENSOR_SPECS = "PP";

    private static final String SENSOR_STATE = "II";

    private static final String SINGLE_SCAN = "GD0044072500";

    private final Scanner sc;

    private final PrintStream ps;

    public SCIP(InputStream in, OutputStream out) {
        sc = new Scanner(in);
        ps = new PrintStream(out);
    }

    private String get(String code, int count) {
        ps.println(code);

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < count; i++) {
            String str = sc.nextLine();
            result.append(str).append('\n');
        }

        return result.toString();
    }

    /**
     * Command "BM", page 14
     */
    public String laserOn() {
        return get(LASER_ON, 3);
    }

    /**
     * Command "QT", page 14
     */
    public String laserOff() {
        return get(LASER_OFF, 3);
    }

    /**
     * Command "RS", page 14
     */
    public String reset() {
        return get(RESET, 3);
    }

    public static enum MotorSpeed {
        DEFAULT {
            @Override
            public String getValue() {
                return "00";
            }
        }, LEVEL_1 {
            @Override
            public String getValue() {
                return "01";
            }
        }, LEVEL_2 {
            @Override
            public String getValue() {
                return "02";
            }
        }, LEVEL_3 {
            @Override
            public String getValue() {
                return "03";
            }
        }, LEVEL_4 {
            @Override
            public String getValue() {
                return "04";
            }
        }, LEVEL_5 {
            @Override
            public String getValue() {
                return "05";
            }
        }, LEVEL_6 {
            @Override
            public String getValue() {
                return "06";
            }
        }, LEVEL_7 {
            @Override
            public String getValue() {
                return "07";
            }
        }, LEVEL_8 {
            @Override
            public String getValue() {
                return "08";
            }
        }, LEVEL_9 {
            @Override
            public String getValue() {
                return "09";
            }
        }, LEVEL_10 {
            @Override
            public String getValue() {
                return "10";
            }
        }, RESET {
            @Override
            public String getValue() {
                return "99";
            }
        };

        public abstract String getValue();
    }

    /**
     * Command "CR", page 17
     */
    public String setMotorSpeed(MotorSpeed motorSpeed) {
        return get(MOTOR_SPEED + motorSpeed.getValue(), 3);
    }

    /**
     * Command "HS", page 18
     */
    public String setHighSensitive(boolean enable) {
        return get(HIGH_SENSITIVE + (enable ? "0" : "1"), 3);
    }

    /**
     * Command "VV", page 20
     */
    public String getVersionInfo() {
        return get(VERSION_INFO, 8);
    }

    /**
     * Command "II", page 22
     */
    public String getSensorState() {
        return get(SENSOR_STATE, 10);
    }

    /**
     * Command "PP", page 21
     */
    public String getSensorSpecs() {
        return get(SENSOR_SPECS, 11);
    }

    private List<MapPoint> fetchData() {
        List<MapPoint> points = new LinkedList<MapPoint>();

        // parse timestamp
        int timeStamp = decode(sc.nextLine().substring(0, 4));

        // fetch data
        int numDataBlocks = 32;
        StringBuilder measurements = new StringBuilder();
        for (int i = 0; i < numDataBlocks; i++) {
            String receivedLine = sc.nextLine();
            measurements.append(receivedLine.substring(0, receivedLine.length() - 1));
        }

        // 725 (end of measurement) - 44 (end of measurement) = 681 values
        for (int i = 0; i < measurements.length() / 3; i += 1) {
            double distanceValue = (double) decode(measurements.substring(3 * i, 3 * (i + 1)));
            points.add(new MapPoint(distanceValue, (0.35208516886930985 * i) - 119.885, timeStamp));
        }

        // return list of points
        return points;
    }

    /**
     * Command "GD", page 13
     */
    public List<MapPoint> singleScan() {
        List<MapPoint> points = new LinkedList<MapPoint>();

        // send command
        ps.println(SINGLE_SCAN);

        // drop first line
        sc.nextLine();

        String receivedLine = sc.nextLine();
        // check if line is valid
        if (receivedLine.contains(OOP)) {
            // fetch data
            points.addAll(fetchData());
        } else {
            // no data?
            System.err.println("driver.SCIP: " + receivedLine);
        }
        // drop last line
        sc.nextLine();

        // return list of points
        return points;
    }

    private static int decode(String enc) {
        char[] encArray = enc.toCharArray();
        String binaryDec = "";

        for (int i = 0; i < encArray.length; i++) {
            encArray[i] -= 0x30;
            String str = Integer.toBinaryString(encArray[i]);

            while (str.length() < 6) {
                str = "0" + str;
            }
            binaryDec += str;
        }
        return Integer.parseInt(binaryDec, 2);
    }
}
