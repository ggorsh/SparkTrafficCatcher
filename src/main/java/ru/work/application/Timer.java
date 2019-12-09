package ru.work.application;

import java.util.ArrayList;
import java.util.List;
/**
 * Таймер для отслеживания времени проверки траффика и обновления парамметров.
 *
 * */
public class Timer {
    private long timeInterval;
    private List listeners = new ArrayList();

    public Timer(long timeInterval) {
        this.timeInterval = timeInterval;
    }

    public void addListener(IListener listener) {
        this.listeners.add(listener);
    }

    public void start() {
        Thread timerThread = new Thread(new Runnable() {
            @Override
            public void run() {
                byte round = 0;
                while (true) {
                    try {
                        Thread.sleep(timeInterval);
                        fireAction(0);
                        round++;
                        if (round == 4) {
                            round = 0;
                            fireAction(1);
                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        timerThread.start();
    }

    protected void fireAction(int x) {
        for (int i = 0; i < listeners.size(); i++) {
            IListener listener = (IListener) listeners.get(i);
            listener.performAction(x);
        }
    }
}
