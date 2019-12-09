package ru.work.application;
/**
 * Класс реакгирующий на события привышения лимитов.
 * */
public class DataPublisher implements IListener {
    MessageProducer producer = new MessageProducer();

    @Override
    public void performAction(int x) {
        producer.init();
        switch (x) {
            case 0: {
                System.out.println("Траффик ниже порогового.");
                producer.sendMessage("Траффик ниже порогового.");
                break;
            }
            case 1: {
                System.out.println("Траффик выше порогового.");
                producer.sendMessage("Траффик выше порогового.");
                break;
            }
        }
    }
}
