package ru.work.application;


import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Application {
    public static String serverIP;

    public static void main(String[] args) throws Exception {
        org.apache.log4j.BasicConfigurator.configure();

        SparkConf sparkConf = new SparkConf()
                .setAppName("TrafficCatcher")
                .setMaster("local");

        try (JavaSparkContext javaSparkContext = new JavaSparkContext(sparkConf)) {


            /**
             * Если через коммандную строку не передается IP то
             * получаем собственный Ip для отслеживания всего входящего и исходящего трафика
             */
            if (args.length != 0) {
                serverIP = args[0];
            } else {
                try (final DatagramSocket socket = new DatagramSocket()) {
                    socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
                    serverIP = socket.getLocalAddress().getHostAddress();
                }
            }

            TrafficCalculator calculator = new TrafficCalculator(serverIP);
            DataPublisher producer = new DataPublisher();
            calculator.addListener(producer);
            Timer timer = new Timer(300000);
            timer.addListener(calculator);
            timer.start();
            calculator.start();
        }
    }
}
