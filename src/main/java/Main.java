import driver.SCIP;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;
import tools.SerialPortHelper;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws PortInUseException, UnsupportedCommOperationException, NoSuchPortException, IOException {
        SerialPort hokuyoSerialPort = SerialPortHelper.getHokuyoSerialPort("/dev/ttyS8");
        SCIP scip = new SCIP(hokuyoSerialPort.getInputStream(), hokuyoSerialPort.getOutputStream());
        scip.laserOn();
        System.out.println(scip.singleScan());
    }
}
