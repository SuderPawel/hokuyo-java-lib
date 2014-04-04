package tools;

import gnu.io.*;

import java.io.IOException;

public abstract class SerialPortHelper {

    public static final String HOKUYO_OWNER = "hokuyo";

    public static final int HOKUYO_PORT = 9600;

    public static SerialPort getSerialPort(String portName, String owner, int baudRate,
                                           int dataBits, int stopBits, int parity, int magicNumber)
            throws NoSuchPortException, PortInUseException, UnsupportedCommOperationException, IOException {

        CommPortIdentifier id = CommPortIdentifier.getPortIdentifier(portName);

        if (id.getPortType() != CommPortIdentifier.PORT_SERIAL) {
            throw new NoSuchPortException();

        } else {
            SerialPort sp = (SerialPort) id.open(owner, magicNumber);
            sp.setSerialPortParams(baudRate, dataBits, stopBits, parity);
            return sp;
        }
    }

    public static SerialPort getHokuyoSerialPort(String portName)
            throws PortInUseException, IOException, NoSuchPortException, UnsupportedCommOperationException {

        return getSerialPort(portName, HOKUYO_OWNER, HOKUYO_PORT,
                SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE, 1001);
    }

    private SerialPortHelper() {
    }
}
