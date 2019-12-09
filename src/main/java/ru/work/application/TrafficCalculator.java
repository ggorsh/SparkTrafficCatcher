package ru.work.application;

import org.pcap4j.core.*;
import org.pcap4j.packet.Packet;
import org.pcap4j.util.NifSelector;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.pcap4j.core.BpfProgram.BpfCompileMode.OPTIMIZE;
import static org.pcap4j.core.PcapNetworkInterface.PromiscuousMode.PROMISCUOUS;

public class TrafficCalculator implements IListener {

    private List listeners = new ArrayList();
    private String serverIP;
    private static long min, max;
    private long count = 0;
    private DataBase dataBase = DataBase.getInstance();

    public TrafficCalculator(String serverIP) {
        this.serverIP = serverIP;
        getLimits();

    }

    public void addListener(IListener listener) {
        this.listeners.add(listener);
    }

    protected void fireAction(int x) {
        for (int i = 0; i < listeners.size(); i++) {
            IListener listener = (IListener) listeners.get(i);
            listener.performAction(x);
        }
    }

    public void start() throws Exception {


        List<PcapNetworkInterface> devices = Pcaps.findAllDevs();
        PcapNetworkInterface nif = Pcaps.getDevByName(devices.get(0).getName());

        //  PcapNetworkInterface nif = getNetworkDevice();

        PcapHandle handle = nif.openLive(65536, PROMISCUOUS, 50);
        String bpfExpression = "dst host " + serverIP + " || src host " + serverIP;
        handle.setFilter(bpfExpression, OPTIMIZE);
        PacketListener listener = packet -> increaseTraffic(packet, handle);
        handle.loop(Integer.MAX_VALUE, listener);

        while (true) {
        }
    }


    private static PcapNetworkInterface getNetworkDevice() throws IOException {
        NifSelector nifSelector = new NifSelector();
        PcapNetworkInterface nif = nifSelector.selectNetworkInterface();
        if (nif == null) {
            System.exit(1);
        }
        return nif;
    }

    private void increaseTraffic(Packet packet, PcapHandle pcapHandle) {
        count += packet.length();
    }

    @Override
    public void performAction(int x) {


        if (x == 1) getLimits();
        if (x == 0) {
            checkTrafficLimits(count);
            count = 0;
        }
    }

    public void getLimits() {

        try {
            dataBase.connect();
            dataBase.update();
            this.min = dataBase.getMin();
            this.max = dataBase.getMax();
            dataBase.disconnect();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        System.out.println("Обновили значения min и max");
        System.out.println("Новые значения min=" + min + " max=" + max);

    }

    public void checkTrafficLimits(long value) {
       if(value<min) fireAction(0);
       if(value>max) fireAction(1);
    }
}

